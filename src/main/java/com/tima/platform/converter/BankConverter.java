package com.tima.platform.converter;

import com.tima.platform.domain.Bank;
import com.tima.platform.model.api.response.BankRecord;

import java.util.List;
import java.util.Objects;

/**
 * @Author: Josiah Adetayo
 * @Email: josleke@gmail.com, josiah.adetayo@meld-tech.com
 * @Date: 12/12/23
 */
public class BankConverter {
    private BankConverter() {}

    public static synchronized Bank mapToEntity(BankRecord dto) {
        return Bank.builder()
                .name(dto.name())
                .currency(dto.currency())
                .code(dto.code())
                .longCode(dto.longCode())
                .slug(dto.slug())
                .country(getOrDefault(dto.country(), "Nigeria"))
                .type(getOrDefault(dto.type(), ""))
                .build();
    }

    public static synchronized BankRecord mapToRecord(Bank entity) {
        return  BankRecord.builder()
                .name(entity.getName())
                .code(entity.getCode())
                .currency(entity.getCurrency())
                .country(entity.getCountry())
                .longCode(entity.getLongCode())
                .slug(entity.getSlug())
                .type(entity.getType())
                .build();
    }

    public static synchronized List<BankRecord> mapToRecords(List<Bank> entities) {
        return entities
                .stream()
                .map(BankConverter::mapToRecord)
                .toList();
    }
    public static synchronized List<Bank> mapToEntities(List<BankRecord> records) {
        return records
                .stream()
                .map(BankConverter::mapToEntity)
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
