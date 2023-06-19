package com.kurata.booking.AdapterRecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.kurata.booking.R;
import com.kurata.booking.data.model.Room;
import com.kurata.booking.databinding.ResultRoomListBinding;
import com.kurata.booking.ui.bookingdetail.booking_detail;
import com.kurata.booking.ui.login.Activity_login;
import com.kurata.booking.ui.room_detail.Room_detail;

import java.util.ArrayList;
import java.util.List;

public class RoomsRecyclerViewAdapter extends RecyclerView.Adapter<RoomsRecyclerViewAdapter.RoomViewHolder> {
    private List<Room> list;
    private RoomListener roomListener;
    Long checkin, checkout;
    Context context;

    public RoomsRecyclerViewAdapter (List<Room> list, RoomListener roomListener, Long checkin, Long checkout) {
        this.list = list;
        this.roomListener = roomListener;
        this.checkin = checkin;
        this.checkout = checkout;

    }

    @NonNull
    @Override
    public RoomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new RoomViewHolder(
                ResultRoomListBinding.inflate(LayoutInflater.from(parent.getContext()),parent,false)
        );
    }

    @Override
    public void onBindViewHolder(@NonNull RoomViewHolder holder, int position) {
        holder.setData(list.get(position), roomListener, checkin,checkout);

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public void setFilteredList(ArrayList<Room> ListToShow){
        this.list= ListToShow;
        notifyDataSetChanged();
    }

    public static class RoomViewHolder extends RecyclerView.ViewHolder {
        ResultRoomListBinding binding;

        public RoomViewHolder(ResultRoomListBinding itemBinding) {
            super(itemBinding.getRoot());
            binding = itemBinding;
        }

        void setData(Room room, RoomListener roomListener ,Long checkin, Long checkout){
            ArrayList<String> arrayList = room.getImage();
            binding.roomname.setText(room.getName());
            ViewPagerAdapter adapter = new ViewPagerAdapter(itemView.getContext(), arrayList);
            binding.image.setAdapter(adapter);
            binding.txtPrice.setText(room.getPrice());
            binding.txtOldPrice.setText(room.getRemai());
            binding.btnChoose.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    FirebaseAuth mAuth = FirebaseAuth.getInstance();
                    if(mAuth.getCurrentUser() != null){
                        Intent i = new Intent(view.getContext(), booking_detail.class);
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
                        model.setImage(room.getImage());

                        bundle.putSerializable("model", model);
                        i.putExtra("checkin", checkin);
                        i.putExtra("checkout", checkout);
                        i.putExtra("BUNDLE",bundle);
                        view.getContext().startActivity(i);
                    }else{
                        Intent intent = new Intent(view.getContext(), Activity_login.class);
                        view.getContext().startActivity(intent);
                        //view.getContext().finish();
                    }
                }
            });
            binding.getRoot().setOnClickListener(v -> {
                Room model = new Room();
                model.setId(room.getId());
                model.setName(room.getName());
                model.setAbout(room.getAbout());
                model.setStatus(room.getStatus());
                model.setHotel_id(room.getHotel_id());
                model.setName(room.getName());
                model.setHoteltype_id(room.getHoteltype_id());
                model.setPrice(room.getPrice());
                model.setImage(room.getImage());
                model.setRemai(room.getRemai());
                model.setSum(room.getSum());
                roomListener.onUserClicked(model);
            });

        }
    }

    public interface RoomListener {
        void onUserClicked(Room room);
    }
}
