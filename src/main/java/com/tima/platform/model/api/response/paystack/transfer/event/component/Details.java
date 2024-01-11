package com.tima.platform.model.api.response.paystack.transfer.event.component;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.gson.annotations.SerializedName;

/**
 * @Author: Josiah Adetayo
 * @Email: josleke@gmail.com, josiah.adetayo@meld-tech.com
 * @Date: 1/10/24
 */
public record Details(@JsonProperty("authorization_code")
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
                      String bankName) {}