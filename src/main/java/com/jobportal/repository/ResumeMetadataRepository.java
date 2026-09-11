package com.jobportal.repository;

import com.jobportal.entity.ResumeMetadata;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ResumeMetadataRepository extends JpaRepository<ResumeMetadata, Long> {

    List<ResumeMetadata> findByCandidateId(Long candidateId);

    void deleteByCandidateId(Long candidateId);
}