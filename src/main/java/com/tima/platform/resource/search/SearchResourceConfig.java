package com.tima.platform.resource.search;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.accept;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

/**
 * @Author: Josiah Adetayo
 * @Email: josleke@gmail.com, josiah.adetayo@meld-tech.com
 * @Date: 1/3/24
 */
@Configuration
public class SearchResourceConfig {
    public static final String API_V1_URL = "/v1";
    public static final String SEARCH_BASE = API_V1_URL + "/histories";
    public static final String GET_INFLUENCER_SEARCH = SEARCH_BASE + "/influencer/search";
    public static final String GET_PAYMENT_SEARCH = SEARCH_BASE + "/brand/search";
    public static final String GET_PAYMENT_ANALYSIS = SEARCH_BASE + "/dashboard/campaign/{campaignId}";

    @Bean
    public RouterFunction<ServerResponse> searchEndpointHandler(SearchResourceHandler handler) {
        return route()
                .GET(GET_INFLUENCER_SEARCH, accept(MediaType.APPLICATION_JSON), handler::getInfluencerSearch)
                .GET(GET_PAYMENT_SEARCH, accept(MediaType.APPLICATION_JSON), handler::getPaymentSearchByNames)
                .GET(GET_PAYMENT_ANALYSIS, accept(MediaType.APPLICATION_JSON), handler::getPaymentAnalytics)
                .build();
    }
}
