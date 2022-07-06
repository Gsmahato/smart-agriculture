package com.hanuman.smartagriculture.services.news;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.annotations.NotNull;
import com.hanuman.smartagriculture.adapters.NewsViewAdapter;
import com.hanuman.smartagriculture.models.News;
import com.hanuman.smartagriculture.databinding.FragmentKishanPostBinding;
import java.util.ArrayList;

public class KishanPostFragment extends Fragment {
    FragmentKishanPostBinding binding;
    DatabaseReference database;
    ArrayList<News> dataholder;
    private NewsViewAdapter.RecyclerViewClickListener listener;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentKishanPostBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        //instance
        database = FirebaseDatabase.getInstance().getReference("News");
        //swip layout call
        setOnClickListener();
        binding.swip2.setRefreshing(true);
        //calling set adapter method
        binding.recyclerViewKishanPost.hasFixedSize();
        //Reverse RecyclerView
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        binding.recyclerViewKishanPost.setLayoutManager(linearLayoutManager);
        dataholder = new ArrayList<>();
        NewsViewAdapter newsAdapter= new NewsViewAdapter(getContext(),listener,dataholder);
        binding.recyclerViewKishanPost.setAdapter(newsAdapter);

        database.addValueEventListener(new ValueEventListener() {
       @Override
       public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
           binding.swip2.setRefreshing(false);
           for(DataSnapshot dataSnapshot : snapshot.getChildren()){
               News newsList = dataSnapshot.getValue(News.class);
               if (newsList.getNews_source().toLowerCase().contains("krishi post")){
                   dataholder.add(newsList);
               }
           }
           newsAdapter.notifyDataSetChanged();
       }

       @Override
       public void onCancelled(@NonNull @NotNull DatabaseError error) {
           Toast.makeText(getActivity(), "Some Error Occurred. Please try again", Toast.LENGTH_SHORT).show();
       }
   }
        );

        return root;
    }
    private void setOnClickListener() {
        listener = (view, position) -> {
            Intent intent = new Intent(getContext(), NewsDetailsActivity.class);
            intent.putExtra("NEWS_SOURCE", dataholder.get(position).getNews_source());
            intent.putExtra("LINK", dataholder.get(position).getNews_link());
            view.getContext().startActivity(intent);
        };
    }
}
