package com.royhome.mystockplanningapp.models;

public enum SubCategory {
    LARGE_CAP_FUND,
    FLEXI_CAP_FUND,
    VALUE_FUND,
    SMALL_CAP_FUND,
    LARGE_AND_MID_CAP_FUND,
    FLOATING_RATE_FUND,
    MULTI_CAP_FUND,
    MID_CAP_FUND,
    INDEX_FUND,
    FOCUSED_FUND,
    FUND_OF_FUNDS_GOLD,
    MICRO_CAP_FUND,
    MONEY_MARKET_FUND,
    ULTRA_SHORT_DURATION_FUND;

    public static boolean contains(String category) {
        for(SubCategory c : SubCategory.values()) {
            if(c.name().equals(category)) {
                return true;
            }
        }
        return false;
    }
}
