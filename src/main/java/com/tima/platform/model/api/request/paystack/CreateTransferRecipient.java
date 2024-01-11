package com.tima.platform.model.api.request.paystack;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.gson.annotations.SerializedName;
import lombok.Builder;

/**
 * @Author: Josiah Adetayo
 * @Email: josleke@gmail.com, josiah.adetayo@meld-tech.com
 * @Date: 1/1/24
 */
@Builder
public record CreateTransferRecipient(String type, String name, String currency,
                                      @JsonProperty("account_number")
                                      @SerializedName("account_number")
                                      String accountNumber,
                                      @JsonProperty("bank_code")
                                      @SerializedName("bank_code")
                                      String bankCode) {}
