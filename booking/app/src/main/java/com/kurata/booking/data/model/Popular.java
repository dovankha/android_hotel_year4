package com.kurata.booking.data.model;

import java.util.ArrayList;

public class Popular {
    private String name;
    private ArrayList<String> hotel_id;
    private ArrayList<String> room_id;

    public Popular() {
    }

    public Popular(String name, ArrayList<String> hotel_id, ArrayList<String> room_id) {
        this.name = name;
        this.hotel_id = hotel_id;
        this.room_id = room_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ArrayList<String> getHotel_id() {
        return hotel_id;
    }

    public void setHotel_id(ArrayList<String> hotel_id) {
        this.hotel_id = hotel_id;
    }

    public ArrayList<String> getRoom_id() {
        return room_id;
    }

    public void setRoom_id(ArrayList<String> room_id) {
        this.room_id = room_id;
    }

}
