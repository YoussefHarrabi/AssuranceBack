package com.example.gharbipi.services;

import com.example.gharbipi.entities.StatisticsDTO;
import com.example.gharbipi.repos.InsuranceProRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class StatsService {

    @Autowired
    private InsuranceProRepository insuranceProRepository;

    // Fetch all statistics
    public StatisticsDTO getAllStatistics() {
        StatisticsDTO statistics = new StatisticsDTO();

        // Total number of InsurancePro objects
        statistics.setTotalInsurancePros(insuranceProRepository.countAllInsurancePros());

        // Number of InsurancePro objects per InsuranceProType
        statistics.setCountByType(insuranceProRepository.countInsuranceProsByType()
                .stream()
                .collect(Collectors.toMap(
                        result -> (String) result[0], // InsuranceProType name
                        result -> (Long) result[1]     // Count
                )));

        // Percentage of InsurancePro objects per InsuranceProType
        statistics.setPercentageByType(insuranceProRepository.percentageInsuranceProsByType()
                .stream()
                .collect(Collectors.toMap(
                        result -> (String) result[0], // InsuranceProType name
                        result -> ((Number) result[1]).doubleValue()  // Handle both Double and BigDecimal
                )));

        // Average premiumAmount per InsuranceProType
        statistics.setAveragePremiumByType(insuranceProRepository.averagePremiumAmountByType()
                .stream()
                .collect(Collectors.toMap(
                        result -> (String) result[0], // InsuranceProType name
                        result -> ((Number) result[1]).doubleValue()  // Handle both Double and BigDecimal
                )));

        // Total premiumAmount per InsuranceProType
        statistics.setTotalPremiumByType(insuranceProRepository.totalPremiumAmountByType()
                .stream()
                .collect(Collectors.toMap(
                        result -> (String) result[0], // InsuranceProType name
                        result -> ((Number) result[1]).doubleValue()  // Handle both Double and BigDecimal
                )));

        // Highest and lowest premiumAmount per InsuranceProType
        statistics.setPremiumRangeByType(insuranceProRepository.premiumAmountRangeByType()
                .stream()
                .collect(Collectors.toMap(
                        result -> (String) result[0], // InsuranceProType name
                        result -> Map.of(
                                "max", ((Number) result[1]).doubleValue(), // Handle both Double and BigDecimal
                                "min", ((Number) result[2]).doubleValue()  // Handle both Double and BigDecimal
                        )
                )));

        // Count of InsurancePro objects by risk level
        statistics.setCountByRisk(insuranceProRepository.countInsuranceProsByRisk()
                .stream()
                .collect(Collectors.toMap(
                        result -> (String) result[0], // Risk level
                        result -> (Long) result[1]    // Count
                )));

        return statistics;
    }
}