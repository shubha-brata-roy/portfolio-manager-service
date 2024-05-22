package com.royhome.mystockplanningapp.controllers;

import com.royhome.mystockplanningapp.dtos.StockHoldingUnitDto;
import com.royhome.mystockplanningapp.dtos.StockInstrumentDto;
import com.royhome.mystockplanningapp.dtos.StockPriceDto;
import com.royhome.mystockplanningapp.models.stocks.StockInstrument;
import com.royhome.mystockplanningapp.services.StocksService;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/stocks")
public class StocksController {

    private final StocksService stocksService;

    @Autowired
    public StocksController(StocksService stocksService) {
        this.stocksService = stocksService;
    }

    @PostMapping("/holding-units/upload")
    public ResponseEntity<String> uploadStockHoldingUnits(@RequestParam("file") MultipartFile file,
                                                          HttpServletResponse response) {
        try {
            // Load the Excel file
            Workbook workbook = new XSSFWorkbook(file.getInputStream());

            // Read data from the Excel file
            List<StockHoldingUnitDto> stockHoldingUnits = new ArrayList<>();

            Sheet sheet = workbook.getSheetAt(0);

            for (Row row : sheet) {
                if(row.getRowNum() == 0) {
                    continue;
                }

                // Here the data for each record is getting read
                StockHoldingUnitDto holdingUnit = new StockHoldingUnitDto();

                holdingUnit.setStockInstrumentName(row.getCell(0).getStringCellValue());
                holdingUnit.setPurchasedDate((Date) row.getCell(1).getDateCellValue());
                holdingUnit.setPurchasedPrice((Double) row.getCell(2).getNumericCellValue());
                holdingUnit.setPurchasedQuantity((int) row.getCell(3).getNumericCellValue());
                stockHoldingUnits.add(holdingUnit);
            }

            // Process the data (e.g., save to database)

            stockHoldingUnits = stocksService.saveStockHoldingUnits(stockHoldingUnits);

            sheet.getRow(0).createCell(4).setCellValue("charges_and_taxes");
            sheet.getRow(0).createCell(5).setCellValue("status");
            int i = 1;
            for(StockHoldingUnitDto holdingUnit : stockHoldingUnits) {
                sheet.getRow(i).createCell(4).setCellValue(holdingUnit.getChargesAndTaxes());
                sheet.getRow(i).createCell(5).setCellValue(holdingUnit.getStatus());
                i++;
            }

            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.setHeader("Content-Disposition", "attachment; filename=stock-holding-units-upload-complete.xlsx");
            workbook.write(response.getOutputStream());

            workbook.close();
            return new ResponseEntity<>("Success: Upload Complete", HttpStatus.OK);
        } catch (IOException e) {
            e.printStackTrace();
            return new ResponseEntity<>("Error: File Upload Failure", HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (IllegalStateException e) {
            e.printStackTrace();
            return new ResponseEntity<>("Error: Excel Column Mismatch", HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/instruments/upload")
    public ResponseEntity<String> uploadStockInstruments(@RequestParam("file") MultipartFile file,
                                                         HttpServletResponse response) {
        try {
            // Load the Excel file
            Workbook workbook = new XSSFWorkbook(file.getInputStream());

            // Read data from the Excel file
            List<StockInstrumentDto> instruments = new ArrayList<>();

            Sheet sheet = workbook.getSheetAt(0);

            for (Row row : sheet) {
                if(row.getRowNum() == 0) {
                    continue;
                }

                // Here the data for each record is getting read
                StockInstrumentDto instrument = new StockInstrumentDto();

                instrument.setCategoryName(row.getCell(0).getStringCellValue());
                instrument.setIndustryName(row.getCell(1).getStringCellValue());
                instrument.setStockInstrumentName(row.getCell(2).getStringCellValue());
                instrument.setExchange(row.getCell(3).getStringCellValue());

                instruments.add(instrument);
            }

            // Process the data (e.g., save to database)

            instruments = stocksService.saveStockInstruments(instruments);

            sheet.getRow(0).createCell(4).setCellValue("status");
            int i = 1;
            for(StockInstrumentDto instrument : instruments) {
                sheet.getRow(i).createCell(4).setCellValue(instrument.getStatus());
                i++;
            }

            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.setHeader("Content-Disposition", "attachment; filename=stock-instruments-upload-complete.xlsx");
            workbook.write(response.getOutputStream());
            workbook.close();

            workbook.close();
            return new ResponseEntity<>("Success: Upload Complete", HttpStatus.OK);
        } catch (IOException e) {
            e.printStackTrace();
            return new ResponseEntity<>("Error: File Upload Failure", HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (IllegalStateException e) {
            e.printStackTrace();
            return new ResponseEntity<>("Error: Excel Column Mismatch", HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/instruments/current-price")
    public ResponseEntity<String> updateInstrumentsCurrentPrice(@RequestParam("file") MultipartFile file,
                                                                             HttpServletResponse response) {
        try {
            // Load the Excel file
            Workbook workbook = new XSSFWorkbook(file.getInputStream());

            // Read data from the Excel file
            List<StockPriceDto> stockPrices = new ArrayList<>();

            Sheet sheet = workbook.getSheetAt(0);

            for (Row row : sheet) {
                if (row.getRowNum() == 0) {
                    continue;
                }

                // Here the data for each record is getting read
                StockPriceDto stockPrice = new StockPriceDto();

                stockPrice.setStockInstrumentName(row.getCell(0).getStringCellValue());
                stockPrice.setCurrentPrice((Double) row.getCell(1).getNumericCellValue());

                stockPrices.add(stockPrice);
            }

            // Process the data (e.g., save to database)
            stockPrices = stocksService.updateStockInstruments(stockPrices);

            sheet.getRow(0).createCell(2).setCellValue("Status");
            int i = 1;
            for(StockPriceDto stockPrice : stockPrices) {
                sheet.getRow(i).createCell(2).setCellValue(stockPrice.getStatus());
                i++;
            }

            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.setHeader("Content-Disposition", "attachment; filename=stock-prices-update-complete.xlsx");
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
