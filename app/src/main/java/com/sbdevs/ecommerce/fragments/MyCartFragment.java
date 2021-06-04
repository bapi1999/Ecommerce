package com.sbdevs.ecommerce.fragments;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.sbdevs.ecommerce.R;
import com.sbdevs.ecommerce.activities.AddAddressActivity;
import com.sbdevs.ecommerce.activities.DBqueriesClass;
import com.sbdevs.ecommerce.activities.MyCartActivity;
import com.sbdevs.ecommerce.adapters.CartItemAdapter;


public class MyCartFragment extends Fragment {

    private RecyclerView mycartRecycler;
    public static CartItemAdapter cartItemAdapter;
    Button cartContinueBtn;
    private Dialog loadingDialog;
    private TextView totalAmount;

    public MyCartFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_my_cart, container, false);

        loadingDialog = new Dialog(getContext());
        loadingDialog.setContentView(R.layout.ast_loading_progress_dialog);
        loadingDialog.setCancelable(false);
        loadingDialog.getWindow().setBackgroundDrawable(getContext().getDrawable(R.drawable.s_bigslider_background));
        loadingDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        loadingDialog.show();

        mycartRecycler = view.findViewById(R.id.myCart_recycler);
        cartContinueBtn = view.findViewById(R.id.cartContinueBtn);
        totalAmount = view.findViewById(R.id.cartTotalPriceUnit);



        cartContinueBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadingDialog.show();
                DBqueriesClass.loadAddresses(getContext(),loadingDialog);
            }
        });

        return view;
    }
    private void MyCart(){
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mycartRecycler.setLayoutManager(layoutManager);

        if (DBqueriesClass.cartItemModelsList.size() == 0){
            DBqueriesClass.cartList.clear();
            //DBqueriesClass.loadCartlist(getContext(),loadingDialog,true,new TextView(getContext()));
        }else {
            loadingDialog.dismiss();
        }

        cartItemAdapter = new CartItemAdapter(DBqueriesClass.cartItemModelsList,totalAmount,true);
        mycartRecycler.setAdapter(cartItemAdapter);
        cartItemAdapter.notifyDataSetChanged();

    }
}