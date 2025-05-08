package com.assurance.assuranceback.Entity.CarrieresEntity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
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
@OneToMany(mappedBy = "quiz", fetch = FetchType.EAGER) // EAGER pour charger les questions automatiquement
private List<QuizQuestion> questions = new ArrayList<>();

}
