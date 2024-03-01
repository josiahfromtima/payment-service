package com.tima.platform.resource.search;

import com.tima.platform.config.AuthTokenConfig;
import com.tima.platform.model.api.ApiResponse;
import com.tima.platform.service.search.PaymentSearchService;
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
 * @Date: 1/3/24
 */
@Service
@RequiredArgsConstructor
public class SearchResourceHandler {
    LoggerHelper log = LoggerHelper.newInstance(SearchResourceHandler.class.getName());
    private final PaymentSearchService searchService;
    private static final String X_FORWARD_FOR = "X-Forwarded-For";

    private static final boolean USE_DATE = false;

    /**
     *  This section marks the payment search activities
     */
    public Mono<ServerResponse> getInfluencerSearch(ServerRequest request)  {
        String campaignName = request.queryParam("campaignName").orElse("");
        String brandName = request.queryParam("brandName").orElse("");
        String status = request.queryParam("status").orElse("");
        log.info("Get Influencer Search Requested", request.headers().firstHeader(X_FORWARD_FOR));
        return buildServerResponse(searchService.getInfluencerTransactions(campaignName, brandName, status,
                reportSettings(request, USE_DATE)));
    }

    public Mono<ServerResponse> getPaymentSearchByNames(ServerRequest request)  {
        String name = request.queryParam("influencerName").orElse("");
        String status = request.queryParam("status").orElse("");
        log.info("Get Payment Search with status Requested", request.headers().firstHeader(X_FORWARD_FOR));
        return buildServerResponse(searchService.getPaymentTransactions(name, status, reportSettings(request, USE_DATE)));
    }

    public Mono<ServerResponse> getPaymentAnalytics(ServerRequest request)  {
        String publicId = request.pathVariable("campaignId");
        Mono<JwtAuthenticationToken> jwtAuthToken = AuthTokenConfig.authenticatedToken(request);
        log.info("Get Payment Analytics Requested", request.headers().firstHeader(X_FORWARD_FOR));
        return jwtAuthToken
                .map(ApiResponse::getToken)
                .map(token ->  searchService.getPaymentAnalysis(publicId, token))
                .flatMap(ApiResponse::buildServerResponse);
    }

}
