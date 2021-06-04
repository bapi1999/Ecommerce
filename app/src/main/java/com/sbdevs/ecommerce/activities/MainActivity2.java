package com.sbdevs.ecommerce.activities;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.sbdevs.ecommerce.R;
import com.sbdevs.ecommerce.fragments.LoginFragment;
import com.sbdevs.ecommerce.fragments.MyCartFragment;
import com.sbdevs.ecommerce.fragments.SignUpFragment;
import com.sbdevs.ecommerce.ui.home.HomeFragment;

import androidx.annotation.NonNull;
import androidx.core.view.GravityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.navigation.ui.NavigationUI;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.sbdevs.ecommerce.activities.RegisterActivity.setSignupFragment;
//import static com.sbdevs.ecommerce.ui.home.HomeFragment.currentUser;

public class  MainActivity2 extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private AppBarConfiguration mAppBarConfiguration;
    private FrameLayout frameLayout;
    private DrawerLayout drawer;
    NavigationView navigationView;

    private static final int HOME_FRAGMENT = 0;
    private static final int CART_FRAGMENT = 1;

    private static int CURRENT_FRAGMENT;
    private FirebaseUser currentUser;
    public static boolean lockMode = false;
    NavController navController;
    private TextView badgeCount;
    public static Activity mainActivity;

    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firebaseFirestore;

    private CircleImageView navProfilePic;
    private TextView navProfileName,navProfileEmail;
    private ImageView navAddImage;


//TODO 1st division = ONCreate ######################################################################################################

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        drawer = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();

        navProfilePic = navigationView.getHeaderView(0).findViewById(R.id.nav_profile_image);
        navProfileName = navigationView.getHeaderView(0).findViewById(R.id.nav_profile_name);
        navProfileEmail = navigationView.getHeaderView(0).findViewById(R.id.nav_profile_email);
        navAddImage =navigationView.getHeaderView(0).findViewById(R.id.nav_add_image);

        navigationView.setNavigationItemSelectedListener(this);
        navigationView.getMenu().getItem(0).setChecked(true);
        mAppBarConfiguration = new AppBarConfiguration.Builder(R.id.nav_home).setDrawerLayout(drawer).build();//todo- (, R.id.nav_gallery, R.id.nav_slideshow) not needed.
        //its better not to delete fagmentgallary & fragmentSlidshow

        frameLayout = findViewById(R.id.home_frameLayout);
        setFragment(new HomeFragment(), HOME_FRAGMENT);

        ConnectivityManager connectivityManager = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

        if (networkInfo != null && networkInfo.isConnected() == true) {
            navController = Navigation.findNavController(this, R.id.nav_host_fragment);
            NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        } else {
            navController = Navigation.findNavController(this, R.id.nav_host_fragment);
            NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
            NavigationUI.setupWithNavController(navigationView, navController); //todo-it must disabled==============================
        }
        //mainActivity = this;///
    }



//TODO 2nd division = ONStart ######################################################################################################

    @Override
    protected void onStart() {
        super.onStart();
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) {
            navigationView.getMenu().getItem(navigationView.getMenu().size() - 1).setEnabled(false);
        } else {



            if (DBqueriesClass.email == null) {
                firebaseFirestore.collection("USERS").document(currentUser.getUid())
                        .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DBqueriesClass.email = task.getResult().getString("email");
                            DBqueriesClass.fullname = task.getResult().getString("name");
                            DBqueriesClass.profilePic = task.getResult().getString("profilePic");

                            navProfileName.setText(DBqueriesClass.fullname);
                            navProfileEmail.setText(DBqueriesClass.email);
                            if (DBqueriesClass.profilePic.equals("")) {
                                //Glide.with(MainActivity2.this).load(R.drawable.i_user).into(navProfilePic);
                                navAddImage.setVisibility(View.VISIBLE);
                            } else {
                                Glide.with(MainActivity2.this).load(DBqueriesClass.profilePic)
                                        .apply(new RequestOptions().placeholder(R.drawable.i_user)).into(navProfilePic);
                                navAddImage.setVisibility(View.INVISIBLE);
                            }

                        } else {
                            String error = task.getException().getMessage();
                            Toast.makeText(MainActivity2.this, error, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }else {
                navProfileName.setText(DBqueriesClass.fullname);
                navProfileEmail.setText(DBqueriesClass.email);
                if (DBqueriesClass.profilePic.equals("")) {
                    //Glide.with(MainActivity2.this).load(R.drawable.i_user).into(navProfilePic);
                    navProfilePic.setImageResource(R.drawable.i_user);
                    navAddImage.setVisibility(View.VISIBLE);
                } else {
                    Glide.with(MainActivity2.this).load(DBqueriesClass.profilePic)
                            .apply(new RequestOptions().placeholder(R.drawable.i_user)).into(navProfilePic);
                    navAddImage.setVisibility(View.INVISIBLE);
                }
            }

            navigationView.getMenu().getItem(navigationView.getMenu().size() - 1).setEnabled(true);
        }
        invalidateOptionsMenu();
    }



//TODO 3st division = OnCreateOptionsMenu ############################################################################################
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (CURRENT_FRAGMENT == HOME_FRAGMENT) {
            getMenuInflater().inflate(R.menu.main_activity2, menu);

            MenuItem cartItem = menu.findItem(R.id.main_cart);
            cartItem.setActionView(R.layout.ast_badge_lay);
            ImageView badgeIcon = cartItem.getActionView().findViewById(R.id.badge_icon);
            badgeIcon.setImageResource(R.drawable.ic_shopping_cart_24);
            badgeCount = cartItem.getActionView().findViewById(R.id.badge_count);

            if (currentUser != null) {
                if (DBqueriesClass.cartList.size() == 0) {
                    badgeCount.setVisibility(View.INVISIBLE);
                    DBqueriesClass.loadCartlist(MainActivity2.this, new Dialog(MainActivity2.this), false, badgeCount,new TextView(MainActivity2.this),new LinearLayout(MainActivity2.this));
                }
                else {
                    badgeCount.setVisibility(View.VISIBLE);
                    if (DBqueriesClass.cartList.size()<99){
                        badgeCount.setText(String.valueOf(DBqueriesClass.cartList.size()));
                    }else {
                        badgeCount.setText("99");
                    }
                }
            }
            cartItem.getActionView().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (currentUser != null) {
                        Intent i = new Intent(MainActivity2.this, MyCartActivity.class);
                        mainActivity = MainActivity2.this;
                        startActivity(i);
                    } else {
                        SignInUpDialog();
                    }
                }
            });

            MenuItem notifyItem = menu.findItem(R.id.main_noti);
            notifyItem.setActionView(R.layout.ast_badge_lay);
            ImageView notifyIcon = notifyItem.getActionView().findViewById(R.id.badge_icon);
            notifyIcon.setImageResource(R.drawable.ic_notifications_none_32);
            TextView notifyCount = notifyItem.getActionView().findViewById(R.id.badge_count);

            if (currentUser !=null){
                DBqueriesClass.checkNotification(false,notifyCount);
            }
            notifyItem.getActionView().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent notificationIntent = new Intent(MainActivity2.this,NotificationActivity.class);
                    startActivity(notificationIntent);
                }
            });



        }
        return true;
    }



//TODO 3rd division = Nvigation ######################################################################################################

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    MenuItem menuItem;
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        final DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        menuItem=item;

        if (currentUser != null) {
            drawer.addDrawerListener(new DrawerLayout.SimpleDrawerListener() {
                @Override
                public void onDrawerClosed(View drawerView) {
                    super.onDrawerClosed(drawerView);
                    int id = menuItem.getItemId();
                    if (id == R.id.nav_myHome) {
                        setFragment(new HomeFragment(), HOME_FRAGMENT);

                    } else if (id == R.id.nav_myReward) {

                    } else if (id == R.id.nav_myWishlist) {
                        Intent i = new Intent(MainActivity2.this, MyWishlistActivity.class);
                        startActivity(i);
                    } else if (id == R.id.nav_myCart) {
                        Intent i = new Intent(MainActivity2.this, MyCartActivity.class);
                        startActivity(i);

                        //setFragment(new MyCartFragment(), CART_FRAGMENT);
                    } else if (id == R.id.nav_myAccount) {
                        Intent i = new Intent(MainActivity2.this, MyAccountActivity.class);
                        startActivity(i);
                    } else if (id == R.id.nav_myOrder) {
                        Intent i = new Intent(MainActivity2.this, MyOrderActivity.class);
                        startActivity(i);

                    } else if (id == R.id.nav_logOut) {
                        FirebaseAuth.getInstance().signOut();
                        DBqueriesClass.clearData();
                        Intent i1 = new Intent(MainActivity2.this, RegisterActivity.class);
                        startActivity(i1);
                        finish();
                    }
                    drawer.removeDrawerListener(this);
                }
            });

            return true;
        } else {
            SignInUpDialog();
            return false;
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.main_search) {
            Intent searchIntent = new Intent(this,SearchActivity.class);
            startActivity(searchIntent);
            return true;
        } else if (id == R.id.main_noti) {
            Intent notificationIntent = new Intent(MainActivity2.this,NotificationActivity.class);
            startActivity(notificationIntent);
            return true;
        } else if (id == R.id.main_cart) {
            if (currentUser == null) {
                SignInUpDialog();
            } else {
                Intent i = new Intent(MainActivity2.this, MyCartActivity.class);
                startActivity(i);
            }
            return true;
        }else if (id == android.R.id.home){
            mainActivity = null;
        }
        return super.onOptionsItemSelected(item);
    }



//TODO 5th division = Extra Methods ###################################################################################################

    private void setFragment(Fragment fragment, int fragmentNumber) {
        CURRENT_FRAGMENT = fragmentNumber;
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(frameLayout.getId(), fragment);
        fragmentTransaction.commit();

    }

    public void SignInUpDialog() {
        final Dialog singInDialog = new Dialog(MainActivity2.this);
        singInDialog.setContentView(R.layout.ast_sign_in_dialog);
        singInDialog.setCancelable(true);
        singInDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        Button dialogSignInBtn = singInDialog.findViewById(R.id.signInBtn);
        Button dialogSignUpBtn = singInDialog.findViewById(R.id.signUpBtn);
        final Intent registerIntent = new Intent(MainActivity2.this, RegisterActivity.class);

        dialogSignInBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LoginFragment.disableCloseBtn = true;
                singInDialog.dismiss();
                setSignupFragment = false;
                startActivity(registerIntent);
            }
        });
        dialogSignUpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SignUpFragment.disableCloseBtn = true;
                singInDialog.dismiss();
                setSignupFragment = true;
                startActivity(registerIntent);

            }
        });
        singInDialog.show();
    }

    @Override
    public void onBackPressed() {
        mainActivity =null;
        super.onBackPressed();
    }

    @Override
    protected void onPause() {
        super.onPause();
        DBqueriesClass.checkNotification(true,null);
    }
}