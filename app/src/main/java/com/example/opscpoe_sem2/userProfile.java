package com.example.opscpoe_sem2;


import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class userProfile extends AppCompatActivity {
    //Setting Variables
    EditText prfName, prfSurname, prfEmail, prfAddress;
    TextView txtName, txtSurname, txtEmail, txtAddress;
    DatabaseReference dbRef;
    DatabaseReference fRef;
    ProgressBar progressBarProfile;
    String ProfileName[];
    String ProfileSurname[];
    String ProfileAddress[];
    String ProfileEmail[];
    String myParentNode, emailLocally, passLocally;
    ImageView profileImg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_profile);

        prfName = findViewById(R.id.txtNameProfile);
        prfSurname = findViewById(R.id.txtSurnameProfile);
        prfEmail = findViewById(R.id.txtEmailProfile);
        prfAddress = findViewById(R.id.txtAddressProfile);

        txtName = findViewById(R.id.tvName);
        txtSurname = findViewById(R.id.tvSurname);
        txtEmail = findViewById(R.id.tvEmail);
        txtAddress = findViewById(R.id.tvAddress);

        final Button update = (Button) findViewById(R.id.btnConfirmUpdateProfile);

        profileImg = (ImageView) findViewById(R.id.imgProfile);

        prfName.setVisibility(View.INVISIBLE);
        prfSurname.setVisibility(View.INVISIBLE);
        prfEmail.setVisibility(View.INVISIBLE);
        prfAddress.setVisibility(View.INVISIBLE);
        txtName.setVisibility(View.INVISIBLE);
        txtSurname.setVisibility(View.INVISIBLE);
        txtEmail.setVisibility(View.INVISIBLE);
        txtAddress.setVisibility(View.INVISIBLE);
        profileImg.setVisibility(View.INVISIBLE);
        update.setVisibility(View.INVISIBLE);

        //Disable dark mode settings
        //stackoverflow.com. 2021. How to disable night mode in my application even if night mode is enabled in andr: Android Tutorial.
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        //Setting screen orientation
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        //Calling action bar and setting page description
        getSupportActionBar().setTitle("My Travel Mate Profile");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        progressBarProfile = (ProgressBar)findViewById(R.id.progressBarProfile);
        //stackoverflow.com. 2021. Android progress bar not hiding: Android Tutorial.
        progressBarProfile.setVisibility(RelativeLayout.VISIBLE);

        SharedPreferences settingsMail = getApplicationContext().getSharedPreferences("Email", 0);
        emailLocally = settingsMail.getString("Email", "email");

        SharedPreferences settingsPass = getApplicationContext().getSharedPreferences("Pass", 0);
        passLocally = settingsPass.getString("Pass", "password");

        update.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v)
            {
                String name = prfName.getText().toString();
                String surname = prfSurname.getText().toString();
                String email = emailLocally;
                String address = prfAddress.getText().toString();
                String password = passLocally;

                if(TextUtils.isEmpty(name)) {
                    prfName.setError("Name required");
                    return;
                }
                else if(TextUtils.isEmpty(surname)) {
                    prfSurname.setError("Surname required");
                    return;
                }
                else if(TextUtils.isEmpty(address)) {
                    prfAddress.setError("Address required");
                    return;
                }
                else {
                    dbRef.child(myParentNode).removeValue();

                    //Generating random number
                    int max = 1000000;
                    int min = 0;

                    Random randomNum = new Random();
                    int generatedID = min + randomNum.nextInt(max);

                    String userID = "UserID-" + generatedID;

                    String passwordToHash = password;
                    String generatedPassword = null;
                    try {
                        MessageDigest md = MessageDigest.getInstance("MD5");
                        md.update(passwordToHash.getBytes());
                        byte[] bytes = md.digest();
                        StringBuilder sb = new StringBuilder();
                        for(int i=0; i< bytes.length ;i++)
                        {
                            sb.append(Integer.toString((bytes[i] & 0xff) + 0x100, 16).substring(1));
                        }
                        generatedPassword = sb.toString();
                    }
                    catch (NoSuchAlgorithmException e)
                    {
                        e.printStackTrace();
                    }

                    dbRef = FirebaseDatabase.getInstance().getReference("Users");
                    dbRef.child(userID).setValue(userID);

                    dbRef = FirebaseDatabase.getInstance().getReference("Users");
                    dbRef.child(userID).child("Name").setValue(name);

                    dbRef = FirebaseDatabase.getInstance().getReference("Users");
                    dbRef.child(userID).child("Surname").setValue(surname);

                    dbRef = FirebaseDatabase.getInstance().getReference("Users");
                    dbRef.child(userID).child("Address").setValue(address);

                    dbRef = FirebaseDatabase.getInstance().getReference("Users");
                    dbRef.child(userID).child("Email").setValue(email);

                    dbRef = FirebaseDatabase.getInstance().getReference("Users");
                    dbRef.child(userID).child("Password").setValue(generatedPassword);

                    startActivity(new Intent(userProfile.this, dashboard.class));
                    Toast.makeText(userProfile.this, "Profile Successfully Updated", Toast.LENGTH_LONG).show();

                }

            }
        });

        dbRef = FirebaseDatabase.getInstance().getReference();
        fRef = dbRef.child("Users");
        Query query = fRef.orderByChild("Email").equalTo(emailLocally);
        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ProfileName = new String[(int)dataSnapshot.getChildrenCount()];
                ProfileSurname = new String[(int)dataSnapshot.getChildrenCount()];
                ProfileAddress = new String[(int)dataSnapshot.getChildrenCount()];
                ProfileEmail = new String[(int)dataSnapshot.getChildrenCount()];
                int index = 0;
                for(DataSnapshot ds : dataSnapshot.getChildren()) {
                    Map mapStock = new HashMap();
                    mapStock = (HashMap) ds.getValue();
                    myParentNode = dataSnapshot.getKey();
                    ProfileName[index] = mapStock.get("Name").toString();
                    ProfileSurname[index] = mapStock.get("Surname").toString();
                    ProfileAddress[index] = mapStock.get("Address").toString();
                    ProfileEmail[index] = mapStock.get("Email").toString();
                    index++;
                }
                prfName.setText(ProfileName[0]);
                prfSurname.setText(ProfileSurname[0]);
                prfAddress.setText(ProfileAddress[0]);
                prfEmail.setText(ProfileEmail[0]);

                prfName.setVisibility(View.VISIBLE);
                prfSurname.setVisibility(View.VISIBLE);
                prfEmail.setVisibility(View.VISIBLE);
                prfAddress.setVisibility(View.VISIBLE);

                txtName.setVisibility(View.VISIBLE);
                txtSurname.setVisibility(View.VISIBLE);
                txtEmail.setVisibility(View.VISIBLE);
                txtAddress.setVisibility(View.VISIBLE);

                profileImg.setVisibility(View.VISIBLE);
                update.setVisibility(View.VISIBLE);

                progressBarProfile.setVisibility(RelativeLayout.VISIBLE);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        query.addListenerForSingleValueEvent(valueEventListener);
    }
}
