package com.tima.platform.model.api.request.analytic;

import lombok.Builder;

import java.math.BigDecimal;

/**
 * @Author: Josiah Adetayo
 * @Email: josleke@gmail.com, josiah.adetayo@meld-tech.com
 * @Date: 3/1/24
 */
@Builder
public record BudgetInsight(String campaignName, BigDecimal campaignBudget, BigDecimal amountSpent) {}
