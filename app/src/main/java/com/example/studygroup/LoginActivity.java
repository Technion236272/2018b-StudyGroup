package com.example.studygroup;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;


import java.util.Arrays;


public class LoginActivity extends Activity {

    LoginButton loginButton;
    CallbackManager callbackManager;
    private StorageReference usersStorageRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FacebookSdk.sdkInitialize(this.getApplicationContext());
        setContentView(R.layout.activity_login);
        loginButton = (LoginButton)findViewById(R.id.login_button);
        callbackManager = CallbackManager.Factory.create();
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Intent myIntent = new Intent(LoginActivity.this,
                        MainActivity.class);

                //here i want to add the user to the data base

                usersStorageRef = FirebaseStorage.getInstance().getReference();


                startActivity(myIntent);
            }

            @Override
            public void onCancel() {
                //Todo: add cancel message.
            }

            @Override
            public void onError(FacebookException error) {
                Toast.makeText(getApplicationContext() , "Error", Toast.LENGTH_LONG).show();
            }
        });


    }
    @Override
    protected void onActivityResult(int requestCode,int resultCode,Intent data){
        callbackManager.onActivityResult(requestCode,resultCode,data);
    }
}