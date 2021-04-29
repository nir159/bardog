package com.nalamala.bardog;

import android.app.Activity;
import android.app.AlertDialog;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;

public class LoadingBar {

    Activity mActivity;
    AlertDialog alertDialog;

    public LoadingBar(Activity activity) {
        mActivity = activity;
    }

    void startLoadingBar() {
        AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);

        LayoutInflater inflater = mActivity.getLayoutInflater();
        builder.setView(inflater.inflate(R.layout.loading_bar, null));
        builder.setCancelable(false);

        alertDialog = builder.create();
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        alertDialog.show();
    }

    void closeDialog() {
        alertDialog.dismiss();
    }
}
