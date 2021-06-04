package com.sbdevs.ecommerce.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sbdevs.ecommerce.R;
import com.sbdevs.ecommerce.adapters.CartItemAdapter;
import com.sbdevs.ecommerce.models.deliveryItemModel;

import java.util.ArrayList;

public class MyCartActivity extends AppCompatActivity {

    private RecyclerView mycartRecycler;
    public static CartItemAdapter cartItemAdapter;
    private Button cartContinueBtn;
    private LinearLayout cartbottomlayout;

    private Dialog loadingDialog;
    private TextView totalAmount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_cart);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        cartbottomlayout = findViewById(R.id.cart_bottom_layout);

        loadingDialog = new Dialog(MyCartActivity.this);
        loadingDialog.setContentView(R.layout.ast_loading_progress_dialog);
        loadingDialog.setCancelable(false);
        loadingDialog.getWindow().setBackgroundDrawable(getDrawable(R.drawable.s_bigslider_background));
        loadingDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        loadingDialog.show();

        mycartRecycler = findViewById(R.id.myCart_recycler);
        cartContinueBtn = findViewById(R.id.cartContinueBtn);
        totalAmount = findViewById(R.id.cartTotalPriceUnit);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mycartRecycler.setLayoutManager(layoutManager);



        cartItemAdapter = new CartItemAdapter(DBqueriesClass.cartItemModelsList,totalAmount,true);
        mycartRecycler.setAdapter(cartItemAdapter);
        cartItemAdapter.notifyDataSetChanged();

        cartContinueBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DeliveryActivity.cartItemModelsList = new ArrayList<>();
                DeliveryActivity.fromcart = true;

                for (int x= 0; x<DBqueriesClass.cartItemModelsList.size(); x++){
                    deliveryItemModel cartItemModel = DBqueriesClass.cartItemModelsList.get(x);
                    if (cartItemModel.isInStock()){
                        DeliveryActivity.cartItemModelsList.add(cartItemModel);
                    }
                }
                DeliveryActivity.cartItemModelsList.add(new deliveryItemModel(deliveryItemModel.TOTAL_AMOUNT_LAY));

                loadingDialog.show();
                if (DBqueriesClass.addressModelList.size() == 0) {
                    DBqueriesClass.loadAddresses(MyCartActivity.this, loadingDialog);
                }else {

                    loadingDialog.dismiss();
                    Intent i = new Intent(MyCartActivity.this, DeliveryActivity.class);
                    startActivity(i);
                }
            }
        });




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

    @Override
    protected void onStart() {
        super.onStart();
        cartItemAdapter.notifyDataSetChanged();
        if (DBqueriesClass.cartItemModelsList.size() == 0){
            DBqueriesClass.cartList.clear();
            DBqueriesClass.loadCartlist(MyCartActivity.this,loadingDialog,true,new TextView(MyCartActivity.this),totalAmount,cartbottomlayout);
        }else {
            if (DBqueriesClass.cartItemModelsList.get(DBqueriesClass.cartItemModelsList.size()-1).getType() == deliveryItemModel.TOTAL_AMOUNT_LAY){
                LinearLayout parent = (LinearLayout) totalAmount.getParent().getParent();
                parent.setVisibility(View.VISIBLE);
            }
            loadingDialog.dismiss();
        }

    }
}