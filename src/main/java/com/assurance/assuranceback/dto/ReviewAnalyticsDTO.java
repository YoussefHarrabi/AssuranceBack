package com.assurance.assuranceback.dto;

import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class ReviewAnalyticsDTO {
    private Double averageRating;
    private Map<Integer, Long> ratingDistribution;
    private List<Object[]> monthlyTrend;
    private Map<String, Long> sentimentDistribution;
}