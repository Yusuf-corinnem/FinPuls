package com.finpuls.domain.repository;

import com.finpuls.domain.model.subscriptionplan.SubscriptionPlan;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SubscriptionPlanRepository extends JpaRepository<SubscriptionPlan, UUID> {
    Optional<SubscriptionPlan> findByName(String name);
    boolean existsByName(String name);
    List<SubscriptionPlan> findAllByActiveTrue();
}