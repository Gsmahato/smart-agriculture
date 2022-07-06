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
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;

import com.google.firebase.storage.internal.Util;
import com.hanuman.smartagriculture.R;
import com.hanuman.smartagriculture.Utilities;
import com.hanuman.smartagriculture.databinding.ActivityWeatherInformationBinding;

public class WeatherInformationActivity extends AppCompatActivity {
    ActivityWeatherInformationBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityWeatherInformationBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Set App bar color
        ActionBar actionBar;
        actionBar = getSupportActionBar();
        Utilities.appBarColor(actionBar,this); //action bar ends

        binding.progressBarWeather.setMax(100);
        WebSettings webSettings = binding.webViewWeather.getSettings();
        webSettings.setJavaScriptEnabled(true);
        binding.webViewWeather.clearCache(true);
        binding.webViewWeather.clearHistory();
        binding.webViewWeather.loadUrl("http://mfd.gov.np/");
        binding.webViewWeather.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        binding.progressBarWeather.setProgress(0);

        binding.webViewWeather.setWebChromeClient(new WebChromeClient() {

            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                binding.progressBarWeather.setProgress(newProgress);
                if (newProgress == 100) {
                    binding.progressBarWeather.setVisibility(View.GONE);
                } else {
                    binding.progressBarWeather.setVisibility(View.VISIBLE);
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
                    .setMessage("This content shown below are the sole property of publisher as " +
                            "mentioned on the respective appbar above,this app(Smart Agriculture) " +
                            "doesn't claim these articles to be it's property in any manner.")
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