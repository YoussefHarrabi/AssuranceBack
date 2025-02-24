package com.assurance.assuranceback.Services.CarrieresServices;

import com.assurance.assuranceback.Entity.CarrieresEntity.JobApplication;
import com.assurance.assuranceback.Entity.CarrieresEntity.JobOffer;

import java.util.List;

public interface IJobOfferService {
    public List<JobOffer> retrieveallJobOffers();
    public JobOffer retrieveJobOfferById(long id);
    public JobOffer addJobOffer(JobOffer JO);
    public void removeJobOffer(long id);
    public JobOffer updateJobOffer(Long id, JobOffer jobOfferDetails);
}
