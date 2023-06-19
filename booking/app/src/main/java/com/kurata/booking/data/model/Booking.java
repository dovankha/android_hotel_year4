package com.kurata.booking.data.model;

import com.google.firebase.firestore.ServerTimestamp;

import java.io.Serializable;
import java.util.Date;

public class Booking implements Serializable{
        private String id;
        private String room_id;
        private @ServerTimestamp Date checkin;
        private @ServerTimestamp Date checkout;
        private String guestname;
        private String guestemail;
        private String specicalrequest;
        private Boolean status;
        private Boolean paystatus;
        private @ServerTimestamp Date guestbirth;
        private @ServerTimestamp Date timebooking;
        private @ServerTimestamp Date joinTime;
        private @ServerTimestamp Date leftTime;
        private String roomfee;
        private String discount;

        public Booking() {

        }

        public Booking(String id, String room_id, Date checkin, Date checkout, Boolean status, String guestname, String guestemail,String specicalrequest, Date guestbirth, Date timebooking, Date joinTime, Date leftTimne, String roomfee, String discount) {
            this.id = id;
            this.room_id = room_id;
            this.checkin = checkin;
            this.checkout = checkout;
            this.status = status;
            this.guestname = guestname;
            this.guestemail = guestemail;
            this.specicalrequest = specicalrequest;
            this.guestbirth = guestbirth;
            this.timebooking = timebooking;
            this.roomfee = roomfee;
            this.discount = discount;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getRoom_id() {
            return room_id;
        }

        public void setRoom_id(String room_id) {
            this.room_id = room_id;
        }

        public Date getCheckin() {
            return checkin;
        }

        public void setCheckin(Date checkin) {
            this.checkin = checkin;
        }

        public Date getCheckout() {
            return checkout;
        }

        public void setCheckout(Date checkout) {
            this.checkout = checkout;
        }

        public Boolean getstatus() {
            return status;
        }

        public void setStatus(Boolean status) {
            this.status = status;
        }

        public Boolean getPaystatus() {
            return paystatus;
        }

        public void setPaystatus(Boolean paystatus) {
            this.paystatus = paystatus;
        }

        public Date getJoinTime() {
            return joinTime;
        }

        public void setJoinTime(Date joinTime) {
            this.joinTime = joinTime;
        }

        public Date getLeftTime() {
            return leftTime;
        }

        public void setLeftTime(Date leftTime) {
            this.leftTime = leftTime;
        }

        public String getGuestname() {
            return guestname;
        }

        public void setGuestname(String guestname) {
            this.guestname = guestname;
        }

        public String getGuestemail() {
            return guestemail;
        }

        public void setGuestemail(String guestemail) {
            this.guestemail = guestemail;
        }

        public String getSpecicalrequest() {
            return specicalrequest;
        }

        public void setSpecicalrequest(String specicalrequest) {
            this.specicalrequest = specicalrequest;
        }

        public Date getGuestbirth() {
            return guestbirth;
        }

        public void setGuestbirth(Date guestbirth) {
            this.guestbirth = guestbirth;
        }

        public Date getTimebooking() {
            return timebooking;
        }

        public void setTimebooking(Date timebooking) {
            this.timebooking = timebooking;
        }

        public String getRoomfee() {
            return roomfee;
        }

        public void setRoomfee(String roomfee) {
            this.roomfee = roomfee;
        }

        public String getDiscount() {
            return discount;
        }

        public void setDiscount(String discount) {
            this.discount = discount;
        }


}
