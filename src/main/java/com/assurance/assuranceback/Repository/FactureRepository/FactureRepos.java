package com.assurance.assuranceback.Repository.FactureRepository;

import com.assurance.assuranceback.Entity.FactureEntity.Facture;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FactureRepos extends JpaRepository<Facture, Long> {

}
