package tn.esprit.examen.nomPrenomClasseExamen.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tn.esprit.examen.nomPrenomClasseExamen.entities.TypeAssurance;
import tn.esprit.examen.nomPrenomClasseExamen.services.TypeAssuranceServiceImpl;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@org.springframework.web.bind.annotation.RestController
@RequestMapping("typeAssurance")
@RequiredArgsConstructor
public class TypeAssuranceController {

    private final TypeAssuranceServiceImpl typeAssuranceService;
    @CrossOrigin(origins = "http://localhost:4200")
    @GetMapping("/All")
    public ResponseEntity<List<TypeAssurance>> getAll() {
        return ResponseEntity.ok(typeAssuranceService.getAllTypeAssurances());
    }
    @CrossOrigin(origins = "http://localhost:4200")
    @GetMapping("/{id}")
    public ResponseEntity<TypeAssurance> getById(@PathVariable Long id) {
        Optional<TypeAssurance> typeAssurance = typeAssuranceService.getTypeAssuranceById(id);
        return typeAssurance.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
    @CrossOrigin(origins = "http://localhost:4200")
    @PostMapping("/Add")  // <-- Correction ici
    public ResponseEntity<TypeAssurance> createTypeAssurance(@RequestBody TypeAssurance typeAssurance) {
        return ResponseEntity.ok(typeAssuranceService.createTypeAssurance(typeAssurance));
    }

    @CrossOrigin(origins = "http://localhost:4200")
    @PutMapping("/{id}")
    public ResponseEntity<TypeAssurance> update(@PathVariable Long id, @RequestBody TypeAssurance typeAssurance) {
        return ResponseEntity.ok(typeAssuranceService.updateTypeAssurance(id, typeAssurance));
    }

    @CrossOrigin(origins = "http://localhost:4200")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        boolean deleted = typeAssuranceService.deleteTypeAssurance(id);
        if (deleted) {
            return ResponseEntity.ok().build(); // ✅ Success
        } else {
            return ResponseEntity.notFound().build(); // ❌ ID not found
        }
    }





    @CrossOrigin(origins = "http://localhost:4200")
    @GetMapping("/search")
    public ResponseEntity<List<TypeAssurance>> search(@RequestParam String keyword) {
        return ResponseEntity.ok(typeAssuranceService.searchByKeyword(keyword));
    }
    @CrossOrigin(origins = "http://localhost:4200")
    @GetMapping("/statistics")
    public Map<String, Long> getTypeAssuranceStatistics() {
        return typeAssuranceService.getTypeAssuranceStatistics();
    }

}
