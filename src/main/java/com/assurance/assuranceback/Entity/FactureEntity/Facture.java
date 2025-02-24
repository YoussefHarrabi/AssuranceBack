package com.assurance.assuranceback.Entity.FactureEntity;

import com.assurance.assuranceback.Enum.FactureStatut;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Facture {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long factureId; // 🟢 Corrigé la majuscule pour respecter les conventions Java
    private Long idClient;
    private float montant;
    private String dateEmission;



    @Enumerated(EnumType.STRING)
    private FactureStatut factureStatut;

    // ✅ Relation avec Paiement (Une Facture -> Plusieurs Paiements)
    @OneToMany(mappedBy = "facture", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference // Empêche la boucle infinie lors de la sérialisation
    private List<Paiement> paiements=new ArrayList<>();;
}
