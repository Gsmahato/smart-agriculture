package com.hanuman.smartagriculture.services.products;

import static com.gitonway.lee.niftymodaldialogeffects.lib.Effectstype.SlideBottom;

import android.app.Dialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.gitonway.lee.niftymodaldialogeffects.lib.NiftyDialogBuilder;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.annotations.NotNull;
import com.hanuman.smartagriculture.models.Farmer;
import com.hanuman.smartagriculture.models.Order;
import com.hanuman.smartagriculture.models.Sale;
import com.hanuman.smartagriculture.services.order.CrudOrder;
import com.hanuman.smartagriculture.services.order.OrderDashboard;
import com.hanuman.smartagriculture.services.register.CrudFarmer;
import com.hanuman.smartagriculture.services.register.FarmerRegisterActivity;
import com.hanuman.smartagriculture.databinding.FragmentFarmerProfileBinding;
import java.util.ArrayList;

public class FarmerProfileFragment extends Fragment {
    FragmentFarmerProfileBinding binding;
    CrudFarmer crudFarmer = new CrudFarmer();
    private FirebaseAuth auth;
    DatabaseReference reference;
    FirebaseDatabase database;
    String key = null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentFarmerProfileBinding.inflate(inflater,container,false);
        View view = binding.getRoot();

        //firebase instance
        auth = FirebaseAuth.getInstance();
        reference = FirebaseDatabase.getInstance().getReference();
        database = FirebaseDatabase.getInstance();

        farmerDashboard();//farmer dashboard method call
        farmerProfile();//farmer profile method call

        // Inflate the layout for this fragment
        return view;
    }

    private void farmerProfile(){
        Glide.with(getContext()).load(auth.getCurrentUser().getPhotoUrl()).into(binding.farmerProfilePicture);
        binding.farmerUsernameTextView.setText(auth.getCurrentUser().getDisplayName());
        binding.editFarmerProfileButton.setOnClickListener(view -> {
            //set intent to edit profile page
            database.getReference("Farmer").child(auth.getCurrentUser().getUid())
                    .get().addOnSuccessListener(dataSnapshot -> {
                Farmer farmer = dataSnapshot.getValue(Farmer.class);
                Intent intent = new Intent(getContext(), FarmerRegisterActivity.class);
                intent.putExtra("EDITFARMER",farmer);
                startActivity(intent);
            }).addOnFailureListener(e -> {
                Toast.makeText(getContext(), ""+e.getMessage(), Toast.LENGTH_SHORT).show();
            });

        });
    }

    private void farmerDashboard(){
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        database.getReference("Sale").child(user.getUid()).get().addOnSuccessListener(dataSnapshot -> {
            Sale sale = dataSnapshot.getValue(Sale.class);
            binding.totalSaleTextView.setText(sale!=null ? sale.getTotalSale() +" Rs" : "0 Rs");
        }).addOnFailureListener(e -> {
            Toast.makeText(getContext(), ""+e.getMessage(), Toast.LENGTH_SHORT).show();
        });
        CrudOrder crudOrder = new CrudOrder();
        crudOrder.get(key).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ArrayList<Order> order = new ArrayList<>();
                ArrayList<Order> pendingOrder = new ArrayList<>();
                ArrayList<Order> completedOrder = new ArrayList<>();
                for (DataSnapshot data: snapshot.getChildren() ){
                    Order order1 = data.getValue(Order.class);
                    if (auth.getCurrentUser().getEmail().trim().equals(order1.getSellerEmail().trim())){
                        order1.setKey(data.getKey());
                        order.add(order1);
                        key = data.getKey();
                        binding.totalOrderTextView.setText(order.size()+"");
                    }
                    if (auth.getCurrentUser().getEmail().equals(order1.getSellerEmail()) && order1.isCompleted()){
                        order1.setKey(data.getKey());
                        completedOrder.add(order1);
                        key = data.getKey();
                        binding.completedTotalOrder.setText(completedOrder.size()+"");

                    }
                    if (auth.getCurrentUser().getEmail().equals(order1.getSellerEmail()) && !order1.isCompleted()){
                        order1.setKey(data.getKey());
                        pendingOrder.add(order1);
                        key = data.getKey();
                        binding.pendingTotalOrder.setText(pendingOrder.size()+"");
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d("TAG",error.getMessage());
            }
        });

        binding.orderDashboardButton.setOnClickListener(view -> {
            Intent intent = new Intent(getContext(), OrderDashboard.class);
            startActivity(intent);
        });

    }

}