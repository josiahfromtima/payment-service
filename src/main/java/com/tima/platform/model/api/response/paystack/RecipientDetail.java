package com.tima.platform.model.api.response.paystack;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.gson.annotations.SerializedName;
import lombok.Builder;

/**
 * @Author: Josiah Adetayo
 * @Email: josleke@gmail.com, josiah.adetayo@meld-tech.com
 * @Date: 1/2/24
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@Builder
public record RecipientDetail(
        @JsonProperty("authorization_code")
        @SerializedName("authorization_code")
        String authorizationCode,
        @JsonProperty("account_number")
        @SerializedName("account_number")
        String accountNumber,
        @JsonProperty("account_name")
        @SerializedName("account_name")
        String accountName,
        @JsonProperty("bank_code")
        @SerializedName("bank_code")
        String bankCode,
        @JsonProperty("bank_name")
        @SerializedName("bank_name")
        String bankName

) {}