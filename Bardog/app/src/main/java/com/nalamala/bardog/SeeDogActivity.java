package com.nalamala.bardog;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;

import de.hdodenhof.circleimageview.CircleImageView;

public class SeeDogActivity extends AppCompatActivity {

    DatabaseReference usersRef;
    CircleImageView profileImage;
    LocaleHelper localeHelper;
    TextView dogName;
    TextView isImmuned;
    TextView dogType;
    TextView comments;
    TextView birthYear;
    TextView ownerName;
    TextView ownerPhone;
    TextView ownerAddress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // reseting the langauge before setting the content view
        localeHelper = new LocaleHelper(this);
        localeHelper.initLanguage();

        setContentView(R.layout.activity_see_dog);

        profileImage = findViewById(R.id.dog_image);
        dogName = findViewById(R.id.dog_name);
        isImmuned = findViewById(R.id.is_immunized);
        dogType = findViewById(R.id.dog_type);
        comments = findViewById(R.id.comments);
        birthYear = findViewById(R.id.birth_date);
        ownerName = findViewById(R.id.owner_name);
        ownerPhone = findViewById(R.id.owner_phone);
        ownerAddress = findViewById(R.id.owner_address);

        String uri = getIntent().getDataString();
        String dogID;
        String userID;
        if (uri != null && uri.contains("~")) {
            String[] bits = uri.split("~", 0);
            dogID = bits[bits.length-1];
            userID = bits[bits.length-2];
        }
        else {
            dogID = getIntent().getStringExtra(CommonFunctions.DOG_EXTRA);
            userID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        }


        usersRef = FirebaseDatabase.getInstance().getReference().child(CommonFunctions.DATABASE_USERS_REF);

        usersRef.child(userID).child(CommonFunctions.DATABASE_DOGS_REF).child(dogID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                String dogProfileImageUrl = snapshot.child(CommonFunctions.PROFILE_IMAGE).getValue().toString();

                Glide.with(getApplicationContext()).load(dogProfileImageUrl).
                        listener(new RequestListener<Drawable>() {
                            @Override
                            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                                return false;
                            }

                            @Override
                            public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                                // set dog info
                                dogName.setText(snapshot.child(CommonFunctions.DOG_NAME).getValue().toString());

                                String type = getString(R.string.type) + " " + snapshot.child(CommonFunctions.DOG_TYPE).getValue().toString();
                                dogType.setText(type);

                                int year = Calendar.getInstance().get(Calendar.YEAR);
                                int dogsBirthYear = Integer.parseInt(snapshot.child(CommonFunctions.BIRTH_DATE).getValue().toString());
                                String ageText = getString(R.string.dog_age) + " " + String.valueOf(year - dogsBirthYear);
                                birthYear.setText(ageText);

                                if (snapshot.child(CommonFunctions.IS_IMMUNIZED).getValue().toString().equals("yes"))
                                    isImmuned.setText(R.string.dog_immuned);
                                else {
                                    isImmuned.setTextSize(20f);
                                    isImmuned.setText(R.string.dog_not_immuned);
                                }

                                String extraComments = snapshot.child(CommonFunctions.COMMENTS).getValue().toString();
                                if (!extraComments.equals("")) {
                                    String commentsText = getString(R.string.additional_comments) + " " +extraComments;
                                    comments.setText(commentsText);
                                }
                                else {
                                    comments.setVisibility(View.GONE);
                                }

                                // set owner contact information
                                String ownerNameText = snapshot.child(CommonFunctions.OWNER_NAME).getValue().toString();
                                String ownerPhoneText = snapshot.child(CommonFunctions.OWNER_PHONE).getValue().toString();
                                String ownerAddressText = snapshot.child(CommonFunctions.OWNER_ADDRESS).getValue().toString();
                                String ownerInfoString;
                                boolean noContactInfo = true;

                                ownerInfoString = getString(R.string.owner_name) + " " + ownerNameText;
                                ownerName.setText(ownerInfoString);

                                if (!ownerPhoneText.equals("")) {
                                    ownerInfoString = getString(R.string.owner_phone) + " " + ownerPhoneText;
                                    ownerPhone.setText(ownerInfoString);
                                    noContactInfo = false;
                                }
                                else
                                    ownerPhone.setVisibility(View.GONE);

                                if (!ownerAddressText.equals("")) {
                                    ownerInfoString = getString(R.string.owner_address) + " " + ownerAddressText;
                                    ownerAddress.setText(ownerInfoString);
                                    noContactInfo = false;
                                }
                                else
                                    ownerAddress.setVisibility(View.GONE);

                                if (noContactInfo) {
                                    // user didn't provide any contact information therefore the email will show
                                    String emailContact = FirebaseAuth.getInstance().getCurrentUser().getEmail();
                                    ownerInfoString = getString(R.string.owner_email) + " " + emailContact;
                                    ownerPhone.setVisibility(View.VISIBLE);
                                    ownerPhone.setText(ownerInfoString);
                                }

                                return false;
                            }
                        })
                        .into(profileImage);
                // Picasso.with(SeeDogActivity.this).load(dogProfileImageUrl).into(profileImage);



            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
}
