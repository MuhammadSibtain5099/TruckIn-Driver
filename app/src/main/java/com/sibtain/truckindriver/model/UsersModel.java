package com.sibtain.truckindriver.model;

public class UsersModel {
    String uid,fullName,email,password,phone,truckDetails;
    Boolean isApproved=false;

    public UsersModel() {
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
