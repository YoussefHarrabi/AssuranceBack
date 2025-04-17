package com.assurance.assuranceback.Entity.CarrieresEntity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Entity
@Setter
@Getter
public class Quiz {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private String title;

  @JsonIgnore
  @OneToMany(mappedBy = "quiz")
  private List<JobOffer> jobOffers;

@JsonIgnore
  @OneToMany(mappedBy = "quiz", cascade = CascadeType.ALL)
  private List<QuizQuestion> questions;

}
