package com.sbdevs.ecommerce.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.sbdevs.ecommerce.R;
import com.sbdevs.ecommerce.activities.MainActivity2;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class SignUpFragment extends Fragment {

    public SignUpFragment() {
        // Required empty public constructor
    }

    private TextView alreadyHaveAccount;
    private FrameLayout parentFrameLayout;

    private EditText name;
    private EditText email;
    private EditText phone;

    private EditText password;
    private EditText confirm_password;
    private Button signUpButton;
    private ImageButton closeBtn;
    private ProgressBar progressBar;

    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firebaseFirestore;
    private String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+.[a-z]+";

    public static boolean disableCloseBtn = false;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_sign_up, container, false);
        alreadyHaveAccount = view.findViewById(R.id.already_have_account);
        parentFrameLayout = getActivity().findViewById(R.id.register_frameLayout);

        name = view.findViewById(R.id.signUp_Name);
        email = view.findViewById(R.id.signUp_Email);
        phone = view.findViewById(R.id.signUp_Phone);
        // address=view.findViewById(R.id.signUp_Address);
        password = view.findViewById(R.id.signUp_Password);
        confirm_password = view.findViewById(R.id.signUp_confirm_Password);
        signUpButton = view.findViewById(R.id.signUp_button);
        progressBar = view.findViewById(R.id.signUp_progressBar);
        closeBtn = view.findViewById(R.id.signUp_closebtn);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        alreadyHaveAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setFragment(new LoginFragment());
            }
        });
        closeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mainIntent();
            }
        });

        name.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                CheckInputs();////////////////////////////////////////////////////////////////
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });
        email.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                CheckInputs();///////////////////////////////////////////////////////////////
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });
        phone.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                CheckInputs();/////////////////////////////////////////////////////////////////////
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });
        /*
        address.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                CheckInputs();///////////////////////////////////////////////////////
            }
            @Override
            public void afterTextChanged(Editable editable) {
            }
        });
         */
        password.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                CheckInputs();////////////////////////////////////////////////////////////
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });
        confirm_password.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                CheckInputs();/////////////////////////////////////////////////////////////////
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkEmailAndPass();
            }
        });
    }

    private void setFragment(Fragment fragment) {
        FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
        fragmentTransaction.setCustomAnimations(R.anim.slidein_left, R.anim.slideout_right);
        fragmentTransaction.replace(parentFrameLayout.getId(), fragment);
        fragmentTransaction.commit();

    }

    private void CheckInputs() {
        if (!TextUtils.isEmpty(name.getText())) {
            if (!TextUtils.isEmpty(email.getText())) {
                if (!TextUtils.isEmpty(phone.getText())) {
                    if (!TextUtils.isEmpty(password.getText()) && password.length() >= 8) {
                        if (!TextUtils.isEmpty(confirm_password.getText())) {
                            signUpButton.setEnabled(true);
                            signUpButton.setTextColor(getResources().getColor(R.color.white));
                        } else {
                            signUpButton.setEnabled(false);
                            signUpButton.setTextColor(getResources().getColor(R.color.disAbled));
                        }
                    } else {
                        signUpButton.setEnabled(false);
                        signUpButton.setTextColor(getResources().getColor(R.color.disAbled));
                    }
                } else {
                    signUpButton.setEnabled(false);
                    signUpButton.setTextColor(getResources().getColor(R.color.disAbled));
                }
            } else {
                signUpButton.setEnabled(false);
                signUpButton.setTextColor(getResources().getColor(R.color.disAbled));
            }
        } else {
            signUpButton.setEnabled(false);
            signUpButton.setTextColor(getResources().getColor(R.color.disAbled));
        }
    }

    private void checkEmailAndPass() {
        if (email.getText().toString().matches(emailPattern)) {
            if (password.getText().toString().equals(confirm_password.getText().toString())) {

                progressBar.setVisibility(View.VISIBLE);
                signUpButton.setEnabled(false);
                signUpButton.setTextColor(getResources().getColor(R.color.disAbled));//TODO /////////////////////////////////
                firebaseAuth.createUserWithEmailAndPassword(email.getText().toString(), password.getText().toString())
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    Map<String, Object> userdata = new HashMap<>();
                                    userdata.put("name", name.getText().toString());
                                    userdata.put("email", email.getText().toString());
                                    userdata.put("phone", phone.getText().toString());
                                    userdata.put("profilePic","");//an empty string

                                    firebaseFirestore.collection("USERS").document(firebaseAuth.getUid())
                                            .set(userdata)
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()) {
                                                        CollectionReference userDataPath = firebaseFirestore.collection("USERS").document(firebaseAuth.getUid())
                                                                .collection("USER_DATA");
                                                        //MAPS
                                                        Map<String, Object> wishListSizeMap = new HashMap<>();
                                                        wishListSizeMap.put("listSize", (long) 0);

                                                        Map<String, Object> ratingsMap = new HashMap<>();
                                                        ratingsMap.put("listSize", (long) 0);

                                                        Map<String, Object> cartMap = new HashMap<>();
                                                        cartMap.put("listSize", (long) 0);

                                                        Map<String, Object> myAddressesMap = new HashMap<>();
                                                        myAddressesMap.put("listSize", (long) 0);

                                                        Map<String, Object> notificationMap = new HashMap<>();
                                                        notificationMap.put("listSize", (long) 0);

                                                        //MAPS


                                                        final List<String> documentPaths = new ArrayList<>();
                                                        documentPaths.add("MY_WISH_LIST");
                                                        documentPaths.add("MY_RATINGS");
                                                        documentPaths.add("MY_CART");
                                                        documentPaths.add("MY_ADDRESSES");
                                                        documentPaths.add("MY_NOTIFICATION");


                                                        List<Map<String, Object>> documentFields = new ArrayList<>();
                                                        documentFields.add(wishListSizeMap);
                                                        documentFields.add(ratingsMap);
                                                        documentFields.add(cartMap);
                                                        documentFields.add(myAddressesMap);
                                                        documentFields.add(notificationMap);

                                                        for (int x = 0; x < documentPaths.size(); x++) {
                                                            final int finalX = x;
                                                            userDataPath.document(documentPaths.get(x)).set(documentFields.get(x))
                                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                        @Override
                                                                        public void onComplete(@NonNull Task<Void> task) {
                                                                            if (task.isSuccessful()) {
                                                                                if (finalX == documentPaths.size() - 1) {
                                                                                    mainIntent();
                                                                                }
                                                                            } else {
                                                                                signUpButton.setEnabled(true);
                                                                                progressBar.setVisibility(View.INVISIBLE);
                                                                                signUpButton.setTextColor(getResources().getColor(R.color.white));//TODO ////////
                                                                                String error = task.getException().getMessage();
                                                                                Toast.makeText(getActivity(), error, Toast.LENGTH_LONG).show();
                                                                            }
                                                                        }
                                                                    });
                                                        }

                                                    } else {
                                                        String error = task.getException().getMessage();
                                                        Toast.makeText(getActivity(), error, Toast.LENGTH_LONG).show();
                                                    }
                                                }
                                            });
                                } else {
                                    String error = task.getException().getMessage();
                                    signUpButton.setEnabled(true);
                                    signUpButton.setTextColor(getResources().getColor(R.color.white));//TODO /////////////////////////////////
                                    Toast.makeText(getActivity(), error, Toast.LENGTH_LONG).show();
                                    progressBar.setVisibility(View.INVISIBLE);
                                }
                            }
                        });
            } else {
                confirm_password.setError("Password doesn't matched!");
            }
        } else {
            email.setError("Invalid Email!");
        }
    }

    private void mainIntent() {
        Intent intent = new Intent(getActivity(), MainActivity2.class);
        startActivity(intent);
        disableCloseBtn = false;
        getActivity().finish();
    }
}