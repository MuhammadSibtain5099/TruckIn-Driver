package com.sibtain.truckindriver;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Switch;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.MenuItemCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.sibtain.truckindriver.directionhelper.TaskLoadedCallback;
import com.sibtain.truckindriver.fragments.AboutFragment;
import com.sibtain.truckindriver.fragments.ContactFragment;
import com.sibtain.truckindriver.fragments.FirstFragment;
import com.sibtain.truckindriver.fragments.HomeFragment;
import com.sibtain.truckindriver.fragments.ProfileFragment;
import java.util.HashMap;
import java.util.Objects;


public class Home extends AppCompatActivity implements TaskLoadedCallback {

    ProfileFragment profileFragment;
    HomeFragment homeFragment;
    AboutFragment aboutFragment;
    ContactFragment contactFragment;
    Polyline currentPolyline;
    FirstFragment firstFragment;
    Switch switch_id;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    private FirebaseAuth mAuth;
    DatabaseReference myRef;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        profileFragment = new ProfileFragment();
        homeFragment = new HomeFragment();
        aboutFragment = new AboutFragment();
        contactFragment = new ContactFragment();
        firstFragment = new FirstFragment();

        DrawerLayout drawer = findViewById(R.id.drawerLayout);
        myRef = database.getReference("Drivers");
        mAuth = FirebaseAuth.getInstance();
        myRef.child(mAuth.getUid()).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()){
                HashMap<String, Object> map = (HashMap<String, Object>) task.getResult().getValue();


                Boolean aBoolean = Boolean.parseBoolean(map.get("isOnline").toString().trim());
                switch_id.setChecked(aBoolean);

            }
        });
        NavigationView navigationView = findViewById(R.id.nav_menu);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open,
                R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        toggle.getDrawerArrowDrawable().setColor(getResources().getColor(R.color.white));
        getSupportFragmentManager().beginTransaction().add(R.id.container, firstFragment).commit();

        navigationView.setNavigationItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.icon_Home:

                    getSupportFragmentManager().beginTransaction().replace(R.id.container, firstFragment).commit();
                    break;
                case R.id.icon_Profile:

                    getSupportFragmentManager().beginTransaction().replace(R.id.container, profileFragment).commit();
                    break;
                case R.id.About_Us:
                    getSupportFragmentManager().beginTransaction().replace(R.id.container, aboutFragment).commit();
                    break;
                case R.id.Contact_Us:
                    getSupportFragmentManager().beginTransaction().replace(R.id.container, contactFragment).commit();
                    break;
                case R.id.logoutUser:
                    mAuth.signOut();
                    startActivity(new Intent(this,Login.class));
                    finish();
                      break;


            }
            drawer.closeDrawers();
            return true;

        });

        Menu menu = navigationView.getMenu();
        MenuItem menuItem = menu.findItem(R.id.nav_switch);
        View actionView = MenuItemCompat.getActionView(menuItem);


        switch_id = actionView.findViewById(R.id.switch_id);


//        switch_id.setChecked(true);
        switch_id.setOnClickListener(v -> {
            //Toast.makeText(getApplicationContext(),  ? "is checked!!!" : "not checked!!!", Toast.LENGTH_SHORT).show();
            if (switch_id.isChecked()) {
                myRef.child(Objects.requireNonNull(mAuth.getUid())).child("isOnline").setValue(true).addOnSuccessListener(aVoid -> {
                    // Write was successful!


                }).addOnFailureListener(e -> {

                    Toast.makeText(getApplicationContext(), "Some Thing Went Wrong", Toast.LENGTH_LONG).show();

                });
            }else{
                myRef.child(Objects.requireNonNull(mAuth.getUid())).child("isOnline").setValue(false).addOnSuccessListener(aVoid -> {
                    // Write was successful!


                }).addOnFailureListener(e -> {

                    Toast.makeText(getApplicationContext(), "Some Thing Went Wrong", Toast.LENGTH_LONG).show();

                });
            }
        });


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);

    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onTaskDone(Object... values) {
        if (currentPolyline != null) {
            currentPolyline.remove();
        }

        currentPolyline = HomeFragment.mGoogleMap.addPolyline((PolylineOptions) values[0]);
    }



}