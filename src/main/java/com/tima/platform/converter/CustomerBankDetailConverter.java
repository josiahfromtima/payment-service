package com.tima.platform.converter;

import com.tima.platform.domain.CustomerBankDetail;
import com.tima.platform.model.api.response.CustomerBankDetailRecord;

import java.util.List;
import java.util.Objects;

/**
 * @Author: Josiah Adetayo
 * @Email: josleke@gmail.com, josiah.adetayo@meld-tech.com
 * @Date: 12/12/23
 */
public class CustomerBankDetailConverter {
    private CustomerBankDetailConverter() {}

    public static synchronized CustomerBankDetail mapToEntity(CustomerBankDetailRecord dto) {
        return CustomerBankDetail.builder()
                .publicId(getOrDefault(dto.publicId(), ""))
                .bankName(dto.bankName())
                .bankAddress(dto.bankAddress())
                .accountName(dto.accountName())
                .accountNumber(dto.accountNumber())
                .bankCode(getOrDefault(dto.bankCode(), ""))
                .currency(getOrDefault(dto.currency(), "NGN"))
                .swiftCode(getOrDefault(dto.swiftCode(), ""))
                .build();
    }

    public static synchronized CustomerBankDetailRecord mapToRecord(CustomerBankDetail entity) {
        return  CustomerBankDetailRecord.builder()
                .publicId(entity.getPublicId())
                .bankName(entity.getBankName())
                .bankAddress(entity.getBankAddress())
                .accountName(entity.getAccountName())
                .accountNumber(entity.getAccountNumber())
                .currency(entity.getCurrency())
                .swiftCode(entity.getSwiftCode())
                .createdOn(entity.getCreatedOn())
                .build();
    }

    public static synchronized List<CustomerBankDetailRecord> mapToRecords(List<CustomerBankDetail> entities) {
        return entities
                .stream()
                .map(CustomerBankDetailConverter::mapToRecord)
                .toList();
    }
    public static synchronized List<CustomerBankDetail> mapToEntities(List<CustomerBankDetailRecord> records) {
        return records
                .stream()
                .map(CustomerBankDetailConverter::mapToEntity)
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
