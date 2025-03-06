package com.assurance.assuranceback.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SocieteStatsDTO {
    private double montantTotal;
    private double montantPaye;
    private double montantNonPaye;
    private double montantEnAttente;
    private long nombreFacturesPayees;
    private long nombreFacturesNonPayees;
    private long nombreFacturesEnAttente;
    private long nombreTotalFactures;
    private double gainPotentiel;
}
