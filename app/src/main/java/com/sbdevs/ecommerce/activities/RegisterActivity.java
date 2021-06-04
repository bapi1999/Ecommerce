package com.sbdevs.ecommerce.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.view.KeyEvent;
import android.widget.FrameLayout;

import com.sbdevs.ecommerce.R;
import com.sbdevs.ecommerce.fragments.LoginFragment;
import com.sbdevs.ecommerce.fragments.SignUpFragment;

public class RegisterActivity extends AppCompatActivity {
    private FrameLayout frameLayout;
    public static boolean onResetPassFrag=false;
    public static boolean setSignupFragment=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        frameLayout=findViewById(R.id.register_frameLayout);
        if(setSignupFragment){
            setSignupFragment = false;
            setFragment(new SignUpFragment());
        }else {
            setFragment(new LoginFragment());
        }

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if(keyCode == event.KEYCODE_BACK){
            LoginFragment.disableCloseBtn = false;
            SignUpFragment.disableCloseBtn = false;

            if(onResetPassFrag){
                onResetPassFrag = false;
                setNewFragment(new LoginFragment());
                return false;
            }
        }
        return super.onKeyDown(keyCode, event);

    }

    private void setFragment(Fragment fragment) {
        FragmentTransaction fragmentTransaction= getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(frameLayout.getId(),fragment);
        fragmentTransaction.commit();
    }
    private void setNewFragment(Fragment fragment){
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.setCustomAnimations(R.anim.slidein_right,R.anim.slideout_left);
        fragmentTransaction.replace(frameLayout.getId(),fragment);
        fragmentTransaction.commit();

    }
}