package com.tima.platform.model.api.request.paystack;

import lombok.Builder;

import java.math.BigDecimal;

/**
 * @Author: Josiah Adetayo
 * @Email: josleke@gmail.com, josiah.adetayo@meld-tech.com
 * @Date: 1/2/24
 */
@Builder
public record InitiateTransfer(String source, BigDecimal amount, String reference,
                               String recipient, String reason) {}
