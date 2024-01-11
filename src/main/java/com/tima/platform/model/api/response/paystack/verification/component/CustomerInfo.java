package com.tima.platform.model.api.response.paystack.verification.component;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.gson.annotations.SerializedName;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

/**
 * @Author: Josiah Adetayo
 * @Email: josleke@gmail.com, josiah.adetayo@meld-tech.com
 * @Date: 1/6/24
 */
public record CustomerInfo(
        String id,
        String message,
        @JsonProperty("first_name")
        @SerializedName("first_name")
        String firstName,
        @JsonProperty("last_name")
        @SerializedName("last_name")
        String lastName,
        String email,
        @JsonProperty("customer_code")
        @SerializedName("customer_code")
        String customerCode,
        String phone,
        String metadata,
        @JsonProperty("risk_action")
        @SerializedName("risk_action")
        String riskAction,
        @JsonProperty("international_format_phone")
        @SerializedName("international_format_phone")
        String internationalFormatPhone) {}
