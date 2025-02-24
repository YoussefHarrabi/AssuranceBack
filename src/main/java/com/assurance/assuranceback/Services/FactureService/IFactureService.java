package com.assurance.assuranceback.Services.FactureService;



import com.assurance.assuranceback.Entity.FactureEntity.Facture;

import java.util.List;
import java.util.Optional;


public interface IFactureService {
    public void addFacture(Facture facture);
    void updateFacture(Long id, Facture updatedFacture);
    List<Facture> getAllFactures();
    Optional<Facture> getFactureById(Long id);
    void deleteFacture(Long id);






}
