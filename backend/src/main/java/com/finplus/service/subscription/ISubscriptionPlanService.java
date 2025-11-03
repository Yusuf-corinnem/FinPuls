package com.finplus.service.subscription;

import com.finplus.domain.model.subscriptionplan.SubscriptionPlan;
import com.finplus.service.common.CrudService;
import com.finplus.domain.dto.request.subscriptionplan.CreateSubscriptionPlanRequest;
import com.finplus.domain.dto.request.subscriptionplan.UpdateSubscriptionPlanRequest;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ISubscriptionPlanService extends CrudService<SubscriptionPlan, UUID> {
    SubscriptionPlan create(CreateSubscriptionPlanRequest request);
    SubscriptionPlan update(UUID id, UpdateSubscriptionPlanRequest request);
    Optional<SubscriptionPlan> findByName(String name);
    List<SubscriptionPlan> findAllActive();
    SubscriptionPlan activate(UUID id);
    SubscriptionPlan deactivate(UUID id);
}