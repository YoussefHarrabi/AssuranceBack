package com.assurance.assuranceback.Repository.CarrieresRepositories;

import com.assurance.assuranceback.Entity.CarrieresEntity.QuizQuestion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface QuizQuestionRepository  extends JpaRepository<QuizQuestion, Long> {
}
