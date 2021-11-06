package com.sibtain.truckindriver;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.sibtain.truckindriver.model.UsersModel;

public class Register extends AppCompatActivity {
    TextInputLayout edFullName, edPhone, edTuckDetails, edEmail, edPassword;
    Button btnRegister;
    private FirebaseAuth mAuth;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mAuth = FirebaseAuth.getInstance();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("Drivers");
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Please Wait...!!!");
        progressDialog.setMessage("Please Wait, We are check your details");
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
            progressDialog.show();
            Boolean errorFlag = false;
            edFullName.setError(null);
            edPhone.setError(null);
            edTuckDetails.setError(null);
            edEmail.setError(null);
            edPassword.setError(null);

            if (FullName.equals("")) {
                edFullName.setError("FullName Required");
                errorFlag = true;
            }
            if (Phone.equals("")) {
                edPhone.setError("Phone is Required");
                errorFlag = true;
            }
            if (TruckDetails.equals("")) {
                edTuckDetails.setError("Truck Details Required");
                errorFlag = true;
            }
            if (Email.equals("")) {
                edEmail.setError("Email Required");
                errorFlag = true;
            }
            if (Password.equals("")) {
                edPassword.setError("Password Required");
                errorFlag = true;

            }
            if (!errorFlag) {
                mAuth.createUserWithEmailAndPassword(Email, Password).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        UsersModel usersModel = new UsersModel(mAuth.getUid(), FullName, Email, Password, Phone, TruckDetails,false);

                        myRef.child(mAuth.getUid()).setValue(usersModel).addOnCompleteListener(task1 -> {
                            if (task1.isSuccessful()) {
                                Toast.makeText(Register.this, "Your Account Created Successfully , Our Experts WIll Contact With you Soon", Toast.LENGTH_SHORT).show();
                                progressDialog.dismiss();
                                mAuth.signOut();
                            } else {
                                Toast.makeText(Register.this, task1.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                progressDialog.dismiss();
                            }
                        });
                    } else {
                        progressDialog.dismiss();
                        Toast.makeText(Register.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();

                    }

                });
            }
        });


    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(getApplicationContext(),Login.class));
        finish();
    }
}