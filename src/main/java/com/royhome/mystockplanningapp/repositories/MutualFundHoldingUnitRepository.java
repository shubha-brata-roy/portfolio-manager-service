package com.royhome.mystockplanningapp.repositories;

import com.royhome.mystockplanningapp.models.mutualfunds.MutualFundHoldingUnit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MutualFundHoldingUnitRepository extends JpaRepository<MutualFundHoldingUnit, Long> {

    public MutualFundHoldingUnit save(MutualFundHoldingUnit mutualFundHoldingUnit);

    public List<MutualFundHoldingUnit> findAll();

    public List<MutualFundHoldingUnit> findMutualFundHoldingUnitsByMutualFundInstrumentId(long id);
}
