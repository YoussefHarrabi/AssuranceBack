package com.assurance.assuranceback.Entity.CarrieresEntity;

import com.assurance.assuranceback.Entity.UserEntity.User;
import com.assurance.assuranceback.Enum.StatutCandidature;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Where;

import java.time.LocalDateTime;

@Entity
@Setter
@Getter
public class JobApplication {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = true)  // Make sure nullable = true is set
    @Where(clause = "role = 'CANDIDAT'")
    private User user;

    @ManyToOne
    @JoinColumn(name = "offre_id", nullable = true) // NULL = candidature spontanée
    private JobOffer jobOffer;

    @Enumerated(EnumType.STRING)
    private StatutCandidature statut = StatutCandidature.NOUVELLE;

    private LocalDateTime dateCandidature = LocalDateTime.now();
    private String cvPath; // Chemin du fichier CV

    private String LettreMotivationPath ;
    // @Version field to handle optimistic locking
    @Version
    private int version;

    private String email;
    private boolean isPinned;
}