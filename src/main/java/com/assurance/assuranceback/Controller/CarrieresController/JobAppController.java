package com.assurance.assuranceback.Controller.CarrieresController;

import com.assurance.assuranceback.Entity.CarrieresEntity.JobApplication;
import com.assurance.assuranceback.Entity.CarrieresEntity.JobOffer;
import com.assurance.assuranceback.Services.CarrieresServices.IJobAppService;
import com.assurance.assuranceback.Services.CarrieresServices.IJobOfferService;
import com.assurance.assuranceback.Services.CarrieresServices.ServiceJobApp;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/JobApplication")
public class JobAppController {
    @Autowired
    IJobAppService JobAppService;

    @GetMapping
    public List<JobApplication> getAllJobApplications() {
        return JobAppService.retrieveallJobApplications();
    }

    @GetMapping("/{id}")
    public JobApplication getJobApplicationById(@PathVariable Long id) {
        return JobAppService. retrieveJobApplicationById(id);
    }

    @PostMapping
    public JobApplication createJobApplication(@RequestBody JobApplication jobApplication) {
        return JobAppService.addJobApplication(jobApplication);
    }

    @PutMapping("/{id}")
    public JobApplication updateJobApplication(@PathVariable Long id, @RequestBody JobApplication updatedJobApplication) {
        return JobAppService.updateJobApplication(id, updatedJobApplication);
    }

    @DeleteMapping("/{id}")
    public void deleteJobApplication(@PathVariable Long id) {
        JobAppService.removeJobApplication(id);
    }}