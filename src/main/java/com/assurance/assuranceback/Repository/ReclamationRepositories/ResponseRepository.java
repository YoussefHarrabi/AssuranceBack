package com.assurance.assuranceback.Repository.ReclamationRepositories;

import com.assurance.assuranceback.Entity.ReclamationEntity.Response;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ResponseRepository extends JpaRepository<Response, Long> {
        Optional<Response> findByComplaintId(Long complaintId);
    }

