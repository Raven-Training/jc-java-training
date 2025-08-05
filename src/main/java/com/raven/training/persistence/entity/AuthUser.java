package com.raven.training.persistence.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Entity representing an authenticated user in the system.
 * This class maps to the "auth_user" table in the database and stores
 * credentials and authentication-related information for a user.
 *
 * @author Juan Esteban Camacho Barrera
 * @version 1.0
 * @since 2025-08-05
 */
@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString
@Table(name = "auth_user")
public class AuthUser {

    @Id
    @EqualsAndHashCode.Include
    private UUID id;

    private String name;

    private LocalDate birthDate;

    @Column(unique = true)
    private String username;

    private String password;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(name = "create_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "is_enable")
    private boolean isEnabled;

    @Column(name = "account_no_expired")
    private boolean accountNoExpired;

    @Column(name = "account_no_locked")
    private boolean accountNoLocked;

    @Column(name = "credential_no_expired")
    private boolean credentialNoExpired;

    /**
     * Callback method executed before the entity is persisted.
     * It ensures a UUID is generated if one is not already set and
     * sets the creation timestamp.
     */
    @PrePersist
    public void prePersist(){
        if (id == null){
            this.id = UUID.randomUUID();
        }
        this.createdAt = LocalDateTime.now();
    }
}
