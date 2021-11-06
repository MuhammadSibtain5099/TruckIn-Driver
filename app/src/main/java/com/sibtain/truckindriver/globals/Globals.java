package com.sibtain.truckindriver.globals;

import com.google.android.gms.maps.model.LatLng;


public class Globals {
    public static final String PICKUP_LOCATION_REQUEST = "Pickup";
    public static final String DROP_OFF_LOCATION_REQUEST = "DropOff";

    public static LatLng currentLocationLatLng;
    public static LatLng pickupLatLng;
    public static LatLng dropOffLatLng;

    public static String selectedLocationRequest = "";
}
