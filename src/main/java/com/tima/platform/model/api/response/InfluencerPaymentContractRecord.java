package com.tima.platform.model.api.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

/**
 * @Author: Josiah Adetayo
 * @Email: josleke@gmail.com, josiah.adetayo@meld-tech.com
 * @Date: 1/3/24
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@Builder
public record InfluencerPaymentContractRecord(@NotNull(message = "Influencer public id is required")
                                              String influencerPublicId,
                                              @NotNull(message = "Campaign public id is required")
                                              String campaignPublicId,
                                              @NotNull(message = "Brand public id is required")
                                              String brandPublicId,
                                              String contractId,
                                              String influencerName,
                                              String campaignName,
                                              String brandName,
                                              List<MediaContract> mediaContract,
                                              @NotNull(message = "Contract Amount is required")
                                              @PositiveOrZero(message = "Negative AMount is not supported")
                                              BigDecimal contractAmount,
                                              BigDecimal balance,
                                              String status,
                                              Instant createdOn) {}