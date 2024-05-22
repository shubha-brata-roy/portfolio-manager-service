package com.royhome.mystockplanningapp.models;

public enum Category {
     EQUITY_STOCK,
     DEBT_STOCK,
     EQUITY_MUTUAL_FUND,
     DEBT_MUTUAL_FUND,
     GOLD_MUTUAL_FUND,
     INDEX;

     public static boolean contains(String category) {
          for(Category c : Category.values()) {
               if(c.name().equals(category)) {
                    return true;
               }
          }
          return false;
     }
}
