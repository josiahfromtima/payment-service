package com.tima.platform.model.api.response;

import lombok.Builder;

/**
 * @Author: Josiah Adetayo
 * @Email: josleke@gmail.com, josiah.adetayo@meld-tech.com
 * @Date: 2/15/24
 */
@Builder
public record InfluencerDashboard(long totalTransactions, long completedTransactions, long pendingTransactions) {}
