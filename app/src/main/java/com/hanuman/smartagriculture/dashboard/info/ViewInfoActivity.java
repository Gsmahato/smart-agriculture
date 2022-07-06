package com.hanuman.smartagriculture.dashboard.info;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NavUtils;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.SearchView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.annotations.NotNull;
import com.hanuman.smartagriculture.R;
import com.hanuman.smartagriculture.Utilities;
import com.hanuman.smartagriculture.adapters.InfoAdapter;
import com.hanuman.smartagriculture.adapters.NewsAdapter;
import com.hanuman.smartagriculture.dashboard.news.CrudNews;
import com.hanuman.smartagriculture.databinding.ActivityViewInfoBinding;
import com.hanuman.smartagriculture.models.Info;
import com.hanuman.smartagriculture.models.News;

import java.util.ArrayList;

public class ViewInfoActivity extends AppCompatActivity {
    ActivityViewInfoBinding binding;
    InfoAdapter adapter;
    boolean isLoading=false;
    CrudInfo crud;
    String key=null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityViewInfoBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        //for action bar color
        ActionBar actionBar;
        actionBar = getSupportActionBar();
        Utilities.appBarColor(actionBar,this);

        binding.recyclerViewViewInfo.setHasFixedSize(true);
        LinearLayoutManager manager = new LinearLayoutManager(this);
        manager.setReverseLayout(true);
        manager.setStackFromEnd(true);
        binding.recyclerViewViewInfo.setLayoutManager(manager);
        adapter = new InfoAdapter(this);
        adapter.notifyDataSetChanged();
        binding.recyclerViewViewInfo.setAdapter(adapter);

        crud = new CrudInfo();
        loadData();
        binding.recyclerViewViewInfo.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull @NotNull RecyclerView recyclerView, int dx, int dy) {
                LinearLayoutManager linearLayoutManager = (LinearLayoutManager)binding.recyclerViewViewInfo.getLayoutManager();
                int totalItem=linearLayoutManager.getItemCount();
                int lastVisible=linearLayoutManager.findLastCompletelyVisibleItemPosition();
                if(totalItem<lastVisible+3){
                    if(!isLoading){
                        isLoading=true;
                        loadData();
                    }

                }
            }
        });
    }
    private void loadData() {
        binding.swipViewInfo.setRefreshing(true);
        crud.get(key).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                ArrayList<Info> info = new ArrayList<>();
                for (DataSnapshot data : snapshot.getChildren()) {
                    Info info1 = data.getValue(Info.class);
                    info1.setKey(data.getKey());
                    info.add(info1);
                    key = data.getKey();
                }
                adapter.setItem(info);
                adapter.notifyDataSetChanged();
                isLoading = false;
                binding.swipViewInfo.setRefreshing(false);
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {
                binding.swipViewInfo.setRefreshing(false);
            }
        });
    }


        @Override
        public void onBackPressed () {
            NavUtils.navigateUpFromSameTask(this);
            super.onBackPressed();
        }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.filter, menu);
        MenuItem item = menu.findItem(R.id.search);
        SearchView searchView = (SearchView) item.getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                adapter.getFilter().filter(s);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                adapter.getFilter().filter(s);
                return false;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }
    }