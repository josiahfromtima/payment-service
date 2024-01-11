package com.tima.platform.model.api.response.paystack.verification;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.gson.annotations.SerializedName;
import com.tima.platform.model.api.response.paystack.verification.component.Authorization;
import com.tima.platform.model.api.response.paystack.verification.component.CustomerInfo;
import com.tima.platform.model.api.response.paystack.verification.component.FeesBreakdown;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

/**
 * @Author: Josiah Adetayo
 * @Email: josleke@gmail.com, josiah.adetayo@meld-tech.com
 * @Date: 1/6/24
 */
public record VerificationData(
        long id,
        String domain,
        String status,
        String reference,
        BigDecimal amount,
        String message,
        @JsonProperty("gateway_response")
        @SerializedName("gateway_response")
        String gatewayResponse,
        String channel,
        String currency,
        @JsonProperty("ip_address")
        @SerializedName("ip_address")
        String ipAddress,
        String metadata,
        String plan,
        Object split,
        @JsonProperty("order_id")
        @SerializedName("order_id")
        String orderId,
        String paidAt,
        String createdAt,
        @JsonProperty("requested_amount")
        @SerializedName("requested_amount")
        BigDecimal requestedAmount,
        @JsonProperty("pos_transaction_data")
        @SerializedName("pos_transaction_data")
        String posTransactionData,
        String source,
        @JsonProperty("fees_breakdown")
        @SerializedName("fees_breakdown")
        List<FeesBreakdown> feesBreakdown,
        @JsonProperty("transaction_date")
        @SerializedName("transaction_date")
        String transactionDate,
        @JsonProperty("plan_object")
        @SerializedName("plan_object")
        Object planObject,
        @JsonProperty("subaccount")
        @SerializedName("subaccount")
        Object subAccount,
        Authorization authorization,
        CustomerInfo customer
) {}
