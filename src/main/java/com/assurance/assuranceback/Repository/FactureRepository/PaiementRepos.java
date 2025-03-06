package com.assurance.assuranceback.Repository.FactureRepository;

import com.assurance.assuranceback.Entity.FactureEntity.Paiement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PaiementRepos extends JpaRepository<Paiement, Long> {

}
