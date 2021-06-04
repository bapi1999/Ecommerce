package com.sbdevs.ecommerce.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;


import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.sbdevs.ecommerce.R;
import com.sbdevs.ecommerce.models.BigSliderModel;
import com.sbdevs.ecommerce.models.CategoryModel;
import com.sbdevs.ecommerce.models.MyWishlistModel;
import com.sbdevs.ecommerce.models.ProductHorizonModel;
import com.sbdevs.ecommerce.ui.home.HomeAdapter;
import com.sbdevs.ecommerce.ui.home.HomeModel;

import java.util.ArrayList;
import java.util.List;


public class  CategoryActivity extends AppCompatActivity {

    HomeAdapter adapter;
    String nameCategory;

    private   FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    private List<HomeModel> list = new ArrayList<>();
    private List<MyWishlistModel> proH_ViewAll=new ArrayList<>();

    private RecyclerView categoryRecycler;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        nameCategory = getIntent().getStringExtra("categoryName");
        getSupportActionBar().setTitle(nameCategory);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        categoryRecycler = findViewById(R.id.category_recycler);

        LoadCategory();

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.search_menue, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if(id== R.id.main_search){
            Intent searchIntent = new Intent(this,SearchActivity.class);
            startActivity(searchIntent);
            return true;
        }else if(id == android.R.id.home){
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    //TODO=====================================================================================================================
    private void LoadCategory(){
        list.clear();

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        categoryRecycler.setLayoutManager(layoutManager);



        adapter = new HomeAdapter(list);
        categoryRecycler.setAdapter(adapter);
        adapter.notifyDataSetChanged();


        // todo - Tutorial No-51, minute-24.
        firebaseFirestore.collection("Categories").document(nameCategory).collection("Mobile").orderBy("index").get().
                addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()){
                            for (QueryDocumentSnapshot documentSnapshot:task.getResult()){
                                if(((long)documentSnapshot.get("view_type"))==0){
                                    list.add(new HomeModel(0,documentSnapshot.get("image").toString(),documentSnapshot.get("bg_color").toString()));
                                }
                                else if((long)documentSnapshot.get("view_type")==1){

                                    List<BigSliderModel> bigSliderList = new ArrayList<BigSliderModel>();
                                    long no_of_sl = (long)documentSnapshot.get("no_of_sl");
                                    for (int x=1;x<no_of_sl+1;x++){
                                        bigSliderList.add(new BigSliderModel(documentSnapshot.get("big_sl_"+x).toString(),documentSnapshot.get("big_sl_"+x+"_bg").toString()));
                                    }
                                    list.add(new HomeModel(1,bigSliderList));
                                }
                                else if((long)documentSnapshot.get("view_type")==2){
                                    List<ProductHorizonModel> productH_List = new ArrayList<ProductHorizonModel>();
                                    long no_of_product = (long)documentSnapshot.get("no_of_product");
                                    for (int x=1;x<no_of_product+1;x++){
                                        productH_List.add(new ProductHorizonModel(documentSnapshot.get(x+"_product_id").toString(),
                                                documentSnapshot.get(x+"_product_img").toString(),
                                                documentSnapshot.get(x+"_product_title").toString(),
                                                documentSnapshot.get(x+"_product_price").toString(),
                                                documentSnapshot.get(x+"_product_price").toString()));
                                    }

                                    list.add(new HomeModel(2,documentSnapshot.get("layout_title").toString(),productH_List,documentSnapshot.get("bg_color").toString(),proH_ViewAll));
                                }
                                else if((long)documentSnapshot.get("view_type")==3){
                                    List<ProductHorizonModel> productG_List = new ArrayList<ProductHorizonModel>();
                                    long no_of_product = (long)documentSnapshot.get("no_of_product");
                                    for (int x=1;x<no_of_product+1;x++){
                                        productG_List.add(new ProductHorizonModel(documentSnapshot.get(x+"_product_id").toString(),
                                                documentSnapshot.get(x+"_product_img").toString(),
                                                documentSnapshot.get(x+"_product_title").toString(),
                                                documentSnapshot.get(x+"_product_price").toString(),
                                                documentSnapshot.get(x+"_product_price").toString()));
                                    }
                                    list.add(new HomeModel(3,documentSnapshot.get("layout_title").toString(),productG_List,documentSnapshot.get("bg_color").toString()));

                                }



                            }
                            adapter.notifyDataSetChanged();
                        }else {
                            String error = task.getException().getMessage();
                            Toast.makeText(CategoryActivity.this, error, Toast.LENGTH_LONG).show();
                        }
                    }
                });


    }
}