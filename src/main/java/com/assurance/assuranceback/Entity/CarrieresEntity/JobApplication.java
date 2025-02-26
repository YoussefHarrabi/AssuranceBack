package com.assurance.assuranceback.Entity.CarrieresEntity;

import com.assurance.assuranceback.Entity.UserEntity.User;
import com.assurance.assuranceback.Enum.StatutCandidature;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;
import jakarta.persistence.*;
import org.hibernate.annotations.Where;

import java.time.LocalDateTime;

@Entity
@Setter
@Getter
public class JobApplication {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    //@ManyToOne
   //@JoinColumn(name = "user_id", nullable = false)
   // @Where(clause = "role = 'CANDIDAT'")
   // private User user;

    @ManyToOne
    @JoinColumn(name = "offre_id", nullable = true) // NULL = candidature spontanée
    private JobOffer JobOffer;

    @Enumerated(EnumType.STRING)
    private StatutCandidature statut = StatutCandidature.NOUVELLE;

    private LocalDateTime dateCandidature = LocalDateTime.now();
    private String cvPath; // Chemin du fichier CV

    private String lettreMotivationPath;
    private String email;
}
