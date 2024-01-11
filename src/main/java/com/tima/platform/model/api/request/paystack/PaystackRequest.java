package com.tima.platform.model.api.request.paystack;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.gson.annotations.SerializedName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * @Author: Josiah Adetayo
 * @Email: josleke@gmail.com, josiah.adetayo@meld-tech.com
 * @Date: 1/6/24
 */
@Builder
public record PaystackRequest(
        String reference,
        String email,
        BigDecimal amount,
        @JsonProperty("callback_url")
        @SerializedName("callback_url")
        String callbackUrl,
        String currency) {}
