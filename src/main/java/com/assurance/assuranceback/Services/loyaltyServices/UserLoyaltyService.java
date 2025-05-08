package com.assurance.assuranceback.Services.loyaltyServices;


import com.assurance.assuranceback.Entity.loyaltyEntity.Bonus;
import com.assurance.assuranceback.Entity.loyaltyEntity.Challenge;
import com.assurance.assuranceback.Entity.loyaltyEntity.LoyaltyStatus;
import com.assurance.assuranceback.Entity.UserEntity.User;
import com.assurance.assuranceback.Repository.loyaltyRepositories.BonusRepository;
import com.assurance.assuranceback.Repository.loyaltyRepositories.ChallengeRepository;
import com.assurance.assuranceback.Repository.loyaltyRepositories.LoyaltyStatusRepository;
import com.assurance.assuranceback.Repository.UserRepositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class UserLoyaltyService {

    private final UserRepository userRepository;
    private final ChallengeRepository challengeRepository;
    private final BonusRepository bonusRepository;
    private final LoyaltyStatusRepository loyaltyStatusRepository;

    // Complete a challenge for a user
    @Transactional
    public Map<String, Object> completeChallenge(Long userId, Long challengeId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + userId));

        Challenge challenge = challengeRepository.findById(challengeId)
                .orElseThrow(() -> new RuntimeException("Challenge not found with ID: " + challengeId));

        // Check if challenge is active
        if (!challenge.isActive()) {
            throw new RuntimeException("This challenge is no longer active");
        }

        // Check if user already completed this challenge
        if (user.getCompletedChallenges() == null) {
            user.setCompletedChallenges(new HashSet<>());
        } else if (user.getCompletedChallenges().contains(challenge)) {
            throw new RuntimeException("User has already completed this challenge");
        }

        // Add challenge to user's completed challenges
        user.getCompletedChallenges().add(challenge);

        // Add points to user
        Integer newPoints = user.getLoyaltyPoints() + challenge.getPointsAwarded();
        user.setLoyaltyPoints(newPoints);

        // Check if user qualifies for a higher loyalty status
        updateUserLoyaltyStatus(user);

        // Save the user
        userRepository.save(user);

        return Map.of(
                "message", "Challenge completed successfully!",
                "pointsAwarded", challenge.getPointsAwarded(),
                "totalPoints", user.getLoyaltyPoints(),
                "loyaltyStatus", user.getLoyaltyStatus() != null ? user.getLoyaltyStatus().getTier() : null
        );
    }

    // Redeem a bonus
    @Transactional
    public Map<String, Object> redeemBonus(Long userId, Long bonusId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + userId));

        Bonus bonus = bonusRepository.findById(bonusId)
                .orElseThrow(() -> new RuntimeException("Bonus not found with ID: " + bonusId));

        // Check if bonus is available
        if (!bonus.isAvailable()) {
            throw new RuntimeException("This bonus is not available for redemption");
        }

        // Check if bonus is in stock
        if (bonus.getStock() <= 0) {
            throw new RuntimeException("This bonus is out of stock");
        }

        // Check if user has enough points
        if (user.getLoyaltyPoints() < bonus.getPointsRequired()) {
            throw new RuntimeException("User does not have enough points to redeem this bonus");
        }

        // Initialize redeemedBonuses if null
        if (user.getRedeemedBonuses() == null) {
            user.setRedeemedBonuses(new HashSet<>());
        }

        // Deduct points from user
        user.setLoyaltyPoints(user.getLoyaltyPoints() - bonus.getPointsRequired());

        // Add bonus to user's redeemed bonuses
        user.getRedeemedBonuses().add(bonus);

        // Reduce bonus stock
        bonus.setStock(bonus.getStock() - 1);

        // Update bonus availability if stock reaches 0
        if (bonus.getStock() <= 0) {
            bonus.setAvailable(false);
        }

        // Save both entities
        bonusRepository.save(bonus);
        userRepository.save(user);

        return Map.of(
                "message", "Bonus redeemed successfully!",
                "pointsDeducted", bonus.getPointsRequired(),
                "remainingPoints", user.getLoyaltyPoints()
        );
    }

    // Helper method to update user's loyalty status based on points
    private void updateUserLoyaltyStatus(User user) {
        // Get all loyalty statuses ordered by points threshold
        List<LoyaltyStatus> statuses = loyaltyStatusRepository.findAll();

        if (statuses.isEmpty()) {
            return;
        }

        // Find the highest status the user qualifies for
        LoyaltyStatus highestQualifyingStatus = null;

        for (LoyaltyStatus status : statuses) {
            if (user.getLoyaltyPoints() >= status.getPointsThreshold()) {
                if (highestQualifyingStatus == null ||
                        status.getPointsThreshold() > highestQualifyingStatus.getPointsThreshold()) {
                    highestQualifyingStatus = status;
                }
            }
        }

        if (highestQualifyingStatus != null) {
            user.setLoyaltyStatus(highestQualifyingStatus);
        }
    }

    // Get user loyalty info
    public Map<String, Object> getUserLoyaltyInfo(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + userId));

        return Map.of(
                "points", user.getLoyaltyPoints(),
                "status", user.getLoyaltyStatus() != null ?
                        Map.of(
                                "tier", user.getLoyaltyStatus().getTier(),
                                "description", user.getLoyaltyStatus().getDescription(),
                                "benefits", user.getLoyaltyStatus().getBenefits()
                        ) : null,
                "completedChallenges", user.getCompletedChallenges() != null ?
                        user.getCompletedChallenges().size() : 0,
                "redeemedBonuses", user.getRedeemedBonuses() != null ?
                        user.getRedeemedBonuses().size() : 0
        );
    }
}