package com.example.map2;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class SignInActivity extends AppCompatActivity {
    //variables
    private EditText txtFullName, txtUsername, txtEmail, txtPhone, txtPassword;
    private String fname, username, email, phone, password;

    //initializing the class
    Users users;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        //initializing the text edits
        txtFullName = (EditText) findViewById(R.id.txtFullName);
        txtUsername = (EditText) findViewById(R.id.txtUsername);
        txtEmail = (EditText) findViewById(R.id.txtEmail);
        txtPhone = (EditText) findViewById(R.id.txtPhone);
        txtPassword = (EditText) findViewById(R.id.txtPassword);

        //intializing the button
        Button btnSignIn = (Button) findViewById(R.id.btnSignIn);

        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //when the button is clicked then save in the database
                loginUser(v);
            }
        });

    }

    //checking if all the fields are valid
    public void loginUser(View view) {
        if (!validateFullname() | !validatePassword() | !validateEmail() | !validateUsername() | !validatePhone()) {
            return;
        } else {
            //if the user details are valid then save in the database
            isUser();
        }

    }

    //save the information
    private void isUser() {

        final String tUsername = txtUsername.getText().toString();

        //final DatabaseReference reff = FirebaseDatabase.getInstance().getReference("Users");
        final DatabaseReference reff = FirebaseDatabase.getInstance().getReference("/Users/"+tUsername);

        // Query checkUser = reff.orderByChild("username").equalTo(tUsername);
        final DatabaseReference writetoDb = FirebaseDatabase.getInstance().getReference("Users");

        reff.orderByChild(tUsername).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                //check if the informations are in the database
                if (!snapshot.exists()) {
                    txtUsername.setError(null);

                    //store the information in the variables
                    fname = txtFullName.getText().toString().trim();
                    username = txtUsername.getText().toString().trim();
                    email = txtEmail.getText().toString().trim();
                    phone = txtPhone.getText().toString().trim();
                    password = txtPassword.getText().toString();

                    //set the informations in class
                    users = new Users(fname, username, email, phone, password);

                    //write in the from the class
                    writetoDb.child(username).setValue(users);

                    //display the message
                    Toast.makeText(getApplicationContext(), "You have been register", Toast.LENGTH_SHORT).show();

                    // close the signin activity and open the Login activity
                    Intent intent = new Intent(SignInActivity.this, LoginActivity.class);
                    startActivity(intent);

                } else {
                    txtUsername.setError("User already used ");
                    txtUsername.requestFocus();
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    //check if the fullname is valid
    private Boolean validateFullname() {

        String val = txtFullName.getText().toString().trim();

        if (val.isEmpty()) {
            txtFullName.setError("Field cannot be empty");
            return false;
        } else {
            txtFullName.setError(null);

            return true;
        }

    }

    //Check if the username is valid
    private Boolean validateUsername() {

        String val = txtUsername.getText().toString().trim();

        if (val.isEmpty()) {
            txtUsername.setError("Field cannot be empty");
            return false;
        } else {
            txtUsername.setError(null);

            return true;
        }

    }

    //Check if the email is valid
    private Boolean validateEmail() {

        String val = txtEmail.getText().toString().trim();

        if (val.isEmpty()) {
            txtEmail.setError("Field cannot be empty");
            return false;
        } else {
            txtEmail.setError(null);

            return true;
        }

    }

    //chech if the Phone is valid
    private Boolean validatePhone() {

        String val = txtPhone.getText().toString().trim();

        if (val.isEmpty() || (val.length() != 10)) {
            txtPhone.setError("Phone must be 10 digit");
            return false;
        } else {
            txtPhone.setError(null);
            return true;
        }


    }

    //chech if the password is valid
    private Boolean validatePassword() {

        String val = txtPassword.getText().toString().trim();

        if (val.isEmpty()) {
            txtPassword.setError("Field cannot be empty");
            return false;
        } else {
            txtPassword.setError(null);

            return true;
        }

    }




}