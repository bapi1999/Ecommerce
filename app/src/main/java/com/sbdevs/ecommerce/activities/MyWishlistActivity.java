package com.sbdevs.ecommerce.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.ViewGroup;

import com.sbdevs.ecommerce.R;
import com.sbdevs.ecommerce.adapters.MyWishlistAdapter;
import com.sbdevs.ecommerce.models.MyWishlistModel;

import java.util.ArrayList;
import java.util.List;

public class MyWishlistActivity extends AppCompatActivity {

    private RecyclerView myWishlistEecycler;
    List<MyWishlistModel> list = new ArrayList<>();
    public static MyWishlistAdapter myWishlistAdapter;
    private Dialog loadingDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_wishlist);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        loadingDialog = new Dialog(MyWishlistActivity.this);
        loadingDialog.setContentView(R.layout.ast_loading_progress_dialog);
        loadingDialog.setCancelable(false);
        loadingDialog.getWindow().setBackgroundDrawable(getDrawable(R.drawable.s_bigslider_background));
        loadingDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        loadingDialog.show();

        myWishlistEecycler = findViewById(R.id.myWihslist_recycler);
        MyWish();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home){
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
    private void MyWish(){
        if (DBqueriesClass.wishlistModelList.size() == 0){
            DBqueriesClass.wishLishList1.clear();
            DBqueriesClass.LoadWishlist(MyWishlistActivity.this,loadingDialog,true);
        }else {
            loadingDialog.dismiss();
        }

        myWishlistAdapter = new MyWishlistAdapter(DBqueriesClass.wishlistModelList,true);
        myWishlistEecycler.setAdapter(myWishlistAdapter);
        myWishlistAdapter.notifyDataSetChanged();
    }
}