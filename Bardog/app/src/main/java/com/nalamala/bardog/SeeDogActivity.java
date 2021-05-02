package com.nalamala.bardog;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import de.hdodenhof.circleimageview.CircleImageView;

public class SeeDogActivity extends AppCompatActivity {

    DatabaseReference usersRef;
    CircleImageView profileImage;
    LocaleHelper localeHelper;
    TextView dogName;
    TextView isImmuned;
    TextView desc;
    TextView ownerInfo;
    TextView comments;
    TextView birthDate;

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
        desc = findViewById(R.id.description);
        ownerInfo = findViewById(R.id.owner_info);
        comments = findViewById(R.id.comments);
        birthDate = findViewById(R.id.birth_date);

        String uri = getIntent().getDataString();
        String[] bits = uri.split("~", 0);
        String dogID = bits[bits.length-1];
        String userID = bits[bits.length-2];

        usersRef = FirebaseDatabase.getInstance().getReference().child(CommonFunctions.DATABASE_USERS_REF);

        usersRef.child(userID).child(CommonFunctions.DATABASE_DOGS_REF).child(dogID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                String dogProfileImageUrl = snapshot.child(CommonFunctions.PROFILE_IMAGE).getValue().toString();

                Glide.with(getApplicationContext()).load(dogProfileImageUrl).into(profileImage);
                // Picasso.with(SeeDogActivity.this).load(dogProfileImageUrl).into(profileImage);

                dogName.setText(snapshot.child(CommonFunctions.DOG_NAME).getValue().toString());
                isImmuned.setText(snapshot.child(CommonFunctions.IS_IMMUNIZED).getValue().toString());
                desc.setText(snapshot.child(CommonFunctions.DOG_TYPE).getValue().toString());
                ownerInfo.setText(snapshot.child(CommonFunctions.OWNER_NAME).getValue().toString() + " - " + snapshot.child(CommonFunctions.OWNER_PHONE).getValue().toString());
                comments.setText(snapshot.child(CommonFunctions.COMMENTS).getValue().toString());
                birthDate.setText("Birth Date: " + snapshot.child(CommonFunctions.BIRTH_DATE).getValue().toString());

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
}
