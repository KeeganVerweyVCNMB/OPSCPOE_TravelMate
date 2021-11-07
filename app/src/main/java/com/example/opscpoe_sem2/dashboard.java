package com.example.opscpoe_sem2;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

public class dashboard extends AppCompatActivity {
    //Setting Variables
    Button btnSignOut;
    String emailPassed, passPassed;
    LinearLayout btnViewMaps, btnShareMaps, btnServices, btnProfile, btnRequestSOS, btnMapSettings, btnPlanMap, btnGoSavedRoutes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dashboard);

        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if(!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) && !locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
            Toast.makeText(getApplicationContext(), "Please enable your devices location.", Toast.LENGTH_LONG).show();
        }

        //Disable dark mode settings
        //stackoverflow.com. 2021. How to disable night mode in my application even if night mode is enabled in andr: Android Tutorial.
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        //Setting screen orientation
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        //Calling action bar and setting page description
        getSupportActionBar().setTitle("Dashboard");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //Sign Out
        btnSignOut = (Button) findViewById(R.id.btnSignOut);
        btnSignOut.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                startActivity(new Intent(dashboard.this, login.class));
            }
        });

        btnViewMaps = findViewById(R.id.btnViewMap);
        btnViewMaps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(dashboard.this, viewMaps.class));
            }
        });

        btnShareMaps = findViewById(R.id.btnShare);
        btnShareMaps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(dashboard.this, shareMap.class));
            }
        });

        btnServices = findViewById(R.id.btnService);
        btnServices.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(dashboard.this, servicesMap.class));
            }
        });

        Bundle b = getIntent().getExtras();
        if(b != null){
            emailPassed = b.getString("Email");
            passPassed = b.getString("Password");
        }

        if(emailPassed != null && passPassed != null) {
            SharedPreferences settingsMail = getApplicationContext().getSharedPreferences("Email", 0);
            SharedPreferences.Editor editorMail = settingsMail.edit();
            editorMail.putString("Email", emailPassed);
            editorMail.apply();

            SharedPreferences settingsPass = getApplicationContext().getSharedPreferences("Pass", 0);
            SharedPreferences.Editor editorPass = settingsPass.edit();
            editorPass.putString("Pass", passPassed);
            editorPass.apply();
        }

        btnProfile = findViewById(R.id.btnProfile);
        btnProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(dashboard.this, userProfile.class));
            }
        });

        btnRequestSOS = findViewById(R.id.btnSOS);
        btnRequestSOS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(dashboard.this, requestSOS.class));
            }
        });

        btnMapSettings = findViewById(R.id.btnSettings);
        btnMapSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(dashboard.this, mapSettings.class));
            }
        });

        btnPlanMap = findViewById(R.id.btnPlanTrip);
        btnPlanMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(dashboard.this, planRoute.class));
            }
        });

        btnGoSavedRoutes = findViewById(R.id.btnSavedRoutes);
        btnGoSavedRoutes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(dashboard.this, savedRoutes.class));
            }
        });
    }
}

