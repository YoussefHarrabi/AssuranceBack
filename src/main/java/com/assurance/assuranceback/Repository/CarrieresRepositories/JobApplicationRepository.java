package com.assurance.assuranceback.Repository.CarrieresRepositories;

import com.assurance.assuranceback.Entity.CarrieresEntity.JobApplication;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JobApplicationRepository extends JpaRepository<JobApplication, Long> {
}
