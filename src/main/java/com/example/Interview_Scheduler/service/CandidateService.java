package com.example.Interview_Scheduler.service;

import com.example.Interview_Scheduler.dto.CandidateDTO;
import com.example.Interview_Scheduler.model.Candidate;
import com.example.Interview_Scheduler.repository.CandidateRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CandidateService {

    private final CandidateRepository candidateRepository;

    public List<CandidateDTO> getCandidatesByBatch(Long batchId){
       List<Candidate> candidate = candidateRepository.findByBatchId(batchId);
        if (candidate.isEmpty()) {
            throw new RuntimeException("No candidates found for batch: " + batchId);
        }
        return candidate.stream()
                .map(this::mapToDto)
                .toList();
    }

    public CandidateDTO getCandidate(Long id){
        Candidate candidate = candidateRepository.findById(id).orElseThrow(()->
                new RuntimeException("Candidate Not Found "+id));
        return mapToDto(candidate);
    }

    @Transactional
    public void deleteBatch(Long batchId) {
//        Todo - Add batchRepository to check the BatchId from the Database
//        if (!batchRepository.existsById(batchId)) {
//            throw new RuntimeException("Batch not found: " + batchId);
//        }
        candidateRepository.deleteByBatchId(batchId);
//      Todo - To Delete the records using given batchId
//        batchRepository.deleteById(batchId);
    }

    //DTO Mapping
    private CandidateDTO mapToDto(Candidate candidate){
        CandidateDTO candidateDTO = new CandidateDTO();
        candidateDTO.setName(candidate.getName());
        candidateDTO.setEmail(candidate.getEmail());
        candidateDTO.setWhatsappNumber(candidate.getWhatsappNumber());
        candidateDTO.setRole(candidate.getRole());
        candidateDTO.setCompanyName(candidate.getCompanyName());
        if (candidate.getPanelTiming() != null) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            candidateDTO.setPanelTiming(candidate.getPanelTiming().format(formatter));
        }
        candidateDTO.setGmeetLink(candidate.getGMeetLink());
        candidateDTO.setInterviewerName(candidate.getInterviewerName());
        return candidateDTO;
    }

    //Entity Mapping
    private Candidate mapToEntity(CandidateDTO candidateDTO){
        Candidate candidate = new Candidate();
        candidate.setName(candidateDTO.getName());
        candidate.setEmail(candidateDTO.getEmail());
        candidate.setWhatsappNumber(candidateDTO.getWhatsappNumber());
        candidate.setRole(candidateDTO.getRole());
        candidate.setCompanyName(candidateDTO.getCompanyName());
        if (candidateDTO.getPanelTiming() != null) {
            candidate.setPanelTiming(LocalDateTime.parse(candidateDTO.getPanelTiming()));
        }        candidate.setGMeetLink(candidateDTO.getGmeetLink());
        candidate.setInterviewerName(candidateDTO.getInterviewerName());
        return candidate;
    }
}
