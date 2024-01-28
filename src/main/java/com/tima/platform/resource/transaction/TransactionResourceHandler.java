package com.tima.platform.resource.transaction;

import com.tima.platform.config.AuthTokenConfig;
import com.tima.platform.config.CustomValidator;
import com.tima.platform.model.api.ApiResponse;
import com.tima.platform.model.api.request.InitiateContractPayment;
import com.tima.platform.model.api.request.ManualTransferRecord;
import com.tima.platform.model.api.request.PaymentRequest;
import com.tima.platform.model.api.response.card.UserSavedCardRecord;
import com.tima.platform.service.TransactionService;
import com.tima.platform.service.card.UserSavedCardService;
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
    private final UserSavedCardService cardService;
    private final CustomValidator validator;

    private static final String X_FORWARD_FOR = "X-Forwarded-For";

    /**
     *  This section marks the payment transaction activities
     */

    public Mono<ServerResponse> initiateTransfer(ServerRequest request)  {
        Mono<InitiateContractPayment> recordMono = request.bodyToMono(InitiateContractPayment.class)
                .doOnNext(validator::validateEntries);
        log.info("Initiate Transfer Requested", request.headers().firstHeader(X_FORWARD_FOR));
        return recordMono
                .map(transactionService::initiateTransfer)
                .flatMap(ApiResponse::buildServerResponse);
    }
    public Mono<ServerResponse> initiatePay(ServerRequest request)  {
        Mono<JwtAuthenticationToken> jwtAuthToken = AuthTokenConfig.authenticatedToken(request);
        Mono<PaymentRequest> recordMono = request.bodyToMono(PaymentRequest.class)
                .doOnNext(validator::validateEntries);
        log.info("Initiate Payment Requested", request.headers().firstHeader(X_FORWARD_FOR));
        return jwtAuthToken
                .map(ApiResponse::getPublicIdFromToken)
                .flatMap(id -> recordMono
                        .map(payRequest ->  transactionService.initiatePayment(payRequest, id)) )
                .flatMap(ApiResponse::buildServerResponse);
    }

    public Mono<ServerResponse> verifyPay(ServerRequest request)  {
        String reference = request.pathVariable("reference");
        log.info("Verify Payment Requested", request.headers().firstHeader(X_FORWARD_FOR));
        return buildServerResponse(transactionService.verifyTransaction(reference));
    }

    public Mono<ServerResponse> saveTransfer(ServerRequest request)  {
        Mono<ManualTransferRecord> recordMono = request.bodyToMono(ManualTransferRecord.class)
                .doOnNext(validator::validateEntries)
                .doOnNext(transferRecord ->  validator.validateEntries(transferRecord.paymentRecord()));
        log.info("Save Manually Done Transfer Requested", request.headers().firstHeader(X_FORWARD_FOR));
        return recordMono
                .map(transactionService::saveBelatedTransfer)
                .flatMap(ApiResponse::buildServerResponse);
    }

    public Mono<ServerResponse> chargeCard(ServerRequest request)  {
        Mono<JwtAuthenticationToken> jwtAuthToken = AuthTokenConfig.authenticatedToken(request);
        Mono<PaymentRequest> recordMono = request.bodyToMono(PaymentRequest.class)
                .doOnNext(validator::validateEntries);
        log.info("Initiate Charge Card Requested", request.headers().firstHeader(X_FORWARD_FOR));
        return jwtAuthToken
                .map(ApiResponse::getPublicIdFromToken)
                .flatMap(id -> recordMono
                        .map(payRequest ->  transactionService.initiateCharge(id, payRequest)) )
                .flatMap(ApiResponse::buildServerResponse);
    }

    public Mono<ServerResponse> getCards(ServerRequest request)  {
        Mono<JwtAuthenticationToken> jwtAuthToken = AuthTokenConfig.authenticatedToken(request);
        log.info("Get Saved Cards Requested", request.headers().firstHeader(X_FORWARD_FOR));
        return jwtAuthToken
                .map(ApiResponse::getPublicIdFromToken)
                .map(cardService::getCards)
                .flatMap(ApiResponse::buildServerResponse);
    }
    public Mono<ServerResponse> removeCard(ServerRequest request)  {
        Mono<UserSavedCardRecord> transactionMono = request.bodyToMono(UserSavedCardRecord.class);
        Mono<JwtAuthenticationToken> jwtAuthToken = AuthTokenConfig.authenticatedToken(request);
        log.info("Remove Card Requested", request.headers().firstHeader(X_FORWARD_FOR) );
        return jwtAuthToken
                .map(ApiResponse::getPublicIdFromToken)
                .flatMap(id -> transactionMono.map(card ->
                        cardService.removeCards(id, card.cardNo(), card.type())))
                .flatMap(ApiResponse::buildServerResponse);
    }

    public Mono<ServerResponse> setDefaultCard(ServerRequest request)  {
        Mono<UserSavedCardRecord> transactionMono = request.bodyToMono(UserSavedCardRecord.class);
        Mono<JwtAuthenticationToken> jwtAuthToken = AuthTokenConfig.authenticatedToken(request);
        log.info("Change Default Card Requested", request.headers().firstHeader(X_FORWARD_FOR) );
        return jwtAuthToken
                .map(ApiResponse::getPublicIdFromToken)
                .flatMap(id -> transactionMono.map(card ->
                        cardService.setCardDefault(id, card.cardNo(), card.type())))
                .flatMap(ApiResponse::buildServerResponse);
    }

}
