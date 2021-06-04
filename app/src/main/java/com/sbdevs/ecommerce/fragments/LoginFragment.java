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
import com.google.firebase.firestore.FirebaseFirestore;
import com.sbdevs.ecommerce.R;
import com.sbdevs.ecommerce.activities.MainActivity2;

import static com.sbdevs.ecommerce.activities.RegisterActivity.onResetPassFrag;


public class LoginFragment extends Fragment {

    public LoginFragment() {
        // Required empty public constructor
    }
    private TextView dontHaveAccount;
    private FrameLayout parentFrameLayout;

    private EditText email;
    private EditText password;
    private Button loginBtn;
    private ImageButton closeBtn;
    private ProgressBar progressBar;
    private TextView forgotpass;

    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firebaseFirestore;
    private String emailPattern="[a-zA-Z0-9._-]+@[a-z]+.[a-z]+";

    public static boolean disableCloseBtn = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view= inflater.inflate(R.layout.fragment_login, container, false);
        dontHaveAccount = view.findViewById(R.id.dont_have_Account);
        parentFrameLayout=getActivity().findViewById(R.id.register_frameLayout);

        email = view.findViewById(R.id.login_Email);
        password = view.findViewById(R.id.login_Password);
        loginBtn = view.findViewById(R.id.login_button);
        closeBtn = view.findViewById(R.id.login_closeBtn);
        progressBar=view.findViewById(R.id.login_progressBar);
        forgotpass = view.findViewById(R.id.login_forgotPassword);

        firebaseAuth = FirebaseAuth.getInstance();
        if(disableCloseBtn){
            closeBtn.setVisibility(View.GONE);
        }else {
            closeBtn.setVisibility(View.VISIBLE);
        }
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        dontHaveAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setFragment(new SignUpFragment());
            }
        });

        forgotpass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onResetPassFrag = true;
                setFragment( new ResetPassFragment());
            }
        });

        closeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mainIntent();
            }
        });

        email.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                CheckInputs();
            }
            @Override
            public void afterTextChanged(Editable editable) {
            }
        });
        password.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                CheckInputs();
            }
            @Override
            public void afterTextChanged(Editable editable) {
            }
        });
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkEmailAndPass();
            }
        });
    }
    private void setFragment(Fragment fragment){
        FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
        fragmentTransaction.setCustomAnimations(R.anim.slidein_right,R.anim.slideout_left);
        fragmentTransaction.replace(parentFrameLayout.getId(),fragment);
        fragmentTransaction.commit();

    }
    private void CheckInputs(){
        if (!TextUtils.isEmpty(email.getText())){
            if (!TextUtils.isEmpty(password.getText())){
                loginBtn.setEnabled(true);
                loginBtn.setTextColor(getResources().getColor(R.color.white));
                //loginBtn.setBackgroundColor(R.drawable.s_shape_kind);
                
            }else {
                loginBtn.setEnabled(false);
                loginBtn.setTextColor(getResources().getColor(R.color.disAbled));
            }
        }else {
            loginBtn.setEnabled(false);
            loginBtn.setTextColor(getResources().getColor(R.color.disAbled));
        }

    }

    private void checkEmailAndPass(){
        if(email.getText().toString().matches(emailPattern)){
            if(password.length() >=8 ){

                progressBar.setVisibility(View.VISIBLE);
                loginBtn.setEnabled(false);
                loginBtn.setTextColor(getResources().getColor(R.color.disAbled));

                firebaseAuth.signInWithEmailAndPassword(email.getText().toString(),password.getText().toString())
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()){
                                    mainIntent();
                                }else {
                                    String error = task.getException().getMessage();
                                    loginBtn.setEnabled(true);
                                    loginBtn.setTextColor(getResources().getColor(R.color.white));
                                    Toast.makeText(getActivity(),error,Toast.LENGTH_LONG).show();
                                    progressBar.setVisibility(View.INVISIBLE);
                                }
                            }
                        });
            }else {
                Toast.makeText(getActivity(),"Incorrect email or password",Toast.LENGTH_LONG).show();
            }
        }else {
            email.setError("Invalid email!");
        }
    }
    private void mainIntent(){
        Intent intent=new Intent(getActivity(), MainActivity2.class);
        startActivity(intent);
        disableCloseBtn = false;
        getActivity().finish();
    }
}