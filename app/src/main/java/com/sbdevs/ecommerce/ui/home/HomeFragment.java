package com.sbdevs.ecommerce.ui.home;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.sbdevs.ecommerce.R;

import com.sbdevs.ecommerce.activities.DBqueriesClass;
import com.sbdevs.ecommerce.activities.MainActivity2;
import com.sbdevs.ecommerce.adapters.CategoryAdapter;
import com.sbdevs.ecommerce.models.BigSliderModel;
import com.sbdevs.ecommerce.models.CategoryModel;
import com.sbdevs.ecommerce.models.MyWishlistModel;
import com.sbdevs.ecommerce.models.ProductHorizonModel;

import java.util.ArrayList;
import java.util.List;

//Todo- PUBLIC STATIC VERIAVLE ADDED
public class HomeFragment extends Fragment {

    private RecyclerView recyclerCategory;
    private RecyclerView recyclerHome;
    HomeAdapter adapter;
    private ImageView noConnectionImg;
    public  SwipeRefreshLayout swipeRefreshLayout;

    ConnectivityManager connectivityManager;
    NetworkInfo networkInfo;


    private FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    private List<CategoryModel> categoryModelList = new ArrayList<CategoryModel>();
    private List<HomeModel> homeModelList = new ArrayList<HomeModel>();
    private List<MyWishlistModel> proH_ViewAll=new ArrayList<>();
    //fake lists
    private List<CategoryModel> categoryModelList2 = new ArrayList<>();

    private Button retryBtn;




    //TODO=====================================================================================================================

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        recyclerCategory = view.findViewById(R.id.category_recycler_home);
        recyclerHome = view.findViewById(R.id.home_recycler);
        firebaseFirestore = FirebaseFirestore.getInstance();
        swipeRefreshLayout = view.findViewById(R.id.refreshLayout);

        retryBtn = view.findViewById(R.id.retryBtn);

        noConnectionImg = view.findViewById(R.id.noConnectionImg);
        FakeCategory();

        connectivityManager = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        networkInfo = connectivityManager.getActiveNetworkInfo();

        if(networkInfo != null && networkInfo.isConnected() == true) {


            noConnectionImg.setVisibility(View.GONE);
            retryBtn.setVisibility(View.GONE);
            recyclerCategory.setVisibility(View.VISIBLE);
            recyclerHome.setVisibility(View.VISIBLE);
            MainActivity2.lockMode = false;
            HomePage();
            Category();
        }else  {

            //MainActivity2.drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNDEFINED);
            MainActivity2.lockMode = true;
            Glide.with(this).load(R.mipmap.shop_outlet_icon).into(noConnectionImg);
            noConnectionImg.setVisibility(View.VISIBLE);
            retryBtn.setVisibility(View.VISIBLE);
            recyclerCategory.setVisibility(View.GONE);
            recyclerHome.setVisibility(View.GONE);
        }

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefreshLayout.setRefreshing(true);
                ReloadPage();
            }
        });
        retryBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ReloadPage();
            }
        });

        return view;
    }
    //TODO=====================================================================================================================
    private void Category(){
        categoryModelList.clear();
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);

        final CategoryAdapter categoryAdapter = new CategoryAdapter(categoryModelList);
        recyclerCategory.setAdapter(categoryAdapter);
        firebaseFirestore.collection("Categories").orderBy("index").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    for (QueryDocumentSnapshot documentSnapshot:task.getResult()){
                        categoryModelList.add(new CategoryModel(documentSnapshot.get("icon").toString(),documentSnapshot.get("categoryName").toString()));
                    }
                    categoryAdapter.notifyDataSetChanged();
                }else {
                    String error = task.getException().getMessage();
                    Toast.makeText(getContext(), error, Toast.LENGTH_LONG).show();
                }

            }
        });

    }
    //TODO=====================================================================================================================

    private void HomePage(){
        homeModelList.clear();
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerHome.setLayoutManager(layoutManager);
//        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(),2,GridLayoutManager.VERTICAL,false);
//        recyclerHome.setLayoutManager(gridLayoutManager);

        adapter = new HomeAdapter(homeModelList);
        recyclerHome.setAdapter(adapter);
        firebaseFirestore.collection("HOMEPAGE").orderBy("index").get().
                addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()){
                            for (QueryDocumentSnapshot documentSnapshot:task.getResult()){
                                if(((long)documentSnapshot.get("view_type"))==0){
                                    homeModelList.add(new HomeModel(0,documentSnapshot.get("image").toString(),documentSnapshot.get("bg_color").toString()));
                                }
                                else if((long)documentSnapshot.get("view_type")==1){

                                    List<BigSliderModel> bigSliderList = new ArrayList<BigSliderModel>();
                                    long no_of_sl = (long)documentSnapshot.get("no_of_sl");
                                    for (int x=1;x<no_of_sl+1;x++){
                                        bigSliderList.add(new BigSliderModel(documentSnapshot.get("big_sl_"+x).toString(),documentSnapshot.get("big_sl_"+x+"_bg").toString()));
                                    }
                                    homeModelList.add(new HomeModel(1,bigSliderList));
                                }
                                else if((long)documentSnapshot.get("view_type")==2){
                                    List<ProductHorizonModel> productH_List = new ArrayList<ProductHorizonModel>();



                                    ArrayList<String> productIds = (ArrayList<String>) documentSnapshot.get("products");
                                    //todo- correction for T-107,
                                    for (String productId : productIds){
                                        productH_List.add(new ProductHorizonModel(
                                                productId,
                                                "",
                                                "",
                                                "",
                                                ""));

                                        proH_ViewAll.add(new MyWishlistModel(
                                                productId,
                                                "",
                                                "",
                                                "",
                                                0,
                                                "",
                                                "",
                                                false));
                                    }
                                    // correction for T-107
                                    homeModelList.add(new HomeModel(2,documentSnapshot.get("layout_title").toString(),productH_List,documentSnapshot.get("bg_color").toString(),proH_ViewAll));
                                }
                                else if((long)documentSnapshot.get("view_type")==3){
                                    List<ProductHorizonModel> productG_List = new ArrayList<ProductHorizonModel>();
                                    //todo- correction for T-107
                                    ArrayList<String> productIds = (ArrayList<String>) documentSnapshot.get("products");
                                    //assert productIds != null;
                                    for (String productId : productIds){
                                        productG_List.add(new ProductHorizonModel(
                                                productId,
                                                "",
                                                "",
                                                "",
                                                ""));
                                    }
                                    // correction for T-107
                                    homeModelList.add(new HomeModel(3,documentSnapshot.get("layout_title").toString(),productG_List,documentSnapshot.get("bg_color").toString()));

                                }

                            }
                            adapter.notifyDataSetChanged();
                            swipeRefreshLayout.setRefreshing(false);
                        }else {
                            String error = task.getException().getMessage();
                            Toast.makeText(getContext(), error, Toast.LENGTH_LONG).show();
                        }
                    }
                });

    }

    private void FakeCategory(){
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        recyclerCategory.setLayoutManager(layoutManager);

        categoryModelList2.add(new CategoryModel("null",""));
        categoryModelList2.add(new CategoryModel("null",""));
        categoryModelList2.add(new CategoryModel("null",""));
        categoryModelList2.add(new CategoryModel("null",""));
        categoryModelList2.add(new CategoryModel("null",""));
        categoryModelList2.add(new CategoryModel("null",""));
        categoryModelList2.add(new CategoryModel("null",""));

        CategoryAdapter fakeCategoryAdapter = new CategoryAdapter(categoryModelList2);
        recyclerCategory.setAdapter(fakeCategoryAdapter);
        fakeCategoryAdapter.notifyDataSetChanged();
    }

    private void ReloadPage(){
        networkInfo = connectivityManager.getActiveNetworkInfo();
        //categoryModelList.clear();
        //homeModelList.clear();
        DBqueriesClass.clearData();
        FakeCategory();
        if(networkInfo != null && networkInfo.isConnected() == true) {

            MainActivity2.lockMode = false;
            noConnectionImg.setVisibility(View.GONE);
            retryBtn.setVisibility(View.GONE);
            recyclerCategory.setVisibility(View.VISIBLE);
            recyclerHome.setVisibility(View.VISIBLE);
            HomePage();
            Category();
        }else {
            MainActivity2.lockMode = true;
            Glide.with(getContext()).load(R.mipmap.shop_outlet_icon).into(noConnectionImg);
            noConnectionImg.setVisibility(View.VISIBLE);
            retryBtn.setVisibility(View.VISIBLE);
            swipeRefreshLayout.setRefreshing(false);
            recyclerCategory.setVisibility(View.GONE);
            recyclerHome.setVisibility(View.GONE);

        }
    }


}