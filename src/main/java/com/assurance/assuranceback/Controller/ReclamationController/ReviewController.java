package com.assurance.assuranceback.Controller.ReclamationController;

import com.assurance.assuranceback.Entity.ReclamationEntity.Review;
import com.assurance.assuranceback.Enum.ReviewStatus;
import com.assurance.assuranceback.Services.ReclamationServices.ReviewService;
import com.assurance.assuranceback.dto.ErrorResponse;
import com.assurance.assuranceback.dto.ReviewAnalyticsDTO;
import com.assurance.assuranceback.dto.ReviewDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/reviews")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    @PostMapping
    public ResponseEntity<Review> createReview(@RequestBody ReviewDTO reviewDTO, Principal principal) {
        // Récupérer l'ID de l'utilisateur connecté (simplifié)
        Long userId = getUserIdFromPrincipal(principal);
        Review review = reviewService.createReview(reviewDTO, userId);
        return new ResponseEntity<>(review, HttpStatus.CREATED);
    }


    @GetMapping
    public ResponseEntity<Page<Review>> getAllReviews(Pageable pageable) {
        // Cette méthode retournera tous les reviews, pas seulement les publics
        Page<Review> reviews = reviewService.findAllReviews(pageable);
        return ResponseEntity.ok(reviews);
    }

    @DeleteMapping("/{reviewId}")
    public ResponseEntity<?> deleteReview(@PathVariable Long reviewId, Principal principal) {
        try {
            Long userId = getUserIdFromPrincipal(principal);
            reviewService.deleteReview(reviewId, userId);
            return ResponseEntity.noContent().build();
        } catch (AccessDeniedException e) {
            return ResponseEntity
                    .status(HttpStatus.FORBIDDEN)
                    .body(new ErrorResponse(
                            "Vous n'êtes pas autorisé à supprimer cet avis",
                            "FORBIDDEN"
                    ));
        } catch (RuntimeException e) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponse(
                            "Avis non trouvé",
                            "NOT_FOUND"
                    ));
        }

    }
    // Pour garder l'ancien comportement si nécessaire
    @GetMapping("/public")
    public ResponseEntity<Page<Review>> getPublicReviews(Pageable pageable) {
        Page<Review> reviews = reviewService.getPublicReviews(pageable);
        return ResponseEntity.ok(reviews);
    }
    @GetMapping("/category/{category}")
    public ResponseEntity<Page<Review>> getReviewsByCategory(
            @PathVariable String category, Pageable pageable) {
        Page<Review> reviews = reviewService.getReviewsByCategory(category, pageable);
        return ResponseEntity.ok(reviews);
    }

    @GetMapping("/analytics")
    public ResponseEntity<ReviewAnalyticsDTO> getReviewAnalytics() {
        ReviewAnalyticsDTO analytics = reviewService.getReviewAnalytics();
        return ResponseEntity.ok(analytics);
    }

    @PostMapping("/{reviewId}/helpful")
    public ResponseEntity<Void> markAsHelpful(
            @PathVariable Long reviewId, Principal principal) {
        Long userId = getUserIdFromPrincipal(principal);
        boolean success = reviewService.markAsHelpful(reviewId, userId);

        if (success) {
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/{reviewId}/moderate")
    public ResponseEntity<Review> moderateReview(
            @PathVariable Long reviewId,
            @RequestParam ReviewStatus status,
            @RequestParam(required = false) String companyResponse) {
        Review review = reviewService.moderateReview(reviewId, status, companyResponse);

        if (review != null) {
            return ResponseEntity.ok(review);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/{reviewId}/similar")
    public ResponseEntity<List<Review>> getSimilarReviews(@PathVariable Long reviewId) {
        List<Review> similarReviews = reviewService.getSimilarReviews(reviewId);
        return ResponseEntity.ok(similarReviews);
    }

    @GetMapping("/user/engagement")
    public ResponseEntity<Map<String, Object>> getUserEngagementStats(Principal principal) {
        Long userId = getUserIdFromPrincipal(principal);
        Map<String, Object> stats = reviewService.getUserEngagementStats(userId);
        return ResponseEntity.ok(stats);
    }

    // Méthode utilitaire pour récupérer l'ID de l'utilisateur depuis Principal
    private Long getUserIdFromPrincipal(Principal principal) {
        // Implémentation simplifiée - à adapter selon votre système d'authentification
        // Dans un système réel, vous extrairiez l'ID utilisateur du Principal
        return 2L; // ID utilisateur fictif
    }
}