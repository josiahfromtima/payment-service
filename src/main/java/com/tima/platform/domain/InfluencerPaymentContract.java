package com.tima.platform.domain;

import com.tima.platform.util.AppUtil;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.domain.Persistable;
import org.springframework.data.relational.core.mapping.Table;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

/**
 * @Author: Josiah Adetayo
 * @Email: josleke@gmail.com, josiah.adetayo@meld-tech.com
 * @Date: 1/3/24
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table("public.influencer_payment_contract")
public class InfluencerPaymentContract implements Serializable, Persistable<Integer> {

    @Id
    private Integer id;
    private String contractId;
    private String influencerPublicId;
    private String campaignPublicId;
    private String brandPublicId;
    private String influencerName;
    private String campaignName;
    private String brandName;
    private String mediaContract;
    private BigDecimal contractAmount;
    private BigDecimal balance;
    private String status;
    private Instant createdOn;

    @Override
    public boolean isNew() {
        boolean newRecord = AppUtil.isNewRecord(id);
        if(newRecord) {
            contractId = UUID.randomUUID().toString();
            createdOn = Instant.now();
        }
        return newRecord;
    }
}
