package com.tima.platform.resource.payment.influencer;

import com.tima.platform.config.AuthTokenConfig;
import com.tima.platform.model.api.ApiResponse;
import com.tima.platform.service.InfluencerPaymentLogService;
import com.tima.platform.util.LoggerHelper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import static com.tima.platform.model.api.ApiResponse.reportSettings;

/**
 * @Author: Josiah Adetayo
 * @Email: josleke@gmail.com, josiah.adetayo@meld-tech.com
 * @Date: 12/18/23
 */
@Service
@RequiredArgsConstructor
public class InfluencerPaymentLogResourceHandler {
    LoggerHelper log = LoggerHelper.newInstance(InfluencerPaymentLogResourceHandler.class.getName());
    private final InfluencerPaymentLogService paymentLogService;
    private static final String X_FORWARD_FOR = "X-Forwarded-For";

    /**
     *  This section marks the Influencer payment log dashboard/reports activities
     */
    public Mono<ServerResponse> getAllTransactions(ServerRequest request)  {
        Mono<JwtAuthenticationToken> jwtAuthToken = AuthTokenConfig.authenticatedToken(request);
        log.info("Get All Transactions For Influencer Requested", request.headers().firstHeader(X_FORWARD_FOR));
        return jwtAuthToken
                .map(ApiResponse::getPublicIdFromToken)
                .map(id -> paymentLogService.getTransactions(id, reportSettings(request, true)))
                .flatMap(ApiResponse::buildServerResponse);
    }

    public Mono<ServerResponse> getTransactionsByStatus(ServerRequest request) {
        Mono<JwtAuthenticationToken> jwtAuthToken = AuthTokenConfig.authenticatedToken(request);
        String status = request.pathVariable("status");
        log.info("Get Transactions By Status Requested", request.headers().firstHeader(X_FORWARD_FOR));
        return jwtAuthToken
                .map(ApiResponse::getPublicIdFromToken)
                .map(id -> paymentLogService.getTransactionsByStatus(id, status, reportSettings(request, false)))
                .flatMap(ApiResponse::buildServerResponse);
    }

    public Mono<ServerResponse> getTransactionsByBrand(ServerRequest request) {
        Mono<JwtAuthenticationToken> jwtAuthToken = AuthTokenConfig.authenticatedToken(request);
        String brand = request.pathVariable("name");
        log.info("Get Transactions By Brand Name Requested ", brand, request.headers().firstHeader(X_FORWARD_FOR));
        return jwtAuthToken
                .map(ApiResponse::getPublicIdFromToken)
                .map(id -> paymentLogService.getTransactionsByBrand(id, brand, reportSettings(request, false)))
                .flatMap(ApiResponse::buildServerResponse);
    }

    public Mono<ServerResponse> getTransactionsByDate(ServerRequest request) {
        Mono<JwtAuthenticationToken> jwtAuthToken = AuthTokenConfig.authenticatedToken(request);
        String endDate = request.queryParam("endDate").orElse("");
        log.info("Get Transactions By Status Requested", request.headers().firstHeader(X_FORWARD_FOR));
        return jwtAuthToken
                .map(ApiResponse::getPublicIdFromToken)
                .map(id -> paymentLogService.getTransactionsByDate(id, endDate ))
                .flatMap(ApiResponse::buildServerResponse);
    }


}
