package com.finpuls.domain.dto.request.subscriptionplan;

import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UpdateSubscriptionPlanRequest {

    private String name;           // optional

    @PositiveOrZero
    @Digits(integer = 4, fraction = 2)
    private Double price;          // optional

    private Boolean active;        // optional

    private String description;    // optional
}


