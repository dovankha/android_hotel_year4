package com.kurata.booking.AdapterRecyclerView;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.kurata.booking.data.model.Booking;
import com.kurata.booking.data.model.Room;
import com.kurata.booking.databinding.MybookingBinding;

import java.util.ArrayList;
import java.util.List;

public class MybookingRecyclerViewAdapter extends RecyclerView.Adapter<MybookingRecyclerViewAdapter.MybookingViewHolder>{
    private List<Booking> list;
    private MybookingListener mybookingListener;


    public MybookingRecyclerViewAdapter (List<Booking> list ,MybookingListener mybookingListener) {
        this.list = list;
        this.mybookingListener = mybookingListener;
    }

    @NonNull
    @Override
    public MybookingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MybookingViewHolder(
                MybookingBinding.inflate(LayoutInflater.from(parent.getContext()),parent,false)
        );
    }

    @Override
    public void onBindViewHolder(@NonNull MybookingViewHolder holder, int position) {
        holder.setData(list.get(position),mybookingListener);

    }


    @Override
    public int getItemCount() {
        return list.size();
    }


    public void setFilteredList(ArrayList<Booking> ListToShow){
        this.list= ListToShow;
        notifyDataSetChanged();
    }

    public static class MybookingViewHolder extends RecyclerView.ViewHolder {
        MybookingBinding binding;

        public MybookingViewHolder(MybookingBinding itemBinding) {
            super(itemBinding.getRoot());
            binding = itemBinding;
        }

        void setData(Booking booking, MybookingListener mybookingListener){

            binding.bookingid.setText(booking.getId());
            binding.txttime.setText(booking.getTimebooking().toString());
            if(booking.getstatus()){
                binding.status.setText("Done");
            }else{
                binding.status.setText("Cancelled");
            }
            Room modelx = new Room();
            FirebaseFirestore.getInstance().collection("room").whereEqualTo("id",booking.getRoom_id()).addSnapshotListener((value, error) -> {
                if (error != null) return;
                if (value != null) {
                    for (DocumentSnapshot document : value.getDocuments()) {
                        Room model = document.toObject(Room.class);
                        assert model != null;
                        binding.txtPrice.setText(model.getPrice());
                        binding.txthotelname.setText(model.getName());
                        modelx.setImage(model.getImage());
                        modelx.setPrice(model.getPrice());
                        modelx.setName(model.getName());
                    }
                }
            });

            binding.getRoot().setOnClickListener(v -> {
                Booking model = new Booking();
                model.setId(booking.getId());
                model.setGuestemail(booking.getGuestemail());
                model.setGuestname(booking.getGuestname());
                model.setGuestbirth(booking.getGuestbirth());
                model.setCheckin(booking.getCheckin());
                model.setCheckout(booking.getCheckout());
                model.setRoom_id(booking.getRoom_id());
                model.setStatus(booking.getstatus());
                mybookingListener.onUserClicked(model, modelx);
            });
        }


    }

    public interface MybookingListener {
        void onUserClicked(Booking booking, Room room);
    }
}
