package com.sbdevs.ecommerce.fragments;

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
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.sbdevs.ecommerce.R;

public class ResetPassFragment extends Fragment {

    public ResetPassFragment() {
        // Required empty public constructor
    }

    private EditText registered_mail;
    private Button resetBtn;
    private TextView recoveryMsg;
    private ProgressBar progressBar;
    private TextView goback;
    private FrameLayout parentFrameLayout;
    private FirebaseAuth firebaseAuth;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_reset_pass, container, false);
        registered_mail = view.findViewById(R.id.registered_EmailAddress);
        resetBtn = view.findViewById(R.id.resetPass_button);
        recoveryMsg = view.findViewById(R.id.recovery_successMsg);
        progressBar = view.findViewById(R.id.recovery_progressBar);
        goback = view.findViewById(R.id.goback);
        parentFrameLayout = getActivity().findViewById(R.id.register_frameLayout);
        firebaseAuth = FirebaseAuth.getInstance();

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        goback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setFragment(new LoginFragment());
            }
        });

        resetBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                resetBtn.setEnabled(false);
                resetBtn.setTextColor(getResources().getColor(R.color.disAbled));
                progressBar.setVisibility(View.VISIBLE);
                firebaseAuth.sendPasswordResetEmail(registered_mail.getText().toString())
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful()){
                                    recoveryMsg.setVisibility(View.VISIBLE);
                                }else {
                                    progressBar.setVisibility(View.INVISIBLE);
                                    String error = task.getException().getMessage();
                                    recoveryMsg.setText(error);
                                    recoveryMsg.setTextColor(getResources().getColor(R.color.brikeRed));
                                    recoveryMsg.setVisibility(View.VISIBLE);
                                    Toast.makeText(getActivity(),error,Toast.LENGTH_LONG).show();
                                }
                                progressBar.setVisibility(View.INVISIBLE);
                                resetBtn.setEnabled(true);
                                resetBtn.setTextColor(getResources().getColor(R.color.white));
                            }
                        });
            }
        });

        registered_mail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                CheckInput();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

    }

    private void CheckInput(){
        if(TextUtils.isEmpty(registered_mail.getText())){
            resetBtn.setEnabled(false);
            resetBtn.setTextColor(getResources().getColor(R.color.disAbled));
        }else {
            resetBtn.setEnabled(true);
            resetBtn.setTextColor(getResources().getColor(R.color.white));
        }

    }
    private void setFragment(Fragment fragment){
        FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
        fragmentTransaction.setCustomAnimations(R.anim.slidein_right,R.anim.slideout_left);
        fragmentTransaction.replace(parentFrameLayout.getId(),fragment);
        fragmentTransaction.commit();

    }
}