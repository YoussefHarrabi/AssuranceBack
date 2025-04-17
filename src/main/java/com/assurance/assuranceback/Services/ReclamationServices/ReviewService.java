package com.assurance.assuranceback.Services.ReclamationServices;

import com.assurance.assuranceback.Entity.ReclamationEntity.Review;
import com.assurance.assuranceback.Entity.UserEntity.User;
import com.assurance.assuranceback.Enum.ReviewStatus;
import com.assurance.assuranceback.Repository.ReclamationRepositories.ReviewRepository;
import com.assurance.assuranceback.Repository.UserRepositories.UserRepository;
import com.assurance.assuranceback.dto.ReviewAnalyticsDTO;
import com.assurance.assuranceback.dto.ReviewDTO;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReviewService {
    private final ReviewRepository reviewRepository;
    private final UserRepository userRepository;
    private final SentimentAnalysisService sentimentAnalysisService;
    private final NotificationService notificationService;

    public Review createReview(ReviewDTO reviewDTO, Long userId) {
        Review review = new Review();
        review.setComment(reviewDTO.getComment());
        review.setRating(reviewDTO.getRating());
        review.setCategories(reviewDTO.getCategories());

        // Analyse de sentiment automatique
        String sentiment = sentimentAnalysisService.analyzeSentiment(reviewDTO.getComment());
        review.setSentiment(sentiment);

        // Liaison avec l'utilisateur
        userRepository.findById(userId).ifPresent(review::setClient);

        // Sauvegarde
        Review savedReview = reviewRepository.save(review);

        // Notification aux administrateurs
        notificationService.notifyAdmins("Nouvel avis soumis",
                "Un nouvel avis a été soumis et nécessite une modération");

        return savedReview;
    }

    public Page<Review> findAllReviews(Pageable pageable) {
        return reviewRepository.findAll(pageable);
    }

    public Page<Review> getPublicReviews(Pageable pageable) {
        // Cette méthode ne retourne que les reviews publics
        return reviewRepository.findByIsPublicTrue(pageable);
    }

    public Page<Review> getReviewsByCategory(String category, Pageable pageable) {
        return reviewRepository.findByCategoriesContainingAndIsPublicTrueAndStatus(
                category, ReviewStatus.APPROVED, pageable);
    }

    public ReviewAnalyticsDTO getReviewAnalytics() {
        ReviewAnalyticsDTO analytics = new ReviewAnalyticsDTO();

        // Moyenne générale des notes
        Double averageRating = reviewRepository.findAverageRating();
        analytics.setAverageRating(averageRating);

        // Distribution des notes
        Map<Integer, Long> ratingDistribution = reviewRepository.findRatingDistribution()
                .stream()
                .collect(Collectors.toMap(
                        arr -> (Integer) arr[0],
                        arr -> (Long) arr[1]
                ));
        analytics.setRatingDistribution(ratingDistribution);

        // Tendance par mois
        List<Object[]> monthlyTrend = reviewRepository.findMonthlyRatingTrend();
        analytics.setMonthlyTrend(monthlyTrend);

        // Distribution des sentiments
        Map<String, Long> sentimentDistribution = reviewRepository.findSentimentDistribution()
                .stream()
                .collect(Collectors.toMap(
                        arr -> (String) arr[0],
                        arr -> (Long) arr[1]
                ));
        analytics.setSentimentDistribution(sentimentDistribution);

        return analytics;
    }

    @Transactional
    public boolean markAsHelpful(Long reviewId, Long userId) {
        Optional<Review> reviewOpt = reviewRepository.findById(reviewId);
        Optional<User> userOpt = userRepository.findById(userId);

        if (reviewOpt.isPresent() && userOpt.isPresent()) {
            Review review = reviewOpt.get();
            User user = userOpt.get();

            // Vérifier si l'utilisateur n'a pas déjà voté
            if (!review.getVotedByUsers().contains(user)) {
                review.getVotedByUsers().add(user);
                review.setHelpfulVotes(review.getHelpfulVotes() + 1);
                reviewRepository.save(review);
                return true;
            }
        }

        return false;
    }

    public Review moderateReview(Long reviewId, ReviewStatus status, String companyResponse) {
        Optional<Review> reviewOpt = reviewRepository.findById(reviewId);

        if (reviewOpt.isPresent()) {
            Review review = reviewOpt.get();
            review.setStatus(status);

            if (companyResponse != null && !companyResponse.trim().isEmpty()) {
                review.setCompanyResponse(companyResponse);

                // Notifier le client de la réponse
                if (review.getClient() != null) {
                    notificationService.notifyUser(
                            review.getClient().getId(),
                            "Réponse à votre avis",
                            "Maghrebia Assurance a répondu à votre avis"
                    );
                }
            }

            return reviewRepository.save(review);
        }

        return null;
    }

    // Recommandation d'avis similaires
    public List<Review> getSimilarReviews(Long reviewId) {
        Optional<Review> reviewOpt = reviewRepository.findById(reviewId);

        if (reviewOpt.isPresent()) {
            Review review = reviewOpt.get();

            // Trouver des avis avec des catégories similaires et sentiments similaires
            return reviewRepository.findSimilarReviews(
                    review.getCategories(),
                    review.getSentiment(),
                    review.getId(),
                    ReviewStatus.APPROVED,
                    true
            );
        }

        return List.of();
    }

    // Statistiques d'engagement par utilisateur
    public Map<String, Object> getUserEngagementStats(Long userId) {
        return reviewRepository.findUserEngagementStats(userId);
    }

    @Transactional
    public void deleteReview(Long reviewId, Long userId) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new RuntimeException("Review not found"));

        // Vérifier si l'utilisateur est le propriétaire de l'avis
        if (!review.getClient().getId().equals(userId)) {
            throw new AccessDeniedException("You can only delete your own reviews");
        }

        // Supprimer les relations many-to-many s'il y en a
        review.getVotedByUsers().clear();

        // Supprimer l'avis
        reviewRepository.delete(review);

        // Optionnel : Envoyer une notification
        notificationService.notifyAdmins(
                "Suppression d'un avis",
                "L'avis #" + reviewId + " a été supprimé par l'utilisateur"
        );
    }
}