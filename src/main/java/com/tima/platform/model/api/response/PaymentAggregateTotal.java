package com.tima.platform.model.api.response;

import java.math.BigDecimal;

/**
 * @Author: Josiah Adetayo
 * @Email: josleke@gmail.com, josiah.adetayo@meld-tech.com
 * @Date: 12/31/23
 */
public record PaymentAggregateTotal(long payee, BigDecimal paid, BigDecimal outstanding) {}
