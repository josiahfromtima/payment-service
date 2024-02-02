package com.tima.platform.model.constant;

import lombok.Getter;

import java.util.Objects;

/**
 * @Author: Josiah Adetayo
 * @Email: josleke@gmail.com, josiah.adetayo@meld-tech.com
 * @Date: 2/2/24
 */
@Getter
public enum AlertType {
        PAYMENT("PAYMENT"),
        PAID("Payment of %s for %s has been made to %s"),
        RECEIVED("A payment of %s, for %s campaign was received from %s ");

                private final String type;

                AlertType(String type) {
                this.type = type;
                }


        public String getTitle(String name, String param) {
                if(PAID.name().equals(name)) return String.format("%s Payment Sent", param);
                else if(RECEIVED.name().equals(name)) return String.format("%s Payment Received", param);
                else return "";
        }

        public String getMessage(String message, Object... param) {
                if(Objects.isNull(param)) return message;
                return String.format(message, param);
        }

}
