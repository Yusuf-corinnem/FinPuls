package com.finplus.domain.dto.request.user;

import com.finplus.domain.model.user.UserStatus;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreateUserRequest {

    @NotBlank
    private String clientId;

    // Может быть null (ссылка на текущую подписку необязательна)
    private UUID subscriptionId;

    // Необязательное поле; если не передано — можно выставить статус по умолчанию в сервисе
    private UserStatus status;
}
