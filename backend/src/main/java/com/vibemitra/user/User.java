package com.vibemitra.user;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "users")
public class User {

    @Id
    @Column(nullable = false, updatable = false)
    private UUID id;

    @Column(nullable = false, unique = true, length = 255)
    private String email;

    @Column(name = "password_hash", nullable = false, length = 100)
    private String passwordHash;

    @Column(name = "display_name", nullable = false, length = 60)
    private String displayName;

    @Column(name = "date_of_birth", nullable = false)
    private LocalDate dateOfBirth;

    @Column(nullable = false, length = 20)
    private String city;

    @Column(nullable = false, length = 20)
    private String status;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    @Column(name = "deleted_at")
    private Instant deletedAt;

    protected User() {
        // for JPA
    }

    public User(UUID id, String email, String passwordHash, String displayName,
                LocalDate dateOfBirth, String city, Instant now) {
        this.id = id;
        this.email = email;
        this.passwordHash = passwordHash;
        this.displayName = displayName;
        this.dateOfBirth = dateOfBirth;
        this.city = city;
        this.status = "ACTIVE";
        this.createdAt = now;
        this.updatedAt = now;
    }

    /** Applies a profile change and records when it happened. Only the owning service should call this. */
    public void updateProfile(String displayName, String city, Instant now) {
        if (displayName != null) {
            this.displayName = displayName;
        }
        if (city != null) {
            this.city = city;
        }
        this.updatedAt = now;
    }

    /**
     * Erases personal data and makes the account unusable. The row is kept (with placeholders)
     * so references such as audit logs stay valid. Only the owning service should call this.
     */
    public void markDeleted(String tombstoneEmail, String tombstonePasswordHash, Instant now) {
        this.status = "DELETED";
        this.email = tombstoneEmail;
        this.passwordHash = tombstonePasswordHash;
        this.displayName = "Deleted user";
        this.dateOfBirth = LocalDate.of(1970, 1, 1);
        this.deletedAt = now;
        this.updatedAt = now;
    }

    public boolean isActive() {
        return "ACTIVE".equals(status) && deletedAt == null;
    }

    public UUID getId() { return id; }
    public String getEmail() { return email; }
    public String getPasswordHash() { return passwordHash; }
    public String getDisplayName() { return displayName; }
    public LocalDate getDateOfBirth() { return dateOfBirth; }
    public String getCity() { return city; }
    public String getStatus() { return status; }
    public Instant getCreatedAt() { return createdAt; }
}
