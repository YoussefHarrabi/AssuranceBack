package com.assurance.assuranceback.Controller.CarrieresController;

import com.assurance.assuranceback.Entity.CarrieresEntity.JobApplication;
import com.assurance.assuranceback.Entity.CarrieresEntity.JobOffer;
import com.assurance.assuranceback.Services.CarrieresServices.IJobAppService;
import com.assurance.assuranceback.Services.CarrieresServices.IJobOfferService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/jobOffers")
public class JobOfferController {
    @Autowired
    IJobOfferService JobOfferService;
    @GetMapping
    public List<JobOffer> getAllJobOffers() {
        return JobOfferService.retrieveallJobOffers();
    }

    @GetMapping("/{id}")
    public JobOffer getJobOffersById(@PathVariable Long id) {
        return JobOfferService. retrieveJobOfferById(id);
    }

    @PostMapping
    public JobOffer createJobOffer(@RequestBody JobOffer jobOffer) {
        return JobOfferService.addJobOffer(jobOffer);
    }

    @PutMapping("/{id}")
    public JobOffer updateJobOffer (@PathVariable Long id, @RequestBody JobOffer updatedJobOffer) {
        return JobOfferService.updateJobOffer(id, updatedJobOffer);
    }

    @DeleteMapping("/{id}")
    public void deleteJobOffer(@PathVariable Long id) {
        JobOfferService.removeJobOffer(id);
    }
}
