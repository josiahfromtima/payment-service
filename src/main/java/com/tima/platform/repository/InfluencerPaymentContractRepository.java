package com.tima.platform.repository;

import com.tima.platform.domain.InfluencerPaymentContract;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Instant;

import static com.tima.platform.repository.projection.NativeSql.CONTRACT_AGGREGATE_STATEMENT;

/**
 * @Author: Josiah Adetayo
 * @Email: josleke@gmail.com, josiah.adetayo@meld-tech.com
 * @Date: 1/3/24
 */
public interface InfluencerPaymentContractRepository
        extends ReactiveCrudRepository<InfluencerPaymentContract, Integer> {
    Mono<InfluencerPaymentContract> findByInfluencerPublicIdAndCampaignPublicId(String influencerId, String campaignId);
    Mono<InfluencerPaymentContract> findByContractId(String contactId);
    Flux<InfluencerPaymentContract> findByCampaignPublicId(String campaignId);
    Flux<InfluencerPaymentContract> findAllBy(Pageable pageable);
    Flux<InfluencerPaymentContract> findByStatus(String status, Pageable pageable);
    Flux<InfluencerPaymentContract> findByInfluencerPublicId(String publicId);
    Flux<InfluencerPaymentContract> findByInfluencerPublicIdAndCreatedOnBetween(String publicId,
                                                                                Instant start,
                                                                                Instant end);


    @Query(CONTRACT_AGGREGATE_STATEMENT)
    <T> Mono<T> getContractPaymentAggregate(String id, Class<T> type);
}
