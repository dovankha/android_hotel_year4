package com.kurata.booking.AdapterRecyclerView;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.kurata.booking.data.model.Hotel;
import com.kurata.booking.databinding.ResultHotelSearchBinding;

import java.util.ArrayList;
import java.util.List;

public class SearchRecyclerViewAdapter extends  RecyclerView.Adapter<SearchRecyclerViewAdapter.SearchViewHolder>{

    private List<Hotel> list; //private List<Popular> popular;
    private SearchListener searchListener;


    public SearchRecyclerViewAdapter (List<Hotel> list, SearchListener searchListener) {
        this.list = list;
        this.searchListener = searchListener;
    }

    @NonNull
    @Override
    public SearchViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new SearchViewHolder(
                ResultHotelSearchBinding.inflate(LayoutInflater.from(parent.getContext()),parent,false)
        );
    }

    @Override
    public void onBindViewHolder(@NonNull SearchViewHolder holder, int position) {
        holder.setData(list.get(position), searchListener);

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public void setFilteredList(ArrayList<Hotel> ListToShow){
        this.list= ListToShow;
        notifyDataSetChanged();
    }

    public static class SearchViewHolder extends RecyclerView.ViewHolder {
        ResultHotelSearchBinding binding;

        public SearchViewHolder(ResultHotelSearchBinding itemBinding) {
            super(itemBinding.getRoot());
            binding = itemBinding;
        }

        void setData(Hotel hotel, SearchListener popularListener){
            ArrayList<String> arrayList = hotel.getImage();
            Glide.with(binding.image).load(arrayList.get(0).toString()).into(binding.image);
            binding.name.setText(hotel.getName());
            binding.address.setText(hotel.getAddress());

            binding.getRoot().setOnClickListener(v -> {
                Hotel model = new Hotel();
                model.setName(hotel.getName());
                model.setId(hotel.getId());
                model.setAddress(hotel.getAddress());
                model.setImage(hotel.getImage());
                model.setAbout(hotel.getAbout());
                model.setStatus(hotel.getStatus());
                model.setCitiID(hotel.getCitiID());
                model.setHoteltypeID(hotel.getHoteltypeID());
                model.setLocation(hotel.getLocation());
                popularListener.onUserClicked(model);
            });
        }

    }

    public interface SearchListener {
        void onUserClicked(Hotel hotel);
    }
}
