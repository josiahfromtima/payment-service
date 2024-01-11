package com.tima.platform.model.api.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

/**
 * @Author: Josiah Adetayo
 * @Email: josleke@gmail.com, josiah.adetayo@meld-tech.com
 * @Date: 1/6/24
 */
public record PaymentRequest(@NotBlank(message = "User email is required")
                             @Email(message = "Email is not valid")
                             String email,
                             @NotNull(message = "Amount is required")
                             @Positive(message = "Amount should be more than zero")
                             BigDecimal amount,
                             @NotBlank(message = "Brand Name is required")
                             String name) {
}
