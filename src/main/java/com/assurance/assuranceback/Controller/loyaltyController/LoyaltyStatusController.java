package com.assurance.assuranceback.Controller.loyaltyController;


import com.assurance.assuranceback.DTOs.LoyaltyStatusDTO;
import com.assurance.assuranceback.Services.loyaltyServices.LoyaltyStatusService;
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
@RequestMapping("/api/loyalty/status")
@RequiredArgsConstructor
public class LoyaltyStatusController {

    private final LoyaltyStatusService loyaltyStatusService;

    @GetMapping
    public ResponseEntity<List<LoyaltyStatusDTO>> getAllLoyaltyStatuses() {
        return ResponseEntity.ok(loyaltyStatusService.getAllLoyaltyStatuses());
    }

    @GetMapping("/{id}")
    public ResponseEntity<LoyaltyStatusDTO> getLoyaltyStatusById(@PathVariable Long id) {
        return ResponseEntity.ok(loyaltyStatusService.getLoyaltyStatusById(id));
    }

    @PostMapping

    public ResponseEntity<LoyaltyStatusDTO> createLoyaltyStatus(@Valid @RequestBody LoyaltyStatusDTO loyaltyStatusDTO) {
        return ResponseEntity.status(HttpStatus.CREATED).body(loyaltyStatusService.createLoyaltyStatus(loyaltyStatusDTO));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<LoyaltyStatusDTO> updateLoyaltyStatus(
            @PathVariable Long id,
            @Valid @RequestBody LoyaltyStatusDTO loyaltyStatusDTO) {
        return ResponseEntity.ok(loyaltyStatusService.updateLoyaltyStatus(id, loyaltyStatusDTO));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<?> deleteLoyaltyStatus(@PathVariable Long id) {
        try {
            loyaltyStatusService.deleteLoyaltyStatus(id);
            return ResponseEntity.ok(Map.of("message", "Loyalty status deleted successfully"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }
}