package com.royhome.mystockplanningapp.controllers;

import com.royhome.mystockplanningapp.dtos.HoldingUnitGrowthMetricsDto;
import com.royhome.mystockplanningapp.dtos.InstrumentGrowthMetricsDto;
import com.royhome.mystockplanningapp.dtos.MetricsComparisonDto;
import com.royhome.mystockplanningapp.services.GrowthMetricsCalculationService;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/metrics")
public class GrowthMetricsCalculationController {

    private final GrowthMetricsCalculationService growthMetricsCalculationService;

    @Autowired
    public GrowthMetricsCalculationController(GrowthMetricsCalculationService growthMetricsCalculationService) {
        this.growthMetricsCalculationService = growthMetricsCalculationService;
    }

    /**
     * This API is called by the client to retrieve the Stock & Mutual Fund Instrument names
     * @return List of GrowthMetricsDto
    **/
    @GetMapping("/instruments")
    public ResponseEntity<List<InstrumentGrowthMetricsDto>> getInstruments() {
        List<InstrumentGrowthMetricsDto> instruments = growthMetricsCalculationService.getInstruments();
        return new ResponseEntity<>(instruments, HttpStatus.OK);
    }

    /**
     * Generate transaction xls sheet to calculate XIRR
     * Request Payload includes the instrument name and category
     * Response Payload will include the "date" and "transaction amount",
     * such that, the SIP deposits are negative and withdrawals or current value is marked positive
     **/
    @PostMapping("/generate-sheet")
    public ResponseEntity<Void> generateXIRRCalculationSheet(@RequestBody InstrumentGrowthMetricsDto instrument,
                                                                HttpServletResponse response) {
        List<HoldingUnitGrowthMetricsDto> records = growthMetricsCalculationService.generateXIRRCalculationSheet(instrument);

        /**
         * The records are then written to an Excel file
         * The Excel file is then sent back to client
         * The client can then download the Excel file
         **/
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("XIRR Calculation");

            // Create header row
            Row headerRow = sheet.createRow(0);
            headerRow.createCell(0).setCellValue("Date");
            headerRow.createCell(1).setCellValue("Amount");

            // Populate data rows
            int rowNum = 1;
            for (HoldingUnitGrowthMetricsDto record : records) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(record.getDate().toString());
                row.createCell(1).setCellValue(record.getAmount());
            }

            // Set response headers
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.setHeader("Content-Disposition", "attachment; filename=" + instrument.getName() + ".xlsx");

            // Write workbook to response output stream
            workbook.write(response.getOutputStream());
            response.flushBuffer();
        } catch (IOException e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }

    /**
     * Generate transaction xls sheet to calculate XIRR
     * NO REQUEST PAYLOAD
     * This method helps calculate XIRR for entire portfolio or by category - stocks or mutual funds
     * Response Payload will include the "date" and "transaction amount",
     * such that, the SIP deposits are negative and withdrawals or current value is marked positive
     **/
    @PostMapping("/generate-master-sheet")
    public ResponseEntity<Void> generateXIRRCalculationSheet(HttpServletResponse response) {
        List<HoldingUnitGrowthMetricsDto> records = growthMetricsCalculationService.generateXIRRCalculationSheet();

        /**
         * The records are then written to an Excel file
         * The Excel file is then sent back to client
         * The client can then download the Excel file
         **/
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("XIRR Calculation");

            // Create header row
            Row headerRow = sheet.createRow(0);
            headerRow.createCell(0).setCellValue("Date");
            headerRow.createCell(1).setCellValue("Amount");
            headerRow.createCell(2).setCellValue("Category");

            // Populate data rows
            int rowNum = 1;
            for (HoldingUnitGrowthMetricsDto record : records) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(record.getDate());
                row.createCell(1).setCellValue(record.getAmount());
                row.createCell(2).setCellValue(record.getCategory());
            }

            // Set response headers
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.setHeader("Content-Disposition", "attachment; filename=xirr-calculation.xlsx");

            // Write workbook to response output stream
            workbook.write(response.getOutputStream());
            response.flushBuffer();
        } catch (IOException e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }

    /**
     * This API is called by the client to submit the XIRR value for the given instrument
     **/
    @PostMapping("/submit-xirr")
    public ResponseEntity<InstrumentGrowthMetricsDto> submitXIRR(@RequestBody InstrumentGrowthMetricsDto instrument) {
        InstrumentGrowthMetricsDto growthMetrics = growthMetricsCalculationService.submitXIRR(instrument);
        return new ResponseEntity<>(growthMetrics, HttpStatus.OK);
    }

    /**
     * This GET API is called by the client to get the MetricsComparisonDto elements
     **/
    @GetMapping("/comparison")
    public ResponseEntity<List<MetricsComparisonDto>> getComparisonMetrics() {
        List<MetricsComparisonDto> comparisonMetrics = growthMetricsCalculationService.getComparisonMetrics();
        return new ResponseEntity<>(comparisonMetrics, HttpStatus.OK);
    }
}
