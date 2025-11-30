package com.example.loansystemcalculator;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.text.DecimalFormat;

public class LoanRegularActivity extends AppCompatActivity {

    private DatabaseHelper dbHelper;
    private String employeeId;
    private double basicSalary;
    private double maxLoanAmount;

    private EditText editTextLoanAmount;
    private TextView textViewBasicSalary, textViewMaxLoanAmount;
    private TextView textViewSelectedMonths, textViewInterestRate;
    private TextView textViewServiceCharge, textViewTotalInterest, textViewTakeHomeLoan, textViewTotalAmount, textViewMonthlyAmortization;
    private Button btnChooseMonths, btnCalculate, btnApply, btnBack;

    private DecimalFormat currencyFormat = new DecimalFormat("₱#,##0.00");
    private DecimalFormat percentFormat = new DecimalFormat("0.00%");

    private int selectedMonths = 0;
    private double currentInterestRate = 0.0;
    private double currentTakeHomeLoan = 0.0;
    private double currentTotalAmountDue = 0.0;
    private double currentMonthlyAmortization = 0.0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loan_regular);

        initializeViews();
        setupClickListeners();

        // Get employee ID from intent
        employeeId = getIntent().getStringExtra("EMPLOYEE_ID");
        if (employeeId == null) {
            Toast.makeText(this, "Error: Employee ID not found", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        dbHelper = new DatabaseHelper(this);
        loadEmployeeData();
    }

    private void initializeViews() {
        editTextLoanAmount = findViewById(R.id.editTextLoanAmount);

        textViewBasicSalary = findViewById(R.id.textViewBasicSalary);
        textViewMaxLoanAmount = findViewById(R.id.textViewMaxLoanAmount);
        textViewSelectedMonths = findViewById(R.id.textViewSelectedMonths);
        textViewInterestRate = findViewById(R.id.textViewInterestRate);

        textViewServiceCharge = findViewById(R.id.textViewServiceCharge);
        textViewTotalInterest = findViewById(R.id.textViewTotalInterest);
        textViewTakeHomeLoan = findViewById(R.id.textViewTakeHomeLoan);
        textViewTotalAmount = findViewById(R.id.textViewTotalAmount);
        textViewMonthlyAmortization = findViewById(R.id.textViewMonthlyAmortization);

        btnChooseMonths = findViewById(R.id.btnChooseMonths);
        btnCalculate = findViewById(R.id.btnCalculate);
        btnApply = findViewById(R.id.btnApply);
        btnBack = findViewById(R.id.btnBack);
    }

    private void setupClickListeners() {
        btnChooseMonths.setOnClickListener(v -> showMonthsDialog());
        btnCalculate.setOnClickListener(v -> calculateLoan());
        btnApply.setOnClickListener(v -> applyForLoan());
        btnBack.setOnClickListener(v -> finish());
    }

    private void loadEmployeeData() {
        // Get employee's basic salary from database
        basicSalary = dbHelper.getEmployeeBasicSalary(employeeId);
        if (basicSalary > 0) {
            // Calculate maximum loan amount (Basic Salary * 2.5)
            maxLoanAmount = LoanCalculator.computeRegularLoanAmount(basicSalary);

            textViewBasicSalary.setText(currencyFormat.format(basicSalary));
            textViewMaxLoanAmount.setText(currencyFormat.format(maxLoanAmount));

            // Set hint for loan amount with max value
            editTextLoanAmount.setHint("Enter amount (max: " + currencyFormat.format(maxLoanAmount) + ")");
        } else {
            Toast.makeText(this, "Error: Could not load salary information", Toast.LENGTH_LONG).show();
            finish();
        }
    }

    private void showMonthsDialog() {
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.popup_dialog);
        dialog.setCancelable(true);

        RadioGroup radioGroupMonths = dialog.findViewById(R.id.radioGroupMonths);
        Button btnSubmit = dialog.findViewById(R.id.btnSubmit);

        // Clear any previous selection
        radioGroupMonths.clearCheck();

        btnSubmit.setOnClickListener(v1 -> {
            int selectedId = radioGroupMonths.getCheckedRadioButtonId();

            if (selectedId == -1) {
                Toast.makeText(this, "Please select a month range", Toast.LENGTH_SHORT).show();
                return;
            }

            // Determine selected months and get interest rate
            if (selectedId == R.id.radio1to5) {
                selectedMonths = 5;
            } else if (selectedId == R.id.radio6to10) {
                selectedMonths = 10;
            } else if (selectedId == R.id.radio11to15) {
                selectedMonths = 15;
            } else if (selectedId == R.id.radio16to20) {
                selectedMonths = 20;
            } else if (selectedId == R.id.radio21to24) {
                selectedMonths = 24;
            }

            // Get interest rate from database (Regular Loan has loanTypeId = 3)
            currentInterestRate = dbHelper.getInterestRate(3, selectedMonths);

            if (currentInterestRate > 0) {
                textViewSelectedMonths.setText("Selected: " + selectedMonths + " months");
                textViewInterestRate.setText("Interest Rate: " + percentFormat.format(currentInterestRate));
                Toast.makeText(this, "Selected: " + selectedMonths + " months", Toast.LENGTH_SHORT).show();
                dialog.dismiss();

                // Enable calculate button
                btnCalculate.setEnabled(true);
            } else {
                Toast.makeText(this, "Error: Could not get interest rate", Toast.LENGTH_SHORT).show();
            }
        });

        dialog.show();
    }

    private void calculateLoan() {
        if (!validateInputs()) {
            return;
        }

        try {
            double loanAmount = Double.parseDouble(editTextLoanAmount.getText().toString());

            // Validate loan amount doesn't exceed maximum
            if (loanAmount > maxLoanAmount) {
                Toast.makeText(this, "Loan amount cannot exceed " + currencyFormat.format(maxLoanAmount), Toast.LENGTH_LONG).show();
                return;
            }

            // Validate loan amount is positive
            if (!LoanCalculator.isValidLoanAmount("regular", loanAmount)) {
                Toast.makeText(this, "Please enter a valid loan amount", Toast.LENGTH_SHORT).show();
                return;
            }

            // Validate months to pay
            if (!LoanCalculator.isValidMonthsToPay("regular", selectedMonths)) {
                Toast.makeText(this, "Please select valid months to pay (1-24)", Toast.LENGTH_SHORT).show();
                return;
            }

            // Calculate service charge (2% for Regular Loan)
            double serviceCharge = LoanCalculator.computeServiceCharge("regular", loanAmount);

            // Calculate total interest
            double totalInterest = LoanCalculator.computeInterest(loanAmount, currentInterestRate, selectedMonths);

            // Calculate take home loan amount
            currentTakeHomeLoan = LoanCalculator.computeRegularLoan(loanAmount, selectedMonths, currentInterestRate, 0.02);

            // Calculate total amount due
            currentTotalAmountDue = LoanCalculator.computeRegularTotalDue(loanAmount, selectedMonths, currentInterestRate, 0.02);

            // Calculate monthly amortization
            currentMonthlyAmortization = LoanCalculator.computeRegularMonthlyAmortization(loanAmount, selectedMonths, currentInterestRate, 0.02);

            // Display results
            displayCalculationResults(serviceCharge, totalInterest, currentTakeHomeLoan, currentTotalAmountDue, currentMonthlyAmortization);

        } catch (NumberFormatException e) {
            Toast.makeText(this, "Please enter valid numbers", Toast.LENGTH_SHORT).show();
        }
    }

    private void displayCalculationResults(double serviceCharge, double totalInterest, double takeHomeLoan,
                                           double totalAmountDue, double monthlyAmortization) {
        textViewServiceCharge.setText(currencyFormat.format(serviceCharge));
        textViewTotalInterest.setText(currencyFormat.format(totalInterest));
        textViewTakeHomeLoan.setText(currencyFormat.format(takeHomeLoan));
        textViewTotalAmount.setText(currencyFormat.format(totalAmountDue));
        textViewMonthlyAmortization.setText(currencyFormat.format(monthlyAmortization));

        // Show monthly amortization section
        findViewById(R.id.layoutMonthlyAmortization).setVisibility(View.VISIBLE);

        // Enable apply button
        btnApply.setEnabled(true);
    }

    private void clearCalculationResults() {
        textViewServiceCharge.setText("₱0.00");
        textViewTotalInterest.setText("₱0.00");
        textViewTakeHomeLoan.setText("₱0.00");
        textViewTotalAmount.setText("₱0.00");
        textViewMonthlyAmortization.setText("₱0.00");

        findViewById(R.id.layoutMonthlyAmortization).setVisibility(View.GONE);
        btnApply.setEnabled(false);
    }

    private boolean validateInputs() {
        if (TextUtils.isEmpty(editTextLoanAmount.getText())) {
            Toast.makeText(this, "Please enter loan amount", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (selectedMonths == 0) {
            Toast.makeText(this, "Please select months to pay", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    private void applyForLoan() {
        if (!validateInputs()) {
            return;
        }

        try {
            double loanAmount = Double.parseDouble(editTextLoanAmount.getText().toString());

            // Validate loan amount doesn't exceed maximum
            if (loanAmount > maxLoanAmount) {
                Toast.makeText(this, "Loan amount cannot exceed " + currencyFormat.format(maxLoanAmount), Toast.LENGTH_LONG).show();
                return;
            }

            // Calculate values for database storage
            double serviceCharge = LoanCalculator.computeServiceCharge("regular", loanAmount);
            double interestAmount = LoanCalculator.computeInterest(loanAmount, currentInterestRate, selectedMonths);

            // Apply for loan (Regular Loan has loanTypeId = 3)
            boolean success = dbHelper.applyForLoan(
                    employeeId,
                    3, // Regular Loan Type ID
                    loanAmount,
                    selectedMonths,
                    currentTotalAmountDue,
                    currentInterestRate,
                    interestAmount,
                    serviceCharge,
                    currentTakeHomeLoan
            );

            if (success) {
                Toast.makeText(this, "Regular Loan application submitted successfully!", Toast.LENGTH_LONG).show();

                // Navigate back to loan type selection
                Intent intent = new Intent(this, LoanChooseTypeActivity.class);
                intent.putExtra("EMPLOYEE_ID", employeeId);
                startActivity(intent);
                finish();
            } else {
                Toast.makeText(this, "Failed to submit loan application", Toast.LENGTH_LONG).show();
            }

        } catch (NumberFormatException e) {
            Toast.makeText(this, "Error in loan application", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (dbHelper != null) {
            dbHelper.close();
        }
    }
}