package com.tima.platform.model.api.response;

import lombok.Builder;

import java.math.BigDecimal;

/**
 * @Author: Josiah Adetayo
 * @Email: josleke@gmail.com, josiah.adetayo@meld-tech.com
 * @Date: 12/31/23
 */
@Builder
public record BrandAggregatePaymentRecord(BigDecimal totalBudget,
                                          Long totalClientPaid,
                                          BigDecimal totalAmountPaid,
                                          BigDecimal totalBalance) {}
