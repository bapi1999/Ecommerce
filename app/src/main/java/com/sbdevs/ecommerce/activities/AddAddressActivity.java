package com.sbdevs.ecommerce.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.sbdevs.ecommerce.R;
import com.sbdevs.ecommerce.models.AddressModel;

import java.util.HashMap;
import java.util.Map;

public class AddAddressActivity extends AppCompatActivity {
    private Button saveBtn;
    private EditText city, localityStreet, flat_NOorName, pinCode, landmrk, fullName, mobNo, alternateMobNo;
    private Spinner stateSpinner;

    private String [] stateList;
    private String selectedState;
    private Dialog loadingDialog;

    private boolean updateAddress = false;
    private AddressModel addressModel;
    private int position;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_address);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        city = findViewById(R.id.city);
        localityStreet = findViewById(R.id.locality);
        flat_NOorName = findViewById(R.id.flatNo);
        pinCode = findViewById(R.id.pincode);
        stateSpinner = findViewById(R.id.state);/////////////
        landmrk = findViewById(R.id.landmark);
        fullName = findViewById(R.id.name);
        mobNo = findViewById(R.id.mobileNo);
        alternateMobNo = findViewById(R.id.alternateMobileNo);
        saveBtn = findViewById(R.id.saveChanges);
        stateList =getResources().getStringArray(R.array.india_states);
        loadingDialog = new Dialog(AddAddressActivity.this);
        loadingDialog.setContentView(R.layout.ast_loading_progress_dialog);
        loadingDialog.setCancelable(false);
        loadingDialog.getWindow().setBackgroundDrawable(getDrawable(R.drawable.s_bigslider_background));
        loadingDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);


        ArrayAdapter spinnerAdapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item,stateList);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        stateSpinner.setAdapter(spinnerAdapter);

        stateSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                selectedState = stateList[i];
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        if (getIntent().getStringExtra("INTENT").equals("update_Adress")){
            updateAddress = true;
            position = getIntent().getIntExtra("index",-1);
            addressModel = DBqueriesClass.addressModelList.get(position);

            city.setText(addressModel.getCity());
            localityStreet.setText(addressModel.getLocality());
            flat_NOorName.setText(addressModel.getFlat_NOorName());
            pinCode.setText(addressModel.getPincode());
            landmrk.setText(addressModel.getLandmark());
            fullName.setText(addressModel.getFullName());
            mobNo.setText(addressModel.getMobileNO());
            alternateMobNo.setText(addressModel.getAlternateMobNo());
            for (int i = 0; i<stateList.length;i++) {
                if (stateList[i].equals(addressModel.getState())){
                    stateSpinner.setSelection(i);
                }
            }
            saveBtn.setText("Update");
        }else {
            position = DBqueriesClass.addressModelList.size();
        }

        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!TextUtils.isEmpty(city.getText())){
                    if (!TextUtils.isEmpty(localityStreet.getText())){
                        if (!TextUtils.isEmpty(flat_NOorName.getText())){
                            if (!TextUtils.isEmpty(pinCode.getText()) && pinCode.getText().length() == 6){
                                if (!TextUtils.isEmpty(fullName.getText())){
                                    if (!TextUtils.isEmpty(mobNo.getText()) && mobNo.getText().length() == 10){
                                        loadingDialog.show();

                                        Map <String,Object> addAddress = new HashMap<>();

                                        addAddress.put("city_"+String.valueOf(position +1),city.getText().toString());
                                        addAddress.put("locatity_"+String.valueOf(position +1),localityStreet.getText().toString());
                                        addAddress.put("flat_NOorName_"+String.valueOf(position +1),flat_NOorName.getText().toString());
                                        addAddress.put("pincode_"+String.valueOf(position +1),pinCode.getText().toString());
                                        addAddress.put("landMark_"+String.valueOf(position +1),landmrk.getText().toString());
                                        addAddress.put("fullName_"+String.valueOf(position +1), fullName.getText().toString());
                                        addAddress.put("mobile_No_primary_"+String.valueOf(position +1),mobNo.getText().toString());
                                        addAddress.put("mobile_No_secondary_"+String.valueOf(position +1),alternateMobNo.getText().toString());
                                        addAddress.put("state_"+String.valueOf(position +1),selectedState);

                                        if (! updateAddress) {
                                            addAddress.put("listSize", DBqueriesClass.addressModelList.size() + 1);
                                            if (getIntent().getStringExtra("INTENT").equals("manage")){
                                                if (DBqueriesClass.addressModelList.size() == 0){
                                                    addAddress.put("selected_"+String.valueOf(position +1),true);
                                                }else {
                                                    addAddress.put("selected_"+String.valueOf(position +1),false);
                                                }
                                            }else {
                                                addAddress.put("selected_"+String.valueOf(position +1),true);
                                            }


                                            if (DBqueriesClass.addressModelList.size()>0){
                                                addAddress.put("selected_"+String.valueOf(DBqueriesClass.selectedAddress +1),false);
                                            }
                                        }

                                        FirebaseFirestore.getInstance().collection("USERS")
                                                .document(FirebaseAuth.getInstance().getUid())
                                                .collection("USER_DATA").document("MY_ADDRESSES")
                                                .update(addAddress).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()){
                                                    if (!updateAddress) {
                                                        if (DBqueriesClass.addressModelList.size() > 0) {
                                                            DBqueriesClass.addressModelList.get(DBqueriesClass.selectedAddress).setSelected(false);
                                                        }
                                                        DBqueriesClass.addressModelList.add(new AddressModel(true,city.getText().toString(),
                                                                localityStreet.getText().toString(), flat_NOorName.getText().toString(),
                                                                pinCode.getText().toString(),landmrk.getText().toString(),
                                                                fullName.getText().toString(),mobNo.getText().toString(),
                                                                alternateMobNo.getText().toString(),selectedState ));

                                                        if (getIntent().getStringExtra("INTENT").equals("manage")){
                                                            if (DBqueriesClass.addressModelList.size() == 0){
                                                                DBqueriesClass.selectedAddress = DBqueriesClass.addressModelList.size() - 1;
                                                            }
                                                        }else {
                                                            DBqueriesClass.selectedAddress = DBqueriesClass.addressModelList.size() - 1;
                                                        }

                                                    }else {
                                                        DBqueriesClass.addressModelList.set(position,new AddressModel(true,city.getText().toString(),
                                                                localityStreet.getText().toString(), flat_NOorName.getText().toString(),
                                                                pinCode.getText().toString(),landmrk.getText().toString(),
                                                                fullName.getText().toString(),mobNo.getText().toString(),
                                                                alternateMobNo.getText().toString(),selectedState ));
                                                    }



                                                    if (getIntent().getStringExtra("INTENT").equals("deliveryIntent")) {

                                                        Intent deliveryIntent = new Intent(AddAddressActivity.this, DeliveryActivity.class);
                                                        startActivity(deliveryIntent);
                                                    }else {
                                                        MyAddressActivity.refreshItem(DBqueriesClass.selectedAddress,DBqueriesClass.addressModelList.size() - 1);
                                                    }

                                                    finish();
                                                }else {
                                                    String error = task.getException().getMessage();
                                                    Toast.makeText(AddAddressActivity.this, error, Toast.LENGTH_LONG).show();
                                                }
                                                loadingDialog.dismiss();
                                            }
                                        });

                                    }else {
                                        mobNo.requestFocus();
                                        Toast.makeText(AddAddressActivity.this, "Please provide valid Mobile No", Toast.LENGTH_SHORT).show();
                                    }
                                }else {
                                    fullName.requestFocus();
                                }
                            }else {
                                pinCode.requestFocus();
                                Toast.makeText(AddAddressActivity.this, "Please provide valid pincode", Toast.LENGTH_SHORT).show();
                            }
                        }else {
                            flat_NOorName.requestFocus();
                        }
                    }else {
                        localityStreet.requestFocus();
                    }
                }else {
                    city.requestFocus();
                }

            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}