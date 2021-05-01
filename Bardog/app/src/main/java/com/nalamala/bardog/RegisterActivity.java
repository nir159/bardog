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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class RegisterActivity extends AppCompatActivity {

    Button loginLinkButton;
    Button registerButton;
    EditText emailEditText;
    EditText passwordEditText;

    FirebaseAuth mAuth;
    LocaleHelper localeHelper;

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
                    Toast.makeText(RegisterActivity.this, "אנא מלא את כל השדות", Toast.LENGTH_SHORT).show();
                    return;
                }

                // verify email
                String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";

                if (!email.matches(emailPattern))
                {
                    Toast.makeText(RegisterActivity.this,"אימייל שגוי",Toast.LENGTH_SHORT).show();
                    return;
                }


                final LoadingBar bar = new LoadingBar(RegisterActivity.this);

                bar.startLoadingBar();

                mAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(RegisterActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {

                                    // user logged
                                    FirebaseUser user = mAuth.getCurrentUser();

                                    Toast.makeText(RegisterActivity.this, "המשתמש נוצר בהצלחה", Toast.LENGTH_SHORT).show();

                                    Intent tutorial = new Intent(RegisterActivity.this, TutorialActivity.class);
                                    startActivity(tutorial);

                                    finish();

                                } else {
                                    // If sign in fails, display a message to the user.
                                    bar.closeDialog();
                                    Toast.makeText(RegisterActivity.this, "המשתמש כבר קיים נסה להתחבר", Toast.LENGTH_SHORT).show();
                                    Log.i("FIREBASE AUTH ERROR", "createUserWithEmail:failure", task.getException());
                                }
                            }
                        });
            }
        });
    }
}
