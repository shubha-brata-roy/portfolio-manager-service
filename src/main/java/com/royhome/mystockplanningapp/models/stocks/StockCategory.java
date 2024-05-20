package com.royhome.mystockplanningapp.models.stocks;

public enum StockCategory {
     EQUITY_STOCK,
     DEBT_STOCK,
     EQUITY_MUTUAL_FUND,
     DEBT_MUTUAL_FUND,
     GOLD,
     INDEX;

     public static boolean contains(String category) {
          for(StockCategory c : StockCategory.values()) {
               if(c.name().equals(category)) {
                    return true;
               }
          }
          return false;
     }
}
