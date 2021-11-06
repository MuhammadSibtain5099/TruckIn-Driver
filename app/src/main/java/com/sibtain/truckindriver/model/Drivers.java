package com.sibtain.truckindriver.model;

public class Drivers {
    String email, fullName, password, phone, truckDetails, uid;
    Boolean approved, isOnline;
    Double currentLat, currentLng;

    public Drivers(){

    }

    public Drivers(String email, String fullName, String password, String phone, String truckDetails, String uid, Boolean approved, Boolean isOnline, Double currentLat, Double currentLng) {
        this.email = email;
        this.fullName = fullName;
        this.password = password;
        this.phone = phone;
        this.truckDetails = truckDetails;
        this.uid = uid;
        this.approved = approved;
        this.isOnline = isOnline;
        this.currentLat = currentLat;
        this.currentLng = currentLng;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getTruckDetails() {
        return truckDetails;
    }

    public void setTruckDetails(String truckDetails) {
        this.truckDetails = truckDetails;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public Boolean getApproved() {
        return approved;
    }

    public void setApproved(Boolean approved) {
        this.approved = approved;
    }

    public Boolean getOnline() {
        return isOnline;
    }

    public void setOnline(Boolean online) {
        isOnline = online;
    }

    public Double getCurrentLat() {
        return currentLat;
    }

    public void setCurrentLat(Double currentLat) {
        this.currentLat = currentLat;
    }

    public Double getCurrentLng() {
        return currentLng;
    }

    public void setCurrentLng(Double currentLng) {
        this.currentLng = currentLng;
    }
}
