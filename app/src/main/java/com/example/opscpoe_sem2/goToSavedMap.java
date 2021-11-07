package com.example.opscpoe_sem2;

import android.Manifest;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.SearchView;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.maps.DirectionsApi;
import com.google.maps.DirectionsApiRequest;
import com.google.maps.GeoApiContext;
import com.google.maps.PlacesApi;
import com.google.maps.android.SphericalUtil;
import com.google.maps.errors.ApiException;
import com.google.maps.model.DirectionsLeg;
import com.google.maps.model.DirectionsResult;
import com.google.maps.model.DirectionsRoute;
import com.google.maps.model.DirectionsStep;
import com.google.maps.model.EncodedPolyline;
import com.google.maps.model.PlaceType;
import com.google.maps.model.PlacesSearchResponse;
import com.google.maps.model.PlacesSearchResult;
import com.google.maps.model.RankBy;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class goToSavedMap extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    ProgressBar progressBarSavedRouteMap;
    Location currentLocation;
    FusedLocationProviderClient fusedLocationProviderClient;
    private static final int REQUEST_CODE = 101;
    private String TAG = "so47492459";
    Address addressDestination;
    DatabaseReference dbRef;
    DatabaseReference fRef;
    String emailLocally, title;
    List<Address> addressListDestination = null;
    LatLng start, end;
    String MetricSetting[];
    String PrefLandmark[];
    String endLocation;
    TextView tvDuration, tvDistance;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.go_saved_map);

        progressBarSavedRouteMap = (ProgressBar) findViewById(R.id.progressBarSavedRouteMap);
        //stackoverflow.com. 2021. Android progress bar not hiding: Android Tutorial.
        progressBarSavedRouteMap.setVisibility(RelativeLayout.VISIBLE);

        //Disable dark mode settings
        //stackoverflow.com. 2021. How to disable night mode in my application even if night mode is enabled in andr: Android Tutorial.
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        //Setting screen orientation
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        //Calling action bar and setting page description
        getSupportActionBar().setTitle("Saved Route Waypoint");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        SharedPreferences settingsMail = getApplicationContext().getSharedPreferences("Email", 0);
        emailLocally = settingsMail.getString("Email", "email");
        SharedPreferences settingsTitle = getApplicationContext().getSharedPreferences("Title", 0);
        title = settingsTitle.getString("Title", "title");

        tvDistance = findViewById(R.id.tvDistanceSaved);
        tvDuration = findViewById(R.id.tvTimeSaved);

        tvDistance.setVisibility(View.INVISIBLE);
        tvDuration.setVisibility(View.INVISIBLE);

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        fetchLocation();
    }

    private void fetchLocation() {
        if (ActivityCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE);
            return;
        }
        Task<Location> task = fusedLocationProviderClient.getLastLocation();
        task.addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location != null) {
                    currentLocation = location;
                    SupportMapFragment supportMapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mapSavedRoute);
                    assert supportMapFragment != null;
                    supportMapFragment.getMapAsync(goToSavedMap.this);
                }
            }
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        progressBarSavedRouteMap.setVisibility(RelativeLayout.INVISIBLE);
        mMap = googleMap;
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mMap.setMyLocationEnabled(true);

        endLocation = title;

        if (endLocation != null && !endLocation.equals("")) {
            Geocoder geocoder = new Geocoder(goToSavedMap.this);
            try {
                addressListDestination = geocoder.getFromLocationName(endLocation, 1);

                if (addressListDestination != null && addressListDestination.size() > 0) {
                    addressDestination = addressListDestination.get(0);

                    if (addressDestination != null) {
                        start = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
                        mMap.addMarker(new MarkerOptions().position(start).title("My Location"));

                        end = new LatLng(addressDestination.getLatitude(), addressDestination.getLongitude());
                        mMap.addMarker(new MarkerOptions().position(end).title(endLocation));

                        List<LatLng> path = new ArrayList();

                        //Execute Directions API request
                        GeoApiContext context = new GeoApiContext.Builder()
                                .apiKey("AIzaSyAX4mrqRb56cAUUKypTAiOGJUgmam-nKQ8")
                                .build();
                        DirectionsApiRequest req = DirectionsApi.getDirections(context, start.latitude + "," + start.longitude, end.latitude + "," + end.longitude);
                        try {
                            DirectionsResult res = req.await();

                            if (res.routes != null && res.routes.length > 0) {
                                DirectionsRoute route = res.routes[0];

                                if (route.legs != null) {
                                    for (int i = 0; i < route.legs.length; i++) {
                                        DirectionsLeg leg = route.legs[i];
                                        if (leg.steps != null) {
                                            for (int j = 0; j < leg.steps.length; j++) {
                                                DirectionsStep step = leg.steps[j];
                                                if (step.steps != null && step.steps.length > 0) {
                                                    for (int k = 0; k < step.steps.length; k++) {
                                                        DirectionsStep stepOne = step.steps[k];
                                                        EncodedPolyline pointOne = stepOne.polyline;
                                                        if (pointOne != null) {
                                                            List<com.google.maps.model.LatLng> coords1 = pointOne.decodePath();
                                                            for (com.google.maps.model.LatLng coord1 : coords1) {
                                                                path.add(new LatLng(coord1.lat, coord1.lng));
                                                            }
                                                        }
                                                    }
                                                } else {
                                                    EncodedPolyline points = step.polyline;
                                                    if (points != null) {
                                                        List<com.google.maps.model.LatLng> coords = points.decodePath();
                                                        for (com.google.maps.model.LatLng coord : coords) {
                                                            path.add(new LatLng(coord.lat, coord.lng));
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }

                                progressBarSavedRouteMap.setVisibility(RelativeLayout.VISIBLE);
                                dbRef = FirebaseDatabase.getInstance().getReference();
                                fRef = dbRef.child("MySettings");
                                Query queryAttr = fRef.orderByChild("Email").equalTo(emailLocally);
                                ValueEventListener valueEventListener = new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        MetricSetting = new String[(int)dataSnapshot.getChildrenCount()];
                                        PrefLandmark = new String[(int)dataSnapshot.getChildrenCount()];
                                        for(DataSnapshot ds : dataSnapshot.getChildren()) {
                                            Map mapStock = new HashMap();
                                            mapStock = (HashMap) ds.getValue();
                                            MetricSetting[0] = mapStock.get("MapSettings").toString();
                                            PrefLandmark[0] = mapStock.get("PreferredLandmark").toString();
                                        }

                                        progressBarSavedRouteMap.setVisibility(RelativeLayout.INVISIBLE);
                                        tvDistance.setVisibility(View.VISIBLE);
                                        tvDuration.setVisibility(View.VISIBLE);

                                        Double distance = SphericalUtil.computeDistanceBetween(start, end);
                                        Double miles = distance / 1000 / 1.609;

                                        Double time = distance / 1000 / 100;
                                        String hours = new DecimalFormat("#").format(time);
                                        Double minutes = time - Double.parseDouble(hours);

                                        if(minutes > 1)
                                        {
                                            tvDuration.setText("Time to Destination: " + hours + " H : " + String.format("%.2f", minutes) + " M");
                                        }
                                        else {
                                            tvDuration.setText("Time to Destination: " + hours + " H");
                                        }

                                        if(MetricSetting[0].equals("KM")) {
                                            if (distance > 1000) {
                                                tvDistance.setText("Distance to Destination: " + String.format("%.2f", distance / 1000) + " KM");
                                            }
                                            else {
                                                tvDistance.setText("Distance to Destination: " + String.format("%.2f", distance / 1000) + "  M");
                                            }
                                        }
                                        else if(MetricSetting[0].equals("Miles")) {
                                            if (distance > 1.60934) {
                                                tvDistance.setText("Distance to Destination: " + String.format("%.2f", miles) + " Miles");
                                            }
                                            else {
                                                tvDistance.setText("Distance to Destination: " + String.format("%.2f", miles) + "  M");
                                            }
                                        }
                                    }
                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                };
                                queryAttr.addListenerForSingleValueEvent(valueEventListener);

                            }
                        } catch (Exception ex) {
                            Log.e(TAG, ex.getLocalizedMessage());
                        }

                        if (path.size() > 0) {
                            PolylineOptions opts = new PolylineOptions().addAll(path).color(Color.BLUE).width(5);
                            mMap.addPolyline(opts);
                        }

                        mMap.getUiSettings().setZoomControlsEnabled(true);
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(start, 6));
                    }
                    else {
                        Toast.makeText(getApplicationContext(), "Route not found", Toast.LENGTH_LONG).show();
                    }

                } else {
                    Toast.makeText(getApplicationContext(), "Route not Found", Toast.LENGTH_LONG).show();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }
}
