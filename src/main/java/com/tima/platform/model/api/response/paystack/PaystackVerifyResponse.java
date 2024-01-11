package com.tima.platform.model.api.response.paystack;

import com.tima.platform.model.api.response.paystack.verification.VerificationData;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

/**
 * @Author: Josiah Adetayo
 * @Email: josleke@gmail.com, josiah.adetayo@meld-tech.com
 * @Date: 1/6/24
 */
public record PaystackVerifyResponse(boolean status, String message, VerificationData data) {}
