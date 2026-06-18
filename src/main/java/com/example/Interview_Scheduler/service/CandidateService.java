package com.example.Interview_Scheduler.service;

import com.example.Interview_Scheduler.dto.CandidateDTO;
import com.example.Interview_Scheduler.model.BatchModel;
import com.example.Interview_Scheduler.model.BatchStatus;
import com.example.Interview_Scheduler.model.Candidate;
import com.example.Interview_Scheduler.repository.BatchRepository;
import com.example.Interview_Scheduler.repository.CandidateRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class CandidateService {


        private final CandidateRepository candidateRepository;
        private final BatchRepository batchRepository;

        @Cacheable(value = "batchCandidates", key = "#batchId")
        public List<CandidateDTO> getCandidatesByBatch(Long batchId) {

            List<Candidate> candidates = candidateRepository.findByBatchId(batchId);

            if (candidates.isEmpty()) {
                throw new RuntimeException("No candidates found for batch : " + batchId);
            }

            return candidates.stream()
                    .map(this::mapToDto)
                    .toList();
        }

        @Cacheable(value = "candidate", key = "#id")
        public CandidateDTO getCandidate(Long id) {

            Candidate candidate = candidateRepository.findById(id)
                            .orElseThrow(() ->
                                    new RuntimeException("Candidate Not Found : " + id));
            return mapToDto(candidate);
        }

        @Caching(evict = {@CacheEvict(value = "batchCandidates", key = "#batchId"),
            @CacheEvict(value = "candidate", allEntries = true)})
        @Transactional
        public void deleteBatch(Long batchId) {
            if(batchRepository.existsById(batchId)) {
                log.info("Deleting candidates for Batch ID : {}", batchId);
                candidateRepository.deleteByBatchId(batchId);
                log.info("Batch deleted successfully : {}", batchId);
                batchRepository.deleteById(batchId);
            }else{
                throw new RuntimeException("Batch Not Found "+batchId);
            }
        }

        @CacheEvict(value = {"batchCandidates", "candidate"}, allEntries = true)
        @Transactional
        public String uploadCandidates(MultipartFile file) {
            if (file == null || file.isEmpty()) {
                throw new RuntimeException("File is empty!");
            }
            String fileName = file.getOriginalFilename();
            if (fileName == null || (!fileName.endsWith(".xlsx") && !fileName.endsWith(".xls"))) {
                throw new RuntimeException("Only Excel files (.xlsx, .xls) are allowed!");
            }

            log.info("Excel Upload Started : {}",
                    file.getOriginalFilename());

            try (
                    InputStream inputStream = file.getInputStream();
                    Workbook workbook =
                            new XSSFWorkbook(inputStream)
            ) {

                Sheet sheet = workbook.getSheetAt(0);
                Long batchId = 1L;
                BatchModel batchModel = new BatchModel();
                batchModel.setFileName(file.getOriginalFilename());
                batchModel.setTotalCandidates(sheet.getLastRowNum());
                batchModel.setStatus(BatchStatus.UPLOADED);
                batchModel.setCreatedAt(LocalDateTime.now());
                batchModel = batchRepository.save(batchModel);
                for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                    Row row = sheet.getRow(i);
                    if (row == null) {
                        continue;
                    }
                    String name = getCellValue(row.getCell(0));
                    String email = getCellValue(row.getCell(1));
                    String whatsapp = getCellValue(row.getCell(2));
                    String role = getCellValue(row.getCell(3));
                    String company = getCellValue(row.getCell(4));
                    LocalDateTime panelTiming =
                            row.getCell(5).getLocalDateTimeCellValue();
                    DateTimeFormatter formatter =
                            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                    String meetLink = getCellValue(row.getCell(6));
                    String interviewer = getCellValue(row.getCell(7));

                    Candidate candidate = new Candidate();
                    candidate.setBatch(batchModel);
                    candidate.setName(name);
                    candidate.setEmail(email);
                    candidate.setWhatsappNumber(whatsapp);
                    candidate.setRole(role);
                    candidate.setCompanyName(company);
                    candidate.setPanelTiming(panelTiming);
                    candidate.setGMeetLink(meetLink);
                    candidate.setInterviewerName(interviewer);
                    candidate.setCreatedAt(LocalDateTime.now());
                    candidateRepository.save(candidate);
                }
                log.info("Excel Upload Completed");
                return "Excel Uploaded Successfully";
            } catch (Exception e) {
                log.error("Excel Upload Failed", e);
                throw new RuntimeException(
                        "Error Processing Excel File"
                );
            }
        }

        private CandidateDTO mapToDto(Candidate candidate) {

            CandidateDTO dto = new CandidateDTO();
            dto.setName(candidate.getName());
            dto.setEmail(candidate.getEmail());
            dto.setWhatsappNumber(candidate.getWhatsappNumber());
            dto.setRole(candidate.getRole());
            dto.setCompanyName(candidate.getCompanyName());
            dto.setPanelTiming(candidate.getPanelTiming());
            dto.setGmeetLink(candidate.getGMeetLink());
            dto.setInterviewerName(candidate.getInterviewerName());
            return dto;
        }

        private String getCellValue(Cell cell) {
            if (cell == null) {
                return "";
            }
            switch (cell.getCellType()) {
                case STRING:
                    return cell.getStringCellValue();
                case NUMERIC:
                    return String.valueOf((long) cell.getNumericCellValue());
                case BOOLEAN:
                    return String.valueOf(cell.getBooleanCellValue());
                default:
                    return "";
            }
        }

    @CacheEvict(value = "candidate", key = "#candidateId")
    @Transactional
    public void deleteCandidate(Long candidateId) {
        if (!candidateRepository.existsById(candidateId)) {
            throw new RuntimeException("Candidate not found : " + candidateId);
        }
        candidateRepository.deleteById(candidateId);
    }
    }


