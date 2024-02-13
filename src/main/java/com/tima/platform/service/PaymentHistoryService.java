package com.tima.platform.service;

import com.tima.platform.converter.PaymentHistoryConverter;
import com.tima.platform.domain.PaymentHistory;
import com.tima.platform.exception.AppException;
import com.tima.platform.model.api.AppResponse;
import com.tima.platform.model.api.response.BrandAggregatePaymentRecord;
import com.tima.platform.model.api.response.PaymentAggregateTotal;
import com.tima.platform.model.api.response.paystack.TransferRecord;
import com.tima.platform.model.constant.StatusType;
import com.tima.platform.repository.PaymentHistoryRepository;
import com.tima.platform.service.helper.AgencyCampaignService;
import com.tima.platform.util.AppError;
import com.tima.platform.util.AppUtil;
import com.tima.platform.util.LoggerHelper;
import com.tima.platform.util.ReportSettings;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.Objects;

import static com.tima.platform.exception.ApiErrorHandler.handleOnErrorResume;
import static com.tima.platform.model.security.TimaAuthority.*;
import static org.springframework.http.HttpStatus.BAD_REQUEST;

/**
 * @Author: Josiah Adetayo
 * @Email: josleke@gmail.com, josiah.adetayo@meld-tech.com
 * @Date: 12/28/23
 */
@Service
@RequiredArgsConstructor
public class PaymentHistoryService {
    private final LoggerHelper log = LoggerHelper.newInstance(PaymentHistoryService.class.getName());
    private final PaymentHistoryRepository historyRepository;
    private final AgencyCampaignService campaignService;

    private static final String PAYMENT_MSG = "Payment Detail request executed successfully";
    private static final String INVALID_STATUS = "Invalid Payment status provided";

    @PreAuthorize(ADMIN_BRAND_INFLUENCER)
    public Mono<AppResponse> getPaymentStatus() {
        log.info("Getting ALl Payment Details Record with filter...");
        return Mono.just(AppUtil.buildAppResponse(StatusType.values(), PAYMENT_MSG));
    }

    @PreAuthorize(ADMIN_BRAND)
    public Mono<AppResponse> getPaymentHistories(ReportSettings settings) {
        log.info("Getting ALl Payment Details Record with pagination...");
        return historyRepository.findAllBy(setPage(settings))
                .collectList()
                .map(PaymentHistoryConverter::mapToRecords)
                .map(paymentRecords -> AppUtil.buildAppResponse(paymentRecords, PAYMENT_MSG));
    }

    @PreAuthorize(ADMIN_BRAND)
    public Mono<AppResponse> getPaymentHistoriesByDateAndStatus(String appStatus, ReportSettings settings) {
        log.info("Getting All Payment Details Record with date range, status, and pagination ...");
        StatusType status = parseStatus(appStatus);
        if(Objects.isNull(status)) return handleOnErrorResume(new AppException(INVALID_STATUS), BAD_REQUEST.value());
        return historyRepository.findByCreatedOnBetweenAndStatus(settings.getStart(),
                        settings.getEnd(),
                        status.name(),
                        setPage(settings))
                .collectList()
                .map(PaymentHistoryConverter::mapToRecords)
                .map(paymentRecords -> AppUtil.buildAppResponse(paymentRecords, PAYMENT_MSG));
    }

    @PreAuthorize(ADMIN_BRAND)
    public Mono<AppResponse> getPaymentHistoryByStatus(String appStatus, ReportSettings settings) {
        log.info("Getting ALl Payment Details Record By Status...");
        StatusType status = parseStatus(appStatus);
        if(Objects.isNull(status)) return handleOnErrorResume(new AppException(INVALID_STATUS), BAD_REQUEST.value());
        return historyRepository.findByStatus(status.name(), setPage(settings))
                .collectList()
                .map(PaymentHistoryConverter::mapToRecords)
                .map(paymentRecords -> AppUtil.buildAppResponse(paymentRecords, PAYMENT_MSG));
    }

    @PreAuthorize(ADMIN_BRAND)
    public Mono<PaymentHistory> addPaymentHistory(PaymentHistory history) {
        log.info("Add New Payment Record");
        return historyRepository.save(history)
                .onErrorResume(t ->
                        handleOnErrorResume(new AppException(AppError.massage(t.getMessage())), BAD_REQUEST.value()));
    }

    @PreAuthorize(ADMIN_BRAND)
    public Mono<PaymentHistory> updatePaymentHistory(TransferRecord transferRecord) {
        log.info("Update Transfer Record");
        return getPaymentHistory(transferRecord.reference())
                .flatMap(oldHistoryRecord -> {
                    oldHistoryRecord.setPaymentResponse(json(transferRecord));
                    return historyRepository.save(oldHistoryRecord);
                })
                .onErrorResume(t ->
                        handleOnErrorResume(new AppException(AppError.massage(t.getMessage())), BAD_REQUEST.value()));
    }

    @PreAuthorize(ADMIN_BRAND)
    public Mono<PaymentHistory> updatePaymentHistory(PaymentHistory history) {
        log.info("Update Payment Record");
        return historyRepository.save(history)
                .onErrorResume(t ->
                        handleOnErrorResume(new AppException(AppError.massage(t.getMessage())), BAD_REQUEST.value()));
    }

    @PreAuthorize(ADMIN_BRAND)
    public Mono<AppResponse> getPaymentTotals(String token) {
        log.info("Payment Total Aggregates Record");
        return  campaignService.getCampaignBudget(token)
                .flatMap(totalBudget -> historyRepository.getPaymentAggregate(PaymentAggregateTotal.class)
                        .map(aggregatePayment -> BrandAggregatePaymentRecord.builder()
                                .totalClientPaid(aggregatePayment.payee())
                                .totalBudget(totalBudget)
                                .totalAmountPaid(getOrDefault(aggregatePayment.paid()))
                                .totalBalance(getOrDefault(aggregatePayment.outstanding()))
                                .build())
                ).map(paymentRecords -> AppUtil.buildAppResponse(paymentRecords, PAYMENT_MSG));
    }

    public Mono<PaymentHistory> getPaymentHistory(String reference) {
        return historyRepository.findByReference(reference);
    }
    public Mono<PaymentHistory> getPaymentHistory(String reference, StatusType type) {
        return historyRepository.findByReferenceAndStatus(reference, type.name());
    }

    private Pageable setPage(ReportSettings settings) {
        return PageRequest.of(settings.getPage(), settings.getSize(),
                Sort.Direction.fromString(settings.getSortIn()), settings.getSortBy());
    }

    private BigDecimal getOrDefault(BigDecimal value) {
        return Objects.isNull(value) ? BigDecimal.ZERO : value;
    }

    private StatusType parseStatus(String status) {
        try {
            return StatusType.valueOf(status);
        }catch (Exception e) {
            return null;
        }
    }

    private String json(Object item) {
        return AppUtil.gsonInstance().toJson(item);
    }

}
