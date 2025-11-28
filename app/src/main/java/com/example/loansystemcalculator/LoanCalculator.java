package com.example.loansystemcalculator;

import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;

public class LoanCalculator {

    /**
     * Compute total interest amount for any loan type
     * Formula: Interest = Loan Amount * Interest Rate * Months to Pay
     */
    public static double computeInterest(double loanAmount, double interestRate, int monthsToPay) {
        return loanAmount * interestRate * monthsToPay;
    }

    /**
     * Compute Emergency Loan total amount due and monthly payment
     * Formulas:
     * - Cash after 6 months: Loan Amount + service charge + interest
     * - Payable in 6 months: (Loan Amount + Service Charge + Interest) / number of months to pay
     * Returns total amount due for emergency loan
     */
    public static double computeEmergencyLoan(String method, double loanAmount, double serviceCharge, double interest, int monthsToPay) {
        return loanAmount + serviceCharge + interest;
    }

    /**
     * Compute Special Loan total amount due
     * Formula: Total Amount = Loan Amount + (Loan Amount * months to pay * interest rate)
     */
    public static double computeSpecialLoan(double loanAmount, int monthsToPay, double interestRate) {
        double interest = computeInterest(loanAmount, interestRate, monthsToPay);
        return loanAmount + interest;
    }

    /**
     * Compute Regular Loan take home amount and total amount due
     * Formulas:
     * - Take Home Loan = Loan Amount - (Loan Interest + Service Charge)
     * - Total Amount Due = Loan Amount + Loan Interest + Service Charge
     * Returns take home amount for regular loan
     */
    public static double computeRegularLoan(double loanAmount, int monthsToPay, double interestRate, double serviceChargeRate) {
        double interest = computeInterest(loanAmount, interestRate, monthsToPay);
        double serviceCharge = loanAmount * serviceChargeRate;
        return loanAmount - (interest + serviceCharge);
    }

    /**
     * Compute total amount due for regular loan (separate from take home)
     */
    public static double computeRegularTotalDue(double loanAmount, int monthsToPay, double interestRate, double serviceChargeRate) {
        double interest = computeInterest(loanAmount, interestRate, monthsToPay);
        double serviceCharge = loanAmount * serviceChargeRate;
        return loanAmount + interest + serviceCharge;
    }

    /**
     * Check if employee is eligible for Special Loan (5+ years in service)
     */
    public static boolean isEligibleForSpecial(String dateHired) {
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            LocalDate hiredDate = LocalDate.parse(dateHired, formatter);
            LocalDate currentDate = LocalDate.now();

            Period period = Period.between(hiredDate, currentDate);
            return period.getYears() >= 5;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Calculate service charge amount based on loan type
     */
    public static double computeServiceCharge(String loanType, double loanAmount) {
        switch (loanType.toLowerCase()) {
            case "emergency":
                return loanAmount * 0.01; // 1%
            case "regular":
                return loanAmount * 0.02; // 2%
            case "special":
                return 0.00; // 0% as specified
            default:
                return 0.00;
        }
    }

    /**
     * Calculate monthly amortization for Emergency Loan (payable in months)
     */
    public static double computeEmergencyMonthlyAmortization(double loanAmount, double serviceCharge, double interest, int monthsToPay) {
        double totalAmountDue = computeEmergencyLoan(loanAmount, serviceCharge, interest, monthsToPay);
        return totalAmountDue / monthsToPay;
    }

    /**
     * Calculate monthly amortization for Special Loan
     */
    public static double computeSpecialMonthlyAmortization(double loanAmount, int monthsToPay, double interestRate) {
        double totalAmountDue = computeSpecialLoan(loanAmount, monthsToPay, interestRate);
        return totalAmountDue / monthsToPay;
    }

    /**
     * Calculate monthly amortization for Regular Loan (based on take home amount)
     */
    public static double computeRegularMonthlyAmortization(double loanAmount, int monthsToPay, double interestRate, double serviceChargeRate) {
        double takeHomeLoan = computeRegularLoan(loanAmount, monthsToPay, interestRate, serviceChargeRate);
        return takeHomeLoan / monthsToPay;
    }

    /**
     * Calculate maximum loan amount for Regular Loan (Basic Salary * 2.5)
     */
    public static double computeRegularLoanAmount(double basicSalary) {
        return basicSalary * 2.5;
    }

    /**
     * Validate loan amount ranges based on loan type
     */
    public static boolean isValidLoanAmount(String loanType, double amount) {
        switch (loanType.toLowerCase()) {
            case "emergency":
                return amount >= 5000 && amount <= 25000;
            case "special":
                return amount >= 50000 && amount <= 100000;
            case "regular":
                return amount > 0; // No specific range, but must be positive
            default:
                return false;
        }
    }

    /**
     * Validate months to pay based on loan type
     */
    public static boolean isValidMonthsToPay(String loanType, int months) {
        switch (loanType.toLowerCase()) {
            case "emergency":
                return months == 6; // Fixed 6 months for emergency
            case "special":
                return months >= 1 && months <= 18;
            case "regular":
                return months >= 1 && months <= 24;
            default:
                return false;
        }
    }
}