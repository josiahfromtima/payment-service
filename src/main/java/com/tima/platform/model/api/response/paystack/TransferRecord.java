package com.tima.platform.model.api.response.paystack;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.gson.annotations.SerializedName;
import lombok.Builder;

import java.math.BigDecimal;
import java.util.List;

/**
 * @Author: Josiah Adetayo
 * @Email: josleke@gmail.com, josiah.adetayo@meld-tech.com
 * @Date: 1/1/24
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@Builder
public record TransferRecord(
        @JsonProperty("transfersessionid")
        @SerializedName("transfersessionid")
        List<Object> transferSessionId,
        Integer id, String currency, BigDecimal amount, Integer integration, String domain, String reference,
        String source, String reason, String status, Integer request, Integer recipient, Object failures,
        @JsonProperty("source_details")
        @SerializedName("source_details")
        Object sourceDetails,
        @JsonProperty("transfer_code")
        @SerializedName("transfer_code")
        String transferCode,
        @JsonProperty("titan_code")
        @SerializedName("titan_code")
        String titanCode,
        @JsonProperty("transferred_at")
        @SerializedName("transferred_at")
        String transferredAt,
        String createdAt, String updatedAt) {}
