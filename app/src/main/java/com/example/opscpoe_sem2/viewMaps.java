package com.example.opscpoe_sem2;

import android.Manifest;
import android.app.ActionBar;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.SearchView;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

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
import com.google.maps.GeoApiContext;
import com.google.maps.PlacesApi;
import com.google.maps.errors.ApiException;
import com.google.maps.model.PlaceType;
import com.google.maps.model.PlacesSearchResponse;
import com.google.maps.model.PlacesSearchResult;
import com.google.maps.model.RankBy;

import java.io.IOException;
import java.util.List;

public class viewMaps extends AppCompatActivity implements OnMapReadyCallback {
    Location currentLocation;
    FusedLocationProviderClient fusedLocationProviderClient;
    private static final int REQUEST_CODE = 101;
    SearchView searchView;
    private GoogleMap mMap;
    ProgressBar progressBarMap;
    Spinner dropdown;
    MarkerOptions markerOptions;
    LatLng latLng;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_maps);

        progressBarMap = (ProgressBar) findViewById(R.id.progressBarMap);
        //stackoverflow.com. 2021. Android progress bar not hiding: Android Tutorial.
        progressBarMap.setVisibility(RelativeLayout.VISIBLE);

        //Disable dark mode settings
        //stackoverflow.com. 2021. How to disable night mode in my application even if night mode is enabled in andr: Android Tutorial.
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        //Setting screen orientation
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        //Calling action bar and setting page description
        getSupportActionBar().setTitle("Map");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        dropdown = findViewById(R.id.spinnerMap);
        String[] items = new String[]{"Nearby Places", "Restaurants", "ATM's", "Police Dept.", "Fire Dept.", "Hospitals"
                , "Mall", "Petrol Stations", "Car Wash", "Car Repair", "Airport"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, items);
        dropdown.setAdapter(adapter);

        dropdown.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                String mapNearbySelected = dropdown.getSelectedItem().toString();

                if(currentLocation != null) {
                    latLng = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
                }
                if(latLng != null) {
                    markerOptions = new MarkerOptions().position(latLng).title("My Position").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
                }

                if (mapNearbySelected == "Restaurants") {

                    mMap.clear();
                    mMap.addMarker(markerOptions);

                    PlacesSearchResult[] restaurantsSearchResults = new nearbyRestaurant().run().results;

                    if (restaurantsSearchResults.length > 0) {
                        mMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
                        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 5));
                        mMap.addMarker(markerOptions);

                        for (int i = 0; i < restaurantsSearchResults.length; i++) {
                            double lat1 = restaurantsSearchResults[i].geometry.location.lat;
                            double lng1 = restaurantsSearchResults[i].geometry.location.lng;

                            mMap.addMarker(new MarkerOptions().position(new LatLng(lat1, lng1)).title(restaurantsSearchResults[i].name));
                            mMap.setMinZoomPreference(14.0f);
                        }

                        LatLng latLngRest = new LatLng(restaurantsSearchResults[0].geometry.location.lat, restaurantsSearchResults[0].geometry.location.lng);

                        mMap.animateCamera(CameraUpdateFactory.newLatLng(latLngRest));
                        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLngRest, 5));

                    }
                    else {
                        Toast.makeText(getApplicationContext(), "No Nearby " + mapNearbySelected, Toast.LENGTH_SHORT).show();
                        mMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
                        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 5));
                        mMap.addMarker(markerOptions);
                    }
                }
                else if (mapNearbySelected == "ATM's") {

                    mMap.clear();
                    mMap.addMarker(markerOptions);

                    PlacesSearchResult[] atmSearchResults = new nearbyATM().run().results;

                    if (atmSearchResults.length > 0) {
                        mMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
                        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 5));
                        mMap.addMarker(markerOptions);

                        for (int i = 0; i < atmSearchResults.length; i++) {
                            double lat1 = atmSearchResults[i].geometry.location.lat;
                            double lng1 = atmSearchResults[i].geometry.location.lng;

                            mMap.addMarker(new MarkerOptions().position(new LatLng(lat1, lng1)).title(atmSearchResults[i].name));
                            mMap.setMinZoomPreference(14.0f);
                        }

                        LatLng latLngAtm = new LatLng(atmSearchResults[0].geometry.location.lat, atmSearchResults[0].geometry.location.lng);

                        mMap.animateCamera(CameraUpdateFactory.newLatLng(latLngAtm));
                        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLngAtm, 5));

                    }
                    else {
                        Toast.makeText(getApplicationContext(), "No Nearby " + mapNearbySelected, Toast.LENGTH_SHORT).show();
                        mMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
                        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 5));
                        mMap.addMarker(markerOptions);
                    }
                }
                else if (mapNearbySelected == "Police Dept.") {

                    mMap.clear();
                    mMap.addMarker(markerOptions);

                    PlacesSearchResult[] policeSearchResults = new nearbyPolice().run().results;

                    if (policeSearchResults.length > 0) {
                        mMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
                        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 5));
                        mMap.addMarker(markerOptions);

                        for (int i = 0; i < policeSearchResults.length; i++) {
                            double lat1 = policeSearchResults[i].geometry.location.lat;
                            double lng1 = policeSearchResults[i].geometry.location.lng;

                            mMap.addMarker(new MarkerOptions().position(new LatLng(lat1, lng1)).title(policeSearchResults[i].name));
                            mMap.setMinZoomPreference(14.0f);
                        }

                        LatLng latLngPolice = new LatLng(policeSearchResults[0].geometry.location.lat, policeSearchResults[0].geometry.location.lng);

                        mMap.animateCamera(CameraUpdateFactory.newLatLng(latLngPolice));
                        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLngPolice, 5));

                    }
                    else {
                        Toast.makeText(getApplicationContext(), "No Nearby " + mapNearbySelected, Toast.LENGTH_SHORT).show();
                        mMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
                        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 5));
                        mMap.addMarker(markerOptions);
                    }
                }
                else if (mapNearbySelected == "Fire Dept.") {

                    mMap.clear();
                    mMap.addMarker(markerOptions);

                    PlacesSearchResult[] fireSearchResults = new nearbyFire().run().results;

                    if (fireSearchResults.length > 0) {
                        mMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
                        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 5));
                        mMap.addMarker(markerOptions);

                        for (int i = 0; i < fireSearchResults.length; i++) {
                            double lat1 = fireSearchResults[i].geometry.location.lat;
                            double lng1 = fireSearchResults[i].geometry.location.lng;

                            mMap.addMarker(new MarkerOptions().position(new LatLng(lat1, lng1)).title(fireSearchResults[i].name));
                            mMap.setMinZoomPreference(14.0f);
                        }

                        LatLng latLngFire = new LatLng(fireSearchResults[0].geometry.location.lat, fireSearchResults[0].geometry.location.lng);

                        mMap.animateCamera(CameraUpdateFactory.newLatLng(latLngFire));
                        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLngFire, 5));

                    }
                    else {
                        Toast.makeText(getApplicationContext(), "No Nearby " + mapNearbySelected, Toast.LENGTH_SHORT).show();
                        mMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
                        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 5));
                        mMap.addMarker(markerOptions);
                    }
                }
                else if (mapNearbySelected == "Hospitals") {

                    mMap.clear();
                    mMap.addMarker(markerOptions);

                    PlacesSearchResult[] hospitalSearchResults = new nearbyHospitals().run().results;

                    if (hospitalSearchResults.length > 0) {
                        mMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
                        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 5));
                        mMap.addMarker(markerOptions);

                        for (int i = 0; i < hospitalSearchResults.length; i++) {
                            double lat1 = hospitalSearchResults[i].geometry.location.lat;
                            double lng1 = hospitalSearchResults[i].geometry.location.lng;

                            mMap.addMarker(new MarkerOptions().position(new LatLng(lat1, lng1)).title(hospitalSearchResults[i].name));
                            mMap.setMinZoomPreference(14.0f);
                        }

                        LatLng latLngHosp = new LatLng(hospitalSearchResults[0].geometry.location.lat, hospitalSearchResults[0].geometry.location.lng);

                        mMap.animateCamera(CameraUpdateFactory.newLatLng(latLngHosp));
                        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLngHosp, 5));

                    }
                    else {
                        Toast.makeText(getApplicationContext(), "No Nearby " + mapNearbySelected, Toast.LENGTH_SHORT).show();
                        mMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
                        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 5));
                        mMap.addMarker(markerOptions);
                    }
                }
                else if (mapNearbySelected == "Mall") {

                    mMap.clear();
                    mMap.addMarker(markerOptions);

                    PlacesSearchResult[] mallSearchResults = new nearbyMall().run().results;

                    if (mallSearchResults.length > 0) {
                        mMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
                        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 5));
                        mMap.addMarker(markerOptions);

                        for (int i = 0; i < mallSearchResults.length; i++) {
                            double lat1 = mallSearchResults[i].geometry.location.lat;
                            double lng1 = mallSearchResults[i].geometry.location.lng;

                            mMap.addMarker(new MarkerOptions().position(new LatLng(lat1, lng1)).title(mallSearchResults[i].name));
                            mMap.setMinZoomPreference(14.0f);
                        }

                        LatLng latLngMall = new LatLng(mallSearchResults[0].geometry.location.lat, mallSearchResults[0].geometry.location.lng);

                        mMap.animateCamera(CameraUpdateFactory.newLatLng(latLngMall));
                        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLngMall, 5));

                    }
                    else {
                        Toast.makeText(getApplicationContext(), "No Nearby " + mapNearbySelected, Toast.LENGTH_SHORT).show();
                        mMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
                        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 5));
                        mMap.addMarker(markerOptions);
                    }
                }
                else if (mapNearbySelected == "Petrol Stations") {

                    mMap.clear();
                    mMap.addMarker(markerOptions);

                    PlacesSearchResult[] petrolSearchResults = new nearbyPetrol().run().results;

                    if (petrolSearchResults.length > 0) {
                        mMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
                        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 5));
                        mMap.addMarker(markerOptions);

                        for (int i = 0; i < petrolSearchResults.length; i++) {
                            double lat1 = petrolSearchResults[i].geometry.location.lat;
                            double lng1 = petrolSearchResults[i].geometry.location.lng;

                            mMap.addMarker(new MarkerOptions().position(new LatLng(lat1, lng1)).title(petrolSearchResults[i].name));
                            mMap.setMinZoomPreference(14.0f);
                        }

                        LatLng latLngPetrol = new LatLng(petrolSearchResults[0].geometry.location.lat, petrolSearchResults[0].geometry.location.lng);

                        mMap.animateCamera(CameraUpdateFactory.newLatLng(latLngPetrol));
                        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLngPetrol, 5));

                    }
                    else {
                        Toast.makeText(getApplicationContext(), "No Nearby " + mapNearbySelected, Toast.LENGTH_SHORT).show();
                        mMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
                        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 5));
                        mMap.addMarker(markerOptions);
                    }
                }
                else if (mapNearbySelected == "Car Wash") {

                    mMap.clear();
                    mMap.addMarker(markerOptions);

                    PlacesSearchResult[] carWashSearchResults = new nearbyCarWash().run().results;

                    if (carWashSearchResults.length > 0) {
                        mMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
                        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 5));
                        mMap.addMarker(markerOptions);

                        for (int i = 0; i < carWashSearchResults.length; i++) {
                            double lat1 = carWashSearchResults[i].geometry.location.lat;
                            double lng1 = carWashSearchResults[i].geometry.location.lng;

                            mMap.addMarker(new MarkerOptions().position(new LatLng(lat1, lng1)).title(carWashSearchResults[i].name));
                            mMap.setMinZoomPreference(14.0f);
                        }

                        LatLng latLngCarWash = new LatLng(carWashSearchResults[0].geometry.location.lat, carWashSearchResults[0].geometry.location.lng);

                        mMap.animateCamera(CameraUpdateFactory.newLatLng(latLngCarWash));
                        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLngCarWash, 5));

                    }
                    else {
                        Toast.makeText(getApplicationContext(), "No Nearby " + mapNearbySelected, Toast.LENGTH_SHORT).show();
                        mMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
                        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 5));
                        mMap.addMarker(markerOptions);
                    }
                }
                else if (mapNearbySelected == "Car Repair") {

                    mMap.clear();
                    mMap.addMarker(markerOptions);

                    PlacesSearchResult[] repairSearchResults = new nearbyCarRepair().run().results;

                    if (repairSearchResults.length > 0) {
                        mMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
                        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 5));
                        mMap.addMarker(markerOptions);

                        for (int i = 0; i < repairSearchResults.length; i++) {
                            double lat1 = repairSearchResults[i].geometry.location.lat;
                            double lng1 = repairSearchResults[i].geometry.location.lng;

                            mMap.addMarker(new MarkerOptions().position(new LatLng(lat1, lng1)).title(repairSearchResults[i].name));
                            mMap.setMinZoomPreference(14.0f);
                        }

                        LatLng latLngCarRep = new LatLng(repairSearchResults[0].geometry.location.lat, repairSearchResults[0].geometry.location.lng);

                        mMap.animateCamera(CameraUpdateFactory.newLatLng(latLngCarRep));
                        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLngCarRep, 5));

                    }
                    else {
                        Toast.makeText(getApplicationContext(), "No Nearby " + mapNearbySelected, Toast.LENGTH_SHORT).show();
                        mMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
                        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 5));
                        mMap.addMarker(markerOptions);
                    }
                }
                else if (mapNearbySelected == "Airport") {

                    mMap.clear();
                    mMap.addMarker(markerOptions);

                    PlacesSearchResult[] airportSearchResults = new nearbyAirport().run().results;

                    if (airportSearchResults.length > 0) {
                        mMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
                        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 5));
                        mMap.addMarker(markerOptions);

                        for (int i = 0; i < airportSearchResults.length; i++) {
                            double lat1 = airportSearchResults[i].geometry.location.lat;
                            double lng1 = airportSearchResults[i].geometry.location.lng;

                            mMap.addMarker(new MarkerOptions().position(new LatLng(lat1, lng1)).title(airportSearchResults[i].name));
                            mMap.setMinZoomPreference(14.0f);
                        }

                        LatLng latLngAirport = new LatLng(airportSearchResults[0].geometry.location.lat, airportSearchResults[0].geometry.location.lng);

                        mMap.animateCamera(CameraUpdateFactory.newLatLng(latLngAirport));
                        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLngAirport, 5));

                    }
                    else {
                        Toast.makeText(getApplicationContext(), "No Nearby " + mapNearbySelected, Toast.LENGTH_SHORT).show();
                        mMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
                        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 5));
                        mMap.addMarker(markerOptions);
                    }
                }
                else if (mapNearbySelected != null && mapNearbySelected == "Nearby Places") {
                    Toast.makeText(getApplicationContext(), "Select Nearby Waypoints", Toast.LENGTH_SHORT).show();
                }
                else if (mapNearbySelected != null) {
                    Toast.makeText(getApplicationContext(), "No Nearby " + mapNearbySelected, Toast.LENGTH_SHORT).show();
                }
                else {
                    Toast.makeText(getApplicationContext(), "Loading...", Toast.LENGTH_SHORT).show();
                }
            }

            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        searchView = findViewById(R.id.idSearchView);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                String location = searchView.getQuery().toString();

                List<Address> addressList = null;

                if (location != null || location.equals("")) {
                    Geocoder geocoder = new Geocoder(viewMaps.this);
                    try {
                        addressList = geocoder.getFromLocationName(location, 1);

                        if(addressList != null && addressList.size() > 0) {
                            Address address = addressList.get(0);

                            if(address != null)
                            {
                                LatLng latLng = new LatLng(address.getLatitude(), address.getLongitude());
                                mMap.addMarker(new MarkerOptions().position(latLng).title(location));
                                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 10));
                            }
                            else {
                                Toast.makeText(getApplicationContext(), "Location not Found", Toast.LENGTH_LONG).show();
                            }
                        }
                        else {
                            Toast.makeText(getApplicationContext(), "Location not Found", Toast.LENGTH_LONG).show();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }

        });

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
                    SupportMapFragment supportMapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
                    assert supportMapFragment != null;
                    supportMapFragment.getMapAsync(viewMaps.this);
                }
            }
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        progressBarMap.setVisibility(RelativeLayout.INVISIBLE);
        mMap = googleMap;

        LatLng latLng = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
        MarkerOptions markerOptions = new MarkerOptions().position(latLng).title("My Position").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            return;
        }
        mMap.setMyLocationEnabled(true);
        mMap.setBuildingsEnabled(true);
        mMap.setIndoorEnabled(true);
        mMap.setTrafficEnabled(true);
        mMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 5));
        mMap.addMarker(markerOptions);
    }

    public void ChangeType(View view) {
        if(mMap.getMapType() == GoogleMap.MAP_TYPE_SATELLITE){
            mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        }
        else if(mMap.getMapType() == GoogleMap.MAP_TYPE_NORMAL){
            mMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
        }
        else if(mMap.getMapType() == GoogleMap.MAP_TYPE_TERRAIN){
            mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    fetchLocation();
                }
                break;
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

    public class nearbyPolice {
        public PlacesSearchResponse run(){

            PlacesSearchResponse request = new PlacesSearchResponse();
            GeoApiContext context = new GeoApiContext.Builder()
                    .apiKey("AIzaSyAX4mrqRb56cAUUKypTAiOGJUgmam-nKQ8")
                    .build();

            com.google.maps.model.LatLng location = new com.google.maps.model.LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
            Toast.makeText(getApplicationContext(), "Nearby Police Dept's", Toast.LENGTH_SHORT).show();

            try {
                request = PlacesApi.nearbySearchQuery(context, location)
                        .radius(50000)
                        .rankby(RankBy.PROMINENCE)
                        .keyword("cruise")
                        .language("en")
                        .type(PlaceType.POLICE)
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

    public class nearbyFire {
        public PlacesSearchResponse run(){

            PlacesSearchResponse request = new PlacesSearchResponse();
            GeoApiContext context = new GeoApiContext.Builder()
                    .apiKey("AIzaSyAX4mrqRb56cAUUKypTAiOGJUgmam-nKQ8")
                    .build();

            com.google.maps.model.LatLng location = new com.google.maps.model.LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
            Toast.makeText(getApplicationContext(), "Nearby Fire Dept's", Toast.LENGTH_SHORT).show();

            try {
                request = PlacesApi.nearbySearchQuery(context, location)
                        .radius(50000)
                        .rankby(RankBy.PROMINENCE)
                        .keyword("cruise")
                        .language("en")
                        .type(PlaceType.FIRE_STATION)
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

    public class nearbyHospitals {
        public PlacesSearchResponse run(){

            PlacesSearchResponse request = new PlacesSearchResponse();
            GeoApiContext context = new GeoApiContext.Builder()
                    .apiKey("AIzaSyAX4mrqRb56cAUUKypTAiOGJUgmam-nKQ8")
                    .build();

            com.google.maps.model.LatLng location = new com.google.maps.model.LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
            Toast.makeText(getApplicationContext(), "Nearby Hospitals", Toast.LENGTH_SHORT).show();

            try {
                request = PlacesApi.nearbySearchQuery(context, location)
                        .radius(50000)
                        .rankby(RankBy.PROMINENCE)
                        .keyword("cruise")
                        .language("en")
                        .type(PlaceType.HOSPITAL)
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

    public class nearbyMall {
        public PlacesSearchResponse run(){

            PlacesSearchResponse request = new PlacesSearchResponse();
            GeoApiContext context = new GeoApiContext.Builder()
                    .apiKey("AIzaSyAX4mrqRb56cAUUKypTAiOGJUgmam-nKQ8")
                    .build();

            com.google.maps.model.LatLng location = new com.google.maps.model.LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
            Toast.makeText(getApplicationContext(), "Nearby Mall's", Toast.LENGTH_SHORT).show();

            try {
                request = PlacesApi.nearbySearchQuery(context, location)
                        .radius(50000)
                        .rankby(RankBy.PROMINENCE)
                        .keyword("cruise")
                        .language("en")
                        .type(PlaceType.SHOPPING_MALL)
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

    public class nearbyAirport {
        public PlacesSearchResponse run(){

            PlacesSearchResponse request = new PlacesSearchResponse();
            GeoApiContext context = new GeoApiContext.Builder()
                    .apiKey("AIzaSyAX4mrqRb56cAUUKypTAiOGJUgmam-nKQ8")
                    .build();

            com.google.maps.model.LatLng location = new com.google.maps.model.LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
            Toast.makeText(getApplicationContext(), "Nearby Airport", Toast.LENGTH_SHORT).show();

            try {
                request = PlacesApi.nearbySearchQuery(context, location)
                        .radius(50000)
                        .rankby(RankBy.PROMINENCE)
                        .keyword("cruise")
                        .language("en")
                        .type(PlaceType.AIRPORT)
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.map_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }
}