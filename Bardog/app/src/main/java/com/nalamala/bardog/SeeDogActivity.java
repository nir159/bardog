package com.nalamala.bardog;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
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
    TextView ownerInfo;
    TextView comments;
    TextView birthYear;

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
        ownerInfo = findViewById(R.id.owner_info);
        comments = findViewById(R.id.comments);
        birthYear = findViewById(R.id.birth_date);

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
                                dogName.setText(snapshot.child(CommonFunctions.DOG_NAME).getValue().toString());
                                if (snapshot.child(CommonFunctions.IS_IMMUNIZED).getValue().toString().equals("yes"))
                                    isImmuned.setText(R.string.dog_immuned);
                                else
                                    isImmuned.setText(R.string.dog_not_immuned);

                                String type = getString(R.string.type) + snapshot.child(CommonFunctions.DOG_TYPE).getValue().toString();
                                dogType.setText(type);

                                String ownerInfoString = snapshot.child(CommonFunctions.OWNER_NAME).getValue().toString() + " - " + snapshot.child(CommonFunctions.OWNER_PHONE).getValue().toString();
                                ownerInfo.setText(ownerInfoString);
                                comments.setText(snapshot.child(CommonFunctions.COMMENTS).getValue().toString());

                                int year = Calendar.getInstance().get(Calendar.YEAR);
                                int dogsBirthYear = Integer.parseInt(snapshot.child(CommonFunctions.BIRTH_DATE).getValue().toString());
                                String ageText = getString(R.string.dog_age) + String.valueOf(year - dogsBirthYear);
                                birthYear.setText(ageText);
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
