package com.royhome.mystockplanningapp.repositories;

import com.royhome.mystockplanningapp.models.PlanType;
import com.royhome.mystockplanningapp.models.mutualfunds.MutualFundInstrument;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MutualFundInstrumentRepository extends JpaRepository<MutualFundInstrument, Long> {
    public Optional<MutualFundInstrument> findMutualFundInstrumentByNameAndPlanType(String name, PlanType planType);

    public List<MutualFundInstrument> findAll();

    public MutualFundInstrument save(MutualFundInstrument mutualFundInstrument);

    public MutualFundInstrument findById(long id);

    @Query(value = "SELECT SUM(s.total_current_market_value) FROM mutual_fund_instrument s", nativeQuery = true)
    public double findTotalCurrentMarketValue();
}
