package com.assurance.assuranceback.Services.CarrieresServices;

import com.assurance.assuranceback.Entity.CarrieresEntity.JobApplication;
import com.assurance.assuranceback.Entity.CarrieresEntity.Quiz;

import java.util.List;

public interface IQuizService {
    public List<Quiz> retrieveallQuizzes();
    public Quiz retrieveQuizById(long id);
    public Quiz addQuiz(Quiz quiz);
    public void removeQuiz(long id);
    public Quiz updateQuiz(Long id, Quiz quizDetails);
}
