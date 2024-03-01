package com.tima.platform.model.api.response.campaign.component;

import lombok.Builder;

import java.math.BigDecimal;
import java.util.List;

/**
 * @Author: Josiah Adetayo
 * @Email: josleke@gmail.com, josiah.adetayo@meld-tech.com
 * @Date: 3/1/24
 */
@Builder
public record OverviewRecord(String name, String briefDescription, String website,
        BigDecimal plannedBudget, BigDecimal costPerPost, List<String> socialMediaPlatforms) {}
