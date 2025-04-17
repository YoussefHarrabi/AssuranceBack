package com.example.gharbipi.controllers;

import com.example.gharbipi.entities.InsurancePro;
import com.example.gharbipi.entities.InsuranceProType;
import com.example.gharbipi.entities.InsuranceStatus;
import com.example.gharbipi.entities.StatisticsDTO;
import com.example.gharbipi.services.InsuranceProService;
import com.example.gharbipi.services.InsuranceProTypeService;
import com.example.gharbipi.services.StatsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/insurancePros")
@CrossOrigin(origins = "http://localhost:4200")
public class InsuranceProController {

    private static final Logger logger = LoggerFactory.getLogger(InsuranceProController.class);

    @Autowired
    private InsuranceProService insuranceProService;

    @Autowired
    private InsuranceProTypeService insuranceProTypeService;

    @Autowired
    private StatsService statsService;

    // ✅ Get all
    @GetMapping
    public ResponseEntity<List<InsurancePro>> getAllInsurancePros() {
        List<InsurancePro> list = insuranceProService.getAllInsurancePros();
        return ResponseEntity.ok(list);
    }

    // ✅ Get one
    @GetMapping("/{id}")
    public ResponseEntity<InsurancePro> getInsuranceProById(@PathVariable Long id) {
        Optional<InsurancePro> insurancePro = insuranceProService.getInsuranceProById(id);
        return insurancePro.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // ✅ Get PDF
    @GetMapping("/{id}/fichier")
    public ResponseEntity<byte[]> getInsuranceProFile(@PathVariable Long id) {
        return insuranceProService.getInsuranceProById(id)
                .map(insurancePro -> ResponseEntity.ok()
                        .header(HttpHeaders.CONTENT_DISPOSITION, "inline;filename=insurance.pdf")
                        .contentType(MediaType.APPLICATION_PDF)
                        .body(insurancePro.getFichier()))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping(consumes = {"multipart/form-data"})
    public ResponseEntity<InsurancePro> createInsurancePro(
            @RequestParam("proName") String proName,
            @RequestParam("description") String description,
            @RequestParam("premiumAmount") Double premiumAmount,
            @RequestParam("insuranceProType") Long insuranceProTypeId,
            @RequestParam("status") String status,
            @RequestParam(value = "fichier", required = false) MultipartFile fichier) throws IOException {

        // Get InsuranceProType based on the ID
        InsuranceProType insuranceProType = insuranceProTypeService.findById(insuranceProTypeId);

        // Create new InsurancePro
        InsurancePro insurancePro = new InsurancePro();
        insurancePro.setProName(proName);
        insurancePro.setDescription(description);
        insurancePro.setPremiumAmount(premiumAmount);
        insurancePro.setInsuranceProType(insuranceProType);
        insurancePro.setStatus(InsuranceStatus.valueOf(status));

        // If a file is provided, set it
        if (fichier != null && !fichier.isEmpty()) {
            byte[] data = fichier.getBytes();
            insurancePro.setFichier(data);
            insurancePro.setStatus(InsuranceStatus.REDEMANDEE); // Example status change when file is uploaded
        }

        // Save the InsurancePro entity
        InsurancePro savedInsurancePro = insuranceProService.createInsurancePro(insurancePro);

        return ResponseEntity.ok(savedInsurancePro);
    }


    // ✅ Update
    @PutMapping(consumes = {"multipart/form-data"})
    public ResponseEntity<InsurancePro> updateInsurancePro(
            @RequestParam("id") Long id,
            @RequestParam("proName") String proName,
            @RequestParam("description") String description,
            @RequestParam("premiumAmount") Double premiumAmount,
            @RequestParam("insuranceProType") Long insuranceProTypeId,
            @RequestParam("status") String status,
            @RequestParam(value = "fichier", required = false) MultipartFile fichier) throws IOException {

        Optional<InsurancePro> optionalInsurancePro = insuranceProService.getInsuranceProById(id);
        if (optionalInsurancePro.isEmpty()) {
            logger.warn("InsurancePro not found with ID: {}", id);
            return ResponseEntity.notFound().build();
        }

        InsurancePro insurancePro = optionalInsurancePro.get();
        InsuranceProType insuranceProType = insuranceProTypeService.findById(insuranceProTypeId);

        // Update fields
        insurancePro.setProName(proName);
        insurancePro.setDescription(description);
        insurancePro.setPremiumAmount(premiumAmount);
        insurancePro.setInsuranceProType(insuranceProType);
        insurancePro.setStatus(InsuranceStatus.valueOf(status));

        // Update file if provided
        if (fichier != null && !fichier.isEmpty()) {
            byte[] data = fichier.getBytes();
            insurancePro.setFichier(data);
            insurancePro.setStatus(InsuranceStatus.REDEMANDEE); // Optional: update status when file is changed
        }

        InsurancePro updatedInsurancePro = insuranceProService.updateInsurancePro(insurancePro);
        return ResponseEntity.ok(updatedInsurancePro);
    }


    // ✅ Change Status
    @PutMapping("/{id}/status/{status}")
    public ResponseEntity<InsurancePro> changeInsuranceProStatus(
            @PathVariable Long id,
            @PathVariable InsuranceStatus status) {

        Optional<InsurancePro> optionalPro = insuranceProService.getInsuranceProById(id);
        if (optionalPro.isEmpty()) {
            logger.warn("Cannot change status, InsurancePro not found with ID: {}", id);
            return ResponseEntity.notFound().build();
        }

        InsurancePro insurancePro = optionalPro.get();
        insurancePro.setStatus(status);
        InsurancePro updatedInsurancePro = insuranceProService.updateInsurancePro(insurancePro);
        return ResponseEntity.ok(updatedInsurancePro);
    }

    // ✅ Upload or replace file (fichier)
    @PutMapping(value = "/upload/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<InsurancePro> uploadFichier(
            @PathVariable Long id,
            @RequestParam("fichier") MultipartFile fichier) throws IOException {

        Optional<InsurancePro> optionalPro = insuranceProService.getInsuranceProById(id);
        if (optionalPro.isEmpty()) {
            logger.warn("Upload failed. InsurancePro not found with ID: {}", id);
            return ResponseEntity.notFound().build();
        }

        InsurancePro insurancePro = optionalPro.get();
        insurancePro.setFichier(fichier.getBytes());
        insurancePro.setStatus(InsuranceStatus.EN_COURS_DE_TRAITEMENT);

        InsurancePro updated = insuranceProService.updateInsurancePro(insurancePro);
        return ResponseEntity.ok(updated);
    }

    // ✅ Simulate premium
    @PostMapping("/simulate-premium")
    public ResponseEntity<InsurancePro> simulatePremiumAmount(@RequestBody InsurancePro insurancePro) {
        InsurancePro simulated = insuranceProService.InitializeAmountInsurancePro(insurancePro);
        return ResponseEntity.ok(simulated);
    }

    // ✅ Initialize and save
    @PostMapping("/initialize")
    public ResponseEntity<InsurancePro> initializeAndSave(@RequestBody InsurancePro insurancePro) {
        insuranceProService.InitializeAmountInsurancePro(insurancePro);
        InsurancePro saved = insuranceProService.updateInsurancePro(insurancePro);
        return ResponseEntity.ok(saved);
    }

    // ✅ Delete
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteInsurancePro(@PathVariable Long id) {
        Optional<InsurancePro> existing = insuranceProService.getInsuranceProById(id);
        if (existing.isEmpty()) {
            logger.warn("Delete failed. InsurancePro not found with ID: {}", id);
            return ResponseEntity.notFound().build();
        }

        insuranceProService.deleteInsurancePro(id);
        return ResponseEntity.noContent().build();
    }

    // ✅ Stats
    @GetMapping("/statistics")
    public ResponseEntity<StatisticsDTO> getAllStatistics() {
        StatisticsDTO statistics = statsService.getAllStatistics();
        return ResponseEntity.ok(statistics);
    }
}
