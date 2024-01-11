package com.tima.platform.service;

import com.tima.platform.converter.CustomerBankDetailConverter;
import com.tima.platform.domain.CustomerBankDetail;
import com.tima.platform.exception.AppException;
import com.tima.platform.model.api.AppResponse;
import com.tima.platform.model.api.response.CustomerBankDetailRecord;
import com.tima.platform.repository.CustomerBankDetailRepository;
import com.tima.platform.service.helper.PaymentProviderService;
import com.tima.platform.util.AppError;
import com.tima.platform.util.AppUtil;
import com.tima.platform.util.LoggerHelper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import static com.tima.platform.exception.ApiErrorHandler.handleOnErrorResume;
import static com.tima.platform.model.security.TimaAuthority.ADMIN;
import static com.tima.platform.model.security.TimaAuthority.ADMIN_BRAND_INFLUENCER;
import static org.springframework.http.HttpStatus.BAD_REQUEST;

/**
 * @Author: Josiah Adetayo
 * @Email: josleke@gmail.com, josiah.adetayo@meld-tech.com
 * @Date: 12/14/23
 */
@Service
@RequiredArgsConstructor
public class CustomerBankDetailService {
    private final LoggerHelper log = LoggerHelper.newInstance(CustomerBankDetailService.class.getName());
    private final CustomerBankDetailRepository detailRepository;
    private final PaymentProviderService providerService;

    private static final String CUSTOMER_MSG = "Customer Bank Detail request executed successfully";
    private static final String INVALID_CUSTOMER = "The Customer Bank Detail publicId is invalid";

    @PreAuthorize(ADMIN)
    public Mono<AppResponse> getCustomerBankDetails() {
        log.info("Getting ALl Customer Bank Details Record...");
        return detailRepository.findAll()
                .collectList()
                .map(CustomerBankDetailConverter::mapToRecords)
                .map(customerRecords -> AppUtil.buildAppResponse(customerRecords, CUSTOMER_MSG));
    }

    @PreAuthorize(ADMIN_BRAND_INFLUENCER)
    public Mono<AppResponse> getDetailsByPublicId(String publicId) {
        log.info("Getting Customer Bank Detail Record bu publicId");
        return detailRepository.findByPublicId(publicId)
                .map(CustomerBankDetailConverter::mapToRecord)
                .map(customerRecords -> AppUtil.buildAppResponse(customerRecords, CUSTOMER_MSG))
                .switchIfEmpty(handleOnErrorResume(new AppException(INVALID_CUSTOMER), BAD_REQUEST.value()));
    }

    public Mono<AppResponse> addCustomerDetails(CustomerBankDetailRecord detailRecord) {
        log.info("Adding new Customer Bank Detail Record ");
        return providerService.createOrUpdateRecipient(CustomerBankDetailConverter.mapToEntity(detailRecord))
                .flatMap(detailRepository::save)
                .map(CustomerBankDetailConverter::mapToRecord)
                .map(customerRecords -> AppUtil.buildAppResponse(customerRecords, CUSTOMER_MSG))
                .onErrorResume(t ->
                        handleOnErrorResume(new AppException(AppError.massage(t.getMessage())), BAD_REQUEST.value()));
    }

    @PreAuthorize(ADMIN_BRAND_INFLUENCER)
    public Mono<AppResponse> updateCustomerDetails(String publicId, CustomerBankDetailRecord detailRecord) {
        log.info("Updating Customer Bank Detail Record ", detailRecord.publicId());
        return validateDetail(publicId)
                        .flatMap(customerBankDetail -> {
                            CustomerBankDetail modifiedRecord = CustomerBankDetailConverter.mapToEntity(detailRecord);
                            modifiedRecord.setId(customerBankDetail.getId());
                            modifiedRecord.setPublicId(publicId);
                            return Mono.just(modifiedRecord);
                        })
                .flatMap(providerService::createOrUpdateRecipient)
                .flatMap(detailRepository::save)
                .map(CustomerBankDetailConverter::mapToRecord)
                .map(customerRecords -> AppUtil.buildAppResponse(customerRecords, CUSTOMER_MSG))
                .onErrorResume(t ->
                        handleOnErrorResume(new AppException(AppError.massage(t.getMessage())), BAD_REQUEST.value()));
    }

    @PreAuthorize(ADMIN)
    public Mono<AppResponse> deleteCustomerDetails(String publicId) {
        log.info("Deleting Customer Bank Detail Record ", publicId);
        return validateDetail(publicId)
                .flatMap(detailRepository::delete)
                .then(Mono.fromCallable(() ->
                        AppUtil.buildAppResponse("Customer (" + publicId + ") Deleted", CUSTOMER_MSG)));
    }

    private Mono<CustomerBankDetail> validateDetail(String publicId) {
        return detailRepository.findByPublicId(publicId)
                .switchIfEmpty(handleOnErrorResume(new AppException(INVALID_CUSTOMER), BAD_REQUEST.value()));
    }

}
