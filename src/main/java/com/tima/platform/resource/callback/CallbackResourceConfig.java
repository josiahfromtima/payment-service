package com.tima.platform.resource.callback;

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
public class CallbackResourceConfig {
    public static final String API_V1_URL = "/v1";
    public static final String CALLBACK_BASE = API_V1_URL + "/payment";
    public static final String WEB_HOOK = CALLBACK_BASE + "/webhook";

    @Bean
    public RouterFunction<ServerResponse> callbackEndpointHandler(CallbackResourceHandler handler) {
        return route()
                .POST(WEB_HOOK, accept(MediaType.APPLICATION_JSON)
                        .and(contentType(MediaType.APPLICATION_JSON)), handler::paymentCallback)
                .build();
    }
}
