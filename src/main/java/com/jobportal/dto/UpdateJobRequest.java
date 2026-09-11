package com.jobportal.dto;

import com.jobportal.enums.JobType;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record UpdateJobRequest(
        @Size(max = 200, message = "Title must be at most 200 characters")
        String title,

        @Size(max = 5000, message = "Description must be at most 5000 characters")
        String description,

        @Size(max = 200, message = "Company must be at most 200 characters")
        String company,

        @Size(max = 100, message = "Location must be at most 100 characters")
        String location,

        @Positive(message = "Salary must be positive")
        BigDecimal salary,

        JobType jobType
) {}