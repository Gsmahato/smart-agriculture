package com.hanuman.smartagriculture.services.kheti;

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
import com.hanuman.smartagriculture.adapters.KhetiAdapter;
import com.hanuman.smartagriculture.dashboard.info.CrudInfo;
import com.hanuman.smartagriculture.databinding.FragmentPhalPhulKhetiBinding;
import com.hanuman.smartagriculture.models.Info;
import java.util.ArrayList;

public class PhalPhulKhetiFragment extends Fragment {
    FragmentPhalPhulKhetiBinding binding;
    private KhetiAdapter adapter;
    private CrudInfo crud;
    private boolean isLoading = false;
    private String key = null;
    private KhetiAdapter.RecyclerViewClickListener listener;
    private DatabaseReference database;
    private ArrayList<Info> list = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentPhalPhulKhetiBinding.inflate(inflater,container,false);

        //instance
        database = FirebaseDatabase.getInstance().getReference("Info");
        //swip layout call
        onClickItem();
        binding.swipFalful.setRefreshing(true);
        //calling set adapter method
        binding.recyclerViewFalful.hasFixedSize();
        //Reverse RecyclerView
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        binding.recyclerViewFalful.setLayoutManager(linearLayoutManager);
        KhetiAdapter newsAdapter= new KhetiAdapter(getContext(),listener,list);
        binding.recyclerViewFalful.setAdapter(newsAdapter);

        database.addValueEventListener(new ValueEventListener() {
           @Override
           public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
               binding.swipFalful.setRefreshing(false);
               for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                   Info infoList = dataSnapshot.getValue(Info.class);
                   if (infoList.getInfoType().toLowerCase().contains("फलफुल खेती")){
                       list.add(infoList);
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

        // Inflate the layout for this fragment
        return binding.getRoot();
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