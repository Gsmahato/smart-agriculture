package com.hanuman.smartagriculture.services.order;

import static com.gitonway.lee.niftymodaldialogeffects.lib.Effectstype.SlideBottom;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import com.gitonway.lee.niftymodaldialogeffects.lib.NiftyDialogBuilder;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.annotations.NotNull;
import com.hanuman.smartagriculture.R;
import com.hanuman.smartagriculture.adapters.FarmerProfileAdapter;
import com.hanuman.smartagriculture.models.Order;
import com.hanuman.smartagriculture.models.Sale;
import com.hanuman.smartagriculture.databinding.FragmentPendingOrderBinding;
import java.util.ArrayList;
import java.util.HashMap;
import es.dmoral.toasty.Toasty;

public class PendingOrderFragment extends Fragment {
    FragmentPendingOrderBinding binding;
    FarmerProfileAdapter adapter;
    FarmerProfileAdapter.OnClickFarmerProfile listener;
    ArrayList<Order> list = new ArrayList<>();
    private boolean isLoading = false;
    private String key = null;
    private CrudOrder crud = new CrudOrder();
    private FirebaseAuth auth;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentPendingOrderBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        setOnClickRadioButton();
        binding.pendingOrdersRv.setHasFixedSize(true);
        LinearLayoutManager manager = new LinearLayoutManager(getContext());
        manager.setReverseLayout(true);
        manager.setStackFromEnd(true);
        binding.pendingOrdersRv.setLayoutManager(manager);
        adapter = new FarmerProfileAdapter(list, listener);
        adapter.notifyDataSetChanged();
        binding.pendingOrdersRv.setAdapter(adapter);

        auth = FirebaseAuth.getInstance();

        loadData();
        binding.pendingOrdersRv.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull @NotNull RecyclerView recyclerView, int dx, int dy) {
                LinearLayoutManager linearLayoutManager = (LinearLayoutManager) binding.pendingOrdersRv.getLayoutManager();
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
        binding.swipePendingOrders.setRefreshing(true);
        crud.get(key).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                ArrayList<Order> order = new ArrayList<>();
                for (DataSnapshot data : snapshot.getChildren()) {
                    Order order1 = data.getValue(Order.class);
                    if (auth.getCurrentUser() != null) {
                        if (order1.getSellerEmail().equals(auth.getCurrentUser().getEmail()) && !order1.isCompleted()) {
                            order1.setKey(data.getKey());
                            order.add(order1);
                            key = data.getKey();
                        }
                    }
                }
                binding.swipePendingOrders.setRefreshing(false);
                adapter.setItem(order);
                adapter.notifyDataSetChanged();
                isLoading = false;

            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {
                binding.swipePendingOrders.setRefreshing(false);
                Toasty.error(getContext(), "" + error.getMessage(), Toast.LENGTH_SHORT, true).show();
            }
        });
    }

    public void setOnClickRadioButton() {
        listener = (view, position) -> {
            Order order = list.get(position);
            NiftyDialogBuilder dialogBuilder = NiftyDialogBuilder.getInstance(getContext());
            dialogBuilder.withTitle("ARE YOU SURE ?")
                    .withMessage("Are you sure this order is completed ? This process is cannot be undone later.")
                    .withButton1Text("COMPLETED")
                    .withButton2Text("CANCEL")
                    .withMessageColor(ContextCompat.getColor(binding.getRoot().getContext(), R.color.white))
                    .withTitleColor(ContextCompat.getColor(binding.getRoot().getContext(), R.color.white))
                    .withDuration(300)
                    .withEffect(SlideBottom)
                    .withIcon(R.drawable.ic_error_outline_white_24dp)
                    .withDialogColor(ContextCompat.getColor(binding.getRoot().getContext(), R.color.darkGreen))
                    .setButton2Click(view1 -> {
                        dialogBuilder.dismiss();
                        order.setCompleted(true);
                    })
                    .setButton1Click(view12 -> {
                        //dismiss the dialog
                        dialogBuilder.dismiss();

                        //update the isCompleted true
                        HashMap<String, Object> hashMap = new HashMap<>();
                        hashMap.put("completed", true);
                        crud.update(order.getKey(), hashMap).addOnSuccessListener(unused -> {
                            adapter.notifyDataSetChanged();
                        });

                        //to add into sale database
                        FirebaseDatabase database = FirebaseDatabase.getInstance();
                        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                        database.getReference("Sale").child(user.getUid()).get().addOnSuccessListener(dataSnapshot -> {
                            //getting data from database
                            Sale saleGet = dataSnapshot.getValue(Sale.class);
                            //sale code here
                            CrudSale saleCrud = new CrudSale();
                            int totalPrice = (!(saleGet == null) ? saleGet.getTotalSale() : 0) + Integer.parseInt(order.getOrderPrice());
                            int orders = list.size();
                            Sale sale = new Sale(totalPrice,orders);
                            saleCrud.add(sale).addOnSuccessListener(unused -> {
                                Toasty.success(getContext(), "Successfully completed the order", Toasty.LENGTH_SHORT, true).show();
                                adapter.notifyDataSetChanged();
                                Intent intent = new Intent(getContext(), OrderDashboard.class);
                                startActivity(intent);
                            }).addOnFailureListener(e -> {
                                Toasty.error(getContext(), "" + e.getMessage(), Toasty.LENGTH_SHORT, true).show();
                            });

                        }).addOnFailureListener(e -> {
                            Toast.makeText(getContext(), "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                        });

                    })
                    .show();
        };
    }
}