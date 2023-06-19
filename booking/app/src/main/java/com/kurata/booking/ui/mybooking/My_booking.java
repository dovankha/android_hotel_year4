package com.kurata.booking.ui.mybooking;

import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.kurata.booking.AdapterRecyclerView.MybookingRecyclerViewAdapter;
import com.kurata.booking.AdapterRecyclerView.RoomRecyclerViewAdapter;
import com.kurata.booking.R;
import com.kurata.booking.data.model.Booking;
import com.kurata.booking.data.model.Hotel;
import com.kurata.booking.data.model.Room;
import com.kurata.booking.databinding.FragmentMyBookingBinding;
import com.kurata.booking.ui.room_detail.Room_detail;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

public class My_booking extends Fragment implements MybookingRecyclerViewAdapter.MybookingListener{

    private MyBookingViewModel mViewModel;
    private FragmentMyBookingBinding binding;
    ArrayList<Booking> list = new ArrayList<Booking>();
    ArrayList<Room> room = new ArrayList<Room>();

    //RecyclerAdapter --> booking
    @Inject
    MybookingRecyclerViewAdapter recyclerAdapter;
    @Inject
    RoomRecyclerViewAdapter recyclerAdapter1;

    public My_booking () {

    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentMyBookingBinding.inflate(inflater,container,false);
        View view = binding.getRoot();

        //get Userid
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String uid = user.getUid();

        //recyclerview
        mViewModel =  new ViewModelProvider(requireActivity()).get(MyBookingViewModel.class);
        mViewModel.init();

        recyclerAdapter = new MybookingRecyclerViewAdapter(list,this);

        binding.mybooking.setHasFixedSize(true);
        binding.mybooking.setLayoutManager(new LinearLayoutManager(
                getActivity(),
                LinearLayoutManager.VERTICAL,
                false));
        binding.mybooking.setAdapter(recyclerAdapter);


        mViewModel.getBooking(uid).observe(getViewLifecycleOwner(),bookings -> {
            list.clear();
            list.addAll(bookings);
            recyclerAdapter.notifyDataSetChanged();
        });




        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(MyBookingViewModel.class);
        // TODO: Use the ViewModel
    }

    @Override
    public void onUserClicked(Booking booking, Room room) {
        Intent i = new Intent(getActivity(), mybookingdetail.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("booking", booking);
        bundle.putSerializable("room", room);
        i.putExtra("BUNDLE",bundle);
        startActivity(i);
        getActivity().overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_up);
    }

}