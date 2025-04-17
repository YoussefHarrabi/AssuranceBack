package com.assurance.assuranceback.Services.CarrieresServices;

import com.assurance.assuranceback.Entity.CarrieresEntity.Quiz;
import com.assurance.assuranceback.Entity.CarrieresEntity.QuizQuestion;

import java.util.List;

public interface IQuestionService {
  public List<QuizQuestion> retrieveallQuestions();
  public QuizQuestion retrieveQuizQuestionById(long id);
  public QuizQuestion addQuestion(QuizQuestion question);
  public void removeQuestion(long id);
  public QuizQuestion updateQuestion(Long id, QuizQuestion questionDetails);

}
