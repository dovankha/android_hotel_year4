package com.kurata.booking.ui.hoteldetails;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.kurata.booking.AdapterRecyclerView.ViewPagerAdapter;
import com.kurata.booking.R;
import com.kurata.booking.data.model.Hotel;
import com.kurata.booking.databinding.ActivityHotelDetailBinding;
import com.kurata.booking.ui.roomlist.Roomlist;

import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.Date;

public class Hotel_detail extends AppCompatActivity implements OnMapReadyCallback {

    private ActivityHotelDetailBinding binding;
    Hotel object;
    Date checkin, checkout;
    FusedLocationProviderClient fusedClient;
    private static final int REQUEST_CODE = 101;
    GoogleMap gMap;
    Location currentLocation;
    Marker marker;
    private Double Latitude, Longitude;
    final Calendar myCalendar = Calendar.getInstance();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        binding = ActivityHotelDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);

        Bundle args = getIntent().getBundleExtra("BUNDLE");
        object = (Hotel) args.getSerializable("model");


        boolean ischeck = getIntent().getBooleanExtra("ischeck", false);

        if(ischeck){
            checkin = myCalendar.getTime();
            Log.d("day 1xx",checkin.toString());
            myCalendar.add(Calendar.DAY_OF_MONTH, 5);
            checkout = myCalendar.getTime();
        }else{
            long ckin = getIntent().getLongExtra("checkin", -1);
            Log.d("day 1",String.valueOf(ckin));
            long ckout = getIntent().getLongExtra("checkout", -1);
            myCalendar.setTimeInMillis(ckin);
            myCalendar.set(Calendar.HOUR_OF_DAY, 6);
            myCalendar.set(Calendar.MINUTE, 0);
            myCalendar.set(Calendar.SECOND, 0);
            checkin = myCalendar.getTime();
            Log.d("day 1 ckin",checkin.toString());
            myCalendar.setTimeInMillis(ckout);
            myCalendar.set(Calendar.HOUR_OF_DAY, 6);
            myCalendar.set(Calendar.MINUTE, 0);
            myCalendar.set(Calendar.SECOND, 0);
            checkout = myCalendar.getTime();
            Log.d("day 2 ckout",checkout.toString());
        }


        ViewPagerAdapter adapter = new ViewPagerAdapter(this, object.getImage());
        binding.viewPager.setAdapter(adapter);

        binding.back.setOnClickListener(v-> onBackPressed());

        binding.txtTropicaliaReso.setText(object.getName());
        binding.txtDescription.setText(object.getAbout());
        binding.txtCaliforniaUni.setText(object.getAddress());

        Location x = new Location(LocationManager.GPS_PROVIDER);
        fusedClient = LocationServices.getFusedLocationProviderClient(this);
        getLocation();

        binding.btnChooseRoom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle bundle = new Bundle();
                Intent i = new Intent(getApplicationContext(), Roomlist.class);
                Hotel model = new Hotel();
                model.setName(object.getName());
                model.setId(object.getId());
                model.setAddress(object.getAddress());
                model.setImage(object.getImage());
                model.setAbout(object.getAbout());
                model.setStatus(object.getStatus());
                model.setCitiID(object.getCitiID());
                model.setHoteltypeID(object.getHoteltypeID());

                i.putExtra("checkin", checkin.getTime() );
                i.putExtra("checkout", checkout.getTime());
                bundle.putSerializable("model", model);
                i.putExtra("BUNDLE",bundle);
                startActivity(i);
                overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_up);
            }
        });
    }

    private void getLocation() {

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE);
            return;
        }

       // binding.search.setQuery(getIntent().getStringExtra("address"),true);
        currentLocation = getIntent().getParcelableExtra("location");

        //Toast.makeText(getApplicationContext(), currentLocation.getLatitude() + "" + currentLocation.getLongitude(), Toast.LENGTH_SHORT).show();
        SupportMapFragment supportMapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        assert supportMapFragment != null;
        supportMapFragment.getMapAsync(Hotel_detail.this);


    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        this.gMap = googleMap;
        LatLng latLng = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
        MarkerOptions markerOptions = new MarkerOptions().position(latLng).title(object.getAddress());
        googleMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 18));
        googleMap.addMarker(markerOptions);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getLocation();
            }
        }
    }
}