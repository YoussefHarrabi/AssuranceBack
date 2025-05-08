package com.assurance.assuranceback.Services.loyaltyServices;


import com.assurance.assuranceback.DTOs.LoyaltyStatusDTO;
import com.assurance.assuranceback.Entity.loyaltyEntity.LoyaltyStatus;
import com.assurance.assuranceback.Repository.loyaltyRepositories.LoyaltyStatusRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LoyaltyStatusService {

    private final LoyaltyStatusRepository loyaltyStatusRepository;

    // Get all loyalty statuses
    public List<LoyaltyStatusDTO> getAllLoyaltyStatuses() {
        return loyaltyStatusRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    // Get loyalty status by ID
    public LoyaltyStatusDTO getLoyaltyStatusById(Long id) {
        LoyaltyStatus loyaltyStatus = loyaltyStatusRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Loyalty status not found with ID: " + id));
        return convertToDTO(loyaltyStatus);
    }

    // Create a new loyalty status
    public LoyaltyStatusDTO createLoyaltyStatus(LoyaltyStatusDTO loyaltyStatusDTO) {
        // Check if tier already exists
        if (loyaltyStatusRepository.findByTier(loyaltyStatusDTO.getTier()).isPresent()) {
            throw new RuntimeException("Loyalty tier already exists: " + loyaltyStatusDTO.getTier());
        }

        LoyaltyStatus loyaltyStatus = convertToEntity(loyaltyStatusDTO);
        loyaltyStatus = loyaltyStatusRepository.save(loyaltyStatus);
        return convertToDTO(loyaltyStatus);
    }

    // Update an existing loyalty status
    public LoyaltyStatusDTO updateLoyaltyStatus(Long id, LoyaltyStatusDTO loyaltyStatusDTO) {
        LoyaltyStatus existingStatus = loyaltyStatusRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Loyalty status not found with ID: " + id));

        // Check if updating to a tier that already exists (but not this one)
        loyaltyStatusRepository.findByTier(loyaltyStatusDTO.getTier())
                .ifPresent(status -> {
                    if (!status.getId().equals(id)) {
                        throw new RuntimeException("Loyalty tier already exists: " + loyaltyStatusDTO.getTier());
                    }
                });

        // Update fields
        existingStatus.setTier(loyaltyStatusDTO.getTier());
        existingStatus.setPointsThreshold(loyaltyStatusDTO.getPointsThreshold());
        existingStatus.setDescription(loyaltyStatusDTO.getDescription());
        existingStatus.setBenefits(loyaltyStatusDTO.getBenefits());

        existingStatus = loyaltyStatusRepository.save(existingStatus);
        return convertToDTO(existingStatus);
    }

    // Delete a loyalty status
    public void deleteLoyaltyStatus(Long id) {
        LoyaltyStatus loyaltyStatus = loyaltyStatusRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Loyalty status not found with ID: " + id));

        // Check if users are associated with this status
        if (loyaltyStatus.getUsers() != null && !loyaltyStatus.getUsers().isEmpty()) {
            throw new RuntimeException("Cannot delete loyalty status that has users assigned to it");
        }

        loyaltyStatusRepository.delete(loyaltyStatus);
    }

    // Utility methods for conversion
    private LoyaltyStatusDTO convertToDTO(LoyaltyStatus loyaltyStatus) {
        return LoyaltyStatusDTO.builder()
                .id(loyaltyStatus.getId())
                .tier(loyaltyStatus.getTier())
                .pointsThreshold(loyaltyStatus.getPointsThreshold())
                .description(loyaltyStatus.getDescription())
                .benefits(loyaltyStatus.getBenefits())
                .build();
    }

    private LoyaltyStatus convertToEntity(LoyaltyStatusDTO dto) {
        return LoyaltyStatus.builder()
                .tier(dto.getTier())
                .pointsThreshold(dto.getPointsThreshold())
                .description(dto.getDescription())
                .benefits(dto.getBenefits())
                .build();
    }
}
