package com.nalamala.bardog;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class HomeFragment extends Fragment {

    DatabaseReference dogsRef;
    ListView dogsListView;
    ArrayList<Dog> listDogs;
    DogsListAdapter adapter;
    LinearLayout noDogsLayout;
    Button createDogButton;
    Context mContext;
    // EditText filterEditText;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        final String currentUserUid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        dogsListView = getView().findViewById(R.id.users_list);
        noDogsLayout = getView().findViewById(R.id.no_dogs_layout);
        createDogButton = getView().findViewById(R.id.create_now_button);
        //filterEditText = findViewById(R.id.filter_edit_text);

        listDogs = new ArrayList<>();
        dogsRef = FirebaseDatabase.getInstance().getReference().child(CommonFunctions.DATABASE_USERS_REF)
                .child(currentUserUid).child(CommonFunctions.DATABASE_DOGS_REF);
        mContext = getContext();

        dogsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                listDogs.clear();

                for (DataSnapshot ds : dataSnapshot.getChildren()) {

                    Dog currDog = new Dog(ds.child(CommonFunctions.DOG_NAME).getValue(String.class),
                            ds.child(CommonFunctions.IS_IMMUNIZED).getValue(String.class),
                            ds.child(CommonFunctions.DOG_TYPE).getValue(String.class),
                            ds.child(CommonFunctions.OWNER_NAME).getValue(String.class),
                            ds.child(CommonFunctions.OWNER_PHONE).getValue(String.class),
                            ds.child(CommonFunctions.COMMENTS).getValue(String.class),
                            ds.child(CommonFunctions.BIRTH_DATE).getValue(String.class),
                            ds.child(CommonFunctions.PROFILE_IMAGE).getValue(String.class),
                            ds.child(CommonFunctions.DOG_ID).getValue(String.class));

                    listDogs.add(currDog);
                }

                if (listDogs.size() == 0) {
                    noDogsLayout.setVisibility(View.VISIBLE);
                    dogsListView.setVisibility(View.INVISIBLE);
                } else {
                    noDogsLayout.setVisibility(View.INVISIBLE);
                    dogsListView.setVisibility(View.VISIBLE);
                }

                adapter = new DogsListAdapter(listDogs, mContext);

                dogsListView.setAdapter(adapter);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
            }
        });

        dogsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ((MainActivity) getActivity()).addExtraDog(listDogs.get(position));
                ((MainActivity) getActivity()).selectCreateDog();
            }
        });

        createDogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity) getActivity()).selectCreateDog();
            }
        });

        /*
        filter is no longer necessary
        filterEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                adapter.getFilter().filter(s.toString());
                dogsListView.setAdapter(adapter);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        */

    }
}
