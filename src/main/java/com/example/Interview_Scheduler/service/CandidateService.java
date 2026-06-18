package com.example.Interview_Scheduler.service;

import com.example.Interview_Scheduler.dto.CandidateDTO;
import com.example.Interview_Scheduler.exception.ExcelParseException;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class CandidateService {

        @Autowired
        private final CandidateRepository candidateRepository;

        @Autowired
        private BatchRepository batchRepository;

        public List<CandidateDTO> getCandidatesByBatch(Long batchId) {
            List<Candidate> candidates = candidateRepository.findByBatchId(batchId);

            if (candidates.isEmpty()) {
                throw new ExcelParseException(
                        "No candidates found for batch : " + batchId
                );
            }
            return candidates.stream()
                    .map(this::mapToDto)
                    .toList();
        }


        public CandidateDTO getCandidate(Long id) {
            Candidate candidate = candidateRepository.findById(id)
                            .orElseThrow(() ->
                                    new ExcelParseException(
                                            "Candidate Not Found : " + id
                                    ));
            return mapToDto(candidate);
        }
        @Transactional
        public void deleteBatch(Long batchId) {

            log.info("Deleting candidates for Batch ID : {}", batchId);
            candidateRepository.deleteByBatchId(batchId);
            log.info("Batch deleted successfully : {}", batchId);
        }
        @Transactional
        public String uploadCandidates(MultipartFile file) {

            log.info("Excel Upload Started : {}",
                    file.getOriginalFilename());
            validateExcelFile(file);

            int totalCandidates = countRowsInExcel(file);
            BatchModel batchInfo = new BatchModel();
            batchInfo.setFileName(file.getOriginalFilename());
            batchInfo.setTotalCandidates(totalCandidates);
            batchInfo.setStatus(BatchStatus.UPLOADED);
            batchInfo.setCreatedAt(LocalDateTime.now());
            batchRepository.save(batchInfo);

            try (
                    InputStream inputStream = file.getInputStream();
                    Workbook workbook =
                            new XSSFWorkbook(inputStream)
            ) {

                Sheet sheet = workbook.getSheetAt(0);

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
                    String panelTime = getCellValue(row.getCell(5));
                    String meetLink = getCellValue(row.getCell(6));
                    String interviewer = getCellValue(row.getCell(7));

                    Candidate  candidate = new Candidate();
                    candidate.setName(name);
                    candidate.setEmail(email);
                    candidate.setWhatsappNumber(whatsapp);
                    candidate.setRole(role);
                    candidate.setCompanyName(company);

                    if (!panelTime.isBlank()) {

                        candidate.setPanelTiming(
                                LocalDateTime.parse(panelTime)
                        );
                    }
                    candidate.setGMeetLink(meetLink);
                    candidate.setInterviewerName(interviewer);
                    candidate.setCreatedAt(LocalDateTime.now());

                    candidateRepository.save(candidate);
                }

                log.info("Excel Upload Completed");
                return "Excel Uploaded Successfully";
            }
            catch (Exception e) {
                log.error("Excel Upload Failed", e);
                throw new ExcelParseException(
                        "Error Processing Excel File"
                );
            }
        }

    private void validateExcelFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new ExcelParseException("File is empty!");
        }

        String fileName = file.getOriginalFilename();
        if (fileName == null || (!fileName.endsWith(".xlsx") && !fileName.endsWith(".xls"))) {
            throw new ExcelParseException("Only Excel files (.xlsx, .xls) are allowed!");
        }
    }

    private int countRowsInExcel(MultipartFile file) {
        try (InputStream inputStream = file.getInputStream();
             Workbook workbook = new XSSFWorkbook(inputStream)) {

            Sheet sheet = workbook.getSheetAt(0);
            int totalRows = sheet.getLastRowNum();

            return totalRows > 0 ? totalRows : 0;

        } catch (Exception e) {
            throw new ExcelParseException("Failed to read Excel file: " + e.getMessage());
        }
    }

        private CandidateDTO mapToDto(
                Candidate candidate
        ) {

            CandidateDTO dto = new CandidateDTO();

            dto.setName(candidate.getName());
            dto.setEmail(candidate.getEmail());
            dto.setWhatsappNumber(
                    candidate.getWhatsappNumber()
            );
            dto.setRole(candidate.getRole());
            dto.setCompanyName(
                    candidate.getCompanyName()
            );

            dto.setPanelTiming(
                    candidate.getPanelTiming()
            );

            dto.setGmeetLink(
                    candidate.getGMeetLink()
            );

            dto.setInterviewerName(
                    candidate.getInterviewerName()
            );

            return dto;
        }

        private Candidate mapToEntity(
                CandidateDTO dto
        ) {
            Candidate candidate = new Candidate();

            candidate.setName(dto.getName());
            candidate.setEmail(dto.getEmail());
            candidate.setWhatsappNumber(
                    dto.getWhatsappNumber()
            );
            candidate.setRole(dto.getRole());
            candidate.setCompanyName(
                    dto.getCompanyName()
            );

            candidate.setPanelTiming(
                    dto.getPanelTiming()
            );

            candidate.setGMeetLink(
                    dto.getGmeetLink()
            );

            candidate.setInterviewerName(
                    dto.getInterviewerName()
            );

            return candidate;
        }

        private String getCellValue(Cell cell) {

            if (cell == null) {
                return "";
            }

            switch (cell.getCellType()) {

                case STRING:
                    return cell.getStringCellValue();

                case NUMERIC:
                    return String.valueOf(
                            (long) cell.getNumericCellValue()
                    );

                case BOOLEAN:
                    return String.valueOf(
                            cell.getBooleanCellValue()
                    );
                default:
                    return "";
            }
        }
    }

    /*private final CandidateRepository candidateRepository;

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
*/