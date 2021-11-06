package com.sibtain.truckindriver.model;

public class RideRequest {
    String DriverCurrentLat,DriverCurrentLng,DriverReached,RiderDropOffLat,RiderDropOffLng,
    RiderPickupLat,RiderPickupLng,driverEmail,driverName,driverPhone,driverTruckDetails,status,uid;

    public RideRequest() {
    }

    public RideRequest(String driverCurrentLat, String driverCurrentLng, String driverReached, String riderDropOffLat, String riderDropOffLng, String riderPickupLat, String riderPickupLng, String driverEmail, String driverName, String driverPhone, String driverTruckDetails, String status) {
        DriverCurrentLat = driverCurrentLat;
        DriverCurrentLng = driverCurrentLng;
        DriverReached = driverReached;
        RiderDropOffLat = riderDropOffLat;
        RiderDropOffLng = riderDropOffLng;
        RiderPickupLat = riderPickupLat;
        RiderPickupLng = riderPickupLng;
        this.driverEmail = driverEmail;
        this.driverName = driverName;
        this.driverPhone = driverPhone;
        this.driverTruckDetails = driverTruckDetails;
        this.status = status;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getDriverCurrentLat() {
        return DriverCurrentLat;
    }

    public void setDriverCurrentLat(String driverCurrentLat) {
        DriverCurrentLat = driverCurrentLat;
    }

    public String getDriverCurrentLng() {
        return DriverCurrentLng;
    }

    public void setDriverCurrentLng(String driverCurrentLng) {
        DriverCurrentLng = driverCurrentLng;
    }

    public String getDriverReached() {
        return DriverReached;
    }

    public void setDriverReached(String driverReached) {
        DriverReached = driverReached;
    }

    public String getRiderDropOffLat() {
        return RiderDropOffLat;
    }

    public void setRiderDropOffLat(String riderDropOffLat) {
        RiderDropOffLat = riderDropOffLat;
    }

    public String getRiderDropOffLng() {
        return RiderDropOffLng;
    }

    public void setRiderDropOffLng(String riderDropOffLng) {
        RiderDropOffLng = riderDropOffLng;
    }

    public String getRiderPickupLat() {
        return RiderPickupLat;
    }

    public void setRiderPickupLat(String riderPickupLat) {
        RiderPickupLat = riderPickupLat;
    }

    public String getRiderPickupLng() {
        return RiderPickupLng;
    }

    public void setRiderPickupLng(String riderPickupLng) {
        RiderPickupLng = riderPickupLng;
    }

    public String getDriverEmail() {
        return driverEmail;
    }

    public void setDriverEmail(String driverEmail) {
        this.driverEmail = driverEmail;
    }

    public String getDriverName() {
        return driverName;
    }

    public void setDriverName(String driverName) {
        this.driverName = driverName;
    }

    public String getDriverPhone() {
        return driverPhone;
    }

    public void setDriverPhone(String driverPhone) {
        this.driverPhone = driverPhone;
    }

    public String getDriverTruckDetails() {
        return driverTruckDetails;
    }

    public void setDriverTruckDetails(String driverTruckDetails) {
        this.driverTruckDetails = driverTruckDetails;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

}
