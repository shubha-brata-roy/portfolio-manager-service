package com.royhome.mystockplanningapp.controllers;

import com.royhome.mystockplanningapp.dtos.*;
import com.royhome.mystockplanningapp.services.MutualFundsService;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/mutual-funds")
public class MutualFundsController {

    private final MutualFundsService mutualFundsService;

    @Autowired
    public MutualFundsController(MutualFundsService mutualFundsService) {
        this.mutualFundsService = mutualFundsService;
    }

    @PostMapping("/holding-units/upload")
    public ResponseEntity<String> uploadMutualFundHoldingUnits(@RequestParam("file") MultipartFile file,
                                                          HttpServletResponse response) {
        try {
            // Load the Excel file
            Workbook workbook = new XSSFWorkbook(file.getInputStream());

            // Read data from the Excel file
            List<MutualFundHoldingUnitDto> mutualFundHoldingUnitDtos = new ArrayList<>();

            Sheet sheet = workbook.getSheetAt(0);

            for (Row row : sheet) {
                if(row.getRowNum() == 0) {
                    continue;
                }

                // Here the data for each record is getting read
                MutualFundHoldingUnitDto holdingUnit = new MutualFundHoldingUnitDto();

                holdingUnit.setOwnerName(row.getCell(0).getStringCellValue());

                Cell cell = row.getCell(1);
                if(cell == null) {
                    holdingUnit.setIsin(null);
                } else {
                    holdingUnit.setIsin(row.getCell(1).getStringCellValue());
                }
                holdingUnit.setMutualFundInstrumentName(row.getCell(2).getStringCellValue());
                holdingUnit.setPlanType(row.getCell(3).getStringCellValue());
                holdingUnit.setTransactionType(row.getCell(4).getStringCellValue());

                cell = row.getCell(5);
                if(cell == null) {
                    holdingUnit.setSettlementId(null);
                } else {
                    holdingUnit.setSettlementId((long)row.getCell(5).getNumericCellValue());
                }
                holdingUnit.setPurchasedDate((Date) row.getCell(6).getDateCellValue());
                holdingUnit.setPurchasedAmount((Double) row.getCell(7).getNumericCellValue());
                holdingUnit.setPurchasedUnits((Double) row.getCell(8).getNumericCellValue());
                holdingUnit.setPurchasedNAV((Double) row.getCell(9).getNumericCellValue());

                cell = row.getCell(10);
                if(cell == null) {
                    holdingUnit.setExchangeOrderId(null);
                } else {
                    holdingUnit.setExchangeOrderId((long) row.getCell(10).getNumericCellValue());
                }

                mutualFundHoldingUnitDtos.add(holdingUnit);
            }

            // Process the data (e.g., save to database)

            mutualFundHoldingUnitDtos = mutualFundsService.saveMutualFundHoldingUnits(mutualFundHoldingUnitDtos);

            sheet.getRow(0).createCell(11).setCellValue("status");
            int i = 1;
            for(MutualFundHoldingUnitDto holdingUnit : mutualFundHoldingUnitDtos) {
                sheet.getRow(i).createCell(11).setCellValue(holdingUnit.getStatus());
                i++;
            }

            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.setHeader("Content-Disposition", "attachment; filename=mutual-fund-holding-units-upload-complete.xlsx");
            workbook.write(response.getOutputStream());

            workbook.close();
            return new ResponseEntity<>("File uploaded successfully", HttpStatus.OK);
        } catch (IOException e) {
            e.printStackTrace();
            return new ResponseEntity<>("Error: File Upload Failure", HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (IllegalStateException e) {
            e.printStackTrace();
            return new ResponseEntity<>("Error: Excel Column Mismatch", HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/instruments/upload")
    public ResponseEntity<String> uploadMutualFundInstruments(@RequestParam("file") MultipartFile file,
                                                         HttpServletResponse response) {
        try {
            // Load the Excel file
            Workbook workbook = new XSSFWorkbook(file.getInputStream());

            // Read data from the Excel file
            List<MutualFundInstrumentDto> mutualFundInstrumentDtos = new ArrayList<>();

            Sheet sheet = workbook.getSheetAt(0);

            for (Row row : sheet) {
                if(row.getRowNum() == 0) {
                    continue;
                }

                // Here the data for each record is getting read
                MutualFundInstrumentDto instrumentDto = new MutualFundInstrumentDto();

                instrumentDto.setCategoryName(row.getCell(0).getStringCellValue());
                instrumentDto.setSubCategoryName(row.getCell(1).getStringCellValue());
                instrumentDto.setMutualFundInstrumentName(row.getCell(2).getStringCellValue());
                instrumentDto.setPlanType(row.getCell(3).getStringCellValue());
                instrumentDto.setFolioNumber(row.getCell(4).getStringCellValue());
                mutualFundInstrumentDtos.add(instrumentDto);
            }

            // Process the data (e.g., save to database)

            mutualFundInstrumentDtos = mutualFundsService.saveMutualFundInstruments(mutualFundInstrumentDtos);

            sheet.getRow(0).createCell(5).setCellValue("status");
            int i = 1;
            for(MutualFundInstrumentDto instrumentDto : mutualFundInstrumentDtos) {
                sheet.getRow(i).createCell(5).setCellValue(instrumentDto.getStatus());
                i++;
            }

            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.setHeader("Content-Disposition", "attachment; filename=mutual-fund-instruments-upload-complete.xlsx");
            workbook.write(response.getOutputStream());

            workbook.close();
            return new ResponseEntity<>("File uploaded successfully", HttpStatus.OK);
        } catch (IOException e) {
            e.printStackTrace();
            return new ResponseEntity<>("Error: File Upload Failure", HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (IllegalStateException e) {
            e.printStackTrace();
            return new ResponseEntity<>("Error: Excel Column Mismatch", HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/instruments/current-market-value")
    public ResponseEntity<String> updateInstrumentsCurrentMarketValue(@RequestParam("file") MultipartFile file,
                                                                HttpServletResponse response) {
        try {
            // Load the Excel file
            Workbook workbook = new XSSFWorkbook(file.getInputStream());

            // Read data from the Excel file
            List<MutualFundCurrentMarketValueDto> currentMarketValueDtos = new ArrayList<>();

            Sheet sheet = workbook.getSheetAt(0);

            for (Row row : sheet) {
                if (row.getRowNum() == 0) {
                    continue;
                }

                // Here the data for each record is getting read
                MutualFundCurrentMarketValueDto currentMarketValue = new MutualFundCurrentMarketValueDto();

                currentMarketValue.setMutualFundInstrumentName(row.getCell(0).getStringCellValue());
                currentMarketValue.setPlanType(row.getCell(1).getStringCellValue());
                currentMarketValue.setTotalCurrentMarketValue((Double) row.getCell(2).getNumericCellValue());
                currentMarketValue.setCurrentNAV((Double) row.getCell(3).getNumericCellValue());

                currentMarketValueDtos.add(currentMarketValue);
            }

            // Process the data (e.g., save to database)
            currentMarketValueDtos = mutualFundsService.updateStockInstruments(currentMarketValueDtos);

            sheet.getRow(0).createCell(4).setCellValue("Status");
            int i = 1;
            for(MutualFundCurrentMarketValueDto currentMarketValue : currentMarketValueDtos) {
                sheet.getRow(i).createCell(4).setCellValue(currentMarketValue.getStatus());
                i++;
            }

            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.setHeader("Content-Disposition", "attachment; filename=mutual-funds-current-market-value-update-complete.xlsx");
            workbook.write(response.getOutputStream());
            workbook.close();

            return new ResponseEntity<>("Success: Update Complete", HttpStatus.OK);
        } catch (IOException e) {
            e.printStackTrace();
            return new ResponseEntity<>("Error: File Upload Failure", HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (IllegalStateException e) {
            e.printStackTrace();
            return new ResponseEntity<>("Error: Excel Column Mismatch", HttpStatus.BAD_REQUEST);
        }
    }
}
