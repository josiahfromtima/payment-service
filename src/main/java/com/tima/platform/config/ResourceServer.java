package com.tima.platform.config;

import com.tima.platform.config.helper.CombinedClaimConverter;
import com.tima.platform.exception.AppException;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
/**
 * @Author: Josiah Adetayo
 * @Email: josleke@gmail.com, josiah.adetayo@meld-tech.com
 * @Date: 12/6/23
 */
@Configuration
@EnableWebFluxSecurity
@EnableReactiveMethodSecurity
@RequiredArgsConstructor
public class ResourceServer {
    private final CombinedClaimConverter converter;
    @Bean
    public SecurityWebFilterChain springWebFilterChain(ServerHttpSecurity http) throws AppException {
        http
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .authorizeExchange(authorizeExchangeSpec ->
                        authorizeExchangeSpec
                                .pathMatchers(AUTH_WHITELIST)
                                .permitAll()
                                .anyExchange()
                                .authenticated()
                )
                .oauth2ResourceServer(oAuth2 -> oAuth2.jwt(jwtSpec ->
                        jwtSpec.jwtAuthenticationConverter(converter)));
        return http.build();
    }

    private static final String[] AUTH_WHITELIST = {
            "/v1/banks",
            "/v1/banks/**",
            "/v1/bank/customers",
            "/v1/methods/types",
            "/v1/methods/_public",
            "/v1/payment/webhook",
            // other public endpoints of your API may be appended to this array
    };
}
