package com.example.loansystemcalculator;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class LoanEmergencyActivity extends AppCompatActivity {

    private Button btnCalculate, btnApply, btnBack;
    private EditText editTextLoanAmount;
    private TextView textViewServiceCharge, textViewTotalInterest, textViewTotalAmount;

    private DatabaseHelper dbHelper;
    private String employeeId;

    // Loan calculation variables
    private double loanAmount = 0;
    private double serviceCharge = 0;
    private double interest = 0;
    private double totalAmount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_loan_emergency);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize DatabaseHelper
        dbHelper = new DatabaseHelper(this);

        // Get employee ID from intent
        employeeId = getIntent().getStringExtra("EMPLOYEE_ID");
        if (employeeId == null || employeeId.isEmpty()) {
            Toast.makeText(this, "Employee ID not found. Please login again.", Toast.LENGTH_LONG).show();
            navigateToEmployeeLogin();
            return;
        }

        // Initialize views
        btnCalculate = findViewById(R.id.btnCalculate);
        btnApply = findViewById(R.id.btnApply);
        btnBack = findViewById(R.id.btnBack);

        editTextLoanAmount = findViewById(R.id.editTextLoanAmount);
        textViewServiceCharge = findViewById(R.id.textViewServiceCharge);
        textViewTotalInterest = findViewById(R.id.textViewTotalInterest);
        textViewTotalAmount = findViewById(R.id.textViewTotalAmount);

        // Calculate button click listener
        btnCalculate.setOnClickListener(v -> {
            String amountInput = editTextLoanAmount.getText().toString();

            if (!amountInput.isEmpty()) {
                loanAmount = Double.parseDouble(amountInput);

                // Validate loan amount range
                if (loanAmount < 5000 || loanAmount > 25000) {
                    Toast.makeText(LoanEmergencyActivity.this,
                            "Loan amount must be between ₱5,000 and ₱25,000",
                            Toast.LENGTH_LONG).show();
                    clearResults();
                    return;
                }

                int monthsToPay = 6;
                double interestRate = 0.006; // 0.60% per month

                // Get loan type ID for Emergency
                int loanTypeId = dbHelper.getLoanTypeIdByName("Emergency");
                if (loanTypeId == -1) {
                    Toast.makeText(this, "Loan type not found. Please try again.", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Service Charge 1%
                serviceCharge = loanAmount * 0.01;

                // Total Interest (LoanCalculator method)
                interest = LoanCalculator.computeInterest(loanAmount, interestRate, monthsToPay);

                // Total Amount
                totalAmount = loanAmount + serviceCharge + interest;

                // Take Home Loan (amount the employee receives)
                double takeHomeLoan = loanAmount - serviceCharge;

                // Display results
                textViewServiceCharge.setText("₱" + String.format("%.2f", serviceCharge));
                textViewTotalInterest.setText("₱" + String.format("%.2f", interest));
                textViewTotalAmount.setText("₱" + String.format("%.2f", totalAmount));

                // Enable apply button
                btnApply.setEnabled(true);
            } else {
                Toast.makeText(LoanEmergencyActivity.this,
                        "Please enter a loan amount",
                        Toast.LENGTH_SHORT).show();
                clearResults();
            }
        });

        // Apply button click listener
        btnApply.setOnClickListener(v -> {
            applyForEmergencyLoan();
        });

        // Back button click listener
        btnBack.setOnClickListener(v -> {
            Intent intent = new Intent(LoanEmergencyActivity.this, LoanChooseTypeActivity.class);
            intent.putExtra("EMPLOYEE_ID", employeeId);
            startActivity(intent);
            finish();
        });
    }

    private void clearResults() {
        textViewServiceCharge.setText("");
        textViewTotalInterest.setText("");
        textViewTotalAmount.setText("");
        btnApply.setEnabled(false);
    }

    private void applyForEmergencyLoan() {
        // Check if calculation has been done
        if (loanAmount <= 0 || totalAmount <= 0) {
            Toast.makeText(this, "Please calculate the loan first", Toast.LENGTH_SHORT).show();
            return;
        }

        // Get loan type ID for Emergency
        int loanTypeId = dbHelper.getLoanTypeIdByName("Emergency");
        if (loanTypeId == -1) {
            Toast.makeText(this, "Loan type not found. Please try again.", Toast.LENGTH_SHORT).show();
            return;
        }

        int monthsToPay = 6;
        double interestRate = 0.006; // 0.60% per month

        // Take Home Loan (amount the employee receives)
        double takeHomeLoan = loanAmount - serviceCharge;

        // Apply for loan using DatabaseHelper method
        boolean success = dbHelper.applyForLoan(
                employeeId,
                loanTypeId,
                loanAmount,
                monthsToPay,
                totalAmount,
                interestRate,
                interest,
                serviceCharge,
                takeHomeLoan
        );

        if (success) {
            Toast.makeText(this, "Emergency loan application submitted successfully!", Toast.LENGTH_LONG).show();

            // Navigate back to home or show confirmation
            Intent intent = new Intent(LoanEmergencyActivity.this, HomeEmployeeActivity.class);
            intent.putExtra("EMPLOYEE_ID", employeeId);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        } else {
            Toast.makeText(this, "Failed to submit loan application. Please try again.", Toast.LENGTH_SHORT).show();
        }
    }

    private void navigateToEmployeeLogin() {
        Intent intent = new Intent(LoanEmergencyActivity.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Disable apply button initially
        btnApply.setEnabled(false);
    }
}