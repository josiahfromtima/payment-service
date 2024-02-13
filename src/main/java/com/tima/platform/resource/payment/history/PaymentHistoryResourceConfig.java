package com.tima.platform.resource.payment.history;

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
public class PaymentHistoryResourceConfig {
    public static final String API_V1_URL = "/v1";
    public static final String PAYMENT_BASE = API_V1_URL + "/histories";
    public static final String PAYMENT_BY_DATE_RANGE = PAYMENT_BASE + "/date/{status}";
    public static final String PAYMENT_STATUSES = API_V1_URL + "/statuses";
    public static final String GET_PAYMENT_BY_STATUS = PAYMENT_BASE + "/{status}";

    @Bean
    public RouterFunction<ServerResponse> paymentHistoryEndpointHandler(PaymentHistoryResourceHandler handler) {
        return route()
                .GET(PAYMENT_BASE, accept(MediaType.APPLICATION_JSON), handler::getPaymentHistories)
//                .GET(CAMPAIGN_DASHBOARD, accept(MediaType.APPLICATION_JSON), handler::getPaymentAggregateDashboard)
                .GET(PAYMENT_BY_DATE_RANGE, accept(MediaType.APPLICATION_JSON), handler::getPaymentHistoryByDateRange)
                .GET(PAYMENT_STATUSES, accept(MediaType.APPLICATION_JSON), handler::getAllPaymentStatus)
                .GET(GET_PAYMENT_BY_STATUS, accept(MediaType.APPLICATION_JSON), handler::getPaymentHistoryByStatus)
                .build();
    }
}
