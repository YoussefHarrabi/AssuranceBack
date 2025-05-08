package com.assurance.assuranceback.Services.CarrieresServices;

import com.assurance.assuranceback.Entity.CarrieresEntity.JobOffer;
import com.assurance.assuranceback.Entity.CarrieresEntity.Quiz;
import com.assurance.assuranceback.Entity.CarrieresEntity.QuizQuestion;
import com.assurance.assuranceback.Repository.CarrieresRepositories.JobOfferRepository;
import com.assurance.assuranceback.Repository.CarrieresRepositories.QuizQuestionRepository;
import com.assurance.assuranceback.Repository.CarrieresRepositories.QuizRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;


import java.util.List;

@Service
public class ServiceQuiz implements IServiceQuiz {

  @Autowired
  private QuizQuestionRepository quizQuestionRepository;

  @Autowired
  private QuizRepository quizRepository;

  @Autowired
  private JobOfferRepository jobOfferRepository;

  @Override
  public List<Quiz> retrieveallQuizs() {
    // Récupérer tous les quiz
    return quizRepository.findAll();
  }

  @Override
  public Quiz retrieveQuizById(long id) {
    return quizRepository.findById(id)
      .orElse(null);

  }



  @Override
  public Quiz addQuiz(Quiz quiz) {
    // Ajouter un nouveau quiz avec ses questions associées
    if (quiz.getQuestions() != null) {
      for (QuizQuestion question : quiz.getQuestions()) {
        question.setQuiz(quiz);
      }
    }
    return quizRepository.save(quiz);
  }

  @Override
  public void removeQuiz(long id) {
    // Vérifier si le quiz existe avant de le supprimer
    if (!quizRepository.existsById(id)) {
      throw new RuntimeException("Impossible de supprimer. Quiz introuvable avec l'ID : " + id);
    }

    // Supprimer le quiz
    quizRepository.deleteById(id);
  }

  @Override
  public Quiz updateQuiz(Long id, Quiz quizDetails) {
    // Récupérer le quiz existant
    Quiz existingQuiz = quizRepository.findById(id)
      .orElseThrow(() -> new RuntimeException("Quiz introuvable avec l'ID : " + id));

    // Mettre à jour les détails du quiz
    existingQuiz.setTitle(quizDetails.getTitle());

    // Mettre à jour les questions
    if (quizDetails.getQuestions() != null) {
      // Supprimer les anciennes questions liées à ce quiz
      quizQuestionRepository.deleteAll(existingQuiz.getQuestions());

      // Ajouter les nouvelles questions
      for (QuizQuestion question : quizDetails.getQuestions()) {
        question.setQuiz(existingQuiz);
        quizQuestionRepository.save(question);
      }

      // Mettre à jour la liste des questions dans le quiz
      existingQuiz.setQuestions(quizDetails.getQuestions());
    }

    // Sauvegarder les modifications
    return quizRepository.save(existingQuiz);
  }

 @Override
  public Quiz getQuizByJobOfferId(Long jobOfferId) {
    /*JobOffer jobOffer = jobOfferRepository.findById(jobOfferId)
      .orElseThrow(() -> new ResponseStatusException(
        HttpStatus.NOT_FOUND,
        "Job offer not found with id: " + jobOfferId
      ));

    List<Quiz> quizzes = jobOffer.getQuizzes();
    if (quizzes.isEmpty()) {
      throw new ResponseStatusException(
        HttpStatus.NOT_FOUND,
        "No quizzes associated with this job offer"
      );
    }*/
    return null;
  }
}
