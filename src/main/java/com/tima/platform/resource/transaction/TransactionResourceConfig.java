package com.tima.platform.resource.transaction;

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
 * @Date: 1/3/24
 */
@Configuration
public class TransactionResourceConfig {
    public static final String API_V1_URL = "/v1";
    public static final String TRANSACTION_BASE = API_V1_URL + "/initiate";
    public static final String TRANSFER = TRANSACTION_BASE + "/transfer";
    public static final String PAYMENT = TRANSACTION_BASE + "/payment";
    public static final String VERIFY_PAYMENT = API_V1_URL + "/verify/{reference}";

    public static final String MANUAL_TRANSFER = TRANSACTION_BASE + "/manual/transfer";

    @Bean
    public RouterFunction<ServerResponse> transactionEndpointHandler(TransactionResourceHandler handler) {
        return route()
                .POST(TRANSFER, accept(MediaType.APPLICATION_JSON)
                        .and(contentType(MediaType.APPLICATION_JSON)), handler::initiateTransfer)
                .POST(MANUAL_TRANSFER, accept(MediaType.APPLICATION_JSON)
                        .and(contentType(MediaType.APPLICATION_JSON)), handler::saveTransfer)
                .POST(PAYMENT, accept(MediaType.APPLICATION_JSON)
                        .and(contentType(MediaType.APPLICATION_JSON)), handler::initiatePay)
                .GET(VERIFY_PAYMENT, accept(MediaType.APPLICATION_JSON), handler::verifyPay)
                .build();
    }
}
