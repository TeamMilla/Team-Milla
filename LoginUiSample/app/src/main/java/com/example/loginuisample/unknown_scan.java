package com.example.loginuisample;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

public class Unknown_scan extends AppCompatActivity {
    Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.unknownqr);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                intent = new Intent(Unknown_scan.this, MainActivity.class);
                startActivity(intent);
            }
        },1600);
    }
}