package com.tima.platform.model.api.response.paystack.verification.component;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

/**
 * @Author: Josiah Adetayo
 * @Email: josleke@gmail.com, josiah.adetayo@meld-tech.com
 * @Date: 1/6/24
 */
public record FeesBreakdown(Long amount, String formula, String type) {}
