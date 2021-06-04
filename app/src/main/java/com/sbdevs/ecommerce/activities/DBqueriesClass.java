package com.sbdevs.ecommerce.activities;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;

import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.sbdevs.ecommerce.R;
import com.sbdevs.ecommerce.models.AddressModel;
import com.sbdevs.ecommerce.models.MyWishlistModel;
import com.sbdevs.ecommerce.models.NotificationModel;
import com.sbdevs.ecommerce.models.deliveryItemModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class DBqueriesClass {

    public static String email,fullname,profilePic;

    public static boolean addressSelected = false;

    public static FirebaseFirestore firebaseFirestore1 = FirebaseFirestore.getInstance();

    public static List<String> wishLishList1 = new ArrayList<>();
    public static List<MyWishlistModel> wishlistModelList = new ArrayList<>();

    public static List<String> myRatedProductID = new ArrayList<>();
    public static List<Long> myRatingNumber = new ArrayList<>();

    public static List<String> cartList = new ArrayList<>();
    public static List<deliveryItemModel> cartItemModelsList  = new ArrayList<>();

    public static int selectedAddress = -1;
    public static List<AddressModel> addressModelList = new ArrayList<>();

    public static List<NotificationModel> notificationModelList = new ArrayList<>();

    public static ListenerRegistration registration ;


    public static void loadRatingList(final Context context) {
        if (!ProductDetailActivity.running_Rating_query) {
            ProductDetailActivity.running_Rating_query =true;
            myRatedProductID.clear();
            myRatingNumber.clear();
            firebaseFirestore1.collection("USERS").document(FirebaseAuth.getInstance().getUid())
                    .collection("USER_DATA").document("MY_RATINGS")
                    .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        for (long x = 0; x < (long) task.getResult().get("listSize"); x++) {
                            myRatedProductID.add(task.getResult().get(x + "_product_id").toString());
                            myRatingNumber.add((long) task.getResult().get(x + "_rating"));
                            if (task.getResult().get(x + "_product_id").toString().equals(ProductDetailActivity.productID)) {

                                ProductDetailActivity.initialRating = Integer.parseInt(String.valueOf((long) task.getResult().get(x + "_rating"))) - 1;
                                if ( ProductDetailActivity.rateNowContainer != null) {
                                    ProductDetailActivity.setRating(ProductDetailActivity.initialRating);
                                }
                            }
                        }
                    } else {
                        String error = task.getException().getMessage();
                        Toast.makeText(context, error, Toast.LENGTH_SHORT).show();
                    }
                    ProductDetailActivity.running_Rating_query = false;
                }

            });
        }

    }

    public static void LoadWishlist(final Context context, final Dialog dialog, final boolean loadProductData)  {
        wishLishList1.clear();
        firebaseFirestore1.collection("USERS").document(FirebaseAuth.getInstance().getUid())
                .collection("USER_DATA").document("MY_WISH_LIST")
                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    for (int x = 0; x < (long) task.getResult().get("listSize"); x++) {

                        wishLishList1.add(task.getResult().get(x + "_product_id").toString());

                        if (DBqueriesClass.wishLishList1.contains(ProductDetailActivity.productID)) {
                            ProductDetailActivity.ALREADY_ADDED_TO_WISHLIST = true;
                            if (ProductDetailActivity.addToWishListBtn != null) {
                                ProductDetailActivity.addToWishListBtn.setSupportImageTintList(context.getResources().getColorStateList(R.color.red));
                            }
                        } else {
                            if (ProductDetailActivity.addToWishListBtn != null) {
                                ProductDetailActivity.addToWishListBtn.setSupportImageTintList(context.getResources().getColorStateList(R.color.gray));
                            }
                            ProductDetailActivity.ALREADY_ADDED_TO_WISHLIST = false;
                        }


                        if (loadProductData) {
                            wishlistModelList.clear();
                            String productID = task.getResult().get(x + "_product_id").toString();
                            firebaseFirestore1.collection("PRODUCTS")
                                    .document(productID)
                                    .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    if (task.isSuccessful()) {
                                        wishlistModelList.add(new MyWishlistModel(
                                                productID,
                                                task.getResult().get("product_img_1").toString(),
                                                task.getResult().get("product_title").toString(),
                                                task.getResult().get("rating_avg").toString(),
                                                (long)task.getResult().get("rating_total"),
                                                task.getResult().get("price_Rs").toString(),
                                                task.getResult().get("price_coin").toString(),
                                                (boolean)task.getResult().get("in_stock")));//todo -creat it in product fire base
                                        MyWishlistActivity.myWishlistAdapter.notifyDataSetChanged();
                                    } else {
                                        String error = task.getException().getMessage();
                                        Toast.makeText(context, error, Toast.LENGTH_LONG).show();
                                    }

                                }
                            });
                        }
                    }
                } else {
                    String error = task.getException().getMessage();
                    Toast.makeText(context, error, Toast.LENGTH_LONG).show();

                }
                dialog.dismiss();
            }
        });
    }

    public static void removeFromWishlist(final int index, final Context context) {

        final String removedProductId = wishLishList1.get(index);
        wishLishList1.remove(index);

        Map<String, Object> updateWishlist = new HashMap<>();

        for (int x = 0; x < wishLishList1.size(); x++) {
            updateWishlist.put(x + "_product_id", wishLishList1.get(x));
        }
        updateWishlist.put("listSize", (long) wishLishList1.size());

        firebaseFirestore1.collection("USERS").document(FirebaseAuth.getInstance().getUid())
                .collection("USER_DATA").document("MY_WISH_LIST")
                .set(updateWishlist).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    if (wishlistModelList.size() != 0){
                        wishlistModelList.remove(index);
                        MyWishlistActivity.myWishlistAdapter.notifyDataSetChanged();
                    }
                    ProductDetailActivity.ALREADY_ADDED_TO_WISHLIST = false;
                    Toast.makeText(context, "Removed successfully", Toast.LENGTH_SHORT).show();
                }else {

                    if (ProductDetailActivity.addToWishListBtn != null) {
                        ProductDetailActivity.addToWishListBtn.setSupportImageTintList(context.getResources().getColorStateList(R.color.red));
                    }
                    wishLishList1.add(index,removedProductId);
                    String error = task.getException().getMessage();
                    Toast.makeText(context, error, Toast.LENGTH_LONG).show();
                }

                ProductDetailActivity.running_Wishlist_query = false;
            }
        });

    }

    public static void loadCartlist(final Context context, final Dialog dialog, final boolean loadProductData,
                                    final TextView badgeCount, final TextView cartTotalPriceUnit, final LinearLayout cartbottomlayout) {
        cartList.clear();
        firebaseFirestore1.collection("USERS").document(FirebaseAuth.getInstance().getUid())
                .collection("USER_DATA").document("MY_CART")
                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    for (long x = 0; x < Long.parseLong(String.valueOf(task.getResult().get("listSize"))); x++) {
                        cartList.add(task.getResult().get(x +"_product_id").toString());

                        if (DBqueriesClass.cartList.contains(ProductDetailActivity.productID)){
                            ProductDetailActivity.ALREADY_ADDED_TO_CART = true;
                        }else {
                            ProductDetailActivity.ALREADY_ADDED_TO_CART = true;
                        }

                        if (loadProductData) {
                            cartItemModelsList.clear();
                            final  String productID = task.getResult().get(x + "_product_id").toString();
                            firebaseFirestore1.collection("PRODUCTS").document(productID)
                                    .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    if (task.isSuccessful()) {
                                        int index  = 0;
                                        if (cartList.size() >=2){
                                            index = cartList.size() - 2;
                                        }
                                        cartItemModelsList.add(index ,new deliveryItemModel(
                                                deliveryItemModel.CART_ITEM,
                                                productID,
                                                task.getResult().get("product_img_1").toString(),
                                                task.getResult().get("product_title").toString(),
                                                task.getResult().get("price_Rs").toString(),
                                                (long)1,(boolean)task.getResult().get("in_stock"),
                                                (long)task.getResult().get("max_quantity"),
                                                (long)task.getResult().get("stock_quantity")));

                                        if (cartList.size()==1){
                                            cartItemModelsList.add(new deliveryItemModel(deliveryItemModel.TOTAL_AMOUNT_LAY));
                                            LinearLayout parent = (LinearLayout) cartTotalPriceUnit.getParent().getParent();
                                            parent.setVisibility(View.VISIBLE);
                                            //cartbottomlayout.setVisibility(View.VISIBLE);
                                        }
                                        if (cartList.size() == 0){
                                            cartItemModelsList.clear();
                                        }
                                        MyCartActivity.cartItemAdapter.notifyDataSetChanged();////////////////////////////////
                                    } else {
                                        String error = task.getException().getMessage();
                                        Toast.makeText(context, error, Toast.LENGTH_LONG).show();
                                    }

                                }
                            });
                        }
                    }
                    if(cartList.size()!=0){
                        badgeCount.setVisibility(View.VISIBLE);
                    }else {
                        badgeCount.setVisibility(View.INVISIBLE);
                    }
                    if (DBqueriesClass.cartList.size()<99){
                        badgeCount.setText(String.valueOf(DBqueriesClass.cartList.size()));//todo  -1 deleted ////////////////////////////////////////////////
                    }else {
                        badgeCount.setText("99");
                    }
                } else {
                    String error = task.getException().getMessage();
                    Toast.makeText(context, error, Toast.LENGTH_LONG).show();

                }
                dialog.dismiss();
            }
        });
    }

    public static void removeFromCart(final int index, final Context context, final TextView cartTotalPriceUnit) {
        final String removedProductID = cartList.get(index);
        cartList.remove(index);

        Map<String, Object> updateCart = new HashMap<>();

        for (int x = 0; x < cartList.size(); x++) {
            updateCart.put(x + "_product_id", cartList.get(x));
        }
        updateCart.put("listSize", (long) cartList.size());
        firebaseFirestore1.collection("USERS").document(FirebaseAuth.getInstance().getUid())
                .collection("USER_DATA").document("MY_CART")
                .set(updateCart).addOnCompleteListener(new OnCompleteListener<Void>() { //todo i replaced set with update
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    if (cartItemModelsList.size() != 0){
                        cartItemModelsList.remove(index);
                        MyCartActivity.cartItemAdapter.notifyDataSetChanged();//////////////////////////////
                    }
                    if (cartList.size() ==0){
                        LinearLayout parent = (LinearLayout) cartTotalPriceUnit.getParent().getParent();
                        parent.setVisibility(View.GONE);
                        cartItemModelsList.clear();
                    }

                    Toast.makeText(context, "Removed successfully", Toast.LENGTH_SHORT).show();
                }else {
                    cartList.add(index,removedProductID);
                    String error = task.getException().getMessage();
                    Toast.makeText(context, error, Toast.LENGTH_LONG).show();
                }
                ProductDetailActivity.running_Cart_query = false;
            }
        });

    }

    public static void loadAddresses( final Context context , final Dialog dialog){
        addressModelList.clear();

        firebaseFirestore1.collection("USERS").document(FirebaseAuth.getInstance().getUid())
                .collection("USER_DATA").document("MY_ADDRESSES")
                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()){

                    Intent deliveryIntent;
                    if ((long)task.getResult().get("listSize") == 0){
                        deliveryIntent = new Intent(context,AddAddressActivity.class);
                        deliveryIntent.putExtra("INTENT","deliveryIntent");
                    }else {
                        for (long x = 1; x < (long) task.getResult().get("listSize")+1; x++) {
                            addressModelList.add(new AddressModel(

                                    task.getResult().getBoolean("selected_"+x),
                                    task.getResult().getString("city_"+x),
                                    task.getResult().getString("locatity_"+x),
                                    task.getResult().getString("flat_NOorName_"+x),
                                    task.getResult().getString("pincode_"+x),
                                    task.getResult().getString("landMark_"+x),
                                    task.getResult().getString("fullName_"+x),
                                    task.getResult().getString("mobile_No_primary_"+x),
                                    task.getResult().getString("mobile_No_secondary_"+x),
                                    task.getResult().getString("state_"+x)   ));

                            if ((boolean)task.getResult().get("selected_"+x)){
                                selectedAddress = Integer.parseInt(String.valueOf(x-1));
                            }
                        }
                        deliveryIntent = new Intent(context,DeliveryActivity.class);
                    }
                    context.startActivity(deliveryIntent);
                }else {
                    String error = task.getException().getMessage();
                    Toast.makeText(context, error, Toast.LENGTH_LONG).show();
                }
                dialog.dismiss();
            }
        });
    }

    public  static void checkNotification(boolean remove, @Nullable TextView notifyCount){

        if (remove){
            registration.remove();
        }else {

            registration =   firebaseFirestore1.collection("USERS").document(FirebaseAuth.getInstance().getUid())
                    .collection("USER_DATA").document("MY_NOTIFICATION")
                    .addSnapshotListener(new EventListener<DocumentSnapshot>() {
                        @Override
                        public void onEvent(@Nullable  DocumentSnapshot documentSnapshot, @Nullable  FirebaseFirestoreException error) {
                            if (documentSnapshot != null && documentSnapshot.exists()){
                                notificationModelList.clear();
                                int unread = 0;
                                for (long x = 0; x < (long)documentSnapshot.get("listSize"); x++){
                                    notificationModelList.add(0,new NotificationModel(documentSnapshot.get("Image_"+x).toString(),
                                            documentSnapshot.get("Body_"+x).toString(),
                                            documentSnapshot.getBoolean("Readed_"+x)
                                    ));
                                    if (!documentSnapshot.getBoolean("Readed_"+x)){
                                        unread ++;
                                        if (notifyCount!=null){
                                            if (unread>0) {
                                                notifyCount.setVisibility(View.VISIBLE);
                                                if (unread < 99) {
                                                    notifyCount.setText(String.valueOf(unread));
                                                } else {
                                                    notifyCount.setText("99");
                                                }
                                            }else {
                                                notifyCount.setVisibility(View.INVISIBLE);
                                            }
                                        }

                                    }

                                }
                                if (NotificationActivity.adapter != null){
                                    NotificationActivity.adapter.notifyDataSetChanged();
                                }
                            }

                        }
                    });
        }


    }

    public static void clearData (){ ///todo###- Don't know how to use because i skip
        wishLishList1.clear();
        wishlistModelList.clear();
        myRatingNumber.clear();
        myRatedProductID.clear();
        cartList.clear();
        cartItemModelsList.clear();
        addressModelList.clear();
    }

}
