package com.kurata.booking.ui.mybooking;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.kurata.booking.R;
import com.kurata.booking.data.model.Booking;
import com.kurata.booking.data.model.Hotel;
import com.kurata.booking.data.model.Room;
import com.kurata.booking.databinding.ActivityBookingDetailBinding;
import com.kurata.booking.databinding.ActivityMainBinding;
import com.kurata.booking.databinding.ActivityMybookingdetailBinding;

import java.util.HashMap;
import java.util.Map;

public class mybookingdetail extends AppCompatActivity {
    private ActivityMybookingdetailBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        binding = ActivityMybookingdetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        //todo -getdata
        Bundle args = getIntent().getBundleExtra("BUNDLE");
        Room object = (Room) args.getSerializable("room");
        Booking booking= (Booking) args.getSerializable("booking");
        //todo - set data
        binding.txtOrderID.setText(booking.getId());
        binding.txtname.setText(booking.getGuestname());
        binding.txtemail.setText(booking.getGuestemail());
        binding.txtdayofbirth.setText(booking.getGuestbirth().toString());
        binding.txtcheckin.setText(booking.getCheckin().toString());
        binding.txtcheckout.setText(booking.getCheckout().toString());

        if(booking.getstatus()){

            binding.btnDone.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    UpdateStatus(booking.getId());
                    onBackPressed();
                }
            });
        }else{
            binding.btnDone.setText("Back");
            binding.btnDone.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onBackPressed();
                }
            });
        }

    }

    private void UpdateStatus(String bookingID){
        // Create a Firestore instance.
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference docRef = db.collection("booking").document(FirebaseAuth.getInstance().getUid()).collection("order").document(bookingID);
        Log.d("uid", FirebaseAuth.getInstance().getUid());
        Map<String, Object> updates = new HashMap<>();
        updates.put("status", false);
        docRef.update(updates);
        docRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(this, "Cancel succesfully", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Cancel failed", Toast.LENGTH_SHORT).show();
            }
        });
    }
}