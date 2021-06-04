package com.sbdevs.ecommerce.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.sbdevs.ecommerce.R;

public class OTPvarificationActivity extends AppCompatActivity {
    private TextView phoneNoTxt;
    private EditText otp;
    private Button verifyBtn;
    private String mobileNo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otp_varification);

        phoneNoTxt = findViewById(R.id.phon_No_txt);
        otp = findViewById(R.id.enter_OTP);
        verifyBtn = findViewById(R.id.verify_btn);
        mobileNo = getIntent().getStringExtra("mobileNo");

        phoneNoTxt.setText("Verification OTP has been sent to +91 "+mobileNo);
    }
}