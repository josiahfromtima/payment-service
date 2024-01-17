package com.tima.platform.model.api.response.card;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;

import java.time.Instant;

/**
 * @Author: Josiah Adetayo
 * @Email: josleke@gmail.com, josiah.adetayo@meld-tech.com
 * @Date: 1/12/24
 */
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public record UserSavedCardRecord(String publicId,
                                  String bankName,
                                  String email,
                                  String authCode,
                                  String response,
                                  String type,
                                  String cardNo,
                                  boolean defaultCard,
                                  Instant createdOn) {}