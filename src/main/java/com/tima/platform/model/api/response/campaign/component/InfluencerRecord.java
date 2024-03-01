package com.tima.platform.model.api.response.campaign.component;

import lombok.Builder;

import java.util.List;

/**
 * @Author: Josiah Adetayo
 * @Email: josleke@gmail.com, josiah.adetayo@meld-tech.com
 * @Date: 3/1/24
 */
@Builder
public record InfluencerRecord(List<String> influencerCategory,
                               List<String> audienceSize,
                               List<String> audienceGender,
                               List<String> audienceAgeGroup,
                               List<String> audienceLocation) {}
