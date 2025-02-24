package com.assurance.assuranceback.Services.FactureService;


import com.assurance.assuranceback.Entity.FactureEntity.Facture;
import com.assurance.assuranceback.Entity.FactureEntity.Paiement;
import com.assurance.assuranceback.Repository.FactureRepository.FactureRepos;
import com.assurance.assuranceback.Repository.FactureRepository.PaiementRepos;
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
    @Override
    public void addFacture(Facture facture) {

        factureRepos.save(facture);

        }
    @Override
    public void updateFacture(Long id, Facture updatedFacture) {
        Facture existingFacture = factureRepos.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Facture not found"));

        // Update fields of existingFacture with updatedFacture
        existingFacture.setIdClient(updatedFacture.getIdClient());
        existingFacture.setMontant(updatedFacture.getMontant());
        existingFacture.setDateEmission(updatedFacture.getDateEmission());
        existingFacture.setFactureStatut(updatedFacture.getFactureStatut());

        // Clear existing paiements and add new paiements
        existingFacture.getPaiements().clear();
        for (Paiement paiement : updatedFacture.getPaiements()) {
            paiement.setFacture(existingFacture);
            existingFacture.getPaiements().add(paiement);
        }

        // Save the updated facture
         factureRepos.save(existingFacture);
    }
    @Override
    public List<Facture> getAllFactures(){
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

}
