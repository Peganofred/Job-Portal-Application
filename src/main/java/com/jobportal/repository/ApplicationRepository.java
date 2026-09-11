package com.jobportal.repository;

import com.jobportal.entity.Application;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ApplicationRepository extends JpaRepository<Application, Long> {

    boolean existsByCandidateIdAndJobPostingId(Long candidateId, Long jobPostingId);

    Optional<Application> findByCandidateIdAndJobPostingId(Long candidateId, Long jobPostingId);

    Page<Application> findByCandidateId(Long candidateId, Pageable pageable);

    Page<Application> findByJobPostingId(Long jobPostingId, Pageable pageable);

    Page<Application> findByJobPostingRecruiterId(Long recruiterId, Pageable pageable);

    long countByJobPostingId(Long jobPostingId);
}