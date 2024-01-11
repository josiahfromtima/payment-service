package com.tima.platform.resource.payment.influencer;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.accept;
import static org.springframework.web.reactive.function.server.RequestPredicates.contentType;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

/**
 * @Author: Josiah Adetayo
 * @Email: josleke@gmail.com, josiah.adetayo@meld-tech.com
 * @Date: 12/18/23
 */
@Configuration
public class InfluencerPaymentLogResourceConfig {
    public static final String API_V1_URL = "/v1";
    public static final String PAYMENT_BASE = API_V1_URL + "/transactions";
    public static final String BRAND_PAYMENT= PAYMENT_BASE + "/brand/{name}";
    public static final String PAYMENT_STATUS = PAYMENT_BASE + "/status/{status}";
    public static final String PAYMENT_BY_DATE = PAYMENT_BASE + "/date";

    @Bean
    public RouterFunction<ServerResponse> paymentLogEndpointHandler(InfluencerPaymentLogResourceHandler handler) {
        return route()
                .GET(PAYMENT_BASE, accept(MediaType.APPLICATION_JSON), handler::getAllTransactions)
                .GET(BRAND_PAYMENT, accept(MediaType.APPLICATION_JSON), handler::getTransactionsByBrand)
                .GET(PAYMENT_STATUS, accept(MediaType.APPLICATION_JSON), handler::getTransactionsByStatus)
                .GET(PAYMENT_BY_DATE, accept(MediaType.APPLICATION_JSON), handler::getTransactionsByDate)
                .build();
    }
}
