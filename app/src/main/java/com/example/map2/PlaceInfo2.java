package com.example.map2;

import android.app.Application;

import com.google.android.gms.maps.model.LatLng;

public class PlaceInfo2 extends Application {
    //initializing variables
    public static String knowName;
    public  static String address;
    public static LatLng latLng;


    public PlaceInfo2() {

    }
    //constructoer
    public PlaceInfo2(String name, String address, LatLng latLng) {

        this.knowName = name;
        this.address = address;

        this.latLng = latLng;

        // this.attributions = attributions;
    }

    //get and set
    public  static String getKnowName() {
        return knowName;
    }

    public   static void setKnowName(String name) {
        knowName = name;
    }

    public static String getAddress() {
        return address;
    }

    public static void setAddress(String addres) {
        address = addres;
    }

    public static LatLng getLatLng() {
        return latLng;
    }

    public static void setLatLng(LatLng latLn) {
        latLng = latLn;
    }


    //to string, to display all the information
    @Override
    public  String toString() {
        return "PlaceInfo{" +
                "name='" + knowName + '\'' +
                ", address='" + address + '\'' +
                ", latLng=" + latLng +
                '}';
    }
}
