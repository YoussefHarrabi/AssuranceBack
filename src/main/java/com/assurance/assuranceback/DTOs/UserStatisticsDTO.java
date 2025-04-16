package com.assurance.assuranceback.DTOs;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserStatisticsDTO {
    private Map<String, Long> usersByRole;
    private Map<Integer, Long> registrationsByMonth;
    private Map<String, Long> ageDistribution;
    private Map<String, Long> identityTypeDistribution;
    private Map<String, Long> mfaStatusDistribution;
    private Map<String, Long> geographicalDistribution;
    private long totalUsers;
    private long newUsersLastMonth;
    private long activeUsersLastMonth;
    private RecentUsersDTO recentUsers;
}









