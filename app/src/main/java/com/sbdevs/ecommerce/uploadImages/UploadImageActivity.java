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
import com.google.firebase.firestore.DocumentSnapshot;
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
    private FirebaseFirestore firebaseFirestore;
    private FirebaseStorage storage;
    private StorageReference storageReference1;

    private RecyclerView recyclerView;
    private List<UploadImageModel> imageModelList = new ArrayList<>();
    private UploadImageAdapter imageAdapter;
    private int totalpic;
    private boolean edit=false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_image);

        user = FirebaseAuth.getInstance().getCurrentUser();
        storage = FirebaseStorage.getInstance();
        storageReference1 = storage.getReference();
        firebaseFirestore = FirebaseFirestore.getInstance();

        uploadBtn = findViewById(R.id.upload_button);
        selectBtn = findViewById(R.id.select_button);

        recyclerView = findViewById(R.id.uploaded_recycler);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 3, GridLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(gridLayoutManager);

        firebaseFirestore.collection("USERS")
                .document(user.getUid()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull  Task<DocumentSnapshot> task) {
                if (task.isSuccessful()){
                    imageModelList.clear();
                    totalpic = Integer.parseInt(task.getResult().get("totalPic").toString());
                    edit = task.getResult().getBoolean("picAvailable");
                    if (edit){
                        for (int i = 0;i< (long)task.getResult().get("totalPic");i++){
                            String imageUri = task.getResult().get("uploadpic"+i).toString();
                            UploadImageModel imageModel = new UploadImageModel(imageUri);
                            imageModelList.add(imageModel);


                        }
                        imageAdapter = new UploadImageAdapter(imageModelList);
                        recyclerView.setAdapter(imageAdapter);
                        imageAdapter.notifyDataSetChanged();
                    }

                }
            }
        });



        selectBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent galleryIntent = new Intent();
                galleryIntent.setType("image/*");
                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
//                galleryIntent.setAction(Intent.ACTION_PICK);
                galleryIntent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                startActivityForResult(Intent.createChooser(galleryIntent, "Select Picture"), 1);

            }
        });

        uploadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


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
                        int x = totalpic;
                        StorageReference mRef = storageReference1.child("image").child(imagename);

                        mRef.putFile(imageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                                if (task.isSuccessful()) {
                                    mRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                        @Override
                                        public void onSuccess(Uri uri) {
                                            Map<String, Object> updateData = new HashMap<>();
                                            updateData.put("uploadpic"+(x), uri.toString());
                                            updateData.put("uploadpicName"+(x),imagename);
                                            updateData.put("picAvailable",(boolean)true);
                                            UpdateFilds(user,updateData);
                                        }
                                    });

//
                                } else {

                                }

                            }
                        });
                        totalpic = totalpic+1;
                    }
                    Map<String, Object> dataup = new HashMap<>();
                    dataup.put("totalPic",totalpic);
                    FirebaseFirestore.getInstance().collection("USERS")
                            .document(user.getUid()).update(dataup).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                        }
                    });
                }
                else if (data.getData() != null) {
                    imageUri = data.getData();
                    String imagename = GetFileName(imageUri);
                    UploadImageModel imageModel = new UploadImageModel(imageUri.toString());
                    imageModelList.add(imageModel);

                    imageAdapter = new UploadImageAdapter(imageModelList);
                    recyclerView.setAdapter(imageAdapter);

                    int y = totalpic;

                    StorageReference mRef = storageReference1.child("image").child(imagename);
                    mRef.putFile(imageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                            if (task.isSuccessful()) {
                                mRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        Map<String, Object> updateData = new HashMap<>();
                                        updateData.put("uploadpic"+(y), uri.toString());
                                        updateData.put("uploadpicName"+(y),imagename);
                                        updateData.put("picAvailable",(boolean)true);
                                        UpdateFilds(user,updateData);
                                    }
                                });

                            } else {

                            }

                        }
                    });
                    totalpic = totalpic+1;
                    Map<String, Object> dataup = new HashMap<>();
                    dataup.put("totalPic",totalpic);
                    FirebaseFirestore.getInstance().collection("USERS")
                            .document(user.getUid()).update(dataup).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                        }
                    });
                }

            }
        }
    }


    public void UpdateFilds(FirebaseUser user, final Map<String, Object> updateData) {
        FirebaseFirestore.getInstance().collection("USERS")
                .document(user.getUid()).update(updateData)
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