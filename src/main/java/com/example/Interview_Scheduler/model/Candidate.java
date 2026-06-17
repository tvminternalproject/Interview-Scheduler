package com.example.Interview_Scheduler.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Entity
@Table(name = "candidates")
public class Candidate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long batchId;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "email", nullable = false)
    private String email;

    @Column(name = "whatsapp_number", nullable = false)
    private String whatsappNumber;

    @Column(name = "role", nullable = false)
    private String role;

    @Column(name = "company_name", nullable = false)
    private String companyName;

    @Column(name = "panel_timing")
    private LocalDateTime panelTiming;

    @Column(name = "gMeet_link")
    private String gMeetLink;

    @Column(name = "interviewer_name")
    private String interviewerName;

    @Column(name = "created_at")
    private LocalDateTime createdAt;
}
