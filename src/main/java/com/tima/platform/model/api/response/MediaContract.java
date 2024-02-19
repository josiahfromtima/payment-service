package com.tima.platform.model.api.response;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Builder;

/**
 * @Author: Josiah Adetayo
 * @Email: josleke@gmail.com, josiah.adetayo@meld-tech.com
 * @Date: 2/18/24
 */
@Builder
public record MediaContract(String name,
                            @NotNull(message = "Media value is required")
                            @PositiveOrZero(message = "Value must be 0 or more")
                            @Max(value = 100, message = "Value cannot be more than 100")
                            Integer value) {}
