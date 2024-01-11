package com.tima.platform.model.api.response.paystack.transfer.event.component;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.gson.annotations.SerializedName;

/**
 * @Author: Josiah Adetayo
 * @Email: josleke@gmail.com, josiah.adetayo@meld-tech.com
 * @Date: 1/10/24
 */
public record Recipient(boolean active,
                        String createdAt,
                        String currency,
                        String description,
                        String email,
                        Integer id,
                        Integer integration,
                        Object metadata,
                        String name,
                        String type,
                        @JsonProperty("recipient_code")
                        @SerializedName("recipient_code")
                        String recipientCode,
                        @JsonProperty("is_deleted")
                        @SerializedName("is_deleted")
                        boolean isDeleted,
                        Details details) {}