package com.example.android.location;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;

import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.List;

public class MapsActivity extends FragmentActivity implements
        OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {

    private GoogleMap mMap;
    private GoogleApiClient googleApiClient;
    private LocationRequest locationRequest;
    private  Location lastLocation;
    private Marker currentUserLocationMarker;
    private static final int Request_User_Location_Code=99;

    private double latitude,longitude;
    private  int ProximityRadius;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.M){
            checkUserLocationPermission();
        }
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }
    //image click method
    public void onClick(View v){
        String restaurant="restaurant";
        Object transferData[]=new Object[2];




        GetNearbyPlaces getNearbyPlaces=new GetNearbyPlaces();

        switch(v.getId()){
            case R.id.search:
                EditText addressField=(EditText) findViewById(R.id.location_search);
                String address=addressField.getText().toString();

                List<Address> addressList =null;         //initializing list
                MarkerOptions userMarkerOptions=new MarkerOptions();         //creating our marker

                //if search box is not empty
                if(!TextUtils.isEmpty(address)){
                    Geocoder geocoder=new Geocoder(this);            //converting address in geocoordinates
                    //now this will return a list of 6 search results list

                    try {
                        addressList=geocoder.getFromLocationName(address,6);
                        if(addressList!=null){
                            for(int i=0;i<addressList.size();i++)
                            {
                                Address userAddress=addressList.get(i);
                                LatLng latLng= new LatLng(userAddress.getLatitude(),userAddress.getLongitude());


                                userMarkerOptions.position(latLng);
                                userMarkerOptions.title(address);
                                userMarkerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ROSE));

                                mMap.addMarker(userMarkerOptions);

                                //to move the camera to the location
                                mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                                mMap.animateCamera(CameraUpdateFactory.zoomTo(10));



                            }
                        }
                        else{
                            Toast.makeText(this,"Can't find address",Toast.LENGTH_SHORT).show();
                        }

                    } catch (IOException e) {
                        e.printStackTrace();
                    }


                }
                // for empty search bar
                else{
                    Toast.makeText(this,"Enter address!",Toast.LENGTH_SHORT).show();
                }
                break;

            case R.id.restaurants:
                mMap.clear();
                String url=getUrl(latitude,longitude,restaurant);
                transferData[0]=mMap;
                transferData[1]=url;

                getNearbyPlaces.execute(transferData);
                Toast.makeText(this,"searching nearby restaurants",Toast.LENGTH_SHORT).show();
                break;
        }

    }


    private  String getUrl(double latitude,double longitude,String nearbyPlace)
    {
        StringBuilder googleUrl= new StringBuilder("https://maps.googleapis.com/maps/api/place/nearbysearch/json?");
        googleUrl.append("location="+latitude+","+longitude);
        googleUrl.append("&radius="+ProximityRadius);
        googleUrl.append("&type="+nearbyPlace);
        googleUrl.append("keyword=cruise");
        googleUrl.append("&key="+"AIzaSyCsYUwdj9m7EAD8dqf4zIUfJwrlDfaH930");

        Log.d("GoogleMapsActivity","url="+googleUrl.toString());


        return googleUrl.toString();
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */

    //whenever the map is ready to be used
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // to get current location
       // ??this if was implemented to check setMyLocationEnabled
        //this if condition basically checks the permissions in manifest
        if(ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION)== PackageManager.PERMISSION_GRANTED) {

            //callimg the build google api client
            buildGoogleApiClient();

            mMap.setMyLocationEnabled(true);

            googleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                @Override
                public boolean onMarkerClick(Marker marker) {



                    Intent intent= new Intent(MapsActivity.this,MainActivity.class);


                    LatLng markerLocation=marker.getPosition();

                    Bundle bundle=new Bundle();
                    bundle.putString("markercoord",markerLocation.toString());

                    intent.putExtras(bundle);
                    startActivity(intent);
                    finish();








                    return false;
                }
            });


        }


    }

    public boolean checkUserLocationPermission()
    {
        //we ll check if the permission is granted or not
        if(ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION)!=PackageManager.PERMISSION_GRANTED)
        {
            //when permission isn't granted
            //we check if we ask the user for permission
            if(ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.ACCESS_FINE_LOCATION)){
                ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},Request_User_Location_Code);
            }
            else
            {
                ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},Request_User_Location_Code);
            }
            return false;
        }
        //if the app has permission granted it returns true and the app runs normally
        else {
            return true;
        }
    }


    //method will get permission result


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
    {
        switch (requestCode)
        {
            case Request_User_Location_Code:
                if(grantResults.length>0&&grantResults[0]==PackageManager.PERMISSION_GRANTED){
                    //that means the permission is granted

                    if(ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION)==PackageManager.PERMISSION_GRANTED){
                        if(googleApiClient==null){
                            //if google api cliet is null we need to build a new google api client
                            buildGoogleApiClient();
                        }
                        mMap.setMyLocationEnabled(true);
                    }
                }

                //now if the permission is denied it is not granted

                else
                {
                    Toast.makeText(this,"Permission denied",Toast.LENGTH_SHORT).show();
                }

                return;

        }
    }

    protected synchronized void buildGoogleApiClient(){
        googleApiClient=new GoogleApiClient.Builder(this).addConnectionCallbacks(this).addOnConnectionFailedListener(this)
                .addApi(LocationServices.API).build();

        googleApiClient.connect();
    }
//whenever there is a change in location
    @Override
    public void onLocationChanged(Location location) {
        latitude=location.getLatitude();
        longitude=location.getLongitude();
        // this location is location passed in parameter
        lastLocation=location;
        // to check if there's a marker which is set to some other place previously

        //this null condition checks if marker is set to some other location
        if(currentUserLocationMarker!=null){
            //so we'll remove the marker location
            currentUserLocationMarker.remove();


        }
        LatLng latLng= new LatLng(location.getLatitude(),location.getLongitude());
        //to display location name we need markerOption
        MarkerOptions markerOptions=new MarkerOptions();
        markerOptions.position(latLng);
        markerOptions.title(latLng.toString());
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_CYAN));

        currentUserLocationMarker=mMap.addMarker(markerOptions);

        //to move the camera to the location
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        mMap.animateCamera(CameraUpdateFactory.zoomBy(12));

        //after setting the current location of user we need to stop the current location update

        if(googleApiClient!=null){
            LocationServices.FusedLocationApi.removeLocationUpdates(googleApiClient,this);
        }

    }


/* this method is called whenever device is connected*/
    @Override
    public void onConnected(@Nullable Bundle bundle) {
        locationRequest=new LocationRequest();          //locationRequest objects are used to request a quality of service for location updates from fusedlocationprovider

        locationRequest.setInterval(1100);          // the location client will actively try to obtain loc updates at this interval
        locationRequest.setFastestInterval(1100);   // fastest rate
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        //gonna use fused location api

        //only fused api will show error cause it needs user permission
        if(ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION)== PackageManager.PERMISSION_GRANTED){
            LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient,locationRequest,this);
        }


    }

    @Override
    public void onConnectionSuspended(int i) {

    }
//whenever connection fails this method is called
    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }



}