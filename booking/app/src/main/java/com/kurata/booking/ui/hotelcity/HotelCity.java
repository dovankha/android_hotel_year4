package com.kurata.booking.ui.hotelcity;

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
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.material.navigation.NavigationBarView;
import com.kurata.booking.AdapterRecyclerView.SearchRecyclerViewAdapter;
import com.kurata.booking.R;
import com.kurata.booking.data.model.Booking;
import com.kurata.booking.data.model.Hotel;
import com.kurata.booking.databinding.ActivityHotelCityBinding;
import com.kurata.booking.databinding.PopupFilterBinding;
import com.kurata.booking.ui.home.HomeViewModel;
import com.kurata.booking.ui.hoteldetails.Hotel_detail;
import com.kurata.booking.ui.map.Map;

import java.text.Normalizer;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.regex.Pattern;

import javax.inject.Inject;


public class HotelCity extends AppCompatActivity implements  SearchRecyclerViewAdapter.SearchListener{

    private HomeViewModel mViewModel;
    private Date checkin, checkout;

    ArrayList<Hotel> list = new ArrayList<Hotel>();

    @Inject
    SearchRecyclerViewAdapter recyclerAdapter;

    private ActivityHotelCityBinding binding;
    private PopupFilterBinding Cbinding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        binding = ActivityHotelCityBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);

        mViewModel = new ViewModelProvider(this).get(HomeViewModel.class);
        mViewModel.init();

        recyclerAdapter = new SearchRecyclerViewAdapter(list,this);

        binding.city.setHasFixedSize(true);
        binding.city.setLayoutManager(new LinearLayoutManager(
                this,
                LinearLayoutManager.VERTICAL,
                false));
        binding.city.setAdapter(recyclerAdapter);

        long cin  = getIntent().getLongExtra("checkin", -1);
        long cout  = getIntent().getLongExtra("checkout", -1);

        checkin = new Date(cin);
        checkout = new Date(cout);


        //checkout.setTime( getIntent().getLongExtra("checkout", -1));

        SimpleDateFormat format = new SimpleDateFormat("dd MMM");

        String ckin = format.format(checkin.getTime());
        String ckout = format.format(checkout.getTime());

        Log.d("checkin-format", ckin);
        Log.d("checkout-format", ckout);

        mViewModel.getHotelID().observe(this, hotelids -> {
            mViewModel.HotelSearch(removeAccent(getIntent().getStringExtra("hotel"))).observe(this, hotels -> {
                list.clear();
                list.addAll(hotels);
                recyclerAdapter.notifyDataSetChanged();
            });
            recyclerAdapter.notifyDataSetChanged();
        });


        //binding.date.setText(ckin + " - " + ckout);
        binding.back.setOnClickListener(v->onBackPressed());
        binding.bottomNavigation.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch(item.getItemId()){
                    case R.id.nav_filter:
                        Popup_filter(Gravity.BOTTOM);
                        break;
                    case R.id.nav_sort:
                        break;
                    case R.id.nav_map:
                        Intent i = new Intent(getApplicationContext(), Map.class);
                        startActivity(i);
                        break;
                }
                return true;
            }
        });

    }



    private void Popup_filter(int gravity) {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        Cbinding = PopupFilterBinding.inflate(getLayoutInflater());
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

        dialog.show();

    }

    @Override
    public void onUserClicked(Hotel hotel) {
        Intent i = new Intent(getApplicationContext(), Hotel_detail.class);
        Hotel model = new Hotel();
        Bundle bundle = new Bundle();


        model.setName(hotel.getName());
        model.setId(hotel.getId());
        model.setAddress(hotel.getAddress());
        model.setImage(hotel.getImage());
        model.setAbout(hotel.getAbout());
        model.setStatus(hotel.getStatus());
        model.setCitiID(hotel.getCitiID());
        model.setHoteltypeID(hotel.getHoteltypeID());


        i.putExtra("checkin", checkin.getTime());
        i.putExtra("checkout", checkout.getTime());

        Location x = new Location(LocationManager.GPS_PROVIDER);
        x.setLatitude(hotel.getLocation().getLatitude());
        x.setLongitude(hotel.getLocation().getLatitude());


        bundle.putSerializable("model", model);
        i.putExtra("location", (Parcelable) x);
        i.putExtra("BUNDLE",bundle);
        startActivity(i);
        overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_up);

    }

    //remove Accent
    public static String removeAccent(String s) {

        String temp = Normalizer.normalize(s, Normalizer.Form.NFD);
        Pattern pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");
        return pattern.matcher(temp).replaceAll("");
    }

}