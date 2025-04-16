package com.assurance.assuranceback.Controller;


import com.assurance.assuranceback.Controller.UserController.ProfileController;
import com.assurance.assuranceback.Entity.UserEntity.User;
import com.assurance.assuranceback.Repository.UserRepositories.UserRepository;
import com.assurance.assuranceback.Services.UserServices.MfaService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/test-profile")
public class TestProfileController {

    private static final Logger logger = LoggerFactory.getLogger(ProfileController.class);

    private final UserRepository userRepository;
    private final MfaService mfaService;
    private final PasswordEncoder passwordEncoder;

    /**
     * Get the current user profile information
     */
    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUserProfile() {
        logger.info("Profile /me endpoint called");

        // Get authentication from security context
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth == null || !auth.isAuthenticated()) {
            logger.warn("No authentication found in security context");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "User not authenticated"));
        }

        String email = auth.getName();
        logger.info("Looking up user with email: {}", email);

        try {
            // Find user by email
            User user = userRepository.findByEmail(email)
                    .orElse(null);

            if (user == null) {
                logger.warn("User not found for email: {}", email);
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("message", "User not found"));
            }

            logger.info("User found: {}", user.getEmail());

            // Check if MFA is enabled
            boolean mfaEnabled = mfaService.isMfaEnabled(user);

            // Build response
            Map<String, Object> response = new HashMap<>();
            response.put("id", user.getId());
            response.put("firstName", user.getFirstName());
            response.put("lastName", user.getLastName());
            response.put("email", user.getEmail());
            response.put("birthday", user.getBirthday());
            response.put("phoneNumber", user.getPhoneNumber());
            response.put("address", user.getAddress());
            response.put("roles", user.getRoles());
            response.put("createdAt", user.getCreatedAt());
            response.put("updatedAt", user.getUpdatedAt());
            response.put("mfaEnabled", mfaEnabled);

            // System information
            response.put("currentDateTime", getCurrentUtcDateTime());
            response.put("username", user.getFirstName() + " " + user.getLastName());

            logger.info("Successfully returning user profile");
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            logger.error("Error fetching user profile: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "Error fetching user profile: " + e.getMessage()));
        }
    }

    /**
     * Update basic profile information
     */
    @PutMapping("/me")
    public ResponseEntity<?> updateProfile(@RequestBody Map<String, Object> updates) {
        logger.info("Profile update endpoint called");

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth == null || !auth.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "User not authenticated"));
        }

        String email = auth.getName();

        try {
            // Find user by email
            User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("User not found with email: " + email));

            // Update fields if provided
            if (updates.containsKey("firstName")) {
                user.setFirstName((String) updates.get("firstName"));
            }

            if (updates.containsKey("lastName")) {
                user.setLastName((String) updates.get("lastName"));
            }

            if (updates.containsKey("phoneNumber")) {
                user.setPhoneNumber((String) updates.get("phoneNumber"));
            }

            if (updates.containsKey("address")) {
                user.setAddress((String) updates.get("address"));
            }

            // Save updated user
            User updatedUser = userRepository.save(user);

            // Return success response
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Profile updated successfully");
            response.put("user", Map.of(
                    "id", updatedUser.getId(),
                    "firstName", updatedUser.getFirstName(),
                    "lastName", updatedUser.getLastName(),
                    "email", updatedUser.getEmail(),
                    "phoneNumber", updatedUser.getPhoneNumber(),
                    "address", updatedUser.getAddress()
            ));

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Error updating profile: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "Error updating profile: " + e.getMessage()));
        }
    }

    /**
     * Change password
     */
    @PutMapping("/change-password")
    public ResponseEntity<?> changePassword(@RequestBody Map<String, String> passwords) {
        logger.info("Change password endpoint called");

        String currentPassword = passwords.get("currentPassword");
        String newPassword = passwords.get("newPassword");

        if (currentPassword == null || newPassword == null) {
            return ResponseEntity.badRequest()
                    .body(Map.of("message", "Current password and new password are required"));
        }

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth == null || !auth.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "User not authenticated"));
        }

        String email = auth.getName();

        try {
            // Find user by email
            User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("User not found with email: " + email));

            // Verify current password
            if (!passwordEncoder.matches(currentPassword, user.getPassword())) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("message", "Current password is incorrect"));
            }

            // Update password
            user.setPassword(passwordEncoder.encode(newPassword));
            userRepository.save(user);

            return ResponseEntity.ok(Map.of("message", "Password changed successfully"));
        } catch (Exception e) {
            logger.error("Error changing password: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "Error changing password: " + e.getMessage()));
        }
    }

    /**
     * Get system information
     */
    @GetMapping("/system-info")
    public ResponseEntity<?> getSystemInfo() {
        logger.info("System info endpoint called");

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth == null || !auth.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "User not authenticated"));
        }

        String email = auth.getName();

        try {
            // Find user
            User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("User not found with email: " + email));

            Map<String, Object> systemInfo = new HashMap<>();
            systemInfo.put("currentDateTime", getCurrentUtcDateTime());
            systemInfo.put("username", user.getFirstName() + " " + user.getLastName());
            systemInfo.put("serverVersion", "1.0.0");

            return ResponseEntity.ok(systemInfo);
        } catch (Exception e) {
            logger.error("Error getting system information: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "Error getting system information: " + e.getMessage()));
        }
    }

    /**
     * Get login activity (placeholder implementation)
     */
    @GetMapping("/login-activity")
    public ResponseEntity<?> getLoginActivity() {
        logger.info("Login activity endpoint called");

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth == null || !auth.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "User not authenticated"));
        }

        String email = auth.getName();

        try {
            // Find user
            User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("User not found with email: " + email));

            // You would typically retrieve this from a database table that logs login events
            Map<String, Object> activity = new HashMap<>();
            activity.put("user_id", user.getId());
            activity.put("recent_logins", Map.of(
                    "last_login", getCurrentUtcDateTime(),
                    "last_login_ip", "192.168.1.1",
                    "login_count_last_30_days", 5
            ));

            return ResponseEntity.ok(activity);
        } catch (Exception e) {
            logger.error("Error fetching login activity: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "Error fetching login activity: " + e.getMessage()));
        }
    }

    /**
     * Helper method to get current UTC datetime in the required format: 2025-03-06 05:44:54
     */
    private String getCurrentUtcDateTime() {
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return now.format(formatter);
    }
}