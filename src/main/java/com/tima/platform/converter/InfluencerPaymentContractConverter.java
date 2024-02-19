package com.tima.platform.converter;

import com.google.gson.reflect.TypeToken;
import com.tima.platform.domain.InfluencerPaymentContract;
import com.tima.platform.model.api.response.InfluencerPaymentContractRecord;
import com.tima.platform.model.api.response.MediaContract;
import com.tima.platform.model.constant.StatusType;
import com.tima.platform.util.AppUtil;

import java.util.List;
import java.util.Objects;

import static com.tima.platform.util.AppUtil.gsonInstance;

/**
 * @Author: Josiah Adetayo
 * @Email: josleke@gmail.com, josiah.adetayo@meld-tech.com
 * @Date: 1/3/24
 */
public class InfluencerPaymentContractConverter {
    private InfluencerPaymentContractConverter() {}

    public static synchronized InfluencerPaymentContract mapToEntity(InfluencerPaymentContractRecord dto) {
        return InfluencerPaymentContract.builder()
                .influencerPublicId(dto.influencerPublicId())
                .brandPublicId(dto.brandPublicId())
                .campaignPublicId(dto.campaignPublicId())
                .influencerName(getOrDefault(dto.influencerName(), ""))
                .campaignName(getOrDefault(dto.campaignName(), ""))
                .brandName(getOrDefault(dto.brandName(), ""))
                .mediaContract(getOrDefault(convertTo(dto.mediaContract()), "[]"))
                .contractAmount(dto.contractAmount())
                .balance(dto.contractAmount())
                .status(getOrDefault(dto.status(), StatusType.PENDING.getType()))
                .build();
    }

    public static synchronized InfluencerPaymentContractRecord mapToRecord(InfluencerPaymentContract entity) {
        return  InfluencerPaymentContractRecord.builder()
                .contractId(entity.getContractId())
                .influencerPublicId(entity.getInfluencerPublicId())
                .brandPublicId(entity.getBrandPublicId())
                .campaignPublicId(entity.getCampaignPublicId())
                .influencerName(entity.getInfluencerName())
                .campaignName(entity.getCampaignName())
                .brandName(entity.getBrandName())
                .mediaContract(convertFrom(entity.getMediaContract()))
                .contractAmount(entity.getContractAmount())
                .balance(entity.getBalance())
                .status(entity.getStatus())
                .createdOn(entity.getCreatedOn())
                .build();
    }

    public static synchronized List<InfluencerPaymentContractRecord> mapToRecords(List<InfluencerPaymentContract> entities) {
        return entities
                .stream()
                .map(InfluencerPaymentContractConverter::mapToRecord)
                .toList();
    }
    public static synchronized List<InfluencerPaymentContract> mapToEntities(List<InfluencerPaymentContractRecord> records) {
        return records
                .stream()
                .map(InfluencerPaymentContractConverter::mapToEntity)
                .toList();
    }

    @SuppressWarnings("unchecked")
    private static <T> T getOrDefault(Object value, T t){
        if( Objects.isNull(value) ) {
            return t;
        }
        return (T) value;
    }

    private static String convertTo(List<MediaContract> value) {
        return gsonInstance().toJson(value);
    }
    private static List<MediaContract> convertFrom(String value) {
        return gsonInstance().fromJson(value, new TypeToken<List<MediaContract>>(){}.getType());
    }
}
