package com.tima.platform.converter;

import com.tima.platform.domain.UserSavedCard;
import com.tima.platform.model.api.response.card.UserSavedCardRecord;

import java.util.List;
import java.util.Objects;

/**
 * @Author: Josiah Adetayo
 * @Email: josleke@gmail.com, josiah.adetayo@meld-tech.com
 * @Date: 1/12/24
 */
public class UserSavedCardConverter {
    private UserSavedCardConverter() {}

    public static synchronized UserSavedCard mapToEntity(UserSavedCardRecord dto) {
        return UserSavedCard.builder()
                .publicId(dto.publicId())
                .email(dto.email())
                .bankName(dto.bankName())
                .authCode(dto.authCode())
                .response(dto.response())
                .cardNo(getOrDefault(dto.cardNo(), "000-000000"))
                .type(getOrDefault(dto.type(), "NA"))
                .defaultCard(dto.defaultCard())
                .build();
    }

    public static synchronized UserSavedCardRecord mapToRecord(UserSavedCard entity) {
        return  UserSavedCardRecord.builder()
                .publicId(entity.getPublicId())
                .email(entity.getEmail())
                .bankName(entity.getBankName())
                .authCode(entity.getAuthCode())
                .cardNo(entity.getCardNo())
                .type(entity.getType())
                .defaultCard(entity.isDefaultCard())
                .createdOn(entity.getCreatedOn())
                .build();
    }

    public static synchronized List<UserSavedCardRecord> mapToRecords(List<UserSavedCard> entities) {
        return entities
                .stream()
                .map(UserSavedCardConverter::mapToRecord)
                .toList();
    }
    public static synchronized List<UserSavedCard> mapToEntities(List<UserSavedCardRecord> records) {
        return records
                .stream()
                .map(UserSavedCardConverter::mapToEntity)
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
