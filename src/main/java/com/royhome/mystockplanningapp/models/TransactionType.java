package com.royhome.mystockplanningapp.models;

public enum TransactionType {
    INVEST,
    WITHDRAW;

    public static boolean contains(String transactionType) {
        for(TransactionType t : TransactionType.values()) {
            if(t.name().equals(transactionType)) {
                return true;
            }
        }
        return false;
    }
}
