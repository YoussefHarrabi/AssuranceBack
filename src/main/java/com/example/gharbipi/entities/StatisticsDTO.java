package com.example.gharbipi.entities;

import java.util.Map;

public class StatisticsDTO {
    private long totalInsurancePros;
    private Map<String, Long> countByType;
    private Map<String, Double> percentageByType;
    private Map<String, Double> averagePremiumByType;
    private Map<String, Double> totalPremiumByType;
    private Map<String, Map<String, Double>> premiumRangeByType;
    private Map<String, Long> countByRisk;

    // Getters and Setters
    public long getTotalInsurancePros() {
        return totalInsurancePros;
    }

    public void setTotalInsurancePros(long totalInsurancePros) {
        this.totalInsurancePros = totalInsurancePros;
    }

    public Map<String, Long> getCountByType() {
        return countByType;
    }

    public void setCountByType(Map<String, Long> countByType) {
        this.countByType = countByType;
    }

    public Map<String, Double> getPercentageByType() {
        return percentageByType;
    }

    public void setPercentageByType(Map<String, Double> percentageByType) {
        this.percentageByType = percentageByType;
    }

    public Map<String, Double> getAveragePremiumByType() {
        return averagePremiumByType;
    }

    public void setAveragePremiumByType(Map<String, Double> averagePremiumByType) {
        this.averagePremiumByType = averagePremiumByType;
    }

    public Map<String, Double> getTotalPremiumByType() {
        return totalPremiumByType;
    }

    public void setTotalPremiumByType(Map<String, Double> totalPremiumByType) {
        this.totalPremiumByType = totalPremiumByType;
    }

    public Map<String, Map<String, Double>> getPremiumRangeByType() {
        return premiumRangeByType;
    }

    public void setPremiumRangeByType(Map<String, Map<String, Double>> premiumRangeByType) {
        this.premiumRangeByType = premiumRangeByType;
    }

    public Map<String, Long> getCountByRisk() {
        return countByRisk;
    }

    public void setCountByRisk(Map<String, Long> countByRisk) {
        this.countByRisk = countByRisk;
    }
}