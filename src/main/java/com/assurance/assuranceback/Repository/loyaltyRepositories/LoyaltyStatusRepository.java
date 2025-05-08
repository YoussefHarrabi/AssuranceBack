package com.assurance.assuranceback.Repository.loyaltyRepositories;

import com.assurance.assuranceback.Entity.loyaltyEntity.LoyaltyStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LoyaltyStatusRepository extends JpaRepository<LoyaltyStatus, Long> {
    Optional<LoyaltyStatus> findByTier(String tier);
}