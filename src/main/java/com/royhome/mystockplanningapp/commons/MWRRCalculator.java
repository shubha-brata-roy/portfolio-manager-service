package com.royhome.mystockplanningapp.commons;

public class MWRRCalculator {

    public static double calculateMWRR(double[] cashflows, double[] dates) {
        if (cashflows.length != dates.length) {
            throw new IllegalArgumentException("Cashflows and dates must have the same length");
        }

        double totalInvestment = 0.0;
        double totalTimeWeightedReturn = 0.0;

        for (int i = 0; i < cashflows.length; i++) {
            // Assuming dates are days since a reference date (adjust as needed)
            double time = dates[i];
            double cashflow = cashflows[i];

            // Calculate time-weighted return for this cashflow
            double timeWeightedReturn = cashflow / (1 + time);

            if (cashflow < 0) { // Negative cashflow represents investment
                totalTimeWeightedReturn += timeWeightedReturn;
            } else { // Positive cashflow represents return
                totalInvestment += cashflow * time;
            }
        }

        // MWRR formula (assuming annualized return)
        double mwrr = Math.pow((1 + totalTimeWeightedReturn / totalInvestment), 1.0 / (dates[dates.length - 1] / 365.0)) - 1;
        return mwrr * 100; // Convert to percentage
    }
}

