package com.tima.platform.repository;

import com.tima.platform.domain.PaymentMethod;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;

/**
 * @Author: Josiah Adetayo
 * @Email: josleke@gmail.com, josiah.adetayo@meld-tech.com
 * @Date: 12/18/23
 */
public interface PaymentMethodRepository  extends ReactiveCrudRepository<PaymentMethod, Integer> {
        Mono<PaymentMethod> findByNameAndType(String name, String type);
}
