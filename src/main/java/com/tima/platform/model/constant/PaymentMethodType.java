package com.tima.platform.model.constant;

/**
 * @Author: Josiah Adetayo
 * @Email: josleke@gmail.com, josiah.adetayo@meld-tech.com
 * @Date: 12/18/23
 */
public enum PaymentMethodType {
    DEMO("DEMO"),
    LIVE("LIVE"),;

    private final String type;

    PaymentMethodType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }
}
