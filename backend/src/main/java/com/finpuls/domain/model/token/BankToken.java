package com.finpuls.domain.model.token;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.annotations.UuidGenerator;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "bank_tokens")
@Getter
@Setter
@NoArgsConstructor
public class BankToken {
    @Id
    @GeneratedValue
    @UuidGenerator
    @Column(name = "id", nullable = false, updatable = false, columnDefinition = "UUID")
    private UUID id;

    @Column(name = "user_id", nullable = false, length = 100)
    private String userId;

    @Column(name = "bank_code", nullable = false, length = 50)
    private String bankCode;

    @Column(name = "client_id", nullable = false, length = 100)
    private String clientId;

    @Column(name = "access_token", nullable = false, length = 500)
    private String accessToken;

    @Column(name = "token_type", nullable = false, length = 20)
    private String tokenType = "bearer";

    @Column(name = "expires_in", nullable = false)
    private Integer expiresIn;

    @Column(name = "expires_at", nullable = false, columnDefinition = "TIMESTAMPTZ")
    private OffsetDateTime expiresAt;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, columnDefinition = "TIMESTAMPTZ")
    private OffsetDateTime createdAt;
    
    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false, columnDefinition = "TIMESTAMPTZ")
    private OffsetDateTime updatedAt;
    
    public BankToken(String userId, String bankCode, String clientId, String accessToken, 
                     String tokenType, Integer expiresIn) {
        this.userId = userId;
        this.bankCode = bankCode;
        this.clientId = clientId;
        this.accessToken = accessToken;
        this.tokenType = tokenType != null ? tokenType : "bearer";
        this.setExpiresIn(expiresIn);
    }

    public void setExpiresIn(Integer expiresIn) {
        this.expiresIn = expiresIn;

        if (expiresIn != null) {
            this.expiresAt = OffsetDateTime.now().plusSeconds(expiresIn);
        }
    }
}

