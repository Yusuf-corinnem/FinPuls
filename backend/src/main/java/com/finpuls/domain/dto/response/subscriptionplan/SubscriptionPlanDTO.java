package com.finpuls.domain.dto.response.subscriptionplan;

import java.time.OffsetDateTime;
import java.util.UUID;

public interface SubscriptionPlanDTO {
    UUID getId();
    String getName();
    Double getPrice();
    boolean isActive();
    String getDescription();
    OffsetDateTime getCreatedAt();
    OffsetDateTime getUpdatedAt();
}


