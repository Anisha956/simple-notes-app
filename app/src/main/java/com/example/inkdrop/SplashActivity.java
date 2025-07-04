package com.example.inkdrop;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

@SuppressLint("CustomSplashScreen")
public class SplashActivity extends AppCompatActivity {

    ImageView app_logo;
    TextView app_name;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_splash);

        app_logo = findViewById(R.id.app_logo);
        app_name = findViewById(R.id.app_name);

        Animation popupAnimation = AnimationUtils.loadAnimation(this,R.anim.popup);
        app_logo.startAnimation(popupAnimation);
        app_name.startAnimation(popupAnimation);

        new Handler().postDelayed(()->{
            startActivity(new Intent(this, MainActivity.class));
            finish();
        },1000);
    }
}