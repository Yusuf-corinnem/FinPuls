package com.finplus.mapper.subscriptionplan;

import com.finplus.domain.dto.response.subscriptionplan.SubscriptionPlanDTOImpl;
import com.finplus.domain.model.subscriptionplan.SubscriptionPlan;
import com.finplus.mapper.config.MapStructConfig;
import org.mapstruct.Mapper;

@Mapper(config = MapStructConfig.class)
public interface SubscriptionPlanMapper {
    SubscriptionPlanDTOImpl toDto(SubscriptionPlan entity);
}