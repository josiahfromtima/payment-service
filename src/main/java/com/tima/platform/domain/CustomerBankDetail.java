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
@Table("public.customer_bank_details")
public class CustomerBankDetail implements Serializable, Persistable<Integer> {

    @Id
    private Integer id;
    private String publicId;
    private String currency;
    private String bankName;
    private String bankAddress;
    private String accountName;
    private String accountNumber;
    private String swiftCode;
    private String subAccountCode;
    private String response;
    private Instant createdOn;
    @Transient
    private String bankCode;

    @Override
    public boolean isNew() {
        boolean newRecord = AppUtil.isNewRecord(id);
        if(newRecord) {
            createdOn = Instant.now();
        }
        return newRecord;
    }
}
