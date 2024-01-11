package com.tima.platform.model.api.response.paystack.transfer.event.component;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.gson.annotations.SerializedName;

/**
 * @Author: Josiah Adetayo
 * @Email: josleke@gmail.com, josiah.adetayo@meld-tech.com
 * @Date: 1/10/24
 */
public record Integration(Integer id,
                          @JsonProperty("is_live")
                          @SerializedName("is_live")
                          boolean isLive,
                          @JsonProperty("business_name")
                          @SerializedName("business_name")
                          String businessName,
                          @JsonProperty("logo_path")
                          @SerializedName("logo_path")
                          String logoPath) {}