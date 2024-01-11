package com.tima.platform.service.helper;

import com.tima.platform.config.client.HttpConnectorService;
import com.tima.platform.model.api.AppResponse;
import com.tima.platform.service.PaymentHistoryService;
import com.tima.platform.util.AppUtil;
import com.tima.platform.util.LoggerHelper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import static com.tima.platform.model.constant.AppConstant.*;


/**
 * @Author: Josiah Adetayo
 * @Email: josleke@gmail.com, josiah.adetayo@meld-tech.com
 * @Date: 12/31/23
 */
@Service
@RequiredArgsConstructor
public class AgencyCampaignService {
    private final LoggerHelper log = LoggerHelper.newInstance(AgencyCampaignService.class.getName());
    private final HttpConnectorService connectorService;

    @Value("${agency.campaign.budget.url}")
    private String campaignBudgetUrl;

    public Mono<BigDecimal> getCampaignBudget(String token) {
        log.info("Getting Budget from ", campaignBudgetUrl);
        return connectorService.get(campaignBudgetUrl, headers(token), String.class)
                .map(s -> gson(s, AppResponse.class))
                .flatMap(appResponse -> json(appResponse.getData()))
                .map(s -> gson(s, BigDecimal.class));
    }

    private Map<String, String> headers(String token) {
        Map<String, String> headers = new HashMap<>();
        headers.put(ACCEPT, MEDIA_TYPE_JSON);
        headers.put(AUTHORIZATION, "Bearer " + token);
        return headers;
    }

    private Mono<String> json(Object data) {
        return Mono.just(AppUtil.gsonInstance().toJson(data) );
    }
    private  <T> T gson(String data, Class<T> returnType) {
        return AppUtil.gsonInstance().fromJson(data, returnType);
    }
}
