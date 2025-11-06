package com.finplus.domain.model.user;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.annotations.UuidGenerator;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor
import lombok.AllArgsConstructor

import java.time.OffsetDateTime;
import java.util.UUID;

import com.finplus.domain.model.user.UserStatus;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class User{
    @Id
    @UuidGenerator
    private UUID id;

    @Column(name = "client_id", length = 128, nullable = false, unique = true)
    private String clientId;

    @Column (name = "subscription_id", nullable = false)
    private UUID subscriptionId;

    @Enumerated(EnumType.STRING)
    @Column (name = "status", nullable = false)
    private UserStatus status = UserStatus.ACTIVE;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, columnDefinition = "TIMESTAMPTZ")
    private OffsetDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false, columnDefinition = "TIMESTAMPTZ")
    private OffsetDateTime updatedAt;
}