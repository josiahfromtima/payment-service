package com.tima.platform.model.api.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Builder;

import java.math.BigDecimal;

/**
 * @Author: Josiah Adetayo
 * @Email: josleke@gmail.com, josiah.adetayo@meld-tech.com
 * @Date: 1/5/24
 */
@Builder
public record InitiateContractPayment(@NotNull(message = "Contract Id is required")
                                      String contractId,
                                      @NotNull(message = "Amount is required")
                                      @PositiveOrZero(message = "Amount cannot be negative")
                                      BigDecimal amount) {}
