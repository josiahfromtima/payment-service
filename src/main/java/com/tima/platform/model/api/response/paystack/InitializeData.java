package com.tima.platform.model.api.response.paystack;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.gson.annotations.SerializedName;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

/**
 * @Author: Josiah Adetayo
 * @Email: josleke@gmail.com, josiah.adetayo@meld-tech.com
 * @Date: 1/6/24
 */
public record InitializeData(
        @JsonProperty("authorization_url")
        @SerializedName("authorization_url")
        String authorizationUrl,
        @JsonProperty("access_code")
        @SerializedName("access_code")
        String accessCode,
        String reference) {}