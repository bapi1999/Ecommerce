package com.sbdevs.ecommerce.fragments;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.ImageViewTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.sbdevs.ecommerce.R;
import com.sbdevs.ecommerce.activities.DBqueriesClass;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;


public class UpdateInfoFragment extends Fragment {


    public UpdateInfoFragment() { // Required empty public constructor
        }

    private CircleImageView circleImageView;
    private Button changePicBtn, removeBtn, updateBtn,doneBtn;
    private EditText personName, personEmail,password;
    private Dialog loadingDialog, passwordDialog;
    private String name,email,photo;
    private Uri imageUri;
    private boolean updatePhoto = false;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_update_info, container, false);

        //loading dialog
        loadingDialog = new Dialog(getContext());
        loadingDialog.setContentView(R.layout.ast_loading_progress_dialog);
        loadingDialog.setCancelable(false);
        loadingDialog.getWindow().setBackgroundDrawable(getResources().getDrawable(R.drawable.s_bigslider_background));
        loadingDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        //loading dialog

        //password dialog
        passwordDialog = new Dialog(getContext());
        passwordDialog.setContentView(R.layout.ast_passwor_confirmation_dialog);
        passwordDialog.setCancelable(true);
        passwordDialog.getWindow().setBackgroundDrawable(getResources().getDrawable(R.drawable.s_bigslider_background));
        passwordDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        password = passwordDialog.findViewById(R.id.password);
        doneBtn = passwordDialog.findViewById(R.id.done_btn);
        //password dialog

        circleImageView = view.findViewById(R.id.profile_image);
        changePicBtn = view.findViewById(R.id.change_photo_btn);
        removeBtn = view.findViewById(R.id.remove_photo_btn);
        updateBtn = view.findViewById(R.id.update_info_btn);
        personName = view.findViewById(R.id.person_name);
        personEmail = view.findViewById(R.id.person_email);

        name = getArguments().getString("Name");
        email = getArguments().getString("Email");
        photo = getArguments().getString("Photo");

        Glide.with(getContext()).load(photo).into(circleImageView);
        personName.setText(name);
        personEmail.setText(email);

        changePicBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (getContext().checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {

                        Intent galleryIntent = new Intent(Intent.ACTION_PICK);
                        galleryIntent.setType("image/*");
                        startActivityForResult(galleryIntent, 1);

                        Toast.makeText(getContext(), "step ######", Toast.LENGTH_SHORT).show();
                    } else {
                        getActivity().requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 2);
                        Toast.makeText(getContext(), "step ------", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Intent galleryIntent = new Intent(Intent.ACTION_PICK);
                    galleryIntent.setType("image/*");
                    startActivityForResult(galleryIntent, 1);
                    Toast.makeText(getContext(), "step !!!!!!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        removeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                imageUri = null;
                updatePhoto = true;
                Glide.with(getContext()).load(R.drawable.i_user).into(circleImageView);
            }
        });

        personName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) { }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                CheckInputs();////////////////////////////////////////////////////////////////
            }
            @Override
            public void afterTextChanged(Editable editable) { }
        });


        personEmail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) { }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                CheckInputs();///////////////////////////////////////////////////////////////
            }
            @Override
            public void afterTextChanged(Editable editable) { }
        });

        updateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkEmailAndPass();
            }
        });

        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if (resultCode == getActivity().RESULT_OK) {
                if (data != null) {
                    imageUri = data.getData();
                    updatePhoto= true;
                    Glide.with(getContext()).load(imageUri).into(circleImageView);
                } else {
                    Toast.makeText(getContext(), "Image not found", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 2) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Intent galleryIntent = new Intent(Intent.ACTION_PICK);
                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent, 1);
            } else {
                Toast.makeText(getContext(), "Permission Denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void CheckInputs() {
        if (!TextUtils.isEmpty(personName.getText())) {
            if (!TextUtils.isEmpty(personEmail.getText())) {
                updateBtn.setEnabled(true);
                updateBtn.setTextColor(getResources().getColor(R.color.white));

            } else {
                updateBtn.setEnabled(false);
                updateBtn.setTextColor(getResources().getColor(R.color.disAbled));
            }
        } else {
            updateBtn.setEnabled(false);
            updateBtn.setTextColor(getResources().getColor(R.color.disAbled));
        }
    }
    private void checkEmailAndPass() {
        String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+.[a-z]+";
        if (personEmail.getText().toString().matches(emailPattern)) {

            final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

            if (personEmail.getText().toString().toLowerCase().trim().equals(email.toLowerCase().trim())){ //same email
                loadingDialog.show();
                UpdatePhotoFunction(user);
            }else { // update email
                passwordDialog.show();
                doneBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        loadingDialog.show();

                        String userPassword = password.getText().toString();

                        passwordDialog.dismiss();

                        AuthCredential credential = EmailAuthProvider.getCredential(email,userPassword);
                        user.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()){
                                    user.updateEmail(personEmail.getText().toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()){

                                                UpdatePhotoFunction(user);

                                            }else {
                                                loadingDialog.dismiss();
                                                String error = task.getException().getMessage();
                                                Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                                } else {
                                    loadingDialog.dismiss();
                                    String error = task.getException().getMessage();
                                    Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                });
            }
        } else {
            personEmail.setError("Invalid Email!");
        }
    }



    private void UpdatePhotoFunction(final FirebaseUser user){
        if (updatePhoto){
            final StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("profile/"+user.getUid()+".jpg");

            if (imageUri != null){
                Glide.with(getContext()).asBitmap().load(imageUri).circleCrop().into(new ImageViewTarget<Bitmap>(circleImageView) {

                    @Override
                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        resource.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                        byte[] data = baos.toByteArray();

                        UploadTask uploadTask = storageReference.putBytes(data);
                        uploadTask.addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                                if (task.isSuccessful()){
                                    storageReference.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Uri> task) {
                                            if (task.isSuccessful()){
                                                imageUri = task.getResult();
                                                DBqueriesClass.profilePic  =  task.getResult().toString();
                                                Glide.with(getContext()).load(DBqueriesClass.profilePic).into(circleImageView);

                                                Map <String,Object> updateData = new HashMap<>();
                                                updateData.put( "email",personEmail.getText().toString());
                                                updateData.put( "name",personName.getText().toString());
                                                updateData.put( "profilePic",DBqueriesClass.profilePic);

                                                UpdateFilds(user,updateData);

                                            }else {
                                                loadingDialog.dismiss();
                                                DBqueriesClass.profilePic = "";
                                                String error = task.getException().getMessage();
                                                Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                                }else {
                                    loadingDialog.dismiss();
                                    String error = task.getException().getMessage();
                                    Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();
                                }
                            }
                        });

                        return;
                    }

                    @Override
                    protected void setResource(@Nullable Bitmap resource) {
                        circleImageView.setImageResource(R.drawable.i_user);
                    }
                });
            }else {//remove photo
                storageReference.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){

                            DBqueriesClass.profilePic  =  "";

                            Map <String,Object> updateData = new HashMap<>();
                            updateData.put( "email",personEmail.getText().toString());
                            updateData.put( "name",personName.getText().toString());
                            updateData.put( "profilePic","");

                            UpdateFilds(user,updateData);
                        } else {
                            loadingDialog.dismiss();
                            String error = task.getException().getMessage();
                            Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        }//////
        else {
            Map <String,Object> updateData = new HashMap<>();
            updateData.put( "name",personName.getText().toString());

            UpdateFilds(user,updateData);
        }

    }



    private void UpdateFilds(FirebaseUser user, final Map<String ,Object> updateData){
        FirebaseFirestore.getInstance().collection("USER")
                .document(user.getUid()).update(updateData)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            if (updateData.size()>1){
                                DBqueriesClass.email = personEmail.getText().toString().trim();////////////////error/////////////////////////////////////////
                                DBqueriesClass.fullname = personName.getText().toString().trim();////////////////error////////////////////////////////////////////
                            }else {
                                DBqueriesClass.fullname = personName.getText().toString().trim();
                            }
                            getActivity().finish();
                            Toast.makeText(getContext(), "Successfully updated", Toast.LENGTH_SHORT).show();
                        }else {
                            String error = task.getException().getMessage();
                            Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();
                        }
                        loadingDialog.dismiss();
                    }
                });
    }

}