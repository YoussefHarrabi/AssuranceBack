package com.assurance.assuranceback.Services.UserServices;


import com.assurance.assuranceback.DTOs.*;
import com.assurance.assuranceback.Entity.UserEntity.User;
import com.assurance.assuranceback.Enum.Role;
import com.assurance.assuranceback.Repository.UserRepositories.UserStatisticsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserStatisticsService {

    private final UserStatisticsRepository userStatisticsRepository;
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public UserStatisticsDTO getUserStatistics() {
        long totalUsers = userStatisticsRepository.count();
        LocalDate oneMonthAgo = LocalDate.now().minusMonths(1);

        // Convert JPQL query results into maps
        Map<String, Long> usersByRole = convertObjectArrayToMap(userStatisticsRepository.countUsersByRole(),
                obj -> obj[0].toString(), obj -> (Long) obj[1]);

        Map<Integer, Long> registrationsByMonth = convertObjectArrayToMap(
                userStatisticsRepository.countUsersRegisteredByMonth(LocalDate.now().getYear()),
                obj -> (Integer) obj[0], obj -> (Long) obj[1]);

        Map<String, Long> ageDistribution = convertObjectArrayToMap(
                userStatisticsRepository.getUserAgeDistribution(),
                obj -> (String) obj[0], obj -> (Long) obj[1]);

        Map<String, Long> identityTypeDistribution = convertObjectArrayToMap(
                userStatisticsRepository.getUsersByIdentityType(),
                obj -> ((Object)obj[0]).toString(), obj -> (Long) obj[1]);

        Map<String, Long> mfaStatusDistribution = convertObjectArrayToMap(
                userStatisticsRepository.getUsersByMfaStatus(),
                obj -> ((Boolean) obj[0] ? "Enabled" : "Disabled"), obj -> (Long) obj[1]);

        Map<String, Long> geographicalDistribution = convertObjectArrayToMap(
                userStatisticsRepository.getUserGeographicalDistribution("Tunis", "Sousse", "Sfax", "Monastir"),
                obj -> (String) obj[0], obj -> (Long) obj[1]);

        // Calculate recent statistics
        long newUsersLastMonth = userStatisticsRepository.getUserActivityByDate(oneMonthAgo)
                .stream()
                .mapToLong(obj -> (Long) obj[1])
                .sum();

        // Get recent users
        List<User> recentUsersList = userStatisticsRepository.findMostRecentUsers(10);

        RecentUsersDTO recentUsers = RecentUsersDTO.builder()
                .users(recentUsersList.stream()
                        .map(this::convertToUserSummaryDTO)
                        .collect(Collectors.toList()))
                .count(recentUsersList.size())
                .build();

        return UserStatisticsDTO.builder()
                .usersByRole(usersByRole)
                .registrationsByMonth(registrationsByMonth)
                .ageDistribution(ageDistribution)
                .identityTypeDistribution(identityTypeDistribution)
                .mfaStatusDistribution(mfaStatusDistribution)
                .geographicalDistribution(geographicalDistribution)
                .totalUsers(totalUsers)
                .newUsersLastMonth(newUsersLastMonth)
                .activeUsersLastMonth(newUsersLastMonth) // As an approximation
                .recentUsers(recentUsers)
                .build();
    }

    public UserActivityDTO getUserActivity(int days) {
        LocalDate startDate = LocalDate.now().minusDays(days);

        List<Object[]> dailyActivity = userStatisticsRepository.getUserActivityByDate(startDate);
        List<DateCountDTO> dailyData = dailyActivity.stream()
                .map(obj -> new DateCountDTO(
                        ((LocalDate) obj[0]).format(DATE_FORMATTER),
                        (Long) obj[1]))
                .collect(Collectors.toList());

        List<Object[]> monthlyActivity = userStatisticsRepository.countUsersRegisteredByMonth(LocalDate.now().getYear());
        List<MonthCountDTO> monthlyData = monthlyActivity.stream()
                .map(obj -> new MonthCountDTO(
                        (Integer) obj[0],
                        (Long) obj[1]))
                .collect(Collectors.toList());

        return UserActivityDTO.builder()
                .dailyActivity(dailyData)
                .monthlyActivity(monthlyData)
                .build();
    }

    public List<UserSummaryDTO> getAdvancedFilteredUsers(Role role, LocalDate startDate, LocalDate endDate, Boolean mfaEnabled) {
        List<User> filteredUsers;

        if (role != null && startDate != null && endDate != null && mfaEnabled != null && mfaEnabled) {
            filteredUsers = userStatisticsRepository.findUsersByRoleRegistrationDateRangeWithMfa(role, startDate, endDate);
        } else {
            // Default case - get recent users as fallback
            filteredUsers = userStatisticsRepository.findMostRecentUsers(20);
        }

        return filteredUsers.stream()
                .map(this::convertToUserSummaryDTO)
                .collect(Collectors.toList());
    }

    public List<User> getUsersWithoutProfileUpdates() {
        return userStatisticsRepository.getUsersWithoutProfileUpdates();
    }

    // Helper conversion methods
    private UserSummaryDTO convertToUserSummaryDTO(User user) {
        return UserSummaryDTO.builder()
                .id(user.getId())
                .email(user.getEmail())
                .fullName(user.getFirstName() + " " + user.getLastName())
                .phoneNumber(user.getPhoneNumber())
                .createdAt(user.getCreatedAt().format(DATE_FORMATTER))
                .roles(user.getRoles().stream()
                        .map(Enum::name)
                        .collect(Collectors.toSet()))
                .mfaEnabled(user.isMfaEnabled())
                .build();
    }

    private <K, V> Map<K, V> convertObjectArrayToMap(List<Object[]> data,
                                                     java.util.function.Function<Object[], K> keyMapper,
                                                     java.util.function.Function<Object[], V> valueMapper) {
        return data.stream()
                .collect(Collectors.toMap(
                        keyMapper,
                        valueMapper,
                        (v1, v2) -> v1, // In case of duplicate keys
                        LinkedHashMap::new // To preserve order
                ));
    }
}