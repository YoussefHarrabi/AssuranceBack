package com.assurance.assuranceback.Services.FactureService;

import com.assurance.assuranceback.Entity.FactureEntity.Paiement;

import java.util.List;

public interface IPaiementService {
    void payerFacture(long factureId, Paiement paiement);
    List<Paiement> consulterPaiements(long factureId);
}
