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
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.List;
import java.util.UUID;

import de.hdodenhof.circleimageview.CircleImageView;
import pl.aprilapps.easyphotopicker.DefaultCallback;
import pl.aprilapps.easyphotopicker.EasyImage;

public class CreateDogFragment extends Fragment {

    CircleImageView dog_profile_image;
    EditText dogNameInput;
    EditText isImmunizedInput;
    EditText descInput;
    EditText ownerNameInput;
    EditText ownerPhoneInput;
    EditText commentsInput;
    EditText birthDateInput;
    Button saveButton;
    Button cancelButton;

    DatabaseReference DataRef;

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
        isImmunizedInput = getView().findViewById(R.id.is_immunized);
        descInput = getView().findViewById(R.id.description);
        ownerNameInput = getView().findViewById(R.id.owner_name);
        ownerPhoneInput = getView().findViewById(R.id.owner_phone);
        commentsInput = getView().findViewById(R.id.comments);
        birthDateInput = getView().findViewById(R.id.birth_date);
        saveButton = getView().findViewById(R.id.save_button);
        cancelButton = getView().findViewById(R.id.cancel_button);

        DataRef = FirebaseDatabase.getInstance().getReference();

        // if updating the dog information
        updateDogInfo = ((MainActivity) getActivity()).getExtraDog();
        if (updateDogInfo != null) {

            Glide.with(this).load(updateDogInfo.getProfileImage()).into(dog_profile_image);
            // glide is faster than Picasso.with(this).load(updateDogInfo.getProfileImage()).into(dog_profile_image);

            dogNameInput.setText(updateDogInfo.getDogName());
            isImmunizedInput.setText(updateDogInfo.getIsImmunized());
            descInput.setText(updateDogInfo.getDesc());
            ownerNameInput.setText(updateDogInfo.getOwnerName());
            ownerPhoneInput.setText(updateDogInfo.getOwnerPhone());
            commentsInput.setText(updateDogInfo.getComments());
            birthDateInput.setText(updateDogInfo.getBirthDate());

            isUpdating = true;
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
                final String isImmunized = isImmunizedInput.getText().toString();
                final String desc = descInput.getText().toString();
                final String ownerName = ownerNameInput.getText().toString();
                final String ownerPhone = ownerPhoneInput.getText().toString();
                final String comments = commentsInput.getText().toString();
                final String birthDate = birthDateInput.getText().toString();

                if (dogName.matches("") || isImmunized.matches("") || desc.matches("") || ownerName.matches("") || ownerPhone.matches("") || birthDate.matches("")) {
                    Toast.makeText(getActivity(), "אנא מלא את כל השדות", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (!didImageChange && !isUpdating) {
                    Toast.makeText(getActivity(), "הקש על התמונה כדי להחליף אותה", Toast.LENGTH_SHORT).show();
                    return;
                }

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
                                                    DatabaseReference currUser = DataRef.child(CommonFunctions.DATABASE_DOGS_REF)
                                                            .child(currentUserUid).child(dogID);
                                                    currUser.child(CommonFunctions.DOG_NAME).setValue(dogName);
                                                    currUser.child(CommonFunctions.IS_IMMUNIZED).setValue(isImmunized);
                                                    currUser.child(CommonFunctions.DOG_DESCRIPTION).setValue(desc);
                                                    currUser.child(CommonFunctions.OWNER_NAME).setValue(ownerName);
                                                    currUser.child(CommonFunctions.OWNER_PHONE).setValue(ownerPhone);
                                                    currUser.child(CommonFunctions.COMMENTS).setValue(comments);
                                                    currUser.child(CommonFunctions.BIRTH_DATE).setValue(birthDate);
                                                    currUser.child(CommonFunctions.PROFILE_IMAGE).setValue(uri.toString());
                                                    currUser.child(CommonFunctions.DOG_ID).setValue(dogID);

                                                    if (isUpdating)
                                                        Toast.makeText(getActivity(), "הכלב עודכן בהצלחה!", Toast.LENGTH_SHORT).show();
                                                    else
                                                        Toast.makeText(getActivity(), "הכלב נוצר בהצלחה!", Toast.LENGTH_SHORT).show();

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
                    DatabaseReference currUser = DataRef.child(CommonFunctions.DATABASE_DOGS_REF)
                            .child(currentUserUid).child(dogID);
                    currUser.child(CommonFunctions.DOG_NAME).setValue(dogName);
                    currUser.child(CommonFunctions.IS_IMMUNIZED).setValue(isImmunized);
                    currUser.child(CommonFunctions.DOG_DESCRIPTION).setValue(desc);
                    currUser.child(CommonFunctions.OWNER_NAME).setValue(ownerName);
                    currUser.child(CommonFunctions.OWNER_PHONE).setValue(ownerPhone);
                    currUser.child(CommonFunctions.COMMENTS).setValue(comments);
                    currUser.child(CommonFunctions.BIRTH_DATE).setValue(birthDate);

                    Toast.makeText(getActivity(), "הכלב שונה בהצלחה!", Toast.LENGTH_SHORT).show();

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
