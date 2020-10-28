package com.example.map2;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PointOfInterest;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.FetchPlaceRequest;
import com.google.android.libraries.places.api.net.FetchPlaceResponse;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.annotations.NotNull;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static com.example.map2.R.id.map;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback,TaskLoadedCallback {

    private static final String TAG = "MapsActivity";
    private static final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COURSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1234;

    //Widgets
    private ImageView mGps, mInfo,mSetting;
    private TextView tvDistance,tvDur;
    public static ImageView ivCancel;
    public static TextView tvName,tvAddress,tvPhone,tvWebsite,tvLat,tvLong;
    public static CheckBox cbHistory,cbModern;

    //Variable
    private static final float DEFAULT_ZOOM = 15f;
    private GoogleMap mMap;
    //Classes
    public static  PlaceInfo placeInfo;
    public static PlaceInfo2 placeInfo2;
    //Marker
    private Marker mMarker;
    public static MarkerOptions deviceLocation ;
    public static MarkerOptions destination ;
    //Polyline
    public Polyline currentPolyline;
    //boolean
    private Boolean mLocationPermissionGranted = false;
    public static boolean isKm= false;
    public static boolean isMil= false;
    public static boolean isLongClick ;
    //double
    public static double distance;
    public static String address,city,state,country,postalCode,name;
    //Latitude Longitude
    public static LatLng coord;
    //Views
    public static View bottomSheetView ;
    //Froms
    public static BottomSheetDialog bottomSheetDialog;
    public static FusedLocationProviderClient mFusedLocationProviderClient;
    private PlacesClient placesClient;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        //initializing the images view
        mGps = (ImageView) findViewById(R.id.ic_gps);
        mInfo = (ImageView) findViewById(R.id.place_info);
        mSetting =(ImageView) findViewById(R.id.ivSetting);
        tvDistance =(TextView) findViewById(R.id.tvDistance);
        tvDur =(TextView) findViewById(R.id.tvDur);

        //initializing the buttom dialog
        bottomSheetDialog = new BottomSheetDialog(MapsActivity.this, R.style.ButtomSheetDialogTheme);
        //initializing the bottom view
        bottomSheetView = LayoutInflater.from(getApplicationContext())
                .inflate(
                        R.layout.layout_bottom_sheet,(ConstraintLayout)findViewById(R.id.bottomSheetContainer)
                );

        //initializing the text view
        tvName= (TextView)bottomSheetView.findViewById(R.id.tvName);
        tvAddress= (TextView)bottomSheetView.findViewById(R.id.tvAdress);
        tvPhone= (TextView)bottomSheetView.findViewById(R.id.tvPhone);
        tvWebsite= (TextView)bottomSheetView.findViewById(R.id.tvWebsite); 
        tvLat= (TextView)bottomSheetView.findViewById(R.id.tvLat);
        tvLong= (TextView)bottomSheetView.findViewById(R.id.tvLong);

        //initializing the check box
        cbHistory = (CheckBox) bottomSheetView.findViewById(R.id.cbHistorical);
        cbModern = (CheckBox) bottomSheetView.findViewById(R.id.cbModern);

        //initializing the Cancel Image View
        ivCancel =(ImageView)bottomSheetView.findViewById(R.id.ivCancel);

        //call method to get the locaton permission
        getLocationPermission();
        //initializing the autocomplete fragment
        initAutocompleteSupportFragment();

        FusedLocationProviderClient mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(
                MapsActivity.this
        );

    }

    //initiaizing the map
    @Override
    public void onMapReady(final GoogleMap googleMap) {

        //check if the distamce is in miles or kilometers
        checkDistance();

        //display a message to show that the map is ready to be used
        Toast.makeText(this, "Map is Ready", Toast.LENGTH_SHORT).show();
        Log.d(TAG, "onMapReady:map is ready");
        mMap = googleMap;

        //if all the permissions are granted then
        if (mLocationPermissionGranted) {

            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                return;

            }

            mMap.setMyLocationEnabled(true);

            //call method to get the device location
            getDeviceLocation();

            //initializing the map
            init();

            mMap.setOnPoiClickListener(new GoogleMap.OnPoiClickListener() {

                @Override
                public void onPoiClick( PointOfInterest pointOfInterest)
                {
                    //call method to get the place details that is clicked
                    getDetailsPlaces(pointOfInterest.placeId);

                    //clean the map
                    mMap.clear();

                    isLongClick = false;

                    //initializing a marker
                    MarkerOptions options = new MarkerOptions().position(pointOfInterest.latLng);

                    // add the marker on the map when clicked on the map
                    mMap.addMarker(options);
                    Log.d(TAG,"the id"+pointOfInterest.placeId);
                    mMarker = null;

                    //call the method to display the the buttom sheet
                    displayButtomSheet();

                }
            });
                //when the user long click on the place
                mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
                    @Override
                    public void onMapLongClick( LatLng latLng) {

                        //gettoing the location of the selected place
                        coord =latLng;

                        //clean the map
                        mMap.clear();
                        isLongClick = true;

                        // First check if myMarker is null
                        if (mMarker == null) {

                            //initialized a geocoder
                            Geocoder geocoder;

                            //initialized a list of type address
                            List<Address> addresses;
                            geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());

                            try {

                                //get all the approximity addresses to the place that is selected by the user
                                addresses = geocoder.getFromLocation(coord.latitude, coord.longitude, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
                                Log.d(TAG,"the address: "+addresses.get(0).getAddressLine(0));

                                //get all the information in from the address
                                address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
                                city = addresses.get(0).getLocality();
                                state = addresses.get(0).getAdminArea();
                                country = addresses.get(0).getCountryName();
                                postalCode = addresses.get(0).getPostalCode();
                                name = addresses.get(0).getFeatureName();

                                Log.d(TAG,"the address: "+addresses.get(0));

                                //setting the informations in the class
                                placeInfo2.setKnowName(name);
                                placeInfo2.setAddress(address);
                                placeInfo2.setLatLng(coord);

                            } catch (IOException e) {

                                e.printStackTrace();
                            }

                            //if the user long click a location
                            if (isLongClick){

                                //set the text of the text edit
                                tvName.setText(name);
                                tvAddress.setText(address);
                                tvPhone.setText("null");
                                tvWebsite.setText("null");
                                tvLat.setText(String.valueOf(coord.latitude));
                                tvLong.setText(String.valueOf(coord.longitude));
                            }

                            //  Marker was not set yet. Add marker:
                            String snippet =
                                    "Address: " + address + "\n" +
                                            "City: " + city + "\n" +
                                            "State: " + state + "\n" +
                                            "Country: " + country + "\n" +
                                            "Postal Code: " + postalCode + "\n" +
                                            "knowName: " + name + "\n" +
                                            "Latitude: " + coord.latitude + "\n" +
                                            "Longitude: " +coord.longitude+ "\n";

                            //set the marker on the map
                            mMarker = mMap.addMarker(new MarkerOptions()
                                    .position(coord)
                                    .title(name)
                                    .snippet(snippet));

                            // the latitude and longitude of the place selected  and set it in the marker destination
                            destination = new MarkerOptions().position(
                                    new LatLng(coord.latitude, coord.longitude)).title("Destination");

                            //add the marker on the mop
                            mMap.addMarker(destination);

                            //display the buttonsheet
                            displayButtomSheet();

                        } else {

                            // Marker already exists, just update it's position
                            mMarker.setPosition(latLng);
                            mMarker =null;
                        }
                    }
                });
        }
    }

    //calculate and display the distance of between the device location and the selected destination
    private void showDistance() {

        //check if the distance must be in Miles or Kilometers
        if (isKm&&!isMil){
            tvDistance.setText("0 km");
        }else if(isMil&&!isKm){
            tvDistance.setText("0 mi");
        }

        //initializing the destination location variable
        Location des = new Location("Destination");
        //set the latitude of the destination getting it from the marker destination
        des.setLatitude(destination.getPosition().latitude);
        //set the longitude of the destination getting it from the marker destination
        des.setLongitude(destination.getPosition().longitude);
        //initializing the device location variable
        Location currentLocation = new Location("Current location");
        //set the latitude of the device location getting it from the marker destination
        currentLocation.setLatitude(deviceLocation.getPosition().latitude);
        //set the longitude of the destination getting it from the marker destination
        currentLocation.setLongitude(deviceLocation.getPosition().longitude);

        //get the distance
        distance = des.distanceTo(currentLocation);
        tvDur.setText(getTimeTaken(distance));

        if (isKm &&!isMil){
            //if the distance between the device location and the destination is less then a 1000 meters
            if(distance<1000){
                //set the textView text to the distance in kilometers
                tvDistance.setText(new DecimalFormat("##.##").format(distance) + "m");

            }else{
                //set the textView text to the distance in kilometers
                tvDistance.setText(new DecimalFormat("##.##").format((distance)/1000) + "km");
            }
        }else if(isMil&&!isKm){

                //convert the distance in Miles
                double distMi= (distance/1000)*0.62137;

                //set the textView text to the distance in miles
                tvDistance.setText(new DecimalFormat("##.##").format(distMi) + "mi");

        }

    }

    //calculate the best route to the destination
    private void calculateBestRoute(MarkerOptions devLoc,MarkerOptions des){
        //get the position of the destionation and device location
        String url = getUrl(devLoc.getPosition(),des.getPosition(),"driving");
        new FetchURL(MapsActivity.this).execute(url, "driving");

    }

    //display the buttom sheet
    private void displayButtomSheet(){

    //when check box checked
    cbHistory.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            //make checked box uncheck
            cbModern.setChecked(false);

        }
    });

    //when check box checked
    cbModern.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
        //make checked box uncheck
            cbHistory.setChecked(false);
        }
    });
    //omce the image view is clicked on
    ivCancel.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            //close the bottomsheetDialog
            bottomSheetDialog.dismiss();
        }
    });

    //initializing the button direction from the button sheet form
    bottomSheetView.findViewById(R.id.btnDirection).setOnClickListener(new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            Log.d(TAG,"the DeviceLocation"+destination.getPosition());

            //calcutate the best route and get as paremeter device location marker and destination marker
            calculateBestRoute(deviceLocation,destination);

            //showing the distance
            showDistance();

            //initializing the latitude and longitude bounds
            LatLngBounds.Builder builder = new LatLngBounds.Builder();

            //set the device location
            builder.include(new LatLng(deviceLocation.getPosition().latitude,deviceLocation.getPosition().longitude));

            //set destination location
            builder.include(destination.getPosition());
            LatLngBounds bounds = builder.build();

            //move the camera into the middle point between the two location
            CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, 300);

            //move the camera with animation
            mMap.animateCamera(cu);

            //display this message
            Toast.makeText(MapsActivity.this,"Giving directions",Toast.LENGTH_SHORT).show();

            //close the buttom sheet diagram
            bottomSheetDialog.dismiss();

        }
    });

    //set the bottom dialog
    bottomSheetDialog.setContentView(bottomSheetView);

    //show the buttom dialog
    bottomSheetDialog.show();

    //initializing the button AddLandmark from the button sheet form
    bottomSheetView.findViewById(R.id.btnAddLandmark).setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            //if none of the check box are not checked when the button Add Landmark is click
            if(!cbHistory.isChecked()&&!cbModern.isChecked()){

                //display this message
                Toast.makeText(MapsActivity.this,
                        "You have to check if you want to save a Historical Landmark or Modern Landmark"
                        ,Toast.LENGTH_SHORT).show();

            }else{

                //when the marker om the map has been set by long clicking
                if(!isLongClick){

                    //when the historicak check box is checked then
                    if (cbHistory.isChecked()){

                        //save in the database the information about selected location as a historical landmark
                        saveHistoricalLandmark();

                        //if the modern check box is ckecked then
                    }else if(cbModern.isChecked()){

                        //save in the database the information about selected location as a modern landmark
                        saveModernLandmark();
                    }

                    //when the marker om the map has been set by simple one clicking
                }else{

                    //when the historicak check box is checked then
                    if (cbHistory.isChecked()){

                        //save in the database the information about selected location as a historical landmark
                        saveHistoricalLandmark2();

                        //if the modern check box is ckecked then
                    }else if(cbModern.isChecked()){

                        //save in the database the information about selected location as a modern landmark
                        saveModernLandmark2();
                    }

                }

            }


        }
    });


}

    //get the informaation about the best route between the device locationa dnd the destination from the direction API
    private String getUrl(LatLng origin,LatLng dest,String directionMode){

        // Origin of route
        String str_origin = "origin=" + origin.latitude + "," + origin.longitude;

        // Destination of route
        String str_dest = "destination=" + dest.latitude + "," + dest.longitude;

        // Mode
        String mode = "mode=" + directionMode;

        // Building the parameters to the web service
        String parameters = str_origin + "&" + str_dest + "&" + mode;

        // Output format
        String output = "json";
        String apiKey="AIzaSyDEx3fu-W_LpjCdnhiX5ZbLrS4O0LAHGZU";

        // Building the url to the web service
        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters + "&key="+apiKey;
        return url;
    }

    //get the details of the selected place
    private void getDetailsPlaces(String pPlaceId){
        //initializing the Places API with the API key
        Places.initialize(getApplicationContext(), "AIzaSyDEx3fu-W_LpjCdnhiX5ZbLrS4O0LAHGZU");

        // Define a Place ID.
        final String placeId = pPlaceId;

        // Specify the fields to return.
        final List<Place.Field> placeFields = Arrays.asList(Place.Field.ID, Place.Field.LAT_LNG,
                Place.Field.NAME, Place.Field.ADDRESS, Place.Field.PHONE_NUMBER,
                Place.Field.WEBSITE_URI, Place.Field.VIEWPORT, Place.Field.RATING);

        // Construct a request object, passing the place ID and fields array.
        final FetchPlaceRequest request = FetchPlaceRequest.newInstance(placeId,placeFields);

        //placesClient.fetchPlace(request).addOnSuccessListener()
        placesClient = Places.createClient(getApplicationContext());

        //set the the event listiner
        placesClient.fetchPlace(request).addOnSuccessListener(new OnSuccessListener<FetchPlaceResponse>() {
            @Override
            public void onSuccess(FetchPlaceResponse response) {

                //set the place object
                Place place = response.getPlace();

                Log.i(TAG, "Place found: " + place.getName());

                try {

                    //iniatializing the class
                    placeInfo = new PlaceInfo();

                    //set the place information in the class
                    placeInfo.setId(place.getId());
                    placeInfo.setLatLng(place.getLatLng());
                    placeInfo.setName(place.getName());
                    placeInfo.setAddress(place.getAddress());
                    placeInfo.setPhoneNumber(place.getPhoneNumber());
                    placeInfo.setWebsiteUri(place.getWebsiteUri());
                    placeInfo.setRating(place.getRating());

                    //set the marker destination position with the current information collected from the API
                    destination = new MarkerOptions().position(
                            new LatLng(placeInfo.getLatLng().latitude,placeInfo.getLatLng().longitude)).title("Destination");

                    //if the user have selected the location by a simple click
                    if(!isLongClick){

                        //set the text view with the collected information
                        tvName.setText(place.getName());
                        tvAddress.setText(place.getAddress());
                        tvPhone.setText(place.getPhoneNumber());


                        //tvWebsite.setText(Html.fromHtml(place.getWebsiteUri().toString()));

                        tvWebsite.setText(place.getWebsiteUri().toString());







                        tvLat.setText(String.valueOf(place.getLatLng().latitude));
                        tvLong.setText(String.valueOf(place.getLatLng().longitude));

                    }

                    Log.i(TAG, "onResult: " + placeInfo.toString());
                } catch (NullPointerException e) {
                    Log.i(TAG, "onResult: NullPointerException" + e.getMessage());

                }

                //move the camera to the location for the selected place
                moveCamera(placeInfo.getLatLng(), DEFAULT_ZOOM, placeInfo.getName(), placeInfo);

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                if (exception instanceof ApiException) {
                    final ApiException apiException = (ApiException) exception;
                    Log.e(TAG, "Place not found: " + exception.getMessage());
                    final int statusCode = apiException.getStatusCode();
                    // TODO: Handle error with given status code.
                }
            }
        });
    }

    private void gotoUrl(String s) {

        Uri link = Uri.parse(s);

        startActivity(new Intent(Intent.ACTION_VIEW,link));
    }

    //initializing autocompletes fragment
    private void initAutocompleteSupportFragment() {

        // Initialize the AutocompleteSupportFragment.
        AutocompleteSupportFragment autocompleteFragment = (AutocompleteSupportFragment)
                getSupportFragmentManager().findFragmentById(R.id.autocomplete_fragment);

        // Specify the types of place data to return.
        autocompleteFragment.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.LAT_LNG,
                Place.Field.NAME, Place.Field.ADDRESS, Place.Field.PHONE_NUMBER,
                Place.Field.WEBSITE_URI, Place.Field.VIEWPORT, Place.Field.RATING));

        // Set up a PlaceSelectionListener to handle the response.
        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(@NotNull Place place) {
                // TODO: Get info about the selected place.
                Log.i(TAG, "Place: " + place.getName() + ", " + place.getId() + ", " + place.getLatLng());


                try {

                    //once the place is selected from the autocompletion list
                    //initializing the class
                    placeInfo = new PlaceInfo();

                    //set the place's details to the class
                    placeInfo.setId(place.getId());
                    placeInfo.setLatLng(place.getLatLng());
                    placeInfo.setName(place.getName());
                    placeInfo.setAddress(place.getAddress());
                    placeInfo.setPhoneNumber(place.getPhoneNumber());
                    placeInfo.setWebsiteUri(place.getWebsiteUri());
                    placeInfo.setRating(place.getRating());

                    //set the destination marker based on the information that got collected from location that was seleceted
                    //on the autocompletion list
                    destination = new MarkerOptions().position(
                            new LatLng(placeInfo.getLatLng().latitude,placeInfo.getLatLng().longitude)).title("Destination");

                    Log.i(TAG, "onResult: " + placeInfo.toString());
                } catch (NullPointerException e) {
                    Log.i(TAG, "onResult: NullPointerException" + e.getMessage());

                }

                //move the camera to the selected location
                moveCamera(placeInfo.getLatLng(), DEFAULT_ZOOM, placeInfo.getName(), placeInfo);

                //hide keyboard
                hideSoftKeyboard();
            }

            @Override
            public void onError(@NotNull Status status) {
                // TODO: Handle the error.
                Log.i(TAG, "An error occurred: " + status);
            }
        });

    }

    //initializing the places API
    private void init() {
        //initializing places
        Places.initialize(getApplicationContext(), "AIzaSyDEx3fu-W_LpjCdnhiX5ZbLrS4O0LAHGZU");
        Log.d(TAG, "init:initializing");

        mGps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onclick: clicked gps icon");

                //get the deive location
                getDeviceLocation();
            }
        });
        mInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onclick: clicked place info");
                try {
                        //if the user click on the marker a small window will appear and show all the information about the place
                    if (mMarker.isInfoWindowShown()) {
                        mMarker.hideInfoWindow();
                    } else {
                        Log.d(TAG, "onclick: place info" + placeInfo.toString());
                        mMarker.showInfoWindow();
                    }
                } catch (NullPointerException e) {
                    Log.d(TAG, "onclick: NullPointerException" + e.getMessage());
                }
            }
        });

        mSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //once image view is clicled then display the setting activity
                Intent intent = new Intent(MapsActivity.this, SettingActivity.class);
                startActivity(intent);
            }
        });

        //hide the keyboard
        hideSoftKeyboard();
    }

    //get the device location
    private void getDeviceLocation() {
        Log.d(TAG, "getDeviceLocation: getting the devices current location");

        //initializing the location services from the gps of the device
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        try {
            if (mLocationPermissionGranted) {
                final Task location = mFusedLocationProviderClient.getLastLocation();
                location.addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {

                        //if the device have been located
                        if (task.isSuccessful() && task.getResult() != null) {

                            Log.d(TAG, "onComplete :found location!");

                            //initializing the position fo the device
                            Location currentLocation = (Location) task.getResult();

                            //set the position of the marker of the device
                            deviceLocation = new MarkerOptions().position(
                                    new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude())).title("My Location");

                            //move the camera to the location of the device
                            moveCamera(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()), DEFAULT_ZOOM,
                                    "My Location", placeInfo);

                            //set the marker on the map to the device location
                            mMap.addMarker(deviceLocation);

                        } else {
                            Log.d(TAG, "onComplete: curremnt location is null");
                            Toast.makeText(MapsActivity.this, "unable to get current location", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }

        } catch (SecurityException e) {
            Log.d(TAG, "getDeviceLocation: SecurityException" + e.getMessage());
        }
    }

    //move the camera
    private void moveCamera(LatLng latLng, float zoom, String title, PlaceInfo placeInfo) {

        Log.d(TAG, "moveCamera:Moving the camera to Lat: " + latLng.latitude + ",lng: " + latLng.longitude);

        //move the camera with animation
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));

        //display the info from the location in the small box
        mMap.setInfoWindowAdapter(new CustomInfoWindowAdapter(MapsActivity.this));

        //initializing the marker
        MarkerOptions options = new MarkerOptions().position(latLng).title(title);

        //set the marker
        mMap.addMarker(options);
        mMap.clear();

        //when the class data is not null
        if (placeInfo != null) {
            try {


                String snippet = "Address: " + placeInfo.getAddress() + "\n" +
                        "Phone Number: " + placeInfo.getPhoneNumber() + "\n" +
                        "Website: " + placeInfo.getWebsiteUri() + "\n" +
                        "Price Rating: " + placeInfo.getRating() + "\n";

                MarkerOptions options1 = new MarkerOptions()
                        .position(placeInfo.getLatLng())
                        .title(placeInfo.getName())
                        .snippet(snippet);
                mMarker = mMap.addMarker(options1);

            } catch (NullPointerException e) {
                Log.d(TAG, "moveCamera:NullPointerException" + e.getMessage());
            }
        } else {
            mMap.addMarker(new MarkerOptions().position(latLng));

        }

        //hide the keyboard
        hideSoftKeyboard();

    }

    //initializing the map
    private void initMap() {
        Log.d(TAG, "initMap:initialazinig map");
        //iniializing the map fragment
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(map);
        mapFragment.getMapAsync(this);

    }

    //hide the keyboard
    private void hideSoftKeyboard() {

        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }

    //get the location Permission
    private void getLocationPermission() {
        Log.d(TAG, "getLocationPermission: getting location permissions");

        String[] permission = {Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION};
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(), FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            if (ContextCompat.checkSelfPermission(this.getApplicationContext(), COURSE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {

                mLocationPermissionGranted = true;
                initMap();

            } else {
                ActivityCompat.requestPermissions(this, permission, LOCATION_PERMISSION_REQUEST_CODE);

            }
        } else {
            ActivityCompat.requestPermissions(this, permission, LOCATION_PERMISSION_REQUEST_CODE);
        }
    }

    //save historical landmark
    private void saveHistoricalLandmark(){

        //initializing the path of the database
        final DatabaseReference writeHistorical = FirebaseDatabase.getInstance().getReference(
                "/Landmark");

        //Writing Hashmap
        Map<String, Object> mHashmap = new HashMap<>();

        mHashmap.put("/"+UsersInfo.getUsername()+"/Historical/"+placeInfo.getName()+"/lat", placeInfo.getLatLng().latitude);
        mHashmap.put("/"+UsersInfo.getUsername()+"/Historical/"+placeInfo.getName()+"/long", placeInfo.getLatLng().longitude);
        mHashmap.put("/"+UsersInfo.getUsername()+"/Historical/"+placeInfo.getName()+"/placeId", placeInfo.getId());
        mHashmap.put("/"+UsersInfo.getUsername()+"/Historical/"+placeInfo.getName()+"/Address", placeInfo.getAddress());
        writeHistorical.updateChildren(mHashmap);

        //display this message
        Toast.makeText(getApplicationContext(), "Historical Landmark has been saved", Toast.LENGTH_LONG).show();

    }

    //save Modern Landmark
    private void saveModernLandmark(){

        //initializing the path to the database
        final DatabaseReference writeHistorical = FirebaseDatabase.getInstance().getReference(
                "/Landmark");

        //Writing Hashmap
        Map<String, Object> mHashmap = new HashMap<>();

        mHashmap.put("/"+UsersInfo.getUsername()+"/Modern/"+placeInfo.getName()+"/lat", placeInfo.getLatLng().latitude);
        mHashmap.put("/"+UsersInfo.getUsername()+"/Modern/"+placeInfo.getName()+"/long", placeInfo.getLatLng().longitude);
        mHashmap.put("/"+UsersInfo.getUsername()+"/Modern/"+placeInfo.getName()+"/placeId", placeInfo.getId());
        mHashmap.put("/"+UsersInfo.getUsername()+"/Modern/"+placeInfo.getName()+"/Address", placeInfo.getAddress());
        writeHistorical.updateChildren(mHashmap);

        //display this message
        Toast.makeText(getApplicationContext(), "Modern Landmark has been saved", Toast.LENGTH_LONG).show();

    }

    //save historical landmark
    private void saveHistoricalLandmark2(){

        final DatabaseReference writeHistorical = FirebaseDatabase.getInstance().getReference(
                "/Landmark");


        //Writing Hashmap
        Map<String, Object> mHashmap = new HashMap<>();

        mHashmap.put("/"+UsersInfo.getUsername()+"/Historical/"+placeInfo2.getKnowName()+"/Lat", placeInfo2.getLatLng().latitude);
        mHashmap.put("/"+UsersInfo.getUsername()+"/Historical/"+placeInfo2.getKnowName()+"/Long", placeInfo2.getLatLng().longitude);
        mHashmap.put("/"+UsersInfo.getUsername()+"/Historical/"+placeInfo2.getKnowName()+"/KnownName", placeInfo2.getKnowName());
        mHashmap.put("/"+UsersInfo.getUsername()+"/Historical/"+placeInfo2.getKnowName()+"/Address", placeInfo2.getAddress());
        writeHistorical.updateChildren(mHashmap);

        Toast.makeText(getApplicationContext(), "Historical Landmark has been saved", Toast.LENGTH_LONG).show();





    }

    //save Modern Landmark
    private void saveModernLandmark2(){

        final DatabaseReference writeHistorical = FirebaseDatabase.getInstance().getReference(
                "/Landmark");


        //Writing Hashmap
        Map<String, Object> mHashmap = new HashMap<>();

        mHashmap.put("/"+UsersInfo.getUsername()+"/Modern/"+placeInfo2.getKnowName()+"/Lat", placeInfo2.getLatLng().latitude);
        mHashmap.put("/"+UsersInfo.getUsername()+"/Modern/"+placeInfo2.getKnowName()+"/Long", placeInfo2.getLatLng().longitude);
        mHashmap.put("/"+UsersInfo.getUsername()+"/Modern/"+placeInfo2.getKnowName()+"/KnownName", placeInfo2.getKnowName());
        mHashmap.put("/"+UsersInfo.getUsername()+"/Modern/"+placeInfo2.getKnowName()+"/Address", placeInfo2.getAddress());
        writeHistorical.updateChildren(mHashmap);

        Toast.makeText(getApplicationContext(), "Modern Landmark has been saved", Toast.LENGTH_LONG).show();





    }

    //check if the user have set the distance in miles or Kilometer
    private void checkDistance(){

        //get the link of the database
        DatabaseReference reff = FirebaseDatabase.getInstance().getReference("/Setting/"+UsersInfo.getUsername());

        //check if the username is in the database
       // Query checkDistance = reff.orderByChild("username").equalTo(UsersInfo.getUsername());
        reff.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                //if the user is register then
                if (snapshot.exists()) {

                    //get the values from the database
                   isKm = snapshot.child("isKilometers").getValue(Boolean.class);
                    isMil = snapshot.child("isMiles").getValue(Boolean.class);

                    Log.d(TAG, "the setting distance is in Miles"+isMil);
                    Log.d(TAG, "the setting distance is in Kilometers:"+isKm);

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    //calculate the duration of the trip
    public String getTimeTaken(double dis) {

        //Log.d("ResponseT","meter :"+meter+ " kms : "+kms+" mins :"+mins_taken);
        int totalMinutes= (int) ((dis/1000)/0.5);

        //if the time of the trip is less then 60
        if (totalMinutes<60)
        {
            //return the time in minutes
            return ""+totalMinutes+" mins";

            //else if the time of the trip is bigger then 24 hours
        }else if((totalMinutes/60)>24){

            String minutes = Integer.toString(totalMinutes % 60);
            minutes = minutes.length() == 1 ? "0" + minutes : minutes;

            //returns time sin days, hours and minutes
            return ((totalMinutes/60)/24)+" Day "+(totalMinutes / 60) + " hour " + minutes +"mins";
        }
        else {

            String minutes = Integer.toString(totalMinutes % 60);
            minutes = minutes.length() == 1 ? "0" + minutes : minutes;

            //return the time in hours and minutes
            return (totalMinutes / 60) + " hour " + minutes +"mins";

        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        Log.d(TAG, "OnRequestPermissionsResults:called");
        mLocationPermissionGranted = false;
        switch (requestCode) {
            case LOCATION_PERMISSION_REQUEST_CODE: {
                if (grantResults.length > 0) {
                    for (int i = 0; i > grantResults.length; i++) {
                        if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                            mLocationPermissionGranted = false;
                            Log.d(TAG, "OnRequestPermissionsResults:permission failed");
                            return;
                        }
                    }
                    Log.d(TAG, "OnRequestPermissionsResults:permission granted");
                    mLocationPermissionGranted = true;

                    //initiation our map
                    initMap();
                }
            }
        }


    }

    @Override
    public void onTaskDone(Object... values) {
        if (currentPolyline != null)
            currentPolyline.remove();
        currentPolyline = mMap.addPolyline((PolylineOptions) values[0]);
    }



}