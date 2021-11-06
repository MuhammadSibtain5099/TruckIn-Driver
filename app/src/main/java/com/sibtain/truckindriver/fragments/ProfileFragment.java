package com.sibtain.truckindriver.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.sibtain.truckindriver.R;
import com.sibtain.truckindriver.model.Drivers;
import com.sibtain.truckindriver.model.UsersModel;

import org.w3c.dom.Text;

import java.util.HashMap;

public class ProfileFragment extends Fragment {
TextView tv_name,tv_truckDetails,tv_email,tv_phoneNumber,tv_isOnline,tv_isApproved;
    private FirebaseAuth mAuth;
    DatabaseReference myRef;
    Drivers driver;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v=  inflater.inflate(R.layout.fragment_profile, container, false);
        tv_name = v.findViewById(R.id.tv_name);
        tv_truckDetails = v.findViewById(R.id.tv_truckDetails);
        tv_email = v.findViewById(R.id.tv_email);
        tv_phoneNumber = v.findViewById(R.id.tv_phoneNumber);
        tv_isOnline=  v.findViewById(R.id.tv_isOnline);
        tv_isApproved = v.findViewById(R.id.tv_isApproved);


        mAuth = FirebaseAuth.getInstance();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        myRef = database.getReference().child("Drivers");

        myRef.child(mAuth.getUid()).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()){
                HashMap<String, Object> map = (HashMap<String, Object>) task.getResult().getValue();

                Log.d("testing", map.toString());

                UsersModel usersModel= new UsersModel(map.get("uid").toString(),map.get("fullName").toString(),map.get("email").toString(),"null",map.get("phone").toString(),map.get("truckDetails").toString(),Boolean.parseBoolean(map.get("approved").toString()),Boolean.parseBoolean(map.get("isOnline").toString()));
                tv_name.setText(usersModel.getFullName());
                tv_truckDetails.setText(usersModel.getTruckDetails());
                tv_email.setText(usersModel.getEmail());
                tv_isOnline.setText(usersModel.getIsOnlineStatus());
                tv_isApproved.setText(usersModel.getIsApprovedStatus());
                tv_phoneNumber.setText(usersModel.getPhone());
            }
        });
        myRef.child(mAuth.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                    HashMap<String, Object> map = (HashMap<String, Object>) snapshot.getValue();

                    Log.d("testing", map.toString());

                    UsersModel usersModel= new UsersModel(map.get("uid").toString(),map.get("fullName").toString(),map.get("email").toString(),"null",map.get("phone").toString(),map.get("truckDetails").toString(),Boolean.parseBoolean(map.get("approved").toString()),Boolean.parseBoolean(map.get("isOnline").toString()));
                    tv_name.setText(usersModel.getFullName());
                    tv_truckDetails.setText(usersModel.getTruckDetails());
                    tv_email.setText(usersModel.getEmail());
                    tv_isOnline.setText(usersModel.getIsOnlineStatus());
                    tv_isApproved.setText(usersModel.getIsApprovedStatus());
                    tv_phoneNumber.setText(usersModel.getPhone());

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        return v;
    }
}