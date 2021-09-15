package com.sibtain.truckindriver;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
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

public class MainActivity extends AppCompatActivity {
    Animation anim;
    LottieAnimationView internetConAnim;
    RelativeLayout splashAnim;
    TextView txtTitle;

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
        txtTitle.startAnimation(anim);
        splashAnim = findViewById(R.id.AniSplash);
        if (checkConnection()) {
            internetConAnim.setVisibility(View.GONE);
            splashAnim.setVisibility(View.VISIBLE);
            new Handler().postDelayed(() -> {
                startActivity(new Intent(this, Login.class));
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