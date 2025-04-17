package com.assurance.assuranceback.Services.CarrieresServices;

import com.assurance.assuranceback.Entity.CarrieresEntity.QuizQuestion;
import com.assurance.assuranceback.Repository.CarrieresRepositories.QuizQuestionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class serviceQuestion implements IQuestionService{

  @Autowired
  private QuizQuestionRepository quizQuestionRepository;

  @Override
  public List<QuizQuestion> retrieveallQuestions() {
    // Récupérer toutes les questions du quiz depuis le repository
    return quizQuestionRepository.findAll();
  }

  @Override
  public QuizQuestion retrieveQuizQuestionById(long id) {
    // Récupérer une question par son ID
    Optional<QuizQuestion> question = quizQuestionRepository.findById(id);
    return question.orElseThrow(() -> new RuntimeException("Question introuvable avec l'ID : " + id));
  }

  @Override
  public QuizQuestion addQuestion(QuizQuestion question) {
    // Enregistrer une nouvelle question
    return quizQuestionRepository.save(question);
  }

  @Override
  public void removeQuestion(long id) {
    // Vérifier si la question existe avant de la supprimer
    if (!quizQuestionRepository.existsById(id)) {
      throw new RuntimeException("Impossible de supprimer. Question introuvable avec l'ID : " + id);
    }
    quizQuestionRepository.deleteById(id);
  }

  @Override
  public QuizQuestion updateQuestion(Long id, QuizQuestion questionDetails) {
    // Récupérer la question existante
    QuizQuestion existingQuestion = quizQuestionRepository.findById(id)
      .orElseThrow(() -> new RuntimeException("Question introuvable avec l'ID : " + id));

    // Mettre à jour les détails de la question
    existingQuestion.setText(questionDetails.getText());
    existingQuestion.setCorrectAnswer(questionDetails.getCorrectAnswer());


    // Sauvegarder les modifications
    return quizQuestionRepository.save(existingQuestion);
  }
}
