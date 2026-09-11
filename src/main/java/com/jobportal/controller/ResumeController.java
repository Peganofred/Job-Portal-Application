package com.jobportal.controller;

import com.jobportal.dto.ResumeMetadataResponse;
import com.jobportal.security.UserPrincipal;
import com.jobportal.service.ResumeService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/resume")
@PreAuthorize("hasRole('CANDIDATE')")
public class ResumeController {

    private final ResumeService resumeService;

    public ResumeController(ResumeService resumeService) {
        this.resumeService = resumeService;
    }

    @PostMapping("/upload")
    public ResponseEntity<ResumeMetadataResponse> upload(
            @AuthenticationPrincipal UserPrincipal principal,
            @RequestParam("file") MultipartFile file) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(resumeService.upload(principal.getId(), file));
    }

    @GetMapping("/mine")
    public ResponseEntity<List<ResumeMetadataResponse>> getMyResumes(
            @AuthenticationPrincipal UserPrincipal principal) {
        return ResponseEntity.ok(resumeService.getMyResumes(principal.getId()));
    }

    @DeleteMapping("/{resumeId}")
    public ResponseEntity<Void> delete(
            @PathVariable Long resumeId,
            @AuthenticationPrincipal UserPrincipal principal) {
        resumeService.delete(resumeId, principal.getId());
        return ResponseEntity.noContent().build();
    }
}