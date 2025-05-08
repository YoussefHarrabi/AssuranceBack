package com.assurance.assuranceback.Services.loyaltyServices;


import com.assurance.assuranceback.DTOs.BonusDTO;
import com.assurance.assuranceback.Entity.loyaltyEntity.Bonus;
import com.assurance.assuranceback.Repository.loyaltyRepositories.BonusRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BonusService {

    private final BonusRepository bonusRepository;

    // Get all bonuses
    public List<BonusDTO> getAllBonuses() {
        return bonusRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    // Get available bonuses only
    public List<BonusDTO> getAvailableBonuses() {
        return bonusRepository.findByIsAvailableTrue().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    // Get bonuses available with points
    public List<BonusDTO> getBonusesByPoints(Integer points) {
        return bonusRepository.findByPointsRequiredLessThanEqual(points).stream()
                .filter(Bonus::isAvailable)
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    // Get bonus by ID
    public BonusDTO getBonusById(Long id) {
        Bonus bonus = bonusRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Bonus not found with ID: " + id));
        return convertToDTO(bonus);
    }

    // Create a new bonus
    public BonusDTO createBonus(BonusDTO bonusDTO) {
        Bonus bonus = convertToEntity(bonusDTO);
        bonus = bonusRepository.save(bonus);
        return convertToDTO(bonus);
    }

    // Update an existing bonus
    public BonusDTO updateBonus(Long id, BonusDTO bonusDTO) {
        Bonus existingBonus = bonusRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Bonus not found with ID: " + id));

        // Update fields
        existingBonus.setName(bonusDTO.getName());
        existingBonus.setDescription(bonusDTO.getDescription());
        existingBonus.setPointsRequired(bonusDTO.getPointsRequired());
        existingBonus.setImageUrl(bonusDTO.getImageUrl());
        existingBonus.setAvailable(bonusDTO.isAvailable());
        existingBonus.setStock(bonusDTO.getStock());

        existingBonus = bonusRepository.save(existingBonus);
        return convertToDTO(existingBonus);
    }

    // Delete a bonus
    public void deleteBonus(Long id) {
        Bonus bonus = bonusRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Bonus not found with ID: " + id));

        // Check if users have redeemed this bonus
        if (bonus.getRedeemedByUsers() != null && !bonus.getRedeemedByUsers().isEmpty()) {
            throw new RuntimeException("Cannot delete bonus that has been redeemed by users");
        }

        bonusRepository.delete(bonus);
    }

    // Utility methods for conversion
    private BonusDTO convertToDTO(Bonus bonus) {
        return BonusDTO.builder()
                .id(bonus.getId())
                .name(bonus.getName())
                .description(bonus.getDescription())
                .pointsRequired(bonus.getPointsRequired())
                .imageUrl(bonus.getImageUrl())
                .isAvailable(bonus.isAvailable())
                .stock(bonus.getStock())
                .build();
    }

    private Bonus convertToEntity(BonusDTO dto) {
        return Bonus.builder()
                .name(dto.getName())
                .description(dto.getDescription())
                .pointsRequired(dto.getPointsRequired())
                .imageUrl(dto.getImageUrl())
                .isAvailable(dto.isAvailable())
                .stock(dto.getStock())
                .build();
    }
}