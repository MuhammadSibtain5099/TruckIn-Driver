package com.sibtain.truckindriver;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;

public class Register extends AppCompatActivity {
    TextInputLayout edFullName,edPhone,edTuckDetails,edEmail,edPassword;
    Button btnRegister;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mAuth = FirebaseAuth.getInstance();
        edEmail = findViewById(R.id.edEmail);
        edFullName = findViewById(R.id.edFullName);
        edPhone = findViewById(R.id.edPhone);
        edTuckDetails = findViewById(R.id.edTruckInfo);
        edPassword = findViewById(R.id.edPassword);
        btnRegister = findViewById(R.id.btnRegister);
        btnRegister.setOnClickListener(v -> {
            String Email = edEmail.getEditText().getText().toString();
            String Password = edPassword.getEditText().getText().toString();
            String FullName = edFullName.getEditText().getText().toString();
            String Phone = edPhone.getEditText().getText().toString();
            String TruckDetails = edTuckDetails.getEditText().getText().toString();
           Boolean errorFlag = false;
            edFullName.setError(null);
            edPhone.setError(null);
            edTuckDetails.setError(null);
            edEmail.setError(null);
            edPassword.setError(null);

            if (FullName.equals("")) {
                edFullName.setError("FullName Required");
                errorFlag = true;
            }  if (Phone.equals("")) {
                edPhone.setError("Phone is Required");
                errorFlag = true;
            }  if (TruckDetails.equals("")) {
                edTuckDetails.setError("Truck Details Required");
                errorFlag = true;
            }  if (Email.equals("")) {
                edEmail.setError("Email Required");
                errorFlag = true;
            } if (Password.equals("")) {
                edPassword.setError("Password Required");
                errorFlag = true;

            } if (!errorFlag){

            }
        });


    }
}