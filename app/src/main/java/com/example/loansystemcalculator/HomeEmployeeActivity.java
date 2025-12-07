package com.example.loansystemcalculator;

import android.os.Bundle;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import android.content.Intent;
import android.view.View;
import android.widget.TextView;
import androidx.cardview.widget.CardView;

// Added imports for Toast and AlertDialog
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;

public class HomeEmployeeActivity extends AppCompatActivity {

    private TextView tvWelcomeEmployee, tvEmployeeId;
    private CardView cardApplyLoan, cardTransactionHistory;
    private View btnLogout;
    private String employeeId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_home_employee);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Get employee ID from intent
        employeeId = getIntent().getStringExtra("EMPLOYEE_ID");
        if (employeeId == null) {
            navigateToEmployeeLogin();
            return;
        }

        initializeViews();
        setupClickListeners();
        updateUI();
    }

    private void initializeViews() {
        tvWelcomeEmployee = findViewById(R.id.tvWelcomeEmployee);
        tvEmployeeId = findViewById(R.id.tvEmployeeId);
        cardApplyLoan = findViewById(R.id.cardApplyLoan);
        cardTransactionHistory = findViewById(R.id.cardTransactionHistory);
        btnLogout = findViewById(R.id.btnLogout);
    }

    private void setupClickListeners() {
        cardApplyLoan.setOnClickListener(v -> navigateToLoanChooseType());

        cardTransactionHistory.setOnClickListener(v -> navigateToLoanHistory());

        // Updated to show confirmation dialog before logout
        btnLogout.setOnClickListener(v -> showLogoutConfirmation());
    }

    private void updateUI() {
        tvWelcomeEmployee.setText("Welcome, Employee!");
        tvEmployeeId.setText("Employee ID: " + employeeId);
    }

    private void navigateToLoanChooseType() {
        Intent intent = new Intent(HomeEmployeeActivity.this, LoanChooseTypeActivity.class);
        intent.putExtra("EMPLOYEE_ID", employeeId);
        startActivity(intent);
    }

    private void navigateToLoanHistory() {
        Intent intent = new Intent(HomeEmployeeActivity.this, EmployeeLoanHistoryActivity.class);
        intent.putExtra("EMPLOYEE_ID", employeeId);
        startActivity(intent);
    }

    private void navigateToEmployeeLogin() {
        Intent intent = new Intent(HomeEmployeeActivity.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    private void showLogoutConfirmation() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Logout")
                .setMessage("Are you sure you want to logout?")
                .setPositiveButton("Yes", (dialog, which) -> logout())
                .setNegativeButton("No", null)
                .show();
    }

    private void logout() {
        // Navigate to landing screen
        Intent intent = new Intent(HomeEmployeeActivity.this, LandingActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();

        // Show confirmation toast
        Toast.makeText(this, "Logged out successfully", Toast.LENGTH_SHORT).show();
    }

    private void navigateToChooseLoan() {
        Intent intent = new Intent(HomeEmployeeActivity.this, LoanChooseTypeActivity.class);
        intent.putExtra("EMPLOYEE_ID", employeeId);
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        // Show logout confirmation when back button is pressed
        showLogoutConfirmation();
    }
}