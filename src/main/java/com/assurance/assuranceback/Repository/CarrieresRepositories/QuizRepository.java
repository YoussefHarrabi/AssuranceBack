package com.assurance.assuranceback.Repository.CarrieresRepositories;

import com.assurance.assuranceback.Entity.CarrieresEntity.Quiz;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface QuizRepository extends JpaRepository<Quiz, Long> {
}
