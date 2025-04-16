package com.assurance.assuranceback.Controller.UserController;


import com.assurance.assuranceback.DTOs.UserActivityDTO;
import com.assurance.assuranceback.DTOs.UserStatisticsDTO;
import com.assurance.assuranceback.DTOs.UserSummaryDTO;
import com.assurance.assuranceback.Entity.UserEntity.User;
import com.assurance.assuranceback.Enum.Role;
import com.assurance.assuranceback.Services.UserServices.UserStatisticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin/statistics")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class UserStatisticsController {

    private final UserStatisticsService userStatisticsService;

    @GetMapping("/users")
    public ResponseEntity<UserStatisticsDTO> getUserStatistics() {
        UserStatisticsDTO statistics = userStatisticsService.getUserStatistics();
        return ResponseEntity.ok(statistics);
    }

    @GetMapping("/users/activity")
    public ResponseEntity<UserActivityDTO> getUserActivity(
            @RequestParam(defaultValue = "30") int days) {
        UserActivityDTO activity = userStatisticsService.getUserActivity(days);
        return ResponseEntity.ok(activity);
    }

    @GetMapping("/users/advanced-filter")
    public ResponseEntity<List<UserSummaryDTO>> getAdvancedFilteredUsers(
            @RequestParam(required = false) Role role,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(required = false) Boolean mfaEnabled) {

        List<UserSummaryDTO> users = userStatisticsService.getAdvancedFilteredUsers(role, startDate, endDate, mfaEnabled);
        return ResponseEntity.ok(users);
    }

    @GetMapping("/users/incomplete-profiles")
    public ResponseEntity<Map<String, Object>> getUsersWithoutProfileUpdates() {
        List<User> users = userStatisticsService.getUsersWithoutProfileUpdates();

        // Sanitize user data before returning
        List<Map<String, Object>> sanitizedUsers = users.stream()
                .map(user -> {
                    Map<String, Object> userMap = new HashMap<>();
                    userMap.put("id", user.getId());
                    userMap.put("email", user.getEmail());
                    userMap.put("createdAt", user.getCreatedAt());
                    userMap.put("daysSinceRegistration", LocalDate.now().toEpochDay() - user.getCreatedAt().toEpochDay());
                    return userMap;
                })
                .collect(Collectors.toList());

        Map<String, Object> response = new HashMap<>();
        response.put("count", users.size());
        response.put("users", sanitizedUsers);

        return ResponseEntity.ok(response);
    }

    // Additional endpoint for dashboard overview
    @GetMapping("/dashboard")
    public ResponseEntity<Map<String, Object>> getDashboardStatistics() {
        UserStatisticsDTO fullStats = userStatisticsService.getUserStatistics();

        // Create a simplified dashboard response with key metrics
        Map<String, Object> dashboardStats = Map.of(
                "totalUsers", fullStats.getTotalUsers(),
                "newUsersLastMonth", fullStats.getNewUsersLastMonth(),
                "usersByRole", fullStats.getUsersByRole(),
                "mfaAdoption", calculatePercentage(
                        fullStats.getMfaStatusDistribution().getOrDefault("Enabled", 0L),
                        fullStats.getTotalUsers()
                ),
                "geographicalDistribution", fullStats.getGeographicalDistribution()
        );

        return ResponseEntity.ok(dashboardStats);
    }

    private double calculatePercentage(long value, long total) {
        return total > 0 ? (double) value / total * 100 : 0;
    }
}
