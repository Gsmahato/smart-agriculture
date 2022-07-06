package com.hanuman.smartagriculture.services.order;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.ViewPager;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import com.hanuman.smartagriculture.R;
import com.hanuman.smartagriculture.Utilities;
import com.ogaclejapan.smarttablayout.SmartTabLayout;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItemAdapter;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItems;

public class OrderDashboard extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_dashboard);

        // Set App bar color
        ActionBar actionBar;
        actionBar = getSupportActionBar();
        Utilities.appBarColor(actionBar, this); //action bar ends

        FragmentPagerItemAdapter adapter = new FragmentPagerItemAdapter(
                getSupportFragmentManager(), FragmentPagerItems.with(this)
                .add("PENDING", PendingOrderFragment.class)
                .add("COMPLETED", CompletedOrderFragment.class)
                .create());
        ViewPager viewPager =  findViewById(R.id.viewpagerOrder);
        viewPager.setAdapter(adapter);
        SmartTabLayout viewPagerTab =  findViewById(R.id.viewpagerTabOrder);
        viewPagerTab.setViewPager(viewPager);


    }
}