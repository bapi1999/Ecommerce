package com.sbdevs.ecommerce.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.sbdevs.ecommerce.R;
import com.sbdevs.ecommerce.adapters.CartItemAdapter;
import com.sbdevs.ecommerce.models.deliveryItemModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class DeliveryActivity extends AppCompatActivity {

    private RecyclerView deliverytRecycler;
    public static List<deliveryItemModel> cartItemModelsList = new ArrayList<deliveryItemModel>();
    private FirebaseFirestore firebaseFirestore;

    CartItemAdapter deliveryItemAdapter;

    Button changeAddressBtn;
    public static final int SELECT_ADDRESS = 0;
    TextView totalAmount ;
    private TextView fullname, fullAdress;
    private String name, mobileNo;
    private TextView pincode;
    private Button deliveryContinueBtn;
    private Dialog loadingDialog;
    private Dialog paymentMethodDialog;
    private ImageButton paytmBtn , codBtn;

    private ConstraintLayout orderConformationLayout;
    private ImageButton continueShoppingBtn;
    private TextView orderId;
    private boolean successfulPurchase = false;
    public static boolean fromcart;
    private String order_id;
    public static boolean getQtyIds = true;
    private boolean allProductIsAvailable;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delivery);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Delivery");

        firebaseFirestore = FirebaseFirestore.getInstance();
        deliverytRecycler = findViewById(R.id.delivery_recycler);
        changeAddressBtn = findViewById(R.id.changeAdressBtn);
        totalAmount = findViewById(R.id.cartTotalPriceUnit);
        fullname = findViewById(R.id.order_details_full_name);
        fullAdress  = findViewById(R.id.order_details_address);
        pincode = findViewById(R.id.pincode);
        deliveryContinueBtn = findViewById(R.id.cartContinueBtn);

        orderConformationLayout= findViewById(R.id.OrderConformation_layout);
        continueShoppingBtn = findViewById(R.id.continue_Shopphin_Btn);
        orderId = findViewById(R.id.orderID_text);

        loadingDialog = new Dialog(DeliveryActivity.this);
        loadingDialog.setContentView(R.layout.ast_loading_progress_dialog);
        loadingDialog.setCancelable(false);
        loadingDialog.getWindow().setBackgroundDrawable(getDrawable(R.drawable.s_bigslider_background));
        loadingDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);


        paymentMethodDialog = new Dialog(DeliveryActivity.this);
        paymentMethodDialog.setContentView(R.layout.ast_payment_method_dialog);
        paymentMethodDialog.setCancelable(true);
        paymentMethodDialog.getWindow().setBackgroundDrawable(getDrawable(R.drawable.s_bigslider_background));
        paymentMethodDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        order_id = UUID.randomUUID().toString().substring(0,20);

        getQtyIds=true;

        paytmBtn = paymentMethodDialog.findViewById(R.id.paytm_btn);
        codBtn = paymentMethodDialog.findViewById(R.id.cod_btn);

        DeliveryMethod();


        orderConformationLayout.setVisibility(View.INVISIBLE);
        changeAddressBtn.setVisibility(View.VISIBLE);
        changeAddressBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getQtyIds = false;
                Intent i = new Intent(DeliveryActivity.this,MyAddressActivity.class);
                i.putExtra("MODE",SELECT_ADDRESS);
                startActivity(i);
            }
        });



        deliveryContinueBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                paymentMethodDialog.show();
            }
        });

        codBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getQtyIds = false;
                Intent otpIntent = new Intent(DeliveryActivity.this,OTPvarificationActivity.class);
                otpIntent.putExtra("mobileNo",mobileNo.substring(0,10));
                startActivity(otpIntent);
            }
        });

        paytmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            //todo- Order Conformation lyout. pore onno methoder moddhe impliment korbo////////////////////////////////////////////////
                successfulPurchase = true;
                getQtyIds = false;
                orderConformationLayout.setVisibility(View.VISIBLE);
                orderId.setText("Order ID "+order_id);
                Toast.makeText(DeliveryActivity.this, "###############", Toast.LENGTH_SHORT).show();
                paymentMethodDialog.dismiss();
                
                OrderConfrimMethod();

                if (MainActivity2.mainActivity != null){
                    MainActivity2.mainActivity.finish();
                    MainActivity2.mainActivity = null;

                }
                if (ProductDetailActivity.productDetailsActivity != null){
                    ProductDetailActivity.productDetailsActivity.finish();
                    ProductDetailActivity.productDetailsActivity = null;
                }
                continueShoppingBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        finish();
                    }
                });
                //loadingDialog.show();
            }
        });

//todo-Remove from cart. pore onno methoder moddhe impliment korbo. ////////////////////////////////////////////////

    }

    @Override
    protected void onStart() {
        super.onStart();
        if (getQtyIds){
            for (int x = 0; x<cartItemModelsList.size() - 1;x++){
                for (int y = 0; y<cartItemModelsList.get(x).getProductQty() - 1;y++){
                    final String quantityDoqumentName = UUID.randomUUID().toString().substring(0,20);
                    Map<String ,Object> timeStamp = new HashMap<>();
                    timeStamp.put("time", FieldValue.serverTimestamp());
                    final int finalX = x;
                    final int finalY = y;
                    firebaseFirestore.collection("PRODUCTS").document(cartItemModelsList.get(x).getProductID())
                            .collection("OUANTITY").document(quantityDoqumentName).set(timeStamp)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    cartItemModelsList.get(finalX).getQtyIDS().add(quantityDoqumentName);
                                    if (finalY +1 == cartItemModelsList.get(finalX).getProductQty()){
                                        firebaseFirestore.collection("PRODUCTS").document(cartItemModelsList.get(finalX).getProductID())
                                                .collection("OUANTITY").orderBy("time", Query.Direction.ASCENDING)
                                                .limit(cartItemModelsList.get(finalX).getStockQuantity()).get()
                                                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                        if (task.isSuccessful()){
                                                            List<String> serverQuantity = new ArrayList<>();
                                                            for (QueryDocumentSnapshot queryDocumentSnapshot: task.getResult()){
                                                                serverQuantity.add(queryDocumentSnapshot.getId());
                                                            }
                                                            for (String qtyIds : cartItemModelsList.get(finalX).getQtyIDS()){
                                                                if (!serverQuantity.contains(qtyIds)){
                                                                    Toast.makeText(DeliveryActivity.this, "This product is out of stock", Toast.LENGTH_SHORT).show();
                                                                    allProductIsAvailable = false;
                                                                }
                                                                if (serverQuantity.size() >= cartItemModelsList.get(finalX).getStockQuantity()){
                                                                    firebaseFirestore.collection("PRODUCTS").document(cartItemModelsList.get(finalX)
                                                                            .getProductID()).update("in_stock",false);
                                                                }
                                                            }
                                                        }else {
                                                            //error
                                                        }
                                                    }
                                                });
                                    }
                                }
                            });

                }
            }
        }else {
            getQtyIds =true;
        }



        name = DBqueriesClass.addressModelList.get(DBqueriesClass.selectedAddress).getFullName();
        mobileNo = DBqueriesClass.addressModelList.get(DBqueriesClass.selectedAddress).getMobileNO();
        String alternateMobNo = DBqueriesClass.addressModelList.get(DBqueriesClass.selectedAddress).getAlternateMobNo();
        if (alternateMobNo.equals("")) {
            fullname.setText(name + " - " + mobileNo);
        }else {
            fullname.setText(name + " - " + mobileNo+" / "+ DBqueriesClass.addressModelList.get(DBqueriesClass.selectedAddress).getAlternateMobNo());
        }

        String city = DBqueriesClass.addressModelList.get(DBqueriesClass.selectedAddress).getCity();
        String locality = DBqueriesClass.addressModelList.get(DBqueriesClass.selectedAddress).getLocality();
        String flatno_name = DBqueriesClass.addressModelList.get(DBqueriesClass.selectedAddress).getFlat_NOorName();

        String landmark = DBqueriesClass.addressModelList.get(DBqueriesClass.selectedAddress).getLandmark();
        String state = DBqueriesClass.addressModelList.get(DBqueriesClass.selectedAddress).getState();

        if(landmark.equals("")){
            fullAdress.setText(flatno_name +", " + locality +", " + city +", " + state);
        }else {
            fullAdress.setText(flatno_name +", " + locality +", " + landmark +", " + city +", " + state);
        }
        pincode.setText(DBqueriesClass.addressModelList.get(DBqueriesClass.selectedAddress).getPincode());

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home){
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPause() {
        super.onPause();
        loadingDialog.dismiss();
        if (getQtyIds){
            for (int x= 0;x<cartItemModelsList.size()-1;x++){
                if (!successfulPurchase){
                    for ( final String qtyIds: cartItemModelsList.get(x).getQtyIDS()){
                        final int finalX = x;
                        firebaseFirestore.collection("PRODUCTS").document(cartItemModelsList.get(x).getProductID())
                                .collection("OUANTITY").document(qtyIds).delete()
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                if (qtyIds.equals(cartItemModelsList.get(finalX).getQtyIDS().get(cartItemModelsList.get(finalX).getQtyIDS().size() - 1))){
                                    cartItemModelsList.get(finalX).getQtyIDS().clear();
                                    firebaseFirestore.collection("PRODUCTS").document(cartItemModelsList.get(finalX).getProductID())
                                            .collection("OUANTITY").orderBy("time", Query.Direction.ASCENDING)
                                            .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                            if (task.isSuccessful()){
                                                if (task.getResult().getDocuments().size() < cartItemModelsList.get(finalX).getStockQuantity() ){
                                                    firebaseFirestore.collection("PRODUCTS").document(cartItemModelsList.get(finalX)
                                                            .getProductID()).update("in_stock",true);
                                                }
                                            }else {
                                                //error
                                            }
                                        }
                                    });
                                }

                            }
                        });

                    }
                }else {
                    cartItemModelsList.get(x).getQtyIDS().clear();
                }
            }
        }

    }

    private void DeliveryMethod(){
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        deliverytRecycler.setLayoutManager(layoutManager);


        deliveryItemAdapter = new CartItemAdapter(cartItemModelsList,totalAmount,false);
        deliverytRecycler.setAdapter(deliveryItemAdapter);
        deliveryItemAdapter.notifyDataSetChanged();

    }

    @Override
    public void onBackPressed() {
        if (successfulPurchase){
            finish();
            return;
        }
        super.onBackPressed();
    }

    public void OrderConfrimMethod( ){
        if (fromcart){
            Map<String, Object> updateCart = new HashMap<>();
            long cartListsize = 0;
            final List<Integer> indexlist = new ArrayList<>();
            for (int x = 0; x < DBqueriesClass.cartList.size(); x++) {
                if (!cartItemModelsList.get(x).isInStock()){
                    updateCart.put(cartListsize + "_product_id", cartItemModelsList.get(x).getProductID());
                    cartListsize ++;
                }else {
                    indexlist.add(x);
                }
            }
            updateCart.put("listSize",  cartListsize);
            FirebaseFirestore.getInstance().collection("USERS").document(FirebaseAuth.getInstance().getUid())
                    .collection("USER_DATA").document("MY_CART")
                    .set(updateCart).addOnCompleteListener(new OnCompleteListener<Void>() { //todo i replaced set with update
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()){
                        for(int x= 0; x<indexlist.size();x++){
                            DBqueriesClass.cartList.remove(indexlist.get(x).intValue());
                            DBqueriesClass.cartItemModelsList.remove(indexlist.get(x).intValue());
                            DBqueriesClass.cartItemModelsList.remove(DBqueriesClass.cartItemModelsList.size() -1);
                        }
                    }else {
                        String error = task.getException().getMessage();
                        Toast.makeText(DeliveryActivity.this, error, Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    public void placedOrderDetails(String paymentMethod){
        loadingDialog.show();
        String userId = FirebaseAuth.getInstance().getUid();
        for (deliveryItemModel cartItemModel : cartItemModelsList) {
            if (cartItemModel.getType() == deliveryItemModel.CART_ITEM) {
                Map <String ,Object> orderDetails = new HashMap<>();
                orderDetails.put("Order_ID",order_id);

                orderDetails.put("Name",name);
                orderDetails.put("Product_Id",cartItemModel.getProductID());
                orderDetails.put("Product_Qty",cartItemModel.getProductQty());
                orderDetails.put("User_Id",userId);
                orderDetails.put("Price",cartItemModel.getProductPrice()); //Example. non nullable object

                if (cartItemModel.getProductPrice() !=null){ //Example. For nullable object this method applicable.
                    orderDetails.put("Discount_prise","discountprice");
                }else {
                    orderDetails.put("Discount_prise","");
                }
                orderDetails.put("Ordered_Date",FieldValue.serverTimestamp());
                orderDetails.put("Packed_Date",FieldValue.serverTimestamp());
                orderDetails.put("Shipped_Date",FieldValue.serverTimestamp());
                orderDetails.put("Delivered_Date",FieldValue.serverTimestamp());
                orderDetails.put("Canceled_Date",FieldValue.serverTimestamp());
                orderDetails.put("Payment_Method",paymentMethod);
                orderDetails.put("Order_Status","Canceled");
                orderDetails.put("Address",fullAdress.getText());
                orderDetails.put("Pincode",pincode.getText());
                orderDetails.put("Phone",mobileNo);


                firebaseFirestore.collection("ORDERS").document(order_id)
                        .collection("OrderItems").document(cartItemModel.getProductID())
                        .set(orderDetails).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (!task.isSuccessful()){
                            String error = task.getException().getMessage();
                            Toast.makeText(DeliveryActivity.this, error, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }else {
                Map<String,Object> orderDetails2 =new HashMap<>();
                orderDetails2.put("Total_items",cartItemModel.getTotalItem());
                orderDetails2.put("Total_item_Price",cartItemModel.getTotalItemPrice());
                orderDetails2.put("Total_Amount",cartItemModel.getTotalAmpount());
                orderDetails2.put("Deliverycharge",cartItemModel.getDeliveryCharge());
                firebaseFirestore.collection("ORDERS").document(order_id)
                        .set(orderDetails2).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            //
                        }else {
                            String error = task.getException().getMessage();
                            Toast.makeText(DeliveryActivity.this, error, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        }
    }

}