package com.tima.platform.repository;

import com.tima.platform.domain.Bank;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;

/**
 * @Author: Josiah Adetayo
 * @Email: josleke@gmail.com, josiah.adetayo@meld-tech.com
 * @Date: 12/14/23
 */
public interface BankRepository extends ReactiveCrudRepository<Bank, Integer> {
    Mono<Bank> findByCodeOrName(String code, String name);
}
