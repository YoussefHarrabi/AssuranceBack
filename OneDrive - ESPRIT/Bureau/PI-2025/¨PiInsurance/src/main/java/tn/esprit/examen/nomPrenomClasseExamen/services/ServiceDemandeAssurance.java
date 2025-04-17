package tn.esprit.examen.nomPrenomClasseExamen.services;

import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import tn.esprit.examen.nomPrenomClasseExamen.ResourceNotFoundException;
import tn.esprit.examen.nomPrenomClasseExamen.entities.DemandeAssurance;
import tn.esprit.examen.nomPrenomClasseExamen.entities.StatusDemande;
import tn.esprit.examen.nomPrenomClasseExamen.entities.TypeAssurance;
import tn.esprit.examen.nomPrenomClasseExamen.repositories.InsuranceParticularRepository;
import tn.esprit.examen.nomPrenomClasseExamen.repositories.TypeAssuranceRepository;
import tn.esprit.examen.nomPrenomClasseExamen.services.EmailService;
import tn.esprit.examen.nomPrenomClasseExamen.services.IDemandeAssurance;

import java.util.List;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@Service
public class ServiceDemandeAssurance implements IDemandeAssurance {

    private final InsuranceParticularRepository demandeAssuranceRepository;
    private final EmailService emailService;
    private final TypeAssuranceRepository typeAssuranceRepository; // Added to verify existence of insurance type

    @Override
    public DemandeAssurance add(DemandeAssurance demandeAssurance) {
        // Verify if the insurance type exists before setting it
        TypeAssurance typeAssurance = typeAssuranceRepository.findById(demandeAssurance.getTypeAssurance().getId())
                .orElseThrow(() -> new ResourceNotFoundException("Insurance type not found with ID: "
                        + demandeAssurance.getTypeAssurance().getId()));

        demandeAssurance.setTypeAssurance(typeAssurance); // Set existing type

        return demandeAssuranceRepository.save(demandeAssurance);
    }

    @Override
    public List<DemandeAssurance> getAll() {
        return demandeAssuranceRepository.findAll();
    }

    @Override
    public DemandeAssurance getById(Long id) {
        return demandeAssuranceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Insurance request not found with ID: " + id));
    }

    @Override
    public DemandeAssurance update(Long id, DemandeAssurance updatedInsurance) {
        return demandeAssuranceRepository.findById(id).map(demande -> {
            demande.setNomAssurance(updatedInsurance.getNomAssurance());
            demande.setDescription(updatedInsurance.getDescription());
            demande.setPrix(updatedInsurance.getPrix());
            demande.setTypeAssurance(updatedInsurance.getTypeAssurance());
            // Verify insurance type
            TypeAssurance typeAssurance = typeAssuranceRepository.findById(updatedInsurance.getTypeAssurance().getId())
                    .orElseThrow(() -> new ResourceNotFoundException("Insurance type not found"));

            demande.setTypeAssurance(typeAssurance);

            return demandeAssuranceRepository.save(demande);
        }).orElseThrow(() -> new ResourceNotFoundException("Insurance request with ID " + id + " not found"));
    }

    @Override
    public void delete(Long id) {
        DemandeAssurance demande = demandeAssuranceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Demande non trouvée avec ID " + id));

        demandeAssuranceRepository.delete(demande);
    }

    @Override
    public boolean existsById(Long id) {
        return demandeAssuranceRepository.existsById(id);
    }

    public Map<String, Long> getTypeAssuranceStatistics() {
        return typeAssuranceRepository.getTypeAssuranceStatistics();
    }

    // Updated method to handle MessagingException
    public DemandeAssurance updateStatus(Long demandeId, StatusDemande nouveauStatus, String emailClient) {
        // Find the DemandeAssurance object
        DemandeAssurance demande = demandeAssuranceRepository.findById(demandeId)
                .orElseThrow(() -> new ResourceNotFoundException("Demande non trouvée avec ID " + demandeId));

        // Set the new status
        demande.setStatus(nouveauStatus);

        // Save the updated demande
        DemandeAssurance updated = demandeAssuranceRepository.save(demande);

        try {
            // Send email notification after status update
            emailService.sendEmail(
                    emailClient,
                    "📩 Mise à jour de votre demande d'assurance",
                    "Votre demande d’assurance <strong>\"" + demande.getNomAssurance() + "\"</strong> a été mise à jour au statut : <strong>" + nouveauStatus.name() + "</strong>."
            );

        } catch (MessagingException e) {
            log.error("Échec de l'envoi de l'email", e);
            throw new RuntimeException("Échec de l'envoi de l'email", e);
        }

        return updated;
    }
    private String generateHtmlEmailContent(String nomAssurance, StatusDemande status) {
        return """
        <!DOCTYPE html>
        <html lang="fr">
        <head>
            <meta charset="UTF-8">
            <style>
                body { font-family: Arial, sans-serif; background-color: #f9f9f9; padding: 30px; }
                .container { background-color: #ffffff; padding: 20px; border-radius: 10px; box-shadow: 0 0 15px rgba(0,0,0,0.1); }
                h2 { color: #0056b3; }
                .status { font-size: 16px; font-weight: bold; color: #28a745; }
                .footer { font-size: 12px; color: #888; margin-top: 30px; }
            </style>
        </head>
        <body>
            <div class="container">
                <h2>🔔 Mise à jour de votre demande d'assurance</h2>
                <p>Bonjour,</p>
                <p>Votre demande <strong>« %s »</strong> a été mise à jour avec le statut suivant :</p>
                <p class="status">%s</p>
                <p>Merci pour votre confiance.<br>Assurance Maghrebia</p>
                <div class="footer">Ceci est un message automatique. Merci de ne pas y répondre.</div>
            </div>
        </body>
        </html>
        """.formatted(nomAssurance, status.name());
    }


}
