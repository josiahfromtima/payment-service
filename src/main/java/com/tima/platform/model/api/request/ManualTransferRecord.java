package com.tima.platform.model.api.request;

import com.tima.platform.model.api.response.PaymentHistoryRecord;
import jakarta.validation.constraints.NotNull;

/**
 * @Author: Josiah Adetayo
 * @Email: josleke@gmail.com, josiah.adetayo@meld-tech.com
 * @Date: 1/11/24
 */
public record ManualTransferRecord(@NotNull(message = "Contract Id is required")
                                   String contractId,
                                   @NotNull(message = "Payment Record Id is required")
                                   PaymentHistoryRecord paymentRecord) {}
