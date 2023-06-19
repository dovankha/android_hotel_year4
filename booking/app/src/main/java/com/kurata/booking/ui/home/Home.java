package com.kurata.booking.ui.home;

import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.kurata.booking.AdapterRecyclerView.PopularRecyclerViewAdapter;
import com.kurata.booking.AdapterRecyclerView.PromotionRecyclerViewAdapter;
import com.kurata.booking.R;
import com.kurata.booking.data.model.Hotel;
import com.kurata.booking.data.model.Popular;
import com.kurata.booking.data.model.Promotion;
import com.kurata.booking.databinding.FragmentHomeBinding;
import com.kurata.booking.ui.hoteldetails.Hotel_detail;
import com.kurata.booking.ui.search.Search;
import com.kurata.booking.utils.Constants;
import com.kurata.booking.utils.Preference;

import java.util.ArrayList;

import javax.inject.Inject;

public class Home extends Fragment implements  PopularRecyclerViewAdapter.PopularListener, PromotionRecyclerViewAdapter.PromotionListener{

    private static final String TAG = "PopularsFragment_Tag";
    ArrayList<Hotel> list = new ArrayList<Hotel>();
    ArrayList<Promotion> promotion = new ArrayList<Promotion>();
    private HomeViewModel mViewModel;
    private FragmentHomeBinding binding;
    private Preference preference;
    ArrayList<Popular> hotelid = new ArrayList<Popular>();
    //RecyclerAdapter --> Hotel
    @Inject
    PopularRecyclerViewAdapter recyclerAdapter;
    @Inject
    PromotionRecyclerViewAdapter promotionAdapter;

    public  Home () {

    }

    @Override
    public View  onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater,container,false);
        //binding = HomeBinding.inflate(inflater,container,false);
        View view = binding.getRoot();
        preference = new Preference(getContext());
        if(preference.getString(Constants.P_FULL_NAME)!=null){
            binding.fullName.setText("Hi, "+ preference.getString(Constants.P_FULL_NAME));
        }else{
            binding.fullName.setText("Hi");
        }

        mViewModel =  new ViewModelProvider(requireActivity()).get(HomeViewModel.class);
        mViewModel.init();


        recyclerAdapter = new PopularRecyclerViewAdapter(list, this);
        promotionAdapter = new PromotionRecyclerViewAdapter(promotion,this);

        binding.RecyclerView.setHasFixedSize(true);
        binding.RecyclerView.setLayoutManager(new LinearLayoutManager(
                getActivity(),
                LinearLayoutManager.HORIZONTAL,
                false));
        binding.RecyclerView.setAdapter(promotionAdapter);


//        binding.poads.setHasFixedSize(true);
//        binding.poads.setLayoutManager(new LinearLayoutManager(
//                getActivity(),
//                LinearLayoutManager.HORIZONTAL,
//                false));
//        binding.poads.setAdapter(recyclerAdapter);

        binding.poads1.setHasFixedSize(true);
        binding.poads1.setLayoutManager(new LinearLayoutManager(
                getActivity(),
                LinearLayoutManager.HORIZONTAL,
                false));
        binding.poads1.setAdapter(recyclerAdapter);


//        binding.poads2.setHasFixedSize(true);
//        binding.poads2.setLayoutManager(new LinearLayoutManager(
//                getActivity(),
//                LinearLayoutManager.HORIZONTAL,
//                false));
//        binding.poads2.setAdapter(recyclerAdapter);

        binding.city.setHasFixedSize(true);
        binding.city.setLayoutManager(new LinearLayoutManager(
                getActivity(),
                LinearLayoutManager.HORIZONTAL,
                false));
        binding.city.setAdapter(recyclerAdapter);

        mViewModel.getHotelID().observe(getViewLifecycleOwner(),hotelids -> {
            mViewModel.getHotelPopular().observe(getViewLifecycleOwner(), hotels -> {
                list.clear();
                list.addAll(hotels);
                recyclerAdapter.notifyDataSetChanged();
            });
            recyclerAdapter.notifyDataSetChanged();
        });

        mViewModel.getAllPromotion().observe(getViewLifecycleOwner(), promotions -> {
            promotion.clear();
            promotion.addAll(promotions);
            promotionAdapter.notifyDataSetChanged();
        });

        binding.search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), Search.class);
                startActivity(intent);
            }
        });

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel =  new ViewModelProvider(requireActivity()).get(HomeViewModel.class);
    }

    @Override
    public void onUserClicked(Hotel hotel) {
        Intent i = new Intent(getActivity(), Hotel_detail.class);
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


        Location x = new Location(LocationManager.GPS_PROVIDER);
        x.setLatitude(hotel.getLocation().getLatitude());
        x.setLongitude(hotel.getLocation().getLongitude());


        i.putExtra("ischeck", true);
        bundle.putSerializable("model", model);
        i.putExtra("BUNDLE",bundle);
        i.putExtra("location", (Parcelable) x);
        startActivity(i);
        getActivity().overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_up);
    }


    @Override
    public void onUserClicked(Promotion promotion) {

    }
}