package com.hanuman.smartagriculture.dashboard;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.hanuman.smartagriculture.LoginActivity;
import com.hanuman.smartagriculture.MainActivity;
import com.hanuman.smartagriculture.R;
import com.hanuman.smartagriculture.Utilities;
import com.hanuman.smartagriculture.dashboard.info.AddInfoActivity;
import com.hanuman.smartagriculture.dashboard.info.ViewInfoActivity;
import com.hanuman.smartagriculture.dashboard.news.AddNewsActivity;
import com.hanuman.smartagriculture.dashboard.news.ViewNewsActivity;
import com.hanuman.smartagriculture.databinding.ActivityDashboardBinding;

import es.dmoral.toasty.Toasty;

public class DashboardActivity extends AppCompatActivity {
    FirebaseAuth auth;
    ActivityDashboardBinding binding;
    Dialog dialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding= ActivityDashboardBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        //get instance
        auth = FirebaseAuth.getInstance();
        dialog = new Dialog(this);

        //action bar color
        ActionBar actionBar;
        actionBar = getSupportActionBar();
        Utilities.appBarColor(actionBar,this); //action bar ends

        binding.imgAddNews.setOnClickListener(view -> {
            Intent intent = new Intent(DashboardActivity.this, AddNewsActivity.class);
            startActivity(intent);
        });
        binding.imgViewNews.setOnClickListener(view -> {
            Intent intent = new Intent(DashboardActivity.this, ViewNewsActivity.class);
            startActivity(intent);
        });
        binding.imgViewInfo.setOnClickListener(view -> {
            Intent intent = new Intent(DashboardActivity.this, ViewInfoActivity.class);
            startActivity(intent);
        });
        binding.imgAddInfo.setOnClickListener(view -> {
            Intent intent = new Intent(DashboardActivity.this, AddInfoActivity.class);
            startActivity(intent);
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.admin_signout, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId()==R.id.nav_signout) {
            dialog.setContentView(R.layout.dialog_confirmation);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            Button yesBtn = dialog.findViewById(R.id.btn_yes_confirm);
            Button noBtn = dialog.findViewById(R.id.btn_no_confirm);
            dialog.show();
            yesBtn.setOnClickListener(view -> {
                Toasty.success(DashboardActivity.this, "Successfully Logout the admin account.", Toasty.LENGTH_SHORT,true).show();
                Intent intent = new Intent(DashboardActivity.this, LoginActivity.class);
                startActivity(intent);
                dialog.dismiss();
                auth.signOut();
            });
            noBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialog.dismiss();
                }
            });

        }
        return false;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();

    }


}