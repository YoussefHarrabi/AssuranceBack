package com.assurance.assuranceback.Services.ReclamationServices;

import com.assurance.assuranceback.Entity.ReclamationEntity.Review;

import java.util.List;
import java.util.Optional;

public interface IServiceReview {
    Review createReview(Review review);

    List<Review> getAllReviews();

    Optional<Review> getReviewById(Long id);

    Review updateReview(Long id, Review reviewDetails);

    void deleteReview(Long id);
}
