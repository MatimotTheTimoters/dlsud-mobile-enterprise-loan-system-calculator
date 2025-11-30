//package com.example.loansystemcalculator;
//
//import android.content.Intent;
//import android.os.Bundle;
//import android.view.View;
//import android.widget.Button;
//import android.widget.RadioGroup;
//
//import androidx.appcompat.app.AppCompatActivity;
//
//public class PopupActivity extends AppCompatActivity {
//
//    RadioGroup radioGroupMonths;
//    Button btnSubmit;
//    double interestRate = 0;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.popup_dialog);
//
//        radioGroupMonths = findViewById(R.id.radioGroupMonths);
//        btnSubmit = findViewById(R.id.btnSubmit);
//
//        btnSubmit.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                int selectedId = radioGroupMonths.getCheckedRadioButtonId();
//
//                if (selectedId == R.id.radio1to5) {
//                    interestRate = 0.0062;
//                } else if (selectedId == R.id.radio6to10) {
//                    interestRate = 0.0065;
//                } else if (selectedId == R.id.radio11to15) {
//                    interestRate = 0.0068;
//                } else if (selectedId == R.id.radio16to20) {
//                    interestRate = 0.0075;
//                } else if (selectedId == R.id.radio21to24) {
//                    interestRate = 0.0080;
//                }
//
//                Intent resultIntent = new Intent();
//                resultIntent.putExtra("selectedInterest", interestRate);
//                setResult(RESULT_OK, resultIntent);
//                finish(); // close popup
//            }
//        });
//    }
//}
