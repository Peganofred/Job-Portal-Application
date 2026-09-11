package com.jobportal.service;

import com.jobportal.entity.JobPosting;
import com.jobportal.enums.JobType;
import org.springframework.data.jpa.domain.Specification;

import java.util.Locale;

public final class JobSearchSpecification {

    private JobSearchSpecification() {}

    public static Specification<JobPosting> searchFilter(String keyword,
                                                        String location,
                                                        JobType jobType) {
        return Specification
                .where(hasKeyword(keyword))
                .and(hasLocation(location))
                .and(hasJobType(jobType));
    }

    private static Specification<JobPosting> hasKeyword(String keyword) {
        return (root, query, cb) -> {
            if (keyword == null || keyword.isBlank()) {
                return cb.conjunction();
            }
            String pattern = "%" + keyword.trim().toLowerCase(Locale.ROOT) + "%";
            return cb.or(
                    cb.like(cb.lower(root.get("title")), pattern),
                    cb.like(cb.lower(root.get("company")), pattern),
                    cb.like(cb.lower(root.get("location")), pattern)
            );
        };
    }

    private static Specification<JobPosting> hasLocation(String location) {
        return (root, query, cb) -> {
            if (location == null || location.isBlank()) {
                return cb.conjunction();
            }
            return cb.equal(cb.lower(root.get("location")), location.trim().toLowerCase(Locale.ROOT));
        };
    }

    private static Specification<JobPosting> hasJobType(JobType jobType) {
        return (root, query, cb) -> {
            if (jobType == null) {
                return cb.conjunction();
            }
            return cb.equal(root.get("jobType"), jobType);
        };
    }
}