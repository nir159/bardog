package com.nalamala.bardog;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RegisterLicense extends AppCompatActivity {

    EditText nameEditText;
    EditText phoneEditText;
    Button registerButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_license);

        final SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        if(preferences.getString(CommonFunctions.LICENSE_PROCESS, null) == null) {
            // User can't leave until accepting the terms and conditions
            SharedPreferences.Editor editor = preferences.edit();
            editor.putString(CommonFunctions.LICENSE_PROCESS, "true");
            editor.apply();
        }

        nameEditText = findViewById(R.id.name_edit_text);
        phoneEditText = findViewById(R.id.phone_edit_text);
        registerButton = findViewById(R.id.register_button);

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String name = nameEditText.getText().toString();
                final String phone = phoneEditText.getText().toString();

                if (name.matches("") || phone.matches("")) {
                    Toast.makeText(RegisterLicense.this, R.string.fill_all, Toast.LENGTH_SHORT).show();
                    return;
                }

                String currentUserUid = FirebaseAuth.getInstance().getCurrentUser().getUid();

                DatabaseReference currUser = FirebaseDatabase.getInstance().getReference().child(CommonFunctions.DATABASE_USERS_REF)
                        .child(currentUserUid);

                currUser.child(CommonFunctions.USER_FULL_NAME).setValue(name);
                currUser.child(CommonFunctions.USER_PHONE_NUMBER).setValue(phone);

                SharedPreferences.Editor editor = preferences.edit();
                editor.remove(CommonFunctions.LICENSE_PROCESS);
                editor.apply();
            }
        });
    }
}