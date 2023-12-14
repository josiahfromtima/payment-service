package com.tima.platform.domain;

import com.tima.platform.util.AppUtil;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.domain.Persistable;
import org.springframework.data.relational.core.mapping.Table;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;

/**
 * @Author: Josiah Adetayo
 * @Email: josleke@gmail.com, josiah.adetayo@meld-tech.com
 * @Date: 12/14/23
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table("public.influencer_transaction")
public class InfluencerTransaction implements Serializable, Persistable<Integer> {

    @Id
    private Integer id;
    private String publicId;
    private String campaignName;
    private String brandName;
    private BigDecimal earning;
    private BigDecimal balance;
    private String status;
    private Instant transactionDate;
    private Instant createdOn;

    @Override
    public boolean isNew() {
        boolean newRecord = AppUtil.isNewRecord(id);
        if(newRecord) {
            createdOn = Instant.now();
        }
        return newRecord;
    }
}
