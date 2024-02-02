package com.tima.platform.event;

import com.tima.platform.model.api.request.AlertRecord;
import com.tima.platform.util.LoggerHelper;
import lombok.RequiredArgsConstructor;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

/**
 * @Author: Josiah Adetayo
 * @Email: josleke@gmail.com, josiah.adetayo@meld-tech.com
 * @Date: 2/2/24
 */
@Service
@RequiredArgsConstructor
public class AlertEvent {
    LoggerHelper log = LoggerHelper.newInstance(AlertEvent.class.getName());
    private final StreamBridge streamBridge;

    public Mono<Boolean> registerAlert(AlertRecord alertRecord) {
        log.info("send payment alert notification ", alertRecord.message());
        return Mono.just( streamBridge.send("alert-out-0", alertRecord)
        );
    }
}
