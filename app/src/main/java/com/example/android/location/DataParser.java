package com.example.android.location;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class DataParser
{
    private HashMap<String,String> getSingleNearbyPlace(JSONObject googlePlaceJSON)
    {
        HashMap<String,String> googlePlaceMap=new HashMap<>();
        String NameOfPlace="-NA-";
        String Vicinity="-NA-";
        String latitude="";
        String longitude="";
        String reference="";

        try {
           if(!googlePlaceJSON.isNull("name")){
               NameOfPlace=googlePlaceJSON.getString("name");
           }
            if(!googlePlaceJSON.isNull("vicinity")){
                Vicinity =googlePlaceJSON.getString("vicinity");
            }

            latitude=googlePlaceJSON.getJSONObject("geometry").getJSONObject("location").getString("lat");
            longitude=googlePlaceJSON.getJSONObject("geometry").getJSONObject("location").getString("lng");

            reference=googlePlaceJSON.getString("reference");

            //now after fetching data we need to store in hashmap

            googlePlaceMap.put("place_name",NameOfPlace);
            googlePlaceMap.put("vicinity",Vicinity);
            googlePlaceMap.put("lat",latitude);
            googlePlaceMap.put("lng",longitude);
            googlePlaceMap.put("reference",reference);


        }

        catch (JSONException e) {
            e.printStackTrace();
        }

        return googlePlaceMap;



    }

    //for list of nearby places in form of hashmaps
    private List<HashMap<String,String>> getAllNearbyPlaces(JSONArray jsonArray)
    {
         int counter=jsonArray.length();                                          //counter for jsonArray

        List<HashMap<String,String>> NearbyPlacesList=new ArrayList<>();

        HashMap<String,String> NearbyPlacesMap=null;

        for(int i=0;i<counter;i++){
            // we r going to get detail of places one by one and then we are going to store in the list of hashma
            try {
                NearbyPlacesMap=getSingleNearbyPlace((JSONObject)jsonArray.get(i));
                NearbyPlacesList.add(NearbyPlacesMap);
            }
            catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return NearbyPlacesList;

    }
    //ths ethod basically parses the data and from getAllNearbyPlaces
    public List<HashMap<String,String>> parse(String jSONdata)
    {
        JSONArray jsonArray=null;
        JSONObject jsonObject;

        try {
            jsonObject=new JSONObject(jSONdata);
            jsonArray=jsonObject.getJSONArray("resuls");
        } catch (JSONException e) {
            e.printStackTrace();
        }


        return getAllNearbyPlaces(jsonArray);
    }
}
