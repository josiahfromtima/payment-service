package com.tima.platform.resource.payment.method;

import com.tima.platform.model.api.ApiResponse;
import com.tima.platform.model.api.response.PaymentMethodRecord;
import com.tima.platform.service.PaymentHistoryService;
import com.tima.platform.service.PaymentMethodService;
import com.tima.platform.util.LoggerHelper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import static com.tima.platform.model.api.ApiResponse.buildServerResponse;

/**
 * @Author: Josiah Adetayo
 * @Email: josleke@gmail.com, josiah.adetayo@meld-tech.com
 * @Date: 12/18/23
 */
@Service
@RequiredArgsConstructor
public class PaymentMethodResourceHandler {
    LoggerHelper log = LoggerHelper.newInstance(PaymentMethodResourceHandler.class.getName());
    private final PaymentMethodService paymentMethodService;
    private final PaymentHistoryService historyService;

    /**
     *  This section marks the payment method activities
     */
    public Mono<ServerResponse> getMethodTypes(ServerRequest request)  {
        log.info("Get Payment Method Type Requested", request.remoteAddress().orElse(null));
        return buildServerResponse(paymentMethodService.getMethodTypes());
    }

    public Mono<ServerResponse> getPaymentMethodsPublic(ServerRequest request)  {
        log.info("Get Payment Methods Requested (Public)", request.remoteAddress().orElse(null));
        return buildServerResponse(paymentMethodService.getPaymentMethods());
    }

    public Mono<ServerResponse> getPaymentMethodsAdmin(ServerRequest request)  {
        String name = request.queryParam("name").orElse("");
        String type = request.queryParam("type").orElse("");
        if(!name.isEmpty() && !type.isEmpty()) {
            log.info("Get Payment Method Requested", request.remoteAddress().orElse(null));
            return buildServerResponse(paymentMethodService.getPaymentRecord(name, type));
        }else {
            log.info("Get Payment Methods Requested (Admin)", request.remoteAddress().orElse(null));
            return buildServerResponse(paymentMethodService.getPaymentMethodsByAdmin());
        }
    }

    public Mono<ServerResponse> addNewPaymentMethod(ServerRequest request)  {
        Mono<PaymentMethodRecord> recordMono = request.bodyToMono(PaymentMethodRecord.class);
        log.info("Add New Payment Method Requested", request.remoteAddress().orElse(null));
        return recordMono
                .map(paymentMethodService::addPaymentMethod)
                .flatMap(ApiResponse::buildServerResponse);
    }
    public Mono<ServerResponse>  editPaymentMethod(ServerRequest request)  {
        Mono<PaymentMethodRecord> recordMono = request.bodyToMono(PaymentMethodRecord.class);
        log.info("Update Payment Method Requested", request.remoteAddress().orElse(null));
        return recordMono
                .map(paymentMethodService::updatePaymentMethod)
                .flatMap(ApiResponse::buildServerResponse);
    }
    public Mono<ServerResponse> deleteCPaymentMethod(ServerRequest request)  {
        String name = request.queryParam("name").orElse("");
        String type = request.queryParam("type").orElse("");
        log.info("Delete Customer Bank Detail Requested", request.remoteAddress().orElse(null));
        return buildServerResponse(paymentMethodService.deletePaymentRecord(name, type));
    }

}
