package com.royhome.mystockplanningapp.repositories;

import com.royhome.mystockplanningapp.models.stocks.StockHoldingUnit;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StockHoldingUnitRepository extends JpaRepository<StockHoldingUnit, Long> {
    public StockHoldingUnit save(StockHoldingUnit stockHoldingUnit);
}
