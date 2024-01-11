package com.tima.platform.model.api.response.paystack.transfer.event;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.gson.annotations.SerializedName;
import com.tima.platform.model.api.response.paystack.transfer.event.component.Integration;
import com.tima.platform.model.api.response.paystack.transfer.event.component.Recipient;
import com.tima.platform.model.api.response.paystack.transfer.event.component.Session;

import java.math.BigDecimal;

/**
 * @Author: Josiah Adetayo
 * @Email: josleke@gmail.com, josiah.adetayo@meld-tech.com
 * @Date: 1/10/24
 */
public record PaystackTransferEventResponse(BigDecimal amount,
                                            String createdAt,
                                            String currency,
                                            Object failures,
                                            Integer id,
                                            String reason,
                                            String reference,
                                            String source,
                                            String status,
                                            @JsonProperty("titan_code")
                                            @SerializedName("titan_code")
                                            String titanCode,
                                            @JsonProperty("source_details")
                                            @SerializedName("source_details")
                                            String sourceDetails,
                                            @JsonProperty("transfer_code")
                                            @SerializedName("transfer_code")
                                            String transferCode,
                                            @JsonProperty("transferred_at")
                                            @SerializedName("transferred_at")
                                            String transferredAt,
                                            String updatedAt,
                                            @JsonProperty("fee_charged")
                                            @SerializedName("fee_charged")
                                            BigDecimal feeCharged,
                                            Integration integration,
                                            Recipient recipient,
                                            Session session) {}