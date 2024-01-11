package com.tima.platform.model.api.response.paystack.transfer;

import com.tima.platform.model.api.response.paystack.transfer.event.PaystackTransferEventResponse;

/**
 * @Author: Josiah Adetayo
 * @Email: josleke@gmail.com, josiah.adetayo@meld-tech.com
 * @Date: 1/10/24
 */
public record PaystackEventTransferResponse(String event, PaystackTransferEventResponse data) {}