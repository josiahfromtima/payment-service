package com.tima.platform.model.api.request.paystack;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.gson.annotations.SerializedName;
import lombok.Builder;

import java.math.BigDecimal;

/**
 * @Author: Josiah Adetayo
 * @Email: josleke@gmail.com, josiah.adetayo@meld-tech.com
 * @Date: 1/12/24
 */
@Builder
public record InitializeCharge(String reference,
                               @JsonProperty("authorization_code")
                               @SerializedName("authorization_code")
                               String authCode,
                               String email,
                               BigDecimal amount) {}