package com.hanuman.smartagriculture;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;
import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.login.LoginManager;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.hanuman.smartagriculture.descriptions.AboutActivity;
import com.hanuman.smartagriculture.descriptions.help.HelpActivity;
import com.hanuman.smartagriculture.hyperlink.DailyVegMarketActivity;
import com.hanuman.smartagriculture.services.news.NewsActivity;
import com.hanuman.smartagriculture.LoginActivity;
import com.hanuman.smartagriculture.databinding.ActivityMainBinding;
import org.jetbrains.annotations.NotNull;
import java.util.Objects;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    ActivityMainBinding binding;
    private ActionBarDrawerToggle toggle;
    private FirebaseAuth auth;
    FirebaseDatabase database;
    private FirebaseAuth.AuthStateListener authStateListener;
    private AccessTokenTracker accessTokenTracker;
    Dialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        //dialog for confirmation
        dialog= new Dialog(this);
        ActionBar actionBar;
        actionBar = getSupportActionBar();
        Utilities.appBarColor(actionBar,this);

        //get instance
        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();

        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_more,R.id.navigation_account)
                .build();

        NavController navController = Navigation.findNavController(MainActivity.this, R.id.nav_host_fragment_activity_main);
        NavigationUI.setupActionBarWithNavController(MainActivity.this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(binding.navView, navController);

//for drawer layout
        toggle = new ActionBarDrawerToggle(this, binding.drawerLayout,R.string.start,R.string.close);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        binding.navigationView.setNavigationItemSelectedListener(this);
        binding.drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        loginLogoutValidate();
        accessTokenTracker = new AccessTokenTracker() {
            @Override
            protected void onCurrentAccessTokenChanged(AccessToken oldAccessToken, AccessToken currentAccessToken) {
                if (currentAccessToken==null){
                    auth.signOut();
                }
            }
        };

    }


    @Override
    public boolean onNavigationItemSelected(@NonNull @NotNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.nav_agro_news:
                Intent intent = new Intent(MainActivity.this, NewsActivity.class);
                startActivity(intent);
                break;
            case R.id.nav_daily_vegetable_market:
                Intent intent2 = new Intent(MainActivity.this, DailyVegMarketActivity.class);
                startActivity(intent2);
                break;
            case R.id.nav_about_us:
                Intent intent3 = new Intent(MainActivity.this, AboutActivity.class);
                startActivity(intent3);
                break;
            case R.id.nav_share_app:
                Intent i = new Intent(Intent.ACTION_SEND);
                i.setType("text/plain");
                i.putExtra(Intent.EXTRA_SUBJECT, "Sharing URL");
                i.putExtra(Intent.EXTRA_TEXT, "https://www.facebook.com/smartagricultureandroid");
                startActivity(Intent.createChooser(i, "Share URL"));
                break;
            case R.id.nav_login:
                Intent intent4 = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(intent4);
                break;
            case R.id.nav_logout:
                dialog.setContentView(R.layout.dialog_confirmation);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                Button yesBtn = dialog.findViewById(R.id.btn_yes_confirm);
                Button noBtn = dialog.findViewById(R.id.btn_no_confirm);
                ImageView closeImage = dialog.findViewById(R.id.close_image);

                closeImage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                    }
                });

                dialog.show();
                yesBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        auth.signOut();
                        LoginManager.getInstance().logOut();
                        Toast.makeText(MainActivity.this, "Successfully Logout the account.", Toast.LENGTH_SHORT).show();
                        Intent intent2 = new Intent(MainActivity.this,LoginActivity.class);
                        startActivity(intent2);
                    }
                });
                noBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                    }
                });

                break;
        }
        return true;
    }

    private void loginLogoutValidate() {
        MenuItem loginText = binding.navigationView.getMenu().findItem(R.id.nav_login);
        MenuItem logoutText = binding.navigationView.getMenu().findItem(R.id.nav_logout);
        if (auth.getCurrentUser() != null) {
            loginText.setVisible(false);
            logoutText.setVisible(true);
        } else {
            loginText.setVisible(true);
            logoutText.setVisible(false);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.help_menu, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(toggle.onOptionsItemSelected(item)){
            return true;
        }
        if(item.getItemId() == R.id.nav_help){
            Intent intent = new Intent(MainActivity.this, HelpActivity.class);
            startActivity(intent);
        }
        return false;
    }


}