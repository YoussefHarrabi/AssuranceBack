package com.assurance.assuranceback.Repository.FactureRepository;

import  com.assurance.assuranceback.Entity.FactureEntity.Facture;
import com.assurance.assuranceback.dto.SocieteStatsDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FactureRepos extends JpaRepository<Facture, Long> {

    List<Facture> findByUserId(Long userId);

    @Query("SELECT new com.assurance.assuranceback.dto.SocieteStatsDTO(" +
            "SUM(f.montant), " + // montantTotal
            "SUM(CASE WHEN f.factureStatut = 'PAYEE' THEN f.montant ELSE 0 END), " + // montantPaye
            "SUM(CASE WHEN f.factureStatut = 'NONPAYEE' THEN f.montant ELSE 0 END), " +
            "SUM(CASE WHEN f.factureStatut = 'EnAttente' THEN f.montant ELSE 0 END), " +
            "SUM(CASE WHEN f.factureStatut = 'PAYEE' THEN 1 ELSE 0 END), " +
            "SUM(CASE WHEN f.factureStatut = 'NONPAYEE' THEN 1 ELSE 0 END), " +
            "SUM(CASE WHEN f.factureStatut = 'EnAttente' THEN 1 ELSE 0 END), " + // nombreFacturesEnAttente
            "COUNT(f), " + // nombreTotalFactures
            "(SUM(CASE WHEN f.factureStatut = 'PAYEE' THEN f.montant ELSE 0 END) - " + // gainPotentiel
            "(SUM(CASE WHEN f.factureStatut = 'NONPAYEE' THEN f.montant ELSE 0 END) + " +
            "SUM(CASE WHEN f.factureStatut = 'EnAttente' THEN f.montant ELSE 0 END))) " +
            ") FROM Facture f")
    SocieteStatsDTO getStatistiquesSociete();





}