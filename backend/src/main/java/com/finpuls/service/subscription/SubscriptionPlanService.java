package com.finpuls.service.subscription;

import com.finpuls.domain.model.subscriptionplan.SubscriptionPlan;
import com.finpuls.domain.repository.SubscriptionPlanRepository;
import com.finpuls.service.common.AbstractCrudService;
import com.finpuls.domain.dto.request.subscriptionplan.CreateSubscriptionPlanRequest;
import com.finpuls.domain.dto.request.subscriptionplan.UpdateSubscriptionPlanRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class SubscriptionPlanService extends AbstractCrudService<SubscriptionPlan, UUID> implements ISubscriptionPlanService {

    private final SubscriptionPlanRepository repository;

    @Autowired
    public SubscriptionPlanService(SubscriptionPlanRepository repository) {
        super(SubscriptionPlan.class, repository);
        this.repository = repository;
    }

    @Override
    @Transactional
    public SubscriptionPlan create(CreateSubscriptionPlanRequest request) {
        String name = requireNotBlank(request.getName(), "name");

        if (existsByField("name", name)) {
            throw new IllegalArgumentException("Тариф с таким именем уже существует: " + name);
        }

        SubscriptionPlan plan = new SubscriptionPlan();
        plan.setName(name);
        setIfNotNull(request.getPrice(), plan::setPrice);
        setIfNotNull(request.getActive(), plan::setActive);
        setIfNotNull(request.getDescription(), plan::setDescription);

        return save(plan);
    }

    @Override
    @Transactional
    public SubscriptionPlan update(UUID id, UpdateSubscriptionPlanRequest request) {
        SubscriptionPlan plan = getOrThrow(findById(id), () -> "Тариф не найден: " + id);

        if (request.getName() != null && !request.getName().isBlank() && !request.getName().equals(plan.getName())) {
            if (existsByField("name", request.getName())) {
                throw new IllegalArgumentException("Тариф с таким именем уже существует: " + request.getName());
            }
            plan.setName(request.getName());
        }

        setIfNotNull(request.getPrice(), plan::setPrice);
        setIfNotNull(request.getActive(), plan::setActive);
        setIfNotNull(request.getDescription(), plan::setDescription);
        
        return save(plan);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<SubscriptionPlan> findByName(String name) {
        return findByField("name", name);
    }

    @Override
    @Transactional(readOnly = true)
    public List<SubscriptionPlan> findAllActive() {
        return repository.findAllByActiveTrue();
    }

    @Override
    @Transactional
    public SubscriptionPlan activate(UUID id) {
        SubscriptionPlan plan = getOrThrow(findById(id), () -> "Тариф не найден: " + id);
        plan.setActive(true);

        return save(plan);
    }

    @Override
    @Transactional
    public SubscriptionPlan deactivate(UUID id) {
        SubscriptionPlan plan = getOrThrow(findById(id), () -> "Тариф не найден: " + id);
        plan.setActive(false);

        return save(plan);
    }
}