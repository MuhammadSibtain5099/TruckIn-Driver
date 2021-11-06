package com.sibtain.truckindriver.model;

public class UsersModel {
    String uid, fullName, email, password, phone, truckDetails;
    Boolean isApproved = false;
    Boolean isOnline = true,isAvailable=true;
    double currentLat = 0.0, currentLng = 0.0;

    public UsersModel() {
    }

    public double getCurrentLat() {
        return currentLat;
    }

    public UsersModel(String uid, String fullName, String email, String password, String phone, String truckDetails, Boolean isApproved, Boolean isOnline, double currentLat, double currentLng) {
        this.uid = uid;
        this.fullName = fullName;
        this.email = email;
        this.password = password;
        this.phone = phone;
        this.truckDetails = truckDetails;
        this.isApproved = isApproved;
        this.isOnline = isOnline;
        this.currentLat = currentLat;
        this.currentLng = currentLng;
    }

    public void setCurrentLat(double currentLat) {
        this.currentLat = currentLat;
    }

    public double getCurrentLng() {
        return currentLng;
    }

    public void setCurrentLng(double currentLng) {
        this.currentLng = currentLng;
    }

    public UsersModel(String uid, String fullName, String email, String password, String phone, String truckDetails, Boolean isApproved) {
        this.uid = uid;
        this.fullName = fullName;
        this.email = email;
        this.password = password;
        this.phone = phone;
        this.truckDetails = truckDetails;
        this.isApproved = isApproved;
    }

    public UsersModel(String uid, String fullName, String email, String password, String phone, String truckDetails, Boolean isApproved, Boolean isOnline) {
        this.uid = uid;
        this.fullName = fullName;
        this.email = email;
        this.password = password;
        this.phone = phone;
        this.truckDetails = truckDetails;
        this.isApproved = isApproved;
        this.isOnline = isOnline;
    }


    public String getIsOnlineStatus(){
        return (isOnline)? "Online":"Offline";
    }
    public String getIsApprovedStatus(){
        return (isApproved)? "Approved":"Not Approved";
    }

    public Boolean getAvailable() {
        return isAvailable;
    }

    public Boolean getIsOnline(){
        return isOnline;
    }

    public void setAvailable(Boolean available) {
        isAvailable = available;
    }

    public void setOnline(Boolean online) {
        isOnline = online;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
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

    public Boolean getApproved() {
        return isApproved;
    }

    public void setApproved(Boolean approved) {
        isApproved = approved;
    }


}
