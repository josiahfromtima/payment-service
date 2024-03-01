package com.tima.platform.service.search;

import com.tima.platform.converter.InfluencerTransactionConverter;
import com.tima.platform.converter.PaymentHistoryConverter;
import com.tima.platform.domain.InfluencerPaymentContract;
import com.tima.platform.exception.AppException;
import com.tima.platform.model.api.AppResponse;
import com.tima.platform.model.api.request.analytic.BudgetInsight;
import com.tima.platform.model.api.response.campaign.CampaignRecord;
import com.tima.platform.model.constant.StatusType;
import com.tima.platform.repository.InfluencerPaymentContractRepository;
import com.tima.platform.repository.InfluencerTransactionRepository;
import com.tima.platform.repository.PaymentHistoryRepository;
import com.tima.platform.service.helper.AgencyCampaignService;
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
import java.util.List;
import java.util.Objects;

import static com.tima.platform.exception.ApiErrorHandler.handleOnErrorResume;
import static com.tima.platform.model.security.TimaAuthority.ADMIN_BRAND;
import static com.tima.platform.model.security.TimaAuthority.ADMIN_INFLUENCER;
import static com.tima.platform.service.InfluencerPaymentLogService.INVALID_STATUS;
import static org.springframework.http.HttpStatus.BAD_REQUEST;

/**
 * @Author: Josiah Adetayo
 * @Email: josleke@gmail.com, josiah.adetayo@meld-tech.com
 * @Date: 3/1/24
 */
@Service
@RequiredArgsConstructor
public class PaymentSearchService {
    private final LoggerHelper log = LoggerHelper.newInstance(PaymentSearchService.class.getName());
    private final InfluencerTransactionRepository transactionRepository;
    private final PaymentHistoryRepository historyRepository;
    private final AgencyCampaignService campaignService;
    private final InfluencerPaymentContractRepository contractRepository;

    private static final String PAYMENT_MSG = "Payment Transaction Search request executed successfully";

    @PreAuthorize(ADMIN_INFLUENCER)
    public Mono<AppResponse> getInfluencerTransactions(String campaignName,
                                                       String brandName,
                                                       String appStatus,
                                                       ReportSettings settings) {
        log.info(String.format("Getting Influencer Transaction search Records for %s %s", campaignName, brandName));
        StatusType status = parseStatus(appStatus);
        if(Objects.isNull(status)) return handleOnErrorResume(new AppException(INVALID_STATUS), BAD_REQUEST.value());
        return transactionRepository.findByCampaignNameContainingIgnoreCaseOrBrandNameContainingIgnoreCaseAndStatus(
                campaignName, brandName, status.name(), setPage(settings))
                .collectList()
                .map(InfluencerTransactionConverter::mapToRecords)
                .map(transactionRecords -> AppUtil.buildAppResponse(transactionRecords, PAYMENT_MSG));
    }
    @PreAuthorize(ADMIN_BRAND)
    public Mono<AppResponse> getPaymentTransactions(String influencerName,
                                                       String appStatus,
                                                       ReportSettings settings) {
        log.info(String.format("Getting Payment Transaction search Records for %s", influencerName));
        StatusType status = parseStatus(appStatus);
        if(Objects.isNull(status)) return handleOnErrorResume(new AppException(INVALID_STATUS), BAD_REQUEST.value());
        return historyRepository.findByNameContainingIgnoreCaseAndStatus(influencerName, status.name(), setPage(settings))
                .collectList()
                .map(PaymentHistoryConverter::mapToRecords)
                .map(transactionRecords -> AppUtil.buildAppResponse(transactionRecords, PAYMENT_MSG));
    }

    @PreAuthorize(ADMIN_BRAND)
    public Mono<AppResponse> getPaymentAnalysis(String publicId, String token) {
        log.info(String.format("Getting Payment Analysis Records for %s", publicId));
       return campaignService.getCampaignById(publicId, token)
               .flatMap(campaignRecord -> getTotalSpent(publicId)
                       .map(totalAmount -> buildInsight(campaignRecord, totalAmount))
               )
                .map(transactionRecords -> AppUtil.buildAppResponse(transactionRecords, PAYMENT_MSG));
    }

    private Mono<BigDecimal> getTotalSpent(String publicId) {
        return contractRepository.findByCampaignPublicId(publicId)
                .collectList()
                .map(this::computeTotalSpent);
    }

    private BigDecimal computeTotalSpent(List<InfluencerPaymentContract> contracts) {
        return contracts.stream()
                .map(contract -> contract.getContractAmount().subtract(contract.getBalance()))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private BudgetInsight buildInsight(CampaignRecord campaignRecord, BigDecimal amountSpent) {
        return BudgetInsight.builder()
                .campaignName(campaignRecord.overview().name())
                .campaignBudget(campaignRecord.overview().plannedBudget())
                .amountSpent(amountSpent)
                .build();
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
}
