package com.kurata.booking.ui.home;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.kurata.booking.data.model.Booking;
import com.kurata.booking.data.model.Hotel;
import com.kurata.booking.data.model.Popular;
import com.kurata.booking.data.model.Promotion;
import com.kurata.booking.data.model.Room;
import com.kurata.booking.data.repo.HotelRepo;

import java.util.Date;
import java.util.List;

public class HomeViewModel extends ViewModel {
    // TODO: Implement the ViewModel
    private MutableLiveData<List<Hotel>> mHotels;
    private MutableLiveData<List<Popular>> mHotelID;
    private MutableLiveData<List<String>> mhotelID;
    private MutableLiveData<List<Room>> mRooms;
    private MutableLiveData<List<Booking>> mBookings;
    private MutableLiveData<List<Promotion>> mPromotions;
    private HotelRepo mRepository;

    public void init(){
        if (mHotels!=null || mRooms != null || mHotelID != null || mhotelID !=null || mBookings !=null || mPromotions != null){
            return;
        }
        mRepository = HotelRepo.getInstance();

    }


    public LiveData<List<Hotel>> getHotelPopular(){

        return mHotels=mRepository.getHotelPopular();
    }

    public LiveData<List<Popular>> getHotelID(){
        return mHotelID=mRepository.getPop();
    }

    public LiveData<List<String>> HotelID(){
        return mhotelID=mRepository.getPopz();
    }

    public LiveData<List<Hotel>> HotelSearch(String search){
        return mHotels=mRepository.getHotelActivate(search);
    }

    public LiveData<List<Room>> getRoom(String hotel_id, long checkin, long checkout){
        return mRooms=mRepository.getAllRoom(hotel_id, checkin, checkout);
    }

    public LiveData<List<Promotion>> getAllPromotion(){
        return mPromotions=mRepository.getAllPromotion();
    }

    public LiveData<List<Booking>> getAllbooking(){
        return mBookings=mRepository.getAllBooking();
    }

}