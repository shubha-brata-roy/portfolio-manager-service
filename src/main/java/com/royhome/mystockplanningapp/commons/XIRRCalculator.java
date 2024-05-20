package com.royhome.mystockplanningapp.commons;

import org.apache.commons.math3.analysis.UnivariateFunction;
import org.apache.commons.math3.analysis.differentiation.DerivativeStructure;
import org.apache.commons.math3.analysis.differentiation.UnivariateDifferentiableFunction;
import org.apache.commons.math3.analysis.solvers.*;
import org.apache.commons.math3.exception.DimensionMismatchException;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class XIRRCalculator {

    // Constants for convergence parameters
    private static final double TOLERANCE = 1e-9;
    private static final int MAX_ITERATIONS = 100;

    // Method to calculate XIRR
    public static double calculateXIRR(Date[] dates, double[] amounts) {
        // Create a CashFlow object
        CashFlow cashFlow = new CashFlow(dates, amounts);

        // Create an instance of NewtonRaphsonSolver
        NewtonRaphsonSolver solver = new NewtonRaphsonSolver(TOLERANCE);

        // Calculate XIRR
        try {
            double xirr = solver.solve(MAX_ITERATIONS, cashFlow, -1, 1);
            return xirr;
        } catch (Exception e) {
            e.printStackTrace();
            return Double.NaN; // Return NaN if XIRR cannot be calculated
        }
    }

    // CashFlow class implementing UnivariateFunction interface
    private static class CashFlow implements UnivariateDifferentiableFunction {

        private Date[] dates;
        private double[] amounts;

        public CashFlow(Date[] dates, double[] amounts) {
            this.dates = dates;
            this.amounts = amounts;
        }

        @Override
        public double value(double rate) {
            double result = 0.0;
            for (int i = 0; i < dates.length; i++) {
                result += amounts[i] / Math.pow(1 + rate, getDateFraction(dates[i]));
            }
            return result;
        }

        private double getDateFraction(Date date) {
            // Calculate the fraction of a year since the start of 1900
            // You can use any appropriate method to calculate the date fraction
            // For simplicity, this example assumes 1 day is equal to 1/365 of a year
            long diff = date.getTime() - new Date(0).getTime();
            return (double) diff / (365 * 24 * 60 * 60 * 1000);
        }

        @Override
        public DerivativeStructure value(DerivativeStructure t) throws DimensionMismatchException {
            return null;
        }
    }
}

