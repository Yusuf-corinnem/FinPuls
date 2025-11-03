package com.finplus.domain.dto.response.subscriptionplan;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.OffsetDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SubscriptionPlanDTOImpl implements SubscriptionPlanDTO {
    private UUID id;
    private String name;
    private Double price;
    private boolean active;
    private String description;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;
}


