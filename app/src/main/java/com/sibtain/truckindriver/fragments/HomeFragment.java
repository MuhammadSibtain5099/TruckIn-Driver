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
import android.net.Uri;
import android.os.Bundle;

import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.sibtain.truckindriver.R;

import java.util.List;
import java.util.Locale;


public class HomeFragment extends Fragment implements OnMapReadyCallback {
    MapView mapView;
    String checking;
    Geocoder mGeocoder;
    List<Address> mAddresses;
    LocationManager locationManager;
    public Double longitude, latitude;
    double lat;
    double longi;
    String fullAddress;
    Uri uri2;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v= inflater.inflate(R.layout.fragment_home, container, false);
         mapView = v.findViewById(R.id.map);
        mapView.onCreate(savedInstanceState);
        mapView.onResume();
        locationManager = (LocationManager) getContext().getSystemService(Context.LOCATION_SERVICE);
        // check gps unable or not
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            OnGPS();
        } else {

            getLocation();
        }



    return v;
    }

    // Obtain the SupportMapFragment and get notified when the map is ready to be used.




    @Override
    public void onMapReady(GoogleMap googleMap) {

        // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(lat, longi);
        googleMap.addMarker(new MarkerOptions().position(sydney).title("Your location ").snippet(getAddress(lat,longi)));

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
                startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
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
Toast.makeText(getContext(),fullAddress,Toast.LENGTH_LONG).show();
//            behaviour.setText("Behavior Recorded at" + fullAddress);
            return address+" "+area+" "+city ;


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
            Location location =getLastKnownLocation();
            if (LocationGps != null) {
                lat = LocationGps.getLatitude();
                longi = LocationGps.getLongitude();
                getAddress(lat, longi);
                latitude = lat;
                longitude = longi;

                //t1.setText("Your Location:"+"\n"+"Latitude= "+latitude+"\n"+"Longitude= "+longitude);
            } else if (LocationNetwork != null) {
                lat = LocationNetwork.getLatitude();
                longi = LocationNetwork.getLongitude();
                getAddress(lat, longi);
                latitude = lat;
                longitude = longi;

                //t1.setText("Your Location:"+"\n"+"Latitude= "+latitude+"\n"+"Longitude= "+longitude);
            } else if (LocationPassive != null) {
                lat = LocationPassive.getLatitude();
                longi = LocationPassive.getLongitude();
                getAddress(lat, longi);
                latitude = lat;
                longitude = longi;


                //t1.setText("Your Location:"+"\n"+"Latitude= "+latitude+"\n"+"Longitude= "+longitude);
            }
            else if(location!=null){
                lat = location.getLatitude();
                longi = location.getLongitude();
                getAddress(lat, longi);
                latitude = lat;
                longitude = longi;
            }
            else {
                Toast.makeText(getContext(), "Can't Get Your Location", Toast.LENGTH_SHORT).show();
            }
            mapView.getMapAsync(this);


        }


    }
    private Location getLastKnownLocation() {
        locationManager = (LocationManager) getContext().getSystemService(LOCATION_SERVICE);
        List<String> providers = locationManager.getProviders(true);
        Location bestLocation = null;
        for (String provider : providers) {
            if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
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


}