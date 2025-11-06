package com.finpuls.mapper.subscriptionplan;

import com.finpuls.domain.dto.response.subscriptionplan.SubscriptionPlanDTOImpl;
import com.finpuls.domain.model.subscriptionplan.SubscriptionPlan;
import com.finpuls.mapper.config.MapStructConfig;
import org.mapstruct.Mapper;

@Mapper(config = MapStructConfig.class)
public interface SubscriptionPlanMapper {
    SubscriptionPlanDTOImpl toDto(SubscriptionPlan entity);
}