package com.assurance.assuranceback.Controller.loyaltyController;


import com.assurance.assuranceback.DTOs.ChallengeDTO;
import com.assurance.assuranceback.Services.loyaltyServices.ChallengeService;
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
@RequestMapping("/api/loyalty/challenges")
@RequiredArgsConstructor
public class ChallengeController {

    private final ChallengeService challengeService;

    @GetMapping
    public ResponseEntity<List<ChallengeDTO>> getAllChallenges() {
        return ResponseEntity.ok(challengeService.getAllChallenges());
    }

    @GetMapping("/active")
    public ResponseEntity<List<ChallengeDTO>> getActiveChallenges() {
        return ResponseEntity.ok(challengeService.getActiveChallenges());
    }

    @GetMapping("/current")
    public ResponseEntity<List<ChallengeDTO>> getCurrentChallenges() {
        return ResponseEntity.ok(challengeService.getCurrentChallenges());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ChallengeDTO> getChallengeById(@PathVariable Long id) {
        return ResponseEntity.ok(challengeService.getChallengeById(id));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<ChallengeDTO> createChallenge(@Valid @RequestBody ChallengeDTO challengeDTO) {
        return ResponseEntity.status(HttpStatus.CREATED).body(challengeService.createChallenge(challengeDTO));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<ChallengeDTO> updateChallenge(
            @PathVariable Long id,
            @Valid @RequestBody ChallengeDTO challengeDTO) {
        return ResponseEntity.ok(challengeService.updateChallenge(id, challengeDTO));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<?> deleteChallenge(@PathVariable Long id) {
        try {
            challengeService.deleteChallenge(id);
            return ResponseEntity.ok(Map.of("message", "Challenge deleted successfully"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }
}
