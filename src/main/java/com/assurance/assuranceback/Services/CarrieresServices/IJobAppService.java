package com.assurance.assuranceback.Services.CarrieresServices;

import com.assurance.assuranceback.Entity.CarrieresEntity.JobApplication;

import java.util.List;

public interface IJobAppService {
    public List<JobApplication> retrieveallJobApplications();
    public JobApplication retrieveJobApplicationById(long id);
    public JobApplication addJobApplication(JobApplication JA);
    public void removeJobApplication(long id);
    public JobApplication updateJobApplication(Long id, JobApplication jobAppDetails);
}
