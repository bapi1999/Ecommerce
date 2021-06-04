package com.sbdevs.ecommerce.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.GridView;

import com.sbdevs.ecommerce.R;
import com.sbdevs.ecommerce.adapters.GridProductAdapter;
import com.sbdevs.ecommerce.adapters.MyWishlistAdapter;
import com.sbdevs.ecommerce.models.MyWishlistModel;
import com.sbdevs.ecommerce.models.ProductHorizonModel;

import java.util.ArrayList;
import java.util.List;

public class ViewAllActivity extends AppCompatActivity {
    private RecyclerView viewAllRecycler;
    private GridView viewAllGridView;

    private MyWishlistAdapter horizonProductAdapter;
    public static List<MyWishlistModel> horizonProductList = new ArrayList<MyWishlistModel>();

    private GridProductAdapter gridProductAdapter;
    public static List<ProductHorizonModel> productG_List = new ArrayList<ProductHorizonModel>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_all);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setTitle(getIntent().getStringExtra("title"));//t52
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        viewAllRecycler = findViewById(R.id.viewAll_recycler);
        viewAllGridView = findViewById(R.id.viewAll_grid);

        int laout_ID =getIntent().getIntExtra("layout_ID",-2);

        if (laout_ID==0){
            RcycleShoew();
        }else if(laout_ID == 1){
            GridShow();
        }

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home){
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void RcycleShoew(){
        viewAllRecycler.setVisibility(View.VISIBLE);

        horizonProductAdapter = new MyWishlistAdapter(horizonProductList,false);
        viewAllRecycler.setAdapter(horizonProductAdapter);
        horizonProductAdapter.notifyDataSetChanged();
    }
    public void GridShow(){
        viewAllGridView.setVisibility(View.VISIBLE);


        gridProductAdapter = new GridProductAdapter(productG_List);
        viewAllGridView.setAdapter(gridProductAdapter);
    }
}