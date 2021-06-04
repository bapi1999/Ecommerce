package com.sbdevs.ecommerce.ui.home;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.gridlayout.widget.GridLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.sbdevs.ecommerce.R;
import com.sbdevs.ecommerce.activities.ProductDetailActivity;
import com.sbdevs.ecommerce.activities.ViewAllActivity;
import com.sbdevs.ecommerce.adapters.BigSliderAdapter;
import com.sbdevs.ecommerce.adapters.GridProductAdapter;
import com.sbdevs.ecommerce.adapters.ProductHorizonAdapter;
import com.sbdevs.ecommerce.models.BigSliderModel;
import com.sbdevs.ecommerce.models.MyWishlistModel;
import com.sbdevs.ecommerce.models.ProductHorizonModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;



public class HomeAdapter extends RecyclerView.Adapter {

    private List<HomeModel> list;
    private RecyclerView.RecycledViewPool recycledViewPool;
    private int lastPosition = -1;
    private FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();

    public HomeAdapter(List<HomeModel> list) {
        this.list = list;
        recycledViewPool = new RecyclerView.RecycledViewPool();
    }

    //TODO-=====================================================================================================================
    @Override
    public int getItemViewType(int position) {
        switch (list.get(position).getType()) {
            case 0:
                return HomeModel.BANNER_ADS;
            case 1:
                return HomeModel.BIG_SLIDER;
            case 2:
                return HomeModel.PRODUCT_HORIZONTAL;
            case 3:
                return HomeModel.PRODUCT_GRID;
            default:
                return -1;

        }
    }

    //TODO-=====================================================================================================================
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        switch (viewType) {
            case HomeModel.BANNER_ADS:
                View bannerView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.ast_banner_ad_item, viewGroup, false);
                return new BannerADSViewHolder(bannerView);
            case HomeModel.BIG_SLIDER:
                View bigSliderView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.l_bigslider_pager_layout, viewGroup, false);
                return new BigSliderViewHolder(bigSliderView);
            case HomeModel.PRODUCT_HORIZONTAL:
                View horizonView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.ast_product_horizontal_layout, viewGroup, false);
                return new HorizontalViewHolder(horizonView);
            case HomeModel.PRODUCT_GRID:
                View gridView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.ast_product_grid_layout, viewGroup, false);
                return new GridViewHolder(gridView);
            default:
                return null;
        }

    }

    //TODO-=====================================================================================================================
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        switch (list.get(position).getType()) {
            case HomeModel.BANNER_ADS:
                String resource = list.get(position).getBannerImg();
                String color = list.get(position).getBannerColor();
                ((BannerADSViewHolder) holder).setBannerAds(resource, color);
                break;
            case HomeModel.BIG_SLIDER:
                List<BigSliderModel> bigSliderList = list.get(position).getBigSliderList();
                ((BigSliderViewHolder) holder).setViewPagerBigSlidr(bigSliderList);
                break;
            case HomeModel.PRODUCT_HORIZONTAL:
                List<ProductHorizonModel> productH_List = list.get(position).getProductHorizonList();
                String header = list.get(position).getHeader();
                String bg_color1 = list.get(position).getBg_color();
                List<MyWishlistModel> proH_viewAllList = list.get(position).getProH_viewAllList();

                ((HorizontalViewHolder)holder).setProductH(productH_List,header,bg_color1,proH_viewAllList);
                break;
            case HomeModel.PRODUCT_GRID:
                List<ProductHorizonModel> productG_List = list.get(position).getProductHorizonList();
                String headerG = list.get(position).getHeader();
                String bg_color2 = list.get(position).getBg_color();
                ((GridViewHolder)holder).setGridProductView(productG_List,headerG,bg_color2);//TODO=error producing
                break;
            default:
                return;
        }

//        if (lastPosition < position){
//            Animation animation = AnimationUtils.loadAnimation(holder.itemView.getContext(),);
//            holder.itemView.setAnimation(animation);
//            lastPosition = position;
//        }


    }

    //TODO-=====================================================================================================================
    @Override
    public int getItemCount() {
        return list.size();
    }

    //TODO-=====================================================================================================================
    public class BigSliderViewHolder extends RecyclerView.ViewHolder {

        private ViewPager viewPagerBigSlidr;
        private int currentPage ;
        private Timer timer;
        long dely = 3000;
        long post = 3000;
        private List <BigSliderModel> bigSliderModelList;
        public BigSliderViewHolder(@NonNull View itemView) {
            super(itemView);
            viewPagerBigSlidr = itemView.findViewById(R.id.viewPager_BigSlider);
        }

        private void setViewPagerBigSlidr(final List<BigSliderModel> bigSliderList) {
            currentPage = 2;
            if(timer != null ){
                timer.cancel();
            }
            bigSliderModelList =new ArrayList<>();
            for (int x=0;x<bigSliderList.size();x++){
                bigSliderModelList.add(x,bigSliderList.get(x));
            }
            bigSliderModelList.add(0,bigSliderList.get(bigSliderList.size()-2));
            bigSliderModelList.add(1,bigSliderList.get(bigSliderList.size()-1));
            bigSliderModelList.add(bigSliderList.get(0));
            bigSliderModelList.add(bigSliderList.get(1));

            BigSliderAdapter bigSliderAdapter = new BigSliderAdapter(bigSliderModelList);
            viewPagerBigSlidr.setAdapter(bigSliderAdapter);
            viewPagerBigSlidr.setClipToPadding(false);
            viewPagerBigSlidr.setPageMargin(10);
            viewPagerBigSlidr.setCurrentItem(currentPage);
            ViewPager.OnPageChangeListener onPageChangeListener = new ViewPager.OnPageChangeListener() {
                @Override
                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                }

                @Override
                public void onPageSelected(int position) {
                    currentPage = position;
                }

                @Override
                public void onPageScrollStateChanged(int state) {
                    if (state == ViewPager.SCROLL_STATE_IDLE) {
                        PageLopper(bigSliderModelList);
                    }
                }
            };
            viewPagerBigSlidr.addOnPageChangeListener(onPageChangeListener);
            StartAni(bigSliderModelList);
            viewPagerBigSlidr.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {
                    PageLopper(bigSliderModelList);
                    StopAni();
                    if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                        StartAni(bigSliderModelList);
                    }
                    return false;
                }
            });
        }

        private void PageLopper(List<BigSliderModel> bigSliderList) {
            if (currentPage == bigSliderList.size() - 2) {
                currentPage = 2;
                viewPagerBigSlidr.setCurrentItem(currentPage, false);
            }
            if (currentPage == 1) {
                currentPage = bigSliderList.size() - 3;
                viewPagerBigSlidr.setCurrentItem(currentPage, false);
            }
        }

        private void StartAni(final List<BigSliderModel> bigSliderList) {
            final Handler handler = new Handler();
            final Runnable runnable = new Runnable() {
                @Override
                public void run() {
                    if (currentPage >= bigSliderList.size()) {
                        currentPage = 1;
                    }
                    viewPagerBigSlidr.setCurrentItem(currentPage++, true);
                }
            };
            timer = new Timer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    handler.post(runnable);
                }
            }, dely, post);
        }

        private void StopAni() {
            timer.cancel();
        }
    }

    public class BannerADSViewHolder extends RecyclerView.ViewHolder {
        private ImageView bannerImg;
        private ConstraintLayout containerBannerAd;

        public BannerADSViewHolder(@NonNull View itemView) {
            super(itemView);
            containerBannerAd = itemView.findViewById(R.id.container_BannerAd);
            bannerImg = itemView.findViewById(R.id.bannerAd_img);
        }

        private void setBannerAds(String resource, String color) {
            Glide.with(itemView.getContext()).load(resource).apply(new RequestOptions().placeholder(R.drawable.s_bigslider_background)).into(bannerImg);
            containerBannerAd.setBackgroundColor(Color.parseColor(color));
        }
    }

    public class HorizontalViewHolder extends RecyclerView.ViewHolder {
        private TextView productH_header;
        private Button productH_viewAll;
        private RecyclerView recyclerProductH;
        private ConstraintLayout container;

        public HorizontalViewHolder(@NonNull View itemView) {
            super(itemView);

            recyclerProductH = itemView.findViewById(R.id.product_H_recycler);
            productH_header = itemView.findViewById(R.id.product_H_Header);
            productH_viewAll = itemView.findViewById(R.id.product_H_viewAll);
            container = itemView.findViewById(R.id.container);

            recyclerProductH.setRecycledViewPool(recycledViewPool);
        }

        private void setProductH(List<ProductHorizonModel> productH_List, final String header, String color, final List<MyWishlistModel> viewAllProductList) {
            container.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor(color)));
//todo- correction for T-107
            for (ProductHorizonModel model: productH_List){
                if (!model.getPrOID().isEmpty() && model.getTitle().isEmpty()){
                    firebaseFirestore.collection("PRODUCTS").document(model.getPrOID())
                            .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if(task.isSuccessful()){
                                model.setTitle(task.getResult().getString("product_title"));
                                model.setProductImg(task.getResult().getString("product_img_1"));
                                model.setPriceUnit(task.getResult().getString("price_Rs"));

                                MyWishlistModel wishlistModel = viewAllProductList.get(productH_List.indexOf(model));
                                wishlistModel.setTotalRating(task.getResult().getLong("rating_total"));
                                wishlistModel.setAvgRating(task.getResult().get("rating_avg").toString());
                                wishlistModel.setProductTitle(task.getResult().getString("product_title"));
                                wishlistModel.setProductPriceBig(task.getResult().getString("price_Rs"));
                                wishlistModel.setProductImg(task.getResult().getString("product_img_1"));
                                wishlistModel.setInStock(task.getResult().getLong("stock_quantity") >0);

                                if (productH_List.indexOf(model) == productH_List.size()-1){
                                    if (recyclerProductH.getAdapter() != null){
                                        recyclerProductH.getAdapter().notifyDataSetChanged();
                                    }
                                }


                            }else {
                                //do nothing
                            }
                        }
                    });
                }
            }
// correction for T-107
            if(productH_List.size() > 7){
                productH_viewAll.setVisibility(View.VISIBLE);
                productH_viewAll.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        ViewAllActivity.horizonProductList = viewAllProductList; //public static
                        Intent i = new Intent(itemView.getContext(), ViewAllActivity.class);
                        i.putExtra("layout_ID",0);
                        i.putExtra("title",header);
                        itemView.getContext().startActivity(i);
                    }
                });
            }else {
                productH_viewAll.setVisibility(View.INVISIBLE);
            }
            productH_header.setText(header);
            ProductHorizonAdapter productH_Adapter = new ProductHorizonAdapter(productH_List);
            recyclerProductH.setAdapter(productH_Adapter);

            //GridLayoutManager gridLayoutManager = new GridLayoutManager(itemView.getContext(),2,GridLayoutManager.VERTICAL,false);

            LinearLayoutManager layoutManager = new LinearLayoutManager(itemView.getContext());
            layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
            recyclerProductH.setLayoutManager(layoutManager);
            productH_Adapter.notifyDataSetChanged();
        }
    }

    public class GridViewHolder extends RecyclerView.ViewHolder{ //TODO=error producing
        TextView gridProHeader;
        Button gridViewall;
        GridLayout gridProductLayout;
        ConstraintLayout container;
        public GridViewHolder(@NonNull View itemView) {
            super(itemView);
            gridProHeader = itemView.findViewById(R.id.product_G_header);
            gridViewall = itemView.findViewById(R.id.product_G_viewall);
            gridProductLayout = itemView.findViewById(R.id.grid_layout);
            container = itemView.findViewById(R.id.container);
        }

        private void setGridProductView(final List<ProductHorizonModel> productH_List, final String title, String color){
            container.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor(color)));
            gridProHeader.setText(title);
//todo- correction for T-107
            for (final ProductHorizonModel model: productH_List){
                if (!model.getPrOID().isEmpty() && model.getTitle().isEmpty()){
                    firebaseFirestore.collection("PRODUCTS").document(model.getPrOID())
                            .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if(task.isSuccessful()){
                                model.setTitle(task.getResult().getString("product_title"));
                                model.setProductImg(task.getResult().getString("product_img_1"));
                                model.setPriceUnit(task.getResult().getString("price_Rs"));


                                if (productH_List.indexOf(model) == productH_List.size()-1){
                                    //setGridData(title,productH_List);

                                    for(int x=0; x<4; x++){
                                        ImageView productImageView = gridProductLayout.getChildAt(x).findViewById(R.id.product_H_item_img);
                                        Glide.with(itemView.getContext()).load(productH_List.get(x).getProductImg()).apply(new RequestOptions().placeholder(R.drawable.i_adlogo)).into(productImageView);
                                        gridProductLayout.getChildAt(x).setBackgroundColor(Color.parseColor("#FFFFFF"));
                                        if(!title.equals("")) {
                                            final int finalX = x;
                                            gridProductLayout.getChildAt(x).setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View view) {
                                                    Intent i = new Intent(itemView.getContext(), ProductDetailActivity.class);
                                                    i.putExtra("product_ID", productH_List.get(finalX).getPrOID());
                                                    itemView.getContext().startActivity(i);
                                                }
                                            });
                                        }
                                    }



                                    if (!title.equals("")) {
                                        gridViewall.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View view) {
                                                ViewAllActivity.productG_List = productH_List;
                                                Intent i = new Intent(itemView.getContext(), ViewAllActivity.class);
                                                i.putExtra("layout_ID", 1);
                                                i.putExtra("title", title);
                                                itemView.getContext().startActivity(i);
                                            }
                                        });
                                    }
                                }


                            }else {
                                //do nothing
                            }
                        }
                    });
                }
            }
            for(int x=0; x<4; x++){
                ImageView productImageView = gridProductLayout.getChildAt(x).findViewById(R.id.product_H_item_img);
                Glide.with(itemView.getContext()).load(productH_List.get(x).getProductImg()).apply(new RequestOptions().placeholder(R.drawable.i_adlogo)).into(productImageView);
                gridProductLayout.getChildAt(x).setBackgroundColor(Color.parseColor("#FFFFFF"));
                if(!title.equals("")) {
                    final int finalX = x;
                    gridProductLayout.getChildAt(x).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent i = new Intent(itemView.getContext(), ProductDetailActivity.class);
                            i.putExtra("product_ID", productH_List.get(finalX).getPrOID());
                            itemView.getContext().startActivity(i);
                        }
                    });
                }
            }

            //setGridData(title,productH_List);//TODO=error producing

        }////
        private void setGridData(String title,final List<ProductHorizonModel> productH_List){
            for(int x=0; x<4; x++){
                ImageView productImageView = gridProductLayout.getChildAt(x).findViewById(R.id.product_H_item_img);
                TextView productTitle = gridProductLayout.getChildAt(x).findViewById(R.id.product_H_item_name);
                TextView productCoin = gridProductLayout.getChildAt(x).findViewById(R.id.product_H_item_coin);
                TextView productPrice = gridProductLayout.getChildAt(x).findViewById(R.id.product_H_item_price);
//TODO=error producing{
                //Glide.with(itemView.getContext()).load(productH_List.get(x).getProductImg()).apply(new RequestOptions().placeholder(R.drawable.i_adlogo)).into(productImageView);
                Glide.with(itemView.getContext()).load(R.drawable.i_vector_thank_you).into(productImageView);
                //TODO=error producing}

                //productTitle.setText(productH_List.get(x).getTitle());
                //productCoin.setText(productH_List.get(x).getCoinUnit());
                //productPrice.setText("Rs."+productH_List.get(x).getPriceUnit()+"/-");
                gridProductLayout.getChildAt(x).setBackgroundColor(Color.parseColor("#FFFFFF"));

                if(!title.equals("")) {
                    final int finalX = x;
                    gridProductLayout.getChildAt(x).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent i = new Intent(itemView.getContext(), ProductDetailActivity.class);
                            i.putExtra("product_ID", productH_List.get(finalX).getPrOID());
                            itemView.getContext().startActivity(i);
                        }
                    });
                }
            }

        }
    }

}
