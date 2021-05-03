package com.nalamala.bardog;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.facebook.login.LoginManager;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.StorageReference;

import java.util.Locale;

public class SettingsFragment extends Fragment {

    final String[] languages = {"English", "עברית"};
    Button languageButton;
    Button deleteAccButton;
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

        languageButton = getView().findViewById(R.id.language_button);
        deleteAccButton = getView().findViewById(R.id.delete_user);

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

        deleteAccButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // making sure the user wants to continue
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                final AlertDialog areYouSure = builder.setMessage(R.string.are_you_sure)
                        .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                // Delete Account
                                final String currentUserUid = FirebaseAuth.getInstance().getCurrentUser().getUid();

                                final StorageReference reference = FirebaseStorage.getInstance().getReference()
                                        .child(CommonFunctions.STORAGE_DOGS_IMAGES)
                                        .child(currentUserUid);

                                reference.listAll().addOnSuccessListener(new OnSuccessListener<ListResult>() {
                                    @Override
                                    public void onSuccess(ListResult listResult) {
                                        // remove storage images
                                        for (StorageReference ref : listResult.getItems()) {
                                            ref.delete();
                                        }

                                        // remove the user data
                                        DatabaseReference dataRef = FirebaseDatabase.getInstance().getReference();
                                        dataRef.child(CommonFunctions.DATABASE_USERS_REF)
                                                .child(currentUserUid).setValue(null);

                                        // remove user
                                        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                                        user.delete();

                                        // logout
                                        FirebaseAuth.getInstance().signOut();
                                        LoginManager.getInstance().logOut();

                                        // go to login screen
                                        ((MainActivity) getActivity()).exitActivity();
                                    }
                                });
                            }
                        })
                        .setNegativeButton(R.string.cancel_button, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        }).create();

                areYouSure.setOnShowListener(new DialogInterface.OnShowListener() {
                    @Override
                    public void onShow(DialogInterface arg0) {
                        // Change the buttons color
                        areYouSure.getButton(androidx.appcompat.app.AlertDialog.BUTTON_POSITIVE).setTextColor(Color.RED);
                        areYouSure.getButton(androidx.appcompat.app.AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.RED);
                    }
                });

                areYouSure.show();
            }
        });
    }
}
