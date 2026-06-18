package com.example.Interview_Scheduler.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import jakarta.validation.constraints.Pattern;
import java.time.LocalDateTime;

@Data
public class CandidateDTO {

    @NotBlank(message = "Candidate name is required")
    private String name;

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;

    @NotBlank(message = "WhatsApp number is required")
    @Pattern(regexp = "^\\+?[1-9]\\d{9,14}$", message = "Invalid WhatsApp number format")
    private String whatsappNumber;

    @NotBlank(message = "Role is required")
    private String role;

    @NotBlank(message = "Company name is required")
    private String companyName;

    @NotNull(message = "Panel timing is required")
    private LocalDateTime panelTiming;

    @NotBlank(message = "Google meet link is required")
    @Pattern(regexp = "^(http|https)://.*$", message = "Invalid Google Meet URL")
    private String gmeetLink;

    @NotBlank(message = "Interviewer name is required")
    private String interviewerName;
}