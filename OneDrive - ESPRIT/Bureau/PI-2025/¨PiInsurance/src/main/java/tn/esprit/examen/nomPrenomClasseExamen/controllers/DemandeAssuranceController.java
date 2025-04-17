package tn.esprit.examen.nomPrenomClasseExamen.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tn.esprit.examen.nomPrenomClasseExamen.ResourceNotFoundException;
import tn.esprit.examen.nomPrenomClasseExamen.entities.DemandeAssurance;
import tn.esprit.examen.nomPrenomClasseExamen.entities.StatusDemande;
import tn.esprit.examen.nomPrenomClasseExamen.services.IDemandeAssurance;
import tn.esprit.examen.nomPrenomClasseExamen.services.ServiceDemandeAssurance;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@RequestMapping("Insurance")
@org.springframework.web.bind.annotation.RestController
public class DemandeAssuranceController {
    private final IDemandeAssurance services;



    @CrossOrigin(origins = "http://localhost:4200")
    @PostMapping("/add")
    public DemandeAssurance add(@RequestBody DemandeAssurance demandeAssurance){
        return  services.add(demandeAssurance);
    }
    @CrossOrigin(origins = "http://localhost:4200")
    @GetMapping("/all")
    public List<DemandeAssurance> getAll() {
        return services.getAll();
    }

    @CrossOrigin(origins = "http://localhost:4200")
    @GetMapping("/{id}")
    public DemandeAssurance getById(@PathVariable Long id) {
        return services.getById(id);
    }

    @CrossOrigin(origins = "http://localhost:4200")
    @PutMapping("/update/{id}")
    public ResponseEntity<DemandeAssurance> updateDemande(@PathVariable Long id, @RequestBody DemandeAssurance updatedInsurance) {
        try {
            DemandeAssurance updatedDemande = services.update(id, updatedInsurance);
            return ResponseEntity.ok(updatedDemande);  // Retourner la demande mise à jour avec un code HTTP 200
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);  // Demande non trouvée
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);  // Erreur serveur
        }
    }

    @CrossOrigin(origins = "http://localhost:4200")
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> delete(@PathVariable Long id) {
        try {
            services.delete(id);
            return ResponseEntity.ok("Demande supprimée avec succès !");
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erreur lors de la suppression de la demande : " + e.getMessage());
        }
    }

    @CrossOrigin(origins = "http://localhost:4200")
    @PutMapping("/update-status/{id}")
    public ResponseEntity<Map<String, String>> updateStatus(
            @PathVariable Long id,
            @RequestParam String status,
            @RequestParam String email) {
        try {
            StatusDemande statusEnum = StatusDemande.valueOf(status.toUpperCase());
            services.updateStatus(id, statusEnum, email);

            Map<String, String> response = new HashMap<>();
            response.put("message", "Status updated successfully");
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Invalid status value");
            return ResponseEntity.badRequest().body(error);
        }
    }


}
