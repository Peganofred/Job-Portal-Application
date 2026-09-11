package com.jobportal.entity;

import com.jobportal.enums.Role;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "users")
public class User extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(nullable = false, unique = true, length = 150)
    private String email;

    @Column(nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Role role;

    @Builder.Default
    @OneToMany(mappedBy = "recruiter")
    private List<JobPosting> jobPostings = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "candidate")
    private List<Application> applications = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "candidate")
    private List<ResumeMetadata> resumes = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "candidate")
    private List<SavedJob> savedJobs = new ArrayList<>();
}
