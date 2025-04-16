package com.assurance.assuranceback.Controller.FactureController;

import com.assurance.assuranceback.Entity.FactureEntity.Facture;
import com.assurance.assuranceback.Entity.FactureEntity.ResponseMessage;
import com.assurance.assuranceback.Services.FactureService.IFactureService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@AllArgsConstructor
@RequestMapping("/api/factPaim")
public class FactPaimController {

    @Autowired
    private IFactureService iFactureService;

    @PostMapping("/addFacture")
    public ResponseEntity<Facture> addFacture(@RequestBody Facture facture) {
        Facture savedFacture = iFactureService.addFacture(facture);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedFacture);
    }

    @PutMapping("/updateFact/{id}")
    public ResponseEntity<Facture> updateFacture(@PathVariable Long id, @RequestBody Facture updatedFacture) {
        Facture savedFacture = iFactureService.updateFacture(id, updatedFacture);
        return ResponseEntity.ok(savedFacture);
    }

    @GetMapping("/factures")
    public ResponseEntity<List<Facture>> getAllFactures() {
        List<Facture> factures = iFactureService.getAllFactures();
        return ResponseEntity.ok(factures);
    }

    @GetMapping("/factures/{id}")
    public ResponseEntity<Facture> getFactureById(@PathVariable Long id) {
        Optional<Facture> facture = iFactureService.getFactureById(id);
        return facture.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    @DeleteMapping("/factures/{id}")
    public ResponseEntity<Void> deleteFacture(@PathVariable Long id) {
        iFactureService.deleteFacture(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{factureId}/send-notification")
    public ResponseEntity<ResponseMessage> sendUnpaidInvoiceNotification(
            @PathVariable Long factureId) {
        try {
            // Utilisez la date actuelle fournie
            String currentDate = "2025-04-15 19:53:12";

            ResponseMessage response = iFactureService.sendUnpaidInvoiceNotification(factureId, currentDate);

            if (response.getStatus().equals("success")) {
                return ResponseEntity.ok(response);
            } else {
                return ResponseEntity.badRequest().body(response);
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ResponseMessage("error", "Erreur lors de l'envoi de la notification: " + e.getMessage()));
        }
    }
}