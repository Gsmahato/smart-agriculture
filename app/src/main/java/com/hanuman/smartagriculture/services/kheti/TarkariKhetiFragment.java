package com.hanuman.smartagriculture.services.kheti;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.annotations.NotNull;
import com.hanuman.smartagriculture.R;
import com.hanuman.smartagriculture.adapters.InfoAdapter;
import com.hanuman.smartagriculture.adapters.KhetiAdapter;
import com.hanuman.smartagriculture.dashboard.info.CrudInfo;
import com.hanuman.smartagriculture.databinding.FragmentTarkariKhetiBinding;
import com.hanuman.smartagriculture.models.Info;

import java.util.ArrayList;

public class TarkariKhetiFragment extends Fragment {
    FragmentTarkariKhetiBinding binding;
    private KhetiAdapter adapter;
    private CrudInfo crud;
    private boolean isLoading = false;
    private String key = null;
    private KhetiAdapter.RecyclerViewClickListener listener;
    private ArrayList<Info> list = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding= FragmentTarkariKhetiBinding.inflate(inflater,container,false);

        onClickItem();
        crud = new CrudInfo();


        // Inflate the layout for this fragment
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.recyclerViewTarkari.setHasFixedSize(true);
        LinearLayoutManager manager = new LinearLayoutManager(getContext());
        manager.setReverseLayout(true);
        manager.setStackFromEnd(true);
        binding.recyclerViewTarkari.setLayoutManager(manager);
        adapter = new KhetiAdapter(getContext(),listener,list);
        binding.recyclerViewTarkari.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        loadData();
        binding.recyclerViewTarkari.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull @NotNull RecyclerView recyclerView, int dx, int dy) {
                LinearLayoutManager linearLayoutManager = (LinearLayoutManager)binding.recyclerViewTarkari.getLayoutManager();
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
        binding.swipTarkari.setRefreshing(true);
        crud.get(key).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                ArrayList<Info> info = new ArrayList<>();
                for (DataSnapshot data : snapshot.getChildren()) {
                    Info info1 = data.getValue(Info.class);
                    if (info1.getInfoType().contains("तरकारी खेती")) {
                        info1.setKey(data.getKey());
                        info.add(info1);
                        key = data.getKey();
                    }
                }
                adapter.setItem(info);
                adapter.notifyDataSetChanged();
                isLoading = false;
                binding.swipTarkari.setRefreshing(false);
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {
                binding.swipTarkari.setRefreshing(false);
            }
        });
    }

    private void onClickItem(){
        listener = (view,position)->{
            Info info = list.get(position);
            Intent intent = new Intent(getContext(),KhetiDetails.class );
            intent.putExtra("infoImage",info.getInfoImage());
            intent.putExtra("infoTitle",info.getInfoTitle() );
            intent.putExtra("infoDesc",info.getInfoDetails());
            intent.putExtra("intoType",info.getInfoType());
            startActivity(intent);
        };
    }
}