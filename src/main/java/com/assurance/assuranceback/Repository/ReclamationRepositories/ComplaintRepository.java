package com.assurance.assuranceback.Repository.ReclamationRepositories;

import com.assurance.assuranceback.Entity.ReclamationEntity.Complaint;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ComplaintRepository extends JpaRepository<Complaint, Long> {
}