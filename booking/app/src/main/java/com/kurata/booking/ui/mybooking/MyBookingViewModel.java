package com.kurata.booking.ui.mybooking;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.kurata.booking.data.model.Booking;
import com.kurata.booking.data.model.Room;
import com.kurata.booking.data.repo.HotelRepo;

import java.util.List;

public class MyBookingViewModel extends ViewModel {
    // TODO: Implement the ViewModel
    private MutableLiveData<List<Booking>> mBooking;
    private MutableLiveData<List<Room>> mRoom;
    private HotelRepo mRepository;

    public void init(){
        if (mBooking!=null){
            return;
        }
        mRepository = HotelRepo.getInstance();

    }

    public LiveData<List<Booking>> getBooking(String userid){
        return mBooking=mRepository.getBooking(userid);
    }
    public LiveData<List<Room>> getRoom(String userid){
        return mRoom=mRepository.getroom(userid);
    }


}