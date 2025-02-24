package com.assurance.assuranceback.Services.CarrieresServices;

import com.assurance.assuranceback.Entity.CarrieresEntity.Quiz;
import com.assurance.assuranceback.Repository.CarrieresRepositories.QuizRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ServiceQuiz implements IQuizService {
   @Autowired
    QuizRepository quizRepository;
    @Override
    public List<Quiz> retrieveallQuizzes() {
        return quizRepository.findAll();
    }
    @Override
    public Quiz retrieveQuizById(long id) {
        return quizRepository.findById(id).get();
    }
    @Override
    public Quiz addQuiz(Quiz quiz) {
        return quizRepository.save(quiz);
    }

    @Override
    public void removeQuiz(long id) {
     quizRepository.deleteById(id);
    }

    @Override
    public Quiz updateQuiz(Long id, Quiz quizDetails) {
        Optional<Quiz> quizOptional = quizRepository.findById(id);

        if (quizOptional.isPresent()) {
            Quiz quiz = quizOptional.get();

            // Update properties using getter and setter methods only if they are not null
            if (quizDetails.getJobApplication() != null) {
                quiz.setJobApplication(quizDetails.getJobApplication());
            }
            if (quizDetails.getLienQuiz() != null) {
                quiz.setLienQuiz(quizDetails.getLienQuiz());
            }
            if (quizDetails.getScore() != null) {
                quiz.setScore(quizDetails.getScore());
            }
            // Save updated quiz
            return quizRepository.save(quiz);
        }
        return null;  // Or handle not found case
    };
}
