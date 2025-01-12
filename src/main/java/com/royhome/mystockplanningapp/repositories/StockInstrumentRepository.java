package com.royhome.mystockplanningapp.repositories;

import com.royhome.mystockplanningapp.models.stocks.StockInstrument;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StockInstrumentRepository extends JpaRepository<StockInstrument, Long> {
    public Optional<StockInstrument> findStockInstrumentByName(String name);

    public StockInstrument findById(long id);

    public List<StockInstrument> findAll();

    public StockInstrument save(StockInstrument stockInstrument);

    @Query(value = "SELECT SUM(s.total_current_market_value) FROM stock_instrument s", nativeQuery = true)
    public double findTotalCurrentMarketValue();
}
