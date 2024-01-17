package com.tima.platform.service;

import com.tima.platform.converter.PaymentHistoryConverter;
import com.tima.platform.domain.CustomerBankDetail;
import com.tima.platform.domain.InfluencerPaymentContract;
import com.tima.platform.domain.InfluencerTransaction;
import com.tima.platform.domain.PaymentHistory;
import com.tima.platform.exception.AppException;
import com.tima.platform.model.api.AppResponse;
import com.tima.platform.model.api.request.InitiateContractPayment;
import com.tima.platform.model.api.request.ManualTransferRecord;
import com.tima.platform.model.api.request.PaymentRequest;
import com.tima.platform.model.api.request.paystack.InitializeCharge;
import com.tima.platform.model.api.request.paystack.InitiateTransfer;
import com.tima.platform.model.api.response.paystack.PaystackVerifyResponse;
import com.tima.platform.model.api.response.paystack.TransferRecord;
import com.tima.platform.model.constant.StatusType;
import com.tima.platform.repository.CustomerBankDetailRepository;
import com.tima.platform.repository.InfluencerPaymentContractRepository;
import com.tima.platform.service.card.UserSavedCardService;
import com.tima.platform.service.helper.PaymentProviderService;
import com.tima.platform.util.AppUtil;
import com.tima.platform.util.LoggerHelper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import reactor.util.function.Tuple2;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Objects;

import static com.tima.platform.exception.ApiErrorHandler.handleOnErrorResume;
import static com.tima.platform.model.constant.StatusType.*;
import static com.tima.platform.model.security.TimaAuthority.ADMIN_BRAND;
import static org.springframework.http.HttpStatus.BAD_REQUEST;

/**
 * @Author: Josiah Adetayo
 * @Email: josleke@gmail.com, josiah.adetayo@meld-tech.com
 * @Date: 1/5/24
 */
@Service
@RequiredArgsConstructor
public class TransactionService {
    private final LoggerHelper log = LoggerHelper.newInstance(TransactionService.class.getName());
    private final PaymentProviderService providerService;
    private final InfluencerPaymentContractRepository contractRepository;
    private final CustomerBankDetailRepository detailRepository;
    private final PaymentHistoryService historyService;
    private final InfluencerPaymentLogService paymentLogService;
    private final UserSavedCardService cardService;

    private static final String TRANSACTION_SOURCE = "balance";
    private static final String TRANSACTION_REASON = "Influencer Contract Payment:";
    private static final String TRANSACTION_MSG = "Payment Transaction request executed successfully";
    private static final String INVALID_CUSTOMER = "Invalid Contract status provided";
    private static final String INSUFFICIENT_BALANCE= "Amount is more than the contract balance";
    private static final String INVALID_CONTRACT = "The Contract id is invalid";
    private static final String INVALID_STATUS = "Invalid Payment status provided";

    @PreAuthorize(ADMIN_BRAND)
    public Mono<AppResponse> initiateTransfer(InitiateContractPayment contractPayment) {
        log.info("Initiated Transfer ", contractPayment);
        return getContract(contractPayment.contractId())
                .flatMap(contract -> checkBalance(contract, contractPayment.amount()) )
                .flatMap(contract -> getCustomer(contract.getInfluencerPublicId())
                        .flatMap(customer -> getCustomer(contract, customer))
                ).flatMap(tuples ->
                        buildHistory(tuples.getT1(), tuples.getT2(),
                                contractPayment.amount(), "", StatusType.PENDING))
                .flatMap(historyService::addPaymentHistory)
                .flatMap(history -> Mono.just(json(history.getInitialRequest(), InitiateTransfer.class)))
                .flatMap(providerService::initiateTransfer)
                .flatMap(appResponse -> historyService.updatePaymentHistory(
                        json(json(appResponse.getData()), TransferRecord.class)) )
                .map(PaymentHistoryConverter::mapToRecord)
                .map(r -> AppUtil.buildAppResponse(r, TRANSACTION_MSG));
    }
    @PreAuthorize(ADMIN_BRAND)
    public Mono<AppResponse> initiatePayment(PaymentRequest request, String publicId) {
        log.info("Initiate Payment ...", publicId);
        return buildHistory(request, publicId)
                .flatMap(historyService::addPaymentHistory)
                .flatMap(history -> providerService.initializePayment(request, history.getReference()))
                .map(paystack -> AppUtil.buildAppResponse(paystack.data().authorizationUrl(), TRANSACTION_MSG));
    }
    @PreAuthorize(ADMIN_BRAND)
    public Mono<AppResponse> verifyTransaction(String reference) {
        return checkTransactionStatus(reference)
                .flatMap(b -> (!b) ? providerService.verifyTransaction(reference) :
                        handleOnErrorResume(new AppException("Payment Was Successful"), BAD_REQUEST.value()))
                .flatMap(response -> build(response, reference))
                .flatMap(aggregate -> updateResponse(aggregate.getT1(), aggregate.getT2()))
                .flatMap(historyService::updatePaymentHistory)
                .flatMap(cardService::storeCard)
                .map(r -> AppUtil.buildAppResponse(r, TRANSACTION_MSG));
    }

    @PreAuthorize(ADMIN_BRAND)
    public Mono<AppResponse> saveBelatedTransfer(ManualTransferRecord transferRecord) {
        log.info("Saving Transfer Record ", transferRecord);
        StatusType status = parseStatus(transferRecord.paymentRecord().status());
        if(Objects.isNull(status)) return handleOnErrorResume(new AppException(INVALID_STATUS), BAD_REQUEST.value());
        return getContract(transferRecord.contractId())
                .flatMap(contract -> checkBalance(contract, transferRecord.paymentRecord().amount()) )
                .flatMap(contract -> getCustomer(contract.getInfluencerPublicId())
                        .flatMap(customer -> getCustomer(contract, customer))
                ).flatMap(tuples -> buildHistory(tuples.getT1(), tuples.getT2(),
                        transferRecord.paymentRecord().amount(),
                        transferRecord.paymentRecord().reference(),
                        StatusType.SUCCESS))
                .flatMap(historyService::addPaymentHistory)
                .doOnNext(history -> log.info("Done updating the payment history"))
                .flatMap(history -> processBeneficiary(history, transferRecord.contractId()))
                .map(r -> AppUtil.buildAppResponse(r, TRANSACTION_MSG));
    }

    public Mono<AppResponse> initiateCharge(String id, PaymentRequest request) {
        log.info("Charging User Card ", id, request);
        return buildHistory(request, id)
                .flatMap(historyService::addPaymentHistory)
                .flatMap(history -> cardService.getCard(id)
                        .map(card -> InitializeCharge.builder()
                                .email(card.email())
                                .authCode(card.authCode())
                                .reference(history.getReference())
                                .amount(history.getAmount().multiply(new BigDecimal(100) ))
                                .build())
                        .flatMap(providerService::initializeCharge)
                        .flatMap(response -> updateResponse(response, history))
                )
                .flatMap(historyService::updatePaymentHistory)
                .map(PaymentHistoryConverter::mapToRecord)
                .map(charge -> AppUtil.buildAppResponse(charge, TRANSACTION_MSG));
    }



    /**
     * Helper method to check balance
     * @param contract the payment contract information
     * @param amount the amount to be paid
     * @return Mono
     */
    private Mono<InfluencerPaymentContract> checkBalance(InfluencerPaymentContract contract, BigDecimal amount) {
        if(contract.getBalance().compareTo(amount) >= 0) return Mono.just(contract);
        else return handleOnErrorResume(new AppException(INSUFFICIENT_BALANCE), BAD_REQUEST.value());
    }

    private Mono<InfluencerPaymentContract> getContract(String contractId) {
        return contractRepository.findByContractId(contractId)
                .switchIfEmpty(handleOnErrorResume(new AppException(INVALID_CONTRACT), BAD_REQUEST.value()));
    }

    private Mono<CustomerBankDetail> getCustomer(String id) {
        return detailRepository.findByPublicId(id)
                .switchIfEmpty(handleOnErrorResume(new AppException(INVALID_CUSTOMER), BAD_REQUEST.value()));
    }

    private Mono<Tuple2<InfluencerPaymentContract, CustomerBankDetail>>
    getCustomer(InfluencerPaymentContract contract, CustomerBankDetail customer) {
        return Mono.zip(Mono.just(contract), Mono.just(customer));
    }

    private Mono<AppResponse> processBeneficiary(PaymentHistory history, String contractId) {
        return contractRepository.findByContractId(contractId)
                .doOnNext(contract -> log.info("Got contract id to be used to update recipient and contract ",
                        contract.getContractId()) )
                .flatMap(contract -> build(contract, history)
                        .doOnNext(i -> log.info("Executing Recipient and Contract update Async"))
                        .publishOn(Schedulers.boundedElastic())
                        .flatMap(paymentLogService::addTransaction)
                        .flatMap(appResponse -> {
                            contract.setBalance(contract.getBalance().subtract(history.getAmount()));
                            if(contract.getBalance().longValue() <= 0 )
                                contract.setStatus(COMPLETED.name());
                            return contractRepository.save(contract);
                        })
                )
                .map(contract -> AppUtil.buildAppResponse("Payment Updated", TRANSACTION_MSG))
                .switchIfEmpty(handleOnErrorResume(
                        new AppException("Contract Information is not found"), BAD_REQUEST.value()));
    }

    private Mono<PaymentHistory> buildHistory(InfluencerPaymentContract contract,
                                              CustomerBankDetail customer,
                                              BigDecimal amount,
                                              String extReference,
                                              StatusType type) {
        String reference = (Objects.isNull(extReference) || extReference.isEmpty()) ?
                AppUtil.generateReferenceNumber(contract.getInfluencerPublicId()) : extReference;
        return Mono.just(PaymentHistory.builder()
                        .initialRequest(json(InitiateTransfer.builder()
                                .source(TRANSACTION_SOURCE)
                                .amount(amount.multiply(BigDecimal.valueOf(100L)))
                                .reference(reference)
                                .recipient(customer.getSubAccountCode())
                                .reason(TRANSACTION_REASON + contract.getContractId())
                                .build() ))
                        .name(contract.getInfluencerName())
                        .amount(amount)
                        .balance(contract.getBalance().subtract(amount))
                        .transactionDate(Instant.now())
                        .reference(reference)
                        .publicId(contract.getInfluencerPublicId())
                        .status(type.name())
                        .type("TRANSFER")
                .build() );
    }
    private Mono<PaymentHistory> buildHistory(PaymentRequest request, String publicId) {
        String reference = AppUtil.generateReferenceNumber(publicId);
        return Mono.just(PaymentHistory.builder()
                        .initialRequest(json(request))
                        .name(request.name())
                        .amount(request.amount())
                        .balance(BigDecimal.ZERO)
                        .transactionDate(Instant.now())
                        .reference(reference)
                        .publicId(publicId)
                        .status(StatusType.PENDING.name())
                        .type("PAYMENT")
                .build() );
    }

    private Mono<PaymentHistory> updateResponse(PaystackVerifyResponse response, PaymentHistory history) {
        log.info(response.data());
        if(response.data().gatewayResponse().equalsIgnoreCase("successful") ||
                response.data().gatewayResponse().equalsIgnoreCase("approved")) {
            history.setPaymentResponse(json(response.data()));
            history.setStatus(StatusType.SUCCESS.name());
            return Mono.just(history);
        }else {
            history.setPaymentResponse(json(response.data()));
            history.setStatus(StatusType.FAILED.name());
            return Mono.just(history);
        }
    }
    private Mono<Tuple2<PaystackVerifyResponse, PaymentHistory>>
                                build(PaystackVerifyResponse response, String reference) {
        return Mono.zip(Mono.just(response), historyService.getPaymentHistory(reference, StatusType.PENDING));
    }

    private Mono<Boolean> checkTransactionStatus(String reference) {
        return historyService.getPaymentHistory(reference)
                .map(history -> history.getStatus().equalsIgnoreCase(SUCCESS.name()))
                .switchIfEmpty(handleOnErrorResume(
                        new AppException("Invalid Reference"), BAD_REQUEST.value()));
    }

    private Mono<InfluencerTransaction> build(InfluencerPaymentContract contract, PaymentHistory history) {
        long balance = contract.getBalance().subtract(history.getAmount()).longValue();
        return Mono.just(InfluencerTransaction.builder()
                .publicId(contract.getInfluencerPublicId())
                .earning(history.getAmount())
                .brandName(contract.getBrandName())
                .transactionDate(history.getTransactionDate())
                .campaignName(contract.getCampaignName())
                .status((balance <= 0)? COMPLETED.name() : PARTIAL.name())
                .balance(contract.getBalance().subtract(history.getAmount()))
                .build() );
    }

    private StatusType parseStatus(String status) {
        try {
            return StatusType.valueOf(status);
        }catch (Exception e) {
            return null;
        }
    }

    private String json(Object item) {
        return AppUtil.gsonInstance().toJson(item);
    }
    private <T> T json(String object, Class<T> item) {
        return AppUtil.gsonInstance().fromJson(object, item);
    }

}
