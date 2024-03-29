package com.tima.platform.resource.contract;

import com.tima.platform.config.AuthTokenConfig;
import com.tima.platform.model.api.ApiResponse;
import com.tima.platform.model.api.response.InfluencerPaymentContractRecord;
import com.tima.platform.service.InfluencerPaymentContractService;
import com.tima.platform.util.LoggerHelper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.time.Year;
import java.time.temporal.ChronoField;

import static com.tima.platform.model.api.ApiResponse.buildServerResponse;
import static com.tima.platform.model.api.ApiResponse.reportSettings;

/**
 * @Author: Josiah Adetayo
 * @Email: josleke@gmail.com, josiah.adetayo@meld-tech.com
 * @Date: 1/3/24
 */
@Service
@RequiredArgsConstructor
public class ContractResourceHandler {
    LoggerHelper log = LoggerHelper.newInstance(ContractResourceHandler.class.getName());
    private final InfluencerPaymentContractService contractService;
    private static final String X_FORWARD_FOR = "X-Forwarded-For";

    /**
     *  This section marks the payment contracts activities
     */
    public Mono<ServerResponse> getAllContract(ServerRequest request)  {
        log.info("Get Contracts Requested", request.headers().firstHeader(X_FORWARD_FOR));
        return buildServerResponse(contractService.getAllContract(reportSettings(request, false)));
    }

    public Mono<ServerResponse> getContractByStatus(ServerRequest request)  {
        String status = request.pathVariable("status");
        log.info("Get Contract by status Requested", request.headers().firstHeader(X_FORWARD_FOR));
        return buildServerResponse(contractService.getContractByStatus(status, reportSettings(request, false)));
    }

    public Mono<ServerResponse> getContractByIds(ServerRequest request)  {
        String influencerId = request.pathVariable("influencerId");
        String campaignId = request.pathVariable("campaignId");
        log.info("Get Contract Requested ", request.headers().firstHeader(X_FORWARD_FOR));
        return buildServerResponse(contractService.getContractById(influencerId, campaignId));
    }

    public Mono<ServerResponse> getContractById(ServerRequest request)  {
        String id = request.pathVariable("id");
        log.info("Get Contract By Id Requested ", request.headers().firstHeader(X_FORWARD_FOR));
        return buildServerResponse(contractService.getContractById(id));
    }

    public Mono<ServerResponse> addContract(ServerRequest request)  {
        Mono<InfluencerPaymentContractRecord> recordMono = request.bodyToMono(InfluencerPaymentContractRecord.class);
        log.info("Add New Contract Requested", request.headers().firstHeader(X_FORWARD_FOR));
        return recordMono
                .map(contractService::addContract)
                .flatMap(ApiResponse::buildServerResponse);
    }

    public Mono<ServerResponse> updateContract(ServerRequest request)  {
        Mono<InfluencerPaymentContractRecord> recordMono = request.bodyToMono(InfluencerPaymentContractRecord.class);
        log.info("Update Contract Requested", request.headers().firstHeader(X_FORWARD_FOR));
        return recordMono
                .map(contractService::updateContract)
                .flatMap(ApiResponse::buildServerResponse);
    }

    public Mono<ServerResponse> deleteContract(ServerRequest request)  {
        String contractId = request.pathVariable("id");
        log.info("Delete Contract Requested", request.headers().firstHeader(X_FORWARD_FOR));
        return buildServerResponse(contractService.deleteContract(contractId));
    }

    public Mono<ServerResponse> getPaymentAggregateDashboard(ServerRequest request)  {
        Mono<JwtAuthenticationToken> jwtAuthToken = AuthTokenConfig.authenticatedToken(request);
        log.info("Get Payment Aggregation Dashboard Requested", request.headers().firstHeader(X_FORWARD_FOR));
        return jwtAuthToken
                .map(ApiResponse::getTokenAndId)
                .map(tokenId -> contractService.getPaymentTotals(tokenId.token(), tokenId.publicId()))
                .flatMap(ApiResponse::buildServerResponse);
    }

    public Mono<ServerResponse> getInfluencerPaymentDashboard(ServerRequest request)  {
        Mono<JwtAuthenticationToken> jwtAuthToken = AuthTokenConfig.authenticatedToken(request);
        log.info("Get Influencer Payment Aggregation Dashboard Requested",
                request.headers().firstHeader(X_FORWARD_FOR));
        return jwtAuthToken
                .map(ApiResponse::getPublicIdFromToken)
                .map(contractService::getPaymentTotalsForInfluencer)
                .flatMap(ApiResponse::buildServerResponse);
    }

    public Mono<ServerResponse> getInfluencerPaymentGraph(ServerRequest request)  {
        Mono<JwtAuthenticationToken> jwtAuthToken = AuthTokenConfig.authenticatedToken(request);
        int year = Integer.parseInt(request.queryParam("year").orElse(String.valueOf(Year.now())) );
        log.info("Get Influencer Payment Graph Requested",
                request.headers().firstHeader(X_FORWARD_FOR));
        return jwtAuthToken
                .map(ApiResponse::getPublicIdFromToken)
                .map(id ->  contractService.getPaymentGraph(id, year))
                .flatMap(ApiResponse::buildServerResponse);
    }
}
