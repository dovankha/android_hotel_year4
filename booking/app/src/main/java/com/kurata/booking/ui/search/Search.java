package com.kurata.booking.ui.search;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CalendarView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.kurata.booking.AdapterRecyclerView.PopularRecyclerViewAdapter;
import com.kurata.booking.R;
import com.kurata.booking.data.model.Hotel;
import com.kurata.booking.databinding.ActivitySearchBinding;
import com.kurata.booking.databinding.CalendarPopupBinding;
import com.kurata.booking.databinding.CheckoutPopupBinding;
import com.kurata.booking.ui.home.HomeViewModel;
import com.kurata.booking.ui.hotelcity.HotelCity;
import com.kurata.booking.ui.hoteldetails.Hotel_detail;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import javax.inject.Inject;

public class Search extends AppCompatActivity implements  PopularRecyclerViewAdapter.PopularListener{
    ActivitySearchBinding binding;
    CalendarPopupBinding Cbinding;
    CheckoutPopupBinding Ebinding;
    private HomeViewModel mViewModel;
    private Date checkin;
    private Date checkout;
    int days;

    ArrayList<Hotel> list = new ArrayList<Hotel>();

    @Inject
    PopularRecyclerViewAdapter recyclerAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        binding = ActivitySearchBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);

        binding.back.setOnClickListener(v->onBackPressed());

        mViewModel = new ViewModelProvider(this).get(HomeViewModel.class);
        mViewModel.init();


        recyclerAdapter = new PopularRecyclerViewAdapter(list,this);

        binding.city.setHasFixedSize(true);
        binding.city.setLayoutManager(new LinearLayoutManager(
                this,
                LinearLayoutManager.HORIZONTAL,
                false));
        binding.city.setAdapter(recyclerAdapter);


        mViewModel.getHotelID().observe(this, hotelids -> {
            mViewModel.getHotelPopular().observe(this, hotels -> {
                list.clear();
                list.addAll(hotels);
                recyclerAdapter.notifyDataSetChanged();
            });
            recyclerAdapter.notifyDataSetChanged();
        });

    binding.txtDuration.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Popup_Calender(Gravity.BOTTOM);
        }
    });


    binding.txtNightCounter.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Popup_NightCounter(Gravity.BOTTOM);
        }
    });

    binding.btnSearch.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            search();
        }
    });

    }

    @Override
    public void onUserClicked(Hotel hotel) {
        Intent i = new Intent(this, Hotel_detail.class);
        Hotel model = new Hotel();
        Bundle bundle = new Bundle();
        model.setId(hotel.getId());
        model.setName(hotel.getName());
        model.setAddress(hotel.getAddress());
        model.setAbout(hotel.getAbout());
        model.setStatus(hotel.getStatus());
        model.setImage(hotel.getImage());
        Location x = new Location(LocationManager.GPS_PROVIDER);
        x.setLatitude(hotel.getLocation().getLatitude());
        x.setLongitude(hotel.getLocation().getLongitude());


       
        i.putExtra("location", (Parcelable) x);

        i.putExtra("ischeck", true);
        bundle.putSerializable("model", model);
        i.putExtra("BUNDLE",bundle);
        startActivity(i);
        overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_up);
    }

    private void search(){
        Intent intent = new Intent(getApplicationContext(), HotelCity.class);
        intent.putExtra("hotel", binding.hotel.getText().toString());
        if(checkin == null || checkout == null){
            Toast.makeText(this, "Vui long chon ngay checkin checkout", Toast.LENGTH_SHORT).show();
        }
        else{
            intent.putExtra("checkin", checkin.getTime());
            intent.putExtra("checkout", checkout.getTime());
            startActivity(intent);
        }
    }

    private void Popup_Calender(int gravity) {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        Cbinding = CalendarPopupBinding.inflate(getLayoutInflater());
        dialog.setContentView(Cbinding.getRoot());


        Window window = dialog.getWindow();
        if(window == null){
            return;
        }
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        WindowManager.LayoutParams windowAttributes = window.getAttributes();
        windowAttributes.gravity = gravity;
        window.setAttributes(windowAttributes);
//        dialog.setCancelable(false);

        Cbinding.btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(checkin!=null){
                    dialog.dismiss();
                    Popup_NightCounter(Gravity.BOTTOM);
                }else{
                    Toast.makeText(Search.this, "Vui long chon ngay checkIn", Toast.LENGTH_SHORT).show();
                }
            }
        });

        Cbinding.checkin.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView calendarView, int year, int month, int dayofmonth) {
                String today = month+"/"+dayofmonth+"/"+year;
                Calendar calendar = Calendar.getInstance();
                Date temp;
                SimpleDateFormat fm = new SimpleDateFormat("yyyy MM dd");
                String formatx = fm.format(calendar.getTime());
                try {
                    temp = fm.parse(formatx);

                } catch (ParseException e) {
                    throw new RuntimeException(e);
                }

                calendar.set(year, month, dayofmonth);
                int dow = calendar.get(Calendar.DAY_OF_WEEK);
                SimpleDateFormat format1 = new SimpleDateFormat("yyyy MM dd");
                String format = format1.format(calendar.getTime());
                try {
                    checkin = format1.parse(format);
                } catch (ParseException e) {
                    throw new RuntimeException(e);
                }
                long diff = checkin.getTime() - temp.getTime();
                int diffDays = (int) (diff / (24 * 60 * 60 * 1000));
                if(diffDays>=0){
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                        if(dow==1){
                            binding.txtDuration.setText(DayOfWeek.of(7).name()+","+today);
                        }
                        else{
                            binding.txtDuration.setText(DayOfWeek.of(dow-1).name()+","+today);
                        }

                    }
                }else{
                    checkin = null;
                    Toast.makeText(Search.this, "Ngay checkin khong hop le\n Vui long chon lai", Toast.LENGTH_SHORT).show();
                }

            }
        });

        dialog.show();



    }


    private void Popup_NightCounter(int gravity) {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        Ebinding = CheckoutPopupBinding.inflate(getLayoutInflater());
        dialog.setContentView(Ebinding.getRoot());


        Window window = dialog.getWindow();
        if(window == null){
            return;
        }
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        WindowManager.LayoutParams windowAttributes = window.getAttributes();
        windowAttributes.gravity = gravity;
        window.setAttributes(windowAttributes);
//        dialog.setCancelable(false);

        Ebinding.btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (days>30)
                {
                    Toast.makeText(Search.this, "SO ngay ban chon vuot qua 30 ngay", Toast.LENGTH_SHORT).show();
                }
                else{
                    dialog.dismiss();
                }

            }
        });

        if (checkin != null){
            Ebinding.checkout.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
                @Override
                public void onSelectedDayChange(@NonNull CalendarView calendarView, int year, int month, int dayofmonth) {
                    String today = month+"/"+dayofmonth+"/"+year;
                    Calendar calendar = Calendar.getInstance();
                    calendar.set(year, month, dayofmonth);
                    int dow = calendar.get(Calendar.DAY_OF_WEEK);

                    SimpleDateFormat format1 = new SimpleDateFormat("yyyy MM dd");
                    String format = format1.format(calendar.getTime());
                    try {
                        checkout = format1.parse(format);

                    } catch (ParseException e) {
                        throw new RuntimeException(e);
                    }
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                        long diff = checkout.getTime() - checkin.getTime();
                        int diffDays = (int) (diff / (24 * 60 * 60 * 1000));
                        DayOfWeek dayOf = DayOfWeek.of(dow);
                        if(diffDays >= 1 ){
                            binding.txtNightCounter.setText(diffDays + " dem");
                            days = diffDays;
                        }else{
                            checkout =null;
                            Toast.makeText(Search.this, "Ngay CheckOut khong hop le \n Ngay checkOut > checkIn", Toast.LENGTH_SHORT).show();
                        }


                    }

                }
            });
        }
        else{
            Toast.makeText(this, "Vui long Check In", Toast.LENGTH_SHORT).show();
        }

        dialog.show();


    }


}