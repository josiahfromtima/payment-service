package com.tima.platform.model.constant;

/**
 * @Author: Josiah Adetayo
 * @Email: josleke@gmail.com, josiah.adetayo@meld-tech.com
 * @Date: 12/6/23
 */
public enum StatusType {
    PENDING("PENDING"),
    SUCCESS("SUCCESS"),
    FAILED("FAILED"),
    PARTIAL("PARTIAL"),
    COMPLETED("COMPLETED");

    private final String type;

    StatusType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }
}
