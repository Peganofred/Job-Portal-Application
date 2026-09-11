package com.jobportal.dto;

import com.jobportal.entity.ResumeMetadata;

import java.time.LocalDateTime;

public record ResumeMetadataResponse(
        Long id,
        String fileName,
        String filePath,
        String contentType,
        long size,
        LocalDateTime uploadedAt
) {
    public static ResumeMetadataResponse from(ResumeMetadata resume) {
        return new ResumeMetadataResponse(
                resume.getId(),
                resume.getFileName(),
                resume.getFilePath(),
                resume.getContentType(),
                resume.getSize(),
                resume.getCreatedAt()
        );
    }
}