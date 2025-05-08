package com.assurance.assuranceback.Repository.loyaltyRepositories;

import com.assurance.assuranceback.Entity.loyaltyEntity.Bonus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BonusRepository extends JpaRepository<Bonus, Long> {
    List<Bonus> findByIsAvailableTrue();
    List<Bonus> findByPointsRequiredLessThanEqual(Integer points);
}