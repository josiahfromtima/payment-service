package com.tima.platform.resource.bank;

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
 * @Date: 12/14/23
 */
@Configuration
public class BankResourceConfig {
    public static final String API_V1_URL = "/v1";
    public static final String BANK_BASE = API_V1_URL + "/banks";
    public static final String CUSTOMER_BASE =  API_V1_URL + "/bank/customers";
    public static final String GET_BANK_BY_NAME_CODE = BANK_BASE + "/{codeOrName}";
    public static final String GET_ALL_CUSTOMERS = CUSTOMER_BASE ;
    public static final String GET_CUSTOMERS = CUSTOMER_BASE + "/_self";
    public static final String UPDATE_CUSTOMER = CUSTOMER_BASE + "/_edit";
    public static final String DELETE_CUSTOMERS = CUSTOMER_BASE + "/{publicId}";

    @Bean
    public RouterFunction<ServerResponse> bankEndpointHandler(BankResourceHandler handler) {
        return route()
                .GET(BANK_BASE, accept(MediaType.APPLICATION_JSON), handler::getBanks)
                .GET(GET_BANK_BY_NAME_CODE, accept(MediaType.APPLICATION_JSON), handler::getBankByCode)
                .POST(CUSTOMER_BASE, accept(MediaType.APPLICATION_JSON)
                        .and(contentType(MediaType.APPLICATION_JSON)), handler::addCustomerDetail)
                .GET(GET_ALL_CUSTOMERS, accept(MediaType.APPLICATION_JSON), handler::getAllCustomerDetails)
                .GET(GET_CUSTOMERS, accept(MediaType.APPLICATION_JSON), handler::getCustomerDetail)
                .PUT(UPDATE_CUSTOMER, accept(MediaType.APPLICATION_JSON)
                        .and(contentType(MediaType.APPLICATION_JSON)), handler::updateCustomerDetail)
                .DELETE(DELETE_CUSTOMERS, accept(MediaType.APPLICATION_JSON), handler::deleteCustomerDetail)
                .build();
    }
}
