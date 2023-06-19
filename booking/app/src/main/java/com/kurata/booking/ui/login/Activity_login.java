package com.kurata.booking.ui.login;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.kurata.booking.data.model.User;
import com.kurata.booking.databinding.ActivityLoginBinding;
import com.kurata.booking.ui.forgetpassword.Forgot_password;
import com.kurata.booking.ui.main.MainActivity;
import com.kurata.booking.ui.phonelogin.Phone_login;
import com.kurata.booking.ui.profile.ProfileViewModel;
import com.kurata.booking.ui.signup.Sign_up;
import com.kurata.booking.utils.Constants;
import com.kurata.booking.utils.Preference;

public class Activity_login extends AppCompatActivity {

    FirebaseAuth mAuth;
    private ActivityLoginBinding binding;
    private Preference preferenceManager;
    private ProfileViewModel profileViewModel;


    public Activity_login() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);


        mAuth = FirebaseAuth.getInstance();


        binding.txtForgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Activity_login.this, Forgot_password.class));
            }

        });


        binding.btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loginUser();
            }
        });


        binding.phone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Activity_login.this, Phone_login.class));
            }
        });

        binding.signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Activity_login.this, Sign_up.class));
            }
        });

        binding.txtBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Activity_login.this, MainActivity.class));
            }
        });
    }


    private void loginUser() {
        String email = binding.txtemail.getText().toString();
        String password = binding.txtpassword.getText().toString();

        if (TextUtils.isEmpty(email)) {
            binding.txtemail.setError("Email cannot be empty");
            binding.txtemail.requestFocus();
        } else if (TextUtils.isEmpty(password)) {
            binding.txtpassword.setError("Password cannot be empty");
            binding.txtpassword.requestFocus();
        } else {
            mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
                        getRole(firebaseUser.getUid());

                    } else {
                        Toast.makeText(Activity_login.this, "Log in Error: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        Log.d("LOG",task.getException().getMessage());
                    }
                }
            });
        }
    }

    private void getRole(String currentId){
        preferenceManager = new Preference(getApplicationContext());
        ViewModelProvider.Factory test = (ViewModelProvider.Factory) new ViewModelProvider.NewInstanceFactory();
        profileViewModel = new ViewModelProvider(getViewModelStore(), test).get(ProfileViewModel.class);
        profileViewModel.init(currentId);
        profileViewModel.getAccount().observe(this, new Observer<User>() {
            @Override
            public void onChanged(User user) {
                //Update(user);
                if (user.getRole().equals("Admin") || user.getRole().equals("Customer") || (user.isStatus() != true)){
                    FirebaseAuth mAuth = FirebaseAuth.getInstance();
                    mAuth.signOut();
                    Toast.makeText(Activity_login.this, "User does not permision or disable", Toast.LENGTH_SHORT).show();
                } else{
                    preferenceManager.putString(Constants.P_ROLE, user.getRole());
                    preferenceManager.putString(Constants.P_FULL_NAME, user.getfullName());
                    Log.d("role test", preferenceManager.getString(Constants.P_ROLE));
                    Toast.makeText(Activity_login.this, "User logged in successfully", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(Activity_login.this, MainActivity.class));
                    finish();
                }
            }
        });
    }


}