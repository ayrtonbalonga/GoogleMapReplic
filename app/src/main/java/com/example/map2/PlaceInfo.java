package com.example.map2;

import android.app.Application;
import android.net.Uri;

import com.google.android.gms.maps.model.LatLng;

public class PlaceInfo extends Application {

    //initializing the variables
    public  static String name;
    public static String address;
    public static String phoneNumber;
    public static String id;
    public static Uri websiteUri;
    public static LatLng latLng;
    public static Double rating;
    public static String attributions;


    public PlaceInfo() {

    }
    //constructor
    public PlaceInfo(String name, String address, String phoneNumber,
                     String id, Uri websiteUri, LatLng latLng,
                     Double rating) {

        this.name = name;
        this.address = address;
        this.phoneNumber = phoneNumber;
        this.id = id;
        this.websiteUri = websiteUri;
        this.latLng = latLng;
        this.rating = rating;
       // this.attributions = attributions;
    }

    //get and set
    public static String getName() {
        return name;
    }

    public static void setName(String name1) {
        name = name1;
    }

    public static String getAddress() {
        return address;
    }

    public static void setAddress(String addres1) {
        address = addres1;
    }

    public  static String getPhoneNumber() {
        return phoneNumber;
    }

    public static void setPhoneNumber(String phoneNumbe1) {
        phoneNumber = phoneNumbe1;
    }

    public  static String getId() {
        return id;
    }

    public  static void setId(String id1) {
        id = id1;
    }

    public static Uri getWebsiteUri() {
        return websiteUri;
    }

    public static void setWebsiteUri(Uri websiteUr1) {
        websiteUri = websiteUr1;
    }

    public static LatLng getLatLng() {
        return latLng;
    }

    public static void setLatLng(LatLng latLn1) { latLng = latLn1; }

    public static double getRating() {
        return rating;
    }

    public static void setRating(Double ratin1) {
        rating = ratin1;
    }

    public  static String getAttributions() {
        return attributions;
    }

    public  void setAttributions(String attribution1) {
        attributions = attribution1;
    }

    @Override
    public   String toString() {
        return "PlaceInfo{" +
                "name='" + name + '\'' +
                ", address='" + address + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", id='" + id + '\'' +
                ", websiteUri=" + websiteUri +
                ", latLng=" + latLng +
                ", rating=" + rating +
                ", attributions='" + attributions + '\'' +
                '}';
    }
}
