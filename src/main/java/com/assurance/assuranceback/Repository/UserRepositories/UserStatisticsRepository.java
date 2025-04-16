package com.assurance.assuranceback.Repository.UserRepositories;


import com.assurance.assuranceback.Entity.UserEntity.User;
import com.assurance.assuranceback.Enum.IdentityType;
import com.assurance.assuranceback.Enum.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Repository
public interface UserStatisticsRepository extends JpaRepository<User, Long> {

    // Count users by role
    @Query("SELECT u.roles as role, COUNT(u) as count FROM User u JOIN u.roles r GROUP BY r")
    List<Object[]> countUsersByRole();

    // Count users registered per month in a given year
    @Query("SELECT MONTH(u.createdAt) as month, COUNT(u) as count FROM User u " +
            "WHERE YEAR(u.createdAt) = :year GROUP BY MONTH(u.createdAt) ORDER BY MONTH(u.createdAt)")
    List<Object[]> countUsersRegisteredByMonth(@Param("year") int year);

    // Get age distribution (ranges)
    @Query("SELECT " +
            "CASE " +
            "  WHEN YEAR(CURRENT_DATE) - YEAR(u.birthday) < 18 THEN 'Under 18' " +
            "  WHEN YEAR(CURRENT_DATE) - YEAR(u.birthday) BETWEEN 18 AND 24 THEN '18-24' " +
            "  WHEN YEAR(CURRENT_DATE) - YEAR(u.birthday) BETWEEN 25 AND 34 THEN '25-34' " +
            "  WHEN YEAR(CURRENT_DATE) - YEAR(u.birthday) BETWEEN 35 AND 44 THEN '35-44' " +
            "  WHEN YEAR(CURRENT_DATE) - YEAR(u.birthday) BETWEEN 45 AND 54 THEN '45-54' " +
            "  WHEN YEAR(CURRENT_DATE) - YEAR(u.birthday) BETWEEN 55 AND 64 THEN '55-64' " +
            "  ELSE '65+' " +
            "END as ageGroup, COUNT(u) as count " +
            "FROM User u GROUP BY " +
            "CASE " +
            "  WHEN YEAR(CURRENT_DATE) - YEAR(u.birthday) < 18 THEN 'Under 18' " +
            "  WHEN YEAR(CURRENT_DATE) - YEAR(u.birthday) BETWEEN 18 AND 24 THEN '18-24' " +
            "  WHEN YEAR(CURRENT_DATE) - YEAR(u.birthday) BETWEEN 25 AND 34 THEN '25-34' " +
            "  WHEN YEAR(CURRENT_DATE) - YEAR(u.birthday) BETWEEN 35 AND 44 THEN '35-44' " +
            "  WHEN YEAR(CURRENT_DATE) - YEAR(u.birthday) BETWEEN 45 AND 54 THEN '45-54' " +
            "  WHEN YEAR(CURRENT_DATE) - YEAR(u.birthday) BETWEEN 55 AND 64 THEN '55-64' " +
            "  ELSE '65+' " +
            "END ORDER BY " +
            "CASE " +
            "  WHEN YEAR(CURRENT_DATE) - YEAR(u.birthday) < 18 THEN 'Under 18' " +
            "  WHEN YEAR(CURRENT_DATE) - YEAR(u.birthday) BETWEEN 18 AND 24 THEN '18-24' " +
            "  WHEN YEAR(CURRENT_DATE) - YEAR(u.birthday) BETWEEN 25 AND 34 THEN '25-34' " +
            "  WHEN YEAR(CURRENT_DATE) - YEAR(u.birthday) BETWEEN 35 AND 44 THEN '35-44' " +
            "  WHEN YEAR(CURRENT_DATE) - YEAR(u.birthday) BETWEEN 45 AND 54 THEN '45-54' " +
            "  WHEN YEAR(CURRENT_DATE) - YEAR(u.birthday) BETWEEN 55 AND 64 THEN '55-64' " +
            "  ELSE '65+' " +
            "END")
    List<Object[]> getUserAgeDistribution();

    // Get identity type distribution
    @Query("SELECT u.identityType as type, COUNT(u) as count FROM User u GROUP BY u.identityType")
    List<Object[]> getUsersByIdentityType();

    // Get users activity by creation date (last X days)
    @Query("SELECT u.createdAt as date, COUNT(u) as count FROM User u " +
            "WHERE u.createdAt >= :startDate GROUP BY u.createdAt ORDER BY u.createdAt")
    List<Object[]> getUserActivityByDate(@Param("startDate") LocalDate startDate);

    // Get users with MFA enabled vs disabled
    @Query("SELECT " +
            "CASE WHEN u.mfaInfo IS NULL OR u.mfaInfo.enabled = false THEN false ELSE true END as mfaEnabled, " +
            "COUNT(u) as count " +
            "FROM User u GROUP BY mfaEnabled")
    List<Object[]> getUsersByMfaStatus();

    // Get users who haven't updated their profile since registration
    @Query("SELECT u FROM User u WHERE u.updatedAt IS NULL OR u.updatedAt = u.createdAt")
    List<User> getUsersWithoutProfileUpdates();

    // Complex query: Get users with a specific role who registered within a date range and have MFA enabled
    @Query("SELECT u FROM User u JOIN u.roles r " +
            "WHERE r = :role " +
            "AND u.createdAt BETWEEN :startDate AND :endDate " +
            "AND EXISTS (SELECT m FROM MfaInfo m WHERE m.user = u AND m.enabled = true)")
    List<User> findUsersByRoleRegistrationDateRangeWithMfa(
            @Param("role") Role role,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);

    // Get most recent users up to a limit
    @Query("SELECT u FROM User u ORDER BY u.createdAt DESC")
    List<User> findMostRecentUsers(int limit);

    // Get geographical distribution by address containing region/city
    @Query("SELECT " +
            "CASE " +
            "  WHEN u.address LIKE %:region1% THEN :region1 " +
            "  WHEN u.address LIKE %:region2% THEN :region2 " +
            "  WHEN u.address LIKE %:region3% THEN :region3 " +
            "  WHEN u.address LIKE %:region4% THEN :region4 " +
            "  ELSE 'Other' " +
            "END as region, COUNT(u) as count " +
            "FROM User u WHERE u.address IS NOT NULL GROUP BY region ORDER BY count DESC")
    List<Object[]> getUserGeographicalDistribution(
            @Param("region1") String region1,
            @Param("region2") String region2,
            @Param("region3") String region3,
            @Param("region4") String region4);
}