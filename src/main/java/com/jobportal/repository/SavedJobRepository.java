package com.jobportal.repository;

import com.jobportal.entity.SavedJob;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SavedJobRepository extends JpaRepository<SavedJob, Long> {

    boolean existsByCandidateIdAndJobPostingId(Long candidateId, Long jobPostingId);

    Optional<SavedJob> findByCandidateIdAndJobPostingId(Long candidateId, Long jobPostingId);

    Page<SavedJob> findByCandidateId(Long candidateId, Pageable pageable);

    void deleteByCandidateIdAndJobPostingId(Long candidateId, Long jobPostingId);
}