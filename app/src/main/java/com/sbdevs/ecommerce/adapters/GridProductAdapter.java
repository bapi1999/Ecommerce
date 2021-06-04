package com.sbdevs.ecommerce.adapters;

import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.sbdevs.ecommerce.R;
import com.sbdevs.ecommerce.activities.ProductDetailActivity;
import com.sbdevs.ecommerce.models.ProductHorizonModel;

import java.util.List;

public class GridProductAdapter extends BaseAdapter {
    private List<ProductHorizonModel> list;

    public GridProductAdapter(List<ProductHorizonModel> list) {
        this.list = list;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, final ViewGroup viewGroup) {
        final View view;
        if(convertView == null){
            view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.ast_product_horizontal_item,null);
            view.setElevation(0);
            view.setBackgroundColor(Color.parseColor("#FFFFFF"));
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(viewGroup.getContext(), ProductDetailActivity.class);
                    intent.putExtra("product_ID",list.get(position).getPrOID());
                    viewGroup.getContext().startActivity(intent);
                }
            });

            ImageView productImg = view.findViewById(R.id.product_H_item_img);
            TextView productTitle= view.findViewById(R.id.product_H_item_name);
            TextView productCoin= view.findViewById(R.id.product_H_item_coin);
            TextView productPrice= view.findViewById(R.id.product_H_item_price);

            Glide.with(viewGroup.getContext()).load(list.get(position).getProductImg()).apply(new RequestOptions().placeholder(R.drawable.s_bigslider_background)).into(productImg);

            productTitle.setText(list.get(position).getTitle());
            productCoin.setText(list.get(position).getCoinUnit());
            productPrice.setText("Rs."+list.get(position).getPriceUnit()+"/-");

        }else {
            view=convertView;
        }
        return view;
    }
}
