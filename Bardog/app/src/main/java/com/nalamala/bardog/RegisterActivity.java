package com.nalamala.bardog;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.facebook.login.LoginManager;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RegisterActivity extends AppCompatActivity {

    Button loginLinkButton;
    Button registerButton;
    EditText emailEditText;
    EditText passwordEditText;

    FirebaseAuth mAuth;
    LocaleHelper localeHelper;

    boolean verifyProcess;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // reseting the langauge before setting the content view
        localeHelper = new LocaleHelper(this);
        localeHelper.initLanguage();

        setContentView(R.layout.activity_register);

        loginLinkButton = findViewById(R.id.login_link_button);
        registerButton = findViewById(R.id.register_button);
        emailEditText = findViewById(R.id.email_edit_text);
        passwordEditText = findViewById(R.id.password_edit_text);

        mAuth = FirebaseAuth.getInstance();

        verifyProcess = false;

        loginLinkButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent register = new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(register);
                finish();
            }
        });

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String email = emailEditText.getText().toString();
                final String password = passwordEditText.getText().toString();

                if (email.matches("") || password.matches("")) {
                    Toast.makeText(RegisterActivity.this, R.string.fill_all, Toast.LENGTH_SHORT).show();
                    return;
                }

                // verify email
                String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";

                if (!email.matches(emailPattern)) {
                    Toast.makeText(RegisterActivity.this, R.string.invalid_email, Toast.LENGTH_SHORT).show();
                    return;
                }

                // verify password
                if (password.length() < 8)
                {
                    Toast.makeText(RegisterActivity.this, R.string.invalid_password, Toast.LENGTH_SHORT).show();
                    return;
                }


                final LoadingBar bar = new LoadingBar(RegisterActivity.this);

                bar.startLoadingBar();

                mAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(RegisterActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {

                                    // verify user email
                                    final FirebaseUser user = mAuth.getCurrentUser();
                                    user.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {

                                                Toast.makeText(RegisterActivity.this, R.string.verify_email_sent, Toast.LENGTH_SHORT).show();

                                                AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
                                                final AlertDialog verifyEmailDialog = builder
                                                        .setTitle(R.string.verify_dialog_title)
                                                        .setMessage(R.string.verify_dialog_message)
                                                        .setCancelable(false)
                                                        .setPositiveButton(R.string.verified, new DialogInterface.OnClickListener() {
                                                            @Override
                                                            public void onClick(DialogInterface dialog, int which) {

                                                                FirebaseAuth.getInstance().signOut();
                                                                mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                                                    @Override
                                                                    public void onComplete(@NonNull Task<AuthResult> task) {
                                                                        if (task.isSuccessful()) {

                                                                            FirebaseUser user = mAuth.getCurrentUser();

                                                                            if (user.isEmailVerified()) {

                                                                                verifyProcess = false;

                                                                                Toast.makeText(RegisterActivity.this, R.string.user_created_successfully, Toast.LENGTH_SHORT).show();

                                                                                Intent license = new Intent(RegisterActivity.this, RegisterLicense.class);
                                                                                startActivity(license);

                                                                                finish();

                                                                            }
                                                                            else {
                                                                                Toast.makeText(RegisterActivity.this, R.string.not_verified, Toast.LENGTH_SHORT).show();
                                                                                user.delete();
                                                                                FirebaseAuth.getInstance().signOut();
                                                                                verifyProcess = false;
                                                                            }

                                                                        }
                                                                        else {
                                                                            Toast.makeText(RegisterActivity.this, R.string.not_verified, Toast.LENGTH_SHORT).show();
                                                                            user.delete();
                                                                            FirebaseAuth.getInstance().signOut();
                                                                            verifyProcess = false;
                                                                        }
                                                                    }
                                                                });

                                                            }
                                                        })
                                                        .setNegativeButton(R.string.cancel_button, new DialogInterface.OnClickListener() {
                                                            @Override
                                                            public void onClick(DialogInterface dialog, int which) {
                                                                user.delete();
                                                                FirebaseAuth.getInstance().signOut();
                                                                verifyProcess = false;
                                                            }
                                                        }).create();

                                                verifyEmailDialog.setOnShowListener( new DialogInterface.OnShowListener() {
                                                    @Override
                                                    public void onShow(DialogInterface arg0) {
                                                        // Change the buttons color
                                                        verifyEmailDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.RED);
                                                        verifyEmailDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.RED);
                                                    }
                                                });

                                                verifyProcess = true;
                                                bar.closeDialog();
                                                verifyEmailDialog.show();

                                            }
                                            else {
                                                Toast.makeText(RegisterActivity.this, R.string.invalid_email, Toast.LENGTH_SHORT).show();
                                                return;
                                            }
                                        }
                                    });

                                } else {
                                    // If sign up fails, display a message to the user.
                                    bar.closeDialog();
                                    Toast.makeText(RegisterActivity.this, R.string.user_exists, Toast.LENGTH_SHORT).show();
                                    Log.i("FIREBASE AUTH ERROR", "createUserWithEmail:failure", task.getException());
                                }
                            }
                        });
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (verifyProcess) {
            final FirebaseUser user = mAuth.getCurrentUser();
            user.delete();

            FirebaseAuth.getInstance().signOut();
        }
    }
}
