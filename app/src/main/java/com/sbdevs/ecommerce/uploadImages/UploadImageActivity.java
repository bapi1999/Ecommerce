package com.sbdevs.ecommerce.uploadImages;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.sbdevs.ecommerce.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UploadImageActivity extends AppCompatActivity {
    private Button uploadBtn, selectBtn;
    private Uri imageUri;

    private FirebaseUser user;
    private FirebaseStorage storage;
    private StorageReference storageReference1;

    private RecyclerView recyclerView;
    private List<UploadImageModel> imageModelList = new ArrayList<>();
    private UploadImageAdapter imageAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_image);

        user = FirebaseAuth.getInstance().getCurrentUser();
        storage = FirebaseStorage.getInstance();
        storageReference1 = storage.getReference();
//        storageReference1 = storage.getReference().child("profile/" + user.getUid() + ".jpg");


        uploadBtn = findViewById(R.id.upload_button);
        selectBtn = findViewById(R.id.select_button);

        recyclerView = findViewById(R.id.uploaded_recycler);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 2, GridLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(gridLayoutManager);

        selectBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent galleryIntent = new Intent();
                galleryIntent.setType("image/*");
                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
                galleryIntent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                startActivityForResult(Intent.createChooser(galleryIntent, "Select Picture"), 1);

            }
        });

        uploadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

//                final StorageReference storageReference2 = storageReference1.child("demo.jpg");
//                storageReference2.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
//                    @Override
//                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
//                        storageReference2.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
//                            @Override
//                            public void onSuccess(Uri uri) {
//                                Map<String, Object> updateData = new HashMap<>();
//                                updateData.put("uploadpic", uri.toString());
//                                Glide.with(UploadImageActivity.this).load(uri).into(imageView);
//                                UpdateFilds(user,updateData);
//                            }
//                        });
//                    }
//                });
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

//        imageModelList.clear();

        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {

                if (data.getClipData() != null) {
                    int totalImage = data.getClipData().getItemCount();
                    for (int i = 0; i < totalImage; i++) {
                        imageUri = data.getClipData().getItemAt(i).getUri();
                        String imagename = GetFileName(imageUri);

                        UploadImageModel imageModel = new UploadImageModel(imageUri.toString());
                        imageModelList.add(imageModel);

                        imageAdapter = new UploadImageAdapter(imageModelList);
                        recyclerView.setAdapter(imageAdapter);


                        StorageReference mRef = storageReference1.child("image").child(imagename);
                        int finalI = i;
                        mRef.putFile(imageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                                if (task.isSuccessful()) {
                                    mRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                        @Override
                                        public void onSuccess(Uri uri) {
                                            Map<String, Object> updateData = new HashMap<>();
                                            updateData.put("uploadpic"+finalI, uri.toString());
                                            updateData.put("uploadpicName"+finalI,imagename);
                                            UpdateFilds(user, updateData);
                                        }
                                    });
                                } else {

                                }

                            }
                        });


                    }
                }
//                else if (data.getData() != null) {
//
//                }


//                if (data != null) {
//                    imageUri = data.getData();
//                    if (imageUri != null){
//                        Glide.with(this).load(imageUri).into(imageView);
//                    }
//                } else {
//                    Toast.makeText(this, "Image not found", Toast.LENGTH_SHORT).show();
//                }


            }
        }
    }


    public void UpdateFilds(FirebaseUser user, final Map<String, Object> updateData) {
        FirebaseFirestore.getInstance().collection("USERS")
                .document(user.getUid()).set(updateData)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {

                            Toast.makeText(UploadImageActivity.this, "Successfully updated", Toast.LENGTH_SHORT).show();
                        } else {
                            String error = task.getException().getMessage();
                            Toast.makeText(UploadImageActivity.this, error, Toast.LENGTH_SHORT).show();
                        }
                        //loadingDialog.dismiss();
                    }
                });
    }

    public String GetFileName(Uri uri) { // for image names
        String result = null;
        if (uri.getScheme().equals("content")) {
            Cursor cursor = getContentResolver().query(uri, null, null, null, null);
            try {
                if (cursor != null && cursor.moveToFirst()) {
                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                }
            } finally {
                cursor.close();
            }
        }
        if (result == null) {
            result = uri.getPath();
            int cut = result.lastIndexOf('/');
            if (cut != -1) {
                result = result.substring(cut + 1);
            }
        }
        return result;
    }
}