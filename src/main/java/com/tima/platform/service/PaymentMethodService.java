package com.tima.platform.service;

import com.tima.platform.converter.PaymentMethodConverter;
import com.tima.platform.domain.PaymentMethod;
import com.tima.platform.exception.AppException;
import com.tima.platform.model.api.AppResponse;
import com.tima.platform.model.api.response.PaymentMethodRecord;
import com.tima.platform.model.constant.PaymentMethodType;
import com.tima.platform.repository.PaymentMethodRepository;
import com.tima.platform.util.AppError;
import com.tima.platform.util.AppUtil;
import com.tima.platform.util.LoggerHelper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.Arrays;

import static com.tima.platform.exception.ApiErrorHandler.handleOnErrorResume;
import static com.tima.platform.model.security.TimaAuthority.ADMIN;
import static org.springframework.http.HttpStatus.BAD_REQUEST;

/**
 * @Author: Josiah Adetayo
 * @Email: josleke@gmail.com, josiah.adetayo@meld-tech.com
 * @Date: 12/18/23
 */
@Service
@RequiredArgsConstructor
public class PaymentMethodService {
    private final LoggerHelper log = LoggerHelper.newInstance(PaymentMethodService.class.getName());
    private final PaymentMethodRepository methodRepository;

    private static final String PAYMENT_MSG = "Payment request executed successfully";
    private static final String INVALID_PAYMENT = "The payment name/code is invalid";
    private static final String ERROR_MSG = "The Payment method record mutation could not be performed";

    public Mono<AppResponse> getMethodTypes() {
        return Mono.just( AppUtil.buildAppResponse(
                Arrays.stream(PaymentMethodType.values()).toList(), PAYMENT_MSG)
        );
    }


    public Mono<AppResponse> getPaymentMethods() {
        log.info("Getting All Payment Method Record (Public)");
        return methodRepository.findAll()
                .collectList()
                .map(PaymentMethodConverter::mapToSimpleRecords)
                .map(methodRecords -> AppUtil.buildAppResponse(methodRecords, PAYMENT_MSG));
    }
    @PreAuthorize(ADMIN)
    public Mono<AppResponse> getPaymentMethodsByAdmin() {
        log.info("Getting All Payment Method Record...");
        return methodRepository.findAll()
                .collectList()
                .map(PaymentMethodConverter::mapToRecords)
                .map(methodRecords -> AppUtil.buildAppResponse(methodRecords, PAYMENT_MSG));
    }

    @PreAuthorize(ADMIN)
    public Mono<AppResponse> getPaymentRecord(String name, String type) {
        log.info("Getting Payment Method Record by name and type", name, type);
        return validateDetail(name, type)
                .map(PaymentMethodConverter::mapToRecord)
                .map(methodRecords -> AppUtil.buildAppResponse(methodRecords, PAYMENT_MSG));
    }

    @PreAuthorize(ADMIN)
    public Mono<AppResponse> addPaymentMethod(PaymentMethodRecord methodRecord) {
        log.info("Adding new Payment Method Record ");
        return  methodRepository.save(PaymentMethodConverter.mapToEntity(methodRecord))
                .map(PaymentMethodConverter::mapToRecord)
                .map(methodRecords -> AppUtil.buildAppResponse(methodRecords, PAYMENT_MSG))
                .onErrorResume(t ->
                        handleOnErrorResume(new AppException("Wrong type " + methodRecord.type()), BAD_REQUEST.value()));
    }

    @PreAuthorize(ADMIN)
    public Mono<AppResponse> updatePaymentMethod(PaymentMethodRecord methodRecord) {
        log.info("Updating Payment Record Record ", methodRecord.name(), methodRecord.type());
        return validateDetail(methodRecord.name(), methodRecord.type())
                .flatMap(paymentMethod -> {
                    PaymentMethod modifiedRecord = PaymentMethodConverter.mapToEntity(methodRecord);
                    modifiedRecord.setId(paymentMethod.getId());
                    return methodRepository.save(modifiedRecord);
                })
                .map(PaymentMethodConverter::mapToRecord)
                .map(customerRecords -> AppUtil.buildAppResponse(customerRecords, PAYMENT_MSG))
                .onErrorResume(t ->
                        handleOnErrorResume(new AppException(AppError.massage(t.getMessage())), BAD_REQUEST.value()));
    }

    @PreAuthorize(ADMIN)
    public Mono<AppResponse> deletePaymentRecord(String name, String type) {
        log.info("Deleting Payment Method Record ", name, type);
        return validateDetail(name, type)
                .flatMap(methodRepository::delete)
                .then(Mono.fromCallable(() -> AppUtil.buildAppResponse("Customer (" + name + ") Deleted", PAYMENT_MSG)));
    }

    private Mono<PaymentMethod> validateDetail(String name, String type) {
        return methodRepository.findByNameAndType(name, type)
                .switchIfEmpty(handleOnErrorResume(new AppException(INVALID_PAYMENT), BAD_REQUEST.value()));
    }

}
