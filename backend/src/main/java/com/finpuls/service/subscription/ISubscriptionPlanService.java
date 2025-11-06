package com.finpuls.service.subscription;

import com.finpuls.domain.model.subscriptionplan.SubscriptionPlan;
import com.finpuls.service.common.CrudService;
import com.finpuls.domain.dto.request.subscriptionplan.CreateSubscriptionPlanRequest;
import com.finpuls.domain.dto.request.subscriptionplan.UpdateSubscriptionPlanRequest;

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