package com.assurance.assuranceback.Controller.loyaltyController;


import com.assurance.assuranceback.Services.loyaltyServices.UserLoyaltyService;
import com.assurance.assuranceback.security.JwtUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@CrossOrigin
@RequestMapping("/api/loyalty/user")
@RequiredArgsConstructor
public class UserLoyaltyController {

    private final UserLoyaltyService userLoyaltyService;
    private final JwtUtils jwtUtils;

    // Get current user's loyalty information
    @GetMapping("/info")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> getCurrentUserLoyaltyInfo(@RequestHeader("Authorization") String authHeader) {
        try {
            Long userId = extractUserIdFromToken(authHeader);
            return ResponseEntity.ok(userLoyaltyService.getUserLoyaltyInfo(userId));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    // Complete a challenge for the current user
    @PostMapping("/complete-challenge/{challengeId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> completeChallenge(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable Long challengeId) {
        try {
            Long userId = extractUserIdFromToken(authHeader);
            return ResponseEntity.ok(userLoyaltyService.completeChallenge(userId, challengeId));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    // Redeem a bonus for the current user
    @PostMapping("/redeem-bonus/{bonusId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> redeemBonus(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable Long bonusId) {
        try {
            Long userId = extractUserIdFromToken(authHeader);
            return ResponseEntity.ok(userLoyaltyService.redeemBonus(userId, bonusId));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    // Admin endpoint to complete a challenge for a specific user
    @PostMapping("/admin/complete-challenge/{userId}/{challengeId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> adminCompleteChallenge(
            @PathVariable Long userId,
            @PathVariable Long challengeId) {
        try {
            return ResponseEntity.ok(userLoyaltyService.completeChallenge(userId, challengeId));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    // Admin endpoint to get loyalty info for a specific user
    @GetMapping("/admin/info/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> getSpecificUserLoyaltyInfo(@PathVariable Long userId) {
        try {
            return ResponseEntity.ok(userLoyaltyService.getUserLoyaltyInfo(userId));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    // Helper method to extract user ID from JWT token
    private Long extractUserIdFromToken(String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new RuntimeException("Invalid or missing Authorization header");
        }

        String token = authHeader.substring(7).trim();
        return jwtUtils.extractUserId(token);
    }
}