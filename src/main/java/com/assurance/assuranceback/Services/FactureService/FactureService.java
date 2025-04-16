package com.assurance.assuranceback.Services.FactureService;

import com.assurance.assuranceback.Entity.FactureEntity.Facture;
import com.assurance.assuranceback.Entity.FactureEntity.Paiement;
import com.assurance.assuranceback.Entity.FactureEntity.ResponseMessage;
import com.assurance.assuranceback.Entity.UserEntity.User;
import com.assurance.assuranceback.Enum.FactureStatut;
import com.assurance.assuranceback.Repository.FactureRepository.FactureRepos;
import com.assurance.assuranceback.Repository.FactureRepository.PaiementRepos;
import com.assurance.assuranceback.Repository.UserRepositories.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class FactureService implements IFactureService {

    @Autowired
    private FactureRepos factureRepos;
    @Autowired
    private PaiementRepos paiementRepos;
    @Autowired
    private UserRepository userRepos;
    @Autowired
    private EmailService emailService;

    @Override
    public Facture addFacture(Facture facture) {
        // Vérifiez si l'utilisateur existe
        User user = userRepos.findById(facture.getUser().getId())
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        for (Paiement paiement : facture.getPaiements()) {
            paiement.setFacture(facture);
        }

        return factureRepos.save(facture);
    }

    @Override
    public Facture updateFacture(Long id, Facture updatedFacture) {
        Facture existingFacture = factureRepos.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Facture not found"));

        // Vérifiez si l'utilisateur existe
        User user = userRepos.findById(updatedFacture.getUser().getId())
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        existingFacture.setMontant(updatedFacture.getMontant());
        existingFacture.setDateEmission(updatedFacture.getDateEmission());
        existingFacture.setFactureStatut(updatedFacture.getFactureStatut());
        existingFacture.setUser(updatedFacture.getUser());

        existingFacture.getPaiements().clear();
        for (Paiement paiement : updatedFacture.getPaiements()) {
            paiement.setFacture(existingFacture);
            existingFacture.getPaiements().add(paiement);
        }

        return factureRepos.save(existingFacture);
    }

    @Override
    public List<Facture> getAllFactures() {
        return factureRepos.findAll();
    }

    @Override
    public Optional<Facture> getFactureById(Long id) {
        return factureRepos.findById(id);
    }

    @Override
    public void deleteFacture(Long id) {
        if (!factureRepos.existsById(id)) {
            throw new EntityNotFoundException("Facture not found");
        }
        factureRepos.deleteById(id);
    }

    @Override
    public List<Facture> findByUserId(Long userId) {
        return factureRepos.findByUserId(userId);
    }

    // Add this new method
    @Override
    public ResponseMessage sendUnpaidInvoiceNotification(Long factureId, String currentDate) {
        try {
            Facture facture = factureRepos.findById(factureId)
                    .orElseThrow(() -> new EntityNotFoundException("Facture non trouvée avec l'id: " + factureId));

            if (facture.getFactureStatut() == FactureStatut.NONPAYEE) {
                return emailService.sendUnpaidInvoiceNotification(facture, currentDate);
            } else {
                return new ResponseMessage("error", "La facture n'est pas en statut non payée");
            }

        } catch (EntityNotFoundException e) {
            return new ResponseMessage("error", e.getMessage());
        } catch (Exception e) {
            return new ResponseMessage("error", "Erreur inattendue: " + e.getMessage());
        }
    }

}