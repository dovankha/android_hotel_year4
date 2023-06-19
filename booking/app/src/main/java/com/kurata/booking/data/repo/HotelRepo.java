package com.kurata.booking.data.repo;

import android.os.SystemClock;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.AggregateQuery;
import com.google.firebase.firestore.AggregateQuerySnapshot;
import com.google.firebase.firestore.AggregateSource;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.kurata.booking.data.model.Booking;
import com.kurata.booking.data.model.City;
import com.kurata.booking.data.model.Hotel;
import com.kurata.booking.data.model.Popular;
import com.kurata.booking.data.model.Promotion;
import com.kurata.booking.data.model.Room;
import com.kurata.booking.utils.Preference;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

public class HotelRepo {
    private static final String TAG = "HotelRepo";
    private Preference preferenceManager;

//    private Hotel hotel = new Hotel();
//    private Room room = new Room();
//    private Popular popular = new Popular();
//    private Booking booking = new Booking();

    private static HotelRepo instance;

    public static HotelRepo getInstance(){
        if (instance==null){
            instance= new HotelRepo();
        }
        return instance;
    }


    //get hotel activate
    public MutableLiveData<List<Hotel>> getHotelActivate(String search) {
        MutableLiveData<List<Hotel>> allHotel = new MutableLiveData<>();
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        firestore.collectionGroup("hotels").whereEqualTo("status", true).addSnapshotListener((value, error) -> {
            if (error != null) return;
            if (value != null) {
                List<Hotel> tempList = new ArrayList<>();
                for (DocumentSnapshot document : value.getDocuments()) {
                    Hotel model = document.toObject(Hotel.class);
                    assert model != null;
                    if(model.getName().toLowerCase().contains(search.toLowerCase())){
                        tempList.add(model);
                    }
                }

//                firestore.collection("Cities").addSnapshotListener((value1, error1) -> {
//                    if (error1 != null) return;
//                    if (value1 != null) {
//                        for (DocumentSnapshot document : value1.getDocuments()) {
//                            City model = document.toObject(City.class);
//                            assert model != null;
//                            if(model.getName().toLowerCase().contains(search.toLowerCase())){
//                                for (DocumentSnapshot documentx : value.getDocuments()) {
//                                    Hotel modelx = documentx.toObject(Hotel.class);
//                                    assert modelx != null;
//                                    if(modelx.getName().toLowerCase().contains(search.toLowerCase())){
//                                        tempList.add(modelx);
//                                    }
//                                }
//                            }
//                        }
//                    }
//                });
//
                allHotel.postValue(tempList);
            }
        });
        firestore.collectionGroup("hotels").whereEqualTo("status", true).addSnapshotListener((value, error) -> {
            if (error != null) return;
            if (value != null) {
                List<Hotel> tempListx = new ArrayList<>();
                firestore.collection("Cities").addSnapshotListener((value1, error1) -> {
                    if (error1 != null) return;
                    if (value1 != null) {
                        for (DocumentSnapshot document : value1.getDocuments()) {
                            City model = document.toObject(City.class);
                            assert model != null;
                            if(model.getName().toLowerCase().contains(search.toLowerCase())){
                                for (DocumentSnapshot documentx : value.getDocuments()) {
                                Hotel modelx = documentx.toObject(Hotel.class);
                                assert modelx != null;
                                if(modelx.getCitiID().equals(model.getId())){
                                    tempListx.add(modelx);
                                }
                            }
                            allHotel.postValue(tempListx);
                            }
                        }
                    }
                });

            }
        });
        return allHotel;
    }

    //get all hotel popular
    public MutableLiveData<List<Hotel>> getHotelPopular() {
        MutableLiveData<List<Hotel>> allHotel = new MutableLiveData<>();
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        List<String> test = new ArrayList<>();
        CollectionReference applicationsRef = firestore.collection("popular");
        DocumentReference applicationIdRef = applicationsRef.document("pop");
        applicationIdRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                List<String> tempList = new ArrayList<String>();
                DocumentSnapshot document = task.getResult();
                tempList = (List<String>) document.get("hotel_id");
                test.addAll(tempList);
                Log.d("Test hotel ID", test.toString());
                firestore.collectionGroup("hotels").addSnapshotListener((value, error) -> {
                    if (error != null) return;
                    if (value != null) {
                        List<Hotel> temp = new ArrayList<>();
                        for (DocumentSnapshot document1 : value.getDocuments()) {
                            if (test.contains(document1.getId())){
                                Hotel model = document1.toObject(Hotel.class);
                                assert model != null;
                                if(model.getStatus()){
                                    temp.add(model);
                                    Log.d("TEST HOTEL:", model.toString());
                                }
                            }else
                            {
                                Log.d("TEST HOTEL NO:","");
                            }
                        }
                        allHotel.postValue(temp);
                    }
                });
            }

        });

        return allHotel;
    }


    //get hotel ID
    public MutableLiveData<List<String>> getPopz() {
        MutableLiveData<List<String>> allpop= new MutableLiveData<>();
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        CollectionReference applicationsRef = firestore.collection("popular");
        DocumentReference applicationIdRef = applicationsRef.document("pop");
        applicationIdRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                List<String> tempList = new ArrayList<String>();
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    tempList = (List<String>) document.get("hotel_id");
                }
                allpop.postValue(tempList);
            }
        });
        return allpop;
    }

    //get all POP
    public MutableLiveData<List<Popular>> getPop() {
        MutableLiveData<List<Popular>> allPop = new MutableLiveData<>();
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        firestore.collection("popular").addSnapshotListener((value, error) -> {
            if (error != null) return;
            if (value != null) {
                List<Popular> tempList = new ArrayList<>();
                for (DocumentSnapshot document : value.getDocuments()) {
                    Popular model = document.toObject(Popular.class);
                    assert model != null;
                    tempList.add(model);
                }
                allPop.postValue(tempList);
                Log.d("POPOP", tempList.toString());
            }
        });
        return allPop;
    }


    //get all hotel popular
    public MutableLiveData<List<Hotel>> getHotelCity() {
        MutableLiveData<List<Hotel>> allHotel = new MutableLiveData<>();
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        List<String> test = new ArrayList<>();
        CollectionReference applicationsRef = firestore.collection("popular");
        DocumentReference applicationIdRef = applicationsRef.document("pop");
        applicationIdRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                List<String> tempList = new ArrayList<String>();
                DocumentSnapshot document = task.getResult();
                tempList = (List<String>) document.get("hotel_id");
                test.addAll(tempList);
                Log.d("Test hotel ID", test.toString());
                firestore.collectionGroup("hotels").addSnapshotListener((value, error) -> {
                    if (error != null) return;
                    if (value != null) {
                        List<Hotel> temp = new ArrayList<>();
                        for (DocumentSnapshot document1 : value.getDocuments()) {
                            if (test.contains(document1.getId())){
                                Hotel model = document1.toObject(Hotel.class);
                                assert model != null;
                                if(model.getStatus().equals("Activate")){
                                    temp.add(model);
                                    Log.d("TEST HOTEL:", model.toString());
                                }
                            }else
                            {
                                Log.d("TEST HOTEL NO:","");
                            }
                        }
                        allHotel.postValue(temp);
                    }
                });
            }

        });

        return allHotel;
    }

    //get all room
    public MutableLiveData<List<Room>> getAllRoom(String hotel_id, long ckin, long ckout) {
        MutableLiveData<List<Room>> allRoom = new MutableLiveData<>();
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        AtomicInteger roombooking = new AtomicInteger();
        Date ckinx =  new Date(ckin);
        Date ckoutx = new Date(ckout);

        //get room
        firestore.collection("room").whereEqualTo("hotel_id", hotel_id).addSnapshotListener((value, error) -> {
            if (error != null) return;
            if (value != null) {
                List<Room> tempList = new ArrayList<>();
                AtomicBoolean check = new AtomicBoolean(false);
                for (DocumentSnapshot document : value.getDocuments()) {
                    Room model = document.toObject(Room.class);
                    assert model != null;
                        model.setRemai(model.getSum());
                        firestore.collectionGroup("order").addSnapshotListener((value1, error1) -> {
                            if (error1 != null) return;
                            if (value1 != null) {
                                for (DocumentSnapshot bk : value1.getDocuments()) {
                                    Booking modelx = bk.toObject(Booking.class);
                                    assert modelx != null;
                                    //set date
                                    Date start = modelx.getCheckin();
                                    Date end = modelx.getCheckout();

                                    if(modelx.getRoom_id().equals(model.getId()) && modelx.getstatus() == true && (ckinx.before(end) || ckinx.equals(end))
                                            && (ckoutx.after(start) || ckoutx.equals(start))){
                                        roombooking.addAndGet(1);
                                    }
                                }
                                Log.d("check", String.valueOf(check.get()));
                                if(roombooking.get() < Integer.parseInt(model.getSum())){
                                    Log.d("IDIDI", model.getId());
                                    int temp = Integer.parseInt(model.getSum()) - roombooking.get();
                                    model.setRemai(String.valueOf(temp));
                                    tempList.add(model);
                                }
                                roombooking.set(0);
                            }

                        });
                    }

                    allRoom.postValue(tempList);
                    }
             });

        return allRoom;
    }

//    public void getData(Room room, model){
//        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
//        List<String> temp = new ArrayList<>();
//
//
//    }


    //todo - get booking

    public MutableLiveData<List<Room>> getroom(String userid) {
        MutableLiveData<List<Room>> allRoom = new MutableLiveData<>();
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        List<String> temp = new ArrayList<>();
//        firestore.collection("booking").document(userid).collection("order").addSnapshotListener((value, error) -> {
//            if (error != null) return;
//            if (value != null) {
//                for (DocumentSnapshot document : value.getDocuments()) {
//                    Booking model = document.toObject(Booking.class);
//                    assert model != null;
//                    temp.add(model.getRoom_id());
//                    Log.d("xxtttromid_test", temp.toString());
//                }
//            }
//        });

        firestore.collection("room").whereEqualTo("id",userid).addSnapshotListener((value, error) -> {
            if (error != null) return;
            if (value != null) {
                List<Room> tempList = new ArrayList<>();
                for (DocumentSnapshot document : value.getDocuments()) {
                    Room model = document.toObject(Room.class);
                    assert model != null;
                    if(model.getId().equals(userid)){
                        tempList.add(model);
                    }
                }
                allRoom.postValue(tempList);
            }
        });

        return allRoom;
    }

    //todo: get Room with Room_id
    public MutableLiveData<List<Booking>> getBooking(String userid) {
        MutableLiveData<List<Booking>> allBooking = new MutableLiveData<>();
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();

        firestore.collection("booking").document(userid).collection("order").addSnapshotListener((value, error) -> {
            if (error != null) return;
            if (value != null) {
                List<Booking> tempList = new ArrayList<>();
                for (DocumentSnapshot document : value.getDocuments()) {
                    Booking model = document.toObject(Booking.class);
                    assert model != null;
                    tempList.add(model);
                }
                allBooking.postValue(tempList);
            }
        });
        return allBooking;
    }

    public MutableLiveData<List<Booking>> getAllBooking() {
        MutableLiveData<List<Booking>> allBooking = new MutableLiveData<>();
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();

        firestore.collectionGroup("order").addSnapshotListener((value, error) -> {
            if (error != null) return;
            if (value != null) {
                List<Booking> tempList = new ArrayList<>();
                for (DocumentSnapshot document : value.getDocuments()) {
                    Booking model = document.toObject(Booking.class);
                    assert model != null;
                    tempList.add(model);
                }
                allBooking.postValue(tempList);
            }
        });
        return allBooking;
    }

    //todo - get promotion
    public MutableLiveData<List<Promotion>> getAllPromotion() {
        MutableLiveData<List<Promotion>> allPromotion = new MutableLiveData<>();
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        firestore.collection("promotions").addSnapshotListener((value, error) -> {
            if (error != null) return;
            if (value != null) {
                List<Promotion> tempList = new ArrayList<>();
                for (DocumentSnapshot document : value.getDocuments()) {
                    Promotion model = document.toObject(Promotion.class);
                    assert model != null;
                    tempList.add(model);
                    //Log.d("name", model.getName());
                }
                allPromotion.postValue(tempList);
            }
        });
        return allPromotion;
    }



}
