package tn.esprit.examen.nomPrenomClasseExamen.entities;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level= AccessLevel.PRIVATE)
@Entity
public class DemandeAssurance {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String NomAssurance; // Nom et prénom du demandeur
    private String Description; // CIN ou Passeport
    private Long Prix;

    @ManyToOne(cascade = CascadeType.ALL) // This will delete the associated TypeAssurance when DemandeAssurance is deleted
    private TypeAssurance typeAssurance;

    // Dans DemandeAssurance.java
    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private StatusDemande status;

}
