package com.assurance.assuranceback.Controller.loyaltyController;


import com.assurance.assuranceback.DTOs.BonusDTO;
import com.assurance.assuranceback.Services.loyaltyServices.BonusService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;

@RestController
@CrossOrigin
@RequestMapping("/api/loyalty/bonuses")
@RequiredArgsConstructor
public class BonusController {

    private final BonusService bonusService;

    @GetMapping
    public ResponseEntity<List<BonusDTO>> getAllBonuses() {
        return ResponseEntity.ok(bonusService.getAllBonuses());
    }

    @GetMapping("/available")
    public ResponseEntity<List<BonusDTO>> getAvailableBonuses() {
        return ResponseEntity.ok(bonusService.getAvailableBonuses());
    }

    @GetMapping("/available-with-points/{points}")
    public ResponseEntity<List<BonusDTO>> getBonusesByPoints(@PathVariable Integer points) {
        return ResponseEntity.ok(bonusService.getBonusesByPoints(points));
    }

    @GetMapping("/{id}")
    public ResponseEntity<BonusDTO> getBonusById(@PathVariable Long id) {
        return ResponseEntity.ok(bonusService.getBonusById(id));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<BonusDTO> createBonus(@Valid @RequestBody BonusDTO bonusDTO) {
        return ResponseEntity.status(HttpStatus.CREATED).body(bonusService.createBonus(bonusDTO));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<BonusDTO> updateBonus(
            @PathVariable Long id,
            @Valid @RequestBody BonusDTO bonusDTO) {
        return ResponseEntity.ok(bonusService.updateBonus(id, bonusDTO));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<?> deleteBonus(@PathVariable Long id) {
        try {
            bonusService.deleteBonus(id);
            return ResponseEntity.ok(Map.of("message", "Bonus deleted successfully"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }
}