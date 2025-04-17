package com.assurance.assuranceback.Services.ReclamationServices;

import com.assurance.assuranceback.Entity.ReclamationEntity.Complaint;
import com.assurance.assuranceback.Entity.ReclamationEntity.Response;
import com.assurance.assuranceback.Entity.UserEntity.User;
import com.assurance.assuranceback.Repository.ReclamationRepositories.ComplaintRepository;
import com.assurance.assuranceback.Repository.ReclamationRepositories.ResponseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ServiceResponse implements IServiceResponse {

    private final ResponseRepository responseRepository;
    private final ComplaintRepository complaintRepository;

    // Créer une nouvelle réponse avec validation (empêcher les doublons)
    @Override
    public Response createResponse(Response response) {
        if (response.getComplaint() == null || response.getAdvisor() == null) {
            throw new RuntimeException("La réclamation et le conseiller sont obligatoires !");
        }

        Long complaintId = response.getComplaint().getId();

        // Vérifier si la réclamation existe
        Complaint complaint = complaintRepository.findById(complaintId)
                .orElseThrow(() -> new RuntimeException("Réclamation non trouvée !"));

        // Vérifier si une réponse existe déjà pour cette réclamation (One-to-One)
        Optional<Response> existingResponse = responseRepository.findByComplaintId(complaintId);
        if (existingResponse.isPresent()) {
            throw new RuntimeException("Cette réclamation a déjà une réponse !");
        }

        // Définir la date actuelle
        response.setDate(LocalDateTime.now());

        // Sauvegarder la réponse
        return responseRepository.save(response);
    }

    // Récupérer toutes les réponses
    @Override
    public List<Response> getAllResponses() {
        return responseRepository.findAll();
    }

    // Récupérer une réponse par ID
    @Override
    public Optional<Response> getResponseById(Long id) {
        return responseRepository.findById(id);
    }

    // Mettre à jour une réponse
    @Override
    public Response updateResponse(Long id, Response responseDetails) {
        Response response = responseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Réponse non trouvée !"));

        response.setAdvisor(responseDetails.getAdvisor());
        response.setContent(responseDetails.getContent());
        response.setDate(LocalDateTime.now());

        return responseRepository.save(response);
    }

    // Supprimer une réponse
    @Override
    public void deleteResponse(Long id) {
        Response response = responseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Réponse non trouvée !"));

        responseRepository.delete(response);
    }
}
