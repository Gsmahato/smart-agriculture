package com.hanuman.smartagriculture.services.news;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;
import android.os.Bundle;

import com.hanuman.smartagriculture.R;
import com.hanuman.smartagriculture.Utilities;
import com.ogaclejapan.smarttablayout.SmartTabLayout;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItemAdapter;
import com.hanuman.smartagriculture.databinding.ActivityNewsBinding;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItems;

public class NewsActivity extends AppCompatActivity {
    ActivityNewsBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityNewsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Set App bar color
        ActionBar actionBar;
        actionBar = getSupportActionBar();
        Utilities.appBarColor(actionBar, this); //action bar ends

        FragmentPagerItemAdapter adapter = new FragmentPagerItemAdapter(
                getSupportFragmentManager(), FragmentPagerItems.with(this)
                .add("All News", AllNewsFragment.class)
                .add("Kishan Aawaj", KishanAwajFragment.class)
                .add("Kishan Post", KishanPostFragment.class)
                .add("Ekantipur", EkantipurFragment.class)
                .create());
        ViewPager viewPager =  findViewById(R.id.viewpager_news);
        viewPager.setAdapter(adapter);
        SmartTabLayout viewPagerTab = findViewById(R.id.viewpager_tab_news);
        viewPagerTab.setViewPager(viewPager);

    }


}