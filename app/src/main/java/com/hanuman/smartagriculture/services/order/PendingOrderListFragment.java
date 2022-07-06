package com.hanuman.smartagriculture.services.order;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;
import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.annotations.NotNull;
import com.hanuman.smartagriculture.R;
import com.hanuman.smartagriculture.adapters.OrderAdapter;
import com.hanuman.smartagriculture.models.Order;
import com.hanuman.smartagriculture.databinding.FragmentPendingOrderListBinding;
import java.util.ArrayList;
import es.dmoral.toasty.Toasty;

public class PendingOrderListFragment extends Fragment {
    FragmentPendingOrderListBinding binding;
    private OrderAdapter adapter;
    private ArrayList<Order> list = new ArrayList<Order>();
    private boolean isLoading=false;
    private CrudOrder crud = new CrudOrder();
    String key = null;
    private FirebaseAuth auth;
    private OrderAdapter.RecyclerViewClickListener listener;
    Dialog dialog;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding=FragmentPendingOrderListBinding.inflate(inflater,container,false);
        View view = binding.getRoot();

        setOnClickListenerOrder();

        auth = FirebaseAuth.getInstance();
        binding.swipePendingOrder.setRefreshing(true);

        binding.recyclerViewOrderListPending.setHasFixedSize(true);
        LinearLayoutManager manager = new LinearLayoutManager(getContext());
        manager.setReverseLayout(true);
        manager.setStackFromEnd(true);
        binding.recyclerViewOrderListPending.setLayoutManager(manager);
        adapter = new OrderAdapter(getContext(),list,listener);
        adapter.notifyDataSetChanged();
        binding.recyclerViewOrderListPending.setAdapter(adapter);
        dialog = new Dialog(getContext());

        //gif image
        Glide.with(getContext()).load(R.drawable.no_order_gif).into(binding.noOrder);
        binding.noOrder.setVisibility(View.GONE);

        loadData();
        binding.recyclerViewOrderListPending.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull @NotNull RecyclerView recyclerView, int dx, int dy) {
                LinearLayoutManager linearLayoutManager = (LinearLayoutManager)binding.recyclerViewOrderListPending.getLayoutManager();
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

        return view;
    }
    private void loadData() {
        if(auth.getCurrentUser()!=null) {
            crud.get(key).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                    ArrayList<Order> order = new ArrayList<>();
                    for (DataSnapshot data : snapshot.getChildren()) {
                        Order order1 = data.getValue(Order.class);
                        if (order1.getBuyerEmail().equals(auth.getCurrentUser().getEmail()) && !order1.isCompleted()) {
                            order1.setKey(data.getKey());
                            order.add(order1);
                            key = data.getKey();
                            if (order.isEmpty()){
                                binding.noOrder.setVisibility(View.VISIBLE);
                                binding.swipePendingOrder.setRefreshing(false);
                            }
                        }

                        binding.swipePendingOrder.setRefreshing(false);

                    }
                    adapter.setItem(order);
                    adapter.notifyDataSetChanged();
                    isLoading = false;

                }
                @Override
                public void onCancelled(@NonNull @NotNull DatabaseError error) {
                    binding.swipePendingOrder.setRefreshing(false);
                    Toasty.error(getContext(), "" + error.getMessage(), Toast.LENGTH_SHORT, true).show();
                }
            });
        }
        else{
            binding.swipePendingOrder.setRefreshing(false);
            Toasty.warning(getContext(), "Kindly login to see your order list", Toasty.LENGTH_SHORT,true).show();
        }
    }
    public void setOnClickListenerOrder(){
        listener=(view,position)->{
            Order order = list.get(position);
            dialog.setContentView(R.layout.dialog_confirmation);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            Button yes,no;
            yes = dialog.findViewById(R.id.btn_yes_confirm);
            no = dialog.findViewById(R.id.btn_no_confirm);
            dialog.show();
            yes.setOnClickListener(view1 -> {
                crud.remove(order.getKey()).addOnSuccessListener(unused -> {
                    dialog.dismiss();
                    Intent intent = new Intent(getContext(), OrderList.class);
                    startActivity(intent);
                    Toasty.success(getContext(), "Successfully removed the order from the list!", Toasty.LENGTH_SHORT,true).show();
                }).addOnFailureListener(e -> Toasty.error(getContext(), ""+e.getMessage(), Toasty.LENGTH_SHORT,true).show());
            });
            no.setOnClickListener(view12 -> {
                dialog.dismiss();
            });
        };
    }
}