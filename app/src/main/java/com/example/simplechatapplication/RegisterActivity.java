package com.example.simplechatapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {

    EditText edtEmail, edtPassword, edtConfirmPassword;
    Button btnRegister;
    FirebaseAuth firebaseAuth;
    FirebaseFirestore firebaseFirestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();

        edtEmail = findViewById(R.id.edtEmail);
        edtPassword = findViewById(R.id.edtPassword);
        edtConfirmPassword = findViewById(R.id.edtConfirmPassword);

        btnRegister = findViewById(R.id.btnRegister);

        btnRegister.setOnClickListener(v -> {
            if(!edtPassword.getText().toString().equals(edtConfirmPassword.getText().toString())){
                Toast.makeText(RegisterActivity.this, "Password is not same", Toast.LENGTH_SHORT).show();
            }
            else {
                firebaseAuth.createUserWithEmailAndPassword(edtEmail.getText().toString(), edtPassword.getText().toString()).addOnCompleteListener(task -> {
                   task.addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                       @Override
                       public void onComplete(@NonNull Task<AuthResult> task) {

                           Map<String, Object> user = new HashMap<>();
                           user.put("uid",task.getResult().getUser().getUid());
                           user.put("email", task.getResult().getUser().getEmail().toLowerCase());
                           firebaseFirestore.collection("users").document(task.getResult().getUser().getUid()).set(user).addOnCompleteListener(task1 -> {
                               task1.addOnSuccessListener(unused -> startActivity(new Intent(RegisterActivity.this, LoginActivity.class)));
                               task1.addOnFailureListener(e -> {

                               });
                           });
                       }
                   });
                   task.addOnFailureListener(e -> {

                   });
                });
            }
        });


    }
}