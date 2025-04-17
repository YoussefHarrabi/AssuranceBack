package tn.esprit.examen.nomPrenomClasseExamen.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import tn.esprit.examen.nomPrenomClasseExamen.entities.DemandeAssurance;

public interface InsuranceParticularRepository extends JpaRepository<DemandeAssurance, Long> {
}