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
        // Vérifier l'existence du type d'assurance
        TypeAssurance typeAssurance = typeAssuranceRepository.findById(demandeAssurance.getTypeAssurance().getId())
                .orElseThrow(() -> new ResourceNotFoundException("Insurance type not found with ID: "
                        + demandeAssurance.getTypeAssurance().getId()));

        demandeAssurance.setTypeAssurance(typeAssurance);

        // Définir le statut par défaut à PENDING si non défini
        if (demandeAssurance.getStatus() == null) {
            demandeAssurance.setStatus(StatusDemande.PENDING);
        }

        DemandeAssurance saved = demandeAssuranceRepository.save(demandeAssurance);

        // Vérifie que l'email est présent et envoie l'email si statut = PENDING
        if (saved.getStatus() == StatusDemande.PENDING && saved.getUserEmail() != null && !saved.getUserEmail().isBlank()) {
            try {
                String subject = "📥 Votre demande d'assurance est en cours de traitement";
                String body = generateHtmlEmailContent(saved.getNomAssurance(), saved.getStatus());

                System.out.println("📧 Envoi d'email à : " + saved.getUserEmail());
                System.out.println("📨 Contenu : " + body);

                emailService.sendEmail(saved.getUserEmail(), subject, body);
            } catch (MessagingException e) {
                log.error("❌ Échec de l'envoi de l'email à {}", saved.getUserEmail(), e);
            }
        } else {
            System.out.println("⚠️ Aucune adresse email renseignée, email non envoyé.");
        }

        return saved;
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
        // Rechercher la demande d'assurance
        DemandeAssurance demande = demandeAssuranceRepository.findById(demandeId)
                .orElseThrow(() -> new ResourceNotFoundException("Demande non trouvée avec ID " + demandeId));

        // Mettre à jour le statut
        demande.setStatus(nouveauStatus);

        // Sauvegarder la demande mise à jour
        DemandeAssurance updated = demandeAssuranceRepository.save(demande);

        // Tenter d'envoyer un email
        try {
            String subject = "📩 Mise à jour de votre demande d'assurance";
            String body = "<p>Bonjour,</p>" +
                    "<p>Votre demande d’assurance <strong>\"" + demande.getNomAssurance() + "\"</strong> a été mise à jour au statut : " +
                    "<strong>" + nouveauStatus.name() + "</strong>.</p>" +
                    "<p>Merci pour votre confiance.</p>";

            emailService.sendEmail(emailClient, subject, body);
        } catch (MessagingException e) {
            log.error("Échec de l'envoi de l'email à {}", emailClient, e);
            // Optionnel : ne pas bloquer le processus si l’email échoue
            // throw new RuntimeException("Échec de l'envoi de l'email", e);
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
