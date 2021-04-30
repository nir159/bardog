package com.nalamala.bardog;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.util.Locale;

public class SettingsFragment extends Fragment {

    final String[] languages = {"English", "עברית"};
    Button languageButton;
    LocaleHelper localeHelper;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_settings, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        localeHelper = new LocaleHelper(getContext());
        localeHelper = new LocaleHelper(getContext());

        languageButton = getView().findViewById(R.id.language_button);
        languageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle("Choose Language");
                builder.setSingleChoiceItems(languages, -1, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        boolean changed = false;
                        switch (which) {
                            case 0:
                                changed = localeHelper.setNewLanguage("en");
                                break;
                            case 1:
                                changed = localeHelper.setNewLanguage("iw");
                                break;
                        }

                        dialog.dismiss();
                        if (changed) getActivity().recreate();

                    }

                }).create().show();
            }
        });
    }
}
