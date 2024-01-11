package com.tima.platform.resource.payment.method;

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
public class PaymentMethodResourceConfig {
    public static final String API_V1_URL = "/v1";
    public static final String PAYMENT_METHOD_BASE = API_V1_URL + "/methods";
    public static final String GET_METHOD_TYPES=  PAYMENT_METHOD_BASE + "/types";
    public static final String GET_PUBLIC_METHODS = PAYMENT_METHOD_BASE + "/_public";

    @Bean
    public RouterFunction<ServerResponse> paymentMethodEndpointHandler(PaymentMethodResourceHandler handler) {
        return route()
                .GET(GET_METHOD_TYPES, accept(MediaType.APPLICATION_JSON), handler::getMethodTypes)
                .GET(GET_PUBLIC_METHODS, accept(MediaType.APPLICATION_JSON), handler::getPaymentMethodsPublic)
                .GET(PAYMENT_METHOD_BASE, accept(MediaType.APPLICATION_JSON), handler::getPaymentMethodsAdmin)
                .POST(PAYMENT_METHOD_BASE, accept(MediaType.APPLICATION_JSON)
                        .and(contentType(MediaType.APPLICATION_JSON)), handler::addNewPaymentMethod)
                .PUT(PAYMENT_METHOD_BASE, accept(MediaType.APPLICATION_JSON)
                        .and(contentType(MediaType.APPLICATION_JSON)), handler::editPaymentMethod)
                .DELETE(PAYMENT_METHOD_BASE, accept(MediaType.APPLICATION_JSON), handler::deleteCPaymentMethod)
                .build();
    }
}
