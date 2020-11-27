package com.example.android.location;

public class Food {

    String pref1;
    String pref2;
    String coord;
//create constructor
    public Food(String pref1, String pref2,String coord) {
        this.pref1 = pref1;
        this.pref2 = pref2;
        this.coord=coord;
    }
    //create getters


    public String getPref1() {
        return pref1;
    }

    public String getPref2() {
        return pref2;
    }

    public String getCoord(){return coord;}
}
