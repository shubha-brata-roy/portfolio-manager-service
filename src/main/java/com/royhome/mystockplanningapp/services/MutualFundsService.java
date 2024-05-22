package com.royhome.mystockplanningapp.services;

import com.royhome.mystockplanningapp.commons.ChargesAndTaxes;
import com.royhome.mystockplanningapp.dtos.MutualFundCurrentMarketValueDto;
import com.royhome.mystockplanningapp.dtos.MutualFundHoldingUnitDto;
import com.royhome.mystockplanningapp.dtos.MutualFundInstrumentDto;
import com.royhome.mystockplanningapp.models.Category;
import com.royhome.mystockplanningapp.models.PlanType;
import com.royhome.mystockplanningapp.models.SubCategory;
import com.royhome.mystockplanningapp.models.TransactionType;
import com.royhome.mystockplanningapp.models.mutualfunds.MutualFundHoldingUnit;
import com.royhome.mystockplanningapp.models.mutualfunds.MutualFundInstrument;
import com.royhome.mystockplanningapp.repositories.MutualFundHoldingUnitRepository;
import com.royhome.mystockplanningapp.repositories.MutualFundInstrumentRepository;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class MutualFundsService {

    private final MutualFundInstrumentRepository mutualFundInstrumentRepository;
    private final MutualFundHoldingUnitRepository mutualFundHoldingUnitRepository;

    public MutualFundsService(MutualFundInstrumentRepository mutualFundInstrumentRepository,
                              MutualFundHoldingUnitRepository mutualFundHoldingUnitRepository) {
        this.mutualFundInstrumentRepository = mutualFundInstrumentRepository;
        this.mutualFundHoldingUnitRepository = mutualFundHoldingUnitRepository;
    }

    public List<MutualFundHoldingUnitDto> saveMutualFundHoldingUnits(List<MutualFundHoldingUnitDto> mutualFundHoldingUnitDtos) {
        for(MutualFundHoldingUnitDto mutualFundHoldingUnitDto : mutualFundHoldingUnitDtos) {
            if(!PlanType.contains(mutualFundHoldingUnitDto.getPlanType())) {
                mutualFundHoldingUnitDto.setStatus("Error: Invalid Plan Type");
                continue;
            }
            if(!TransactionType.contains(mutualFundHoldingUnitDto.getTransactionType())) {
                mutualFundHoldingUnitDto.setStatus("Error: Invalid Transaction Type");
                continue;
            }
            Optional<MutualFundInstrument> mutualFundInstrumentOptional
                    = mutualFundInstrumentRepository.findMutualFundInstrumentByNameAndPlanType(
                    mutualFundHoldingUnitDto.getMutualFundInstrumentName(),
                    PlanType.valueOf(mutualFundHoldingUnitDto.getPlanType()));

            if(mutualFundInstrumentOptional.isEmpty()) {
                mutualFundHoldingUnitDto.setStatus("Error: Invalid Instrument Name");
                continue;
            }
            MutualFundInstrument mutualFundInstrument = mutualFundInstrumentOptional.get();

            double stampDutyCharges = mutualFundHoldingUnitDto.getPurchasedAmount() * ChargesAndTaxes.MUTUAL_FUND_STAMP_DUTY_CHARGES;
            double purchasedAmount = mutualFundHoldingUnitDto.getPurchasedAmount() - stampDutyCharges;

            // set the totalInvestedAmount value in the Mutual Fund Instrument

            Double totalInvestedAmount = mutualFundInstrument.getTotalAmountInvested() == null ?
                                                                    0d : mutualFundInstrument.getTotalAmountInvested();
            totalInvestedAmount += purchasedAmount;

            mutualFundInstrument.setTotalAmountInvested(totalInvestedAmount);
            mutualFundInstrument.setTotalAmountInvestedUpdatedOn(new Date());

            // set the totalHoldingUnits value in the Mutual Fund Instrument

            Double totalHoldingUnits = mutualFundInstrument.getTotalHoldingUnits() == null ?
                                                                    0d : mutualFundInstrument.getTotalHoldingUnits();

            totalHoldingUnits += mutualFundHoldingUnitDto.getPurchasedUnits();

            mutualFundInstrument.setTotalHoldingUnits(totalHoldingUnits);

            mutualFundInstrument.setUpdatedAt(new Date());

            mutualFundInstrument = mutualFundInstrumentRepository.save(mutualFundInstrument);

            MutualFundHoldingUnit mutualFundHoldingUnit = new MutualFundHoldingUnit();

            mutualFundHoldingUnit.setIsin(mutualFundHoldingUnitDto.getIsin());
            mutualFundHoldingUnit.setMutualFundInstrument(mutualFundInstrument);
            mutualFundHoldingUnit.setTransactionType(TransactionType.valueOf(mutualFundHoldingUnitDto.getTransactionType()));
            mutualFundHoldingUnit.setSettlementId(mutualFundHoldingUnitDto.getSettlementId());
            mutualFundHoldingUnit.setPurchasedDate(mutualFundHoldingUnitDto.getPurchasedDate());
            mutualFundHoldingUnit.setPurchasedAmount(purchasedAmount);
            mutualFundHoldingUnit.setPurchasedUnits(mutualFundHoldingUnitDto.getPurchasedUnits());
            mutualFundHoldingUnit.setPurchasedNAV(mutualFundHoldingUnitDto.getPurchasedNAV());
            mutualFundHoldingUnit.setExchangeOrderId(mutualFundHoldingUnitDto.getExchangeOrderId());
            mutualFundHoldingUnit.setStampDutyCharges(stampDutyCharges);
            mutualFundHoldingUnit.setCreatedAt(new Date());
            mutualFundHoldingUnit.setUpdatedAt(new Date());

            mutualFundHoldingUnitRepository.save(mutualFundHoldingUnit);
            mutualFundHoldingUnitDto.setStatus("Success: Saved");
        }
        return mutualFundHoldingUnitDtos;
    }

    public List<MutualFundInstrumentDto> saveMutualFundInstruments(List<MutualFundInstrumentDto> mutualFundInstrumentDtos) {
        for(MutualFundInstrumentDto mutualFundInstrumentDto : mutualFundInstrumentDtos) {
            if(!Category.contains(mutualFundInstrumentDto.getCategoryName())) {
                mutualFundInstrumentDto.setStatus("Error: Invalid Category Name");
                continue;
            }
            if(!SubCategory.contains(mutualFundInstrumentDto.getSubCategoryName())) {
                mutualFundInstrumentDto.setStatus("Error: Invalid Sub Category Name");
                continue;
            }
            if(!PlanType.contains(mutualFundInstrumentDto.getPlanType())) {
                mutualFundInstrumentDto.setStatus("Error: Invalid Plan Type");
                continue;
            }
            Optional<MutualFundInstrument> mutualFundInstrumentOptional
                                            = mutualFundInstrumentRepository.findMutualFundInstrumentByNameAndPlanType(
                                                    mutualFundInstrumentDto.getMutualFundInstrumentName(),
                                                    PlanType.valueOf(mutualFundInstrumentDto.getPlanType()));
            if(mutualFundInstrumentOptional.isPresent()) {
                mutualFundInstrumentDto.setStatus("Error: Instrument Already Exists");
                continue;
            }
            MutualFundInstrument mutualFundInstrument = new MutualFundInstrument();
            mutualFundInstrument.setCategory(Category.valueOf(mutualFundInstrumentDto.getCategoryName()));
            mutualFundInstrument.setSubCategory(SubCategory.valueOf(mutualFundInstrumentDto.getSubCategoryName()));
            mutualFundInstrument.setName(mutualFundInstrumentDto.getMutualFundInstrumentName());
            mutualFundInstrument.setPlanType(PlanType.valueOf(mutualFundInstrumentDto.getPlanType()));
            mutualFundInstrument.setFolioNumber(mutualFundInstrumentDto.getFolioNumber());
            mutualFundInstrument.setCreatedAt(new Date());
            mutualFundInstrument.setUpdatedAt(new Date());

            mutualFundInstrumentRepository.save(mutualFundInstrument);

            mutualFundInstrumentDto.setStatus("Success: Saved");
        }
        return mutualFundInstrumentDtos;
    }

    public List<MutualFundCurrentMarketValueDto> updateStockInstruments(List<MutualFundCurrentMarketValueDto> currentMarketValueDtos) {
        for(MutualFundCurrentMarketValueDto currentMarketValueDto : currentMarketValueDtos) {
            Optional<MutualFundInstrument> mutualFundInstrumentOptional
                    = mutualFundInstrumentRepository.findMutualFundInstrumentByNameAndPlanType(
                    currentMarketValueDto.getMutualFundInstrumentName(),
                    PlanType.valueOf(currentMarketValueDto.getPlanType()));

            if(mutualFundInstrumentOptional.isEmpty()) {
                currentMarketValueDto.setStatus("Error: Invalid Instrument Name");
                continue;
            }

            MutualFundInstrument mutualFundInstrument = mutualFundInstrumentOptional.get();

            mutualFundInstrument.setTotalCurrentMarketValue(currentMarketValueDto.getTotalCurrentMarketValue());
            mutualFundInstrument.setCurrentNAV(currentMarketValueDto.getCurrentNAV());
            mutualFundInstrument.setCurrentMarketValueUpdatedOn(new Date());
            mutualFundInstrument.setUpdatedAt(new Date());

            mutualFundInstrumentRepository.save(mutualFundInstrument);
            currentMarketValueDto.setStatus("Success: Updated");
        }
        return currentMarketValueDtos;
    }
}
