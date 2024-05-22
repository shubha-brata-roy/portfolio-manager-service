package com.royhome.mystockplanningapp.models;

public enum PlanType {
    STANDARD,
    DIRECT;

    public static boolean contains(String planType) {
        for(PlanType p : PlanType.values()) {
            if(p.name().equals(planType)) {
                return true;
            }
        }
        return false;
    }
}
