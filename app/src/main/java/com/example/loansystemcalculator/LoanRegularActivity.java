package com.example.loansystemcalculator;

import android.app.Dialog;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class LoanRegularActivity extends AppCompatActivity {


    private RadioGroup radio1to5;
    private RadioGroup radio6to10;
    private RadioGroup radio11to15;
    private RadioGroup radio16to20;
    private RadioGroup radio21to24;
    private int selectedMonths = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_loan_regular);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        Dialog dialog = new Dialog(LoanRegularActivity.this);
        dialog.setContentView(R.layout.popup_dialog);



        Button btnChooseMonths = findViewById(R.id.btnChooseMonths);

        btnChooseMonths.setOnClickListener(v -> {
            Dialog dialog = new Dialog(LoanRegularActivity.this);
            dialog.setContentView(R.layout.popup_dialog);
            dialog.setCancelable(true);

            RadioGroup radioGroupMonths = dialog.findViewById(R.id.radioGroupMonths);
            Button btnSubmit = dialog.findViewById(R.id.btnSubmit);

            btnSubmit.setOnClickListener(v1 -> {
                int selectedId = radioGroupMonths.getCheckedRadioButtonId();

                switch (selectedId) {
                    case R.id.radio1to5:
                        selectedMonths = 5;
                        break;
                    case R.id.radio6to10:
                        selectedMonths = 10;
                        break;
                    case R.id.radio11to15:
                        selectedMonths = 15;
                        break;
                    case R.id.radio16to20:
                        selectedMonths = 20;
                        break;
                    case R.id.radio21to24:
                        selectedMonths = 24;
                        break;
                    default:
                        Toast.makeText(this, "Please select a month range", Toast.LENGTH_SHORT).show();
                        return;
                }

                Toast.makeText(this, "Selected: " + selectedMonths + " months", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            });

            dialog.show();
        });
    }
}
