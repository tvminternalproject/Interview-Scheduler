package com.example.Interview_Scheduler.controller;

import com.example.Interview_Scheduler.dto.CandidateDTO;
import com.example.Interview_Scheduler.service.CandidateService;
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

    @PostMapping("/upload")
    public ResponseEntity<String> uploadExcel(@RequestParam("file") MultipartFile file) {

        return ResponseEntity.ok(
                candidateService.uploadCandidates(file));
    }

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

    @DeleteMapping("/{candidateId}")
    public ResponseEntity<String> deleteCandidate(@PathVariable Long candidateId) {
        candidateService.deleteCandidate(candidateId);

        return ResponseEntity.ok("Candidate deleted successfully");
    }
}