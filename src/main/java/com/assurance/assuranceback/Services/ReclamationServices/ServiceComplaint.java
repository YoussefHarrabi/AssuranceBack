package com.assurance.assuranceback.Services.ReclamationServices;

import com.assurance.assuranceback.Entity.ReclamationEntity.Complaint;
import com.assurance.assuranceback.Repository.ReclamationRepositories.ComplaintRepository;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;


@Service
@AllArgsConstructor

public class ServiceComplaint implements IServiceComplaint{
    private  final ComplaintRepository complaintRepository;


    @Override
    public Complaint createComplaint(Complaint complaint) {

        return complaintRepository.save(complaint);
    }

    @Override
    public List<Complaint> getAllComplaints() {

        return complaintRepository.findAll();
    }



    @Override
    public Optional<Complaint> getComplaintById(Long id)
    {
        return complaintRepository.findById(id);
    }

    @Override
    public Complaint updateComplaint(Long id, Complaint complaintDetails) {
        Optional<Complaint> complaintOptional = complaintRepository.findById(id);

        if (complaintOptional.isPresent()) {
            Complaint complaint = complaintOptional.get();

            // Mise à jour des autres propriétés
            complaint.setTitle(complaintDetails.getTitle());
            complaint.setStatus(complaintDetails.getStatus());
            complaint.setDescription(complaintDetails.getDescription());
            complaint.setType(complaintDetails.getType());

            // Assigner la date automatiquement si elle n'est pas définie
            if (complaint.getCreationDate() == null) {
                complaint.setCreationDate(java.sql.Date.valueOf(LocalDate.now()));
            }

            // Sauvegarde et retour de l'entité mise à jour
            return complaintRepository.save(complaint);
        }

        throw new RuntimeException("Complaint with ID " + id + " not found.");
    }

    @Override
    public void deleteComplaint(Long id) {
        complaintRepository.deleteById(id);
    }


}
