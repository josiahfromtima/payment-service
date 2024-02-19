package com.tima.platform.resource.contract;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static com.tima.platform.resource.payment.history.PaymentHistoryResourceConfig.PAYMENT_BASE;
import static org.springframework.web.reactive.function.server.RequestPredicates.accept;
import static org.springframework.web.reactive.function.server.RequestPredicates.contentType;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

/**
 * @Author: Josiah Adetayo
 * @Email: josleke@gmail.com, josiah.adetayo@meld-tech.com
 * @Date: 1/3/24
 */
@Configuration
public class ContractResourceConfig {
    public static final String API_V1_URL = "/v1";
    public static final String CONTRACT_BASE = API_V1_URL + "/contracts";
    public static final String CAMPAIGN_DASHBOARD = PAYMENT_BASE + "/campaign/dashboard";
    public static final String INFLUENCER_DASHBOARD = PAYMENT_BASE + "/influencer/dashboard";
    public static final String INFLUENCER_GRAPH = PAYMENT_BASE + "/influencer/graph";
    public static final String CONTRACT_BY_IDS = CONTRACT_BASE + "/{influencerId}/{campaignId}";
    public static final String CONTRACT_BY_ID = CONTRACT_BASE + "/{id}";
    public static final String CONTRACT_BY_STATUS = CONTRACT_BASE + "/status/{status}";

    @Bean
    public RouterFunction<ServerResponse> contractEndpointHandler(ContractResourceHandler handler) {
        return route()
                .GET(CONTRACT_BASE, accept(MediaType.APPLICATION_JSON), handler::getAllContract)
                .GET(CONTRACT_BY_STATUS, accept(MediaType.APPLICATION_JSON), handler::getContractByStatus)
                .GET(CONTRACT_BY_IDS, accept(MediaType.APPLICATION_JSON), handler::getContractByIds)
                .GET(CONTRACT_BY_ID, accept(MediaType.APPLICATION_JSON), handler::getContractById)
                .GET(CAMPAIGN_DASHBOARD, accept(MediaType.APPLICATION_JSON), handler::getPaymentAggregateDashboard)
                .GET(INFLUENCER_DASHBOARD, accept(MediaType.APPLICATION_JSON), handler::getInfluencerPaymentDashboard)
                .GET(INFLUENCER_GRAPH, accept(MediaType.APPLICATION_JSON), handler::getInfluencerPaymentGraph)
                .POST(CONTRACT_BASE, accept(MediaType.APPLICATION_JSON)
                        .and(contentType(MediaType.APPLICATION_JSON)), handler::addContract)
                .PUT(CONTRACT_BASE, accept(MediaType.APPLICATION_JSON)
                        .and(contentType(MediaType.APPLICATION_JSON)), handler::updateContract)
                .DELETE(CONTRACT_BY_ID, accept(MediaType.APPLICATION_JSON), handler::deleteContract)
                .build();
    }
}
