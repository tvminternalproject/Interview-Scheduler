package com.example.Interview_Scheduler.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import jakarta.validation.constraints.Pattern;


@Data
public class CandidateDTO {

    private Long id;

    @NotBlank(message = "Candidate name is required")
    @Size(min = 3, max = 100, message = "Name must be between 3 and 100 characters")
    private String name;

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;

    @NotBlank(message = "WhatsApp number is required")
    @Pattern(regexp = "^\\+?[1-9]\\d{9,14}$", message = "Invalid WhatsApp number format")
    private String whatsappNumber;

    @NotBlank(message = "Role is required")
    @Size(max = 100, message = "Role cannot exceed 100 characters")
    private String role;

    @NotBlank(message = "Company name is required")
    @Size(max = 100, message = "Company name cannot exceed 100 characters")
    private String companyName;

    @NotBlank(message = "Panel timing is required")
    private String panelTiming;

    @NotBlank(message = "Google Meet link is required")
    @Pattern(regexp = "^(http|https)://.*$", message = "Invalid Google Meet URL")
    private String gmeetLink;

    @Size(max = 100, message = "Interviewer name cannot exceed 100 characters")
    private String interviewerName;
}