package com.nalamala.bardog;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.multidex.MultiDex;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.view.View;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        MultiDex.install(this);

        // check internet connection
        ConnectivityManager connectivityManager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();

        if (activeNetwork == null) {
            // no internet - display message
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            final AlertDialog noInternet = builder.setMessage(R.string.no_internet_connection)
                .setCancelable(false)
                .setPositiveButton(R.string.connect_to_internet, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // try again
                        startActivity(new Intent(SplashActivity.this, SplashActivity.class));
                    }
                })
                .setNegativeButton(R.string.cancel_internet, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                }).create();

            noInternet.setOnShowListener( new DialogInterface.OnShowListener() {
                @Override
                public void onShow(DialogInterface arg0) {
                    // Change the buttons color
                    noInternet.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.RED);
                    noInternet.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.RED);
                }
            });

            noInternet.show();

        }
        else {
            final Handler handler = new Handler(Looper.getMainLooper());
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    // if logged load dogs and save in memory

                    Intent register = new Intent(SplashActivity.this, LoginActivity.class);
                    startActivity(register);
                    finish();
                }
            }, 3000);
        }
    }
}