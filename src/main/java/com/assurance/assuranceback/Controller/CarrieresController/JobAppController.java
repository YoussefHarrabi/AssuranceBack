package com.assurance.assuranceback.Controller.CarrieresController;

import com.assurance.assuranceback.Entity.CarrieresEntity.JobApplication;
import com.assurance.assuranceback.Enum.StatutCandidature;
import com.assurance.assuranceback.Services.CarrieresServices.IJobAppService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.http.HttpStatus;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.Map;

@RestController
@RequestMapping("/api/JobApplication")
public class JobAppController {
    @Autowired
    IJobAppService jobAppService;

    @GetMapping
    public List<JobApplication> getAllJobApplications() {
        return jobAppService.retrieveallJobApplications();
    }

    @GetMapping("/{id}")
    public JobApplication getJobApplicationById(@PathVariable Long id) {
        return jobAppService.retrieveJobApplicationById(id);
    }

    @PostMapping("/createJobApplication")
    public JobApplication createJobApplication(@RequestBody JobApplication jobApplication) {
        return jobAppService.addJobApplication(jobApplication);
    }

    @PostMapping("/spontaneous")
    public JobApplication createSpontaneousApplication(@RequestBody JobApplication jobApplication) {
        return jobAppService.addSpontaneousApplication(jobApplication);
    }

    @PutMapping("/{id}")
    public JobApplication updateJobApplication(@PathVariable Long id, @RequestBody JobApplication updatedJobApplication) {
        return jobAppService.updateJobApplication(id, updatedJobApplication);
    }

    @DeleteMapping("/{id}")
    public void deleteJobApplication(@PathVariable Long id) {
        jobAppService.removeJobApplication(id);
    }

    /**
     * Update only the status of a job application and send a notification email
     */
    @PatchMapping("/{id}/status")
    public ResponseEntity<?> updateJobApplicationStatus(
            @PathVariable Long id,
            @RequestBody Map<String, String> payload) {

        try {
            String statusStr = payload.get("statut");
            if (statusStr == null) {
                return ResponseEntity
                        .badRequest()
                        .body(Collections.singletonMap("error", "Status is required"));
            }

            StatutCandidature status;
            try {
                status = StatutCandidature.valueOf(statusStr);
            } catch (IllegalArgumentException e) {
                return ResponseEntity
                        .badRequest()
                        .body(Collections.singletonMap("error", "Invalid status value: " + statusStr));
            }

            JobApplication updated = jobAppService.updateStatus(id, status);
            return ResponseEntity.ok(updated);
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Collections.singletonMap("error", "Failed to update status: " + e.getMessage()));
        }
    }

    @PostMapping("/upload")
    public ResponseEntity<Map<String, String>> uploadCv(@RequestParam("cv") MultipartFile file) {
        Map<String, String> response = new HashMap<>();
        if (file.isEmpty()) {
            response.put("error", "Aucun fichier n'a été envoyé.");
            return ResponseEntity.badRequest().body(response);
        }

        try {
            String uploadDir = "C:\\uploads";
            File directory = new File(uploadDir);
            if (!directory.exists()) {
                directory.mkdirs();
            }

            File destinationFile = new File(uploadDir + File.separator + file.getOriginalFilename());
            file.transferTo(destinationFile);

            response.put("filePath", destinationFile.getPath());
            return ResponseEntity.ok(response);
        } catch (IOException e) {
            response.put("error", "Erreur lors de l'upload du fichier.");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @PostMapping("/uploadFiles")
    public ResponseEntity<Map<String, String>> uploadFiles(
            @RequestParam("cv") MultipartFile cv,
            @RequestParam("lettreMotivation") MultipartFile lettreMotivation) {

        // Sauvegarder les fichiers et obtenir leurs chemins
        String cvPath = saveFile(cv, "cv");
        String lettreMotivationPath = saveFile(lettreMotivation, "lettreMotivation");

        // Retourner les chemins des fichiers
        Map<String, String> filePaths = new HashMap<>();
        filePaths.put("cvPath", cvPath);
        filePaths.put("lettreMotivationPath", lettreMotivationPath);

        return ResponseEntity.ok(filePaths);
    }

    private String saveFile(MultipartFile file, String fileType) {
        // Code pour sauvegarder le fichier et retourner son chemin
        // Exemple : "/uploads/cv/nom_fichier.pdf"
        String fileName = file.getOriginalFilename();
        Path filePath = Paths.get("uploads", fileType, fileName);
        try {
            Files.copy(file.getInputStream(), filePath);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return filePath.toString();
    }

    @PostMapping("/applyJob")
    public ResponseEntity<String> applyJob(@RequestBody JobApplication jobApplication) {
        // Sauvegarder la candidature avec les chemins des fichiers
        jobAppService.addJobApplication(jobApplication);
        return ResponseEntity.ok("Candidature envoyée avec succès !");
    }

  @PutMapping("/{id}/status")
  public JobApplication updateCandidatureStatus(@PathVariable Long id, @RequestBody StatutCandidature newStatus) {
    return jobAppService.updateCandidatureStatus(id, newStatus);
  }
}
