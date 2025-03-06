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
}