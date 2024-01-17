package com.tima.platform.service.helper;

import com.tima.platform.config.client.HttpConnectorService;
import com.tima.platform.domain.CustomerBankDetail;
import com.tima.platform.model.api.AppResponse;
import com.tima.platform.model.api.request.PaymentRequest;
import com.tima.platform.model.api.request.paystack.CreateTransferRecipient;
import com.tima.platform.model.api.request.paystack.InitializeCharge;
import com.tima.platform.model.api.request.paystack.InitiateTransfer;
import com.tima.platform.model.api.request.paystack.PaystackRequest;
import com.tima.platform.model.api.response.paystack.PaystackInitializeResponse;
import com.tima.platform.model.api.response.paystack.PaystackVerifyResponse;
import com.tima.platform.model.api.response.paystack.TransferRecipientRecord;
import com.tima.platform.model.constant.PaymentMethodType;
import com.tima.platform.repository.PaymentMethodRepository;
import com.tima.platform.util.AppUtil;
import com.tima.platform.util.LoggerHelper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static com.tima.platform.model.constant.AppConstant.*;
import static com.tima.platform.model.constant.PaymentMethodType.DEMO;

/**
 * @Author: Josiah Adetayo
 * @Email: josleke@gmail.com, josiah.adetayo@meld-tech.com
 * @Date: 1/1/24
 */
@Service
@RequiredArgsConstructor
public class PaymentProviderService {
    private final LoggerHelper log = LoggerHelper.newInstance(PaymentProviderService.class.getName());
    private final HttpConnectorService connectorService;
    private final PaymentMethodRepository methodRepository;

    @Value("${paystack.createRecipient.url}")
    private String createRecipientUrl;

    @Value("${paystack.transfer.url}")
    private String transferUrl;

    @Value("${paystack.access.type}")
    private String accountType;

    @Value("${paystack.access.name}")
    private String providerName;

    @Value("${paystack.callback.url}")
    private String callbackUrl;

    @Value("${paystack.charge.url}")
    private String chargeUrl;

    public Mono<CustomerBankDetail> createOrUpdateRecipient(CustomerBankDetail customer) {
        log.info("Creating new Customer Recipient Account ", customer);
        if(checkCurrency(customer.getCurrency(), "Naira") || checkCurrency(customer.getCurrency(), "NGN"))
            return Mono.just(customer);
        return methodRepository.findByNameAndType(providerName, parseStatus(accountType).name())
                .flatMap(paymentMethod ->
                        connectorService.post(createRecipientUrl, toJson(createAccount(customer)),
                                headers(paymentMethod.getApiKey()), AppResponse.class)
                ).flatMap(appResponse -> Mono.just(toJson(appResponse.getData())) )
                .map(s -> json(s, TransferRecipientRecord.class) )
                .map(recipient -> {
                    customer.setSubAccountCode(recipient.recipientCode());
                    customer.setResponse(toJson(recipient));
                    customer.setAccountName(recipient.details().accountName());
                    return customer;
                }).switchIfEmpty(Mono.just(customer));
    }

    public Mono<AppResponse> initiateTransfer(InitiateTransfer transfer) {
        log.info("Initiating Transfer ", transfer);
        if(Objects.isNull(transfer) || transfer.recipient().isEmpty()) return Mono.empty();
        return methodRepository.findByNameAndType(providerName, parseStatus(accountType).name())
                .flatMap(paymentMethod ->
                        connectorService.post(transferUrl, toJson(transfer),
                                headers(paymentMethod.getApiKey()), AppResponse.class)
                );
    }

    public Mono<PaystackInitializeResponse> initializePayment(PaymentRequest request, String reference) {
        log.info("Initiated payment ", request);
        return methodRepository.findByNameAndType(providerName, parseStatus(accountType).name())
                .flatMap(paymentMethod ->
                        connectorService.post(paymentMethod.getInitiatePaymentUrl(),
                                toJson(buildRequest(request, reference)),
                                headers(paymentMethod.getApiKey()), PaystackInitializeResponse.class)
                );
    }
    public Mono<PaystackVerifyResponse> initializeCharge(InitializeCharge request) {
        log.info("Initiated Charge ", request);
        return methodRepository.findByNameAndType(providerName, parseStatus(accountType).name())
                .flatMap(paymentMethod ->
                        connectorService.post(chargeUrl, toJson(request),
                                headers(paymentMethod.getApiKey()), PaystackVerifyResponse.class)
                );
    }


    public Mono<PaystackVerifyResponse> verifyTransaction(String transactionReference) {
        log.info("Requesting Payment Verification with provider...");
        return methodRepository.findByNameAndType(providerName, parseStatus(accountType).name())
                        .flatMap(paymentMethod ->
                                connectorService.get(
                                        paymentMethod.getVerifyPaymentUrl() +  transactionReference,
                                                headers(paymentMethod.getApiKey()),
                                                PaystackVerifyResponse.class)
                        );
    }

    private PaystackRequest buildRequest(PaymentRequest request, String reference) {
        return PaystackRequest.builder()
                .reference(reference)
                .email(request.email())
                .callbackUrl(callbackUrl)
                .currency(CURRENCY)
                .amount(request.amount().multiply(BigDecimal.valueOf(100)))
                .build();
    }


    private <T> String toJson(T item) { return AppUtil.gsonInstance().toJson(item); }

    private  <T> T json(String data, Class<T> returnType) {
        return AppUtil.gsonInstance().fromJson(data, returnType);
    }

    private CreateTransferRecipient createAccount(CustomerBankDetail bankDetail) {
        return CreateTransferRecipient.builder()
                .type(ACCOUNT_TYPE)
                .name(bankDetail.getAccountName())
                .accountNumber(bankDetail.getAccountNumber())
                .bankCode(bankDetail.getBankCode())
                .currency(CURRENCY)
                .build();
    }

    private Map<String, String> headers(String accessToken) {
        Map<String, String> headers = new HashMap<>();
        headers.put(ACCEPT, MEDIA_TYPE_JSON);
        headers.put(CONTENT_TYPE, MEDIA_TYPE_JSON);
        headers.put(AUTHORIZATION, "Bearer " + accessToken);
        return headers;
    }

    private boolean checkCurrency(String currency, String type) {
        return Objects.isNull(currency) || !currency.equalsIgnoreCase(type);
    }

    private PaymentMethodType parseStatus(String type) {
        try {
            return PaymentMethodType.valueOf(type);
        }catch (Exception e) {
            return DEMO;
        }
    }
}
