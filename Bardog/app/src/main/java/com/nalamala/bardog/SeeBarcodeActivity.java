package com.nalamala.bardog;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import java.io.File;
import java.io.FileOutputStream;

public class SeeBarcodeActivity extends AppCompatActivity {

    ImageView barcodeImage;
    Button goBackButton;
    Button shareButton;

    Bitmap barcodeMap;
    LocaleHelper localeHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // reseting the langauge before setting the content view
        localeHelper = new LocaleHelper(this);
        localeHelper.initLanguage();

        setContentView(R.layout.activity_see_barcode);

        barcodeImage = findViewById(R.id.barcode_image);
        goBackButton = findViewById(R.id.go_back_button);
        shareButton = findViewById(R.id.share_button);

        barcodeMap = getIntent().getParcelableExtra("barcode");
        barcodeImage.setImageBitmap(barcodeMap);

        goBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        shareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // share the barcode
                try {

                    // saving the image
                    File f = new File(getExternalMediaDirs()[0], File.separator + "bardog.png");
                    FileOutputStream fo = new FileOutputStream(f);
                    barcodeMap.compress(Bitmap.CompressFormat.PNG, 100, fo);
                    fo.flush();
                    fo.close();

                    // open share intent
                    Intent share = new Intent(Intent.ACTION_SEND);
                    share.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    Uri photoURI = FileProvider.getUriForFile(getApplicationContext(), getPackageName() + ".provider", f);
                    share.putExtra(Intent.EXTRA_STREAM, photoURI);
                    share.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    share.setType("image/*");
                    startActivity(Intent.createChooser(share, "Share Image Via"));

                } catch (Exception e) {
                    Log.i("shareImageBitmap:failure", e.toString());
                }
            }
        });
    }
}
