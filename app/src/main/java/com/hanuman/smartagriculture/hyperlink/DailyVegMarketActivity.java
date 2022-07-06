package com.hanuman.smartagriculture.hyperlink;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;

import com.hanuman.smartagriculture.R;
import com.hanuman.smartagriculture.Utilities;
import com.hanuman.smartagriculture.databinding.ActivityDailyVegMarketBinding;

import org.jetbrains.annotations.NotNull;

public class DailyVegMarketActivity extends AppCompatActivity {
    ActivityDailyVegMarketBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityDailyVegMarketBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Set App bar color
        ActionBar actionBar;
        actionBar = getSupportActionBar();
        Utilities.appBarColor(actionBar,this); //action bar ends

        binding.progressBarMarket.setMax(100);
        WebSettings webSettings = binding.webViewMarket.getSettings();
        webSettings.setJavaScriptEnabled(true);
        binding.webViewMarket.clearCache(true);
        binding.webViewMarket.clearHistory();
        binding.webViewMarket.loadUrl("https://kalimatimarket.gov.np/price");
        binding.webViewMarket.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        binding.progressBarMarket.setProgress(0);

        binding.webViewMarket.setWebChromeClient(new WebChromeClient() {

            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                binding.progressBarMarket.setProgress(newProgress);
                if (newProgress == 100) {
                    binding.progressBarMarket.setVisibility(View.GONE);
                } else {
                    binding.progressBarMarket.setVisibility(View.VISIBLE);
                }
                super.onProgressChanged(view, newProgress);
            }
        });


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.disclaimer, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.nav_disclaimer) {

            new AlertDialog.Builder(this)
                    .setIcon(R.drawable.ic_info)
                    .setTitle("Content Disclaimer")
                    .setMessage("This content shown below are the sole property of publisher as mentioned on the respective appbar above,this app(Smart Agriculture) doesn't claim these articles to be it's property in any manner.")
                    .setPositiveButton("Okay", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    }).show();

        }
        return false;
    }
}