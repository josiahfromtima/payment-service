package com.tima.platform.util;

import com.tima.platform.model.api.response.PaymentHistoryRecord;

import java.util.Objects;

/**
 * @Author: Josiah Adetayo
 * @Email: josleke@gmail.com, josiah.adetayo@meld-tech.com
 * @Date: 12/28/23
 */
public class PaymentHistoryRecordUpdater {

    private PaymentHistoryRecordUpdater(){}

    public static PaymentHistoryRecord updateRecord(PaymentHistoryRecord newRecord, PaymentHistoryRecord oldRecord) {
        return PaymentHistoryRecord.builder()
                .name(Objects.isNull(newRecord.name()) ? oldRecord.name() : newRecord.name())
                .amount(Objects.isNull(newRecord.amount()) ? oldRecord.amount() : newRecord.amount())
                .balance(Objects.isNull(newRecord.balance()) ? oldRecord.balance() : newRecord.balance())
                .initialRequest(Objects.isNull(newRecord.initialRequest()) ? oldRecord.initialRequest()
                        : newRecord.initialRequest())
                .paymentResponse(Objects.isNull(newRecord.paymentResponse()) ? oldRecord.paymentResponse()
                        : newRecord.paymentResponse())
                .publicId(Objects.isNull(newRecord.publicId()) ? oldRecord.publicId() : newRecord.publicId())
                .reference(Objects.isNull(newRecord.reference()) ? oldRecord.reference() : newRecord.reference())
                .status(Objects.isNull(newRecord.status()) ? oldRecord.status() : newRecord.status())
                .transactionDate(Objects.isNull(newRecord.transactionDate()) ? oldRecord.transactionDate()
                        : newRecord.transactionDate())
                .type(Objects.isNull(newRecord.type()) ? oldRecord.type() : newRecord.type())
                .build();
    }
}
