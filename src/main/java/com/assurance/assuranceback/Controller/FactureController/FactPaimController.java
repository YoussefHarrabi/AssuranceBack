package com.assurance.assuranceback.Controller.FactureController;


import com.assurance.assuranceback.Entity.FactureEntity.Facture;
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
    public ResponseEntity<Void> addFacture(@RequestBody Facture facture) {
        iFactureService.addFacture(facture);
        return ResponseEntity.status(HttpStatus.CREATED).build(); // Ajout de statut 201 Created
    }

    @PutMapping("/updateFact/{id}")
    public ResponseEntity<Facture> updateFacture(@PathVariable Long id, @RequestBody Facture updatedFacture) {
        iFactureService.updateFacture(id, updatedFacture);
        return ResponseEntity.ok(updatedFacture); // Retourne 200 OK avec la facture mise à jour
    }

    @GetMapping("/factures")
    public ResponseEntity<List<Facture>> getAllFactures() {
        List<Facture> factures = iFactureService.getAllFactures();
        return ResponseEntity.ok(factures); // Retourne 200 OK avec la liste des factures
    }

    @GetMapping("/factures/{id}")
    public ResponseEntity<Facture> getFactureById(@PathVariable Long id) {
        Optional<Facture> facture = iFactureService.getFactureById(id);
        return facture.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build()); // 404 si non trouvé
    }

    @DeleteMapping("/factures/{id}")
    public ResponseEntity<Void> deleteFacture(@PathVariable Long id) {
        iFactureService.deleteFacture(id);
        return ResponseEntity.noContent().build(); // Retourne 204 No Content
    }
}
