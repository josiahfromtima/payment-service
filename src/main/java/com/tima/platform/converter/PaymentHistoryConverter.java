package com.tima.platform.converter;

import com.tima.platform.domain.PaymentHistory;
import com.tima.platform.model.api.response.PaymentHistoryRecord;
import com.tima.platform.model.constant.StatusType;

import java.time.Instant;
import java.util.List;
import java.util.Objects;

/**
 * @Author: Josiah Adetayo
 * @Email: josleke@gmail.com, josiah.adetayo@meld-tech.com
 * @Date: 12/28/23
 */
public class PaymentHistoryConverter {
    private PaymentHistoryConverter() {}

    public static synchronized PaymentHistory mapToEntity(PaymentHistoryRecord dto) {
        return PaymentHistory.builder()
                .name(dto.name())
                .publicId(dto.publicId())
                .initialRequest(dto.initialRequest())
                .paymentResponse(dto.paymentResponse())
                .reference(dto.reference())
                .amount(dto.amount())
                .balance(dto.balance())
                .status(getOrDefault(dto.status(), StatusType.PENDING.getType()))
                .transactionDate(getOrDefault(dto.transactionDate(), Instant.now()))
                .type(getOrDefault(dto.type(), ""))
                .build();
    }

    public static synchronized PaymentHistoryRecord mapToRecord(PaymentHistory entity) {
        return  PaymentHistoryRecord.builder()
                .name(entity.getName())
                .publicId(entity.getPublicId())
                .reference(entity.getReference())
                .amount(entity.getAmount())
                .balance(entity.getBalance())
                .status(entity.getStatus())
                .transactionDate(entity.getTransactionDate())
                .type(entity.getType())
                .build();
    }

    public static synchronized List<PaymentHistoryRecord> mapToRecords(List<PaymentHistory> entities) {
        return entities
                .stream()
                .map(PaymentHistoryConverter::mapToRecord)
                .toList();
    }

    public static synchronized List<PaymentHistory> mapToEntities(List<PaymentHistoryRecord> records) {
        return records
                .stream()
                .map(PaymentHistoryConverter::mapToEntity)
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
