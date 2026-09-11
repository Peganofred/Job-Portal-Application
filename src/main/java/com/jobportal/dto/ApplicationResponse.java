package com.jobportal.dto;

import com.jobportal.entity.Application;
import com.jobportal.enums.ApplicationStatus;

import java.time.LocalDateTime;

public record ApplicationResponse(
        Long id,
        Long candidateId,
        String candidateName,
        String candidateEmail,
        Long jobPostingId,
        String jobTitle,
        String company,
        String location,
        ApplicationStatus status,
        LocalDateTime appliedAt,
        LocalDateTime updatedAt
) {
    public static ApplicationResponse from(Application application) {
        return new ApplicationResponse(
                application.getId(),
                application.getCandidate().getId(),
                application.getCandidate().getName(),
                application.getCandidate().getEmail(),
                application.getJobPosting().getId(),
                application.getJobPosting().getTitle(),
                application.getJobPosting().getCompany(),
                application.getJobPosting().getLocation(),
                application.getStatus(),
                application.getCreatedAt(),
                application.getUpdatedAt()
        );
    }
}