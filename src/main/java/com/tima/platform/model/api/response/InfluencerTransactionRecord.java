package com.tima.platform.model.api.response;

import lombok.Builder;

import java.math.BigDecimal;
import java.time.Instant;

/**
 * @Author: Josiah Adetayo
 * @Email: josleke@gmail.com, josiah.adetayo@meld-tech.com
 * @Date: 12/28/23
 */
@Builder
public record InfluencerTransactionRecord(String publicId, String campaignName, String brandName,
                                          BigDecimal earning, BigDecimal balance, String status,
                                          Instant transactionDate, Instant createdOn) {}
