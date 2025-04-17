package com.assurance.assuranceback.Controller.CarrieresController;

import com.assurance.assuranceback.Entity.CarrieresEntity.QuizQuestion;

import com.assurance.assuranceback.Services.CarrieresServices.IQuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/questions")
public class QuestionController {

@Autowired
IQuestionService serviceQuestion;

  // Récupérer toutes les questions
  @GetMapping
  public ResponseEntity<List<QuizQuestion>> getAllQuestions() {
    List<QuizQuestion> questions = serviceQuestion.retrieveallQuestions();
    return ResponseEntity.ok(questions);
  }

  // Récupérer une question par ID
  @GetMapping("/{id}")
  public ResponseEntity<QuizQuestion> getQuestionById(@PathVariable long id) {
    QuizQuestion question = serviceQuestion.retrieveQuizQuestionById(id);
    return ResponseEntity.ok(question);
  }

  // Ajouter une nouvelle question
  @PostMapping
  public ResponseEntity<QuizQuestion> addQuestion(@RequestBody QuizQuestion question) {
    QuizQuestion createdQuestion = serviceQuestion.addQuestion(question);
    return ResponseEntity.ok(createdQuestion);
  }

  // Mettre à jour une question existante
  @PutMapping("/{id}")
  public ResponseEntity<QuizQuestion> updateQuestion(
    @PathVariable Long id,
    @RequestBody QuizQuestion questionDetails) {
    QuizQuestion updatedQuestion = serviceQuestion.updateQuestion(id, questionDetails);
    return ResponseEntity.ok(updatedQuestion);
  }

  // Supprimer une question
  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteQuestion(@PathVariable long id) {
    serviceQuestion.removeQuestion(id);
    return ResponseEntity.noContent().build();
  }
}
