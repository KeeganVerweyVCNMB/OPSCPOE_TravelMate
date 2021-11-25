package com.example.opscpoe_sem2;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class mapSettings extends AppCompatActivity {

    DatabaseReference dbRef;
    DatabaseReference fRef;
    RadioButton rbKM, rbMiles, rbServices, rbATM, rbRestaurants, rbPetrol, rbCarWash;
    String emailLocally, myParentNode;
    String MapSettings[];
    String PrefLandmark[];
    ProgressBar progressBarSettings;
    TextView tvTop, tvBottom;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.map_settings);

        tvTop = findViewById(R.id.tvMapSettings);
        tvBottom = findViewById(R.id.tvPrefSettings);

        rbKM = findViewById(R.id.rbKM);
        rbMiles = findViewById(R.id.rbMiles);
        rbServices = findViewById(R.id.rbServices);
        rbATM = findViewById(R.id.rbATM);
        rbRestaurants = findViewById(R.id.rbRestaurants);
        rbPetrol = findViewById(R.id.rbFuel);
        rbCarWash = findViewById(R.id.rbCarWash);

        //Disable dark mode settings
        //stackoverflow.com. 2021. How to disable night mode in my application even if night mode is enabled in andr: Android Tutorial.
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        //Setting screen orientation
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        //Calling action bar and setting page description
        getSupportActionBar().setTitle("My Travel Settings");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        progressBarSettings = (ProgressBar)findViewById(R.id.progressBarSettings);
        //stackoverflow.com. 2021. Android progress bar not hiding: Android Tutorial.
        progressBarSettings.setVisibility(RelativeLayout.VISIBLE);

        SharedPreferences settingsMail = getApplicationContext().getSharedPreferences("Email", 0);
        emailLocally = settingsMail.getString("Email", "email");

        final Button btnSettings = (Button) findViewById(R.id.btnUpdateSettings);

        tvTop.setVisibility(View.INVISIBLE);
        tvBottom.setVisibility(View.INVISIBLE);
        rbKM.setVisibility(View.INVISIBLE);
        rbMiles.setVisibility(View.INVISIBLE);
        rbServices.setVisibility(View.INVISIBLE);
        rbATM.setVisibility(View.INVISIBLE);
        rbRestaurants.setVisibility(View.INVISIBLE);
        rbPetrol.setVisibility(View.INVISIBLE);
        rbCarWash.setVisibility(View.INVISIBLE);
        btnSettings.setVisibility(View.INVISIBLE);

        btnSettings.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v)
            {
                if(rbKM.isChecked() || rbMiles.isChecked()){
                    if(rbServices.isChecked() || rbATM.isChecked() || rbRestaurants.isChecked() || rbPetrol.isChecked() || rbCarWash.isChecked())
                    {
                        if(myParentNode != null) {
                            if (rbKM.isChecked() && rbServices.isChecked()) {
                                dbRef.child(myParentNode).removeValue();

                                //Generating random number
                                int max = 1000000;
                                int min = 0;

                                Random randomNum = new Random();
                                int generatedID = min + randomNum.nextInt(max);

                                String settingsID = "SettingID-" + generatedID;

                                dbRef = FirebaseDatabase.getInstance().getReference("MySettings");
                                dbRef.child(settingsID).setValue(settingsID);

                                dbRef = FirebaseDatabase.getInstance().getReference("MySettings");
                                dbRef.child(settingsID).child("MapSettings").setValue("KM");

                                dbRef = FirebaseDatabase.getInstance().getReference("MySettings");
                                dbRef.child(settingsID).child("PreferredLandmark").setValue("Services");

                                dbRef = FirebaseDatabase.getInstance().getReference("MySettings");
                                dbRef.child(settingsID).child("Email").setValue(emailLocally);

                                startActivity(new Intent(mapSettings.this, dashboard.class));
                                Toast.makeText(mapSettings.this, "Settings Successfully Updated", Toast.LENGTH_LONG).show();
                            }
                            if (rbKM.isChecked() && rbATM.isChecked()) {
                                dbRef.child(myParentNode).removeValue();

                                //Generating random number
                                int max = 1000000;
                                int min = 0;

                                Random randomNum = new Random();
                                int generatedID = min + randomNum.nextInt(max);

                                String settingsID = "SettingID-" + generatedID;

                                dbRef = FirebaseDatabase.getInstance().getReference("MySettings");
                                dbRef.child(settingsID).setValue(settingsID);

                                dbRef = FirebaseDatabase.getInstance().getReference("MySettings");
                                dbRef.child(settingsID).child("MapSettings").setValue("KM");

                                dbRef = FirebaseDatabase.getInstance().getReference("MySettings");
                                dbRef.child(settingsID).child("PreferredLandmark").setValue("ATM");

                                dbRef = FirebaseDatabase.getInstance().getReference("MySettings");
                                dbRef.child(settingsID).child("Email").setValue(emailLocally);

                                startActivity(new Intent(mapSettings.this, dashboard.class));
                                Toast.makeText(mapSettings.this, "Settings Successfully Updated", Toast.LENGTH_LONG).show();
                            }
                            if (rbKM.isChecked() && rbRestaurants.isChecked()) {
                                dbRef.child(myParentNode).removeValue();

                                //Generating random number
                                int max = 1000000;
                                int min = 0;

                                Random randomNum = new Random();
                                int generatedID = min + randomNum.nextInt(max);

                                String settingsID = "SettingID-" + generatedID;

                                dbRef = FirebaseDatabase.getInstance().getReference("MySettings");
                                dbRef.child(settingsID).setValue(settingsID);

                                dbRef = FirebaseDatabase.getInstance().getReference("MySettings");
                                dbRef.child(settingsID).child("MapSettings").setValue("KM");

                                dbRef = FirebaseDatabase.getInstance().getReference("MySettings");
                                dbRef.child(settingsID).child("PreferredLandmark").setValue("Restaurants");

                                dbRef = FirebaseDatabase.getInstance().getReference("MySettings");
                                dbRef.child(settingsID).child("Email").setValue(emailLocally);

                                startActivity(new Intent(mapSettings.this, dashboard.class));
                                Toast.makeText(mapSettings.this, "Settings Successfully Updated", Toast.LENGTH_LONG).show();
                            }
                            if (rbKM.isChecked() && rbPetrol.isChecked()) {
                                dbRef.child(myParentNode).removeValue();

                                //Generating random number
                                int max = 1000000;
                                int min = 0;

                                Random randomNum = new Random();
                                int generatedID = min + randomNum.nextInt(max);

                                String settingsID = "SettingID-" + generatedID;

                                dbRef = FirebaseDatabase.getInstance().getReference("MySettings");
                                dbRef.child(settingsID).setValue(settingsID);

                                dbRef = FirebaseDatabase.getInstance().getReference("MySettings");
                                dbRef.child(settingsID).child("MapSettings").setValue("KM");

                                dbRef = FirebaseDatabase.getInstance().getReference("MySettings");
                                dbRef.child(settingsID).child("PreferredLandmark").setValue("Petrol");

                                dbRef = FirebaseDatabase.getInstance().getReference("MySettings");
                                dbRef.child(settingsID).child("Email").setValue(emailLocally);

                                startActivity(new Intent(mapSettings.this, dashboard.class));
                                Toast.makeText(mapSettings.this, "Settings Successfully Updated", Toast.LENGTH_LONG).show();
                            }
                            if (rbKM.isChecked() && rbCarWash.isChecked()) {
                                dbRef.child(myParentNode).removeValue();

                                //Generating random number
                                int max = 1000000;
                                int min = 0;

                                Random randomNum = new Random();
                                int generatedID = min + randomNum.nextInt(max);

                                String settingsID = "SettingID-" + generatedID;

                                dbRef = FirebaseDatabase.getInstance().getReference("MySettings");
                                dbRef.child(settingsID).setValue(settingsID);

                                dbRef = FirebaseDatabase.getInstance().getReference("MySettings");
                                dbRef.child(settingsID).child("MapSettings").setValue("KM");

                                dbRef = FirebaseDatabase.getInstance().getReference("MySettings");
                                dbRef.child(settingsID).child("PreferredLandmark").setValue("CarWash");

                                dbRef = FirebaseDatabase.getInstance().getReference("MySettings");
                                dbRef.child(settingsID).child("Email").setValue(emailLocally);

                                startActivity(new Intent(mapSettings.this, dashboard.class));
                                Toast.makeText(mapSettings.this, "Settings Successfully Updated", Toast.LENGTH_LONG).show();
                            }

                            if (rbMiles.isChecked() && rbServices.isChecked()) {
                                dbRef.child(myParentNode).removeValue();

                                //Generating random number
                                int max = 1000000;
                                int min = 0;

                                Random randomNum = new Random();
                                int generatedID = min + randomNum.nextInt(max);

                                String settingsID = "SettingID-" + generatedID;

                                dbRef = FirebaseDatabase.getInstance().getReference("MySettings");
                                dbRef.child(settingsID).setValue(settingsID);

                                dbRef = FirebaseDatabase.getInstance().getReference("MySettings");
                                dbRef.child(settingsID).child("MapSettings").setValue("Miles");

                                dbRef = FirebaseDatabase.getInstance().getReference("MySettings");
                                dbRef.child(settingsID).child("PreferredLandmark").setValue("Services");

                                dbRef = FirebaseDatabase.getInstance().getReference("MySettings");
                                dbRef.child(settingsID).child("Email").setValue(emailLocally);

                                startActivity(new Intent(mapSettings.this, dashboard.class));
                                Toast.makeText(mapSettings.this, "Settings Successfully Updated", Toast.LENGTH_LONG).show();
                            }
                            if (rbMiles.isChecked() && rbATM.isChecked()) {
                                dbRef.child(myParentNode).removeValue();

                                //Generating random number
                                int max = 1000000;
                                int min = 0;

                                Random randomNum = new Random();
                                int generatedID = min + randomNum.nextInt(max);

                                String settingsID = "SettingID-" + generatedID;

                                dbRef = FirebaseDatabase.getInstance().getReference("MySettings");
                                dbRef.child(settingsID).setValue(settingsID);

                                dbRef = FirebaseDatabase.getInstance().getReference("MySettings");
                                dbRef.child(settingsID).child("MapSettings").setValue("Miles");

                                dbRef = FirebaseDatabase.getInstance().getReference("MySettings");
                                dbRef.child(settingsID).child("PreferredLandmark").setValue("ATM");

                                dbRef = FirebaseDatabase.getInstance().getReference("MySettings");
                                dbRef.child(settingsID).child("Email").setValue(emailLocally);

                                startActivity(new Intent(mapSettings.this, dashboard.class));
                                Toast.makeText(mapSettings.this, "Settings Successfully Updated", Toast.LENGTH_LONG).show();
                            }
                            if (rbMiles.isChecked() && rbRestaurants.isChecked()) {
                                dbRef.child(myParentNode).removeValue();

                                //Generating random number
                                int max = 1000000;
                                int min = 0;

                                Random randomNum = new Random();
                                int generatedID = min + randomNum.nextInt(max);

                                String settingsID = "SettingID-" + generatedID;

                                dbRef = FirebaseDatabase.getInstance().getReference("MySettings");
                                dbRef.child(settingsID).setValue(settingsID);

                                dbRef = FirebaseDatabase.getInstance().getReference("MySettings");
                                dbRef.child(settingsID).child("MapSettings").setValue("Miles");

                                dbRef = FirebaseDatabase.getInstance().getReference("MySettings");
                                dbRef.child(settingsID).child("PreferredLandmark").setValue("Restaurants");

                                dbRef = FirebaseDatabase.getInstance().getReference("MySettings");
                                dbRef.child(settingsID).child("Email").setValue(emailLocally);

                                startActivity(new Intent(mapSettings.this, dashboard.class));
                                Toast.makeText(mapSettings.this, "Settings Successfully Updated", Toast.LENGTH_LONG).show();
                            }
                            if (rbMiles.isChecked() && rbPetrol.isChecked()) {
                                dbRef.child(myParentNode).removeValue();

                                //Generating random number
                                int max = 1000000;
                                int min = 0;

                                Random randomNum = new Random();
                                int generatedID = min + randomNum.nextInt(max);

                                String settingsID = "SettingID-" + generatedID;

                                dbRef = FirebaseDatabase.getInstance().getReference("MySettings");
                                dbRef.child(settingsID).setValue(settingsID);

                                dbRef = FirebaseDatabase.getInstance().getReference("MySettings");
                                dbRef.child(settingsID).child("MapSettings").setValue("Miles");

                                dbRef = FirebaseDatabase.getInstance().getReference("MySettings");
                                dbRef.child(settingsID).child("PreferredLandmark").setValue("Petrol");

                                dbRef = FirebaseDatabase.getInstance().getReference("MySettings");
                                dbRef.child(settingsID).child("Email").setValue(emailLocally);

                                startActivity(new Intent(mapSettings.this, dashboard.class));
                                Toast.makeText(mapSettings.this, "Settings Successfully Updated", Toast.LENGTH_LONG).show();
                            }
                            if (rbMiles.isChecked() && rbCarWash.isChecked()) {
                                dbRef.child(myParentNode).removeValue();

                                //Generating random number
                                int max = 1000000;
                                int min = 0;

                                Random randomNum = new Random();
                                int generatedID = min + randomNum.nextInt(max);

                                String settingsID = "SettingID-" + generatedID;

                                dbRef = FirebaseDatabase.getInstance().getReference("MySettings");
                                dbRef.child(settingsID).setValue(settingsID);

                                dbRef = FirebaseDatabase.getInstance().getReference("MySettings");
                                dbRef.child(settingsID).child("MapSettings").setValue("Miles");

                                dbRef = FirebaseDatabase.getInstance().getReference("MySettings");
                                dbRef.child(settingsID).child("PreferredLandmark").setValue("CarWash");

                                dbRef = FirebaseDatabase.getInstance().getReference("MySettings");
                                dbRef.child(settingsID).child("Email").setValue(emailLocally);

                                startActivity(new Intent(mapSettings.this, dashboard.class));
                                Toast.makeText(mapSettings.this, "Settings Successfully Updated", Toast.LENGTH_LONG).show();
                            }
                        }
                        else {
                            Toast.makeText(getApplicationContext(), "View map before updating settings.", Toast.LENGTH_SHORT).show();
                        }
                    }
                    else {
                        Toast.makeText(mapSettings.this, "Please ensure that each section has a selected value.", Toast.LENGTH_LONG).show();
                    }
                }
                else {
                    Toast.makeText(mapSettings.this, "Please ensure that each section has a selected value.", Toast.LENGTH_LONG).show();
                }
            }
        });

        dbRef = FirebaseDatabase.getInstance().getReference();
        fRef = dbRef.child("MySettings");
        Query query = fRef.orderByChild("Email").equalTo(emailLocally);
        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                MapSettings = new String[(int)dataSnapshot.getChildrenCount()];
                PrefLandmark = new String[(int)dataSnapshot.getChildrenCount()];

                for(DataSnapshot ds : dataSnapshot.getChildren()) {
                    Map mapStock = new HashMap();
                    mapStock = (HashMap) ds.getValue();
                    myParentNode = dataSnapshot.getKey();
                    MapSettings[0] = mapStock.get("MapSettings").toString();
                    PrefLandmark[0] = mapStock.get("PreferredLandmark").toString();
                }

                if(myParentNode == null) {
                    //Generating random number
                    int max = 1000000;
                    int min = 0;

                    Random randomNum = new Random();
                    int generatedID = min + randomNum.nextInt(max);

                    String settingsID = "SettingID-" + generatedID;

                    dbRef = FirebaseDatabase.getInstance().getReference("MySettings");
                    dbRef.child(settingsID).setValue(settingsID);

                    dbRef = FirebaseDatabase.getInstance().getReference("MySettings");
                    dbRef.child(settingsID).child("MapSettings").setValue("KM");

                    dbRef = FirebaseDatabase.getInstance().getReference("MySettings");
                    dbRef.child(settingsID).child("PreferredLandmark").setValue("Services");

                    dbRef = FirebaseDatabase.getInstance().getReference("MySettings");
                    dbRef.child(settingsID).child("Email").setValue(emailLocally);

                    for(DataSnapshot ds : dataSnapshot.getChildren()) {
                        myParentNode = dataSnapshot.getKey();
                    }
                }

                if(MapSettings != null && MapSettings.length > 0 && MapSettings[0].length() > 0) {
                    if(MapSettings[0].equals("KM"))
                    {
                        rbKM.setChecked(true);
                        rbMiles.setChecked(false);
                    }
                    else if(MapSettings[0].equals("Miles")){
                        rbKM.setChecked(false);
                        rbMiles.setChecked(true);
                    }
                }
                else {
                    rbKM.setChecked(true);
                }

                if(PrefLandmark != null && PrefLandmark.length > 0 && PrefLandmark[0].length() > 0) {
                    if(PrefLandmark[0].equals("Services")){
                        rbServices.setChecked(true);
                        rbATM.setChecked(false);
                        rbRestaurants.setChecked(false);
                        rbPetrol.setChecked(false);
                        rbCarWash.setChecked(false);
                    }
                    else if(PrefLandmark[0].equals("ATM")) {
                        rbServices.setChecked(false);
                        rbATM.setChecked(true);
                        rbRestaurants.setChecked(false);
                        rbPetrol.setChecked(false);
                        rbCarWash.setChecked(false);
                    }
                    else if(PrefLandmark[0].equals("Restaurants")) {
                        rbServices.setChecked(false);
                        rbATM.setChecked(false);
                        rbRestaurants.setChecked(true);
                        rbPetrol.setChecked(false);
                        rbCarWash.setChecked(false);
                    }
                    else if(PrefLandmark[0].equals("Petrol")) {
                        rbServices.setChecked(false);
                        rbATM.setChecked(false);
                        rbRestaurants.setChecked(false);
                        rbPetrol.setChecked(true);
                        rbCarWash.setChecked(false);
                    }
                    else if(PrefLandmark[0].equals("CarWash")) {
                        rbServices.setChecked(false);
                        rbATM.setChecked(false);
                        rbRestaurants.setChecked(false);
                        rbPetrol.setChecked(false);
                        rbCarWash.setChecked(true);
                    }
                }
                else {
                    rbServices.setChecked(true);
                }

                progressBarSettings.setVisibility(RelativeLayout.INVISIBLE);
                tvTop.setVisibility(View.VISIBLE);
                tvBottom.setVisibility(View.VISIBLE);
                rbKM.setVisibility(View.VISIBLE);
                rbMiles.setVisibility(View.VISIBLE);
                rbServices.setVisibility(View.VISIBLE);
                rbATM.setVisibility(View.VISIBLE);
                rbRestaurants.setVisibility(View.VISIBLE);
                rbPetrol.setVisibility(View.VISIBLE);
                rbCarWash.setVisibility(View.VISIBLE);
                btnSettings.setVisibility(View.VISIBLE);

            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        query.addListenerForSingleValueEvent(valueEventListener);

    }
}
