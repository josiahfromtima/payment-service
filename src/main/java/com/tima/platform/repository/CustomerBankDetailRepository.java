package com.tima.platform.repository;

import com.tima.platform.domain.CustomerBankDetail;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * @Author: Josiah Adetayo
 * @Email: josleke@gmail.com, josiah.adetayo@meld-tech.com
 * @Date: 12/14/23
 */
public interface CustomerBankDetailRepository extends ReactiveCrudRepository<CustomerBankDetail, Integer> {
    Mono<CustomerBankDetail> findByPublicId(String publicId);
    Flux<CustomerBankDetail> findByAccountNumber(String accountNo);
}
