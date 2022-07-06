package com.hanuman.smartagriculture.services.news;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.annotations.NotNull;
import com.hanuman.smartagriculture.adapters.NewsViewAdapter;
import com.hanuman.smartagriculture.dashboard.news.CrudNews;
import com.hanuman.smartagriculture.models.News;
import com.hanuman.smartagriculture.adapters.NewsViewAdapter;
import com.hanuman.smartagriculture.dashboard.news.CrudNews;
import com.hanuman.smartagriculture.databinding.FragmentEkantipurBinding;
import com.hanuman.smartagriculture.models.News;
import java.util.ArrayList;

import es.dmoral.toasty.Toasty;

public class EkantipurFragment extends Fragment {
    FragmentEkantipurBinding binding;
    private NewsViewAdapter adapter;
    private boolean isLoading=false;
    private CrudNews crud;
    private String key=null;
    FirebaseAuth auth;
    private final ArrayList<News> list = new ArrayList<>();
    private NewsViewAdapter.RecyclerViewClickListener listener;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentEkantipurBinding.inflate(inflater,container,false);
        View root = binding.getRoot();

        setOnClickListener();
        binding.recyclerViewEkantipur.setHasFixedSize(true);
        LinearLayoutManager manager = new LinearLayoutManager(getContext());
        manager.setReverseLayout(true);
        manager.setStackFromEnd(true);
        binding.recyclerViewEkantipur.setLayoutManager(manager);
        adapter = new NewsViewAdapter(getContext(),listener,list);
        adapter.notifyDataSetChanged();
        binding.recyclerViewEkantipur.setAdapter(adapter);

        //get instance
        auth = FirebaseAuth.getInstance();

        crud = new CrudNews();
        loadData();
        binding.recyclerViewEkantipur.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull @NotNull RecyclerView recyclerView, int dx, int dy) {
                LinearLayoutManager linearLayoutManager = (LinearLayoutManager)binding.recyclerViewEkantipur.getLayoutManager();
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

        return root;
    }
    private void loadData() {
        binding.swip3.setRefreshing(true);
        crud.get(key).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                ArrayList<News> news = new ArrayList<>();
                for (DataSnapshot data : snapshot.getChildren()) {
                    News news1 = data.getValue(News.class);
                    if (news1.getNews_source().trim().toLowerCase().contains("ekantipur")) {
                        news1.setKey(data.getKey());
                        news.add(news1);
                        key = data.getKey();
                    }

                }
                adapter.setItem(news);
                adapter.notifyDataSetChanged();
                isLoading = false;
                binding.swip3.setRefreshing(false);


            }
            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {
                binding.swip3.setRefreshing(false);
                Toasty.error(getContext(), "" + error.getMessage(), Toast.LENGTH_SHORT,true).show();
            }
        });
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