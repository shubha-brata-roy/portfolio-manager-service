package com.royhome.mystockplanningapp.services;

import com.royhome.mystockplanningapp.commons.ChargesAndTaxes;
import com.royhome.mystockplanningapp.dtos.MutualFundCurrentMarketValueDto;
import com.royhome.mystockplanningapp.dtos.MutualFundHoldingUnitDto;
import com.royhome.mystockplanningapp.dtos.MutualFundInstrumentDto;
import com.royhome.mystockplanningapp.models.mutualfunds.MutualFundCategory;
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
            Optional<MutualFundInstrument> mutualFundInstrumentOptional
                                            = mutualFundInstrumentRepository.findMutualFundInstrumentByName(
                                                    mutualFundHoldingUnitDto.getMutualFundInstrumentName());
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

            mutualFundInstrument.setUpdatedAt(new Date());

            mutualFundInstrument = mutualFundInstrumentRepository.save(mutualFundInstrument);

            MutualFundHoldingUnit mutualFundHoldingUnit = new MutualFundHoldingUnit();

            mutualFundHoldingUnit.setMutualFundInstrument(mutualFundInstrument);
            mutualFundHoldingUnit.setPurchasedDate(mutualFundHoldingUnitDto.getPurchasedDate());
            mutualFundHoldingUnit.setPurchasedAmount(purchasedAmount);
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
            if(!MutualFundCategory.contains(mutualFundInstrumentDto.getCategoryName())) {
                mutualFundInstrumentDto.setStatus("Error: Invalid Mutual Fund Category Name");
                continue;
            }
            Optional<MutualFundInstrument> mutualFundInstrumentOptional
                                            = mutualFundInstrumentRepository.findMutualFundInstrumentByName(
                                                    mutualFundInstrumentDto.getMutualFundInstrumentName());
            if(mutualFundInstrumentOptional.isPresent()) {
                mutualFundInstrumentDto.setStatus("Error: Instrument Already Exists");
                continue;
            }
            MutualFundInstrument mutualFundInstrument = new MutualFundInstrument();
            mutualFundInstrument.setCategory(MutualFundCategory.valueOf(mutualFundInstrumentDto.getCategoryName()));
            mutualFundInstrument.setName(mutualFundInstrumentDto.getMutualFundInstrumentName());
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
                                            = mutualFundInstrumentRepository.findMutualFundInstrumentByName(
                                                    currentMarketValueDto.getMutualFundInstrumentName());

            if(mutualFundInstrumentOptional.isEmpty()) {
                currentMarketValueDto.setStatus("Error: Invalid Instrument Name");
                continue;
            }

            MutualFundInstrument mutualFundInstrument = mutualFundInstrumentOptional.get();

            mutualFundInstrument.setTotalCurrentMarketValue(currentMarketValueDto.getTotalCurrentMarketValue());
            mutualFundInstrument.setTotalCurrentMarketValueUpdatedOn(new Date());
            mutualFundInstrument.setUpdatedAt(new Date());

            mutualFundInstrumentRepository.save(mutualFundInstrument);
            currentMarketValueDto.setStatus("Success: Updated");
        }
        return currentMarketValueDtos;
    }
}
