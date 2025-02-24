package com.assurance.assuranceback.Repository.CarrieresRepositories;

import com.assurance.assuranceback.Entity.CarrieresEntity.JobOffer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JobOfferRepository extends JpaRepository<JobOffer, Long> {
}
