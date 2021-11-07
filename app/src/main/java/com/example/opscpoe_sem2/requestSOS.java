package com.example.opscpoe_sem2;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import java.util.ArrayList;
import java.util.HashMap;

public class requestSOS extends AppCompatActivity {
    private ListView sosListView;
    ArrayAdapter<String> myAdapter;
    EditText inputSearch;
    ImageView imgSOS;
    double longitude;
    double latitude;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.request_sos);

        LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        Location location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        longitude = location.getLongitude();
        latitude = location.getLatitude();

        imgSOS = (ImageView) findViewById(R.id.imgSOSMain);
        Animation pulse = AnimationUtils.loadAnimation(this, R.anim.sos_pulse);
        imgSOS.startAnimation(pulse);

        imgSOS.setOnClickListener(new View.OnClickListener() {
            //@Override
            public void onClick(View v) {
                String uriText =
                        "mailto:travelMateSOS@gmail.com" +
                                "?subject=" + Uri.encode("Travel Mate SOS") +
                                "&body=" + Uri.encode("This is a Travel Mate SOS signal. \n\n Please send out SOS rescue services!\n" +
                                " http://maps.google.com/maps?saddr=" + latitude + "," + longitude
                                + "\n\n Thank You! \nTravel Mate User Emergency");

                Uri uri = Uri.parse(uriText);
                Intent sendIntent = new Intent(Intent.ACTION_SENDTO);
                sendIntent.setData(uri);
                startActivity(Intent.createChooser(sendIntent, "Send email"));
            }
        });

        //Disable dark mode settings
        //stackoverflow.com. 2021. How to disable night mode in my application even if night mode is enabled in andr: Android Tutorial.
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        //Setting screen orientation
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        //Calling action bar and setting page description
        getSupportActionBar().setTitle("Request SOS");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        String sosTypes[] = {"SA Police Dept. TEL - 10111", "SA Sea Rescue TEL - 082 990 5980", "SA Ambulance Service TEL - 021 551 2999", "SA Fire Dept. TEL - 10111",
                "US Police Dept. TEL - 102", "US Sea Rescue TEL - +1 786-777-0775", "US Ambulance Service TEL - 103", "US Fire Dept. TEL - 101",
                "Europe Police Dept. TEL - 112", "Europe Sea Rescue TEL - 112", "Europe Ambulance Service TEL - 112", "Europe Fire Dept. TEL - 112"};

        sosListView = (ListView) findViewById(R.id.editListView);
        inputSearch = (EditText) findViewById(R.id.itemSearch);

        sosListView.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                if(sosTypes[position] == "SA Police Dept. TEL - 10111"){
                    Intent intent = new Intent(Intent.ACTION_DIAL);
                    intent.setData(Uri.parse("tel:10111"));
                    startActivity(intent);
                }
                else if(sosTypes[position] == "SA Sea Rescue TEL - 082 990 5980"){
                    Intent intent = new Intent(Intent.ACTION_DIAL);
                    intent.setData(Uri.parse("tel:0829905980"));
                    startActivity(intent);
                }
                else if(sosTypes[position] == "SA Ambulance Service TEL - 021 551 2999"){
                    Intent intent = new Intent(Intent.ACTION_DIAL);
                    intent.setData(Uri.parse("tel:0215512999"));
                    startActivity(intent);
                }
                else if(sosTypes[position] == "SA Fire Dept. TEL - 10111"){
                    Intent intent = new Intent(Intent.ACTION_DIAL);
                    intent.setData(Uri.parse("tel:10111"));
                    startActivity(intent);
                }
                else if(sosTypes[position] == "US Police Dept. TEL - 102"){
                    Intent intent = new Intent(Intent.ACTION_DIAL);
                    intent.setData(Uri.parse("tel:102"));
                    startActivity(intent);
                }
                else if(sosTypes[position] == "US Sea Rescue TEL - +1 786-777-0775"){
                    Intent intent = new Intent(Intent.ACTION_DIAL);
                    intent.setData(Uri.parse("tel:7867770775"));
                    startActivity(intent);
                }
                else if(sosTypes[position] == "US Ambulance Service TEL - 103"){
                    Intent intent = new Intent(Intent.ACTION_DIAL);
                    intent.setData(Uri.parse("tel:103"));
                    startActivity(intent);
                }
                else if(sosTypes[position] == "US Fire Dept. TEL - 101"){
                    Intent intent = new Intent(Intent.ACTION_DIAL);
                    intent.setData(Uri.parse("tel:101"));
                    startActivity(intent);
                }
                else if(sosTypes[position] == "Europe Police Dept. TEL - 112"){
                    Intent intent = new Intent(Intent.ACTION_DIAL);
                    intent.setData(Uri.parse("tel:112"));
                    startActivity(intent);
                }
                else if(sosTypes[position] == "Europe Sea Rescue TEL - 112"){
                    Intent intent = new Intent(Intent.ACTION_DIAL);
                    intent.setData(Uri.parse("tel:112"));
                    startActivity(intent);
                }
                else if(sosTypes[position] == "Europe Ambulance Service TEL - 112"){
                    Intent intent = new Intent(Intent.ACTION_DIAL);
                    intent.setData(Uri.parse("tel:112"));
                    startActivity(intent);
                }
                else if(sosTypes[position] == "Europe Fire Dept. TEL - 112"){
                    Intent intent = new Intent(Intent.ACTION_DIAL);
                    intent.setData(Uri.parse("tel:11120111"));
                    startActivity(intent);
                }
            }
        });

        myAdapter = new ArrayAdapter<String>(this, R.layout.sos_listsearch, R.id.sos_name, sosTypes);
        sosListView.setAdapter(myAdapter);

        inputSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence cs, int arg1, int arg2, int arg3) {
                requestSOS.this.myAdapter.getFilter().filter(cs);
            }
            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
            }
            @Override
            public void afterTextChanged(Editable arg0) {
            }
        });
    }
}
