package com.jobportal.service;

import com.jobportal.dto.ResumeMetadataResponse;
import com.jobportal.entity.ResumeMetadata;
import com.jobportal.entity.User;
import com.jobportal.exception.BadRequestException;
import com.jobportal.exception.ForbiddenException;
import com.jobportal.exception.ResourceNotFoundException;
import com.jobportal.repository.ResumeMetadataRepository;
import com.jobportal.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
public class ResumeService {

    private static final long MAX_FILE_SIZE = 5 * 1024 * 1024;

    private final ResumeMetadataRepository resumeRepository;
    private final UserRepository userRepository;
    private final FileStorageService fileStorageService;

    public ResumeService(ResumeMetadataRepository resumeRepository,
                         UserRepository userRepository,
                         FileStorageService fileStorageService) {
        this.resumeRepository = resumeRepository;
        this.userRepository = userRepository;
        this.fileStorageService = fileStorageService;
    }

    @Transactional
    public ResumeMetadataResponse upload(Long candidateId, MultipartFile file) {
        if (file.getSize() > MAX_FILE_SIZE) {
            throw new BadRequestException("File size must be at most 5 MB");
        }

        String contentType = file.getContentType();
        if (contentType == null
                || !(contentType.equals("application/pdf")
                || contentType.equals("application/msword")
                || contentType.equals("text/plain")
                || contentType.equals(
                        "application/vnd.openxmlformats-officedocument.wordprocessingml.document"))) {
            throw new BadRequestException("Only PDF, DOCX, DOC or TXT files are allowed");
        }

        User candidate = userRepository.findById(candidateId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + candidateId));

        String storedPath = fileStorageService.storeFile(file);

        ResumeMetadata resume = ResumeMetadata.builder()
                .candidate(candidate)
                .fileName(file.getOriginalFilename())
                .filePath(storedPath)
                .contentType(contentType)
                .size(file.getSize())
                .build();

        return ResumeMetadataResponse.from(resumeRepository.save(resume));
    }

    public List<ResumeMetadataResponse> getMyResumes(Long candidateId) {
        return resumeRepository.findByCandidateId(candidateId).stream()
                .map(ResumeMetadataResponse::from)
                .toList();
    }

    @Transactional
    public void delete(Long resumeId, Long candidateId) {
        ResumeMetadata resume = resumeRepository.findById(resumeId)
                .orElseThrow(() -> new ResourceNotFoundException("Resume not found with id: " + resumeId));

        if (!resume.getCandidate().getId().equals(candidateId)) {
            throw new ForbiddenException("You are not authorized to delete this resume");
        }

        fileStorageService.deleteFile(resume.getFilePath());
        resumeRepository.delete(resume);
    }
}