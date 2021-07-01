package com.sbdevs.ecommerce.uploadImages;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.view.View;
import android.view.ViewGroup;
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
import com.sbdevs.ecommerce.activities.ProductDetailActivity;

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
    public static UploadImageAdapter imageAdapter;

    private int totalpic;
    private boolean edit=false;
    public List<Uri> uriList = new ArrayList<Uri>();
    public List<String> nameList = new ArrayList<String>();
    private Dialog loadingDialog;
    public static final List<String> Already_added_name_List = new ArrayList<String>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_image);

        user = FirebaseAuth.getInstance().getCurrentUser();
        storage = FirebaseStorage.getInstance();
        storageReference1 = storage.getReference();
        firebaseFirestore = FirebaseFirestore.getInstance();


        //loading dialog
        loadingDialog = new Dialog(this);
        loadingDialog.setContentView(R.layout.ast_loading_progress_dialog);
        loadingDialog.setCancelable(false);
        loadingDialog.getWindow().setBackgroundDrawable(getDrawable(R.drawable.s_bigslider_background));
        loadingDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        //loading dialog


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
                            String imagename = task.getResult().get("uploadpic"+i).toString();
                            UploadImageModel imageModel = new UploadImageModel(imageUri,imagename);
                            imageModelList.add(imageModel);
                            Already_added_name_List.add(imagename);


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
                galleryIntent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                startActivityForResult(Intent.createChooser(galleryIntent, "Select Picture"), 1);

            }
        });

        uploadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UpdateImages();


            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {

                if (data.getClipData() != null) {
                    int totalImage = data.getClipData().getItemCount();

                    for (int i = 0; i < totalImage; i++) {
                        imageUri = data.getClipData().getItemAt(i).getUri();
                        String imagename = GetFileName(imageUri);

                        uriList.add(imageUri);
                        nameList.add(imagename);

                        UploadImageModel imageModel = new UploadImageModel(imageUri.toString(),imagename);
                        imageModelList.add(imageModel);
                        imageAdapter = new UploadImageAdapter(imageModelList);
                        recyclerView.setAdapter(imageAdapter);

                    }

                }
                else if (data.getData() != null) {
                    imageUri = data.getData();
                    String imagename = GetFileName(imageUri);

                    if (uriList.size() == 0){
                        uriList.add(imageUri);
                        nameList.add(imagename);
                    }else {
                        uriList.clear();
                        nameList.clear();
                        uriList.add(imageUri);
                        nameList.add(imagename);
                    }
                    UploadImageModel imageModel = new UploadImageModel(imageUri.toString(),imagename);
                    imageModelList.add(imageModel);
                    imageAdapter = new UploadImageAdapter(imageModelList);
                    recyclerView.setAdapter(imageAdapter);

                }

            }
        }
    }

    public void UpdateImages( ){
        for (int i= 0; i<uriList.size();i++) {
            loadingDialog.show();
            int x = totalpic;
            StorageReference mRef = storageReference1.child("image").child(nameList.get(i));
            int finalI = i;
            mRef.putFile(uriList.get(i)).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                    if (task.isSuccessful()) {
                        mRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                Map<String, Object> updateData = new HashMap<>();
                                updateData.put("uploadpic" + (x), uri.toString());
                                updateData.put("uploadpicName" + (x), nameList.get(finalI));
                                updateData.put("picAvailable", (boolean) true);
//                                UpdateFilds(user, updateData);
                                firebaseFirestore.collection("USERS")
                                        .document(user.getUid()).update(updateData)
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    Toast.makeText(UploadImageActivity.this, "Successfully updated "+(finalI+1), Toast.LENGTH_SHORT).show();
                                                    Already_added_name_List.add(nameList.get(finalI));
                                                    loadingDialog.dismiss();
                                                    if (finalI==uriList.size()-1){
                                                        uriList.clear();
                                                        nameList.clear();
                                                    }
                                                } else {
                                                    String error = task.getException().getMessage();
                                                    Toast.makeText(UploadImageActivity.this, error, Toast.LENGTH_SHORT).show();
                                                }

                                            }
                                        });
                            }
                        });
                    } else {
                        //error toast
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

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Already_added_name_List.clear();
    }

    private void RemoveMethode(int index){
        final Uri removedPhotoUri = uriList.get(index);
        final String removedPhotoName = nameList.get(index);
        uriList.remove(index);
        nameList.remove(index);
        int x = totalpic;
        Map<String,Object> updateMap = new HashMap<>();
        for (int i=0;i<uriList.size();i++){
            updateMap.put("uploadpic" + (i), uriList.get(i));
            updateMap.put("uploadpicName" + (i), nameList.get(i));
            x--;
        }
        updateMap.put("totalPic",x);
        FirebaseFirestore.getInstance().collection("USERS")
                .document(user.getUid()).set(updateMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    Already_added_name_List.remove(index);
                    imageAdapter.notifyDataSetChanged();
                }else {
                    Toast.makeText(UploadImageActivity.this, "error", Toast.LENGTH_SHORT).show();
                }
            }
        });



    }
}