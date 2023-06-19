package com.kurata.booking.ui.room_detail;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatActivity;

import com.kurata.booking.AdapterRecyclerView.ViewPagerAdapter;
import com.kurata.booking.R;
import com.kurata.booking.data.model.Booking;
import com.kurata.booking.data.model.Room;
import com.kurata.booking.databinding.ActivityRoomDetailBinding;
import com.kurata.booking.ui.bookingdetail.booking_detail;

public class Room_detail extends AppCompatActivity {
    private ActivityRoomDetailBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        binding = ActivityRoomDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        //get hotel_id
        Bundle args = getIntent().getBundleExtra("BUNDLE");
        Room object = (Room) args.getSerializable("model");

        binding.txtTropicaliaReso.setText(object.getName());
        binding.txtPrice.setText(object.getPrice());

        ViewPagerAdapter adapter = new ViewPagerAdapter(this, object.getImage());
        binding.viewPager.setAdapter(adapter);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        binding.btnChoose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), booking_detail.class);
                Room model = new Room();
                Bundle bundle = new Bundle();
                model.setId(object.getId());
                model.setName(object.getName());
                model.setAbout(object.getAbout());
                model.setStatus(object.getStatus());
                model.setHotel_id(object.getHotel_id());
                model.setName(object.getName());
                model.setHoteltype_id(object.getHoteltype_id());
                model.setPrice(object.getPrice());
                model.setImage(object.getImage());
                bundle.putSerializable("model", model);
                i.putExtra("checkin", getIntent().getLongExtra("checkin", -1));
                i.putExtra("checkout", getIntent().getLongExtra("checkout", -1));
                i.putExtra("BUNDLE",bundle);
                startActivity(i);
                overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_up);
            }
        });

        binding.back.setOnClickListener(v->onBackPressed());

    }
}