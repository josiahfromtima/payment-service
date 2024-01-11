package com.tima.platform.resource.callback;

import com.tima.platform.config.AuthTokenConfig;
import com.tima.platform.config.CustomValidator;
import com.tima.platform.model.api.ApiResponse;
import com.tima.platform.model.api.request.InitiateContractPayment;
import com.tima.platform.model.api.request.PaymentRequest;
import com.tima.platform.service.PaymentCallbackService;
import com.tima.platform.service.TransactionService;
import com.tima.platform.util.LoggerHelper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import static com.tima.platform.model.api.ApiResponse.buildServerResponse;

/**
 * @Author: Josiah Adetayo
 * @Email: josleke@gmail.com, josiah.adetayo@meld-tech.com
 * @Date: 1/3/24
 */
@Service
@RequiredArgsConstructor
public class CallbackResourceHandler {
    LoggerHelper log = LoggerHelper.newInstance(CallbackResourceHandler.class.getName());
    private final PaymentCallbackService callbackService;

    /**
     *  This section marks the Callback activities
     */

    public Mono<ServerResponse> paymentCallback(ServerRequest request)  {
        Mono<String> recordMono = request.bodyToMono(String.class);
        log.info("Payment Callback Received", request.remoteAddress().orElse(null));
        return recordMono
                .map(callbackService::updatePayment)
                .flatMap(ApiResponse::buildServerResponse);
    }
}
