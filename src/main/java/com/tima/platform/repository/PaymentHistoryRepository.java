package com.tima.platform.repository;

import com.tima.platform.domain.PaymentHistory;
import com.tima.platform.model.constant.StatusType;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.time.LocalDate;

import static com.tima.platform.repository.projection.NativeSql.AGGREGATE_STATEMENT;

/**
 * @Author: Josiah Adetayo
 * @Email: josleke@gmail.com, josiah.adetayo@meld-tech.com
 * @Date: 12/28/23
 */
public interface PaymentHistoryRepository extends ReactiveCrudRepository<PaymentHistory, Integer> {
    Flux<PaymentHistory> findAllBy(Pageable pageable);
    Mono<PaymentHistory> findByReference(String reference);
    Mono<PaymentHistory> findByReferenceAndStatus(String reference, String status);
    Flux<PaymentHistory> findByStatus(String status, Pageable pageable);
    @Query(AGGREGATE_STATEMENT)
    <T> Mono<T> getPaymentAggregate(Class<T> type);
}
