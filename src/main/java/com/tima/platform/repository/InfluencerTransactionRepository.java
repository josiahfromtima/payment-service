package com.tima.platform.repository;

import com.tima.platform.domain.InfluencerTransaction;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;

import java.time.Instant;

/**
 * @Author: Josiah Adetayo
 * @Email: josleke@gmail.com, josiah.adetayo@meld-tech.com
 * @Date: 12/28/23
 */
public interface InfluencerTransactionRepository extends ReactiveCrudRepository<InfluencerTransaction, Integer> {
    Flux<InfluencerTransaction> findByPublicId(String id, Pageable pageable);
    Flux<InfluencerTransaction> findByBrandNameAndPublicId(String name, String id, Pageable pageable);
    Flux<InfluencerTransaction> findByPublicIdAndCreatedOnBefore(String id, Instant endDate);
    Flux<InfluencerTransaction> findByStatusAndPublicId(String status, String id, Pageable pageable);
    Flux<InfluencerTransaction> findByCampaignNameContainingIgnoreCaseOrBrandNameContainingIgnoreCaseAndStatus(
            String campaignName, String brandName, String status, Pageable pageable);
}
