package com.kurata.booking.ui.otp;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.kurata.booking.data.model.User;
import com.kurata.booking.ui.main.MainActivity;
import com.kurata.booking.databinding.ActivityOtpBinding;
import com.kurata.booking.ui.profile.ProfileViewModel;
import com.kurata.booking.utils.Constants;
import com.kurata.booking.utils.Preference;

import java.util.concurrent.TimeUnit;

public class Otp extends AppCompatActivity {

    //private EditText mCode1, mCode2, mCode3 , mCode4, mCode5, mCode6;
    private String verificationId;

    private Preference preferenceManager;
    private ActivityOtpBinding binding;
    private ProfileViewModel profileViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        binding = ActivityOtpBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        //setContentView(R.layout.activity_otp);

        //init();
        setTextMobile();
        setupOTPInputs();
        setVerificationId();
        setListener();
    }

    private void setListener(){
        binding.veryOTP.setOnClickListener(v ->{
            if (binding.inputCode1.getText().toString().trim().isEmpty()
                    || binding.inputCode2.getText().toString().trim().isEmpty()
                    || binding.inputCode3.getText().toString().trim().isEmpty()
                    || binding.inputCode4.getText().toString().trim().isEmpty()
                    || binding.inputCode5.getText().toString().trim().isEmpty()
                    || binding.inputCode6.getText().toString().trim().isEmpty()){
                Toast.makeText(Otp.this, "Please enter valid code", Toast.LENGTH_SHORT).show();
                return;
            }
            String code =
                    binding.inputCode1.getText().toString()+
                            binding.inputCode2.getText().toString()+
                            binding.inputCode3.getText().toString()+
                            binding.inputCode4.getText().toString()+
                            binding.inputCode5.getText().toString()+
                            binding.inputCode6.getText().toString();

            if(verificationId != null){
                binding.mbar.setVisibility(View.VISIBLE);
                binding.veryOTP.setVisibility(View.INVISIBLE);
                PhoneAuthCredential phoneAuthCredential = PhoneAuthProvider.getCredential(
                        verificationId,
                        code
                );

                FirebaseAuth.getInstance().signInWithCredential(phoneAuthCredential)
                        .addOnCompleteListener(task -> {
                            binding.mbar.setVisibility(View.GONE);
                            binding.veryOTP.setVisibility(View.VISIBLE);
                            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            if(task.isSuccessful()){
                                FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
                                preferenceManager = new Preference(getApplicationContext());

                                ViewModelProvider.Factory test = (ViewModelProvider.Factory) new ViewModelProvider.NewInstanceFactory();
                                profileViewModel = new ViewModelProvider(getViewModelStore(), test).get(ProfileViewModel.class);
                                profileViewModel.init(firebaseUser.getUid());
                                profileViewModel.getAccount().observe(this, new Observer<User>() {
                                    @Override
                                    public void onChanged(User user) {
                                        if (user.getRole().equals("Customer") || user.getRole().equals("Admin") || (user.isStatus() != true)){
                                            FirebaseAuth mAuth = FirebaseAuth.getInstance();
                                            mAuth.signOut();
                                            Toast.makeText(Otp.this, "User does not permision or disable", Toast.LENGTH_SHORT).show();
                                        } else {
                                            preferenceManager.putString(Constants.P_ROLE, user.getRole());
                                            preferenceManager.putString(Constants.P_FULL_NAME, user.getfullName());
                                            Log.d("role test", preferenceManager.getString(Constants.P_ROLE));
                                            startActivity(intent);
                                            finish();
                                        }
                                    }
                                });
                            }else {
                                Toast.makeText(Otp.this, "The verification code entered was invalid", Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });

        binding.resendOtp.setOnClickListener(v -> {
            //verify phone number
            PhoneAuthOptions options =
                    PhoneAuthOptions.newBuilder()
                            .setPhoneNumber("+84"+getIntent().getStringExtra("mobile"))
                            .setTimeout(60L, TimeUnit.SECONDS)
                            .setActivity(this)
                            .setCallbacks(new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                                @Override
                                public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {

                                }

                                @Override
                                public void onVerificationFailed(@NonNull FirebaseException e) {

                                    Toast.makeText(Otp.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                }

                                @Override
                                public void onCodeSent(@NonNull String newVerificationId, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                                    verificationId = newVerificationId;
                                    Toast.makeText(Otp.this, "OTP Sent", Toast.LENGTH_SHORT).show();
                                }
                            })
                            .build();
            PhoneAuthProvider.verifyPhoneNumber(options);

            new CountDownTimer(30000, 1000) {

                public void onTick(long millisUntilFinished) {
                    binding.resendOtp.setVisibility(View.GONE);
                    binding.time.setVisibility(View.VISIBLE);
                    binding.time.setText("00 :" + millisUntilFinished / 1000);
                }

                public void onFinish() {
                    binding.time.setVisibility(View.GONE);
                    binding.resendOtp.setVisibility(View.VISIBLE);

                }
            }.start();

        });
    }

    private void setVerificationId(){
        verificationId = getIntent().getStringExtra("verificationId");
    }

    /** If Intent() getStringExtra == "mobile" -> startActivity(VerifyActivity),
     * (TextView) textMobile will be received value "user mobile number"*/
    private void setTextMobile(){
        binding.textMobile.setText(String.format(
                "+84-%s",getIntent().getStringExtra("mobile")
        ));
    }

    /** When the edittext1 (inputCode1) was inserted, the cursor will be jump to the
     * next edittext (in this case it would be "inputCode2")*/
    private void setupOTPInputs(){
        binding.inputCode1.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(!s.toString().trim().isEmpty()){
                    binding.inputCode2.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        binding.inputCode2.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(!s.toString().trim().isEmpty()){
                    binding.inputCode3.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        binding.inputCode3.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(!s.toString().trim().isEmpty()){
                    binding.inputCode4.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        binding.inputCode4.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(!s.toString().trim().isEmpty()){
                    binding.inputCode5.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        binding.inputCode5.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(!s.toString().trim().isEmpty()){
                    binding.inputCode6.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }
}