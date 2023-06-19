package com.kurata.booking.ui.bookingdetail;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.kurata.booking.R;
import com.kurata.booking.data.model.Booking;
import com.kurata.booking.data.model.Promotion;
import com.kurata.booking.data.model.Room;
import com.kurata.booking.databinding.ActivityBookingDetailBinding;
import com.kurata.booking.ui.payment.Payment_method;
import com.kurata.booking.utils.Constants;
import com.kurata.booking.utils.Preference;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicReference;

public class booking_detail extends AppCompatActivity {
    private Date checkin, checkout, birth, today;
    private ActivityBookingDetailBinding binding;
    private FirebaseFirestore firestore;
    private boolean check = false;
    private String temp;
    private Preference preferenceManager;
    final Calendar myCalendar = Calendar.getInstance();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        binding = ActivityBookingDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        //init firestore
        firestore = FirebaseFirestore.getInstance();
        //init
        preferenceManager = new Preference(getApplicationContext());

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        Bundle args = getIntent().getBundleExtra("BUNDLE");
        Room object = (Room) args.getSerializable("model");
        temp = object.getPrice();
        checkin = new Date(getIntent().getLongExtra("checkin", -1));
        checkout = new Date(getIntent().getLongExtra("checkout", -1));
        today = new Date();
        today = myCalendar.getTime();
        birth =new Date();
//        checkin.setTime( getIntent().getLongExtra("checkin", -1));
//        checkout.setTime( getIntent().getLongExtra("checkout", -1));
        SimpleDateFormat format = new SimpleDateFormat("dd MMM yyyy");

        DatePickerDialog.OnDateSetListener dateBirth= (view, year, month, day) -> {
            myCalendar.set(Calendar.YEAR, year);
            myCalendar.set(Calendar.MONTH, month);
            myCalendar.set(Calendar.DAY_OF_MONTH, day);
            birth = myCalendar.getTime();
            updateLabel(binding.birth);
        };

        binding.birth.setOnClickListener(view -> new DatePickerDialog(booking_detail.this, dateBirth, myCalendar.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH), myCalendar.get(Calendar.DAY_OF_MONTH)).show());

        binding.txtPrice.setText(object.getPrice());

        binding.txtCheckin.setText(format.format(checkin.getTime()));
        binding.txtCheckout.setText(format.format(checkout.getTime()));

        binding.back.setOnClickListener(v-> onBackPressed());

        binding.btnChooseRoom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(binding.txtSpecialRequest.getText().toString().isEmpty() || binding.name.getText().toString().isEmpty() ||
                    binding.birth.getText().toString().isEmpty() || binding.email.getText().toString().isEmpty()){
                    Toast.makeText(booking_detail.this, "khong duoc de trong", Toast.LENGTH_SHORT).show();
                }else{
                    Intent i = new Intent(getApplicationContext(), Payment_method.class);
                    Bundle bundle = new Bundle();
                    Booking model = new Booking();
                    model.setGuestname(binding.name.getText().toString());
                    model.setGuestemail(binding.email.getText().toString());
                    model.setGuestbirth(birth);
                    model.setSpecicalrequest(binding.request.getText().toString());
                    model.setCheckin(checkin);
                    model.setRoomfee(binding.txtPrice.getText().toString());
                    model.setCheckout(checkout);
                    model.setDiscount(binding.promotion.getText().toString());
                    bundle.putSerializable("model", model);
                    bundle.putSerializable("room", object);
                    i.putExtra("BUNDLE",bundle);
                    i.putExtra("checkin", getIntent().getLongExtra("checkin", -1));
                    i.putExtra("checkout", getIntent().getLongExtra("checkout", -1));
                    startActivity(i);
                    overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_up);
                }
            }
        });

        binding.btnapply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String code = binding.promotion.getText().toString();
                if(code.isEmpty()){
                    Toast.makeText(booking_detail.this, "Voucher is empty", Toast.LENGTH_SHORT).show();
                }else{
                    if(check){
                        binding.btnapply.setText("Apply");
                        binding.promotion.setText("");
                        binding.txtPrice.setText(temp);
                        binding.notify.setVisibility(View.GONE);
                        check =false;
                        //updateDiscount( String.valueOf(Integer.parseInt(preferenceManager.getString(Constants.P_REMAI).toString())+1));

                    }else{
                        firestore.collection("promotions").whereEqualTo("code",code).addSnapshotListener((value, error) -> {
                            if (error != null) return;
                            if (value != null) {
                                for (DocumentSnapshot document : value.getDocuments()) {
                                    Promotion model = document.toObject(Promotion.class);
                                    assert model != null;
                                    Date start = model.getTime_start();
                                    Date end = model.getTime_end();
                                    Log.d("today", today.toString());
                                    if( (today.before(end) || today.equals(end))
                                            && (today.after(start) || today.equals(start)) &&  Integer.parseInt(model.getRemai()) > 0 ){
                                        float booking = Float.parseFloat(model.getDiscount_ratio()) * Float.parseFloat(temp);
                                        object.setPrice(String.valueOf((int) booking ));
                                        binding.txtPrice.setText(String.valueOf((int) booking ));
                                        binding.notify.setVisibility(View.VISIBLE);
                                        binding.notify.setText("Voucher discount: - " + Float.parseFloat(model.getDiscount_ratio()) * 100 + " %"  );
                                        binding.btnapply.setText("Cancel");
                                        check = true;
                                        preferenceManager.putString(Constants.P_PROMOTION,model.getId());
                                        preferenceManager.putString(Constants.P_REMAI,model.getRemai());
                                    }
//                                    else{
//                                        Toast.makeText(booking_detail.this, "Voucher has been used or expired", Toast.LENGTH_SHORT).show();
//                                    }
                                }
                            }
                        });
                       updateDiscount( String.valueOf(Integer.parseInt(preferenceManager.getString(Constants.P_REMAI).toString())-1));
                    }

                }

            }
        });

    }

    private void updateLabel(EditText editText) {
        String myFormat = "dd-MM-yyyy";
        SimpleDateFormat dateFormat = new SimpleDateFormat(myFormat, Locale.US);
        editText.setText(dateFormat.format(myCalendar.getTime()));
    }

    private void updateDiscount(String remai){
        HashMap<String, Object> booking = new HashMap<>();
        booking.put("remai",remai);
        DocumentReference reference = firestore.collection("promotions").document(preferenceManager.getString(Constants.P_PROMOTION).toString());
        Log.d(" ID PROMOTION", preferenceManager.getString(Constants.P_PROMOTION).toString());
        reference.update(booking);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void setCalendar(LocalDateTime d) {
        myCalendar.set(Calendar.YEAR, d.getYear());
        myCalendar.set(Calendar.MONTH, d.getMonthValue());
        myCalendar.set(Calendar.DAY_OF_MONTH, d.getDayOfMonth());
    }
    
}