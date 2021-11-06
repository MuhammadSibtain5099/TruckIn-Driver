package com.sibtain.truckindriver;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.os.Bundle;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.sibtain.truckindriver.adapter.ShowRequestsAdapter;
import com.sibtain.truckindriver.model.RideRequest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class ShowRequestListActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    ShowRequestsAdapter requestsAdapter;
    List<RideRequest> rideRequestList;

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    private FirebaseAuth mAuth;
    DatabaseReference myRef, updateRef;
    DatabaseReference rootRef;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_request_list);
        mAuth = FirebaseAuth.getInstance();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        myRef = database.getReference().child("Requests");
        rootRef = database.getReference();
        recyclerView = findViewById(R.id.recyclerView);
        lisRidesRequest();
        rideRequestList = new ArrayList<>();


    }
    private void lisRidesRequest() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReference().child("Requests");

        ref.child(mAuth.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    HashMap<String, Object> map = (HashMap<String, Object>) snapshot.getValue();
                    if (map.size() > 0) {
                        Iterator iterator = map.entrySet().iterator();
                        while (iterator.hasNext()) {
                            Map.Entry me2 = (Map.Entry) iterator.next();
                            HashMap<String, Object> requestDetails = (HashMap<String, Object>) me2.getValue();
                            Log.d("RequestDetails", map.size() + requestDetails.toString());
                            RideRequest r = new RideRequest();
                            r.setRiderDropOffLat(requestDetails.get("RideDropOffLat").toString());
                            r.setRiderDropOffLng(requestDetails.get("RideDropOffLng").toString());
                            r.setRiderPickupLat(requestDetails.get("RidePickupLat").toString());
                            r.setRiderPickupLng(requestDetails.get("RidePickupLng").toString());
                            r.setStatus(requestDetails.get("status").toString());
                            r.setUid(me2.getKey().toString());
                            if (r.getStatus().equals("pending")) {
                                rideRequestList.add(r);
                            }
                        }

                    }
                    if (rideRequestList.size() > 0) {
//                    alertDialog.show();

                        requestsAdapter = new ShowRequestsAdapter(ShowRequestListActivity.this, rideRequestList);
                        recyclerView.setLayoutManager(new LinearLayoutManager(ShowRequestListActivity.this));
                        recyclerView.setAdapter(requestsAdapter);

                    }


                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

}