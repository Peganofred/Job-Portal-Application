package com.jobportal.dto;

import com.jobportal.entity.JobPosting;
import com.jobportal.enums.JobType;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record JobPostingResponse(
        Long id,
        String title,
        String description,
        String company,
        String location,
        BigDecimal salary,
        JobType jobType,
        String recruiterName,
        Long recruiterId,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
    public static JobPostingResponse from(JobPosting job) {
        return new JobPostingResponse(
                job.getId(),
                job.getTitle(),
                job.getDescription(),
                job.getCompany(),
                job.getLocation(),
                job.getSalary(),
                job.getJobType(),
                job.getRecruiter().getName(),
                job.getRecruiter().getId(),
                job.getCreatedAt(),
                job.getUpdatedAt()
        );
    }
}