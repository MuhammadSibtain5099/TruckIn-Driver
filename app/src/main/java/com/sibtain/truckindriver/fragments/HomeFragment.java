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
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.sibtain.truckindriver.R;
import com.sibtain.truckindriver.directionhelper.FetchURL;
import com.sibtain.truckindriver.directionhelper.TaskLoadedCallback;
import com.sibtain.truckindriver.model.UsersModel;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;


public class HomeFragment extends Fragment implements OnMapReadyCallback, TaskLoadedCallback {
    MapView mapView;
    public static GoogleMap mGoogleMap;
    Marker currentLocationMarker, pickupLocationMarker, dropOffLocationMarker;
    Geocoder mGeocoder;
    List<Address> mAddresses;
    LocationManager locationManager;
    public Double longitude, latitude;
    Double lat=0.0;
    Double longi=0.0;
    String fullAddress;
    private Polyline currentPolyline;
    private LatLng pickupLatLng, dropOffLatLng;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    private FirebaseAuth mAuth;
    DatabaseReference myRef,updateRef;
    DatabaseReference rootRef;
    UsersModel driver;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_home, container, false);
        mapView = v.findViewById(R.id.map);

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
        /*
          configuring places api
         */

        initializePlaces(inflater.getContext().getApplicationContext());
        PlacesClient placesClient = Places.createClient(inflater.getContext());

        final AutocompleteSupportFragment pickupAutocompleteSupportFragment = (AutocompleteSupportFragment) getChildFragmentManager().findFragmentById(R.id.pickupAutoCompleteFragment);
        final AutocompleteSupportFragment dropOffAutocompleteSupportFragment = (AutocompleteSupportFragment) getChildFragmentManager().findFragmentById(R.id.dropOffAutoCompleteFragment);
        assert pickupAutocompleteSupportFragment != null;
        assert dropOffAutocompleteSupportFragment != null;
        pickupAutocompleteSupportFragment.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.LAT_LNG, Place.Field.ADDRESS));
        pickupAutocompleteSupportFragment.setHint("Choose Pickup");
        dropOffAutocompleteSupportFragment.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.LAT_LNG, Place.Field.ADDRESS));
        dropOffAutocompleteSupportFragment.setHint("Choose Drop Off");

        /*
          pickupLocationListener
         */

        pickupAutocompleteSupportFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(@NonNull Place place) {
                LatLng sydney = new LatLng(lat, longi);
                pickupLatLng = place.getLatLng();
                assert pickupLatLng != null;
                if (currentLocationMarker != null) currentLocationMarker.remove();
                pickupAutocompleteSupportFragment.setHint(place.getAddress());
                if(pickupLocationMarker != null) pickupLocationMarker.remove();
                pickupLocationMarker = mGoogleMap.addMarker(new MarkerOptions().position(pickupLatLng).title(place.getAddress()).snippet(place.getAddress()));
                mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(pickupLatLng, dropOffLatLng != null ? 11.0f : 12.0f));
                if (dropOffLocationMarker != null) {
                    String url = getUrl(pickupLocationMarker.getPosition(), dropOffLocationMarker.getPosition());
                    new FetchURL(getActivity()).execute(url, "driving");
                }
            }

            @Override
            public void onError(@NonNull Status status) {
                if (status.getStatusMessage() != null) {
                    Log.e("location", status.getStatusMessage());
                    Toast.makeText(getActivity(), "error" + status, Toast.LENGTH_SHORT).show();
                }
            }
        });

        /*
          dropOffLocationListener
         */

        dropOffAutocompleteSupportFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(@NonNull Place place) {
                dropOffLatLng = place.getLatLng();
                if (currentLocationMarker != null) currentLocationMarker.remove();
                dropOffAutocompleteSupportFragment.setHint(place.getAddress());
                if(dropOffLocationMarker != null) dropOffLocationMarker.remove();
                dropOffLocationMarker = mGoogleMap.addMarker(new MarkerOptions().position(dropOffLatLng).title(place.getAddress()).snippet(place.getAddress()));
                mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(dropOffLatLng, pickupLatLng != null ? 11.0f : 12.0f));

                if (pickupLocationMarker != null) {
                    String url = getUrl(pickupLocationMarker.getPosition(), dropOffLocationMarker.getPosition());
                    new FetchURL(getActivity()).execute(url, "driving");
                }
            }

            @Override
            public void onError(@NonNull Status status) {
                if (status.getStatusMessage() != null) {
                    Log.e("location", status.getStatusMessage());
                    Toast.makeText(getContext(), "error" + status, Toast.LENGTH_SHORT).show();
                }
            }
        });


        return v;
    }

    private void getDriverDetails(){
        myRef = database.getReference().child("Drivers");

        myRef.child(mAuth.getUid()).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()){
                HashMap<String, Object> map = (HashMap<String, Object>) task.getResult().getValue();

                Log.d("testing", map.toString());

                 driver= new UsersModel(map.get("uid").toString(),map.get("fullName").toString(),map.get("email").toString(),"null",map.get("phone").toString(),map.get("truckDetails").toString(),Boolean.parseBoolean(map.get("approved").toString()),Boolean.parseBoolean(map.get("isOnline").toString()));

            }
        });
    }

    private void lisRidesRequest(){
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReference().child("Requests");

        ref.child(mAuth.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                HashMap<String, Object> map = (HashMap<String, Object>) snapshot.getValue();
                Iterator iterator = map.entrySet().iterator();
                while (iterator.hasNext()) {
                    Map.Entry me2 = (Map.Entry) iterator.next();
                    HashMap<String, Object> requestDetails = (HashMap<String, Object>) me2.getValue();
                    Log.d("RequestDetails",map.size()+ requestDetails.toString());
                }






            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    private String getUrl(LatLng origin, LatLng dest) {
        String strOrigin = "origin=" + origin.latitude + "," + origin.longitude;
        String strDest = "destination=" + dest.latitude + "," + dest.longitude;
        String mode = "mode=" + "driving";
        String parameters = strOrigin + "&" + strDest + "&" + mode;
        String output = "json";
        return "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters + "&key=" + getString(R.string.places_api_key);
    }

    private void initializePlaces(Context context) {
        if (!Places.isInitialized()) {
            Places.initialize(context, getString(R.string.places_api_key));
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        mGoogleMap = googleMap;
        // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(lat, longi);
        currentLocationMarker = googleMap.addMarker(new MarkerOptions().position(sydney).title("Your location ").snippet(getAddress(lat, longi)));

        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(sydney, 12.0f));
        // googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney,12.0f));
    }

    public void requestPermission() {
        ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
    }

    private void OnGPS() {

        final AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

        builder.setMessage("Enable GPS").setCancelable(false).setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                startActivityForResult(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS),100);
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
            Toast.makeText(getContext(), fullAddress, Toast.LENGTH_LONG).show();
//            behaviour.setText("Behavior Recorded at" + fullAddress);
            return address + " " + area + " " + city;


        } catch (Exception e) {


        }
        return null;

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
                UpdateLocationInFirebase(lat,longi);
                //t1.setText("Your Location:"+"\n"+"Latitude= "+latitude+"\n"+"Longitude= "+longitude);
            } else if (LocationNetwork != null) {
                lat = LocationNetwork.getLatitude();
                longi = LocationNetwork.getLongitude();
                getAddress(lat, longi);
                latitude = lat;
                longitude = longi;
                UpdateLocationInFirebase(lat,longi);
                //t1.setText("Your Location:"+"\n"+"Latitude= "+latitude+"\n"+"Longitude= "+longitude);
            } else if (LocationPassive != null) {
                lat = LocationPassive.getLatitude();
                longi = LocationPassive.getLongitude();
                getAddress(lat, longi);
                latitude = lat;
                longitude = longi;
                UpdateLocationInFirebase(lat,longi);

                //t1.setText("Your Location:"+"\n"+"Latitude= "+latitude+"\n"+"Longitude= "+longitude);
            } else if (location != null) {
                lat = location.getLatitude();
                longi = location.getLongitude();
                getAddress(lat, longi);
                latitude = lat;
                longitude = longi;
                UpdateLocationInFirebase(lat,longi);
            } else {
                Toast.makeText(getContext(), "Can't Get Your Location", Toast.LENGTH_SHORT).show();
            }
            mapView.getMapAsync(this);
        }
    }

    private Boolean UpdateLocationInFirebase(Double lat,Double longi){
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

    @Override
    public void onTaskDone(Object... values) {
        if (currentPolyline != null) {
            currentPolyline.remove();
        }
        currentPolyline = mGoogleMap.addPolyline((PolylineOptions) values[0]);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==100){
            if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                OnGPS();
            } else {
                getLocation();
            }
        }
    }



}