package com.kurata.booking.AdapterRecyclerView;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.kurata.booking.data.model.Hotel;
import com.kurata.booking.data.model.Promotion;
import com.kurata.booking.databinding.PopularHotelBinding;
import com.kurata.booking.databinding.PromotionBinding;

import java.util.ArrayList;
import java.util.List;

public class PromotionRecyclerViewAdapter  extends  RecyclerView.Adapter<PromotionRecyclerViewAdapter.PromotionViewHolder>{

    private List<Promotion> list;
    private PromotionListener promotionListener;


    public PromotionRecyclerViewAdapter (List<Promotion> list, PromotionListener promotionListener) {
        this.list = list;
        this.promotionListener = promotionListener;

    }


    @NonNull
    @Override
    public PromotionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new PromotionViewHolder(
                PromotionBinding.inflate(LayoutInflater.from(parent.getContext()),parent,false)
        );
    }

    @Override
    public void onBindViewHolder(@NonNull PromotionViewHolder holder, int position) {
        holder.setData(list.get(position), promotionListener);
    }


    @Override
    public int getItemCount() {
        return list.size();
    }

    public void setFilteredList(ArrayList<Promotion> ListToShow){
        this.list= ListToShow;
        notifyDataSetChanged();
    }

    public static class PromotionViewHolder extends RecyclerView.ViewHolder {
        PromotionBinding binding;

        public PromotionViewHolder(PromotionBinding itemBinding) {
            super(itemBinding.getRoot());
            binding = itemBinding;
        }

        void setData(Promotion promotion, PromotionListener  promotionListener ){
            Glide.with(binding.image).load(promotion.getImg()).into(binding.image);
            binding.getRoot().setOnClickListener(v -> {
                Promotion model = new Promotion();
                model.setName(promotion.getName());
                model.setId(promotion.getId());
                model.setImg(promotion.getImg());
                model.setCode(promotion.getCode());
                model.setHoteltypeID(promotion.getHoteltypeID());
                model.setRoomID(promotion.getRoomID());
                model.setSum(promotion.getSum());
                model.setDiscount_ratio(promotion.getDiscount_ratio());
                model.setTime_start(promotion.getTime_start());
                model.setTime_end(promotion.getTime_end());
                promotionListener .onUserClicked(model);
            });
        }

    }

    public interface PromotionListener  {
        void onUserClicked(Promotion promotion);
    }

}
