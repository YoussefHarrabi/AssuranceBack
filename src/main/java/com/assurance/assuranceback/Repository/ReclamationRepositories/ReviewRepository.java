package com.assurance.assuranceback.Repository.ReclamationRepositories;

import com.assurance.assuranceback.Entity.ReclamationEntity.Review;
import com.assurance.assuranceback.Enum.ReviewStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Map;
import java.util.Set;


@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {
    Page<Review> findByIsPublicTrue(Pageable pageable);

    Page<Review> findByIsPublicTrueAndStatus(ReviewStatus status, Pageable pageable);

    Page<Review> findByCategoriesContainingAndIsPublicTrueAndStatus(
            String category, ReviewStatus status, Pageable pageable);

    @Query("SELECT AVG(r.rating) FROM Review r WHERE r.status = 'APPROVED'")
    Double findAverageRating();

    @Query("SELECT r.rating as rating, COUNT(r) as count FROM Review r " +
            "WHERE r.status = 'APPROVED' GROUP BY r.rating ORDER BY r.rating")
    List<Object[]> findRatingDistribution();

    @Query("SELECT FUNCTION('YEAR', r.createdAt) as year, " +
            "FUNCTION('MONTH', r.createdAt) as month, " +
            "AVG(r.rating) as avgRating, COUNT(r) as count " +
            "FROM Review r WHERE r.status = 'APPROVED' " +
            "GROUP BY FUNCTION('YEAR', r.createdAt), FUNCTION('MONTH', r.createdAt) " +
            "ORDER BY year DESC, month DESC")
    List<Object[]> findMonthlyRatingTrend();

    @Query("SELECT r.sentiment as sentiment, COUNT(r) as count FROM Review r " +
            "WHERE r.status = 'APPROVED' GROUP BY r.sentiment")
    List<Object[]> findSentimentDistribution();

    @Query("SELECT r FROM Review r JOIN r.categories c " +
            "WHERE c IN :categories AND r.sentiment = :sentiment " +
            "AND r.id != :reviewId AND r.status = :status AND r.isPublic = :isPublic " +
            "GROUP BY r.id ORDER BY COUNT(c) DESC, r.helpfulVotes DESC")
    List<Review> findSimilarReviews(
            @Param("categories") Set<String> categories,
            @Param("sentiment") String sentiment,
            @Param("reviewId") Long reviewId,
            @Param("status") ReviewStatus status,
            @Param("isPublic") boolean isPublic);

    @Query("SELECT COUNT(r) as reviewCount, " +
            "AVG(r.rating) as averageRating, " +
            "SUM(r.helpfulVotes) as totalHelpfulVotes, " +
            "(SELECT COUNT(v) FROM Review v JOIN v.votedByUsers u WHERE u.id = :userId) as votesGiven " +
            "FROM Review r WHERE r.client.id = :userId")
    Map<String, Object> findUserEngagementStats(@Param("userId") Long userId);
}