package com.royhome.mystockplanningapp.repositories;

import com.royhome.mystockplanningapp.models.mutualfunds.MutualFundHoldingUnit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MutualFundHoldingUnitRepository extends JpaRepository<MutualFundHoldingUnit, Long> {

    public MutualFundHoldingUnit save(MutualFundHoldingUnit mutualFundHoldingUnit);

}
