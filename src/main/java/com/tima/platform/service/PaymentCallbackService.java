package com.tima.platform.service;

import com.tima.platform.domain.InfluencerPaymentContract;
import com.tima.platform.domain.InfluencerTransaction;
import com.tima.platform.domain.PaymentHistory;
import com.tima.platform.exception.AppException;
import com.tima.platform.model.api.AppResponse;
import com.tima.platform.model.api.response.paystack.transfer.PaystackEventTransferResponse;
import com.tima.platform.model.api.response.paystack.transfer.event.PaystackTransferEventResponse;
import com.tima.platform.repository.InfluencerPaymentContractRepository;
import com.tima.platform.repository.PaymentHistoryRepository;
import com.tima.platform.util.AppUtil;
import com.tima.platform.util.LoggerHelper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.Objects;

import static com.tima.platform.exception.ApiErrorHandler.handleOnErrorResume;
import static com.tima.platform.model.constant.StatusType.*;
import static org.springframework.http.HttpStatus.BAD_REQUEST;

/**
 * @Author: Josiah Adetayo
 * @Email: josleke@gmail.com, josiah.adetayo@meld-tech.com
 * @Date: 1/9/24
 */
@Service
@RequiredArgsConstructor
public class PaymentCallbackService {
    private final LoggerHelper log = LoggerHelper.newInstance(PaymentCallbackService.class.getName());
    private final PaymentHistoryRepository historyRepository;
    private final InfluencerPaymentLogService paymentLogService;
    private final InfluencerPaymentContractRepository contractRepository;

    private static final String PAYMENT_MSG = "The Payment and Recipient Details Updated";

    public Mono<AppResponse> updatePayment(String eventResponse) {
        log.info(eventResponse);
        if(eventResponse.contains("transfer."))
            return transferEvent(json(eventResponse, PaystackEventTransferResponse.class));
        else return Mono.just(AppUtil.buildAppResponse("Charge Completed", PAYMENT_MSG));
    }

    private Mono<AppResponse> transferEvent(PaystackEventTransferResponse resp) {
        return historyRepository
                .findByReferenceAndStatus(resp.data().reference(), PENDING.name())
                .flatMap(foundPayment -> {
                    foundPayment.setStatus(FAILED.name());
                    if(resp.data().status().equalsIgnoreCase("success") ||
                            resp.data().status().equalsIgnoreCase("approved")) {
                        foundPayment.setStatus(SUCCESS.name());
                    }
                    foundPayment.setPaymentResponse( json(resp.data()) );
                    return historyRepository.save(foundPayment);
                }).doOnNext(history -> log.info("Done updating the payment history"))
                .flatMap(this::processBeneficiary)
                .switchIfEmpty(handleOnErrorResume(
                        new AppException("Payment is already Verified"), BAD_REQUEST.value()));
    }

    private Mono<AppResponse> processBeneficiary(PaymentHistory history) {
        return contractRepository.findByContractId(getContractId(history.getPaymentResponse() ))
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
                .map(contract -> AppUtil.buildAppResponse("Payment Updated", PAYMENT_MSG))
                .switchIfEmpty(handleOnErrorResume(
                        new AppException("Contract Information is not found"), BAD_REQUEST.value()));
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
    private String getContractId(String payload) {
        try {
            PaystackTransferEventResponse response = json(payload, PaystackTransferEventResponse.class);
            log.info(response.reason());
            String[] reason = Objects.isNull(response.reason()) ? new String[]{"", ""}
                    : response.reason().split(":");
            return (reason.length == 2) ? reason[1] : "";
        }catch (Exception ex) {
            log.error(ex.getMessage());
            return "";
        }
    }
    private String json(Object item) {
        return AppUtil.gsonInstance().toJson(item);
    }
    private <T> T json(String object, Class<T> item) {
        return AppUtil.gsonInstance().fromJson(object, item);
    }
}
