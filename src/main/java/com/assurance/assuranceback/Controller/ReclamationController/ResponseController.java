package com.assurance.assuranceback.Controller.ReclamationController;


import com.assurance.assuranceback.Entity.ReclamationEntity.Complaint;
import com.assurance.assuranceback.Entity.ReclamationEntity.DateTimeUtils;
import com.assurance.assuranceback.Entity.ReclamationEntity.Response;
import com.assurance.assuranceback.Entity.UserEntity.User;
import com.assurance.assuranceback.Repository.ReclamationRepositories.ComplaintRepository;
import com.assurance.assuranceback.Repository.UserRepositories.UserRepository;
import com.assurance.assuranceback.Services.ReclamationServices.EmailService;
import com.assurance.assuranceback.Services.ReclamationServices.ServiceResponse;
// import com.assurance.assuranceback.Services.ReclamationServices.EmailService;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/responses")
@RequiredArgsConstructor
public class ResponseController {

    private final ServiceResponse serviceResponse;
    @Autowired
    private ComplaintRepository complaintRepository;
    @Autowired
    private UserRepository userRepository;
    private final EmailService emailService;

    @PostMapping
    public ResponseEntity<Response> createResponse(@Valid @RequestBody Response response) {
        try {
            Optional<Complaint> complaintOptional = complaintRepository.findById(response.getComplaint().getId());
            Optional<User> advisorOptional = userRepository.findById(response.getAdvisor().getId());

            if (complaintOptional.isPresent() && advisorOptional.isPresent()) {
                response.setComplaint(complaintOptional.get());
                response.setAdvisor(advisorOptional.get());

                // Utiliser DateTimeUtils pour la date actuelle
                response.setDate(DateTimeUtils.getCurrentDateTime());

                Response createdResponse = serviceResponse.createResponse(response);

                // Mettre à jour la réclamation avec la nouvelle date
                Complaint complaint = complaintOptional.get();
                // Convertir LocalDateTime en Date pour lastModifiedDate
                complaint.setLastModifiedDate(java.util.Date.from(
                        DateTimeUtils.getCurrentDateTime()
                                .atZone(java.time.ZoneId.systemDefault())
                                .toInstant()
                ));
                complaintRepository.save(complaint);

                // Envoyer l'email de notification
                emailService.sendComplaintResponseNotification(createdResponse);

                return new ResponseEntity<>(createdResponse, HttpStatus.CREATED);
            }

            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } catch (MessagingException e) {
            System.err.println("Failed to send email notification: " + e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.CREATED);
        }
    }

    // Mettre à jour une réponse
    @PutMapping("/{id}")
    public ResponseEntity<Response> updateResponse(@PathVariable Long id, @Valid @RequestBody Response responseDetails) {
        try {
            // Mettre à jour la date avec DateTimeUtils
            responseDetails.setDate(DateTimeUtils.getCurrentDateTime());
            Response updatedResponse = serviceResponse.updateResponse(id, responseDetails);

            if (updatedResponse != null) {
                // Mettre à jour la date de modification de la réclamation
                Complaint complaint = updatedResponse.getComplaint();
                complaint.setLastModifiedDate(java.util.Date.from(
                        DateTimeUtils.getCurrentDateTime()
                                .atZone(java.time.ZoneId.systemDefault())
                                .toInstant()
                ));
                complaintRepository.save(complaint);

                return new ResponseEntity<>(updatedResponse, HttpStatus.OK);
            } else {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Récupérer toutes les réponses
    @GetMapping
    public ResponseEntity<List<Response>> getAllResponses() {
        List<Response> responses = serviceResponse.getAllResponses();
        return new ResponseEntity<>(responses, HttpStatus.OK);
    }

    // Récupérer une réponse par ID
    @GetMapping("/{id}")
    public ResponseEntity<Response> getResponseById(@PathVariable Long id) {
        Optional<Response> responseOptional = serviceResponse.getResponseById(id);

        if (responseOptional.isPresent()) {
            return new ResponseEntity<>(responseOptional.get(), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }



    // Supprimer une réponse
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteResponse(@PathVariable Long id) {
        try {
            serviceResponse.deleteResponse(id);
            return ResponseEntity.noContent().build(); // 204 No Content
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build(); // 404 Not Found
        }
    }
}