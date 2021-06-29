package com.sbdevs.ecommerce.uploadImages;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.firestore.FirebaseFirestore;
import com.sbdevs.ecommerce.R;
import com.sbdevs.ecommerce.adapters.CartItemAdapter;
import com.sbdevs.ecommerce.adapters.CategoryAdapter;
import com.sbdevs.ecommerce.models.BigSliderModel;
import com.sbdevs.ecommerce.models.MyWishlistModel;
import com.sbdevs.ecommerce.models.ProductHorizonModel;
import com.sbdevs.ecommerce.models.deliveryItemModel;
import com.sbdevs.ecommerce.ui.home.HomeAdapter;
import com.sbdevs.ecommerce.ui.home.HomeModel;

import java.util.List;

public class UploadImageAdapter extends RecyclerView.Adapter<UploadImageAdapter.ViewHolder> {

    private List<UploadImageModel> list;
    private  FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();

    public UploadImageAdapter(List<UploadImageModel> list) {
        this.list = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull  ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.le_uploaded_image_item,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull  ViewHolder holder, int position) {
        String images = list.get(position).getImages();
        holder.setImages(images);

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView imageView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.uploaded_image);
        }
        private void setImages(String images){
            Glide.with(imageView.getContext()).load(images).into(imageView);
            //Glide.with(imageView.getContext()).load(uri).into(imageView);
        }
    }
}
