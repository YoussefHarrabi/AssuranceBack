package com.assurance.assuranceback.Entity.FactureEntity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RelanceHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "facture_id")
    private Facture facture;

    private LocalDateTime dateEnvoi;
    private String statusEmail; // SUCCESS, FAILED
    private String statusPaiement; // EN_ATTENTE, PAYÉ, EN_RETARD
    private int nombreRelances;
    private String dernierEmailEnvoye;
}
