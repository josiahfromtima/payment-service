package com.tima.platform.converter;

import com.tima.platform.domain.PaymentMethod;
import com.tima.platform.model.api.response.PaymentMethodRecord;
import com.tima.platform.model.constant.PaymentMethodType;

import java.util.List;
import java.util.Objects;

/**
 * @Author: Josiah Adetayo
 * @Email: josleke@gmail.com, josiah.adetayo@meld-tech.com
 * @Date: 12/18/23
 */
public class PaymentMethodConverter {
    private PaymentMethodConverter() {}

    public static synchronized PaymentMethod mapToEntity(PaymentMethodRecord dto) {
        return PaymentMethod.builder()
                .name(dto.name())
                .apiKey(dto.apiKey())
                .initiatePaymentUrl(dto.initiatePaymentUrl())
                .verifyPaymentUrl(dto.verifyPaymentUrl())
                .type(getOrDefault(dto.type(), PaymentMethodType.DEMO.getType()))
                .build();
    }

    public static synchronized PaymentMethodRecord mapToRecord(PaymentMethod entity) {
        return  PaymentMethodRecord.builder()
                .name(entity.getName())
                .apiKey(entity.getApiKey())
                .initiatePaymentUrl(entity.getInitiatePaymentUrl())
                .verifyPaymentUrl(entity.getVerifyPaymentUrl())
                .createdOn(entity.getCreatedOn())
                .type(entity.getType())
                .build();
    }
    public static synchronized PaymentMethodRecord mapToSimpleRecord(PaymentMethod entity) {
        return  PaymentMethodRecord.builder()
                .name(entity.getName())
                .createdOn(entity.getCreatedOn())
                .type(entity.getType())
                .build();
    }

    public static synchronized List<PaymentMethodRecord> mapToSimpleRecords(List<PaymentMethod> entities) {
        return entities
                .stream()
                .map(PaymentMethodConverter::mapToSimpleRecord)
                .toList();
    }

    public static synchronized List<PaymentMethodRecord> mapToRecords(List<PaymentMethod> entities) {
        return entities
                .stream()
                .map(PaymentMethodConverter::mapToRecord)
                .toList();
    }

    public static synchronized List<PaymentMethod> mapToEntities(List<PaymentMethodRecord> records) {
        return records
                .stream()
                .map(PaymentMethodConverter::mapToEntity)
                .toList();
    }

    @SuppressWarnings("unchecked")
    private static <T> T getOrDefault(Object value, T t){
        if( Objects.isNull(value) ) {
            return t;
        }
        return (T) value;
    }
}
