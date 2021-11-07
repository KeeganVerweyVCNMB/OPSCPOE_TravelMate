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

public class planRoute extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    SearchView searchStart, searchDestination;
    ProgressBar progressBarPlanMap;
    Location currentLocation;
    FusedLocationProviderClient fusedLocationProviderClient;
    private static final int REQUEST_CODE = 101;
    private String TAG = "so47492459";
    Address addressStart, addressDestination;
    DatabaseReference dbRef;
    DatabaseReference fRef;
    String emailLocally;
    List<Address> addressListStart = null;
    List<Address> addressListDestination = null;
    LatLng start, end;
    String MetricSetting[];
    String PrefLandmark[];
    String endLocation;
    TextView tvDuration, tvDistance;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.plan_route);

        progressBarPlanMap = (ProgressBar) findViewById(R.id.progressBarPlanMap);
        //stackoverflow.com. 2021. Android progress bar not hiding: Android Tutorial.
        progressBarPlanMap.setVisibility(RelativeLayout.VISIBLE);

        //Disable dark mode settings
        //stackoverflow.com. 2021. How to disable night mode in my application even if night mode is enabled in andr: Android Tutorial.
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        //Setting screen orientation
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        //Calling action bar and setting page description
        getSupportActionBar().setTitle("Plan Route");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        SharedPreferences settingsMail = getApplicationContext().getSharedPreferences("Email", 0);
        emailLocally = settingsMail.getString("Email", "email");

        tvDistance = findViewById(R.id.tvDistance);
        tvDuration = findViewById(R.id.tvTime);

        tvDistance.setVisibility(View.INVISIBLE);
        tvDuration.setVisibility(View.INVISIBLE);

        searchDestination = findViewById(R.id.idEndLocation);
        searchStart = findViewById(R.id.idStartLocation);
        searchDestination.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return SearchInitiate(query);
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        searchStart.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return SearchInitiate(query);
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        fetchLocation();
    }

    private boolean SearchInitiate(String query) {
        mMap.clear();

        LatLng latLng = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
        MarkerOptions markerOptions = new MarkerOptions().position(latLng).title("My Position").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));

        mMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 5));

        String startLocation = searchStart.getQuery().toString();
        endLocation = searchDestination.getQuery().toString();

        if (startLocation != null && !startLocation.equals("") && endLocation != null && !endLocation.equals("")) {
            Geocoder geocoder = new Geocoder(planRoute.this);
            try {
                addressListStart = geocoder.getFromLocationName(startLocation, 1);
                addressListDestination = geocoder.getFromLocationName(endLocation, 1);

                if (addressListStart != null && addressListStart.size() > 0 && addressListDestination != null && addressListDestination.size() > 0) {
                    addressDestination = addressListDestination.get(0);
                    addressStart = addressListStart.get(0);

                    if (addressStart != null && addressDestination != null) {
                        start = new LatLng(addressStart.getLatitude(), addressStart.getLongitude());
                        mMap.addMarker(new MarkerOptions().position(start).title(startLocation));

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

                                progressBarPlanMap.setVisibility(RelativeLayout.VISIBLE);
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

                                        if (PrefLandmark[0].equals("Services")) {

                                            mMap.addMarker(markerOptions);

                                            PlacesSearchResult[] servicesSearchResults = new planRoute.nearbyCarRepair().run().results;

                                            if (servicesSearchResults.length > 0) {

                                                for (int i = 0; i < servicesSearchResults.length; i++) {
                                                    double lat1 = servicesSearchResults[i].geometry.location.lat;
                                                    double lng1 = servicesSearchResults[i].geometry.location.lng;

                                                    mMap.addMarker(new MarkerOptions().position(new LatLng(lat1, lng1)).title(servicesSearchResults[i].name));
                                                }
                                            }
                                            else {
                                                Toast.makeText(getApplicationContext(), "No Nearby Car Services", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                        else if (PrefLandmark[0].equals("ATM")) {

                                            mMap.addMarker(markerOptions);

                                            PlacesSearchResult[] atmSearchResults = new planRoute.nearbyATM().run().results;

                                            if (atmSearchResults.length > 0) {

                                                for (int i = 0; i < atmSearchResults.length; i++) {
                                                    double lat1 = atmSearchResults[i].geometry.location.lat;
                                                    double lng1 = atmSearchResults[i].geometry.location.lng;

                                                    mMap.addMarker(new MarkerOptions().position(new LatLng(lat1, lng1)).title(atmSearchResults[i].name));
                                                }
                                            }
                                            else {
                                                Toast.makeText(getApplicationContext(), "No Nearby ATM's", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                        else if (PrefLandmark[0].equals("Restaurants")) {

                                            mMap.addMarker(markerOptions);

                                            PlacesSearchResult[] restaurantSearchResults = new planRoute.nearbyRestaurant().run().results;

                                            if (restaurantSearchResults.length > 0) {

                                                for (int i = 0; i < restaurantSearchResults.length; i++) {
                                                    double lat1 = restaurantSearchResults[i].geometry.location.lat;
                                                    double lng1 = restaurantSearchResults[i].geometry.location.lng;

                                                    mMap.addMarker(new MarkerOptions().position(new LatLng(lat1, lng1)).title(restaurantSearchResults[i].name));
                                                }
                                            }
                                            else {
                                                Toast.makeText(getApplicationContext(), "No Nearby Restaurants", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                        else if (PrefLandmark[0].equals("Petrol")) {

                                            mMap.addMarker(markerOptions);

                                            PlacesSearchResult[] petrolSearchResults = new planRoute.nearbyPetrol().run().results;

                                            if (petrolSearchResults.length > 0) {

                                                for (int i = 0; i < petrolSearchResults.length; i++) {
                                                    double lat1 = petrolSearchResults[i].geometry.location.lat;
                                                    double lng1 = petrolSearchResults[i].geometry.location.lng;

                                                    mMap.addMarker(new MarkerOptions().position(new LatLng(lat1, lng1)).title(petrolSearchResults[i].name));
                                                }

                                            }
                                            else {
                                                Toast.makeText(getApplicationContext(), "No Nearby Petrol Stations", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                        else if (PrefLandmark[0].equals("CarWash")) {

                                            mMap.addMarker(markerOptions);

                                            PlacesSearchResult[] carWashSearchResults = new planRoute.nearbyCarWash().run().results;

                                            if (carWashSearchResults.length > 0) {

                                                for (int i = 0; i < carWashSearchResults.length; i++) {
                                                    double lat1 = carWashSearchResults[i].geometry.location.lat;
                                                    double lng1 = carWashSearchResults[i].geometry.location.lng;

                                                    mMap.addMarker(new MarkerOptions().position(new LatLng(lat1, lng1)).title(carWashSearchResults[i].name));
                                                }

                                            }
                                            else {
                                                Toast.makeText(getApplicationContext(), "No Nearby Car Washes", Toast.LENGTH_SHORT).show();
                                            }
                                        }

                                        progressBarPlanMap.setVisibility(RelativeLayout.INVISIBLE);
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
                    } else {
                        Toast.makeText(getApplicationContext(), "Please provide routes", Toast.LENGTH_LONG).show();
                    }

                } else {
                    Toast.makeText(getApplicationContext(), "Destination not Found", Toast.LENGTH_LONG).show();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return false;
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
                    SupportMapFragment supportMapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mapPlanRoute);
                    assert supportMapFragment != null;
                    supportMapFragment.getMapAsync(planRoute.this);
                }
            }
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        progressBarPlanMap.setVisibility(RelativeLayout.INVISIBLE);
        mMap = googleMap;

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
          return;
        }
        mMap.setMyLocationEnabled(true);
        LatLng latLng = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
        MarkerOptions markerOptions = new MarkerOptions().position(latLng).title("My Position").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
        mMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 5));
        mMap.addMarker(markerOptions);
    }

    public class nearbyCarRepair {
        public PlacesSearchResponse run() {

            PlacesSearchResponse request = new PlacesSearchResponse();
            GeoApiContext context = new GeoApiContext.Builder()
                    .apiKey("AIzaSyAX4mrqRb56cAUUKypTAiOGJUgmam-nKQ8")
                    .build();

            com.google.maps.model.LatLng location = new com.google.maps.model.LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
            Toast.makeText(getApplicationContext(), "Nearby Car Repair", Toast.LENGTH_SHORT).show();

            try {
                request = PlacesApi.nearbySearchQuery(context, location)
                        .radius(50000)
                        .rankby(RankBy.PROMINENCE)
                        .keyword("cruise")
                        .language("en")
                        .type(PlaceType.CAR_REPAIR)
                        .await();
            } catch (ApiException | IOException | InterruptedException e) {
                e.printStackTrace();
            } finally {
                return request;
            }
        }
    }

    public class nearbyCarWash {
        public PlacesSearchResponse run(){

            PlacesSearchResponse request = new PlacesSearchResponse();
            GeoApiContext context = new GeoApiContext.Builder()
                    .apiKey("AIzaSyAX4mrqRb56cAUUKypTAiOGJUgmam-nKQ8")
                    .build();

            com.google.maps.model.LatLng location = new com.google.maps.model.LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
            Toast.makeText(getApplicationContext(), "Nearby Car Wash", Toast.LENGTH_SHORT).show();

            try {
                request = PlacesApi.nearbySearchQuery(context, location)
                        .radius(50000)
                        .rankby(RankBy.PROMINENCE)
                        .keyword("cruise")
                        .language("en")
                        .type(PlaceType.CAR_WASH)
                        .await();
            }
            catch (ApiException | IOException | InterruptedException e) {
                e.printStackTrace();
            }
            finally {
                return request;
            }
        }
    }

    public class nearbyPetrol {
        public PlacesSearchResponse run(){

            PlacesSearchResponse request = new PlacesSearchResponse();
            GeoApiContext context = new GeoApiContext.Builder()
                    .apiKey("AIzaSyAX4mrqRb56cAUUKypTAiOGJUgmam-nKQ8")
                    .build();

            com.google.maps.model.LatLng location = new com.google.maps.model.LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
            Toast.makeText(getApplicationContext(), "Nearby Petrol Stations", Toast.LENGTH_SHORT).show();

            try {
                request = PlacesApi.nearbySearchQuery(context, location)
                        .radius(50000)
                        .rankby(RankBy.PROMINENCE)
                        .keyword("cruise")
                        .language("en")
                        .type(PlaceType.GAS_STATION)
                        .await();
            }
            catch (ApiException | IOException | InterruptedException e) {
                e.printStackTrace();
            }
            finally {
                return request;
            }
        }
    }

    public class nearbyATM {
        public PlacesSearchResponse run(){

            PlacesSearchResponse request = new PlacesSearchResponse();
            GeoApiContext context = new GeoApiContext.Builder()
                    .apiKey("AIzaSyAX4mrqRb56cAUUKypTAiOGJUgmam-nKQ8")
                    .build();

            com.google.maps.model.LatLng location = new com.google.maps.model.LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
            Toast.makeText(getApplicationContext(), "Nearby ATM's", Toast.LENGTH_SHORT).show();

            try {
                request = PlacesApi.nearbySearchQuery(context, location)
                        .radius(50000)
                        .rankby(RankBy.PROMINENCE)
                        .keyword("cruise")
                        .language("en")
                        .type(PlaceType.ATM)
                        .await();
            }
            catch (ApiException | IOException | InterruptedException e) {
                e.printStackTrace();
            }
            finally {
                return request;
            }
        }
    }

    public class nearbyRestaurant {
        public PlacesSearchResponse run(){

            PlacesSearchResponse request = new PlacesSearchResponse();
            GeoApiContext context = new GeoApiContext.Builder()
                    .apiKey("AIzaSyAX4mrqRb56cAUUKypTAiOGJUgmam-nKQ8")
                    .build();

            com.google.maps.model.LatLng location = new com.google.maps.model.LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
            Toast.makeText(getApplicationContext(), "Nearby Restaurants", Toast.LENGTH_SHORT).show();

            try {
                request = PlacesApi.nearbySearchQuery(context, location)
                        .radius(50000)
                        .rankby(RankBy.PROMINENCE)
                        .keyword("cruise")
                        .language("en")
                        .type(PlaceType.RESTAURANT)
                        .await();
            }
            catch (ApiException | IOException | InterruptedException e) {
                e.printStackTrace();
            }
            finally {
                return request;
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.plan_map, menu);
        return super.onCreateOptionsMenu(menu);
    }

    //Handle button activities
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.btnPlanMap) {
            if(start != null && end != null) {
                //Generating random number
                int max = 1000000;
                int min = 0;

                Random randomNum = new Random();
                int generatedID = min + randomNum.nextInt(max);

                String mapID = "SavedMapID-" + generatedID;

                dbRef = FirebaseDatabase.getInstance().getReference("SavedMap");
                dbRef.child(mapID).setValue(mapID);

                dbRef = FirebaseDatabase.getInstance().getReference("SavedMap");
                dbRef.child(mapID).child("LatLong").setValue(end.latitude + "," + end.longitude);

                dbRef = FirebaseDatabase.getInstance().getReference("SavedMap");
                dbRef.child(mapID).child("Email").setValue(emailLocally);

                dbRef = FirebaseDatabase.getInstance().getReference("SavedMap");
                dbRef.child(mapID).child("DestinationTitle").setValue(endLocation);

                Toast.makeText(getApplicationContext(), "Map Successfully Saved", Toast.LENGTH_LONG).show();
            }
            else {
                Toast.makeText(getApplicationContext(), "Please provide routes before trying to save", Toast.LENGTH_LONG).show();
            }
        }
        return super.onOptionsItemSelected(item);
    }
}
