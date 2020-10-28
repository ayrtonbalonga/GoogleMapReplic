package com.example.map2;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

import static com.example.map2.MapsActivity.isKm;
import static com.example.map2.MapsActivity.isMil;

public class SettingActivity extends AppCompatActivity {
    //  Variables
    private CheckBox cbMiles,cbKilometers;
    private Button btnSaveSetting;
    private ImageView ivCancel;
    private static boolean isMiles = false;
    private static boolean isKilometers= false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        //initializing the check buttoms
        cbMiles =(CheckBox)findViewById(R.id.cbMiles);
        cbKilometers =(CheckBox)findViewById(R.id.cbKilometers);
        checkDistance();

        //initializing the save button
        btnSaveSetting =(Button) findViewById(R.id.btnSaveSetting);
        ivCancel= (ImageView)findViewById(R.id.ivCancelSetting);
        //
        cbMiles.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                isMiles = true;
                isKilometers = false;
                cbKilometers.setChecked(false);
                cbMiles.setChecked(true);
            }
        });

        cbKilometers.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                isKilometers= true;
                isMiles= false;
                cbMiles.setChecked(false);
                cbKilometers.setChecked(true);
            }
        });

        btnSaveSetting.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                if(!cbMiles.isChecked()&&!cbKilometers.isChecked()){

                    Toast.makeText(SettingActivity.this,
                            "You have to check if you want to have distances in Miles or Kilometers"
                            ,Toast.LENGTH_SHORT).show();
                }else {
                    saveSetting();

                }

            }
        });

        ivCancel.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SettingActivity.this, MapsActivity.class);
                startActivity(intent);
            }
        });


    }

    private void saveSetting(){
        final DatabaseReference writeSetting = FirebaseDatabase.getInstance().getReference(
                "/Setting");

        //Writing Hashmap
        Map<String, Object> mHashmap = new HashMap<>();

        mHashmap.put("/"+UsersInfo.getUsername()+"/isKilometers/", isKilometers);
        mHashmap.put("/"+UsersInfo.getUsername()+"/isMiles/", isMiles);
        writeSetting.updateChildren(mHashmap);

        Toast.makeText(getApplicationContext(), "Setting has been saved", Toast.LENGTH_LONG).show();

    }


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

                     isKm = snapshot.child("isKilometers").getValue(Boolean.class);
                    isMil = snapshot.child("isMiles").getValue(Boolean.class);

                  //  Log.d(TAG, "the setting distance is in Miles"+isMil);
                  //  Log.d(TAG, "the setting distance is in Kilometers:"+isKm);
                    cbKilometers.setChecked(isKm);
                    cbMiles.setChecked(isMiles);

                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }

}