package com.tima.platform.service;

import com.tima.platform.converter.InfluencerTransactionConverter;
import com.tima.platform.domain.InfluencerTransaction;
import com.tima.platform.exception.AppException;
import com.tima.platform.model.api.AppResponse;
import com.tima.platform.model.constant.StatusType;
import com.tima.platform.repository.InfluencerTransactionRepository;
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
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.time.LocalDate;
import java.util.Objects;

import static com.tima.platform.exception.ApiErrorHandler.handleOnErrorResume;
import static com.tima.platform.model.security.TimaAuthority.ADMIN_INFLUENCER;
import static org.springframework.http.HttpStatus.BAD_REQUEST;

/**
 * @Author: Josiah Adetayo
 * @Email: josleke@gmail.com, josiah.adetayo@meld-tech.com
 * @Date: 1/1/24
 */
@Service
@RequiredArgsConstructor
public class InfluencerPaymentLogService {
    private final LoggerHelper log = LoggerHelper.newInstance(InfluencerPaymentLogService.class.getName());
    private final InfluencerTransactionRepository transactionRepository;

    private static final String PAYMENT_MSG = "Influencer Payment Transaction request executed successfully";
    public static final String INVALID_STATUS = "Invalid Transaction status provided";

    @PreAuthorize(ADMIN_INFLUENCER)
    public Mono<AppResponse> getTransactions(String publicId, ReportSettings settings) {
        log.info("Getting ALl Transaction Records for ", publicId, " with filter");
        return transactionRepository.findByPublicId(publicId, setPage(settings))
                .collectList()
                .map(InfluencerTransactionConverter::mapToRecords)
                .map(transactionRecords -> AppUtil.buildAppResponse(transactionRecords, PAYMENT_MSG));
    }

    @PreAuthorize(ADMIN_INFLUENCER)
    public Mono<AppResponse> getTransactionsByBrand(String publicId, String brandName, ReportSettings settings) {
        log.info("Getting Transaction Records By Brand ", brandName);
        return transactionRepository.findByBrandNameAndPublicId(brandName, publicId, setPage(settings))
                .collectList()
                .map(InfluencerTransactionConverter::mapToRecords)
                .map(transactionRecords -> AppUtil.buildAppResponse(transactionRecords, PAYMENT_MSG));
    }

    @PreAuthorize(ADMIN_INFLUENCER)
    public Mono<AppResponse> getTransactionsByStatus(String publicId, String appStatus, ReportSettings settings) {
        log.info("Getting Transaction Records By Brand ", appStatus);
        StatusType status = parseStatus(appStatus);
        if(Objects.isNull(status)) return handleOnErrorResume(new AppException(INVALID_STATUS), BAD_REQUEST.value());
        return transactionRepository.findByStatusAndPublicId(status.name(), publicId, setPage(settings))
                .collectList()
                .map(InfluencerTransactionConverter::mapToRecords)
                .map(transactionRecords -> AppUtil.buildAppResponse(transactionRecords, PAYMENT_MSG));
    }

    @PreAuthorize(ADMIN_INFLUENCER)
    public Mono<AppResponse> getTransactionsByDate(String publicId, String lastDate) {
        log.info("Getting Transaction Records By Date ", lastDate);
        return getTransactionByYear(publicId, lastDate)
                .collectList()
                .map(InfluencerTransactionConverter::mapToRecords)
                .map(transactionRecords -> AppUtil.buildAppResponse(transactionRecords, PAYMENT_MSG));
    }
    public Mono<AppResponse> addTransaction(InfluencerTransaction transactionRecord) {
        log.info("Add Transaction Record ", transactionRecord);
        return transactionRepository.save(transactionRecord)
                .map(InfluencerTransactionConverter::mapToRecord)
                .map(savedRecord -> AppUtil.buildAppResponse(savedRecord, PAYMENT_MSG))
                .onErrorResume(t ->
                        handleOnErrorResume(new AppException(AppError.massage(t.getMessage())), BAD_REQUEST.value()));
    }

    private Flux<InfluencerTransaction> getTransactionByYear(String publicId, String lastDate) {
        Instant lastDay = getLastDate(lastDate);
        return transactionRepository.findByPublicIdAndCreatedOnBefore(publicId, lastDay);
    }

    private Pageable setPage(ReportSettings settings) {
        return PageRequest.of(settings.getPage(), settings.getSize(),
                Sort.Direction.fromString(settings.getSortIn()), settings.getSortBy());
    }

    private StatusType parseStatus(String status) {
        try {
            return StatusType.valueOf(status);
        }catch (Exception e) {
            return null;
        }
    }

    private Instant getLastDate(String theDate) {
        if(Objects.nonNull(theDate) && theDate.isEmpty())
            theDate = LocalDate.now().getYear() +  "-12-31";
        return ReportSettings.instance()
                .end(theDate)
                .getEnd();
    }

}
