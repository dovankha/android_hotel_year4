package com.kurata.booking.ui.payment;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.view.WindowManager;

import com.kurata.booking.R;
import com.kurata.booking.databinding.ActivityPaymentBinding;
import com.kurata.booking.databinding.ActivityPaymentMethodBinding;

public class Payment extends AppCompatActivity {
    private ActivityPaymentBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        binding = ActivityPaymentBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        long time = getIntent().getLongExtra("time",0);

        new CountDownTimer(time, 1000) {

            public void onTick(long millisUntilFinished) {
                // binding.timePm.setText("00 :" + millisUntilFinished / 1000);
                int seconds = (int) (millisUntilFinished / 1000);
                int minutes = seconds / 60;
                seconds = seconds % 60;
                binding.timepm.setText("TIME : " + String.format("%02d", minutes)
                        + ":" + String.format("%02d", seconds));
            }
            public void onFinish() {
            }
        }.start();

        binding.txtday.setText(getIntent().getStringExtra("checkin"));

        binding.back.setOnClickListener(v-> onBackPressed());
        binding.btnChoose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), Payment_success.class);
                startActivity(i);
                overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_up);
            }
        });
    }
}