package com.nalamala.bardog;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.Calendar;
import java.util.List;
import java.util.UUID;

import de.hdodenhof.circleimageview.CircleImageView;
import pl.aprilapps.easyphotopicker.DefaultCallback;
import pl.aprilapps.easyphotopicker.EasyImage;

public class CreateDogFragment extends Fragment {

    CircleImageView dog_profile_image;
    EditText dogNameInput;
    RadioGroup isImmunizedInput;
    EditText dogTypeInput;
    EditText ownerNameInput;
    EditText ownerPhoneInput;
    EditText ownerAddress;
    EditText commentsInput;
    EditText birthYearInput;
    Button saveButton;
    Button cancelButton;

    DatabaseReference dataRef;

    boolean isUpdating = false;
    boolean didImageChange = false;
    Dog updateDogInfo;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_create_dog, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        final String currentUserUid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        dog_profile_image = getView().findViewById(R.id.dog_image);
        dogNameInput = getView().findViewById(R.id.dog_name);
        dogTypeInput = getView().findViewById(R.id.dog_type);
        birthYearInput = getView().findViewById(R.id.birth_date);
        isImmunizedInput = getView().findViewById(R.id.is_immunized);
        commentsInput = getView().findViewById(R.id.comments);
        ownerAddress = getView().findViewById(R.id.address);
        ownerNameInput = getView().findViewById(R.id.owner_name);
        ownerPhoneInput = getView().findViewById(R.id.owner_phone);

        saveButton = getView().findViewById(R.id.save_button);
        cancelButton = getView().findViewById(R.id.cancel_button);

        dataRef = FirebaseDatabase.getInstance().getReference();

        // if updating the dog information
        updateDogInfo = ((MainActivity) getActivity()).getExtraDog();
        if (updateDogInfo != null) {

            Glide.with(this).load(updateDogInfo.getProfileImage()).into(dog_profile_image);
            // glide is faster than Picasso.with(this).load(updateDogInfo.getProfileImage()).into(dog_profile_image);

            dogNameInput.setText(updateDogInfo.getDogName());

            if (updateDogInfo.getIsImmunized().equals("yes")) {
                RadioButton radioButton = (RadioButton) isImmunizedInput.getChildAt(0);
                isImmunizedInput.check(radioButton.getId());
            }
            else {
                RadioButton radioButton = (RadioButton) isImmunizedInput.getChildAt(1);
                isImmunizedInput.check(radioButton.getId());
            }

            dogTypeInput.setText(updateDogInfo.getdogType());
            ownerNameInput.setText(updateDogInfo.getOwnerName());
            ownerPhoneInput.setText(updateDogInfo.getOwnerPhone());
            commentsInput.setText(updateDogInfo.getComments());
            ownerAddress.setText(updateDogInfo.getOwnerAddress());
            birthYearInput.setText(updateDogInfo.getBirthDate());

            isUpdating = true;
        }
        else {
            // auto fill contact information
            dataRef.child(CommonFunctions.DATABASE_USERS_REF)
                    .child(currentUserUid).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    String userName = snapshot.child(CommonFunctions.USER_FULL_NAME).getValue(String.class);
                    String userPhone = snapshot.child(CommonFunctions.USER_PHONE_NUMBER).getValue(String.class);
                    String userAddress = snapshot.child(CommonFunctions.USER_ADDRESS).getValue(String.class);

                    ownerNameInput.setText(userName);
                    ownerPhoneInput.setText(userPhone);
                    ownerAddress.setText(userAddress);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }

        final LoadingBar bar = new LoadingBar(getActivity());

        // get camera permission
        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(getActivity(), new String[] {Manifest.permission.CAMERA}, 0);
        }

        // EasyImage is responsible for getting an image from the media
        EasyImage.configuration(getContext()).setImagesFolderName("My app images");

        dog_profile_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // EasyImage is used to load image from system
                EasyImage.openChooserWithGallery(getActivity(), "Please Pick Image", 0);
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity) getActivity()).selectHomeFragment();
            }
        });

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final String dogName = dogNameInput.getText().toString();
                final int isImmunizedChecked = isImmunizedInput.getCheckedRadioButtonId();
                final String dogType = dogTypeInput.getText().toString();
                final String ownerName = ownerNameInput.getText().toString();
                final String ownerPhone = ownerPhoneInput.getText().toString();
                final String comments = commentsInput.getText().toString();
                final String address = ownerAddress.getText().toString();
                final String birthYear = birthYearInput.getText().toString();

                if (dogName.matches("") || isImmunizedChecked == -1 || dogType.matches("") || ownerName.matches("") || birthYear.matches("")) {
                    Toast.makeText(getActivity(), R.string.fill_all, Toast.LENGTH_SHORT).show();
                    return;
                }
                if (!didImageChange && !isUpdating) {
                    Toast.makeText(getActivity(), R.string.change_image, Toast.LENGTH_SHORT).show();
                    return;
                }

                int year = Calendar.getInstance().get(Calendar.YEAR);
                if (Integer.parseInt(birthYear) > year || Integer.parseInt(birthYear) < year-100) {
                    Toast.makeText(getActivity(), R.string.change_year, Toast.LENGTH_SHORT).show();
                    return;
                }

                String isImmunized = ((RadioButton)getView().findViewById(isImmunizedChecked)).getText().toString();

                final String dogID;
                if (isUpdating) {
                    dogID = updateDogInfo.getID();
                } else {
                    dogID = UUID.randomUUID().toString();
                }

                if (didImageChange) {
                    // get image
                    BitmapDrawable drawable = (BitmapDrawable) dog_profile_image.getDrawable();
                    final Bitmap bitmap = drawable.getBitmap();

                    // reduce image size to reduce upload time
                    final Bitmap dogImageBitmap = CommonFunctions.ReduceBitmapSize(bitmap, 105000);

                    ByteArrayOutputStream bos = new ByteArrayOutputStream();
                    dogImageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos);

                    // save image in storage
                    final StorageReference reference = FirebaseStorage.getInstance().getReference()
                            .child(CommonFunctions.STORAGE_DOGS_IMAGES)
                            .child(currentUserUid)
                            .child(dogID + ".jpeg");

                    bar.startLoadingBar();

                    reference.putBytes(bos.toByteArray())
                            .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                                    reference.getDownloadUrl()
                                            .addOnSuccessListener(new OnSuccessListener<Uri>() {
                                                @Override
                                                public void onSuccess(Uri uri) {

                                                    // Once the image has been saved, updating the rest of the information
                                                    DatabaseReference currUser = dataRef.child(CommonFunctions.DATABASE_USERS_REF)
                                                            .child(currentUserUid).child(CommonFunctions.DATABASE_DOGS_REF).child(dogID);
                                                    currUser.child(CommonFunctions.DOG_NAME).setValue(dogName);
                                                    currUser.child(CommonFunctions.IS_IMMUNIZED).setValue(isImmunized);
                                                    currUser.child(CommonFunctions.DOG_TYPE).setValue(dogType);
                                                    currUser.child(CommonFunctions.OWNER_NAME).setValue(ownerName);
                                                    currUser.child(CommonFunctions.OWNER_PHONE).setValue(ownerPhone);
                                                    currUser.child(CommonFunctions.COMMENTS).setValue(comments);
                                                    currUser.child(CommonFunctions.OWNER_ADDRESS).setValue(address);
                                                    currUser.child(CommonFunctions.BIRTH_DATE).setValue(birthYear);
                                                    currUser.child(CommonFunctions.PROFILE_IMAGE).setValue(uri.toString());
                                                    currUser.child(CommonFunctions.DOG_ID).setValue(dogID);

                                                    if (isUpdating)
                                                        Toast.makeText(getActivity(), R.string.updated_successfully, Toast.LENGTH_SHORT).show();
                                                    else
                                                        Toast.makeText(getActivity(), R.string.created_successfully, Toast.LENGTH_SHORT).show();

                                                    Bitmap barcode = CommonFunctions.getBarcode(getContext(), dogID);

                                                    // open barcode in another activity
                                                    /*Intent update = new Intent(getActivity(), SeeBarcodeActivity.class);
                                                    update.putExtra("barcode", barcode);
                                                    startActivity(update);*/

                                                    bar.closeDialog();

                                                    ((MainActivity) getActivity()).selectHomeFragment();

                                                }
                                            });
                                }
                            });
                } else {
                    // update all the information
                    DatabaseReference currUser = dataRef.child(CommonFunctions.DATABASE_USERS_REF)
                            .child(currentUserUid).child(CommonFunctions.DATABASE_DOGS_REF).child(dogID);
                    currUser.child(CommonFunctions.DOG_NAME).setValue(dogName);
                    currUser.child(CommonFunctions.IS_IMMUNIZED).setValue(isImmunized);
                    currUser.child(CommonFunctions.DOG_TYPE).setValue(dogType);
                    currUser.child(CommonFunctions.OWNER_NAME).setValue(ownerName);
                    currUser.child(CommonFunctions.OWNER_PHONE).setValue(ownerPhone);
                    currUser.child(CommonFunctions.COMMENTS).setValue(comments);
                    currUser.child(CommonFunctions.OWNER_ADDRESS).setValue(address);
                    currUser.child(CommonFunctions.BIRTH_DATE).setValue(birthYear);

                    Toast.makeText(getActivity(), R.string.updated_successfully, Toast.LENGTH_SHORT).show();

                    ((MainActivity) getActivity()).selectHomeFragment();
                }
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        EasyImage.handleActivityResult(requestCode, resultCode, data, getActivity(), new DefaultCallback() {
            @Override
            public void onImagePickerError(Exception e, EasyImage.ImageSource source, int type) {
                // Some error handling
            }

            @Override
            public void onImagesPicked(@NonNull List<File> imageFiles, EasyImage.ImageSource source, int type) {
                Picasso.with(getActivity()).load(imageFiles.get(0)).into(dog_profile_image);

                // Image was updated successfully
                didImageChange = true;
            }
        });
    }
}
