package com.finplus.domain.dto.request.subscriptionplan;

import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreateSubscriptionPlanRequest {

    @NotBlank
    private String name;

    @NotNull
    @PositiveOrZero
    @Digits(integer = 4, fraction = 2)
    private Double price;

    @NotNull
    private Boolean active;

    private String description;
}


