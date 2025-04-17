package com.assurance.assuranceback.Services.CarrieresServices;

import com.assurance.assuranceback.Entity.CarrieresEntity.JobApplication;
import com.assurance.assuranceback.Entity.CarrieresEntity.JobOffer;
import com.assurance.assuranceback.Entity.UserEntity.User;
import com.assurance.assuranceback.Enum.StatutCandidature;
import com.assurance.assuranceback.Enum.StatutOffre;
import com.assurance.assuranceback.Repository.CarrieresRepositories.JobApplicationRepository;
import com.assurance.assuranceback.Repository.CarrieresRepositories.JobOfferRepository;
import com.assurance.assuranceback.Repository.UserRepositories.UserRepository;

import jakarta.mail.MessagingException;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class ServiceJobApp implements IJobAppService {
    @Autowired
    JobApplicationRepository jobApplicationRepository;

    @Autowired
    JobOfferRepository  jobOfferRepository;

    @Autowired
    UserRepository userRepository;

  @Autowired
 ServiceJobOffer jobOfferService;
  @Autowired
    EmailService emailService;

    @Override
    public List<JobApplication> retrieveallJobApplications() {
        return jobApplicationRepository.findAll();
    }

    @Override
    public JobApplication retrieveJobApplicationById(long id) {
        return jobApplicationRepository.findById(id).orElse(null);
    }

    @Override
    @Transactional
    public JobApplication addJobApplication(JobApplication JA) {
        // Set a default user if not already set
        if (JA.getUser() == null) {
            Optional<User> defaultUser = userRepository.findById(1L); // Assume user with ID 1 is the default user
            defaultUser.ifPresent(JA::setUser);
        }
        return jobApplicationRepository.save(JA);
    }

    @Override
    @Transactional
    public void removeJobApplication(long id) {
        jobApplicationRepository.deleteById(id);
    }

    @Override
    @Transactional
    public JobApplication updateJobApplication(Long id, JobApplication jobAppDetails) {
        Optional<JobApplication> jobApplicationOptional = jobApplicationRepository.findById(id);

        if (jobApplicationOptional.isPresent()) {
            JobApplication jobApplication = jobApplicationOptional.get();

            if (jobAppDetails.getEmail() != null) {
                jobApplication.setEmail(jobAppDetails.getEmail());
            }
            if (jobAppDetails.getJobOffer() != null) {
                jobApplication.setJobOffer(jobAppDetails.getJobOffer());
            }
            if (jobAppDetails.getStatut() != null) {
                jobApplication.setStatut(jobAppDetails.getStatut());
            }
            if (jobAppDetails.getDateCandidature() != null) {
                jobApplication.setDateCandidature(jobAppDetails.getDateCandidature());
            }
            jobApplication.setPinned(jobAppDetails.isPinned());

            return jobApplicationRepository.save(jobApplication);
        }
        return null;
    }

    @Override
    public JobApplication addSpontaneousApplication(JobApplication jobApplication) {
        // Set the user to null for spontaneous applications
        jobApplication.setUser(null);
        // Save the application
        return jobApplicationRepository.save(jobApplication);
    }

    @Override
    @Transactional
    public JobApplication updateStatus(Long id, StatutCandidature status) {
        JobApplication jobApplication = jobApplicationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Job Application not found with id: " + id));

        // Update status
        jobApplication.setStatut(status);
        JobApplication updatedApplication = jobApplicationRepository.save(jobApplication);

        // Get application details for email
        String email = updatedApplication.getEmail();
        String jobTitle = "Candidature";
        if (updatedApplication.getJobOffer() != null && updatedApplication.getJobOffer().getTitre() != null) {
            jobTitle = updatedApplication.getJobOffer().getTitre();
        }

        // Get applicant name
        String applicantName = "Candidat";
        if (updatedApplication.getUser() != null) {
            String firstName = updatedApplication.getUser().getFirstName();
            String lastName = updatedApplication.getUser().getLastName();
            if (firstName != null && lastName != null) {
                applicantName = firstName + " " + lastName;
            }
        }

        // Send email notification
        try {
            emailService.sendStatusUpdateNotification(email, status, jobTitle, applicantName);
        } catch (MessagingException e) {
            // Log error but don't prevent the update
            System.err.println("Failed to send notification email: " + e.getMessage());
            e.printStackTrace();
        }

        return updatedApplication;
    }

  @Override
  public JobApplication updateCandidatureStatus(Long candidatureId, StatutCandidature newStatus) {
    // Récupérer la candidature
    JobApplication jobApplication = jobApplicationRepository.findById(candidatureId)
      .orElseThrow(() -> new EntityNotFoundException("Candidature avec ID " + candidatureId + " introuvable"));

    // Mettre à jour le statut
    jobApplication.setStatut(newStatus);

    // Si le statut est "PRESELECTIONNEE", mettre à jour les postes vacants
    if (newStatus == StatutCandidature.PRESELECTIONNEE) {
      JobOffer jobOffer = jobApplication.getJobOffer();
      if (jobOffer != null) {
        if (jobOffer.getNbrVacant() > 0) {
          jobOffer.setNbrVacant(jobOffer.getNbrVacant() - 1);

          // Si le nombre de postes vacants atteint 0, changer le statut à EXPIRE
          if (jobOffer.getNbrVacant() == 0) {
            jobOffer.setStatut(StatutOffre.EXPIRE);
          }

          // Sauvegarder les modifications de l'offre
          jobOfferRepository.save(jobOffer);
        }
      }
    }

    // Sauvegarder la candidature mise à jour
    return jobApplicationRepository.save(jobApplication);
  }
}
