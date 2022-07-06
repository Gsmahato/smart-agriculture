package com.hanuman.smartagriculture.dashboard.news;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NavUtils;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
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
import com.hanuman.smartagriculture.MainActivity;
import com.hanuman.smartagriculture.R;
import com.hanuman.smartagriculture.Utilities;
import com.hanuman.smartagriculture.adapters.NewsAdapter;
import com.hanuman.smartagriculture.databinding.ActivityViewNewsBinding;
import com.hanuman.smartagriculture.descriptions.help.HelpActivity;
import com.hanuman.smartagriculture.models.News;

import java.util.ArrayList;

public class ViewNewsActivity extends AppCompatActivity {
    ActivityViewNewsBinding binding;
    NewsAdapter adapter;
    boolean isLoading=false;
    CrudNews crud;
    String key=null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityViewNewsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        //for action bar color
        ActionBar actionBar;
        actionBar = getSupportActionBar();
        Utilities.appBarColor(actionBar,this); //action bar ends

        binding.recyclerViewViewNews.setHasFixedSize(true);
        LinearLayoutManager manager = new LinearLayoutManager(this);
        manager.setReverseLayout(true);
        manager.setStackFromEnd(true);
        binding.recyclerViewViewNews.setLayoutManager(manager);
        adapter = new NewsAdapter(this);
        adapter.notifyDataSetChanged();
        binding.recyclerViewViewNews.setAdapter(adapter);

        crud = new CrudNews();
        loadData();
        binding.recyclerViewViewNews.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull @NotNull RecyclerView recyclerView, int dx, int dy) {
                LinearLayoutManager linearLayoutManager = (LinearLayoutManager)binding.recyclerViewViewNews.getLayoutManager();
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
        binding.swipViewNews.setRefreshing(true);
        crud.get(key).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                ArrayList<News> news = new ArrayList<>();
                for(DataSnapshot data: snapshot.getChildren()){
                    News news1 = data.getValue(News.class);
                    news1.setKey(data.getKey());
                    news.add(news1);
                    key=data.getKey();
                }
                adapter.setItem(news);
                adapter.notifyDataSetChanged();
                isLoading=false;
                binding.swipViewNews.setRefreshing(false);
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {
                binding.swipViewNews.setRefreshing(false);
            }
        });
    }

    @Override
    public void onBackPressed() {
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