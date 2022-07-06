package com.hanuman.smartagriculture.services.kheti;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.os.Bundle;

import com.hanuman.smartagriculture.R;
import com.hanuman.smartagriculture.Utilities;
import com.ogaclejapan.smarttablayout.SmartTabLayout;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItemAdapter;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItems;
public class KhetiDashboard extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kheti_dashboard);

        //action bar color
        ActionBar actionBar;
        actionBar = getSupportActionBar();
        Utilities.appBarColor(actionBar,this);

        FragmentPagerItemAdapter adapter = new FragmentPagerItemAdapter(
                getSupportFragmentManager(), FragmentPagerItems.with(this)
                .add("फलफुल खेती", PhalPhulKhetiFragment.class)
                .add("जडिबुटी खेती", JadibutiKhetiFragment.class)
                .add("तरकारी खेती", TarkariKhetiFragment.class)
                .create());
        ViewPager viewPager =  findViewById(R.id.viewpagerKheti);
        viewPager.setAdapter(adapter);
        SmartTabLayout viewPagerTab =  findViewById(R.id.viewpagertabKheti);
        viewPagerTab.setViewPager(viewPager);
    }
}