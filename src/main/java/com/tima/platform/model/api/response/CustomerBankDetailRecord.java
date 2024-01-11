package com.tima.platform.model.api.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;

import java.time.Instant;

/**
 * @Author: Josiah Adetayo
 * @Email: josleke@gmail.com, josiah.adetayo@meld-tech.com
 * @Date: 12/12/23
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@Builder
public record CustomerBankDetailRecord(String publicId, String bankName, String bankAddress,
                                       String currency, String accountName, String accountNumber,
                                       String bankCode, String swiftCode, Instant createdOn) {}