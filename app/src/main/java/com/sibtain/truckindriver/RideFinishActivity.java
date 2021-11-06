package com.sibtain.truckindriver;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import java.text.DecimalFormat;

public class RideFinishActivity extends AppCompatActivity {

    TextView txtTotalAmount;
    Button button;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ride_finish);
        txtTotalAmount = findViewById(R.id.txtTotalAmount);
        button = findViewById(R.id.button);
        Intent intent = getIntent();
        Double value = intent.getDoubleExtra("price",0.0);
        DecimalFormat df = new DecimalFormat("#.##");
        String setValue = df.format(value);
        txtTotalAmount.setText(setValue);
        button.setOnClickListener(view -> {
            startActivity(new Intent(getApplicationContext(),Rating.class));
            finish();
        });





    }
}