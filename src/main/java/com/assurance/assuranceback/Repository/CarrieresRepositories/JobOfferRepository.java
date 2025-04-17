package com.assurance.assuranceback.Repository.CarrieresRepositories;

import com.assurance.assuranceback.Entity.CarrieresEntity.JobOffer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface JobOfferRepository extends JpaRepository<JobOffer, Long> {
    List<JobOffer> findByTitreContainingIgnoreCase(String titre);
}
