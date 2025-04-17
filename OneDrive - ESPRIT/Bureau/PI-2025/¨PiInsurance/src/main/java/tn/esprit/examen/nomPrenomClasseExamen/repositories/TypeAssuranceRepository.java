package tn.esprit.examen.nomPrenomClasseExamen.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import tn.esprit.examen.nomPrenomClasseExamen.entities.TypeAssurance;

import java.util.List;
import java.util.Map;

public interface TypeAssuranceRepository extends JpaRepository<TypeAssurance, Long> {
    List<TypeAssurance> findByNomContainingIgnoreCase(String keyword);
    // JPQL query to get the statistics of 'TypeAssurance' by 'nom'
    @Query("SELECT t.nom, COUNT(t) FROM TypeAssurance t GROUP BY t.nom")
    Map<String, Long> getTypeAssuranceStatistics();

}
