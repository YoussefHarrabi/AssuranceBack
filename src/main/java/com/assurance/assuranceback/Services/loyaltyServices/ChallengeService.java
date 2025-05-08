package com.assurance.assuranceback.Services.loyaltyServices;


import com.assurance.assuranceback.DTOs.ChallengeDTO;
import com.assurance.assuranceback.Entity.loyaltyEntity.Challenge;
import com.assurance.assuranceback.Repository.loyaltyRepositories.ChallengeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ChallengeService {

    private final ChallengeRepository challengeRepository;

    // Get all challenges
    public List<ChallengeDTO> getAllChallenges() {
        return challengeRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    // Get active challenges only
    public List<ChallengeDTO> getActiveChallenges() {
        return challengeRepository.findByIsActiveTrue().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    // Get current challenges (active and within date range)
    public List<ChallengeDTO> getCurrentChallenges() {
        LocalDate now = LocalDate.now();
        return challengeRepository.findByStartDateBeforeAndEndDateAfter(now, now).stream()
                .filter(Challenge::isActive)
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    // Get challenge by ID
    public ChallengeDTO getChallengeById(Long id) {
        Challenge challenge = challengeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Challenge not found with ID: " + id));
        return convertToDTO(challenge);
    }

    // Create a new challenge
    public ChallengeDTO createChallenge(ChallengeDTO challengeDTO) {
        Challenge challenge = convertToEntity(challengeDTO);
        challenge = challengeRepository.save(challenge);
        return convertToDTO(challenge);
    }

    // Update an existing challenge
    public ChallengeDTO updateChallenge(Long id, ChallengeDTO challengeDTO) {
        Challenge existingChallenge = challengeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Challenge not found with ID: " + id));

        // Update fields
        existingChallenge.setName(challengeDTO.getName());
        existingChallenge.setDescription(challengeDTO.getDescription());
        existingChallenge.setPointsAwarded(challengeDTO.getPointsAwarded());
        existingChallenge.setStartDate(challengeDTO.getStartDate());
        existingChallenge.setEndDate(challengeDTO.getEndDate());
        existingChallenge.setActive(challengeDTO.isActive());

        existingChallenge = challengeRepository.save(existingChallenge);
        return convertToDTO(existingChallenge);
    }

    // Delete a challenge
    public void deleteChallenge(Long id) {
        Challenge challenge = challengeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Challenge not found with ID: " + id));

        // Check if users have completed this challenge
        if (challenge.getCompletedByUsers() != null && !challenge.getCompletedByUsers().isEmpty()) {
            throw new RuntimeException("Cannot delete challenge that has been completed by users");
        }

        challengeRepository.delete(challenge);
    }

    // Utility methods for conversion
    private ChallengeDTO convertToDTO(Challenge challenge) {
        return ChallengeDTO.builder()
                .id(challenge.getId())
                .name(challenge.getName())
                .description(challenge.getDescription())
                .pointsAwarded(challenge.getPointsAwarded())
                .startDate(challenge.getStartDate())
                .endDate(challenge.getEndDate())
                .isActive(challenge.isActive())
                .build();
    }

    private Challenge convertToEntity(ChallengeDTO dto) {
        return Challenge.builder()
                .name(dto.getName())
                .description(dto.getDescription())
                .pointsAwarded(dto.getPointsAwarded())
                .startDate(dto.getStartDate())
                .endDate(dto.getEndDate())
                .isActive(dto.isActive())
                .build();
    }
}