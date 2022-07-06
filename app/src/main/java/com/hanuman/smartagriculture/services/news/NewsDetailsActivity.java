package com.hanuman.smartagriculture.services.news;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Toast;

import com.hanuman.smartagriculture.Utilities;
import com.hanuman.smartagriculture.databinding.ActivityNewsDetailsBinding;

public class NewsDetailsActivity extends AppCompatActivity {
    ActivityNewsDetailsBinding binding;
    private String LINK, NEWS_SOURCE;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityNewsDetailsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        getAndSetIntentData();

        //action bar color
        ActionBar actionBar;
        actionBar = getSupportActionBar();
        getSupportActionBar().setTitle(NEWS_SOURCE);
        Utilities.appBarColor(actionBar,this); //action bar ends

    }

    private void getAndSetIntentData() {
        if(getIntent().hasExtra("NEWS_SOURCE") &&
                getIntent().hasExtra("LINK")){
            //Getting Data from Intent
            LINK = getIntent().getStringExtra("LINK");
            NEWS_SOURCE = getIntent().getStringExtra("NEWS_SOURCE");

            binding.pbarNews.setMax(100);
            WebSettings webSettings = binding.webNews.getSettings();
            webSettings.setJavaScriptEnabled(true);
            binding.webNews.clearCache(true);
            binding.webNews.clearHistory();
            binding.webNews.loadUrl(LINK);
            binding.webNews.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
            binding.pbarNews.setProgress(0);

            binding.webNews.setWebChromeClient(new WebChromeClient()
            {
                @Override
                public void onProgressChanged(WebView view, int newProgress) {
                    binding.pbarNews.setProgress(newProgress);
                    if(newProgress==100){
                        binding.pbarNews.setVisibility(View.GONE);
                    }
                    else{
                        binding.pbarNews.setVisibility(View.VISIBLE);
                    }
                    super.onProgressChanged(view, newProgress);
                }
            });

        }else{
            Toast.makeText(this, "No data.", Toast.LENGTH_SHORT).show();
        }
    }
}