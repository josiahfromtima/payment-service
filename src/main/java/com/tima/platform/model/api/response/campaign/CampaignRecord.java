package com.tima.platform.model.api.response.campaign;

import com.tima.platform.model.api.response.campaign.component.CreativeRecord;
import com.tima.platform.model.api.response.campaign.component.InfluencerRecord;
import com.tima.platform.model.api.response.campaign.component.OverviewRecord;
import lombok.Builder;

/**
 * @Author: Josiah Adetayo
 * @Email: josleke@gmail.com, josiah.adetayo@meld-tech.com
 * @Date: 3/1/24
 */
@Builder
public record CampaignRecord(OverviewRecord overview, InfluencerRecord influencer, CreativeRecord creative) {}
