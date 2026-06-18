package com.example.Interview_Scheduler.dto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BatchDTO {
    private Long id;
    private String fileName;
    private Integer totalCandidates;
    private String status;
    private LocalDateTime createdAt;
}