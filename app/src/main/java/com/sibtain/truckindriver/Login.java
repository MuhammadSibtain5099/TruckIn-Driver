package com.sibtain.truckindriver;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Objects;

public class Login extends AppCompatActivity {
    TextInputLayout edEmail,edPassword;
    Button btnLogin,btnRegister;
    private FirebaseAuth mAuth;
    DatabaseReference myRef;
    ViewGroup viewGroup ;
    AlertDialog alertDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mAuth = FirebaseAuth.getInstance();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        myRef = database.getReference().child("Drivers");
        edEmail = findViewById(R.id.edEmail);
        edPassword = findViewById(R.id.edPassword);
        btnLogin = findViewById(R.id.btnLogin);
        btnRegister = findViewById(R.id.btnRegister);
        viewGroup=findViewById(android.R.id.content);

        Objects.requireNonNull(edEmail.getEditText()).setText("sibtain.inceptions@gmail.com");
        Objects.requireNonNull(edPassword.getEditText()).setText("123456");
        btnLogin.setOnClickListener(v -> {
            String Email = edEmail.getEditText().getText().toString();
            String Password = edPassword.getEditText().getText().toString();
            if (Email.equals("")) {
                edEmail.setError("Email Required");
            } else if (Password.equals("")) {
                edPassword.setError("Password Required");
                edEmail.setError(null);
            } else {
                edEmail.setError(null);
                edPassword.setError(null);
                loadingScreenDialog();
                mAuth.signInWithEmailAndPassword(Email, Password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(Task<AuthResult> task) {
                        if (task.isSuccessful()){
//                            Toast.makeText(Login.this, mAuth.getUid(), Toast.LENGTH_SHORT).show();
                            myRef.child(Objects.requireNonNull(mAuth.getUid())).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                                @Override
                                public void onComplete(Task<DataSnapshot> task) {
                                    if (task.isSuccessful()){

                                        HashMap<String, Object> map = (HashMap<String, Object>) task.getResult().getValue();


                                        Boolean aBoolean = Boolean.parseBoolean(map.get("approved").toString().trim());

                                    if (aBoolean){

                                        startActivity(new Intent(Login.this,Home.class));
                                    }else {
                                        Toast.makeText(Login.this, "Your Account is Not Activated Yet", Toast.LENGTH_SHORT).show();
                                    }

                                }else{
                                        alertDialog.dismiss();
                                    }

                                }
                            });
                        }else{
                            Toast.makeText(getApplicationContext(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            alertDialog.dismiss();
                        }

                    }
                });
            }
        });
        btnRegister.setOnClickListener(v -> {
            Intent intent = new Intent(this,Register.class);
            startActivity(intent);
        });

    }


    private void loadingScreenDialog(){

        AlertDialog.Builder builder = new AlertDialog.Builder(Login.this);
        ViewGroup viewGroup = findViewById(android.R.id.content);
        View dialogView = LayoutInflater.from(getApplicationContext()).inflate(R.layout.custom_dialog_loading, viewGroup, false);
        builder.setView(dialogView);
        alertDialog = builder.create();
        alertDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        alertDialog.show();





        }

    @Override
    protected void onPause() {
        super.onPause();
//        alertDialog.dismiss();
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (mAuth.getUid()!=null){
            Toast.makeText(getApplicationContext(),mAuth.getUid().toString(),Toast.LENGTH_LONG).show();
            startActivity(new Intent(this,Home.class));
            finish();
        }
    }
}




