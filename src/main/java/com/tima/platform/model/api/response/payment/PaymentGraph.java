package com.tima.platform.model.api.response.payment;

import lombok.Builder;

import java.util.List;

/**
 * @Author: Josiah Adetayo
 * @Email: josleke@gmail.com, josiah.adetayo@meld-tech.com
 * @Date: 2/17/24
 */
@Builder
public record PaymentGraph(String name, int index, List<Legend> legends) {}
