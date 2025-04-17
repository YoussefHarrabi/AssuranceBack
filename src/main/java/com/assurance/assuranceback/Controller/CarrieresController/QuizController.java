package com.assurance.assuranceback.Controller.CarrieresController;
import com.assurance.assuranceback.Entity.CarrieresEntity.JobOffer;
import com.assurance.assuranceback.Entity.CarrieresEntity.Quiz;
import com.assurance.assuranceback.Services.CarrieresServices.IJobOfferService;
import com.assurance.assuranceback.Services.CarrieresServices.IServiceQuiz;
import com.assurance.assuranceback.Services.CarrieresServices.ServiceQuiz;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/quizzes")
public class QuizController {
  @Autowired
  IServiceQuiz serviceQuiz;

  @GetMapping
  public ResponseEntity<List<Quiz>> getAllQuizzes() {
    List<Quiz> quizzes = serviceQuiz.retrieveallQuizs();
    return ResponseEntity.ok(quizzes);
  }

  // Créer un nouveau quiz
  @PostMapping
  public ResponseEntity<Quiz> createQuiz(@RequestBody Quiz quiz) {
    Quiz createdQuiz = serviceQuiz.addQuiz(quiz);
    return ResponseEntity.ok(createdQuiz);
  }


  @GetMapping("/{id}")
  public ResponseEntity<Quiz> getQuizById(@PathVariable Long id) {
    Quiz quiz = serviceQuiz.retrieveQuizById(id);
    if (quiz == null) {
      return ResponseEntity.notFound().build(); // <-- Retourne 404 si le quiz n'existe pas
    }
    return ResponseEntity.ok(quiz); // <-- Sinon, retourne 200 + JSON
  }

}
