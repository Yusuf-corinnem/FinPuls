package com.finplus.domain.dto.request.user;

import com.finplus.domain.model.user.UserStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UpdateUserRequest {

    // Все поля опциональны — обновляем только переданные
    private String clientId;

    private UUID subscriptionId;

    private UserStatus status;
}
