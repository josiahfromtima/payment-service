package com.tima.platform.model.api.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;

import java.time.Instant;

/**
 * @Author: Josiah Adetayo
 * @Email: josleke@gmail.com, josiah.adetayo@meld-tech.com
 * @Date: 12/18/23
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@Builder
public record PaymentMethodRecord(String name, String apiKey, String initiatePaymentUrl,
                                  String verifyPaymentUrl, String type, Instant createdOn) {}
