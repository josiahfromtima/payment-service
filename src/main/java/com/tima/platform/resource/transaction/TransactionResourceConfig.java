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
    public static final String GET_CARDS_BY_USER_ID = API_V1_URL + "/cards";
    public static final String DELETE_CARD = API_V1_URL + "/cards";
    public static final String UPDATE_CARDS_DEFAULT = API_V1_URL + "/cards/default";
    public static final String CHARGE_DEFAULT_CARD = API_V1_URL + "/cards/default/charge";

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
                .GET(GET_CARDS_BY_USER_ID, accept(MediaType.APPLICATION_JSON), handler::getCards)
                .DELETE(DELETE_CARD, accept(MediaType.APPLICATION_JSON)
                        .and(contentType(MediaType.APPLICATION_JSON)), handler::removeCard)
                .PUT(UPDATE_CARDS_DEFAULT, accept(MediaType.APPLICATION_JSON)
                        .and(contentType(MediaType.APPLICATION_JSON)), handler::setDefaultCard)
                .POST(CHARGE_DEFAULT_CARD, accept(MediaType.APPLICATION_JSON)
                        .and(contentType(MediaType.APPLICATION_JSON)), handler::chargeCard)
                .build();
    }
}
