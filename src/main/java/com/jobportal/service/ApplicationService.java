package com.jobportal.service;

import com.jobportal.dto.ApplicationResponse;
import com.jobportal.dto.UpdateApplicationStatusRequest;
import com.jobportal.entity.Application;
import com.jobportal.entity.JobPosting;
import com.jobportal.entity.User;
import com.jobportal.exception.BadRequestException;
import com.jobportal.exception.ForbiddenException;
import com.jobportal.exception.ResourceNotFoundException;
import com.jobportal.repository.ApplicationRepository;
import com.jobportal.repository.JobPostingRepository;
import com.jobportal.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ApplicationService {

    private final ApplicationRepository applicationRepository;
    private final JobPostingRepository jobPostingRepository;
    private final UserRepository userRepository;

    public ApplicationService(ApplicationRepository applicationRepository,
                              JobPostingRepository jobPostingRepository,
                              UserRepository userRepository) {
        this.applicationRepository = applicationRepository;
        this.jobPostingRepository = jobPostingRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public ApplicationResponse apply(Long jobId, Long candidateId) {
        JobPosting job = jobPostingRepository.findById(jobId)
                .orElseThrow(() -> new ResourceNotFoundException("Job not found with id: " + jobId));

        User candidate = userRepository.findById(candidateId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + candidateId));

        if (job.getRecruiter().getId().equals(candidateId)) {
            throw new BadRequestException("Recruiters cannot apply to their own jobs");
        }

        if (applicationRepository.existsByCandidateIdAndJobPostingId(candidateId, jobId)) {
            throw new BadRequestException("You have already applied to this job");
        }

        Application application = Application.builder()
                .candidate(candidate)
                .jobPosting(job)
                .build();

        return ApplicationResponse.from(applicationRepository.save(application));
    }

    public Page<ApplicationResponse> getMyApplications(Long candidateId, int page, int size, String sortBy, String sortDir) {
        Pageable pageable = buildPageable(page, size, sortBy, sortDir);
        return applicationRepository.findByCandidateId(candidateId, pageable)
                .map(ApplicationResponse::from);
    }

    public Page<ApplicationResponse> getApplicationsForJob(Long jobId, Long recruiterId,
                                                           int page, int size, String sortBy, String sortDir) {
        JobPosting job = jobPostingRepository.findById(jobId)
                .orElseThrow(() -> new ResourceNotFoundException("Job not found with id: " + jobId));

        if (!job.getRecruiter().getId().equals(recruiterId)) {
            throw new ForbiddenException("You are not authorized to view applications for this job");
        }

        Pageable pageable = buildPageable(page, size, sortBy, sortDir);
        return applicationRepository.findByJobPostingId(jobId, pageable)
                .map(ApplicationResponse::from);
    }

    public Page<ApplicationResponse> getApplicationsForRecruiter(Long recruiterId,
                                                                 int page, int size, String sortBy, String sortDir) {
        Pageable pageable = buildPageable(page, size, sortBy, sortDir);
        return applicationRepository.findByJobPostingRecruiterId(recruiterId, pageable)
                .map(ApplicationResponse::from);
    }

    @Transactional
    public ApplicationResponse updateStatus(Long applicationId, Long recruiterId,
                                            UpdateApplicationStatusRequest request) {
        Application application = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new ResourceNotFoundException("Application not found with id: " + applicationId));

        if (!application.getJobPosting().getRecruiter().getId().equals(recruiterId)) {
            throw new ForbiddenException("You are not authorized to update this application");
        }

        application.setStatus(request.status());
        return ApplicationResponse.from(applicationRepository.save(application));
    }

    private Pageable buildPageable(int page, int size, String sortBy, String sortDir) {
        Sort.Direction direction = "desc".equalsIgnoreCase(sortDir)
                ? Sort.Direction.DESC : Sort.Direction.ASC;
        return PageRequest.of(page, size, Sort.by(direction, sortBy));
    }
}