package com.tima.platform.resource.payment.history;

import com.tima.platform.config.AuthTokenConfig;
import com.tima.platform.model.api.ApiResponse;
import com.tima.platform.model.api.response.PaymentHistoryRecord;
import com.tima.platform.model.api.response.PaymentMethodRecord;
import com.tima.platform.service.PaymentHistoryService;
import com.tima.platform.service.PaymentMethodService;
import com.tima.platform.util.LoggerHelper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import static com.tima.platform.model.api.ApiResponse.buildServerResponse;
import static com.tima.platform.model.api.ApiResponse.reportSettings;

/**
 * @Author: Josiah Adetayo
 * @Email: josleke@gmail.com, josiah.adetayo@meld-tech.com
 * @Date: 12/18/23
 */
@Service
@RequiredArgsConstructor
public class PaymentHistoryResourceHandler {
    LoggerHelper log = LoggerHelper.newInstance(PaymentHistoryResourceHandler.class.getName());
    private final PaymentHistoryService historyService;
    private static final String X_FORWARD_FOR = "X-Forwarded-For";

    /**
     *  This section marks the payment dashboard/reports activities
     */
    public Mono<ServerResponse> getAllPaymentStatus(ServerRequest request)  {
        log.info("Get Payment Statuses Requested", request.headers().firstHeader(X_FORWARD_FOR));
        return buildServerResponse(historyService.getPaymentStatus());
    }

    public Mono<ServerResponse> getPaymentAggregateDashboard(ServerRequest request)  {
        Mono<JwtAuthenticationToken> jwtAuthToken = AuthTokenConfig.authenticatedToken(request);
        log.info("Get Payment Aggregation Dashboard Requested", request.headers().firstHeader(X_FORWARD_FOR));
        return jwtAuthToken
                .map(ApiResponse::getToken)
                .map(historyService::getPaymentTotals)
                .flatMap(ApiResponse::buildServerResponse);
    }

    public Mono<ServerResponse> getPaymentHistories(ServerRequest request)  {
        log.info("Get Payment Histories Requested ", request.headers().firstHeader(X_FORWARD_FOR));
        return buildServerResponse(historyService.getPaymentHistories(reportSettings(request, false)));
    }

    public Mono<ServerResponse> getPaymentHistoryByDateRange(ServerRequest request)  {
        String status = request.pathVariable("status");
        log.info("Get Payment Histories By Date Range Requested ", request.headers().firstHeader(X_FORWARD_FOR));
        return buildServerResponse(
                historyService.getPaymentHistoriesByDateAndStatus(status, reportSettings(request, true)));
    }

    public Mono<ServerResponse> getPaymentHistoryByStatus(ServerRequest request) {
        String status = request.pathVariable("status");
        log.info("Get Payment Histories By Status Requested ", request.headers().firstHeader(X_FORWARD_FOR));
        return buildServerResponse(
                historyService.getPaymentHistoryByStatus(status, reportSettings(request, true)));
    }
}
