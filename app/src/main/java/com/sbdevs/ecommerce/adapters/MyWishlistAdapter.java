package com.sbdevs.ecommerce.adapters;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.sbdevs.ecommerce.R;
import com.sbdevs.ecommerce.activities.DBqueriesClass;
import com.sbdevs.ecommerce.activities.ProductDetailActivity;
import com.sbdevs.ecommerce.models.MyWishlistModel;

import java.util.List;

public class MyWishlistAdapter extends RecyclerView.Adapter<MyWishlistAdapter.ViewHolder> {
    List<MyWishlistModel> list;
    private Boolean wishlist;
    private int lastPosition = -1;
    private boolean fromSearch;

    public boolean isFromSearch() {
        return fromSearch;
    }

    public void setFromSearch(boolean fromSearch) {
        this.fromSearch = fromSearch;
    }

    public MyWishlistAdapter(List<MyWishlistModel> list, Boolean wishlist) {
        this.list = list;
        this.wishlist=wishlist;
    }

    public List<MyWishlistModel> getList() {
        return list;
    }

    public void setList(List<MyWishlistModel> list) {
        this.list = list;
    }

    @NonNull
    @Override
    public MyWishlistAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.ast_my_wishlist_item_lay,parent,false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyWishlistAdapter.ViewHolder holder, int position) {
        String productID = list.get(position).getProductID();
        String img = list.get(position).getProductImg();
        String title = list.get(position).getProductTitle();
        String avgRating = list.get(position).getAvgRating();
        long totalRating = list.get(position).getTotalRating();
        String priceB = list.get(position).getProductPriceBig();
        String priceS = list.get(position).getProductPriceSml();
        boolean inStock = list.get(position).isInStock();
        holder.setProduct(productID,img, title,avgRating,totalRating,priceB, priceS,inStock,position);

        if (lastPosition < position){
            Animation animation = AnimationUtils.loadAnimation(holder.itemView.getContext(),R.anim.slidein_left);
            holder.itemView.setAnimation(animation);
            lastPosition = position;
        }

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView productImg;
        TextView productTitle;
        TextView rating;
        TextView totalRating;
        TextView priceBig;
        TextView priceSml;
        ImageButton remove;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            productImg=itemView.findViewById(R.id.myWihslist_productImg);
            productTitle=itemView.findViewById(R.id.myWihslist_productTitle);
            rating=itemView.findViewById(R.id.myWihslist_productRating);
            totalRating =itemView.findViewById(R.id.myWihslist_productTotalrate);
            priceBig=itemView.findViewById(R.id.myWihslist_productPriceBig);
            priceSml=itemView.findViewById(R.id.myWihslist_productPriceSml);
            remove = itemView.findViewById(R.id.removeBtn);
        }
        private void setProduct(String productID,String img, String title, String avgRating, long totalRating1, String priceB, String priceS , boolean inStock , final int index){

            Glide.with(itemView.getContext()).load(img).apply(new RequestOptions().placeholder(R.drawable.s_bigslider_background)).into(productImg);
            productTitle.setText(title);
            if (inStock){
                rating.setVisibility(View.VISIBLE);
                totalRating.setVisibility(View.VISIBLE);

                priceBig.setTextColor(itemView.getContext().getResources().getColor(R.color.black));
                priceSml.setVisibility(View.VISIBLE);

                rating.setText(avgRating);
                totalRating.setText("("+totalRating1+") ratings");
                priceBig.setText(priceB+" Rs/-");
                priceSml.setText(priceS);
            }else {
                rating.setVisibility(View.INVISIBLE);
                totalRating.setVisibility(View.INVISIBLE);
                priceBig.setText("Out of stock");
                priceBig.setTextColor(itemView.getContext().getResources().getColor(R.color.red));
                priceSml.setVisibility(View.INVISIBLE);
            }


            if(wishlist){
                remove.setVisibility(View.VISIBLE);
            }else {
                remove.setVisibility(View.GONE);
            }
            remove.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (! ProductDetailActivity.running_Wishlist_query ) {
                        ProductDetailActivity.running_Wishlist_query = true;
                        DBqueriesClass.removeFromWishlist(index, itemView.getContext());
                    }
                }
            });
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    if (fromSearch){
                        ProductDetailActivity.fromSearch = true;
                    }

                    Intent productDetailsIntent = new Intent(itemView.getContext(), ProductDetailActivity.class);
                    productDetailsIntent.putExtra("product_ID",productID);
                    itemView.getContext().startActivity(productDetailsIntent);
                }
            });

        }
    }
}
