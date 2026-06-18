package com.example.Interview_Scheduler.dto;

import com.example.Interview_Scheduler.model.Role;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AuthResponse {

    private String token;

    private String email;

    private Role role;
}