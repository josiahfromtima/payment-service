package com.tima.platform.service;

import com.tima.platform.converter.BankConverter;
import com.tima.platform.exception.AppException;
import com.tima.platform.model.api.AppResponse;
import com.tima.platform.repository.BankRepository;
import com.tima.platform.util.AppUtil;
import com.tima.platform.util.LoggerHelper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import static com.tima.platform.exception.ApiErrorHandler.handleOnErrorResume;
import static org.springframework.http.HttpStatus.BAD_REQUEST;

/**
 * @Author: Josiah Adetayo
 * @Email: josleke@gmail.com, josiah.adetayo@meld-tech.com
 * @Date: 12/14/23
 */
@Service
@RequiredArgsConstructor
public class BankService {
    private final LoggerHelper log = LoggerHelper.newInstance(BankService.class.getName());
    private final BankRepository bankRepository;

    private static final String BANK_MSG = "Bank request executed successfully";
    private static final String INVALID_BANK = "The bank name/code is invalid";

    public Mono<AppResponse> getBanks() {
        log.info("Getting ALl Banks Record...");
        return bankRepository.findAll()
                .collectList()
                .map(BankConverter::mapToRecords)
                .map(countryRecords -> AppUtil.buildAppResponse(countryRecords, BANK_MSG));
    }

    public Mono<AppResponse> getBankByCodeOName(String codeOrName) {
        log.info("Getting Bank Record by code", codeOrName);
        return bankRepository.findByCodeOrName(codeOrName, codeOrName)
                .map(BankConverter::mapToRecord)
                .map(countryRecords -> AppUtil.buildAppResponse(countryRecords, BANK_MSG))
                .switchIfEmpty(handleOnErrorResume(new AppException(INVALID_BANK), BAD_REQUEST.value()));
    }
}
