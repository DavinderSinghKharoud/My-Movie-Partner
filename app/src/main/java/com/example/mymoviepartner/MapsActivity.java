package com.example.mymoviepartner;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mymoviepartner.ViewHolders.PlaceAutoSuggestAdapter;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private static final int REQUEST_LOCATION = 1;

    private GoogleMap gMap;

    private Geocoder geo;
    LocationManager locationManager;
    String latitude, longitude;
    private AutoCompleteTextView searchView;
    private ImageButton selectLocation;
    View mapView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
/*
        searchText = (EditText) findViewById(R.id.searchViewMaps);
        searchButton = findViewById(R.id.buttonSearchMap);

        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                searchLocation(searchText.getText().toString().trim());
            }
        });
*/


        searchView = findViewById(R.id.autoCompleteTextView);
        selectLocation = findViewById(R.id.checkRight);





        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        //getting the view to change the gps icon
        mapView = mapFragment.getView();


    }

    /**
     * Searching the location using geocoder, and adding marker on the screen
     *
     * @param locationSearch
     */
    public void searchLocation(String locationSearch) {
        String location = locationSearch;
        List<Address> addressList = null;

        if (locationSearch.isEmpty()) {
            return;
        }

        if (location != null || !location.equals("")) {
            Geocoder geocoder = new Geocoder(this);
            try {

                addressList = geocoder.getFromLocationName(location, 3);

            } catch (IOException e) {
                e.printStackTrace();
            }

            if (addressList.size() > 0 && addressList != null) {


                Address address = addressList.get(0);
                LatLng latLng = new LatLng(address.getLatitude(), address.getLongitude());
                gMap.clear();
                gMap.addMarker(new MarkerOptions().position(latLng).title(address.getAddressLine(0) + ", " + address.getCountryName()));
                gMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
                // Toast.makeText(getApplicationContext(), address.getLatitude() + " " + address.getLongitude(), Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(getApplicationContext(), "Not able to find the address", Toast.LENGTH_LONG).show();

            }
        }
    }

    /**
     * get current location
     *
     * @param googleMap
     */
    private void getLocation(GoogleMap googleMap) {

        //Check Permissions again

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,

                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]
                    {Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION);
        } else {
            Location LocationGps = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            Location LocationNetwork = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            Location LocationPassive = locationManager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER);

            if (LocationGps != null) {
                double lat = LocationGps.getLatitude();
                double longi = LocationGps.getLongitude();

                latitude = String.valueOf(lat);
                longitude = String.valueOf(longi);

            //    Toast.makeText(this, "Your Location:" + "\n" + "Latitude= " + latitude + "\n" + "Longitude= " + longitude, Toast.LENGTH_SHORT).show();
                markLocation(lat, longi, googleMap);

            } else if (LocationNetwork != null) {
                double lat = LocationNetwork.getLatitude();
                double longi = LocationNetwork.getLongitude();

                latitude = String.valueOf(lat);
                longitude = String.valueOf(longi);


              //  Toast.makeText(this, "Your Location:" + "\n" + "Latitude= " + latitude + "\n" + "Longitude= " + longitude, Toast.LENGTH_SHORT).show();
                markLocation(lat, longi, googleMap);
            } else if (LocationPassive != null) {
                double lat = LocationPassive.getLatitude();
                double longi = LocationPassive.getLongitude();

                latitude = String.valueOf(lat);
                longitude = String.valueOf(longi);


              //  Toast.makeText(this, "Your Location:" + "\n" + "Latitude= " + latitude + "\n" + "Longitude= " + longitude, Toast.LENGTH_SHORT).show();

                markLocation(lat, longi, googleMap);
            } else {
                Toast.makeText(this, "Can't Get Your Location", Toast.LENGTH_SHORT).show();
            }

            //Thats All Run Your App
        }

    }


    /**
     * Checking, if the GPS is on or off
     */
    private void OnGPS() {

        final AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setMessage("Enable GPS").setCancelable(false).setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
            }
        }).setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                dialog.cancel();
            }
        });
        final AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        gMap = googleMap;


        //setting the text view empty, because sometime we are not able to write something at the first start
        searchView.setText("");


        //setting up adapter for autosuggestion of places
        searchView.setAdapter(new PlaceAutoSuggestAdapter(MapsActivity.this,android.R.layout.simple_list_item_1));

        //getting permission
        ActivityCompat.requestPermissions(this, new String[]
                {Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION);

        //when user click on the search button from the keyboard
        searchView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionID, KeyEvent keyEvent) {

                if (actionID == EditorInfo.IME_ACTION_SEARCH
                        || actionID == EditorInfo.IME_ACTION_DONE
                        || keyEvent.getAction() == keyEvent.ACTION_DOWN
                        || keyEvent.getAction() == keyEvent.KEYCODE_ENTER) {

                    //getting the user typed text
                    String searchText = searchView.getText().toString().trim();

                    //searching the location
                    searchLocation(searchText);
                }
                return true;
            }

        });

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        //change gps icon place
        changeGPSiconPlace();

        //Check gps is enable or not

        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            //Write Function To enable gps

            OnGPS();
        } else {
            //GPS is already On then

            getLocation(gMap);
        }

        if (gMap != null) {
            geo = new Geocoder(this, Locale.getDefault());

            //setting up the marker on the user click
            gMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                @Override
                public void onMapClick(LatLng latLng) {
                    //clear the map
                    gMap.clear();

                    //find the address and add the marker
                    try {
                        if (geo == null)
                            geo = new Geocoder(getApplicationContext(), Locale.getDefault());
                        List<Address> address = geo.getFromLocation(latLng.latitude, latLng.longitude, 1);
                        if (address.size() > 0) {
                            gMap.addMarker(new MarkerOptions().position(latLng).title(
                                    address.get(0).getAddressLine(0)+ ", "+address.get(0).getCountryName()));
                            searchView.setText(address.get(0).getAddressLine(0) + ", " + address.get(0).getCountryName());

                        }
                    } catch (IOException ex) {
                        if (ex != null)
                            Toast.makeText(getApplicationContext(), "Error:" + ex.getMessage().toString(), Toast.LENGTH_LONG).show();
                    }
                }
            });

            //When click on the marker
            gMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                @Override
                public boolean onMarkerClick(Marker marker) {
                    //  txtMarkers.setText(marker.getTitle().toString() + " Lat:" + marker.getPosition().latitude + " Long:" + marker.getPosition().longitude);

                    searchView.setText(marker.getTitle());

                    return false;
                }
            });

            //setting up select location on click listener
            selectLocation.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //sending data again back
                    String result=searchView.getText().toString();
                    Intent returnIntent = new Intent();
                    returnIntent.putExtra("result",result);
                    setResult(RESULT_OK,returnIntent);
                    finish();
                }
            });
        }
    }

    /**
     * Marking the location on the map
     *
     * @param latitude
     * @param longitude
     * @param mMap
     */
    private void markLocation(double latitude, double longitude, GoogleMap mMap) {
        // create marker
        LatLng userLocation = new LatLng(latitude, longitude);
        MarkerOptions marker = new MarkerOptions().position(userLocation).title("My Location");

        mMap.setMyLocationEnabled(true);
        // adding marker
        mMap.addMarker(marker);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLocation, 12));
    }

    /**
     * change the gps icon place on the screen
     */
    private void changeGPSiconPlace() {
        if (mapView != null &&
                mapView.findViewById(Integer.parseInt("1")) != null) {
            // Get the button view
            View locationButton = ((View) mapView.findViewById(Integer.parseInt("1")).getParent()).findViewById(Integer.parseInt("2"));
            // and next place it, on bottom right (as Google Maps app)
            RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams)
                    locationButton.getLayoutParams();
            // position on right bottom
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP, 0);
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
            layoutParams.setMargins(0, 0, 30, 50);
        }
    }

    private TextWatcher emptyTextWatcher=new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void afterTextChanged(Editable editable) {

        }
    };
}
