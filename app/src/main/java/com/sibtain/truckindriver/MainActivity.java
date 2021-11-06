package com.sibtain.truckindriver;

import androidx.appcompat.app.AppCompatActivity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.sibtain.truckindriver.boarding.OnBoardingActivity;
import com.sibtain.truckindriver.broadcastreceiver.MyBroadcastReceiver;
import com.sibtain.truckindriver.sharedpreferences.SharedPreferencesCustomClass;

public class MainActivity extends AppCompatActivity {
    Animation anim;
    LottieAnimationView internetConAnim;
    RelativeLayout splashAnim;
    TextView txtTitle;
    BroadcastReceiver br = new MyBroadcastReceiver();

    SharedPreferencesCustomClass sps ;
    Boolean spsValue;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_main);
        anim = AnimationUtils.loadAnimation(getApplicationContext(),
                R.anim.animation_on_title);
        txtTitle = findViewById(R.id.txtTitle);
        internetConAnim = findViewById(R.id.animationView);
        sps = new SharedPreferencesCustomClass(MainActivity.this);
        spsValue =sps.getValue();

        /*  //Just testing for broadcast receiver
        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        filter.addAction(Intent.ACTION_AIRPLANE_MODE_CHANGED);
        this.registerReceiver(br, filter);
        */
        txtTitle.startAnimation(anim);
        splashAnim = findViewById(R.id.AniSplash);
        if (checkConnection()) {
            internetConAnim.setVisibility(View.GONE);
            splashAnim.setVisibility(View.VISIBLE);
            new Handler().postDelayed(() -> {
                if (spsValue){
                    startActivity(new Intent(this, Login.class));
                }
                else{
                    startActivity(new Intent(this, OnBoardingActivity.class));
                }
                sps.save();
                finish();

            }, 3500);


        } else {
            internetConAnim.setVisibility(View.VISIBLE);
            splashAnim.setVisibility(View.GONE);

        }

    }

    public Boolean checkConnection() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if (networkInfo != null) {
            if (networkInfo.getType() == ConnectivityManager.TYPE_WIFI) {
                Toast.makeText(MainActivity.this, "Wifi is Connected", Toast.LENGTH_SHORT).show();
            } else if (networkInfo.getType() == ConnectivityManager.TYPE_MOBILE) {
                Toast.makeText(MainActivity.this, "Mobile Data is Connected", Toast.LENGTH_SHORT).show();
            }
            return true;
        } else {

            Toast.makeText(MainActivity.this, "No Internet Available", Toast.LENGTH_SHORT).show();
            return false;
        }


    }
}