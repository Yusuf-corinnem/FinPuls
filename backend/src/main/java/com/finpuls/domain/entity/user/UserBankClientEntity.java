package com.finpuls.domain.entity.user;

import java.util.UUID;

import com.finpuls.domain.entity.common.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

@Entity
@Table(name = "user_bank_clients")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserBankClientEntity extends BaseEntity {
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

    @Column(name = "bank_client_id", nullable = false)
    private UUID bankClientId;

    @Column(name = "bank_name", nullable = false)
    private String bankName;

    @Column(name = "is_primary", nullable = false)
    private Boolean isPrimary;
}
