package com.tima.platform.model.api.response.paystack.event;

import com.tima.platform.model.api.response.paystack.verification.VerificationData;

/**
 * @Author: Josiah Adetayo
 * @Email: josleke@gmail.com, josiah.adetayo@meld-tech.com
 * @Date: 1/10/24
 */
public record PaystackEventResponse(String event, VerificationData data) {}
