package com.example.simplechatapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class AuthActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);

        ((Button)findViewById(R.id.btnLogin)).setOnClickListener(v -> startActivity(new Intent(AuthActivity.this, LoginActivity.class)));
        ((Button)findViewById(R.id.btnRegister)).setOnClickListener(v -> startActivity(new Intent(AuthActivity.this, RegisterActivity.class)));
    }
}