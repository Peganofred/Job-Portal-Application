package com.jobportal.repository;

import com.jobportal.entity.JobPosting;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface JobPostingRepository extends JpaRepository<JobPosting, Long>,
        JpaSpecificationExecutor<JobPosting> {

    Page<JobPosting> findByRecruiterId(Long recruiterId, Pageable pageable);

    @Query("""
            SELECT jp FROM JobPosting jp
            WHERE (:keyword IS NULL OR
                   LOWER(jp.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR
                   LOWER(jp.company) LIKE LOWER(CONCAT('%', :keyword, '%')))
            """)
    Page<JobPosting> searchByKeyword(@Param("keyword") String keyword, Pageable pageable);
}