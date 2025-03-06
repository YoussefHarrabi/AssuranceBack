package com.assurance.assuranceback.Controller.FactureController;

import com.assurance.assuranceback.Entity.FactureEntity.Paiement;
import com.assurance.assuranceback.Services.FactureService.PaiementService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/api/paiement")
public class PaiementController {

    private final PaiementService paiementService;

    @PostMapping("/payer/{factureId}")
    public ResponseEntity<String> payerFacture(@PathVariable long factureId, @RequestBody Paiement paiement) {
        paiementService.payerFacture(factureId, paiement);
        return ResponseEntity.ok("Paiement effectué pour la facture n°" + factureId);
    }

    @GetMapping("/facture/{factureId}")
    public ResponseEntity<List<Paiement>> consulterPaiements(@PathVariable long factureId) {
        List<Paiement> paiements = paiementService.consulterPaiements(factureId);
        return ResponseEntity.ok(paiements);
    }
}