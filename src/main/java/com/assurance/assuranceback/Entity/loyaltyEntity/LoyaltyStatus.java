package com.assurance.assuranceback.Entity.loyaltyEntity;

import com.assurance.assuranceback.Entity.UserEntity.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.Set;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoyaltyStatus {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String tier; // Silver, Gold, Platinum

    @Column(nullable = false)
    private Integer pointsThreshold; // Minimum points needed to reach this tier

    private String description;

    private String benefits;

    @OneToMany(mappedBy = "loyaltyStatus")
    private Set<User> users;

    @Column(nullable = false, updatable = false)
    private LocalDate createdAt;

    private LocalDate updatedAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDate.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDate.now();
    }
}