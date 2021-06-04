package com.sbdevs.ecommerce.adapters;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.sbdevs.ecommerce.R;
import com.sbdevs.ecommerce.activities.ProductDetailActivity;
import com.sbdevs.ecommerce.models.ProductHorizonModel;

import java.util.List;

public class ProductHorizonAdapter extends RecyclerView.Adapter<ProductHorizonAdapter.ViewHolder> {

    private List<ProductHorizonModel> list;

    public ProductHorizonAdapter(List<ProductHorizonModel> list) {
        this.list = list;
    }

    @NonNull
    @Override
    public ProductHorizonAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.ast_product_horizontal_item,viewGroup,false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductHorizonAdapter.ViewHolder holder, int position) {

        String product_id=list.get(position).getPrOID();
        String resource = list.get(position).getProductImg();
        String title = list.get(position).getTitle();
        String coin = list.get(position).getCoinUnit();
        String price = list.get(position).getPriceUnit();

        holder.setProductData(product_id,resource,title,coin,price);


    }

    @Override
    public int getItemCount() {
        if (list.size() > 8){
            return 8;
        }else {
            return list.size();
        }

    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView productImg;
        TextView productTitle;
        TextView coinUnit;
        TextView priceUnit;
        public ViewHolder(@NonNull final View itemView) {
            super(itemView);
            productImg = itemView.findViewById(R.id.product_H_item_img);
            productTitle = itemView.findViewById(R.id.product_H_item_name);
            coinUnit = itemView.findViewById(R.id.product_H_item_coin);
            priceUnit = itemView.findViewById(R.id.product_H_item_price);


        }
        private void setProductData(final String product_id, String resource, String title, String coin, String price){
            Glide.with(itemView.getContext()).load(resource).apply(new RequestOptions().placeholder(R.drawable.i_oppo_a5)).into(productImg);
            productTitle.setText(title);
            coinUnit.setText(coin);
            priceUnit.setText("Rs."+price+"/-");

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(itemView.getContext(), ProductDetailActivity.class);
                    intent.putExtra("product_ID",product_id);
                    itemView.getContext().startActivity(intent);
                }
            });
        }

    }
}
