package com.sbdevs.ecommerce.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SimpleItemAnimator;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.sbdevs.ecommerce.R;
import com.sbdevs.ecommerce.adapters.AddressAdapter;
import com.sbdevs.ecommerce.models.AddressModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.sbdevs.ecommerce.activities.DeliveryActivity.SELECT_ADDRESS;
import static com.sbdevs.ecommerce.activities.MyAccountActivity.MANNAGE_ADDRESS;

public class MyAddressActivity extends AppCompatActivity {

    private RecyclerView addressRecycler;
    private static AddressAdapter addressAdapter;
    private List<AddressModel> addressList = new ArrayList<AddressModel>();
    private int MODE,previousAddress;
    private Button applyBtn;
    private LinearLayout addNewAddressBtn;
    private TextView addressSaved;
    private Dialog loadingDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_address);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        addressRecycler = findViewById(R.id.address_recycler);
        addNewAddressBtn = findViewById(R.id.addNewAddressBtn);
        applyBtn = findViewById(R.id.applyBtn);
        addressSaved = findViewById(R.id.address_saved);
        MODE = getIntent().getIntExtra("MODE",-1);
        previousAddress = DBqueriesClass.selectedAddress;

        loadingDialog = new Dialog(this);
        loadingDialog.setContentView(R.layout.ast_loading_progress_dialog);
        loadingDialog.setCancelable(false);
        loadingDialog.getWindow().setBackgroundDrawable(getDrawable(R.drawable.s_bigslider_background));
        loadingDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        loadingDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                addressSaved.setText(String.valueOf(DBqueriesClass.addressModelList.size())+" address saved");
            }
        });


        if (MODE == SELECT_ADDRESS){
            applyBtn.setVisibility(View.VISIBLE);
        }else if(MODE == MANNAGE_ADDRESS){
            applyBtn.setVisibility(View.GONE);
        }
        applyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (DBqueriesClass.selectedAddress != previousAddress){
                    final int previousAddressIndex  = previousAddress;
                    loadingDialog.show();
                    Map<String ,Object> updateSelected = new HashMap<>();
                    updateSelected.put("selected_"+String.valueOf(previousAddress+1),false);
                    updateSelected.put("selected_"+String.valueOf(DBqueriesClass.selectedAddress+1),true);

                    previousAddress = DBqueriesClass.selectedAddress;

                    FirebaseFirestore.getInstance().collection("USERS")
                            .document(FirebaseAuth.getInstance().getUid())
                            .collection("USER_DATA").document("MY_ADDRESSES")
                            .update(updateSelected).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()){
                                finish();
                            }else {
                                previousAddress = previousAddressIndex;
                                String error = task.getException().getMessage();
                                Toast.makeText(MyAddressActivity.this, error, Toast.LENGTH_SHORT).show();
                            }
                            loadingDialog.dismiss();
                        }
                    });
                }
                else {
                    finish();
                }
            }
        });

        addressAdapter = new AddressAdapter(DBqueriesClass.addressModelList,MODE,loadingDialog);
        addressRecycler.setAdapter(addressAdapter);
        ((SimpleItemAnimator)addressRecycler.getItemAnimator()).setSupportsChangeAnimations(false);
        addressAdapter.notifyDataSetChanged();

        addNewAddressBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent addAddressIntent = new Intent( MyAddressActivity.this,AddAddressActivity.class);
                if (MODE != SELECT_ADDRESS){
                    addAddressIntent.putExtra("INTENT","manage");
                }else {
                    addAddressIntent.putExtra("INTENT", "null");
                }
                startActivity(addAddressIntent);
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        addressSaved.setText(String.valueOf(DBqueriesClass.addressModelList.size())+" address saved");
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home){
            if (MODE == SELECT_ADDRESS) {
                if (DBqueriesClass.selectedAddress != previousAddress) {
                    DBqueriesClass.addressModelList.get(DBqueriesClass.selectedAddress).setSelected(false);
                    DBqueriesClass.addressModelList.get(previousAddress).setSelected(true);
                    DBqueriesClass.selectedAddress = previousAddress;
                }
            }
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public static void refreshItem(int deSelect, int seleceted){
        addressAdapter.notifyItemChanged(deSelect);
        addressAdapter.notifyItemChanged(seleceted);
    }

    @Override
    public void onBackPressed() {
        if (MODE == SELECT_ADDRESS) {
            if (DBqueriesClass.selectedAddress != previousAddress) {
                DBqueriesClass.addressModelList.get(DBqueriesClass.selectedAddress).setSelected(false);
                DBqueriesClass.addressModelList.get(previousAddress).setSelected(true);
                DBqueriesClass.selectedAddress = previousAddress;
            }
        }
        super.onBackPressed();
    }
}