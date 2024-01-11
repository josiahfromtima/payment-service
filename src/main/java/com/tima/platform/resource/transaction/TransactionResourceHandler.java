package com.tima.platform.resource.transaction;

import com.tima.platform.config.AuthTokenConfig;
import com.tima.platform.config.CustomValidator;
import com.tima.platform.model.api.ApiResponse;
import com.tima.platform.model.api.request.InitiateContractPayment;
import com.tima.platform.model.api.request.ManualTransferRecord;
import com.tima.platform.model.api.request.PaymentRequest;
import com.tima.platform.service.TransactionService;
import com.tima.platform.util.LoggerHelper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import static com.tima.platform.model.api.ApiResponse.buildServerResponse;

/**
 * @Author: Josiah Adetayo
 * @Email: josleke@gmail.com, josiah.adetayo@meld-tech.com
 * @Date: 1/3/24
 */
@Service
@RequiredArgsConstructor
public class TransactionResourceHandler {
    LoggerHelper log = LoggerHelper.newInstance(TransactionResourceHandler.class.getName());
    private final TransactionService transactionService;
    private final CustomValidator validator;

    /**
     *  This section marks the payment transaction activities
     */

    public Mono<ServerResponse> initiateTransfer(ServerRequest request)  {
        Mono<InitiateContractPayment> recordMono = request.bodyToMono(InitiateContractPayment.class)
                .doOnNext(validator::validateEntries);
        log.info("Initiate Transfer Requested", request.remoteAddress().orElse(null));
        return recordMono
                .map(transactionService::initiateTransfer)
                .flatMap(ApiResponse::buildServerResponse);
    }
    public Mono<ServerResponse> initiatePay(ServerRequest request)  {
        Mono<JwtAuthenticationToken> jwtAuthToken = AuthTokenConfig.authenticatedToken(request);
        Mono<PaymentRequest> recordMono = request.bodyToMono(PaymentRequest.class)
                .doOnNext(validator::validateEntries);
        log.info("Initiate Payment Requested", request.remoteAddress().orElse(null));
        return jwtAuthToken
                .map(ApiResponse::getPublicIdFromToken)
                .flatMap(id -> recordMono
                        .map(payRequest ->  transactionService.initiatePayment(payRequest, id)) )
                .flatMap(ApiResponse::buildServerResponse);
    }

    public Mono<ServerResponse> verifyPay(ServerRequest request)  {
        String reference = request.pathVariable("reference");
        log.info("Verify Payment Requested", request.remoteAddress().orElse(null));
        return buildServerResponse(transactionService.verifyTransaction(reference));
    }

    public Mono<ServerResponse> saveTransfer(ServerRequest request)  {
        Mono<ManualTransferRecord> recordMono = request.bodyToMono(ManualTransferRecord.class)
                .doOnNext(validator::validateEntries)
                .doOnNext(transferRecord ->  validator.validateEntries(transferRecord.paymentRecord()));
        log.info("Save Manually Done Transfer Requested", request.remoteAddress().orElse(null));
        return recordMono
                .map(transactionService::saveBelatedTransfer)
                .flatMap(ApiResponse::buildServerResponse);
    }

}