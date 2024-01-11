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
public record Authorization(
        @JsonProperty("authorization_code")
        @SerializedName("authorization_code")
        String authorizationCode,
        String bin,
        String last4,
        @JsonProperty("exp_month")
        @SerializedName("exp_month")
        String expMonth,
        @JsonProperty("exp_year")
        @SerializedName("exp_year")
        String expYear,
        String channel,
        @JsonProperty("card_type")
        @SerializedName("card_type")
        String cardType,
        String bank,
        @JsonProperty("country_code")
        @SerializedName("country_code")
        String countryCode,
        String brand,
        boolean reusable,
        String signature,
        @JsonProperty("account_name")
        @SerializedName("account_name")
        String accountName) {}