package com.example.Interview_Scheduler.controller;


import com.example.Interview_Scheduler.dto.CandidateDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/candidates")
@RequiredArgsConstructor
public class CandidateController {

    private final CandidateService candidateService;

    @GetMapping
    public ResponseEntity<List<CandidateDTO>> getCandidatesByBatch(@RequestParam Long batchId) {
        return ResponseEntity.ok(candidateService.getCandidatesByBatch(batchId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<CandidateDTO> getCandidate(@PathVariable Long id) {

        return ResponseEntity.ok(candidateService.getCandidate(id));
    }


    @DeleteMapping("/batch/{batchId}")
    public ResponseEntity<String> deleteBatch(@PathVariable Long batchId) {

        candidateService.deleteBatch(batchId);

        return ResponseEntity.ok("Batch deleted successfully");
    }
}