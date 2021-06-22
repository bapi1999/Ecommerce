package com.sbdevs.ecommerce.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.sbdevs.ecommerce.R;
import com.sbdevs.ecommerce.adapters.ProductImgAdapter;
import com.sbdevs.ecommerce.fragments.LoginFragment;
import com.sbdevs.ecommerce.fragments.SignUpFragment;
import com.sbdevs.ecommerce.models.MyWishlistModel;
import com.sbdevs.ecommerce.models.deliveryItemModel;

import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.sbdevs.ecommerce.activities.RegisterActivity.setSignupFragment;


public class ProductDetailActivity extends AppCompatActivity {
    private ViewPager productImgViewPager;
    private TabLayout productImgIndicator;

    public static FloatingActionButton addToWishListBtn;

    public static boolean ALREADY_ADDED_TO_WISHLIST = false;
    public static boolean ALREADY_ADDED_TO_CART = false;

    public static boolean running_Wishlist_query = false;
    public static boolean running_Rating_query = false;
    public static boolean running_Cart_query = false;
    public static boolean fromSearch = false;


    private Button buyNowBtn;
    private LinearLayout addToCart_btn;

    private FirebaseFirestore firebaseFirestore;
    private FirebaseUser currentUser;

    private TextView productName, mini_avg_rating, mini_total_ratings, product_price;
    private TextView product_details_text_mini, productDetailsViewAll;

    //rating
    public static LinearLayout rateNowContainer;
    private TextView average_rating, totalRating;
    private LinearLayout ratings_number_container, rating_bar_container;
    public static int initialRating;
//rating

    public static String productID;

    private Dialog loadingDialog;

    private DocumentSnapshot documentSnapshot;
    public static MenuItem cartItem;
    private TextView badgeCount;
    public static Activity productDetailsActivity;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_detail);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        productName = findViewById(R.id.product_name);
        mini_avg_rating = findViewById(R.id.mini_product_rating);
        mini_total_ratings = findViewById(R.id.mini_totalNumberOf_ratings);
        product_price = findViewById(R.id.product_price);
        product_details_text_mini = findViewById(R.id.product_details_text_mini);

        average_rating = findViewById(R.id.average_rating_text);
        totalRating = findViewById(R.id.totalRating);

        ratings_number_container = findViewById(R.id.ratings_number_container);
        rating_bar_container = findViewById(R.id.rating_bar_containter);
        addToCart_btn = findViewById(R.id.addToCart_btn);

        productImgViewPager = findViewById(R.id.product_Img_viewPager);
        productImgIndicator = findViewById(R.id.product_Img_indicator);
        productImgIndicator.setupWithViewPager(productImgViewPager, true);
        initialRating = -1;
        rateNowContainer = findViewById(R.id.rate_now_container);

        //loading dialog
        loadingDialog = new Dialog(ProductDetailActivity.this);
        loadingDialog.setContentView(R.layout.ast_loading_progress_dialog);
        loadingDialog.setCancelable(false);
        loadingDialog.getWindow().setBackgroundDrawable(getDrawable(R.drawable.s_bigslider_background));
        loadingDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        loadingDialog.show();
        //loading dialog


//TODO- Main query###################################################################################################
        firebaseFirestore = FirebaseFirestore.getInstance();
        final List<String> productImgList = new ArrayList<>();
        productID = getIntent().getStringExtra("product_ID");
        firebaseFirestore.collection("PRODUCTS").document(productID)
                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isComplete()) {
                    documentSnapshot = task.getResult();
                    for (long x = 1; x < (long) documentSnapshot.get("no_of_img") + 1; x++) {
                        productImgList.add(documentSnapshot.get("product_img_" + x).toString());
                    }
                    ProductImgAdapter adapter = new ProductImgAdapter(productImgList);
                    productImgViewPager.setAdapter(adapter);
                    productName.setText(documentSnapshot.get("product_title").toString());
                    mini_avg_rating.setText(documentSnapshot.get("rating_avg").toString());
                    mini_total_ratings.setText("(" + documentSnapshot.get("rating_total").toString() + ") ratings");
                    product_price.setText("Rs." + documentSnapshot.get("price_Rs").toString() + "/-");
                    product_details_text_mini.setText(documentSnapshot.get("product_details").toString());

                    average_rating.setText(documentSnapshot.get("rating_avg").toString());
                    totalRating.setText(documentSnapshot.get("rating_total").toString());
                    for (int x = 0; x < 5; x++) {
                        TextView rating = (TextView) ratings_number_container.getChildAt(x);
                        rating.setText(documentSnapshot.get("rating_Star_" + (5 - x)).toString());

                        ProgressBar progressBar = (ProgressBar) rating_bar_container.getChildAt(x);
                        int maxProgress = Integer.parseInt(documentSnapshot.get("rating_total").toString());
                        progressBar.setMax(maxProgress);
                        String perccing = documentSnapshot.get("rating_Star_" + (5 - x)).toString();
                        int progress = Integer.valueOf(perccing);
                        progressBar.setProgress(progress);
                    }


                    if ((boolean) documentSnapshot.get("in_stock")) {

                        //todo = Add to cart ##################################################################

                        addToCart_btn.setOnClickListener(new View.OnClickListener()  {
                            @Override
                            public void onClick(View view) {
                                if (currentUser == null) {
                                    SignInUpDialog();
                                } else {
                                    if (!running_Cart_query) {
                                        running_Cart_query = true;
                                        if (ALREADY_ADDED_TO_CART) {
                                            Toast.makeText(ProductDetailActivity.this, "Alredy added to cart", Toast.LENGTH_SHORT).show();
                                        } else {

                                            Map<String, Object> addProduct = new HashMap<>();
                                            addProduct.put(String.valueOf(DBqueriesClass.cartList.size()) + "_product_id", productID);
                                            addProduct.put("listSize", (long) (DBqueriesClass.cartList.size() + 1));

                                            firebaseFirestore.collection("USERS").document(currentUser.getUid())
                                                    .collection("USER_DATA").document("MY_CART")
                                                    .update(addProduct).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()) {


                                                        if (DBqueriesClass.cartList.size() != 0) {
                                                            DBqueriesClass.cartItemModelsList.add(0, new deliveryItemModel(
                                                                    deliveryItemModel.CART_ITEM,
                                                                    productID,
                                                                    documentSnapshot.get("product_img_1").toString(),
                                                                    documentSnapshot.get("product_title").toString(),
                                                                    documentSnapshot.get("price_Rs").toString(),
                                                                    (long) 1,
                                                                    (boolean) documentSnapshot.get("in_stock"),
                                                                    (long) documentSnapshot.get("max_quantity"),
                                                                    (long) documentSnapshot.get("stock_quantity")));
                                                        }
                                                        ALREADY_ADDED_TO_CART = true;
                                                        DBqueriesClass.cartList.add(productID);
                                                        Toast.makeText(ProductDetailActivity.this, "Added to cart successfully ", Toast.LENGTH_SHORT).show();
                                                        invalidateOptionsMenu();
                                                        running_Cart_query = false;
                                                    } else {
                                                        running_Cart_query = false;
                                                        String error = task.getException().getMessage();
                                                        Toast.makeText(ProductDetailActivity.this, error, Toast.LENGTH_SHORT).show();
                                                    }
                                                }
                                            });
                                        }
                                    } else {
                                        running_Cart_query = false;
                                    }

                                }
                            }
                        });

                    } else {
                        buyNowBtn.setVisibility(View.GONE);
                        TextView outofStock = (TextView) addToCart_btn.getChildAt(0);
                        outofStock.setText("Out Of Stock");
                        outofStock.setTextColor(getResources().getColor(R.color.gray));
                        outofStock.setCompoundDrawables(null, null, null, null);
                    }


                    if (currentUser != null) {
                        if (DBqueriesClass.myRatingNumber.size() == 0) {
                            DBqueriesClass.loadRatingList(ProductDetailActivity.this);
                        }
                        if (DBqueriesClass.cartList.size() == 0) {
                            DBqueriesClass.loadCartlist(ProductDetailActivity.this, loadingDialog, false, badgeCount, new TextView(ProductDetailActivity.this), new LinearLayout(ProductDetailActivity.this));
                        }
                        if (DBqueriesClass.wishLishList1.size() == 0) {
                            DBqueriesClass.LoadWishlist(ProductDetailActivity.this, loadingDialog, false);
                        } else {
                            loadingDialog.dismiss();
                        }

                    } else {
                        loadingDialog.dismiss();
                    }

                    //rating
                    if (DBqueriesClass.myRatedProductID.contains(productID)) {
                        int index = DBqueriesClass.myRatedProductID.indexOf(productID);
                        initialRating = Integer.parseInt(String.valueOf(DBqueriesClass.myRatingNumber.get(index))) - 1;
                        setRating(initialRating);
                    }
                    //rating

                    //wish list
                    if (DBqueriesClass.wishLishList1.contains(productID)) {
                        ALREADY_ADDED_TO_WISHLIST = true;
                        addToWishListBtn.setSupportImageTintList(getResources().getColorStateList(R.color.red));
                    } else {
                        ALREADY_ADDED_TO_WISHLIST = false;
                        addToWishListBtn.setSupportImageTintList(getResources().getColorStateList(R.color.gray));
                    }
                    //wish list

                    //cart list
                    if (DBqueriesClass.cartList.contains(productID)) {
                        ALREADY_ADDED_TO_CART = true;
                    } else {
                        ALREADY_ADDED_TO_CART = false;
                    }
                    //cart list


                } else {
                    loadingDialog.dismiss();
                    String error = task.getException().getMessage();
                    Toast.makeText(ProductDetailActivity.this, error, Toast.LENGTH_SHORT).show();
                }
            }
        });

//todo Wishlist ########################################################################################################
        addToWishListBtn = findViewById(R.id.addToWishList_Btn);
        addToWishListBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (currentUser == null) {
                    SignInUpDialog();
                } else {

                    if (!running_Wishlist_query) {//todo- error doubt
                        running_Wishlist_query = true;
                        if (ALREADY_ADDED_TO_WISHLIST) {
                            int index = DBqueriesClass.wishLishList1.indexOf(productID);
                            DBqueriesClass.removeFromWishlist(index, ProductDetailActivity.this);
                            addToWishListBtn.setSupportImageTintList(getResources().getColorStateList(R.color.disAbled));
                        } else {


                            addToWishListBtn.setSupportImageTintList(getResources().getColorStateList(R.color.red));
                            Map<String, Object> addProduct = new HashMap<>();
                            addProduct.put(String.valueOf(DBqueriesClass.wishLishList1.size()) + "_product_id", productID);

                            firebaseFirestore.collection("USERS").document(currentUser.getUid())
                                    .collection("USER_DATA").document("MY_WISH_LIST")
                                    .update(addProduct).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {

                                    if (task.isSuccessful()) {
                                        Map<String, Object> updatelistSize = new HashMap<>();
                                        updatelistSize.put("listSize", (long) (DBqueriesClass.wishLishList1.size() + 1));

                                        firebaseFirestore.collection("USERS").document(currentUser.getUid())
                                                .collection("USER_DATA").document("MY_WISH_LIST")
                                                .update(updatelistSize).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {

                                                if (task.isSuccessful()) {
                                                    if (DBqueriesClass.wishLishList1.size() != 0) {
                                                        DBqueriesClass.wishlistModelList.add(new MyWishlistModel(productID,
                                                                documentSnapshot.get("product_img_1").toString(),
                                                                documentSnapshot.get("product_title").toString(),
                                                                documentSnapshot.get("rating_avg").toString(),
                                                                documentSnapshot.getLong("rating_total"),
                                                                documentSnapshot.get("price_Rs").toString(),
                                                                documentSnapshot.get("price_Rs").toString(),
                                                                (boolean) documentSnapshot.get("in_stock")));
                                                    }
                                                    ALREADY_ADDED_TO_WISHLIST = true;
                                                    addToWishListBtn.setSupportImageTintList(getResources().getColorStateList(R.color.red));
                                                    DBqueriesClass.wishLishList1.add(productID);
                                                    Toast.makeText(ProductDetailActivity.this, "Added to wishlist successfully ", Toast.LENGTH_SHORT).show();

                                                } else {
                                                    addToWishListBtn.setSupportImageTintList(getResources().getColorStateList(R.color.disAbled));
                                                    String error = task.getException().getMessage();
                                                    Toast.makeText(ProductDetailActivity.this, error, Toast.LENGTH_SHORT).show();
                                                }

                                                running_Wishlist_query = false;
                                            }
                                        });

                                    } else {

                                        running_Wishlist_query = false;
                                        String error = task.getException().getMessage();
                                        Toast.makeText(ProductDetailActivity.this, error, Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                            /////

                        }////
                    }
                }
            }
        });
//todo Reting ###############################################################################################3
        for (int x = 0; x < rateNowContainer.getChildCount(); x++) {
            final int starPosition = x;
            rateNowContainer.getChildAt(x).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (currentUser == null) {
                        SignInUpDialog();
                    } else {
                        if (starPosition != initialRating) {
                            if (!running_Rating_query) {
                                running_Rating_query = true;
                                setRating(starPosition);

                                Map<String, Object> updateRating = new HashMap<>();
                                if (DBqueriesClass.myRatedProductID.contains(productID)) {

                                    TextView oldRating = (TextView) ratings_number_container.getChildAt(5 - initialRating - 1);
                                    TextView finalRating = (TextView) ratings_number_container.getChildAt(5 - starPosition - 1);

                                    updateRating.put("rating_Star_" + (initialRating + 1), Long.parseLong(oldRating.getText().toString()) - 1);
                                    updateRating.put("rating_Star_" + (starPosition + 1), Long.parseLong(finalRating.getText().toString()) + 1);
                                    updateRating.put("rating_avg", calculateAvargeRating((long) starPosition - initialRating, true));

                                } else {

                                    //productRating.put("rating_Star_" + starPosition + 1, Long.parseLong(String.valueOf(documentSnapshot.get("rating_Star_" + starPosition + 1))) + 1);// todo - most dengours error. try to fix it
                                    updateRating.put("rating_Star_" + (starPosition + 1), (long) documentSnapshot.get("rating_Star_" + (starPosition + 1)) + 1);
                                    updateRating.put("rating_avg", calculateAvargeRating((long) starPosition + 1, false));
                                    updateRating.put("rating_total", (long) documentSnapshot.get("rating_total") + 1);
                                }

                                firebaseFirestore.collection("PRODUCTS").document(productID)
                                        .update(updateRating).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {

                                            Map<String, Object> myRatingMap = new HashMap<>();
                                            if (DBqueriesClass.myRatedProductID.contains(productID)) {
                                                myRatingMap.put(DBqueriesClass.myRatedProductID.indexOf(productID) + "_rating", (long) starPosition + 1);
                                            } else {
                                                myRatingMap.put("listSize", (long) DBqueriesClass.myRatedProductID.size() + 1);
                                                myRatingMap.put(DBqueriesClass.myRatedProductID.size() + "_product_id", productID);
                                                myRatingMap.put(DBqueriesClass.myRatedProductID.size() + "_rating", (long) starPosition + 1);
                                            }
                                            firebaseFirestore.collection("USERS").document(currentUser.getUid())
                                                    .collection("USER_DATA").document("MY_RATINGS")
                                                    .update(myRatingMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()) {

                                                        if (DBqueriesClass.myRatedProductID.contains(productID)) {

                                                            DBqueriesClass.myRatingNumber.set(DBqueriesClass.myRatedProductID.indexOf(productID), (long) starPosition + 1);

                                                            TextView oldRating = (TextView) ratings_number_container.getChildAt(5 - initialRating - 1);
                                                            TextView finalRating = (TextView) ratings_number_container.getChildAt(5 - starPosition - 1);

                                                            oldRating.setText(String.valueOf(Integer.parseInt(oldRating.getText().toString()) - 1));
                                                            finalRating.setText(String.valueOf(Integer.parseInt(finalRating.getText().toString()) + 1));

                                                        } else {
                                                            DBqueriesClass.myRatedProductID.add(productID);
                                                            DBqueriesClass.myRatingNumber.add((long) starPosition + 1);//todo-string & int problem

                                                            TextView rating = (TextView) ratings_number_container.getChildAt(5 - starPosition - 1);
                                                            rating.setText(String.valueOf(Integer.parseInt(rating.getText().toString()) + 1));

                                                            mini_total_ratings.setText("(" + ((long) documentSnapshot.get("rating_total") + 1) + ")ratings");
                                                            totalRating.setText(String.valueOf((long) documentSnapshot.get("rating_total") + 1));


                                                            Toast.makeText(ProductDetailActivity.this, "Thank you for rating", Toast.LENGTH_SHORT).show();
                                                        }

                                                        for (int x = 0; x < 5; x++) {
                                                            TextView rating1 = (TextView) ratings_number_container.getChildAt(x);


                                                            ProgressBar progressBar = (ProgressBar) rating_bar_container.getChildAt(x);

                                                            if (!DBqueriesClass.myRatedProductID.contains(productID)) {
                                                                int maxProgress = Integer.parseInt(totalRating.getText().toString());
                                                                progressBar.setMax(maxProgress);
                                                            }
                                                            int progress = Integer.parseInt(rating1.getText().toString());
                                                            progressBar.setProgress(progress);
                                                        }
                                                        initialRating = starPosition;

                                                        average_rating.setText(calculateAvargeRating(0, true));
                                                        mini_avg_rating.setText(calculateAvargeRating(0, true));
                                                        if (DBqueriesClass.wishLishList1.contains(productID) && DBqueriesClass.wishlistModelList.size() != 0) {
                                                            int index = DBqueriesClass.wishLishList1.indexOf(productID);

                                                            DBqueriesClass.wishlistModelList.get(index).setAvgRating(average_rating.getText().toString());
                                                            DBqueriesClass.wishlistModelList.get(index).setTotalRating(Long.parseLong(totalRating.getText().toString()));
                                                        }
                                                    } else {
                                                        setRating(initialRating);
                                                        String error = task.getException().getMessage();
                                                        Toast.makeText(ProductDetailActivity.this, error, Toast.LENGTH_SHORT).show();
                                                    }
                                                    running_Rating_query = false;
                                                }
                                            });
                                        } else {
                                            running_Rating_query = false;
                                            setRating(initialRating);
                                            String error = task.getException().getMessage();
                                            Toast.makeText(ProductDetailActivity.this, error, Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });

                            }
                        }
                    }///
                }
            });
        }
//TODO- Buy Now ###################################################################################################
        buyNowBtn = findViewById(R.id.buy_Now_btn);
        buyNowBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (currentUser == null) {
                    SignInUpDialog();
                } else {
                    loadingDialog.show();
                    productDetailsActivity = ProductDetailActivity.this;
                    DeliveryActivity.fromcart = false;
                    DeliveryActivity.cartItemModelsList = new ArrayList<>();
                    DeliveryActivity.cartItemModelsList.add(new deliveryItemModel(
                            deliveryItemModel.CART_ITEM,
                            productID,
                            documentSnapshot.get("product_img_1").toString(),
                            documentSnapshot.get("product_title").toString(),
                            documentSnapshot.get("price_Rs").toString(),
                            (long) 1,
                            (boolean) documentSnapshot.get("in_stock"),
                            (long) documentSnapshot.get("max_quantity"),
                            (long) documentSnapshot.get("stock_quantity")));
                    DeliveryActivity.cartItemModelsList.add(new deliveryItemModel(deliveryItemModel.TOTAL_AMOUNT_LAY));

                    if (DBqueriesClass.addressModelList.size() == 0) {
                        DBqueriesClass.loadAddresses(ProductDetailActivity.this, loadingDialog);
                    } else {
                        loadingDialog.dismiss();
                        Intent i = new Intent(ProductDetailActivity.this, DeliveryActivity.class);
                        startActivity(i);
                    }


                }
            }
        });

        productDetailsViewAll = findViewById(R.id.product_details_viewAll);
        productDetailsViewAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //setFragment(new  ProductAllDetailsFragment()); // use activity for safety parpous, and to avoid numerous amount of coding
                Intent i1 = new Intent(ProductDetailActivity.this, ProductSpecificDetailsActivity.class);
                startActivity(i1);

            }
        });

    }



    @Override
    protected void onStart() {
        super.onStart();
        currentUser = FirebaseAuth.getInstance().getCurrentUser();


        if (currentUser != null) {
            //rating
            if (DBqueriesClass.myRatingNumber.size() == 0) {
                DBqueriesClass.loadRatingList(ProductDetailActivity.this);
            }
            //wish List
            if (DBqueriesClass.wishLishList1.size() == 0) {
                DBqueriesClass.LoadWishlist(ProductDetailActivity.this, loadingDialog, false);

            } else {
                loadingDialog.dismiss();
            }

        } else {
            loadingDialog.dismiss();
        }

        //rating
        if (DBqueriesClass.myRatedProductID.contains(productID)) {
            int index = DBqueriesClass.myRatedProductID.indexOf(productID);
            initialRating = Integer.parseInt(String.valueOf(DBqueriesClass.myRatingNumber.get(index))) - 1;
            setRating(initialRating);
        }
        //rating

        //wish List
        if (DBqueriesClass.wishLishList1.contains(productID)) {
            ALREADY_ADDED_TO_WISHLIST = true;
            addToWishListBtn.setSupportImageTintList(getResources().getColorStateList(R.color.red));
        } else {
            addToWishListBtn.setSupportImageTintList(getResources().getColorStateList(R.color.gray));
            ALREADY_ADDED_TO_WISHLIST = false;
        }
        //wish List

        //cart List
        if (DBqueriesClass.cartList.contains(productID)) {
            ALREADY_ADDED_TO_CART = true;
        } else {
            ALREADY_ADDED_TO_CART = false;
        }
        //cart List
        invalidateOptionsMenu();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.search_cart_menue, menu);

        cartItem = menu.findItem(R.id.main_cart);
        cartItem.setActionView(R.layout.ast_badge_lay);
        ImageView badgeIcon = cartItem.getActionView().findViewById(R.id.badge_icon);
        badgeIcon.setImageResource(R.drawable.ic_shopping_cart_24);
        badgeCount = cartItem.getActionView().findViewById(R.id.badge_count);

        if (currentUser != null) {
            if (DBqueriesClass.cartList.size() == 0) {
                badgeCount.setVisibility(View.INVISIBLE);
                DBqueriesClass.loadCartlist(ProductDetailActivity.this, loadingDialog, false, badgeCount, new TextView(ProductDetailActivity.this), new LinearLayout(ProductDetailActivity.this));
            }
            else {
                badgeCount.setVisibility(View.VISIBLE);
                if (DBqueriesClass.cartList.size() < 99) {
                    badgeCount.setText(String.valueOf(DBqueriesClass.cartList.size()));
                } else {
                    badgeCount.setText("99");
                }
            }
        }

        cartItem.getActionView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (currentUser == null) {
                    SignInUpDialog();
                } else {
                    Intent i1 = new Intent(ProductDetailActivity.this, MyCartActivity.class);
                    startActivity(i1);
                }
            }
        });

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.main_search) {
            if (fromSearch) {
                finish();
            } else {
                Intent searchIntent = new Intent(this, SearchActivity.class);
                startActivity(searchIntent);
            }
            return true;
        } else if (id == R.id.main_cart) {

            if (currentUser == null) {
                SignInUpDialog();
            } else {
                Intent i1 = new Intent(ProductDetailActivity.this, MyCartActivity.class);
                startActivity(i1);
            }

            return true;
        } else if (id == android.R.id.home) {
            productDetailsActivity = null;
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    public void SignInUpDialog() {
        final Dialog singInDialog = new Dialog(ProductDetailActivity.this);
        singInDialog.setContentView(R.layout.ast_sign_in_dialog);
        singInDialog.setCancelable(true);
        singInDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        Button dialogSignInBtn = singInDialog.findViewById(R.id.signInBtn);
        Button dialogSignUpBtn = singInDialog.findViewById(R.id.signUpBtn);
        final Intent registerIntent = new Intent(ProductDetailActivity.this, RegisterActivity.class);

        dialogSignInBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LoginFragment.disableCloseBtn = true;
                setSignupFragment = false;
                startActivity(registerIntent);
                singInDialog.dismiss();
            }
        });
        dialogSignUpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SignUpFragment.disableCloseBtn = true;
                singInDialog.dismiss();
                setSignupFragment = true;
                startActivity(registerIntent);
            }
        });
        singInDialog.show();
    }

    public static void setRating(int starPosition) {

        for (int y = 0; y < rateNowContainer.getChildCount(); y++) {
            ImageView starBtn = (ImageView) rateNowContainer.getChildAt(y);
            starBtn.setImageTintList(ColorStateList.valueOf(Color.parseColor("#90A4AE")));
            if (y <= starPosition) {
                starBtn.setImageTintList(ColorStateList.valueOf(Color.parseColor("#FDD835")));
            }
        }


    }

    private String  calculateAvargeRating(long currentUserReting,boolean update) {
        Double totalStars = Double.valueOf(0);
        for (int x = 1; x < 6; x++) {
            TextView ratingNo = (TextView) ratings_number_container.getChildAt(5 - x);
            totalStars = totalStars + (Long.parseLong(ratingNo.getText().toString())*x);
        }
        totalStars = totalStars + currentUserReting;
        if (update){
            return String.valueOf(totalStars / Long.parseLong(totalRating.getText().toString())).substring(0,4);
        }else {
            return String.valueOf(totalStars / (Long.parseLong(totalRating.getText().toString()) + 1)).substring(0,4);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        fromSearch = false;
    }

    @Override
    public void onBackPressed() {
        productDetailsActivity = null;
        super.onBackPressed();
    }
}