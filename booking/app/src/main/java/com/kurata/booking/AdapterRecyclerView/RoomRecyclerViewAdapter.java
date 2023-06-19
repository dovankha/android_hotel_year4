package com.kurata.booking.AdapterRecyclerView;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.kurata.booking.data.model.Room;
import com.kurata.booking.databinding.MybookingBinding;

import java.util.ArrayList;
import java.util.List;

public class RoomRecyclerViewAdapter extends RecyclerView.Adapter<RoomRecyclerViewAdapter.RoomViewHolder> {
    private List<Room> list;
    private RoomListener roomListener;


    public RoomRecyclerViewAdapter(List<Room> list, RoomListener roomListener) {
        this.list = list;
        this.roomListener = roomListener;

    }

    @NonNull
    @Override
    public RoomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new RoomViewHolder(
                MybookingBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false)
        );
    }

    @Override
    public void onBindViewHolder(@NonNull RoomViewHolder holder, int position) {
        holder.setData(list.get(position), roomListener);

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public void setFilteredList(ArrayList<Room> ListToShow) {
        this.list = ListToShow;
        notifyDataSetChanged();
    }

    public static class RoomViewHolder extends RecyclerView.ViewHolder {
        MybookingBinding binding;

        public RoomViewHolder(MybookingBinding itemBinding) {
            super(itemBinding.getRoot());
            binding = itemBinding;
        }

        void setData(Room room, RoomListener roomListener) {
            binding.txtPrice.setText(room.getPrice());

//            binding.getRoot().setOnClickListener(v -> {
//                Room model = new Room();
//                model.setId(room.getId());
//                model.setName(room.getName());
//                model.setAbout(room.getAbout());
//                model.setStatus(room.getStatus());
//                model.setHotel_id(room.getHotel_id());
//                model.setName(room.getName());
//                model.setHoteltype_id(room.getHoteltype_id());
//                model.setPrice(room.getPrice());
//                model.setImage(room.getImage());
//                roomListener.onUserClicked(model);
//            });

        }
    }

    public interface RoomListener {
        void onUserClicked(Room room);
    }
}
