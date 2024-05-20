package com.royhome.mystockplanningapp.models;

public enum Industry {
    IT,
    FINANCIALS,
    CONSUMER_STAPLES,
    INDUSTRIALS,
    PHARMACEUTICALS,
    MATERIALS,
    COMMUNICATION_SERVICES,
    ENERGY,
    CONSUMER_DISCRETIONARY;

    public static boolean contains(String industry) {
        for(Industry i : Industry.values()) {
            if(i.name().equals(industry)) {
                return true;
            }
        }
        return false;
    }
}
