package com.assurance.assuranceback.Entity.CarrieresEntity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Entity
@Setter
@Getter
public class QuizQuestion {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private String correctAnswer;
  private String text;
  private String options;
  @ManyToOne
  @JoinColumn(name = "quiz_id")
  private Quiz quiz;
}
