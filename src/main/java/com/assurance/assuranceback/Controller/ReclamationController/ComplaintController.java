package com.assurance.assuranceback.Controller.ReclamationController;

import com.assurance.assuranceback.Entity.ReclamationEntity.Complaint;
import com.assurance.assuranceback.Entity.UserEntity.User;
import com.assurance.assuranceback.Repository.UserRepositories.UserRepository;
import com.assurance.assuranceback.Services.ReclamationServices.NotificationService;
import com.assurance.assuranceback.Services.ReclamationServices.ServiceComplaint;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@AllArgsConstructor
@RequestMapping("/api/complaints")
public class ComplaintController {

    @Autowired
    private ServiceComplaint complaintService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private NotificationService notificationService;

    // ✅ Créer une réclamation
    @PostMapping
    public ResponseEntity<Complaint> createComplaint(@RequestBody Complaint complaint) {
        try {
            // Vérifier si le client existe avant d'associer la réclamation
            if (complaint.getClient() != null && complaint.getClient().getId() != null) {
                User client = userRepository.findById(2L).orElse(null);


                complaint.setClient(client);
                System.out.println("complaint"+complaint);
            }
            Complaint createdComplaint = complaintService.createComplaint(complaint);
            notificationService.sendNotification("Nouvelle réclamation créée avec ID : " + createdComplaint.getId());

            System.out.println("✅ Réclamation créée avec succès : " + createdComplaint);

            return new ResponseEntity<>(createdComplaint, HttpStatus.CREATED);
        } catch (Exception e) {
            System.err.println("❌ Erreur lors de la création de la réclamation : " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // ✅ Récupérer toutes les réclamations
    @GetMapping
    public ResponseEntity<List<Complaint>> getAllComplaints() {
        List<Complaint> complaints = complaintService.getAllComplaints();

        System.out.println("📢 Nombre de réclamations récupérées : " + complaints.size());

        if (complaints.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return new ResponseEntity<>(complaints, HttpStatus.OK);
    }

    // ✅ Récupérer une réclamation par son ID
    @GetMapping("/{id}")
    public ResponseEntity<Complaint> getComplaintById(@PathVariable Long id) {
        Optional<Complaint> complaint = complaintService.getComplaintById(id);

        if (complaint.isPresent()) {
            System.out.println("✅ Réclamation trouvée : " + complaint.get());
            return ResponseEntity.ok(complaint.get());
        } else {
            System.out.println("⚠️ Aucune réclamation trouvée avec l'ID : " + id);
            return ResponseEntity.notFound().build();
        }
    }

    // ✅ Mettre à jour une réclamation
    @PutMapping("/{id}")
    public ResponseEntity<Complaint> updateComplaint(@PathVariable Long id, @RequestBody Complaint complaintDetails) {
        Complaint updatedComplaint = complaintService.updateComplaint(id, complaintDetails);

        if (updatedComplaint != null) {
            System.out.println("✅ Réclamation mise à jour : " + updatedComplaint);
            notificationService.sendNotification("Réclamation mise à jour avec ID : " + updatedComplaint.getId());
            return ResponseEntity.ok(updatedComplaint);
        } else {
            System.out.println("❌ Impossible de mettre à jour, réclamation non trouvée avec ID : " + id);
            return ResponseEntity.notFound().build();
        }
    }

    // ✅ Supprimer une réclamation
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteComplaint(@PathVariable Long id) {
        try {
            complaintService.deleteComplaint(id);
            notificationService.sendNotification("Réclamation supprimée avec ID : " + id);
            System.out.println("🗑️ Réclamation supprimée avec ID : " + id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            System.err.println("❌ Erreur lors de la suppression de la réclamation : " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
