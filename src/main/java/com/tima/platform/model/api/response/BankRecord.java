package com.tima.platform.model.api.response;

import lombok.Builder;

/**
 * @Author: Josiah Adetayo
 * @Email: josleke@gmail.com, josiah.adetayo@meld-tech.com
 * @Date: 12/12/23
 */
@Builder
public record BankRecord(String name, String slug, String code,
                         String longCode, String country, String currency, String type) {}