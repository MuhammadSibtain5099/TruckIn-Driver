package com.sibtain.truckindriver.fragments;

import static android.content.Context.LOCATION_SERVICE;
import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.sibtain.truckindriver.R;
import com.sibtain.truckindriver.ShowRequestListActivity;
import com.sibtain.truckindriver.directionhelper.TaskLoadedCallback;
import com.sibtain.truckindriver.model.UsersModel;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;


public class FirstFragment extends Fragment implements OnMapReadyCallback, TaskLoadedCallback {
    MapView mapView;
    public static GoogleMap mGoogleMap;
    Marker currentLocationMarker, pickupLocationMarker, dropOffLocationMarker;
    Geocoder mGeocoder;
    List<Address> mAddresses;
    LocationManager locationManager;
    public Double longitude, latitude;
    Double lat = 0.0;
    Double longi = 0.0;
    String fullAddress;
    private Polyline currentPolyline;
    private LatLng pickupLatLng, dropOffLatLng;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    private FirebaseAuth mAuth;
    DatabaseReference myRef, updateRef;
    DatabaseReference rootRef;
    UsersModel driver;
    TextView txtTotalNumberOfRequests;
    ViewGroup viewGroup ;
    AlertDialog alertDialog;
    Button btnShowRequests;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_first, container, false);
        mapView = v.findViewById(R.id.map);
        //txtTotalNumberOfRequests = v.findViewById(R.id.txtTotalNumberOfRequests);
        loadingScreenDialog(v);

        mAuth = FirebaseAuth.getInstance();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        myRef = database.getReference().child("Requests");
        rootRef = database.getReference();
        mapView.onCreate(savedInstanceState);
        mapView.onResume();
        /*
          location manager
         */
        locationManager = (LocationManager) getContext().getSystemService(Context.LOCATION_SERVICE);
        // check gps unable or not
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            OnGPS();
        } else {
            getLocation();
        }
        getDriverDetails();
        lisRidesRequest();

        return v;
    }

    private void getDriverDetails() {
        myRef = database.getReference().child("Drivers");

        myRef.child(mAuth.getUid()).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                HashMap<String, Object> map = (HashMap<String, Object>) task.getResult().getValue();

                Log.d("testing", map.toString());

                driver = new UsersModel(map.get("uid").toString(), map.get("fullName").toString(), map.get("email").toString(), "null", map.get("phone").toString(), map.get("truckDetails").toString(), Boolean.parseBoolean(map.get("approved").toString()), Boolean.parseBoolean(map.get("isOnline").toString()));

            }
        });
    }

    private void lisRidesRequest() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReference().child("Requests");

        ref.child(mAuth.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                HashMap<String, Object> map = (HashMap<String, Object>) snapshot.getValue();
                if(snapshot.hasChildren()) {
                    Iterator iterator = map.entrySet().iterator();
                    int count = 0;
                    while (iterator.hasNext()) {
                        Map.Entry me2 = (Map.Entry) iterator.next();
                        HashMap<String, Object> requestDetails = (HashMap<String, Object>) me2.getValue();
                        Log.d("RequestDetails", map.size() + requestDetails.toString());
                        if (requestDetails.get("status").toString().equals("pending")) {
                            count++;
                        }

                    }


                    if (count > 0) {
                        alertDialog.show();
                    }

                    txtTotalNumberOfRequests.setText(getString(R.string.waiting_user) + count);
                }
               /* HashMap<String, Object> requestHashmap = new HashMap<>();
                requestHashmap.put(Constants.RIDE_PICKUP_LAT, map.get("RidePickupLat"));
                requestHashmap.put(Constants.RIDE_PICKUP_LNG, map.get("RidePickupLng"));
                requestHashmap.put(Constants.RIDE_DROP_OFF_LAT, map.get("RideDropOffLat"));
                requestHashmap.put(Constants.RIDE_DROP_OFF_LNG, map.get("RideDropOffLng"));
                requestHashmap.put(Constants.DRIVER_CURRENT_LAT, map.get("DriverCurrentLat"));
                requestHashmap.put(Constants.DRIVER_CURRENT_LNG, map.get("DriverReached"));
                requestHashmap.put(Constants.DRIVER_REACHED, false);
                requestHashmap.put(Constants.RIDE_STATUS, "accepted");
                requestHashmap.put(Constants.DRIVER_FULL_NAME, driver.getFullName());
                requestHashmap.put(Constants.DRIVER_EMAIL, driver.getEmail());
                requestHashmap.put(Constants.DRIVER_PHONE, driver.getPhone());
                requestHashmap.put(Constants.DRIVER_TRUCK_DETAILS, driver.getTruckDetails());
                rootRef.child("ActiveRides").child(driver.getUid()).child(Objects.requireNonNull(mAuth.getUid())).setValue(requestHashmap).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                          //  getDetailsAndDrawRoute(driver);
                        }else{
                            Toast.makeText(getContext(), "Error", Toast.LENGTH_SHORT).show();
                        }
                    }
                });*/


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void getLocation() {

        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(),

                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]
                    {Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        } else {

            Location LocationGps = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            Location LocationNetwork = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            Location LocationPassive = locationManager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER);
            Location location = getLastKnownLocation();
            if (LocationGps != null) {
                lat = LocationGps.getLatitude();
                longi = LocationGps.getLongitude();
                getAddress(lat, longi);
                latitude = lat;
                longitude = longi;
                UpdateLocationInFirebase(lat, longi);
                //t1.setText("Your Location:"+"\n"+"Latitude= "+latitude+"\n"+"Longitude= "+longitude);
            } else if (LocationNetwork != null) {
                lat = LocationNetwork.getLatitude();
                longi = LocationNetwork.getLongitude();
                getAddress(lat, longi);
                latitude = lat;
                longitude = longi;
                UpdateLocationInFirebase(lat, longi);
                //t1.setText("Your Location:"+"\n"+"Latitude= "+latitude+"\n"+"Longitude= "+longitude);
            } else if (LocationPassive != null) {
                lat = LocationPassive.getLatitude();
                longi = LocationPassive.getLongitude();
                getAddress(lat, longi);
                latitude = lat;
                longitude = longi;
                UpdateLocationInFirebase(lat, longi);

                //t1.setText("Your Location:"+"\n"+"Latitude= "+latitude+"\n"+"Longitude= "+longitude);
            } else if (location != null) {
                lat = location.getLatitude();
                longi = location.getLongitude();
                getAddress(lat, longi);
                latitude = lat;
                longitude = longi;
                UpdateLocationInFirebase(lat, longi);
            } else {
                Toast.makeText(getContext(), "Can't Get Your Location", Toast.LENGTH_SHORT).show();
            }
            mapView.getMapAsync(this);
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        mGoogleMap = googleMap;
        // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(lat, longi);
        currentLocationMarker = googleMap.addMarker(new MarkerOptions().position(sydney).title("Your location ").snippet(getAddress(lat, longi)).icon(BitmapDescriptorFactory.fromResource(R.drawable.truck_icon)));

        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(sydney, 12.0f));
        // googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney,12.0f));
    }

    @Override
    public void onTaskDone(Object... values) {

    }

    private void OnGPS() {

        final AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

        builder.setMessage("Enable GPS").setCancelable(false).setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                startActivityForResult(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS), 100);
            }
        }).setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                dialog.cancel();
            }
        });
        final AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    public String getAddress(Double lat1, Double longi1) {
        try {
            mGeocoder = new Geocoder(getContext(), Locale.getDefault());
            mAddresses = mGeocoder.getFromLocation(lat1, longi1, 1);

            String address = mAddresses.get(0).getAddressLine(0);
            String area = mAddresses.get(0).getLocality();
            String city = mAddresses.get(0).getAdminArea();
            String country = mAddresses.get(0).getCountryName();
            //String postal =mAddresses.get(0).getPostalCode();


            fullAddress = address + "," + area + ", " + city + ", " + country;
        //    Toast.makeText(getContext(), fullAddress, Toast.LENGTH_LONG).show();
//            behaviour.setText("Behavior Recorded at" + fullAddress);
            return area ;


        } catch (Exception e) {


        }
        return null;

    }

    private Boolean UpdateLocationInFirebase(Double lat, Double longi) {
        updateRef = database.getReference().child("Drivers");
        updateRef.child(Objects.requireNonNull(mAuth.getUid())).child("currentLat").setValue(lat).addOnSuccessListener(aVoid -> {
            updateRef.child(Objects.requireNonNull(mAuth.getUid())).child("currentLng").setValue(longi).addOnSuccessListener(aaVoid -> {
                // Write was successful!

            });
        }).addOnFailureListener(e -> {

            Toast.makeText(getContext(), "Some Thing Went Wrong", Toast.LENGTH_LONG).show();

        });


        return true;

    }

    private Location getLastKnownLocation() {
        locationManager = (LocationManager) getContext().getSystemService(LOCATION_SERVICE);
        List<String> providers = locationManager.getProviders(true);
        Location bestLocation = null;
        for (String provider : providers) {
            if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                return null;
            }
            Location l = locationManager.getLastKnownLocation(provider);

            if (l == null) {
                continue;
            }
            if (bestLocation == null || l.getAccuracy() < bestLocation.getAccuracy()) {
                // Found best last known location: %s", l);
                bestLocation = l;
            }
        }
        return bestLocation;
    }

    private void loadingScreenDialog(View v){

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        ViewGroup viewGroup = v.findViewById(android.R.id.content);
        View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.custom_dialog_show_requests, viewGroup, false);
        builder.setView(dialogView);
        alertDialog = builder.create();
        alertDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        btnShowRequests = dialogView.findViewById(R.id.btnShowRequests);
        txtTotalNumberOfRequests =  dialogView.findViewById(R.id.txtTotalNumberOfRequests);
        btnShowRequests.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getContext(), ShowRequestListActivity.class));

            }
        });
    }

    @Override
    public void onPause() {
        super.onPause();

            alertDialog.dismiss();

        }

}