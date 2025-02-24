package com.assurance.assuranceback.Services.CarrieresServices;

import com.assurance.assuranceback.Entity.CarrieresEntity.JobApplication;
import com.assurance.assuranceback.Repository.CarrieresRepositories.JobApplicationRepository;
import com.assurance.assuranceback.Repository.CarrieresRepositories.QuizRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ServiceJobApp implements IJobAppService{
    @Autowired
    JobApplicationRepository jobApplicationRepository;
    @Override
    public List<JobApplication> retrieveallJobApplications() {
        return jobApplicationRepository.findAll();
    }

    @Override
    public JobApplication retrieveJobApplicationById(long id) {
        return jobApplicationRepository.findById(id).get();
    }

    @Override
    public JobApplication addJobApplication(JobApplication JA) {
        return jobApplicationRepository.save(JA);

    }
    @Override
    public void removeJobApplication(long id) {
jobApplicationRepository.deleteById(id);
    }

    @Override
    public JobApplication updateJobApplication(Long id, JobApplication jobAppDetails) {
        Optional<JobApplication> jobApplicationOptional = jobApplicationRepository.findById(id);

        if (jobApplicationOptional.isPresent()) {
            JobApplication jobApplication = jobApplicationOptional.get();

            // Mettre à jour les propriétés en utilisant les méthodes getter et setter uniquement si elles ne sont pas nulles
            if (jobAppDetails.getUser() != null) {
                jobApplication.setUser(jobAppDetails.getUser());
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
            // Sauvegarder le jobApplication mis à jour
            return jobApplicationRepository.save(jobApplication);
        }
        return null;  // Ou gérer le cas où l'objet n'est pas trouvé
    }
}
