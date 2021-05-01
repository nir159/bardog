package com.nalamala.bardog;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class TutorialActivity extends AppCompatActivity {

    Button okButton;
    LocaleHelper localeHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // reseting the langauge before setting the content view
        localeHelper = new LocaleHelper(this);
        localeHelper.initLanguage();

        setContentView(R.layout.activity_tutorial);

        okButton = findViewById(R.id.ok_button);

        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent homepage = new Intent(TutorialActivity.this, MainActivity.class);
                startActivity(homepage);
                finish();
            }
        });
    }
}
