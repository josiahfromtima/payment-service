package com.tima.platform.resource.bank;

import com.tima.platform.config.AuthTokenConfig;
import com.tima.platform.model.api.ApiResponse;
import com.tima.platform.model.api.response.CustomerBankDetailRecord;
import com.tima.platform.service.BankService;
import com.tima.platform.service.CustomerBankDetailService;
import com.tima.platform.util.LoggerHelper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import static com.tima.platform.model.api.ApiResponse.buildServerResponse;

/**
 * @Author: Josiah Adetayo
 * @Email: josleke@gmail.com, josiah.adetayo@meld-tech.com
 * @Date: 12/14/23
 */
@Service
@RequiredArgsConstructor
public class BankResourceHandler {
    LoggerHelper log = LoggerHelper.newInstance(BankResourceHandler.class.getName());

    private final BankService bankService;
    private final CustomerBankDetailService customerService;
    private static final String X_FORWARD_FOR = "X-Forwarded-For";

    /**
     *  This section marks the banks activities
     */
    public Mono<ServerResponse> getBanks(ServerRequest request)  {
        log.info("Get Registered Banks Requested", request.headers().firstHeader(X_FORWARD_FOR));
        return buildServerResponse(bankService.getBanks());
    }

    public Mono<ServerResponse> getBankByCode(ServerRequest request)  {
        String codeOrName = request.pathVariable("codeOrName");
        log.info("Get Registered Bank by code Requested", request.headers().firstHeader(X_FORWARD_FOR));
        return buildServerResponse(bankService.getBankByCodeOName(codeOrName));
    }

    /**
     *  This section marks the customer bank detail activities
     */
    public Mono<ServerResponse> getAllCustomerDetails(ServerRequest request)  {
        log.info("Get All Customer Bank Detail Requested", request.headers().firstHeader(X_FORWARD_FOR));
        return buildServerResponse(customerService.getCustomerBankDetails());
    }

    public Mono<ServerResponse> getCustomerDetail(ServerRequest request)  {
        Mono<JwtAuthenticationToken> jwtAuthToken = AuthTokenConfig.authenticatedToken(request);
        log.info("Get My Customer Bank Detail Requested", request.headers().firstHeader(X_FORWARD_FOR));
        return jwtAuthToken
                .map(ApiResponse::getPublicIdFromToken)
                .map(customerService::getDetailsByPublicId)
                .flatMap(ApiResponse::buildServerResponse);
    }
    public Mono<ServerResponse> addCustomerDetail(ServerRequest request)  {
        Mono<CustomerBankDetailRecord> recordMono = request.bodyToMono(CustomerBankDetailRecord.class);
        log.info("Add Customer Bank Detail Requested", request.headers().firstHeader(X_FORWARD_FOR));
        return recordMono
                .map(customerService::addCustomerDetails)
                .flatMap(ApiResponse::buildServerResponse);
    }
    public Mono<ServerResponse>  updateCustomerDetail(ServerRequest request)  {
        Mono<CustomerBankDetailRecord> recordMono = request.bodyToMono(CustomerBankDetailRecord.class);
        Mono<JwtAuthenticationToken> jwtAuthToken = AuthTokenConfig.authenticatedToken(request);
        log.info("Update Customer Bank Detail Requested", request.headers().firstHeader(X_FORWARD_FOR));
        return jwtAuthToken
                .map(ApiResponse::getPublicIdFromToken)
                .flatMap(publicId -> recordMono.map(detailRecord ->
                        customerService.updateCustomerDetails(publicId, detailRecord) ))
                .flatMap(ApiResponse::buildServerResponse);
    }
    public Mono<ServerResponse> deleteCustomerDetail(ServerRequest request)  {
        String publicId = request.pathVariable("publicId");
        log.info("Delete Customer Bank Detail Requested", request.headers().firstHeader(X_FORWARD_FOR));
        return buildServerResponse(customerService.deleteCustomerDetails(publicId));
    }

}
