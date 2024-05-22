package com.royhome.mystockplanningapp.repositories;

import com.royhome.mystockplanningapp.models.PlanType;
import com.royhome.mystockplanningapp.models.mutualfunds.MutualFundInstrument;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MutualFundInstrumentRepository extends JpaRepository<MutualFundInstrument, Long> {
    public Optional<MutualFundInstrument> findMutualFundInstrumentByNameAndPlanType(String name, PlanType planType);

    public MutualFundInstrument save(MutualFundInstrument mutualFundInstrument);
}
