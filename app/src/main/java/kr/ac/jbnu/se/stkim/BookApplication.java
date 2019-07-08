package kr.ac.jbnu.se.stkim;

import android.app.AlarmManager;
import android.app.Application;
import android.app.PendingIntent;
import android.content.Intent;

import com.google.firebase.FirebaseApp;

import java.util.Calendar;

import kr.ac.jbnu.se.stkim.receiver.AlarmReceiver;

public class BookApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        FirebaseApp.initializeApp(this);
    }
}
