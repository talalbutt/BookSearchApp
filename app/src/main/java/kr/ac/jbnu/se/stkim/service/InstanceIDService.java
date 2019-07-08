package kr.ac.jbnu.se.stkim.service;

import android.annotation.SuppressLint;
import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;

import com.google.firebase.iid.FirebaseInstanceIdService;


public class InstanceIDService extends FirebaseInstanceIdService {

    private static final String TAG = "FirebaseInstanceIDService";


    @SuppressLint("LongLogTag")
    @Override

    public void onTokenRefresh() {

        String refreshedToken = FirebaseInstanceId.getInstance().getToken();

        Log.d(TAG, "Refreshed token: " + refreshedToken);

        sendRegistrationToServer();

    }


    private void sendRegistrationToServer() {

    }

}

