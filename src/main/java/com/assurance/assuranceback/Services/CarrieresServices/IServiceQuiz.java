package com.assurance.assuranceback.Services.CarrieresServices;

import com.assurance.assuranceback.Entity.CarrieresEntity.Quiz;

import java.util.List;

public interface IServiceQuiz {
  public List<Quiz> retrieveallQuizs();
  public Quiz retrieveQuizById(long id);
  public Quiz addQuiz(Quiz quiz);
  public void removeQuiz(long id);
  public Quiz updateQuiz(Long id, Quiz quizDetails);
  public Quiz getQuizByJobOfferId(Long jobOfferId);

}
