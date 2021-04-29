package com.nalamala.bardog;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

public class DogsListAdapter extends ArrayAdapter<Dog> implements View.OnClickListener, Filterable {
    private ArrayList<Dog> dogs;
    private ArrayList<Dog> originalDogs;
    Context mContext;
    DatabaseReference dogsRef;

    public DogsListAdapter(ArrayList<Dog> dogs, Context context) {
        super(context, R.layout.dog_list_item, dogs);
        this.originalDogs = new ArrayList<Dog>(dogs);
        this.dogs = dogs;
        this.mContext = context;
    }

    // View lookup cache
    private static class ViewHolder {
        TextView dogName;
        Button barcode;
        Button delete;
    }

    @Override
    public void onClick(View v) {

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        final Dog dog = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        ViewHolder viewHolder; // view lookup cache stored in tag

        final View result;

        if (convertView == null) {

            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.dog_list_item, parent, false);
            viewHolder.dogName = convertView.findViewById(R.id.dog_name_text);
            viewHolder.barcode = convertView.findViewById(R.id.barcode_button);
            viewHolder.delete = convertView.findViewById(R.id.delete_button);

            result = convertView;

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
            result = convertView;
        }

        viewHolder.dogName.setText(dog.getDogName());

        viewHolder.barcode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View v) {

                Bitmap bitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888); // single color bitmap used for testing
                Bitmap barcode = CommonFunctions.getBarcode(getContext(), dog.getID());

                // open barcode in another activity
                Intent update = new Intent(getContext(), SeeBarcodeActivity.class);
                update.putExtra("barcode", barcode);
                update.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                getContext().startActivity(update);

            }
        });

        viewHolder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final String currentUserUid = FirebaseAuth.getInstance().getCurrentUser().getUid();

                // remove image
                final StorageReference reference = FirebaseStorage.getInstance().getReference()
                        .child(CommonFunctions.STORAGE_DOGS_IMAGES)
                        .child(currentUserUid)
                        .child(dog.getID() + ".jpeg");

                reference.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {

                        // remove dog details
                        dogsRef = FirebaseDatabase.getInstance().getReference().child(CommonFunctions.DATABASE_DOGS_REF).child(currentUserUid);
                        dogsRef.child(dog.getID()).setValue(null);

                        Toast.makeText(mContext, "הכלב נמחק", Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });

            }
        });

        // Return the completed view to render on screen
        return convertView;
    }

    @Override
    public Filter getFilter() {
        Filter filter = new Filter() {

            @SuppressWarnings("unchecked")
            @Override
            protected void publishResults(CharSequence constraint,FilterResults results) {
                dogs.clear();
                dogs.addAll((ArrayList<Dog>)results.values); // has the filtered values
                notifyDataSetChanged();
            }

            @Override
            protected FilterResults performFiltering(CharSequence filterID) {
                FilterResults results = new FilterResults();        // Holds the results of a filtering operation in values
                ArrayList<Dog> FilteredArrList = new ArrayList<Dog>();

                if (filterID == null || filterID.length() == 0) {
                    results.count = originalDogs.size();
                    results.values = originalDogs;
                } else {

                    for (int i = 0; i < dogs.size(); i++) {

                        String userID = dogs.get(i).getID();

                        if (userID.startsWith(filterID.toString())) {
                            FilteredArrList.add(new Dog(dogs.get(i).getDogName(),
                                    dogs.get(i).getIsImmunized(),
                                    dogs.get(i).getDesc(),
                                    dogs.get(i).getOwnerName(),
                                    dogs.get(i).getOwnerPhone(),
                                    dogs.get(i).getProfileImage(),
                                    dogs.get(i).getComments(),
                                    dogs.get(i).getBirthDate(),
                                    dogs.get(i).getID()));
                        }
                    }
                    // set the Filtered result to return
                    results.count = FilteredArrList.size();
                    results.values = FilteredArrList;
                }
                return results;
            }
        };
        return filter;

    }
}
