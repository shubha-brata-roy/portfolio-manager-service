package com.royhome.mystockplanningapp.repositories;

import com.royhome.mystockplanningapp.models.Owner;
import com.royhome.mystockplanningapp.models.stocks.StockHoldingUnit;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;

public interface StockHoldingUnitRepository extends JpaRepository<StockHoldingUnit, Long> {
    public StockHoldingUnit save(StockHoldingUnit stockHoldingUnit);

    List<StockHoldingUnit> findAll();

    public List<StockHoldingUnit> findStockHoldingUnitsByStockInstrumentId(Long stockInstrumentId);
}
