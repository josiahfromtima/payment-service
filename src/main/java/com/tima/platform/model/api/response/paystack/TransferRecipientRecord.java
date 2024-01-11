package com.tima.platform.model.api.response.paystack;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.gson.annotations.SerializedName;

/**
 * @Author: Josiah Adetayo
 * @Email: josleke@gmail.com, josiah.adetayo@meld-tech.com
 * @Date: 1/2/24
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record TransferRecipientRecord(boolean active, String createdAt, String currency,
                                      String domain, Integer id, Integer integration, String name,
                                      String type, String updatedAt, boolean isDeleted, RecipientDetail details,
                                      @JsonProperty("recipient_code")
                                      @SerializedName("recipient_code")
                                      String recipientCode ) {}
