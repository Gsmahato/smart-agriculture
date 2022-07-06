package com.hanuman.smartagriculture.services.order;

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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.annotations.NotNull;
import com.hanuman.smartagriculture.adapters.FarmerProfileAdapter;
import com.hanuman.smartagriculture.models.Order;
import com.hanuman.smartagriculture.databinding.FragmentCompletedOrderBinding;
import java.util.ArrayList;
import es.dmoral.toasty.Toasty;


public class CompletedOrderFragment extends Fragment {
    FragmentCompletedOrderBinding binding;
    FarmerProfileAdapter adapter;
    FarmerProfileAdapter.OnClickFarmerProfile listener;
    ArrayList<Order> list = new ArrayList<>();
    private boolean isLoading = false;
    private String key = null;
    private CrudOrder crud = new CrudOrder();
    private FirebaseAuth auth;
    private DatabaseReference database;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentCompletedOrderBinding.inflate(inflater,container,false);
        View view = binding.getRoot();

        binding.completedOrdersRv.setHasFixedSize(true);
        LinearLayoutManager manager = new LinearLayoutManager(getContext());
        manager.setReverseLayout(true);
        manager.setStackFromEnd(true);
        binding.completedOrdersRv.setLayoutManager(manager);
        adapter = new FarmerProfileAdapter(list, listener);
        adapter.notifyDataSetChanged();
        binding.completedOrdersRv.setAdapter(adapter);
        auth = FirebaseAuth.getInstance();

        loadData();
        binding.completedOrdersRv.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull @NotNull RecyclerView recyclerView, int dx, int dy) {
                LinearLayoutManager linearLayoutManager = (LinearLayoutManager) binding.completedOrdersRv.getLayoutManager();
                int totalItem = linearLayoutManager.getItemCount();
                int lastVisible = linearLayoutManager.findLastCompletelyVisibleItemPosition();
                if (totalItem < lastVisible + 3) {
                    if (!isLoading) {
                        isLoading = true;
                        loadData();
                    }

                }
            }
        });

        return view;
    }

    private void loadData() {
        binding.swipeCompletedOrders.setRefreshing(true);
        crud.get(key).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                ArrayList<Order> order = new ArrayList<>();
                for (DataSnapshot data : snapshot.getChildren()) {
                    Order order1 = data.getValue(Order.class);
                    if (auth.getCurrentUser() != null) {
                        if (order1.getSellerEmail().equals(auth.getCurrentUser().getEmail()) && order1.isCompleted()) {
                            order1.setKey(data.getKey());
                            order.add(order1);
                            key = data.getKey();
                        }
                    }
                }
                binding.swipeCompletedOrders.setRefreshing(false);
                adapter.setItem(order);
                adapter.notifyDataSetChanged();
                isLoading = false;

            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {
                binding.swipeCompletedOrders.setRefreshing(false);
                Toasty.error(getContext(), "" + error.getMessage(), Toast.LENGTH_SHORT, true).show();
            }
        });
    }

}