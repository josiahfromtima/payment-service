package com.tima.platform.model.api.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.Instant;

/**
 * @Author: Josiah Adetayo
 * @Email: josleke@gmail.com, josiah.adetayo@meld-tech.com
 * @Date: 12/28/23
 */
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public record PaymentHistoryRecord(String initialRequest, String paymentResponse, Instant transactionDate,
                                   @NotBlank(message = "External Reference is required")
                                   String reference,
                                   @NotNull(message = "Amount is required")
                                   @PositiveOrZero(message = "Amount cannot be negative")
                                   BigDecimal amount,
                                   BigDecimal balance,
                                   @NotBlank(message = "Payment Status is required")
                                   String status,
                                   String publicId, String type, String name) {}