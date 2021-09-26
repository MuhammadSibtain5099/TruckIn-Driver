package com.sibtain.truckindriver;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;
import com.sibtain.truckindriver.fragments.AboutFragment;
import com.sibtain.truckindriver.fragments.ContactFragment;
import com.sibtain.truckindriver.fragments.HomeFragment;
import com.sibtain.truckindriver.fragments.ProfileFragment;

public class Home extends AppCompatActivity {

    ProfileFragment   profileFragment ;
    HomeFragment homeFragment;
    AboutFragment aboutFragment;
    ContactFragment contactFragment;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Toolbar toolbar = findViewById(R.id.toolbar ) ;
        setSupportActionBar(toolbar) ;
       profileFragment = new ProfileFragment();
       homeFragment = new HomeFragment();
       aboutFragment = new AboutFragment();
       contactFragment = new ContactFragment();
        DrawerLayout drawer = findViewById(R.id.drawerLayout ) ;
        NavigationView navigationView = findViewById(R.id.nav_menu);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer , toolbar , R.string.navigation_drawer_open ,
                R.string.navigation_drawer_close ) ;
        drawer.addDrawerListener(toggle) ;
        toggle.syncState() ;
        toggle.getDrawerArrowDrawable().setColor(getResources().getColor(R.color.white));
        getSupportFragmentManager().beginTransaction().add(R.id.container,homeFragment ).commit();

        navigationView.setNavigationItemSelectedListener(item -> {
           switch (item.getItemId()){
               case R.id.icon_Home:
                   getSupportFragmentManager().beginTransaction().replace(R.id.container, homeFragment).commit();
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


           }
            drawer.closeDrawers();
            return true;

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
}