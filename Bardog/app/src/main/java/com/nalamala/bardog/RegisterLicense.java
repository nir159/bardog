package com.nalamala.bardog;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RegisterLicense extends AppCompatActivity {

    EditText nameEditText;
    EditText phoneEditText;
    EditText addressEditText;
    CheckBox agreeToTerms;
    Button registerButton;
    Button termsButton;

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
        addressEditText = findViewById(R.id.address_edit_text);
        agreeToTerms = findViewById(R.id.agree_to_terms);
        termsButton = findViewById(R.id.terms_button);
        registerButton = findViewById(R.id.register_button);

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String name = nameEditText.getText().toString();
                final String phone = phoneEditText.getText().toString();
                final String address = addressEditText.getText().toString();

                if (name.matches("")) {
                    Toast.makeText(RegisterLicense.this, R.string.fill_all, Toast.LENGTH_SHORT).show();
                    return;
                }

                if (!agreeToTerms.isChecked()) {

                    Toast.makeText(RegisterLicense.this, R.string.agree_to_terms, Toast.LENGTH_SHORT).show();
                    return;
                }

                String currentUserUid = FirebaseAuth.getInstance().getCurrentUser().getUid();

                DatabaseReference currUser = FirebaseDatabase.getInstance().getReference().child(CommonFunctions.DATABASE_USERS_REF)
                        .child(currentUserUid);

                currUser.child(CommonFunctions.USER_FULL_NAME).setValue(name);
                currUser.child(CommonFunctions.USER_PHONE_NUMBER).setValue(phone);
                currUser.child(CommonFunctions.USER_ADDRESS).setValue(address);

                SharedPreferences.Editor editor = preferences.edit();
                editor.remove(CommonFunctions.LICENSE_PROCESS);
                editor.apply();

                Intent main = new Intent(RegisterLicense.this, MainActivity.class);
                startActivity(main);
                finish();
            }
        });

        termsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_VIEW);
                intent.addCategory(Intent.CATEGORY_BROWSABLE);
                intent.setData(Uri.parse("https://sites.google.com/view/bardog-privacy-policy/%D7%91%D7%99%D7%AA"));
                startActivity(intent);
            }
        });
    }
}