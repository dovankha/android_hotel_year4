package com.kurata.booking.ui.payment;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.kurata.booking.R;
import com.kurata.booking.data.model.Booking;
import com.kurata.booking.data.model.Room;
import com.kurata.booking.databinding.ActivityBookingDetailBinding;
import com.kurata.booking.databinding.ActivityPaymentSuccessBinding;
import com.kurata.booking.ui.main.MainActivity;

import java.util.Date;

public class Payment_success extends AppCompatActivity {

    private ActivityPaymentSuccessBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        binding = ActivityPaymentSuccessBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);

        Bundle args = getIntent().getBundleExtra("BUNDLE");
        Booking booking = (Booking) args.getSerializable("model");
        Room room = (Room) args.getSerializable("room");

        Date timebooking = new Date();
        booking.setId("");
        booking.setStatus(true);
        booking.setPaystatus(true);
        booking.setTimebooking(timebooking);
        addBooking(booking);
        binding.txtname.setText(booking.getGuestname());
        binding.txtemail.setText(booking.getGuestemail());
        binding.txtdayofbirth.setText(booking.getGuestbirth().toString());
        binding.txtcheckin.setText(booking.getCheckin().toString());
        binding.txtcheckout.setText(booking.getCheckout().toString());


        binding.btnDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(i);
                overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_up);
                finish();
            }
        });
    }

    private void addBooking(Booking booking){
            Booking model = booking;
            FirebaseFirestore firestore = FirebaseFirestore.getInstance();
            firestore.collection("booking").document(FirebaseAuth.getInstance().getUid()).collection("order").add(model).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                @Override
                public void onSuccess(DocumentReference documentReference) {
                    // now here we need Item id and set into model
                    model.setId(documentReference.getId());
                    binding.txtOrderID.setText(documentReference.getId());
                    firestore.collection("booking").document(FirebaseAuth.getInstance().getUid()).collection("order").document(model.getId())
                            .set(model, SetOptions.merge()).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    // if data uploaded successfully then show ntoast
                                    Toast.makeText(Payment_success.this, "Your Booking Uploaded Successfully", Toast.LENGTH_SHORT).show();
                                    SystemClock.sleep(500);
                                }
                            });
                }
            });

        }
}