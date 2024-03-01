package com.tima.platform.model.api.response.campaign.component;

import lombok.Builder;

import java.util.List;

/**
 * @Author: Josiah Adetayo
 * @Email: josleke@gmail.com, josiah.adetayo@meld-tech.com
 * @Date: 3/1/24
 */
@Builder
public record CreativeRecord(String paymentType,
                             String startDate,
                             String endDate,
                             List<String> contentType,
                             List<String> contentPlacement,
                             String creativeBrief,
                             String rules,
                             List<String> creativeTone,
                             String referenceLink,
                             List<String> awarenessObjective,
                             List<String> acquisitionObjective,
                             String thumbnail,
                             Boolean visibility) {}
