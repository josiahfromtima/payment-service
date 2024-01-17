package com.tima.platform.service.card;

import com.tima.platform.converter.PaymentHistoryConverter;
import com.tima.platform.converter.UserSavedCardConverter;
import com.tima.platform.domain.PaymentHistory;
import com.tima.platform.domain.UserSavedCard;
import com.tima.platform.exception.AppException;
import com.tima.platform.model.api.AppResponse;
import com.tima.platform.model.api.response.card.UserSavedCardRecord;
import com.tima.platform.model.api.response.paystack.verification.VerificationData;
import com.tima.platform.repository.UserSavedCardRepository;
import com.tima.platform.util.AppUtil;
import com.tima.platform.util.LoggerHelper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import static com.tima.platform.exception.ApiErrorHandler.handleOnErrorResume;
import static com.tima.platform.model.constant.StatusType.SUCCESS;
import static com.tima.platform.model.messages.AppMessages.CARD.*;
import static org.springframework.http.HttpStatus.BAD_REQUEST;

/**
 * @Author: Josiah Adetayo
 * @Email: josleke@gmail.com, josiah.adetayo@meld-tech.com
 * @Date: 1/12/24
 */
@Service
@RequiredArgsConstructor
public class UserSavedCardService {
    private final LoggerHelper log = LoggerHelper.newInstance(UserSavedCardService.class.getName());
    private final UserSavedCardRepository cardRepository;
    private static final String INVALID_CARD_MSG = "Payment not completed. Card cannot be stored";
    public Mono<AppResponse> storeCard(PaymentHistory payment) {
        if(!payment.getStatus().equalsIgnoreCase(SUCCESS.name()))
            return handleOnErrorResume( new AppException(INVALID_CARD_MSG), BAD_REQUEST.value());
        else {
            VerificationData data = getAuthorization(payment.getPaymentResponse());
            return checkExistence(payment.getPublicId(),
                    data.authorization().last4(),
                    data.authorization().cardType())
                    .doOnNext(aBoolean -> log.info("Found Saved Card ", aBoolean))
                    .flatMap(aBoolean ->  (!aBoolean) ? cardRepository.save(UserSavedCard.builder()
                            .authCode(data.authorization().authorizationCode())
                            .bankName(data.authorization().bank())
                            .email(data.customer().email())
                            .type(data.authorization().cardType())
                            .cardNo(data.authorization().last4())
                            .publicId(payment.getPublicId())
                            .response(AppUtil.gsonInstance().toJson(data))
                            .build()) : getEmptyCardInfo(payment))
                    .map(userSavedCard -> PaymentHistoryConverter.mapToRecord(payment))
                    .map(cardDto ->
                            AppUtil.buildAppResponse( cardDto, CARD_SUCCESS.getInfo()));
        }
    }

    public Mono<AppResponse> getCards(String userId) {
        return cardRepository.findByPublicId(userId)
                .collectList()
                .map(UserSavedCardConverter::mapToRecords)
                .map(cards -> AppUtil.buildAppResponse(cards, CARD_SUCCESS.getInfo()))
                .switchIfEmpty( handleOnErrorResume(
                        new AppException(NO_CARD_MSG.getInfo()), BAD_REQUEST.value()) );
    }

    public Mono<UserSavedCardRecord> getCard(String userId) {
        return cardRepository.findByPublicIdAndDefaultCard(userId, true)
                .map(UserSavedCardConverter::mapToRecord)
                .switchIfEmpty( handleOnErrorResume(
                        new AppException(NO_CARD_MSG.getInfo()), BAD_REQUEST.value()) );
    }

    public Mono<AppResponse> setCardDefault(String userId, String cardNo, String cardType) {
        return cardRepository.findByPublicId(userId)
                .flatMap(card -> {
                    card.setDefaultCard(card.getCardNo().equals(cardNo) &&
                            card.getType().trim().equalsIgnoreCase(cardType) );
                    return cardRepository.save(card);
                })
                .collectList()
                .map(UserSavedCardConverter::mapToRecords)
                .map(cards -> AppUtil.buildAppResponse(cards, CARD_SUCCESS.getInfo()))
                .switchIfEmpty( handleOnErrorResume(
                        new AppException(NO_CARD_MSG.getInfo()), BAD_REQUEST.value()) );
    }

    public Mono<AppResponse> removeCards(String userId, String cardNo, String cardType) {
        log.info("Delete Card for: ",userId);
        return cardRepository.findByPublicIdAndCardNoAndType(userId, cardNo, cardType)
                .flatMap(cardRepository::delete)
                .thenReturn(AppUtil.buildAppResponse( CARD_REMOVED.getInfo(), CARD_SUCCESS.getInfo()))
                .switchIfEmpty( handleOnErrorResume(
                        new AppException(CARD_NOT_FOUND.getInfo()), BAD_REQUEST.value()) );
    }

    private Mono<Boolean> checkExistence(String userId, String cardNo, String cardType) {
        return cardRepository.findByPublicIdAndCardNoAndType(userId, cardNo, cardType)
                .flatMap(cardDetails -> Mono.just(true))
                .switchIfEmpty(Mono.just(false));
    }

    private Mono<UserSavedCard> getEmptyCardInfo(PaymentHistory payment) {
        return Mono.just(UserSavedCard.builder()
                .email(payment.getPublicId())
                .authCode(CARD_EXISTS.getInfo())
                .build()
        );
    }

    private VerificationData getAuthorization(String response) {
        return AppUtil.gsonInstance().fromJson(response, VerificationData.class);
    }
}
