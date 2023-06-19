package com.kurata.booking.ui.roomlist;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.kurata.booking.AdapterRecyclerView.RoomsRecyclerViewAdapter;
import com.kurata.booking.R;
import com.kurata.booking.data.model.Booking;
import com.kurata.booking.data.model.Hotel;
import com.kurata.booking.data.model.Room;
import com.kurata.booking.databinding.ActivityRoomlistBinding;
import com.kurata.booking.ui.home.HomeViewModel;
import com.kurata.booking.ui.login.Activity_login;
import com.kurata.booking.ui.room_detail.Room_detail;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicInteger;

import javax.inject.Inject;

public class Roomlist extends AppCompatActivity implements  RoomsRecyclerViewAdapter.RoomListener{

    private ActivityRoomlistBinding binding;
    private Date checkin, checkout;
    private long cin, cout;
    final Calendar myCalendar = Calendar.getInstance();

    ArrayList<Room> list = new ArrayList<Room>();
    private HomeViewModel mViewModel;
    //RecyclerAdapter --> Room
    @Inject
    RoomsRecyclerViewAdapter recyclerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        binding = ActivityRoomlistBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        //get hotel_id
        Bundle args = getIntent().getBundleExtra("BUNDLE");
        Hotel object = (Hotel) args.getSerializable("model");

        cin = getIntent().getLongExtra("checkin", -1);
        cout = getIntent().getLongExtra("checkout", -1);
        //get date data

        checkin = new Date(cin);
        checkout = new Date(cout);

        //init
        mViewModel = new ViewModelProvider(this).get(HomeViewModel.class);
        mViewModel.init();

        FirebaseFirestore  firestore = FirebaseFirestore.getInstance();
        recyclerAdapter = new RoomsRecyclerViewAdapter(list,this, cin, cout );

        binding.room.setHasFixedSize(true);
        binding.room.setLayoutManager(new LinearLayoutManager(
                this,
                LinearLayoutManager.VERTICAL,
                false));
        binding.room.setAdapter(recyclerAdapter);

        DatePickerDialog.OnDateSetListener dateCheckIn= (view, year, month, day) -> {
            myCalendar.set(Calendar.YEAR, year);
            myCalendar.set(Calendar.MONTH, month);
            myCalendar.set(Calendar.DAY_OF_MONTH, day);
            checkin = myCalendar.getTime();
            cin = checkin.getTime();
            updateLabel(binding.dateCkin);
        };

        //binding.dateCkin.setOnClickListener(view -> new DatePickerDialog(Roomlist.this, dateCheckIn, myCalendar.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH), myCalendar.get(Calendar.DAY_OF_MONTH)).show());
        binding.dateCkin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            recyclerAdapter = new RoomsRecyclerViewAdapter(list, (RoomsRecyclerViewAdapter.RoomListener) view.getContext(), cin, cout );
            binding.room.setAdapter(recyclerAdapter);
                new DatePickerDialog(Roomlist.this, dateCheckIn, myCalendar.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH), myCalendar.get(Calendar.DAY_OF_MONTH)).show();
                mViewModel.getRoom(object.getId(),cin,cout).observe((LifecycleOwner) view.getContext(), rooms -> {
                    list.clear();
                    list.addAll(rooms);
                    recyclerAdapter.notifyDataSetChanged();
                });
            }
        });

        DatePickerDialog.OnDateSetListener dateCheckOut= (view, year, month, day) -> {
            myCalendar.set(Calendar.YEAR, year);
            myCalendar.set(Calendar.MONTH, month);
            myCalendar.set(Calendar.DAY_OF_MONTH, day);
            checkout = myCalendar.getTime();
            cout = checkout.getTime();
            updateLabel(binding.dateCkout);
        };

        binding.dateCkout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                mViewModel.getRoom(object.getId(),cin,cout).observe((LifecycleOwner) view.getContext(), rooms -> {
                    list.clear();
                    list.addAll(rooms);
                    recyclerAdapter.notifyDataSetChanged();
                });
                new DatePickerDialog(Roomlist.this, dateCheckOut, myCalendar.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH), myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }

        });


        SimpleDateFormat format = new SimpleDateFormat("dd MMM");
        String ckin = format.format(checkin);
        String ckout = format.format(checkout);

        //set date
        binding.dateCkin.setText(ckin);
        binding.dateCkout.setText(ckout);


        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        binding.back.setOnClickListener(v->onBackPressed());


        mViewModel.getRoom(object.getId(),cin,cout).observe(this, rooms -> {
            list.clear();
            list.addAll(rooms);
//            Room tmp;
//            AtomicInteger roombooking = new AtomicInteger();
//            for(int i=0; i < rooms.size(); i++){
//                tmp = rooms.get(i);
//                int sum = Integer.parseInt(tmp.getSum());
//                Log.d("tetstst", String.valueOf(sum));
//                Room finalTmp = tmp;
//                firestore.collectionGroup("order").whereEqualTo("room_id", tmp.getId()).addSnapshotListener((value1, error1) -> {
//                    if (error1 != null) return;
//                    if (value1 != null) {
//                        for (DocumentSnapshot bk : value1.getDocuments()) {
//                            Booking modelx = bk.toObject(Booking.class);
//                            assert modelx != null;
//                            //set date
//                            Date start = modelx.getCheckin();
//                            Date end = modelx.getCheckout();
//                            //Log.d("room", room_id);
//                            if( (checkin.before(end) || checkin.equals(end))
//                                    && (checkout.after(start) || checkout.equals(start))){
//                                Log.d(TAG,modelx.getRoom_id());
//                                roombooking.addAndGet(1);
//                            }
//                            else{
//                                Log.d(TAG,modelx.getRoom_id());
//                            }
//
//                        }
//                        Log.d("TAG", String.valueOf(roombooking.get()));
//                        if(roombooking.get() == 0){
//                            finalTmp.setRemai(finalTmp.getSum());
//                            list.add(finalTmp);
//                        }
//                        else if(roombooking.get() < sum){
//                            int temp = sum - roombooking.get();
//                            finalTmp.setRemai(String.valueOf(temp));
//                            list.add(finalTmp);
//                        }
//                        roombooking.set(0);
//                    }
//
//                });
//
//            }

            recyclerAdapter.notifyDataSetChanged();
        });

    }

    @Override
    public void onUserClicked(Room room) {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        if(mAuth.getCurrentUser() != null){
            Intent i = new Intent(getApplicationContext(), Room_detail.class);
            Room model = new Room();
            Bundle bundle = new Bundle();
            model.setId(room.getId());
            model.setName(room.getName());
            model.setAbout(room.getAbout());
            model.setStatus(room.getStatus());
            model.setHotel_id(room.getHotel_id());
            model.setName(room.getName());
            model.setHoteltype_id(room.getHoteltype_id());
            model.setPrice(room.getPrice());
            model.setImage(room.getImage());
            Log.d("remai room", room.getRemai().toString());
            bundle.putSerializable("model", model);

            i.putExtra("checkin", checkin.getTime());
            i.putExtra("checkout", checkout.getTime());
            i.putExtra("BUNDLE",bundle);
            startActivity(i);
            overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_up);
        }else{
            Intent intent = new Intent(getApplicationContext(), Activity_login.class);
            startActivity(intent);
            finish();
        }
    }

    private void updateLabel(TextView editText) {
        String myFormat = "dd MMM";
        SimpleDateFormat dateFormat = new SimpleDateFormat(myFormat, Locale.US);
        editText.setText(dateFormat.format(myCalendar.getTime()));
    }

    public AtomicInteger getRoombooking (String room_id, long checkin, long checkout){
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        AtomicInteger roombooking = new AtomicInteger();
        Date ckinx = new Date(checkin);
        Date ckoutx = new Date(checkout);
        firestore.collectionGroup("order").whereEqualTo("room_id", room_id).addSnapshotListener((value1, error1) -> {
            if (error1 != null) return;
            if (value1 != null) {
                for (DocumentSnapshot bk : value1.getDocuments()) {
                    Booking modelx = bk.toObject(Booking.class);
                    assert modelx != null;
                    //set date
                    Date start = modelx.getCheckin();
                    Date end = modelx.getCheckout();
                    //Log.d("room", room_id);
                    if(modelx.getstatus() == true && (ckinx.before(end) || ckinx.equals(end))
                            && (ckoutx.after(start) || ckoutx.equals(start))){
                        Log.d(TAG,room_id);
                        roombooking.addAndGet(1);
                    }
                    else{
                        Log.d(TAG,room_id);
                    }

                }
            }
            //booking.set(roombooking);
            Log.d("TAG", String.valueOf(roombooking.get()));
        });
        return roombooking;
    }
}