package com.sbdevs.ecommerce.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.sbdevs.ecommerce.R;

import de.hdodenhof.circleimageview.CircleImageView;

public class MyAccountActivity extends AppCompatActivity {

    public static final int MANNAGE_ADDRESS = 1;
    private Button viewAddressBtn;

    private CircleImageView userProfilePic;
    private TextView userName, userEmail;
    private TextView addressName,fullAddress,pincode;
    private Button signOutBtn;

    private FloatingActionButton settingsBts;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_account);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        userProfilePic = findViewById(R.id.user_profile_image);
        userEmail = findViewById(R.id.user_mail);
        userName =findViewById(R.id.user_name);
        addressName = findViewById(R.id.address_name);
        fullAddress = findViewById(R.id.address);
        pincode = findViewById(R.id.pincode);
        signOutBtn = findViewById(R.id.sign_out_btn);
        settingsBts = findViewById(R.id.settings_btn);

        userName.setText(DBqueriesClass.fullname);
        userEmail.setText(DBqueriesClass.email);
        if (! DBqueriesClass.profilePic.equals("")){
            Glide.with(MyAccountActivity.this).load(DBqueriesClass.profilePic)
                    .apply(new RequestOptions().placeholder(R.drawable.i_user)).into(userProfilePic);
        }

        if (DBqueriesClass.addressModelList.size() == 0){
            addressName.setText("No address saved");
            fullAddress.setText("---");
            pincode.setText("---");
        }else {
            LoadAddress();
        }

        viewAddressBtn = findViewById(R.id.viewAllAddressBtn);
        viewAddressBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MyAccountActivity.this,MyAddressActivity.class);
                i.putExtra("MODE",MANNAGE_ADDRESS);
                startActivity(i);
            }
        });


        signOutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();
                DBqueriesClass.clearData();
                Intent i1 = new Intent(MyAccountActivity.this, RegisterActivity.class);
                startActivity(i1);
                finish();
            }
        });

        settingsBts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent updateInfo = new Intent(MyAccountActivity.this,UpdateUserInfoActivity.class);
                updateInfo.putExtra("Name",userName.getText());
                updateInfo.putExtra("Email",userEmail.getText());
                updateInfo.putExtra("Photo",DBqueriesClass.profilePic);
                startActivity(updateInfo);
            }
        });


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
    protected void onStart() {
        super.onStart();

        userName.setText(DBqueriesClass.fullname);
        userEmail.setText(DBqueriesClass.email);
        if (! DBqueriesClass.profilePic.equals("")){
            Glide.with(MyAccountActivity.this).load(DBqueriesClass.profilePic)
                    .apply(new RequestOptions().placeholder(R.drawable.i_user)).into(userProfilePic);
        }else {
            userProfilePic.setImageResource(R.drawable.i_user);
        }

        if (DBqueriesClass.addressModelList.size() == 0){
            addressName.setText("No address saved");
            fullAddress.setText("---");
            pincode.setText("---");
        }else {
            LoadAddress();
        }
    }

    public void LoadAddress(){

        String nameText = DBqueriesClass.addressModelList.get(DBqueriesClass.selectedAddress).getFullName();
        String mobileNo = DBqueriesClass.addressModelList.get(DBqueriesClass.selectedAddress).getMobileNO();
        String alternateMobNo = DBqueriesClass.addressModelList.get(DBqueriesClass.selectedAddress).getAlternateMobNo();
        if (alternateMobNo.equals("")) {
            addressName.setText(nameText + " - " + mobileNo);
        }else {
            addressName.setText(nameText + " - " + mobileNo+" / "+ DBqueriesClass.addressModelList.get(DBqueriesClass.selectedAddress).getAlternateMobNo());
        }

        String city = DBqueriesClass.addressModelList.get(DBqueriesClass.selectedAddress).getCity();
        String locality = DBqueriesClass.addressModelList.get(DBqueriesClass.selectedAddress).getLocality();
        String flatno_name = DBqueriesClass.addressModelList.get(DBqueriesClass.selectedAddress).getFlat_NOorName();

        String landmark = DBqueriesClass.addressModelList.get(DBqueriesClass.selectedAddress).getLandmark();
        String state = DBqueriesClass.addressModelList.get(DBqueriesClass.selectedAddress).getState();

        if(landmark.equals("")){
            fullAddress.setText(flatno_name +", " + locality +", " + city +", " + state);
        }else {
            fullAddress.setText(flatno_name +", " + locality +", " + landmark +", " + city +", " + state);
        }
        pincode.setText(DBqueriesClass.addressModelList.get(DBqueriesClass.selectedAddress).getPincode());
    }
}