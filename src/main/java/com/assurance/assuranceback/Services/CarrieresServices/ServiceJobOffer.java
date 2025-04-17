package com.assurance.assuranceback.Services.CarrieresServices;

import com.assurance.assuranceback.Entity.CarrieresEntity.JobOffer;
import com.assurance.assuranceback.Entity.CarrieresEntity.Quiz;
import com.assurance.assuranceback.Enum.StatutOffre;
import com.assurance.assuranceback.Repository.CarrieresRepositories.JobOfferRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.config.ConfigDataResourceNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service

public class ServiceJobOffer implements IJobOfferService {
    @Autowired
    JobOfferRepository jobOfferRepository;

    @Override
    public List<JobOffer> retrieveallJobOffers() {
        return jobOfferRepository.findAll();
    }

    @Override
    public JobOffer retrieveJobOfferById(long id) {
        return jobOfferRepository.findById(id).get();
    }

    @Override
    public JobOffer addJobOffer(JobOffer JO) {
        return jobOfferRepository.save(JO);
    }

    @Override
    public void removeJobOffer(long id) {
jobOfferRepository.deleteById(id);
    }

    @Override
    public JobOffer updateJobOffer(Long id, JobOffer jobOfferDetails) {
        Optional<JobOffer> jobOfferOptional = jobOfferRepository.findById(id);

        if (jobOfferOptional.isPresent()) {
            JobOffer jobOffer = jobOfferOptional.get();

            // Mettre à jour les propriétés en utilisant les méthodes getter et setter uniquement si elles ne sont pas nulles
            if (jobOfferDetails.getTitre() != null) {
                jobOffer.setTitre(jobOfferDetails.getTitre());
            }
            if (jobOfferDetails.getDescription() != null) {
                jobOffer.setDescription(jobOfferDetails.getDescription());
            }
            if (jobOfferDetails.getDatePublication() != null) {
                jobOffer.setDatePublication(jobOfferDetails.getDatePublication());
            }
            if (jobOfferDetails.getDateExpiration() != null) {
                jobOffer.setDateExpiration(jobOfferDetails.getDateExpiration());
            }
            if (jobOfferDetails.getLieu() != null) {
                jobOffer.setLieu(jobOfferDetails.getLieu());
            }
            if (jobOfferDetails.getTypeContrat() != null) {
                jobOffer.setTypeContrat(jobOfferDetails.getTypeContrat());
            }
            if (jobOfferDetails.getSalaire() != null) {
                jobOffer.setSalaire(jobOfferDetails.getSalaire());
            }
            if (jobOfferDetails.getCompetencesRequises() != null) {
                jobOffer.setCompetencesRequises(jobOfferDetails.getCompetencesRequises());
            }
            if (jobOfferDetails.getExperienceMinimale() != 0) {
                jobOffer.setExperienceMinimale(jobOfferDetails.getExperienceMinimale());
            }
            if (jobOfferDetails.getStatut() != null) {
                jobOffer.setStatut(jobOfferDetails.getStatut());
            }
            // Sauvegarder le jobOffer mis à jour
            return jobOfferRepository.save(jobOffer);
        }
        return null;  // Ou gérer le cas où l'objet n'est pas trouvé
    }
    public List<JobOffer> searchJobOffersByTitle(String title) {
        return jobOfferRepository.findByTitreContainingIgnoreCase(title);
    }
  @Override
    public Optional<Long> getQuizIdByJobOffer(Long jobOfferId) {
    return jobOfferRepository.findById(jobOfferId)
      .map(JobOffer::getQuiz)
      .map(Quiz::getId);
  }

}
