package com.sibtain.truckindriver;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.RatingBar;

import dev.yuganshtyagi.smileyrating.SmileyRatingView;

public class Rating extends AppCompatActivity {
    Button btnSubmit;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rating);
        btnSubmit = findViewById(R.id.btnSubmit);
        SmileyRatingView smileyRatingView = findViewById(R.id.smiley_view);
        RatingBar   ratingBar = findViewById(R.id.rating_bar);
        ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                smileyRatingView.setSmiley(rating);
            }
        });

        btnSubmit.setOnClickListener(view -> {
            startActivity(new Intent(getApplicationContext(),Home.class));
            finish();
        });

    }
}