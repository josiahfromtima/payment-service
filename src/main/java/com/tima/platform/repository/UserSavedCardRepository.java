package com.tima.platform.repository;

import com.tima.platform.domain.Bank;
import com.tima.platform.domain.UserSavedCard;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * @Author: Josiah Adetayo
 * @Email: josleke@gmail.com, josiah.adetayo@meld-tech.com
 * @Date: 1/12/24
 */
public interface UserSavedCardRepository extends ReactiveCrudRepository<UserSavedCard, Integer> {
    Flux<UserSavedCard> findByPublicId(String id);
    Mono<UserSavedCard> findByPublicIdAndDefaultCard(String id, boolean isDefault);
    Mono<UserSavedCard> findByPublicIdAndCardNoAndType(String id, String cardNo, String cardType);
}
