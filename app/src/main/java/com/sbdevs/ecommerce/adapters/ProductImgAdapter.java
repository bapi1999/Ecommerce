package com.sbdevs.ecommerce.adapters;

import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.sbdevs.ecommerce.R;

import java.util.List;

public class ProductImgAdapter extends PagerAdapter {

    private List<String> productImgList;

    public ProductImgAdapter(List<String> productImgList) {
        this.productImgList = productImgList;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        ImageView productImage = new ImageView(container.getContext());
        Glide.with(container.getContext()).load(productImgList.get(position)).apply(new RequestOptions().placeholder(R.drawable.i_plus)).into(productImage);
        //productImage.setImageResource(productImgList.get(position));
        container.addView(productImage,0);
        return productImage;
    }

    @Override
    public int getCount() {
        return productImgList.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {

        return view == object;
    }
    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        //super.destroyItem(container, position, object);
        container.removeView((ImageView)object);
    }
}
