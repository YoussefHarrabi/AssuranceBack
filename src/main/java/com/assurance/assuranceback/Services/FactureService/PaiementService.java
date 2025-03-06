package com.assurance.assuranceback.Services.FactureService;
import com.assurance.assuranceback.Entity.FactureEntity.Facture;
import com.assurance.assuranceback.Entity.FactureEntity.Paiement;
import com.assurance.assuranceback.Enum.FactureStatut;
import com.assurance.assuranceback.Repository.FactureRepository.FactureRepos;
import com.assurance.assuranceback.Repository.FactureRepository.PaiementRepos;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class PaiementService implements IPaiementService {
    @Autowired
    private PaiementRepos paiementRepos;

    @Autowired
    private FactureRepos factureRepos;

    @Override
    public void payerFacture(long factureId, Paiement paiement) {
        Facture facture = factureRepos.findById(factureId)
                .orElseThrow(() -> new RuntimeException("Facture not found"));
        paiement.setFacture(facture);
        paiementRepos.save(paiement);

        // Mettre à jour le statut de la facture en "PAYEE"
        facture.setFactureStatut(FactureStatut.valueOf("PAYEE"));
        factureRepos.save(facture);

        System.out.println("Paiement effectué pour la facture n°" + factureId);
    }

    @Override
    public List<Paiement> consulterPaiements(long factureId) {
        Facture facture = factureRepos.findById(factureId)
                .orElseThrow(() -> new RuntimeException("Facture not found"));
        return facture.getPaiements();
    }
}