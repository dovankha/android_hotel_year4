package com.kurata.booking.ui.main;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.badge.BadgeDrawable;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.auth.FirebaseAuth;
import com.kurata.booking.R;
import com.kurata.booking.databinding.ActivityMainBinding;
import com.kurata.booking.ui.home.Home;
import com.kurata.booking.ui.login.Activity_login;
import com.kurata.booking.ui.mybooking.My_booking;
import com.kurata.booking.ui.profile.Profile;
import com.kurata.booking.ui.search.Search;

public class MainActivity extends AppCompatActivity {
    FirebaseAuth mAuth;
    Home fragmentHome = new Home();
    Profile fragmentProfile = new Profile();
    My_booking fragmentMybooking = new My_booking();
    ActivityMainBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_main);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);

        mAuth = FirebaseAuth.getInstance();

        getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout, fragmentHome).commit();

        binding.bottomNavigation.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch(item.getItemId()){
                    case R.id.nav_home:
                        if(mAuth.getCurrentUser() != null){
                            getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout, fragmentMybooking).commit();
                            break;
                        }else{
                            Intent intent = new Intent(getApplicationContext(), Activity_login.class);
                            startActivity(intent);
                            finish();
                        }
                    case R.id.nav_manage:
                        getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout, fragmentHome).commit();
                        break;
                    case R.id.nav_profile:
                        if(mAuth.getCurrentUser() != null){
                            getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout, fragmentProfile).commit();
                            break;
                        }else{
                            Intent intent = new Intent(getApplicationContext(), Activity_login.class);
                            startActivity(intent);
                            finish();
                        }



                }
                return true;
            }
        });

//        if(mAuth.getCurrentUser() != null){
//            Intent intent = new Intent(activity_splash_screen.this, MainActivity.class);
//            startActivity(intent);
//            finish();
//        }else{
//            Intent intent = new Intent(activity_splash_screen.this, Activity_login.class);
//            startActivity(intent);
//            finish();
//        }


    }



}