package com.sbdevs.ecommerce.adapters;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.viewpager.widget.PagerAdapter;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.sbdevs.ecommerce.R;
import com.sbdevs.ecommerce.models.BigSliderModel;

import java.util.List;

public class BigSliderAdapter extends PagerAdapter {
    private List<BigSliderModel> list;

    public BigSliderAdapter(List<BigSliderModel> list) {
        this.list = list;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        View view = LayoutInflater.from(container.getContext()).inflate(R.layout.ast_bigslider_item,container,false);
        ConstraintLayout continerBigSlider = view.findViewById(R.id.container_BigSlider);
        continerBigSlider.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor(list.get(position).getBackground())));
        ImageView bigSlider = view.findViewById(R.id.bigSlider_image);
        Glide.with(container.getContext()).load(list.get(position).getBanner()).apply(new RequestOptions().placeholder(R.drawable.s_bigslider_background)).into(bigSlider);
        container.addView(view);
        return  view;
        //return super.instantiateItem(container, position);
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        //super.destroyItem(container, position, object);
        container.removeView((View) object);
    }
}
