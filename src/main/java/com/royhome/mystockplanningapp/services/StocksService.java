package com.royhome.mystockplanningapp.services;

import com.royhome.mystockplanningapp.commons.ChargesAndTaxes;
import com.royhome.mystockplanningapp.dtos.StockHoldingUnitDto;
import com.royhome.mystockplanningapp.dtos.StockInstrumentDto;
import com.royhome.mystockplanningapp.dtos.StockPriceDto;
import com.royhome.mystockplanningapp.models.Category;
import com.royhome.mystockplanningapp.models.Industry;
import com.royhome.mystockplanningapp.models.Owner;
import com.royhome.mystockplanningapp.models.stocks.StockHoldingUnit;
import com.royhome.mystockplanningapp.models.stocks.StockInstrument;
import com.royhome.mystockplanningapp.repositories.OwnerRepository;
import com.royhome.mystockplanningapp.repositories.StockHoldingUnitRepository;
import com.royhome.mystockplanningapp.repositories.StockInstrumentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class StocksService {

    private final StockInstrumentRepository stockInstrumentRepository;
    private final StockHoldingUnitRepository stockHoldingUnitRepository;
    private final OwnerRepository ownerRepository;

    @Autowired
    public StocksService(StockInstrumentRepository stockInstrumentRepository,
                         StockHoldingUnitRepository stockHoldingUnitRepository,
                         OwnerRepository ownerRepository) {
        this.stockInstrumentRepository = stockInstrumentRepository;
        this.stockHoldingUnitRepository = stockHoldingUnitRepository;
        this.ownerRepository = ownerRepository;
    }

    public List<StockInstrumentDto> saveStockInstruments(List<StockInstrumentDto> instruments) {
        for(StockInstrumentDto instrument : instruments) {
            if(!Category.contains(instrument.getCategoryName())) {
                instrument.setStatus("Error: Invalid Category Name");
                continue;
            }

            if(!Industry.contains(instrument.getIndustryName())) {
                instrument.setStatus("Error: Invalid Industry Name");
                continue;
            }

            Optional<StockInstrument> stockInstrumentOptional = stockInstrumentRepository.findStockInstrumentByName(
                                                                                instrument.getStockInstrumentName());

            if(stockInstrumentOptional.isPresent()) {
                instrument.setStatus("Error: Instrument Already Exists");
                continue;
            }

            StockInstrument stockInstrument = new StockInstrument();
            stockInstrument.setName(instrument.getStockInstrumentName());
            stockInstrument.setCategory(Category.valueOf(instrument.getCategoryName()));
            stockInstrument.setIndustry(Industry.valueOf(instrument.getIndustryName()));
            stockInstrument.setExchange(instrument.getExchange());

            stockInstrument.setCreatedAt(new Date());
            stockInstrument.setUpdatedAt(new Date());
            // set average purchased price logic: This should be changed whenever a holding unit is added
            // set lowest purchased price logic: This should be changed whenever a holding unit is added, also add the date when the lowest price is updated
            // set highest purchased price logic: This should be changed whenever a holding unit is added, also add the date when the lowest price is updated
            // set last purchased price logic: This should be changed whenever a holding unit is added, also add the date when the last price is updated

            stockInstrumentRepository.save(stockInstrument);
            instrument.setStatus("Success: Saved");
        }

        return instruments;
    }

    public List<StockPriceDto> updateStockInstruments(List<StockPriceDto> stockPrices) {
        for(StockPriceDto stockPrice : stockPrices) {
            Optional<StockInstrument> stockInstrumentOptional = stockInstrumentRepository.findStockInstrumentByName(
                                                                                stockPrice.getStockInstrumentName());

            if(stockInstrumentOptional.isEmpty()) {
                stockPrice.setStatus("Error: Invalid Instrument Name");
                continue;
            }

            StockInstrument stockInstrument = stockInstrumentOptional.get();

            stockInstrument.setCurrentPrice(stockPrice.getCurrentPrice());
            stockInstrument.setCurrentPriceUpdatedOn(new Date());

            double totalCurrentMarketValue = stockInstrument.getTotalHoldingQuantity() * stockInstrument.getCurrentPrice();
            stockInstrument.setTotalCurrentMarketValue(totalCurrentMarketValue);
            stockInstrument.setTotalCurrentMarketValueUpdatedOn(new Date());

            stockInstrument.setUpdatedAt(new Date());
            stockInstrumentRepository.save(stockInstrument);

            stockPrice.setStatus("Success: Updated");
        }

        return stockPrices;
    }

    public List<StockHoldingUnitDto> saveStockHoldingUnits(List<StockHoldingUnitDto> stockHoldingUnits) {
        for(StockHoldingUnitDto stockHoldingUnitDto : stockHoldingUnits) {

            StockHoldingUnit holdingUnit = new StockHoldingUnit();

            Optional<StockInstrument> stockInstrumentOptional = stockInstrumentRepository.findStockInstrumentByName(
                                                                            stockHoldingUnitDto.getStockInstrumentName());

            if(stockInstrumentOptional.isEmpty()) {
                stockHoldingUnitDto.setStatus("Error: Invalid Instrument Name");
                continue;
            }

            StockInstrument stockInstrument = stockInstrumentOptional.get();

            // set the owner name for the holding unit
            Optional<Owner> ownerOptional = ownerRepository.findOwnerByName(stockHoldingUnitDto.getOwnerName());

            if(ownerOptional.isEmpty()) {
                stockHoldingUnitDto.setStatus("Error: Invalid Owner Name");
                continue;
            }

            holdingUnit.setOwner(ownerOptional.get());

            // total amount of purchase in this holding unit
            double purchasedValue = stockHoldingUnitDto.getPurchasedPrice() * stockHoldingUnitDto.getPurchasedQuantity();

            // total amount invested in the stock instrument so far
            double totalAmountInvested = stockInstrument.getTotalAmountInvested() == null ?
                                            0d : stockInstrument.getTotalAmountInvested();

            // new total amount invested after adding the purchasedValue
            totalAmountInvested += purchasedValue;

            stockInstrument.setTotalAmountInvested(totalAmountInvested);
            stockInstrument.setTotalAmountInvestedUpdatedOn(new Date());

            // calculate and save Charges and Taxes
            Double securitiesTransactionTax = purchasedValue * ChargesAndTaxes.SECURITIES_TRANSACTION_TAX_RATE;
            Double nseTransactionCharges = purchasedValue * ChargesAndTaxes.NSE_TRANSACTION_CHARGES;
            Double sebiCharges = purchasedValue * ChargesAndTaxes.SEBI_CHARGES;
            Double stampDutyCharges = purchasedValue * ChargesAndTaxes.STOCK_STAMP_DUTY_CHARGES;
            Double gst = (nseTransactionCharges + sebiCharges) * ChargesAndTaxes.GST_RATE;

            Double totalCharges = securitiesTransactionTax + nseTransactionCharges + sebiCharges + stampDutyCharges + gst;
            holdingUnit.setSecuritiesTransactionTax(securitiesTransactionTax);
            holdingUnit.setNseTransactionCharges(nseTransactionCharges);
            holdingUnit.setSebiCharges(sebiCharges);
            holdingUnit.setStampDutyCharges(stampDutyCharges);
            holdingUnit.setGst(gst);
            holdingUnit.setTotalCharges(totalCharges);

            // purchased Date, Price and Quantity saved in Holding unit
            holdingUnit.setPurchasedDate(stockHoldingUnitDto.getPurchasedDate());
            holdingUnit.setPurchasedPrice(stockHoldingUnitDto.getPurchasedPrice());
            holdingUnit.setPurchasedQuantity(stockHoldingUnitDto.getPurchasedQuantity());

            holdingUnit.setCreatedAt(new Date());
            holdingUnit.setUpdatedAt(new Date());

            // set average purchased price logic: This should be changed whenever a holding unit is added
            int currentHoldingQuantity = stockInstrument.getTotalHoldingQuantity() == null ?
                                                                        0 : stockInstrument.getTotalHoldingQuantity();
            int newHoldingQuantity = currentHoldingQuantity + stockHoldingUnitDto.getPurchasedQuantity();
            double currentAveragePurchasedPrice = stockInstrument.getAveragePurchasedPrice() == null ?
                                                                        0d : stockInstrument.getAveragePurchasedPrice();

            double newAverage = 0d;
            if(newHoldingQuantity != 0) {
                newAverage = ((currentAveragePurchasedPrice * currentHoldingQuantity) + purchasedValue) / newHoldingQuantity;
            }

            stockInstrument.setTotalHoldingQuantity(newHoldingQuantity);
            stockInstrument.setAveragePurchasedPrice(newAverage);

            double purchasedPrice = holdingUnit.getPurchasedPrice();
            // set last purchased price logic: This should be changed whenever a holding unit is added, also add the date when the last price is updated
            Date lastPurchasedDate = stockInstrument.getLastPurchasedOn();

            if(purchasedPrice != 0d && (lastPurchasedDate == null || lastPurchasedDate.before(holdingUnit.getPurchasedDate()))) {
                stockInstrument.setLastPurchasedOn(holdingUnit.getPurchasedDate());
                stockInstrument.setLastPurchasedPrice(purchasedPrice);
            }

            // set lowest purchased price logic: This should be changed whenever a holding unit is added, also add the date when the lowest price is updated
            Double lowestPurchasedPrice = stockInstrument.getLowestPurchasedPrice();

            if(purchasedPrice != 0d && (lowestPurchasedPrice == null || lowestPurchasedPrice > purchasedPrice)) {
                stockInstrument.setLowestPurchasedPrice(purchasedPrice);
                stockInstrument.setLowestPurchasedOn(holdingUnit.getPurchasedDate());
            }

            // set highest purchased price logic: This should be changed whenever a holding unit is added, also add the date when the lowest price is updated
            Double highestPurchasedPrice = stockInstrument.getHighestPurchasedPrice();

            if(highestPurchasedPrice == null || highestPurchasedPrice < holdingUnit.getPurchasedPrice()) {
                stockInstrument.setHighestPurchasedPrice(holdingUnit.getPurchasedPrice());
                stockInstrument.setHighestPurchaseOn(holdingUnit.getPurchasedDate());
            }

            stockInstrumentRepository.save(stockInstrument);

            holdingUnit.setStockInstrument(stockInstrument);

            stockHoldingUnitRepository.save(holdingUnit);

            stockHoldingUnitDto.setChargesAndTaxes(totalCharges);
            stockHoldingUnitDto.setStatus("Success: Saved");
        }
        return stockHoldingUnits;
    }

    public List<StockPriceDto> getInstrumentsCurrentPrice() {
        List<StockInstrument> stockInstruments = stockInstrumentRepository.findAll();
        List<StockPriceDto> stockPrices = new ArrayList<>();

        for(StockInstrument stockInstrument : stockInstruments) {
            StockPriceDto stockPrice = new StockPriceDto();
            stockPrice.setStockInstrumentName(stockInstrument.getName());
            stockPrice.setCurrentPrice(stockInstrument.getCurrentPrice());
            stockPrices.add(stockPrice);
        }

        return stockPrices;
    }
}
