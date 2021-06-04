package com.sbdevs.ecommerce.adapters;

import android.app.Dialog;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.sbdevs.ecommerce.R;
import com.sbdevs.ecommerce.activities.AddAddressActivity;
import com.sbdevs.ecommerce.activities.DBqueriesClass;
import com.sbdevs.ecommerce.activities.MyAddressActivity;
import com.sbdevs.ecommerce.models.AddressModel;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.sbdevs.ecommerce.activities.DeliveryActivity.SELECT_ADDRESS;
import static com.sbdevs.ecommerce.activities.MyAccountActivity.MANNAGE_ADDRESS;
import static com.sbdevs.ecommerce.activities.MyAddressActivity.refreshItem;

public class AddressAdapter extends RecyclerView.Adapter<AddressAdapter.ViewHolder> {

    private List<AddressModel> list;
    private int MODE;
    private int preSelectedPsition;
    private boolean refresh = false;
    private Dialog loadingDialog;

    public AddressAdapter(List<AddressModel> list, int MODE,  Dialog loadingDialog) {
        this.list = list;
        this.MODE = MODE;
        preSelectedPsition = DBqueriesClass.selectedAddress;

        this.loadingDialog = loadingDialog;

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.ast_my_address_item_lay, parent, false);
        return new ViewHolder(view);



    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String city = list.get(position).getCity();
        String locality = list.get(position).getLocality();
        String flat_NOorName = list.get(position).getFlat_NOorName();
        String pincode = list.get(position).getPincode();
        String landmark = list.get(position).getLandmark();
        String name = list.get(position).getFullName();
        String mobileNo = list.get(position).getMobileNO();
        String alternateMobNo = list.get(position).getAlternateMobNo();
        String state = list.get(position).getState();
        Boolean selected = list.get(position).getSelected();

        holder.setData(selected, city, locality, flat_NOorName, pincode, landmark, name, mobileNo, alternateMobNo, state, position);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView fullname;
        private TextView address;
        private TextView pincode;
        private ImageView selectIcon;
        private LinearLayout optionContainer;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            fullname = itemView.findViewById(R.id.name);
            address = itemView.findViewById(R.id.address);
            pincode = itemView.findViewById(R.id.pincode);
            selectIcon = itemView.findViewById(R.id.applyIcon);
            optionContainer = itemView.findViewById(R.id.option_cotainer);
        }
        private void setData(Boolean selected, String city, String locality, String flatno_name, final String pincode1, String landmark, String name, String mobileNo, String alternateMobNo, String state, final int position){

            if (alternateMobNo.equals("")) {
                fullname.setText(name + " - " + mobileNo);
            }else {
                fullname.setText(name + " - " + mobileNo+" / "+ alternateMobNo);
            }
            if(landmark.equals("")){
                address.setText(flatno_name +", " + locality +", " + city +", " + state);
            }else {
                address.setText(flatno_name +", " + locality +", " + landmark +", " + city +", " + state);
            }
            pincode.setText(pincode1);

            if(MODE==SELECT_ADDRESS){
                selectIcon.setImageResource(R.drawable.ic_check);
                if (selected){
                    selectIcon.setVisibility(View.VISIBLE);
                    preSelectedPsition = position;
                }else {
                    selectIcon.setVisibility(View.GONE);
                }
                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (preSelectedPsition != position){
                            list.get(position).setSelected(true);
                            list.get(preSelectedPsition).setSelected(false);
                            refreshItem(preSelectedPsition,position);
                            preSelectedPsition = position;
                            DBqueriesClass.selectedAddress = position;
                        }

                    }
                });

            }else if (MODE == MANNAGE_ADDRESS){
                selectIcon.setVisibility(View.GONE);
                optionContainer.setVisibility(View.VISIBLE);// changed

                optionContainer.getChildAt(0).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) { // edit address
                        Intent addAddressIntent = new Intent( itemView.getContext(), AddAddressActivity.class);
                        addAddressIntent.putExtra("INTENT","update_Adress");
                        addAddressIntent.putExtra("index",position);

                        itemView.getContext().startActivity(addAddressIntent);
                        refresh = false;
                    }
                });
                optionContainer.getChildAt(1).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) { // delete address

                        loadingDialog.show();
                        Map<String ,Object>  address1 = new HashMap<>();
                        int x = 1;
                        int selected = -1;
                        for (int i = 0; i<list.size(); i++){
                            if( i!= position){
                                x++;
                                address1.put("city_"+x,list.get(i).getCity());
                                address1.put("locatity_"+x,list.get(i).getLocality());
                                address1.put("flat_NOorName_"+x,list.get(i).getFlat_NOorName());
                                address1.put("pincode_"+x,list.get(i).getPincode());
                                address1.put("landMark_"+x,list.get(i).getLandmark());
                                address1.put("fullName_"+x,list.get(i).getFullName());
                                address1.put("mobile_No_primary_"+x,list.get(i).getMobileNO());
                                address1.put("mobile_No_secondary_"+x,list.get(i).getAlternateMobNo());
                                address1.put("state_"+x,list.get(i).getState());
                                if (list.get(position).getSelected()){
                                    if(position -1 >= 0) {
                                        if( x == position) {
                                            address1.put("selected_" + x, true);
                                            selected = x;
                                        }else {
                                            address1.put("selected_"+x,list.get(i).getSelected());
                                        }
                                    }else {
                                        if (x == 1){
                                            address1.put("selected_" + x, true);
                                            selected = x;
                                        }else {
                                            address1.put("selected_"+x,list.get(i).getSelected());
                                        }
                                    }
                                }else {
                                    address1.put("selected_"+x,list.get(i).getSelected());
                                    if (list.get(i).getSelected()){
                                        selected = x;
                                    }
                                }
                            }
                        }
                        address1.put("listSize",x);
                        final int finalSelected = selected;
                        FirebaseFirestore.getInstance().collection("USERS").document(FirebaseAuth.getInstance().getUid())
                                .collection("USER_DATA").document("MY_ADDRESSES")
                                .set(address1).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()){
                                    DBqueriesClass.addressModelList.remove(position);
                                    if (finalSelected != -1) {
                                        DBqueriesClass.selectedAddress = finalSelected - 1;
                                        DBqueriesClass.addressModelList.get(finalSelected - 1).setSelected(true);
                                    }else if (DBqueriesClass.addressModelList.size() == 0){
                                        DBqueriesClass.selectedAddress = - 1;
                                    }
                                    notifyDataSetChanged();
                                }else {
                                    String  error = task.getException().getMessage();
                                    Toast.makeText(itemView.getContext(), error, Toast.LENGTH_SHORT).show();
                                }
                                loadingDialog.dismiss();
                            }

                        });
                        refresh = false;
                    }
                });
                //todo-- Not Showing===========================================
                //selectIcon.setBackgroundColor( itemView.getResources().getColor(R.color.black));// change
                selectIcon.setVisibility(View.GONE);
                selectIcon.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //optionContainer.setVisibility(View.VISIBLE);
                        if (refresh) {
                            refreshItem(preSelectedPsition, preSelectedPsition);
                        }else {
                            refresh =true;
                        }
                        preSelectedPsition = position;
                    }
                });
                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        refreshItem(preSelectedPsition,preSelectedPsition);
                        preSelectedPsition = -1;
                    }
                });

            }
        }
    }
}
