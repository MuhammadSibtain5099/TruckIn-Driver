package com.sibtain.truckindriver;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;

public class Login extends AppCompatActivity {
    TextInputLayout edEmail,edPassword;
    Button btnLogin;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mAuth = FirebaseAuth.getInstance();
        edEmail = findViewById(R.id.edEmail);
        edPassword = findViewById(R.id.edPassword);
        btnLogin = findViewById(R.id.btnLogin);
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

                mAuth.signInWithEmailAndPassword(Email, Password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()){

                        }else{
                            Toast.makeText(getApplicationContext(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });

    }
}