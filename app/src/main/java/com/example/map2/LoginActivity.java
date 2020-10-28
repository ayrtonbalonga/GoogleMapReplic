package com.example.map2;

        import android.app.Dialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG ="LoginActivity";
    private static final int ERROR_DIALOG_REQUEST = 9001;

    //Variables
    EditText Username,Password;
    UsersInfo userInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //if statement to check if all the google services are available on the device
        if (isServiceOK()){

            //initializing edittext for the username and password
            Username = (EditText) findViewById(R.id.txtUsername);
            Password = (EditText) findViewById(R.id.txtPassword);

            //initializing the the login button
            Button login = findViewById(R.id.btnLogin);
            login.setOnClickListener(new View.OnClickListener() {
                @RequiresApi(api = Build.VERSION_CODES.N)
                @Override
                public void onClick(View v) {
                    //when the user have typed the his login detailas, and click on the login button then call this method
                    loginUser(v);

                }
            });

            Button signIn = (Button) findViewById(R.id.btnSignIn);

            //initializing the the signin button
            signIn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    //When the user click on the signin button, then it will open sign in page
                    Intent intent = new Intent(LoginActivity.this, SignInActivity.class);
                    startActivity(intent);
                }
            });

        }

    }


    public boolean isServiceOK(){

        Log.d(TAG,"isServicesOK : Checking google servoces version");
        int available = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(LoginActivity.this);
        if(available == ConnectionResult.SUCCESS){
            //everything is working
            return true;

        }else if(GoogleApiAvailability.getInstance().isUserResolvableError(available)){
            // an error happen and the user can fix it
            Log.d(TAG,"isServicesOK: an error occured but we can fix it");
            Dialog dialog = GoogleApiAvailability.getInstance().getErrorDialog(LoginActivity.this,available,ERROR_DIALOG_REQUEST);
            dialog.show();

        }else{
            //display this message
            Toast.makeText(this,"You can't make map request",Toast.LENGTH_SHORT).show();
        }
        return false;
    }
        @RequiresApi(api = Build.VERSION_CODES.N)

    public void onClick(View view) {
        loginUser(view);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private Boolean validateUsername() {

        //storing the text that have been typed in the username textedit
        String val = Username.getText().toString().trim();

        //check if the edittext is empty
        if (val.isEmpty()) {

            //display message
            Username.setError("Field cannot be empty");
            return false;

        } else {

            Username.setError(null);

            return true;
        }
    }

    //check if the password is valid
    private Boolean validatePassword() {

        //storing the text that have been typed in the password textedit
        String val = Password.getText().toString();

        //check if the edittext is empty
        if (val.isEmpty()) {

            //display message
            Password.setError("Field connot be empty");
            return false;

        } else {
            Password.setError(null);
            return true;
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    //check if the username and password are valid
    public void loginUser(View view) {

        //if statement to check if the typed information in the edtittext is valid
        if (!validateUsername() | !validatePassword()) {

            return;

        } else {

            //call the method where it check if the user is in the database
            isUser();
        }
    }

    //checking if the username and passaword that the user have provided are in the database
    private void isUser() {

        //getting the username and password from the user
        final String tUsername = Username.getText().toString();
        final String tPasword = Password.getText().toString();

        //get the link of the database
        DatabaseReference reff = FirebaseDatabase.getInstance().getReference("Users");

        //check if the username is in the database
        Query checkUser = reff.orderByChild("username").equalTo(tUsername);
        checkUser.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                //if the user is register then
                if (snapshot.exists()) {

                    Username.setError(null);

                    //if the username is register then get the password from the database
                    String dbPassword = snapshot.child(tUsername).child("password").getValue(String.class);

                    //if the password typed is equal to the one the user have registered with
                    if (dbPassword.equals(tPasword)) {

                        Username.setError(null);

                        //get the user details
                        String dbfullname = snapshot.child(tUsername).child("fullname").getValue(String.class);
                        String dbphone = snapshot.child(tUsername).child("phone").getValue(String.class);
                        String dbemail = snapshot.child(tUsername).child("email").getValue(String.class);
                        String dbusername = snapshot.child(tUsername).child("username").getValue(String.class);

                        //store the details in this class
                        userInfo = new UsersInfo(dbfullname,dbusername,dbemail,dbphone,dbPassword);

                        //go in the MapActivity from
                        Intent intent = new Intent(getApplicationContext(), MapsActivity.class);
                        startActivity(intent);

                    } else {
                        Password.setError("wrong Password");
                        Password.requestFocus();
                    }
                } else {
                    Username.setError("No such User");
                    Username.requestFocus();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


}