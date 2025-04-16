package com.assurance.assuranceback.Repository.FactureRepository;

import com.assurance.assuranceback.Entity.FactureEntity.RelanceHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface RelanceHistoryRepository extends JpaRepository<RelanceHistory, Long> {
    // Trouver les relances par facture avec JPQL
    @Query("SELECT r FROM RelanceHistory r WHERE r.facture.factureId = :factureId ORDER BY r.dateEnvoi DESC")
    List<RelanceHistory> findRelancesByFactureId(@Param("factureId") Long factureId);

    // Trouver la dernière relance d'une facture
    @Query("SELECT r FROM RelanceHistory r WHERE r.facture.factureId = :factureId " +
            "ORDER BY r.dateEnvoi DESC LIMIT 1")
    Optional<RelanceHistory> findLastRelanceByFactureId(@Param("factureId") Long factureId);

    // Compter le nombre de relances par facture
    @Query("SELECT r.facture.factureId, COUNT(r) as nombreRelances " +
            "FROM RelanceHistory r GROUP BY r.facture.factureId")
    List<Object[]> countRelancesPerFacture();

    // Trouver les relances envoyées entre deux dates
    @Query("SELECT r FROM RelanceHistory r WHERE r.dateEnvoi BETWEEN :debut AND :fin " +
            "ORDER BY r.dateEnvoi DESC")
    List<RelanceHistory> findRelancesBetweenDates(
            @Param("debut") LocalDateTime debut,
            @Param("fin") LocalDateTime fin
    );

    // Statistiques des relances par statut
    @Query("SELECT r.statusPaiement, COUNT(r) " +
            "FROM RelanceHistory r GROUP BY r.statusPaiement")
    List<Object[]> getRelanceStatsByStatus();

    // Trouver les factures avec plus de X relances
    @Query("SELECT r.facture.factureId, COUNT(r) as nombreRelances " +
            "FROM RelanceHistory r " +
            "GROUP BY r.facture.factureId " +
            "HAVING COUNT(r) > :nombreMinimal")
    List<Object[]> findFacturesWithMultipleRelances(@Param("nombreMinimal") int nombreMinimal);

    // Trouver les relances par statut d'email et période
    @Query("SELECT r FROM RelanceHistory r " +
            "WHERE r.statusEmail = :status " +
            "AND r.dateEnvoi BETWEEN :debut AND :fin " +
            "ORDER BY r.dateEnvoi DESC")
    List<RelanceHistory> findRelancesByStatusAndPeriod(
            @Param("status") String status,
            @Param("debut") LocalDateTime debut,
            @Param("fin") LocalDateTime fin
    );

    // Moyenne des relances par facture
    @Query("SELECT AVG(subquery.count) FROM " +
            "(SELECT COUNT(r) as count FROM RelanceHistory r " +
            "GROUP BY r.facture.factureId) as subquery")
    Double getAverageRelancesPerFacture();
}
