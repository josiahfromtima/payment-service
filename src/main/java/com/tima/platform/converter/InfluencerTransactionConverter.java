package com.tima.platform.converter;

import com.tima.platform.domain.InfluencerTransaction;
import com.tima.platform.model.api.response.InfluencerTransactionRecord;
import com.tima.platform.model.constant.StatusType;

import java.time.Instant;
import java.util.List;
import java.util.Objects;

/**
 * @Author: Josiah Adetayo
 * @Email: josleke@gmail.com, josiah.adetayo@meld-tech.com
 * @Date: 12/28/23
 */
public class InfluencerTransactionConverter {
    private InfluencerTransactionConverter() {}

    public static synchronized InfluencerTransaction mapToEntity(InfluencerTransactionRecord dto) {
        return InfluencerTransaction.builder()
                .brandName(dto.brandName())
                .campaignName(dto.campaignName())
                .publicId(dto.publicId())
                .earning(dto.earning())
                .balance(dto.balance())
                .transactionDate(getOrDefault(dto.transactionDate(), Instant.now()))
                .status(getOrDefault(dto.status(), StatusType.PARTIAL.getType()))
                .build();
    }

    public static synchronized InfluencerTransactionRecord mapToRecord(InfluencerTransaction entity) {
        return  InfluencerTransactionRecord.builder()
                .brandName(entity.getBrandName())
                .campaignName(entity.getCampaignName())
                .publicId(entity.getPublicId())
                .earning(entity.getEarning())
                .balance(entity.getBalance())
                .transactionDate(entity.getTransactionDate())
                .status(entity.getStatus())
                .build();
    }

    public static synchronized List<InfluencerTransactionRecord> mapToRecords(List<InfluencerTransaction> entities) {
        return entities
                .stream()
                .map(InfluencerTransactionConverter::mapToRecord)
                .toList();
    }

    public static synchronized List<InfluencerTransaction> mapToEntities(List<InfluencerTransactionRecord> records) {
        return records
                .stream()
                .map(InfluencerTransactionConverter::mapToEntity)
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
