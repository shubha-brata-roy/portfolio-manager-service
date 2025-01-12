package com.royhome.mystockplanningapp.services;

import com.royhome.mystockplanningapp.dtos.HoldingUnitGrowthMetricsDto;
import com.royhome.mystockplanningapp.dtos.InstrumentGrowthMetricsDto;
import com.royhome.mystockplanningapp.dtos.MetricsComparisonDto;
import com.royhome.mystockplanningapp.dtos.MutualFundInstrumentDto;
import com.royhome.mystockplanningapp.models.Category;
import com.royhome.mystockplanningapp.models.mutualfunds.MutualFundHoldingUnit;
import com.royhome.mystockplanningapp.models.mutualfunds.MutualFundInstrument;
import com.royhome.mystockplanningapp.models.stocks.StockHoldingUnit;
import com.royhome.mystockplanningapp.models.stocks.StockInstrument;
import com.royhome.mystockplanningapp.repositories.MutualFundHoldingUnitRepository;
import com.royhome.mystockplanningapp.repositories.MutualFundInstrumentRepository;
import com.royhome.mystockplanningapp.repositories.StockHoldingUnitRepository;
import com.royhome.mystockplanningapp.repositories.StockInstrumentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class GrowthMetricsCalculationService {
    private StockInstrumentRepository stockInstrumentRepository;
    private MutualFundInstrumentRepository mutualFundInstrumentRepository;
    private StockHoldingUnitRepository stockHoldingUnitRepository;
    private MutualFundHoldingUnitRepository mutualFundHoldingUnitRepository;

    @Autowired
    public GrowthMetricsCalculationService(StockHoldingUnitRepository stockHoldingUnitRepository,
                                           MutualFundHoldingUnitRepository mutualFundHoldingUnitRepository,
                                           StockInstrumentRepository stockInstrumentRepository,
                                           MutualFundInstrumentRepository mutualFundInstrumentRepository) {
        this.stockHoldingUnitRepository = stockHoldingUnitRepository;
        this.mutualFundHoldingUnitRepository = mutualFundHoldingUnitRepository;
        this.stockInstrumentRepository = stockInstrumentRepository;
        this.mutualFundInstrumentRepository = mutualFundInstrumentRepository;
    }

    /**
     * This method loops through all the StockInstrument records, and pulls the Stock Name
     * Then it loops through all the MutualFundInstrument records, and pulls the Mutual Fund Name
     * Populates the value into the InstrumentGrowthMetricsDto with the Name and the Category - Stock or Mutual Fund
     * @return List of InstrumentGrowthMetricsDto
     **/
    public List<InstrumentGrowthMetricsDto> getInstruments() {
        List<InstrumentGrowthMetricsDto> instruments = new ArrayList<>();

        // Get all the Stock Instruments
        List<StockInstrument> stockInstruments = stockInstrumentRepository.findAll();
        for (StockInstrument stockInstrument : stockInstruments) {

            InstrumentGrowthMetricsDto instrument = new InstrumentGrowthMetricsDto();

            instrument.setId(stockInstrument.getId());
            instrument.setName(stockInstrument.getName());
            instrument.setCategory(stockInstrument.getCategory().toString());

            instruments.add(instrument);
        }

        // Get all the Mutual Fund Instruments
        List<MutualFundInstrument> mutualFundInstruments = mutualFundInstrumentRepository.findAll();
        for (MutualFundInstrument mutualFundInstrument : mutualFundInstruments) {

            InstrumentGrowthMetricsDto instrument = new InstrumentGrowthMetricsDto();

            instrument.setId(mutualFundInstrument.getId());
            instrument.setName(mutualFundInstrument.getName());
            instrument.setCategory(mutualFundInstrument.getCategory().toString());

            instruments.add(instrument);
        }

        return instruments;
    }

    /**
     * This method generates the XIRR calculation sheet for the given instrument
     * This method has an if-else condition where it checks for the Category and then calls the respective repository
     * It pulls all the dates and the amount for each of the Holding Units and stores in the HoldingUnitGrowthMetricsDto
     * Return this list back to the controller
     **/
    public List<HoldingUnitGrowthMetricsDto> generateXIRRCalculationSheet(InstrumentGrowthMetricsDto instrument) {
        List<HoldingUnitGrowthMetricsDto> records = new ArrayList<>();
        SimpleDateFormat date = new SimpleDateFormat(("dd-MMM-yy"));

        if(instrument.getCategory().equals((Category.EQUITY_STOCK).toString()) ||
            instrument.getCategory().equals((Category.DEBT_STOCK).toString()) ||
            instrument.getCategory().equals((Category.EXCHANGE_TRADED_FUND).toString())) {

            // Get all the Stock Holding Units
            List<StockHoldingUnit> stockHoldingUnits = stockHoldingUnitRepository.findStockHoldingUnitsByStockInstrumentId(instrument.getId());

            // Fill in the HoldingUnitGrowthMetricsDto records

            for(StockHoldingUnit stockHoldingUnit : stockHoldingUnits) {
                HoldingUnitGrowthMetricsDto record = new HoldingUnitGrowthMetricsDto();
                record.setDate(date.format(stockHoldingUnit.getPurchasedDate()));
                record.setAmount(stockHoldingUnit.getPurchasedPrice() * stockHoldingUnit.getPurchasedQuantity() * -1);
                records.add(record);
            }

            StockInstrument stockInstrument = stockInstrumentRepository.findById(instrument.getId());
            HoldingUnitGrowthMetricsDto record = new HoldingUnitGrowthMetricsDto();
            record.setDate(date.format(new Date()));
            record.setAmount(stockInstrument.getTotalCurrentMarketValue());
            records.add(record);

        } else {
            // Get all the Mutual Fund Holding Units
            List<MutualFundHoldingUnit> mutualFundHoldingUnits = mutualFundHoldingUnitRepository.findMutualFundHoldingUnitsByMutualFundInstrumentId(instrument.getId());

            // Fill in the HoldingUnitGrowthMetricsDto records

            for(MutualFundHoldingUnit mutualFundHoldingUnit : mutualFundHoldingUnits) {
                HoldingUnitGrowthMetricsDto record = new HoldingUnitGrowthMetricsDto();
                record.setDate(date.format(mutualFundHoldingUnit.getPurchasedDate()));
                record.setAmount(mutualFundHoldingUnit.getPurchasedAmount() * -1);
                records.add(record);
            }

            MutualFundInstrument mutualFundInstrument = mutualFundInstrumentRepository.findById(instrument.getId());
            HoldingUnitGrowthMetricsDto record = new HoldingUnitGrowthMetricsDto();
            record.setDate(date.format(new Date()));
            record.setAmount(mutualFundInstrument.getTotalCurrentMarketValue());
            records.add(record);
        }
        return records;
    }

    /**
     * This method helps calculate XIRR for entire portfolio or by category - stocks or mutual funds
     * This method returns all the transactions for all the instruments along with the Category column
     * This additional column will help to check the XIRR by category and also for all the instruments
     **/
    public List<HoldingUnitGrowthMetricsDto> generateXIRRCalculationSheet() {
        List<HoldingUnitGrowthMetricsDto> records = new ArrayList<>();
        SimpleDateFormat date = new SimpleDateFormat(("dd-MMM-yy"));

        // Get all the Stock Holding Units
        List<StockHoldingUnit> stockHoldingUnits = stockHoldingUnitRepository.findAll();

        // Fill in the HoldingUnitGrowthMetricsDto records
        for(StockHoldingUnit stockHoldingUnit : stockHoldingUnits) {
            HoldingUnitGrowthMetricsDto record = new HoldingUnitGrowthMetricsDto();
            record.setDate(date.format(stockHoldingUnit.getPurchasedDate()));
            record.setAmount(stockHoldingUnit.getPurchasedPrice() * stockHoldingUnit.getPurchasedQuantity() * -1);
            record.setCategory("stock");
            records.add(record);
        }

        double totalCurrentMarketValue = stockInstrumentRepository.findTotalCurrentMarketValue();
        HoldingUnitGrowthMetricsDto stockRecord = new HoldingUnitGrowthMetricsDto();
        stockRecord.setDate(date.format(new Date()));
        stockRecord.setAmount(totalCurrentMarketValue);
        stockRecord.setCategory("stock");
        records.add(stockRecord);


        // Get all the Mutual Fund Holding Units
        List<MutualFundHoldingUnit> mutualFundHoldingUnits = mutualFundHoldingUnitRepository.findAll();

        // Fill in the HoldingUnitGrowthMetricsDto records

        for(MutualFundHoldingUnit mutualFundHoldingUnit : mutualFundHoldingUnits) {
            HoldingUnitGrowthMetricsDto record = new HoldingUnitGrowthMetricsDto();
            record.setDate(date.format(mutualFundHoldingUnit.getPurchasedDate()));
            record.setAmount(mutualFundHoldingUnit.getPurchasedAmount() * -1);
            record.setCategory("mutual fund");
            records.add(record);
        }

        totalCurrentMarketValue = mutualFundInstrumentRepository.findTotalCurrentMarketValue();
        HoldingUnitGrowthMetricsDto mfRecord = new HoldingUnitGrowthMetricsDto();
        mfRecord.setDate(date.format(new Date()));
        mfRecord.setAmount(totalCurrentMarketValue);
        records.add(mfRecord);

        return records;
    }

    /**
     * This method submits the XIRR for the given instrument and returns the response
     **/
    public InstrumentGrowthMetricsDto submitXIRR(InstrumentGrowthMetricsDto instrument) {

        if(instrument.getCategory().equals((Category.EQUITY_STOCK).toString()) ||
                instrument.getCategory().equals((Category.DEBT_STOCK).toString()) ||
                instrument.getCategory().equals((Category.EXCHANGE_TRADED_FUND).toString())) {

            StockInstrument stockInstrument = stockInstrumentRepository.findById(instrument.getId());

            stockInstrument.setXirr(instrument.getXirr());
            stockInstrumentRepository.save(stockInstrument);
        } else {
            MutualFundInstrument mutualFundInstrument = mutualFundInstrumentRepository.findById(instrument.getId());

            mutualFundInstrument.setXirr(instrument.getXirr());
            mutualFundInstrumentRepository.save(mutualFundInstrument);
        }

        return instrument;
    }

    /**
     * This method runs through all the StockInstruments and MutualFundInstruments
     * It calculates the sum(totalAmountInvested) and sum(totalCurrentMarketValue) for all stocks and mutual funds
     * It Pulls the id, name, category, subCategory, planType, netReturnRate, xirr, totalAmountInvested, totalCurrentMarketValue
     * It then calculates the shareInPortfolioOfTotalInvested = totalAmountInvested / sum(totalAmountInvested)
     * and shareInPortfolioOfTotalMarketValue = totalCurrentMarketValue / sum(totalCurrentMarketValue)
     * Prepares the MetricsComparisonDto list for all instruments and returns back to the controller
     **/
    public List<MetricsComparisonDto> getComparisonMetrics() {
        List<MetricsComparisonDto> comparisonMetrics = new ArrayList<>();

        List<StockInstrument> stockInstruments = stockInstrumentRepository.findAll();

        List<MutualFundInstrument> mutualFundInstruments = mutualFundInstrumentRepository.findAll();

        /*
         * Calculate the totalAmountInvested and totalCurrentMarketValue for all the StockInstruments and MutualFundInstruments
         */
        double totalAmountInvested = 0;
        double totalMarketValue = 0;

        for(StockInstrument stockInstrument : stockInstruments) {
            totalAmountInvested += stockInstrument.getTotalAmountInvested();
            totalMarketValue += stockInstrument.getTotalCurrentMarketValue();
        }

        for(MutualFundInstrument mutualFundInstrument : mutualFundInstruments) {
            if(mutualFundInstrument.getTotalAmountInvested() > 0) {
                totalAmountInvested += mutualFundInstrument.getTotalAmountInvested();
                totalMarketValue += mutualFundInstrument.getTotalCurrentMarketValue();
            }
        }

        /*
         * Calculate the MetricsComparisonDto for all the StockInstruments
         */
        for(StockInstrument stockInstrument : stockInstruments) {
            MetricsComparisonDto comparisonMetric = new MetricsComparisonDto();
            comparisonMetric.setId(stockInstrument.getId());
            comparisonMetric.setName(stockInstrument.getName());
            comparisonMetric.setCategory(stockInstrument.getCategory().toString());

            /* SubCategory and PlanType are supposed to be NULL for StockInstrument */
            comparisonMetric.setNetReturnRate(stockInstrument.getNetReturnRate());
            comparisonMetric.setXirr(stockInstrument.getXirr());
            comparisonMetric.setShareInPortfolioOfTotalInvested(stockInstrument.getTotalAmountInvested() / totalAmountInvested);
            comparisonMetric.setShareInPortfolioOfTotalMarketValue(stockInstrument.getTotalCurrentMarketValue() / totalMarketValue);
            comparisonMetrics.add(comparisonMetric);
        }

        /*
         * Calculate the MetricsComparisonDto for all the MutualFundInstruments
         */
        for(MutualFundInstrument mutualFundInstrument : mutualFundInstruments) {
            MetricsComparisonDto comparisonMetric = new MetricsComparisonDto();
            comparisonMetric.setId(mutualFundInstrument.getId());
            comparisonMetric.setName(mutualFundInstrument.getName());
            comparisonMetric.setCategory(mutualFundInstrument.getCategory().toString());
            comparisonMetric.setSubCategory(mutualFundInstrument.getSubCategory().toString());
            comparisonMetric.setPlanType(mutualFundInstrument.getPlanType().toString());
            comparisonMetric.setNetReturnRate(mutualFundInstrument.getNetReturnRate());
            comparisonMetric.setXirr(mutualFundInstrument.getXirr());
            comparisonMetric.setShareInPortfolioOfTotalInvested(mutualFundInstrument.getTotalAmountInvested() / totalAmountInvested);
            comparisonMetric.setShareInPortfolioOfTotalMarketValue(mutualFundInstrument.getTotalCurrentMarketValue() / totalMarketValue);
            comparisonMetrics.add(comparisonMetric);
        }

        return comparisonMetrics;
    }
}
