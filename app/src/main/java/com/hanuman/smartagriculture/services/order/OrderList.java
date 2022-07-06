package com.hanuman.smartagriculture.services.order;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import androidx.viewpager.widget.ViewPager;
import android.os.Bundle;
import com.hanuman.smartagriculture.R;
import com.hanuman.smartagriculture.Utilities;
import com.ogaclejapan.smarttablayout.SmartTabLayout;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItemAdapter;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItems;
import com.hanuman.smartagriculture.databinding.ActivityOrderListBinding;

public class OrderList extends AppCompatActivity {
    ActivityOrderListBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityOrderListBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Set App bar color
        ActionBar actionBar;
        actionBar = getSupportActionBar();
        Utilities.appBarColor(actionBar,this); //action bar ends

        FragmentPagerItemAdapter adapter = new FragmentPagerItemAdapter(
                getSupportFragmentManager(), FragmentPagerItems.with(this)
                .add("PENDING ORDER", PendingOrderListFragment.class)
                .add("COMPLETED ORDER", CompletedOrdersListFragment.class)
                .create());

        ViewPager viewPager =  findViewById(R.id.viewpagerOrderList);
        viewPager.setAdapter(adapter);
        SmartTabLayout viewPagerTab =  findViewById(R.id.viewpagerTabOrderList);
        viewPagerTab.setViewPager(viewPager);


    }
}