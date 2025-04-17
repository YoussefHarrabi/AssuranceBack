package com.assurance.assuranceback.Services.ReclamationServices;

import com.assurance.assuranceback.Entity.ReclamationEntity.Complaint;

import java.util.List;
import java.util.Optional;

public interface IServiceComplaint {
    Complaint createComplaint(Complaint complaint);

    List<Complaint> getAllComplaints();

    Optional<Complaint> getComplaintById(Long  id);

    Complaint updateComplaint(Long id, Complaint complaintDetails);

    void deleteComplaint(Long id);
}
