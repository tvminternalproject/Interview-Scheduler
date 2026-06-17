package com.example.Interview_Scheduler.model;

import com.example.Interview_Scheduler.model.BatchStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "batch_info")
public class BatchModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @OneToMany(mappedBy = "batchInfo", cascade = CascadeType.ALL, orphanRemoval = true)
//    private List<InterviewCandidate> candidates = new ArrayList<>();

    @Column(name = "file_name", nullable = false)
    private String fileName;

    @Column(name = "total_candidates")
    private Integer totalCandidates;

    @Enumerated(EnumType.STRING)
    @Column(length = 20, nullable = false)
    private BatchStatus status;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

//    public void addCandidate(InterviewCandidate candidate) {
//        candidates.add(candidate);
//        candidate.setBatchid(this);
    }
}