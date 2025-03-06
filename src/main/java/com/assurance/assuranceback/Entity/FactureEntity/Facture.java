package com.assurance.assuranceback.Entity.FactureEntity;

import com.assurance.assuranceback.Entity.UserEntity.User;
import com.assurance.assuranceback.Enum.FactureStatut;
import com.fasterxml.jackson.annotation.JsonIgnore;
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
    private Long factureId; // Corrigé la majuscule pour respecter les conventions Java
    private float montant;
    private String dateEmission;

    @Enumerated(EnumType.STRING)
    private FactureStatut factureStatut;

    // Relation avec Paiement (Une Facture -> Plusieurs Paiements)
    @OneToMany(mappedBy = "facture", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<Paiement> paiements = new ArrayList<>();

    // Relation avec User (Plusieurs Factures -> Un seul User)
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false) // Clé étrangère dans Facture
    private User user;

    public void addPaiement(Paiement paiement) {
        paiements.add(paiement);
        paiement.setFacture(this);
    }
}