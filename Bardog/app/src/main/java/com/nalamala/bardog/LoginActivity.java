package com.nalamala.bardog;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

public class LoginActivity extends AppCompatActivity {

    private static final int GOOGLE_SIGN_IN_FLAG = 129;

    Button googleLoginButton;
    LoginButton facebookLoginButton;
    Button registerButton;
    Button loginButton;
    EditText emailEditText;
    EditText passwordEditText;

    CallbackManager mCallbackManager;
    GoogleSignInClient mGoogleSignInClient;
    FirebaseAuth mAuth;
    LoadingBar bar;
    boolean facebookCanceled = false;

    @Override
    public void onStart() {
        super.onStart();

        try {
            mAuth = FirebaseAuth.getInstance();

            FirebaseUser currentUser = mAuth.getCurrentUser();
            if (currentUser != null) {
                Intent homepage = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(homepage);

                // The user is not suppose to get to this activity if logged
                finish();

            }
        }
        catch (Exception e) {

        }

    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        googleLoginButton = findViewById(R.id.google_login_button);
        facebookLoginButton = findViewById(R.id.facebook_login_button);
        registerButton = findViewById(R.id.resgister_link_button);
        loginButton = findViewById(R.id.login_button);
        emailEditText = findViewById(R.id.email_edit_text);
        passwordEditText = findViewById(R.id.password_edit_text);

        // Fasebook Sign In
        mCallbackManager = CallbackManager.Factory.create();
        facebookLoginButton.setPermissions("email", "public_profile");


        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                // the client ID of web client from Google Console configuration
                .requestIdToken("993528266298-2vfs00v66bv8u81lg2dbr7gs76ne1odp.apps.googleusercontent.com")
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        googleLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent signInIntent = mGoogleSignInClient.getSignInIntent();
                startActivityForResult(signInIntent, GOOGLE_SIGN_IN_FLAG);
            }
        });

        facebookLoginButton.registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                handleFacebookAccessToken(loginResult.getAccessToken());
            }

            @Override
            public void onCancel() {
                facebookCanceled = true;
            }

            @Override
            public void onError(FacebookException error) {
                Log.d("ERROR FACEBOOK SIGN IN", "facebook:onError", error);
                facebookCanceled = true;
            }
        });

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent register = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(register);
                finish();
            }
        });

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String email = emailEditText.getText().toString();
                final String password = passwordEditText.getText().toString();

                if (email.matches("") || password.matches("")) {
                    Toast.makeText(LoginActivity.this, "אנא מלא את כל השדות", Toast.LENGTH_SHORT).show();
                    return;
                }

                // verify email
                String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";

                if (!email.matches(emailPattern))
                {
                    Toast.makeText(LoginActivity.this,"אימייל שגוי",Toast.LENGTH_SHORT).show();
                    return;
                }


                final LoadingBar bar = new LoadingBar(LoginActivity.this);

                bar.startLoadingBar();

                mAuth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {

                                    // user logged
                                    FirebaseUser user = mAuth.getCurrentUser();

                                    Toast.makeText(LoginActivity.this, "מחובר", Toast.LENGTH_SHORT).show();

                                    Intent tutorial = new Intent(LoginActivity.this, MainActivity.class);
                                    startActivity(tutorial);

                                    finish();

                                } else {
                                    // If sign in fails, display a message to the user.
                                    bar.closeDialog();
                                    Toast.makeText(LoginActivity.this, "סיסמא או אימייל שגויים", Toast.LENGTH_SHORT).show();
                                    Log.i("FIREBASE AUTH ERROR", "signInWithEmail:failure", task.getException());
                                }
                            }
                        });

            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Pass the activity result back to the Facebook SDK
        mCallbackManager.onActivityResult(requestCode, resultCode, data);

        bar = new LoadingBar(this);
        bar.startLoadingBar();

        if (facebookCanceled)
            bar.closeDialog();

        if (requestCode == GOOGLE_SIGN_IN_FLAG) {

            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);

            try {

                GoogleSignInAccount account = task.getResult(ApiException.class);

                firebaseAuthWithGoogle(account.getIdToken());

            } catch (ApiException e) {
                // The ApiException status code indicates the detailed failure reason.
                // Please refer to the GoogleSignInStatusCodes class reference for more information.
                Toast.makeText(LoginActivity.this,  "אירעה שגיאה נסה דרך אחרת" + e.toString(), Toast.LENGTH_LONG).show();
                bar.closeDialog();
            }
        }
    }

    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            FirebaseUser user = mAuth.getCurrentUser();
                            Intent tutorial = new Intent(LoginActivity.this, MainActivity.class);
                            startActivity(tutorial);

                            bar.closeDialog();
                            finish();
                        } else {
                            // If sign in fails, display a message to the user.
                            Toast.makeText(LoginActivity.this, "המייל כבר מקושר לגוגל. נסה להתחבר בעזרת גוגל", Toast.LENGTH_LONG).show();
                            bar.closeDialog();
                        }
                    }
                });
    }

    private void handleFacebookAccessToken(AccessToken token) {
        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());

        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if (task.isSuccessful()) {

                            // Sign in success, update UI with the signed-in user's information
                            Log.i("FIREBASE GOOGLE LOGIN", "signInWithCredential:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            Intent tutorial = new Intent(LoginActivity.this, MainActivity.class);
                            startActivity(tutorial);

                            bar.closeDialog();
                            finish();
                        } else {
                            // If sign in fails, display a message to the user.
                            Toast.makeText(LoginActivity.this, "אירעה שגיאה נסה דרך אחרת", Toast.LENGTH_LONG).show();
                            LoginManager.getInstance().logOut();
                            bar.closeDialog();
                        }
                    }
                });
    }


}
