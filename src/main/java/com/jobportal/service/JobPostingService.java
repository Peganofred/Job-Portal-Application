package com.jobportal.service;

import com.jobportal.dto.CreateJobRequest;
import com.jobportal.dto.JobPostingResponse;
import com.jobportal.dto.UpdateJobRequest;
import com.jobportal.entity.JobPosting;
import com.jobportal.entity.User;
import com.jobportal.exception.ForbiddenException;
import com.jobportal.exception.ResourceNotFoundException;
import com.jobportal.repository.JobPostingRepository;
import com.jobportal.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class JobPostingService {

    private final JobPostingRepository jobPostingRepository;
    private final UserRepository userRepository;

    public JobPostingService(JobPostingRepository jobPostingRepository,
                             UserRepository userRepository) {
        this.jobPostingRepository = jobPostingRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public JobPostingResponse createJob(Long recruiterId, CreateJobRequest request) {
        User recruiter = userRepository.findById(recruiterId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + recruiterId));

        JobPosting job = JobPosting.builder()
                .title(request.title())
                .description(request.description())
                .company(request.company())
                .location(request.location())
                .salary(request.salary())
                .jobType(request.jobType())
                .recruiter(recruiter)
                .build();

        return JobPostingResponse.from(jobPostingRepository.save(job));
    }

    public JobPostingResponse getJob(Long jobId) {
        return JobPostingResponse.from(getJobEntity(jobId));
    }

    public Page<JobPostingResponse> getMyJobs(Long recruiterId, int page, int size, String sortBy, String sortDir) {
        Pageable pageable = buildPageable(page, size, sortBy, sortDir);
        return jobPostingRepository.findByRecruiterId(recruiterId, pageable)
                .map(JobPostingResponse::from);
    }

    @Transactional
    public JobPostingResponse updateJob(Long jobId, Long recruiterId, UpdateJobRequest request) {
        JobPosting job = getJobEntity(jobId);
        verifyOwner(job, recruiterId);

        if (request.title() != null) job.setTitle(request.title());
        if (request.description() != null) job.setDescription(request.description());
        if (request.company() != null) job.setCompany(request.company());
        if (request.location() != null) job.setLocation(request.location());
        if (request.salary() != null) job.setSalary(request.salary());
        if (request.jobType() != null) job.setJobType(request.jobType());

        return JobPostingResponse.from(jobPostingRepository.save(job));
    }

    @Transactional
    public void deleteJob(Long jobId, Long recruiterId) {
        JobPosting job = getJobEntity(jobId);
        verifyOwner(job, recruiterId);
        jobPostingRepository.delete(job);
    }

    private JobPosting getJobEntity(Long jobId) {
        return jobPostingRepository.findById(jobId)
                .orElseThrow(() -> new ResourceNotFoundException("Job not found with id: " + jobId));
    }

    private void verifyOwner(JobPosting job, Long recruiterId) {
        if (!job.getRecruiter().getId().equals(recruiterId)) {
            throw new ForbiddenException("You are not authorized to modify this job posting");
        }
    }

    protected Pageable buildPageable(int page, int size, String sortBy, String sortDir) {
        Sort.Direction direction = "desc".equalsIgnoreCase(sortDir)
                ? Sort.Direction.DESC : Sort.Direction.ASC;
        return PageRequest.of(page, size, Sort.by(direction, sortBy));
    }
}