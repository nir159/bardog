package com.nalamala.bardog;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import androidx.appcompat.app.AlertDialog;

public class NetworkChangeListener extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        // check internet connection
        ConnectivityManager connectivityManager = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();

        if (activeNetwork == null) {
            // no internet - display message
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            final AlertDialog noInternet = builder.setMessage(R.string.no_internet_connection)
                    .setCancelable(false)
                    .setPositiveButton(R.string.connect_to_internet, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // try again
                            context.startActivity(new Intent(context, SplashActivity.class));
                        }
                    }).create();

            noInternet.setOnShowListener(new DialogInterface.OnShowListener() {
                @Override
                public void onShow(DialogInterface arg0) {
                    // Change the buttons color
                    noInternet.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.RED);
                    noInternet.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.RED);
                }
            });

            noInternet.show();
        }
    }
}
