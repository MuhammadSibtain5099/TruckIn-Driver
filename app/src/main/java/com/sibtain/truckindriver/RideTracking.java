package com.sibtain.truckindriver;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import com.airbnb.lottie.LottieAnimationView;
import com.directions.route.AbstractRouting;
import com.directions.route.Route;
import com.directions.route.RouteException;
import com.directions.route.Routing;
import com.directions.route.RoutingListener;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.maps.android.SphericalUtil;
import com.sibtain.truckindriver.adapter.ShowRequestsAdapter;
import com.sibtain.truckindriver.globals.Constants;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class RideTracking extends FragmentActivity implements OnMapReadyCallback,
        GoogleApiClient.OnConnectionFailedListener, RoutingListener {
    public static String uid;
    DatabaseReference myRef;
    TextView txtPhone, txtName;
    DatabaseReference rootRef, truckRequestRef;
    private FirebaseAuth mAuth;
    DatabaseReference updateRef;

    Button btnAction, btnFinishRide;
    LottieAnimationView lottieAnimationView;
    LatLng pickupLatLng, dropOffLatLng, driverCurrentLatLng;
    ValueEventListener listener;
    //google map object
    private GoogleMap mMap;
    //current and destination location objects
    Location myLocation = null;
    protected LatLng start = null;
    protected LatLng end = null;

    //to get location permissions.
    private final static int LOCATION_REQUEST_CODE = 23;
    boolean locationPermission = false;
    Boolean checkIfDriverIsOnTheWay = false;
    int countLocation = 0;

    //polyline object
    private List<Polyline> polyline = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ride_tracking);
        mAuth = FirebaseAuth.getInstance();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        myRef = database.getReference().child("Users");
        rootRef = database.getReference();
        txtName = findViewById(R.id.txtName);
        txtPhone = findViewById(R.id.txtPhone);
        btnFinishRide = findViewById(R.id.btnFinishRide);
        Intent intent = getIntent();
        uid = intent.getStringExtra("uid");
        Toast.makeText(getApplicationContext(), uid, Toast.LENGTH_SHORT).show();
        getUserDetailsByUid(uid);
        //request location permission.
        requestPermision();
        getDetailsAndDrawRoute();
        btnAction = findViewById(R.id.btnAction);
        lottieAnimationView = findViewById(R.id.loading_location_ripple);
        //init google map fragment to show map.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        btnAction.setOnClickListener(view -> updateStatus());
        btnFinishRide.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finishRide();
            }
        });

    }

    private void getUserDetailsByUid(String uid) {
        myRef.child(uid).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                HashMap<String, Object> map = (HashMap<String, Object>) task.getResult().getValue();

                assert map != null;
                txtName.setText(Objects.requireNonNull(map.get("fullName")).toString());
                txtPhone.setText(Objects.requireNonNull(map.get("phone")).toString());

            }
        });

    }

    private void requestPermision() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                    LOCATION_REQUEST_CODE);
        } else {
            locationPermission = true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case LOCATION_REQUEST_CODE: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //if permission granted.
                    locationPermission = true;
                    getMyLocation();

                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }
        }
    }

    //to get user location
    private void getMyLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            return;
        }
        mMap.setMyLocationEnabled(true);
        mMap.setOnMyLocationChangeListener(location -> {
            myLocation = location;

            if (countLocation == 0) {
                btnAction.setEnabled(true);
                lottieAnimationView.setVisibility(View.INVISIBLE);
                mMap.clear();
                UpdateLocationInFirebase(myLocation.getLatitude(), myLocation.getLongitude());
            }
            countLocation++;
            if (countLocation % 10 == 0) {
                if (checkIfDriverIsOnTheWay) {

                    LatLng ltlng = new LatLng(location.getLatitude(), location.getLongitude());
                    CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(
                            ltlng, 16f);
                    mMap.animateCamera(cameraUpdate);

                    LatLng star = new LatLng(myLocation.getLatitude(), myLocation.getLongitude());
                    mMap.clear();
                    Findroutes(star, pickupLatLng);
                    UpdateLocationInFirebase(myLocation.getLatitude(), myLocation.getLongitude());
                }
            }
        });

        //get destination location when user click on map
        mMap.setOnMapClickListener(latLng -> {

            end = latLng;

            //    mMap.clear();

            start = new LatLng(myLocation.getLatitude(), myLocation.getLongitude());
            //start route finding
            //   Findroutes(start,end);
        });

    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        if (locationPermission) {
            getMyLocation();
        }

    }


    // function to find Routes.
    public void Findroutes(LatLng Start, LatLng End) {
        if (Start == null || End == null) {
            Toast.makeText(RideTracking.this, "Unable to get location", Toast.LENGTH_LONG).show();
        } else {

            Routing routing = new Routing.Builder()
                    .travelMode(AbstractRouting.TravelMode.DRIVING)
                    .withListener(this)
                    .alternativeRoutes(true)
                    .waypoints(Start, End)
                    .key("AIzaSyBGhn-udvXYCOeKuLkJTWWYy0L-2B7LwkE")  //also define your api key here.
                    .build();
            routing.execute();
        }
    }

    //Routing call back functions.
    @Override
    public void onRoutingFailure(RouteException e) {
        View parentLayout = findViewById(android.R.id.content);
        Snackbar snackbar = Snackbar.make(parentLayout, e.toString(), Snackbar.LENGTH_LONG);
        snackbar.show();
//        Findroutes(start,end);
    }

    @Override
    public void onRoutingStart() {
        Toast.makeText(RideTracking.this, "Finding Route...", Toast.LENGTH_LONG).show();
    }

    //If Route finding success..
    @Override
    public void onRoutingSuccess(ArrayList<Route> route, int shortestRouteIndex) {

        CameraUpdate center = CameraUpdateFactory.newLatLng(start);
        CameraUpdate zoom = CameraUpdateFactory.zoomTo(16);
        if (polyline != null) {
            polyline.clear();
        }
        PolylineOptions polyOptions = new PolylineOptions();
        LatLng polylineStartLatLng = null;
        LatLng polylineEndLatLng = null;


        polyline = new ArrayList<>();
        //add route(s) to the map using polyline
        for (int i = 0; i < route.size(); i++) {

            if (i == shortestRouteIndex) {
                polyOptions.color(getResources().getColor(R.color.colorPrimary));
                polyOptions.width(7);
                polyOptions.addAll(route.get(shortestRouteIndex).getPoints());
                Polyline polyline = mMap.addPolyline(polyOptions);
                polylineStartLatLng = polyline.getPoints().get(0);
                int k = polyline.getPoints().size();
                polylineEndLatLng = polyline.getPoints().get(k - 1);
                this.polyline.add(polyline);

            } else {

            }

        }

        //Add Marker on route starting position
        MarkerOptions startMarker = new MarkerOptions();
        startMarker.position(polylineStartLatLng);
        startMarker.title("My Location");
        startMarker.icon(BitmapDescriptorFactory.fromResource(R.drawable.truck_icon));
        mMap.addMarker(startMarker);

        //Add Marker on route ending position
        MarkerOptions endMarker = new MarkerOptions();
        endMarker.position(polylineEndLatLng);
        endMarker.title("Destination");
        mMap.addMarker(endMarker);
    }

    @Override
    public void onRoutingCancelled() {
        Findroutes(start, end);
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Findroutes(start, end);

    }

    private void getDetailsAndDrawRoute() {
        Log.e("userid", mAuth.getUid() + ShowRequestsAdapter.userId);

        truckRequestRef = rootRef.child(Constants.REQUEST_RIDE_NODE).child(mAuth.getUid()).child(ShowRequestsAdapter.userId);
        listener = truckRequestRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.hasChildren()) {
                    HashMap<String, Object> map = (HashMap<String, Object>) snapshot.getValue();

                    if (Objects.requireNonNull(Objects.requireNonNull(map).get(Constants.RIDE_STATUS)).toString().equals(Constants.Pending_RIDE_STATUS)) {
                        // Toast.makeText(getApplicationContext(), "Driver is on his way", Toast.LENGTH_SHORT).show();

                        pickupLatLng = new LatLng(Double.parseDouble(Objects.requireNonNull(map.get(Constants.RIDE_PICKUP_LAT)).toString()), Double.parseDouble(Objects.requireNonNull(map.get(Constants.RIDE_PICKUP_LNG)).toString()));
                        dropOffLatLng = new LatLng(Double.parseDouble(Objects.requireNonNull(map.get(Constants.RIDE_DROP_OFF_LAT)).toString()), Double.parseDouble(Objects.requireNonNull(map.get(Constants.RIDE_DROP_OFF_LNG)).toString()));
                        driverCurrentLatLng = new LatLng(Double.parseDouble(Objects.requireNonNull(map.get(Constants.DRIVER_CURRENT_LAT)).toString()), Double.parseDouble(Objects.requireNonNull(map.get(Constants.DRIVER_CURRENT_LNG)).toString()));
                        Log.d("test", pickupLatLng.latitude + " " + pickupLatLng.longitude + " " + dropOffLatLng.toString());
                        mMap.clear();
                        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(driverCurrentLatLng, 12.0f));
                        mMap.addMarker(new MarkerOptions().position(driverCurrentLatLng).title("Driver").snippet("xyz").icon(BitmapDescriptorFactory.fromResource(R.drawable.truck_icon)));
                        Findroutes(driverCurrentLatLng, pickupLatLng);
                        //  new FetchURL(getActivity()).execute(url, "driving");
                        mMap.addMarker(new MarkerOptions().position(pickupLatLng).title("Your Pickup"));
                        double distance = SphericalUtil.computeDistanceBetween(driverCurrentLatLng, pickupLatLng);

                    } else if (Objects.requireNonNull(Objects.requireNonNull(map).get(Constants.RIDE_STATUS)).toString().equals(Constants.ACCEPTED_RIDE_STATUS)) {
                        Toast.makeText(getApplicationContext(), "Go to pickup point", Toast.LENGTH_SHORT).show();

                        pickupLatLng = new LatLng(Double.parseDouble(Objects.requireNonNull(map.get(Constants.RIDE_PICKUP_LAT)).toString()), Double.parseDouble(Objects.requireNonNull(map.get(Constants.RIDE_PICKUP_LNG)).toString()));
                        dropOffLatLng = new LatLng(Double.parseDouble(Objects.requireNonNull(map.get(Constants.RIDE_DROP_OFF_LAT)).toString()), Double.parseDouble(Objects.requireNonNull(map.get(Constants.RIDE_DROP_OFF_LNG)).toString()));
                        driverCurrentLatLng = new LatLng(Double.parseDouble(Objects.requireNonNull(map.get(Constants.DRIVER_CURRENT_LAT)).toString()), Double.parseDouble(Objects.requireNonNull(map.get(Constants.DRIVER_CURRENT_LNG)).toString()));
                        Log.d("test", pickupLatLng.latitude + " " + pickupLatLng.longitude + " " + dropOffLatLng.toString());
                        mMap.clear();
                        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(driverCurrentLatLng, 12.0f));
                        mMap.addMarker(new MarkerOptions().position(driverCurrentLatLng).title("Driver").snippet("xyz").icon(BitmapDescriptorFactory.fromResource(R.drawable.truck_icon)));
                        Findroutes(driverCurrentLatLng, pickupLatLng);
                        //  new FetchURL(getActivity()).execute(url, "driving");
                        mMap.addMarker(new MarkerOptions().position(pickupLatLng).title("Your Pickup"));
                        double distance = SphericalUtil.computeDistanceBetween(driverCurrentLatLng, pickupLatLng);
                        if (distance <= 500) {
                            HashMap<String, Object> mapForUpdate = new HashMap<>();
                            mapForUpdate.put(Constants.RIDE_STATUS, Constants.REACHED_AT_PICKUP);
                            truckRequestRef.updateChildren(mapForUpdate);
                            //  truckCompleteDetailsLayout.setVisibility(View.GONE);
                            //    truckArrivedLayout.setVisibility(View.VISIBLE);
                            //truckRequestRef.removeEventListener(listener);
                            btnFinishRide.setVisibility(View.VISIBLE);
                            Toast.makeText(getApplicationContext(), "Your Truck has been arrived to your location", Toast.LENGTH_SHORT).show();

                        }
                    } else if (Objects.requireNonNull(Objects.requireNonNull(map).get(Constants.RIDE_STATUS)).toString().equals(Constants.REACHED_AT_PICKUP)) {
                        //  truckCompleteDetailsLayout.setVisibility(View.GONE);
                        //  truckStatusTextView.setText("Your Truck is on its way");
                        //  truckArrivedLayout.setVisibility(View.VISIBLE);
                        mMap.clear();
                        dropOffLatLng = new LatLng(Double.parseDouble(Objects.requireNonNull(map.get(Constants.RIDE_DROP_OFF_LAT)).toString()), Double.parseDouble(Objects.requireNonNull(map.get(Constants.RIDE_DROP_OFF_LNG)).toString()));
                        driverCurrentLatLng = new LatLng(Double.parseDouble(Objects.requireNonNull(map.get(Constants.DRIVER_CURRENT_LAT)).toString()), Double.parseDouble(Objects.requireNonNull(map.get(Constants.DRIVER_CURRENT_LNG)).toString()));
                        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(driverCurrentLatLng, 12.0f));
                        mMap.addMarker(new MarkerOptions().position(driverCurrentLatLng).title("Driver").snippet("xyz").icon(BitmapDescriptorFactory.fromResource(R.drawable.truck_icon)));
                        Findroutes(driverCurrentLatLng, dropOffLatLng);
                        //new FetchURL(getActivity()).execute(url, "driving");
                        mMap.addMarker(new MarkerOptions().position(dropOffLatLng).title("Your Pickup"));
                        double distance = SphericalUtil.computeDistanceBetween(driverCurrentLatLng, dropOffLatLng);
                        if (distance <= 50) {
                            // truckStatusTextView.setText("Your Truck has been arrived");
                            HashMap<String, Object> mapForUpdate = new HashMap<>();
                            mapForUpdate.put(Constants.RIDE_STATUS, Constants.REACHED_AT_DROP_OFF);
                            truckRequestRef.updateChildren(mapForUpdate);
                            truckRequestRef.removeEventListener(listener);
                            btnFinishRide.setVisibility(View.VISIBLE);
                            Toast.makeText(getApplicationContext(), "Your Truck has been arrived to your location", Toast.LENGTH_SHORT).show();
                        }
                    } else if (Objects.requireNonNull(Objects.requireNonNull(map).get(Constants.RIDE_STATUS)).toString().equals(Constants.REACHED_AT_DROP_OFF)) {

                        mMap.clear();
                        dropOffLatLng = new LatLng(Double.parseDouble(Objects.requireNonNull(map.get(Constants.RIDE_DROP_OFF_LAT)).toString()), Double.parseDouble(Objects.requireNonNull(map.get(Constants.RIDE_DROP_OFF_LNG)).toString()));
                        driverCurrentLatLng = new LatLng(Double.parseDouble(Objects.requireNonNull(map.get(Constants.DRIVER_CURRENT_LAT)).toString()), Double.parseDouble(Objects.requireNonNull(map.get(Constants.DRIVER_CURRENT_LNG)).toString()));
                        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(driverCurrentLatLng, 12.0f));
                        mMap.addMarker(new MarkerOptions().position(driverCurrentLatLng).title("Driver").snippet("xyz").icon(BitmapDescriptorFactory.fromResource(R.drawable.truck_icon)));

                        //new FetchURL(getActivity()).execute(url, "driving");
                        mMap.addMarker(new MarkerOptions().position(dropOffLatLng).title("Your Pickup"));
                        double distance = SphericalUtil.computeDistanceBetween(driverCurrentLatLng, dropOffLatLng);
                        if (distance <= 50) {
                            // truckStatusTextView.setText("Your Truck has been arrived");
                            HashMap<String, Object> mapForUpdate = new HashMap<>();
                            mapForUpdate.put(Constants.RIDE_STATUS, Constants.REACHED_AT_DROP_OFF);
                            truckRequestRef.updateChildren(mapForUpdate);
                            truckRequestRef.removeEventListener(listener);
                            Toast.makeText(getApplicationContext(), "Your Truck has been Reached to your Drop off location", Toast.LENGTH_SHORT).show();
                        }
                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }

    private void updateStatus() {
        truckRequestRef = rootRef.child(Constants.REQUEST_RIDE_NODE).child(mAuth.getUid()).child(ShowRequestsAdapter.userId);
        truckRequestRef.child("status").setValue("accepted").addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                btnAction.setText("Ride Accepted");
                btnAction.setEnabled(false);
                LatLng star = new LatLng(myLocation.getLatitude(), myLocation.getLongitude());
                mMap.clear();
                Findroutes(star, pickupLatLng);
                checkIfDriverIsOnTheWay = true;

            }
        });
    }

    private Boolean UpdateLocationInFirebase(Double lat, Double longi) {
        updateRef = rootRef.child(Constants.REQUEST_RIDE_NODE).child(mAuth.getUid()).child(ShowRequestsAdapter.userId);

        updateRef.child("DriverCurrentLat").setValue(lat).addOnSuccessListener(aVoid -> {
            updateRef.child("DriverCurrentLng").setValue(longi).addOnSuccessListener(aaVoid -> {
                // Write was successful!

            });
        }).addOnFailureListener(e -> {

            Toast.makeText(getApplicationContext(), "Some Thing Went Wrong", Toast.LENGTH_LONG).show();

        });


        return true;

    }

    private void finishRide() {
         DecimalFormat df = new DecimalFormat("0.00");
        double distance = SphericalUtil.computeDistanceBetween(driverCurrentLatLng, dropOffLatLng);
      distance/=1000;
       final double amts= distance * Constants.RATE_PER_KM;

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("CompletedRides");
        truckRequestRef = rootRef.child(Constants.REQUEST_RIDE_NODE).child(mAuth.getUid()).child(ShowRequestsAdapter.userId);
        HashMap<String, Object> requestHashmap = new HashMap<>();

        listener = truckRequestRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    HashMap<String, Object> map = (HashMap<String, Object>) snapshot.getValue();
                    requestHashmap.put(Constants.RIDE_PICKUP_LAT, map.get("RidePickupLat"));
                    requestHashmap.put(Constants.RIDE_PICKUP_LNG, map.get("RidePickupLng"));
                    requestHashmap.put(Constants.RIDE_DROP_OFF_LAT, map.get("RideDropOffLat"));
                    requestHashmap.put(Constants.RIDE_DROP_OFF_LNG, map.get("RideDropOffLng"));
                    requestHashmap.put(Constants.DRIVER_CURRENT_LAT, map.get("DriverCurrentLat"));
                    requestHashmap.put(Constants.DRIVER_CURRENT_LNG, map.get("DriverReached"));
                    requestHashmap.put(Constants.DRIVER_REACHED, true);
                    requestHashmap.put(Constants.DRIVER_FULL_NAME, map.get("driverName"));
                    requestHashmap.put(Constants.DRIVER_EMAIL, map.get("driverEmail"));
                    requestHashmap.put(Constants.DRIVER_PHONE, map.get("driverPhone"));
                    requestHashmap.put(Constants.DRIVER_TRUCK_DETAILS, map.get("driverTruckDetails"));
                    requestHashmap.put(Constants.RIDE_STATUS, "Completed");
                    requestHashmap.put(Constants.TOTAL_RIDE_AMOUNT, String.valueOf(amts));
                    reference.child(mAuth.getUid()).child(ShowRequestsAdapter.userId).setValue(requestHashmap).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            if (task.isSuccessful()) {
                                rootRef.child(Constants.REQUEST_RIDE_NODE).child(mAuth.getUid()).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {

                                            Intent intent = new Intent(getApplicationContext(), RideFinishActivity.class);
                                            intent.putExtra("",amts);
                                            startActivity(intent);
                                            finish();
                                        }
                                    }
                                });
                            }
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }
}