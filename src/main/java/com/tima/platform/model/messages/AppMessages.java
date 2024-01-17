package com.tima.platform.model.messages;

import lombok.Getter;

/**
 * @Author: Josiah Adetayo
 * @Email: josleke@gmail.com, josiah.adetayo@meld-tech.com
 * @Date: 1/12/24
 */
public interface AppMessages {
    @Getter
    enum CARD {
        CARD_SUCCESS("Card Details processed successfully"),
        CARD_REMOVED("Card Remove Successfully"),
        CARD_NOT_FOUND("Card does not Exists"),
        CARD_EXISTS("Card is Already Saved"),
        NO_CARD_MSG("No Card Found for User");

        private final String info;

        CARD(String info) {
            this.info = info;
        }

    }
}
