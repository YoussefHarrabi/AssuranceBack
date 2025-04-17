package com.example.gharbipi.repos;

import com.example.gharbipi.entities.InsurancePro;
import com.example.gharbipi.entities.InsuranceProType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
public interface InsuranceProRepository extends JpaRepository<InsurancePro, Long> {
    @Query("SELECT COUNT(i) FROM InsurancePro i WHERE i.insuranceProType = :insuranceProType")
    long countByInsuranceProType(@Param("insuranceProType") InsuranceProType insuranceProType);

    // Total number of InsurancePro objects
    @Query("SELECT COUNT(i) FROM InsurancePro i")
    long countAllInsurancePros();

    // Number of InsurancePro objects per InsuranceProType
    @Query("SELECT i.insuranceProType.name, COUNT(i) FROM InsurancePro i GROUP BY i.insuranceProType.name")
    List<Object[]> countInsuranceProsByType();

    // Percentage of InsurancePro objects per InsuranceProType
    @Query(value = "SELECT t.name, COUNT(*) * 100.0 / (SELECT COUNT(*) FROM insurance_pro) AS percentage " +
            "FROM insurance_pro i " +
            "JOIN insurance_pro_type t ON i.insurance_pro_type_id = t.id " +
            "GROUP BY t.name", nativeQuery = true)
    List<Object[]> percentageInsuranceProsByType();

    // Average premiumAmount per InsuranceProType
    @Query("SELECT i.insuranceProType.name, AVG(i.premiumAmount) FROM InsurancePro i GROUP BY i.insuranceProType.name")
    List<Object[]> averagePremiumAmountByType();

    // Total premiumAmount per InsuranceProType
    @Query("SELECT i.insuranceProType.name, SUM(i.premiumAmount) FROM InsurancePro i GROUP BY i.insuranceProType.name")
    List<Object[]> totalPremiumAmountByType();

    // Highest and lowest premiumAmount per InsuranceProType
    @Query("SELECT i.insuranceProType.name, MAX(i.premiumAmount), MIN(i.premiumAmount) FROM InsurancePro i GROUP BY i.insuranceProType.name")
    List<Object[]> premiumAmountRangeByType();

    // Count of InsurancePro objects by risk level
    @Query("SELECT i.insuranceProType.risk, COUNT(i) FROM InsurancePro i GROUP BY i.insuranceProType.risk")
    List<Object[]> countInsuranceProsByRisk();
}