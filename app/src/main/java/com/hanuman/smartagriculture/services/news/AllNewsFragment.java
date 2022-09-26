package com.hanuman.smartagriculture.services.news;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.annotations.NotNull;
import com.hanuman.smartagriculture.adapters.NewsViewAdapter;
import com.hanuman.smartagriculture.dashboard.news.CrudNews;
import com.hanuman.smartagriculture.models.News;
import com.hanuman.smartagriculture.databinding.FragmentAllNewsBinding;
import java.util.ArrayList;

public class AllNewsFragment extends Fragment {
    FragmentAllNewsBinding binding;
    private NewsViewAdapter.RecyclerViewClickListener listener;
    private NewsViewAdapter adapter;
    private boolean isLoading=false;
    private CrudNews crud;
    private String key=null;
    FirebaseAuth auth;
    DatabaseReference database;
    private final ArrayList<News> list = new ArrayList<>();


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentAllNewsBinding.inflate(inflater,container,false);
        View view = binding.getRoot();

        //instance
        database = FirebaseDatabase.getInstance().getReference("News");
        //swip layout call
        setOnClickListener();
        binding.swipAllNews.setRefreshing(true);
        //calling set adapter method
        binding.recyclerViewAllNews.hasFixedSize();
        //Reverse RecyclerView
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        binding.recyclerViewAllNews.setLayoutManager(linearLayoutManager);
        NewsViewAdapter newsAdapter= new NewsViewAdapter(getContext(),listener,list);
        binding.recyclerViewAllNews.setAdapter(newsAdapter);
        //get instance
        auth = FirebaseAuth.getInstance();

        database.addValueEventListener(new ValueEventListener() {
       @Override
       public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
           binding.swipAllNews.setRefreshing(false);
           for(DataSnapshot dataSnapshot : snapshot.getChildren()){
               News newsList = dataSnapshot.getValue(News.class);
               list.add(newsList);

           }
           newsAdapter.notifyDataSetChanged();
       }

       @Override
       public void onCancelled(@NonNull @NotNull DatabaseError error) {
           Log.d("AllNewsFragment",error.getMessage()+"");
       }});

        return view;
    }
    private void setOnClickListener(){
        listener=(view,position)->{
            Intent intent = new Intent(getContext(),NewsDetailsActivity.class);
            intent.putExtra("NEWS_SOURCE", list.get(position).getNews_source());
            intent.putExtra("LINK", list.get(position).getNews_link());
            startActivity(intent);
        };
    }
}