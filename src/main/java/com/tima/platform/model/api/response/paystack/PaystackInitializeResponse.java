package com.tima.platform.model.api.response.paystack;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

/**
 * @Author: Josiah Adetayo
 * @Email: josleke@gmail.com, josiah.adetayo@meld-tech.com
 * @Date: 1/6/24
 */
public record PaystackInitializeResponse(boolean status, String message, InitializeData data) {}