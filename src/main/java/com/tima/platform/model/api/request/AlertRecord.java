package com.tima.platform.model.api.request;

import lombok.Builder;

/**
 * @Author: Josiah Adetayo
 * @Email: josleke@gmail.com, josiah.adetayo@meld-tech.com
 * @Date: 2/2/24
 */
@Builder
public record AlertRecord(String userPublicId,
                          String title,
                          String message,
                          String type,
                          String typeStatus,
                          String status
) {}
