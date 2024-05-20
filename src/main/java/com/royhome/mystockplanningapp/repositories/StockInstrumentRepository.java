package com.royhome.mystockplanningapp.repositories;

import com.royhome.mystockplanningapp.models.stocks.StockInstrument;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface StockInstrumentRepository extends JpaRepository<StockInstrument, Long> {
    public Optional<StockInstrument> findStockInstrumentByName(String name);

    public StockInstrument save(StockInstrument stockInstrument);
}
