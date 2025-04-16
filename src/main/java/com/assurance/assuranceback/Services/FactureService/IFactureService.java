package com.assurance.assuranceback.Services.FactureService;

import com.assurance.assuranceback.Entity.FactureEntity.Facture;
import com.assurance.assuranceback.Entity.FactureEntity.ResponseMessage;

import java.util.List;
import java.util.Optional;

public interface IFactureService {
    Facture addFacture(Facture facture);
    Facture updateFacture(Long id, Facture updatedFacture);
    List<Facture> getAllFactures();
    Optional<Facture> getFactureById(Long id);
    void deleteFacture(Long id);
    List<Facture> findByUserId(Long userId);
    ResponseMessage sendUnpaidInvoiceNotification(Long factureId, String currentDate);

}