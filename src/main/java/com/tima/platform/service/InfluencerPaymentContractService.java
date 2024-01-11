package com.tima.platform.service;

import com.tima.platform.converter.InfluencerPaymentContractConverter;
import com.tima.platform.domain.InfluencerPaymentContract;
import com.tima.platform.exception.AppException;
import com.tima.platform.model.api.AppResponse;
import com.tima.platform.model.api.response.BrandAggregatePaymentRecord;
import com.tima.platform.model.api.response.InfluencerPaymentContractRecord;
import com.tima.platform.model.api.response.PaymentAggregateTotal;
import com.tima.platform.model.constant.StatusType;
import com.tima.platform.repository.InfluencerPaymentContractRepository;
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
import static com.tima.platform.model.security.TimaAuthority.ADMIN;
import static com.tima.platform.model.security.TimaAuthority.ADMIN_BRAND;
import static org.springframework.http.HttpStatus.BAD_REQUEST;

/**
 * @Author: Josiah Adetayo
 * @Email: josleke@gmail.com, josiah.adetayo@meld-tech.com
 * @Date: 1/3/24
 */
@Service
@RequiredArgsConstructor
public class InfluencerPaymentContractService {
    private final LoggerHelper log = LoggerHelper.newInstance(InfluencerPaymentContractService.class.getName());
    private final InfluencerPaymentContractRepository contractRepository;
    private final AgencyCampaignService campaignService;

    private static final String CONTRACT_MSG = "Contract request executed successfully";
    private static final String INVALID_STATUS = "Invalid Contract status provided";
    private static final String INVALID_CONTRACT = "One of the Contract ids is invalid";

    @PreAuthorize(ADMIN)
    public Mono<AppResponse> getAllContract(ReportSettings settings) {
        log.info("Getting All Contract Records with filter...");
        return contractRepository.findAllBy(setPage(settings))
                .collectList()
                .map(InfluencerPaymentContractConverter::mapToRecords)
                .map(contractRecords -> AppUtil.buildAppResponse(contractRecords, CONTRACT_MSG));
    }
    @PreAuthorize(ADMIN)
    public Mono<AppResponse> getContractByStatus(String appStatus, ReportSettings settings) {
        log.info("Getting Contract Records by status with filter...", appStatus);
        StatusType status = parseStatus(appStatus);
        if(Objects.isNull(status)) return handleOnErrorResume(new AppException(INVALID_STATUS), BAD_REQUEST.value());
        return contractRepository.findByStatus(status.name(), setPage(settings))
                .collectList()
                .map(InfluencerPaymentContractConverter::mapToRecords)
                .map(contractRecords -> AppUtil.buildAppResponse(contractRecords, CONTRACT_MSG));
    }
    @PreAuthorize(ADMIN)
    public Mono<AppResponse> getContractById(String publicId, String campaignPublicId) {
        log.info("Getting Contract Record by ids");
        return getContact(publicId, campaignPublicId)
                .map(InfluencerPaymentContractConverter::mapToRecord)
                .map(contractRecord -> AppUtil.buildAppResponse(contractRecord, CONTRACT_MSG));
    }

    @PreAuthorize(ADMIN)
    public Mono<AppResponse> getContractById(String contactId) {
        log.info("Getting Contract Record by ids");
        return getContact(contactId)
                .map(InfluencerPaymentContractConverter::mapToRecord)
                .map(contractRecord -> AppUtil.buildAppResponse(contractRecord, CONTRACT_MSG));
    }

    @PreAuthorize(ADMIN)
    public Mono<AppResponse> addContract(InfluencerPaymentContractRecord contractRecord) {
        log.info("Adding Contract Record by");
        return contractRepository.save(InfluencerPaymentContractConverter.mapToEntity(contractRecord))
                .map(InfluencerPaymentContractConverter::mapToRecord)
                .map(contract -> AppUtil.buildAppResponse(contract, CONTRACT_MSG))
                .onErrorResume(t ->
                        handleOnErrorResume(new AppException(AppError.massage(t.getMessage())), BAD_REQUEST.value()));
    }

    @PreAuthorize(ADMIN)
    public Mono<AppResponse> updateContract(InfluencerPaymentContractRecord contractRecord) {
        log.info("Updating Contract Record");
        return getContact(contractRecord.influencerPublicId(), contractRecord.campaignPublicId())
                .flatMap(paymentContract -> {
                    InfluencerPaymentContract modified = InfluencerPaymentContractConverter.mapToEntity(contractRecord);
                    modified.setId(paymentContract.getId());
                    modified.setContractId(paymentContract.getContractId());
                    return contractRepository.save(modified);
                })
                .map(InfluencerPaymentContractConverter::mapToRecord)
                .map(contract -> AppUtil.buildAppResponse(contract, CONTRACT_MSG))
                .onErrorResume(t ->
                        handleOnErrorResume(new AppException(AppError.massage(t.getMessage())), BAD_REQUEST.value()));
    }

    @PreAuthorize(ADMIN)
    public Mono<AppResponse> deleteContract(String publicId) {
        log.info("Deleting Contract Record ", publicId);
        return getContact(publicId)
                .flatMap(contractRepository::delete)
                .then(Mono.fromCallable(() ->
                        AppUtil.buildAppResponse(publicId + " Contract Deleted", CONTRACT_MSG)));
    }

    @PreAuthorize(ADMIN_BRAND)
    public Mono<AppResponse> getPaymentTotals(String token, String publicId) {
        log.info("Payment Total Aggregates Record for ", publicId);
        return  campaignService.getCampaignBudget(token)
                .flatMap(totalBudget ->
                        contractRepository.getContractPaymentAggregate(publicId, PaymentAggregateTotal.class)
                        .map(aggregatePayment -> BrandAggregatePaymentRecord.builder()
                                .totalClientPaid(aggregatePayment.payee())
                                .totalBudget(totalBudget)
                                .totalAmountPaid(getOrDefault(aggregatePayment.paid()))
                                .totalBalance(getOrDefault(aggregatePayment.outstanding()))
                                .build())
                ).map(paymentRecords -> AppUtil.buildAppResponse(paymentRecords, CONTRACT_MSG));
    }

    public Mono<InfluencerPaymentContract> getContact(String influencerId, String campaignId) {
        return contractRepository.findByInfluencerPublicIdAndCampaignPublicId(influencerId, campaignId)
                .switchIfEmpty(handleOnErrorResume(new AppException(INVALID_CONTRACT), BAD_REQUEST.value()));
    }
    public Mono<InfluencerPaymentContract> getContact(String contactId) {
        return contractRepository.findByContractId(contactId)
                .switchIfEmpty(handleOnErrorResume(new AppException(INVALID_CONTRACT), BAD_REQUEST.value()));
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

    private BigDecimal getOrDefault(BigDecimal value) {
        return Objects.isNull(value) ? BigDecimal.ZERO : value;
    }

}
