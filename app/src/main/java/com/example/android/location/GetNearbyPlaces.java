package com.example.android.location;

import android.os.AsyncTask;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

public class GetNearbyPlaces extends AsyncTask<Object,String,String> {

    private  String googlePlaceData,url;
    private GoogleMap mMap;
    @Override
    protected String doInBackground(Object... objects) {

        mMap=(GoogleMap)objects[0];
        url=(String)objects[1];
        //using the value from the downloadurl class
        DownloadUrl downloadUrl=new DownloadUrl();
        try {
            googlePlaceData=downloadUrl.ReadUrl(url);
        } catch (IOException e)
        {
            e.printStackTrace();
        }
        return googlePlaceData;
    }

    @Override
    protected void onPostExecute(String s)
    {
        List<HashMap<String,String>> nearbyPlacesList=null;
        DataParser dataParser=new DataParser();
        nearbyPlacesList=dataParser.parse(s);

        DisplayNearbyPlaces(nearbyPlacesList );

    }

    private void  DisplayNearbyPlaces(List<HashMap<String,String>> nearbyPlacesList)
    {
      for(int i=0;i<nearbyPlacesList.size();i++)
      {
          MarkerOptions markerOptions=new MarkerOptions();
          HashMap<String,String> googleNearbyPlace=nearbyPlacesList.get(i);
          //we going to get place name and vicinity
          String nameOfPlace=googleNearbyPlace.get("place_name");
          String vicinity=googleNearbyPlace.get("vicinity");
          Double lat=Double.parseDouble(googleNearbyPlace.get("lat"));
          Double lng=Double.parseDouble(googleNearbyPlace.get("lng"));

          // adding the marker now

          LatLng latLng= new LatLng(lat,lng);


          markerOptions.position(latLng);
         markerOptions.title(nameOfPlace +" : "+ vicinity);
         markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW));

          mMap.addMarker(markerOptions);

          //to move the camera to the location
          mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
          mMap.animateCamera(CameraUpdateFactory.zoomTo(10));
      }
    }
}
