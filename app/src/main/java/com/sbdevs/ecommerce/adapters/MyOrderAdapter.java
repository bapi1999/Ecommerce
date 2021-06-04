package com.sbdevs.ecommerce.adapters;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.sbdevs.ecommerce.R;
import com.sbdevs.ecommerce.activities.OrderDetailsActivity;
import com.sbdevs.ecommerce.models.MyOrderModel;

import java.util.List;

public class MyOrderAdapter extends RecyclerView.Adapter<MyOrderAdapter.ViewHolder> {
    private List<MyOrderModel> list;

    public MyOrderAdapter(List<MyOrderModel> list) {
        this.list = list;
    }

    @NonNull
    @Override
    public MyOrderAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.ast_my_order_item_lay,parent,false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyOrderAdapter.ViewHolder holder, int position) {
        int resource = list.get(position).getProductImg();
        String title = list.get(position).getProductName();
        String date = list.get(position).getDeliveryStatus();
        int starPosition = list.get(position).getRating();
        holder.setMyOrder(resource,title,  date,starPosition);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView productImg;
        ImageView deliveryIndecator;
        TextView productName;
        TextView deliVeryStatus;
        LinearLayout rateNowContainer;

        public ViewHolder(@NonNull final View itemView) {
            super(itemView);
            productImg = itemView.findViewById(R.id.myorder_product_Img);
            productName = itemView.findViewById(R.id.myorder_product_Name);
            deliveryIndecator = itemView.findViewById(R.id.order_Delivery_indicator);
            deliVeryStatus = itemView.findViewById(R.id.order_Delivery_status);
            rateNowContainer = itemView.findViewById(R.id.rate_now_container);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent i = new Intent(itemView.getContext(), OrderDetailsActivity.class);
                    itemView.getContext().startActivity(i);
                }
            });
        }
        private void setMyOrder(int resource, String title, String date,int starPosition){
            productImg.setImageResource(resource);
            productName.setText(title);
            if(date.equals("canceled")){

                deliveryIndecator.setImageTintList(ColorStateList.valueOf(itemView.getContext().getResources().getColor(R.color.brikeRed)));
            }else {
                deliveryIndecator.setImageTintList(ColorStateList.valueOf(itemView.getContext().getResources().getColor(R.color.successGreen)));
            }
            deliVeryStatus.setText(date);
            setRating(starPosition);
            RateNow();
        }

        private void RateNow(){
            for(int x = 0; x < rateNowContainer.getChildCount();x++){
                final int starPosition = x;
                rateNowContainer.getChildAt(x).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //Toast.makeText(ProductDetailActivity.this,"starcount",Toast.LENGTH_SHORT).show();
                        for(int y = 0; y < rateNowContainer.getChildCount();y++){
                            ImageView starBtn = (ImageView)rateNowContainer.getChildAt(y);
                            starBtn.setImageTintList(ColorStateList.valueOf(itemView.getContext().getResources().getColor(R.color.gray)));
                            if(y<=starPosition){
                                starBtn.setImageTintList(ColorStateList.valueOf(itemView.getContext().getResources().getColor(R.color.topbar)));
                            }
                        }
                    }
                });
            }
        }

        private void setRating(int starPosition){
            for(int y = 0; y < rateNowContainer.getChildCount();y++){
                ImageView starBtn = (ImageView)rateNowContainer.getChildAt(y);
                starBtn.setImageTintList(ColorStateList.valueOf(itemView.getContext().getResources().getColor(R.color.gray)));
                if(y<=starPosition){
                    starBtn.setImageTintList(ColorStateList.valueOf(itemView.getContext().getResources().getColor(R.color.topbar)));
                }
            }
        }
    }
}
