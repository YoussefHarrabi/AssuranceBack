package com.assurance.assuranceback.Repository.loyaltyRepositories;

import com.assurance.assuranceback.Entity.loyaltyEntity.Challenge;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ChallengeRepository extends JpaRepository<Challenge, Long> {
    List<Challenge> findByIsActiveTrue();
    List<Challenge> findByStartDateBeforeAndEndDateAfter(LocalDate now, LocalDate now2);
}
