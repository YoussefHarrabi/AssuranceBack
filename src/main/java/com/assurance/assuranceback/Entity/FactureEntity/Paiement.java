package com.assurance.assuranceback.Entity.FactureEntity;

import com.assurance.assuranceback.Enum.MethodePaiement;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Paiement {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String datePaiement;
    private float montantPaiement;

    @Enumerated(EnumType.STRING)
    private MethodePaiement methodePaiement;

    // Relation avec Facture (Plusieurs Paiements -> Une Facture)
    @ManyToOne
    @JoinColumn(name = "facture_id") // Nom de la clé étrangère en BDD
    private Facture facture;
}