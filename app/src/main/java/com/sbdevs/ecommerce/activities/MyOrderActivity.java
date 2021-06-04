package com.sbdevs.ecommerce.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.MenuItem;

import com.sbdevs.ecommerce.R;
import com.sbdevs.ecommerce.adapters.MyOrderAdapter;
import com.sbdevs.ecommerce.models.MyOrderModel;

import java.util.ArrayList;
import java.util.List;

public class MyOrderActivity extends AppCompatActivity {
    RecyclerView myOrderRecycler;
    List<MyOrderModel> list = new ArrayList<>();
    MyOrderAdapter myOrderAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_order);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        myOrderRecycler = findViewById(R.id.myOrder_recycler);
        MyOrder();
    }
    private void MyOrder(){
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        myOrderRecycler.setLayoutManager(layoutManager);
         list.add(new MyOrderModel(R.drawable.i_oppo_a5,2,"oppo 5s","canceled"));
        list.add(new MyOrderModel(R.drawable.i_oppo_a5,0,"oppo 5s","deliverd on may 15th, jan 2323"));
        myOrderAdapter = new MyOrderAdapter(list);
        myOrderRecycler.setAdapter(myOrderAdapter);
        myOrderAdapter.notifyDataSetChanged();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if(id== android.R.id.home){
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}