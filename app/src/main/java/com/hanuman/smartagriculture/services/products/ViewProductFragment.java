package com.hanuman.smartagriculture.services.products;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import com.gitonway.lee.niftymodaldialogeffects.lib.NiftyDialogBuilder;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.annotations.NotNull;
import com.hanuman.smartagriculture.R;
import com.hanuman.smartagriculture.adapters.ProductAdapter;
import com.hanuman.smartagriculture.models.Product;
import com.hanuman.smartagriculture.databinding.FragmentViewProductBinding;
import java.util.ArrayList;
import es.dmoral.toasty.Toasty;

public class ViewProductFragment extends Fragment {
    FragmentViewProductBinding binding;
    ProductAdapter adapter;
    boolean isLoading=false;
    CrudProduct crud;
    String key=null;
    FirebaseAuth auth;
    ArrayList<Product> list = new ArrayList<>();
    private ProductAdapter.RecyclerViewLongClickListener listener;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentViewProductBinding.inflate(inflater,container,false);
        View root = binding.getRoot();

        setOnLongClickListener();
        binding.recyclerViewProduct.setHasFixedSize(true);
        GridLayoutManager manager = new GridLayoutManager(getContext(),2);
        binding.recyclerViewProduct.setLayoutManager(manager);
        adapter = new ProductAdapter(getContext(),listener,list);
        adapter.notifyDataSetChanged();
        binding.recyclerViewProduct.setAdapter(adapter);

        //get instance
        auth = FirebaseAuth.getInstance();

        crud = new CrudProduct();
        loadData();
        binding.recyclerViewProduct.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull @NotNull RecyclerView recyclerView, int dx, int dy) {
                LinearLayoutManager linearLayoutManager = (LinearLayoutManager)binding.recyclerViewProduct.getLayoutManager();
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
        binding.shimmerText.startShimmer();
        crud.get(key).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                ArrayList<Product> product = new ArrayList<>();
                for (DataSnapshot data : snapshot.getChildren()) {
                    Product product1 = data.getValue(Product.class);
                        if (auth.getCurrentUser() != null) {
                            if (product1.getUserId().equals(auth.getCurrentUser().getUid())) {
                                product1.setKey(data.getKey());
                                product.add(product1);
                                key = data.getKey();
                            }
                    }
                    binding.shimmerText.stopShimmer();
                    binding.shimmerText.setVisibility(View.GONE);

                }
                adapter.setItem(product);
                adapter.notifyDataSetChanged();
                isLoading = false;

            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {
                binding.shimmerText.stopShimmer();
                binding.shimmerText.setVisibility(View.GONE);
                Toasty.error(getContext(), "" + error.getMessage(), Toast.LENGTH_SHORT,true).show();
            }
        });
    }

    public void setItem(ArrayList<Product> product){
        list.addAll(product);
    }

    private void setOnLongClickListener() {
        listener = (view, position) -> {
            NiftyDialogBuilder dialogBuilder = NiftyDialogBuilder.getInstance(ViewProductFragment.this.getContext());
             crud = new CrudProduct();
             Product product = list.get(position);
            dialogBuilder.withTitle("ACTION YOUR PRODUCT")
                    .withMessage(R.string.del_or_ed)
                    .withIcon(R.drawable.ic_edit)
                    .withDialogColor(R.color.lightWhite)
                    .withDividerColor(Color.GREEN)
                    .withButton1Text("EDIT")
                    .withButton2Text("DELETE")
                    .isCancelableOnTouchOutside(true)
                    .setButton2Click(view13 -> {
                        NiftyDialogBuilder dialogBuilder1 = NiftyDialogBuilder.getInstance(getContext());
                        dialogBuilder1.withTitle("ARE YOU SURE ?")
                                .withMessage("You want to delete this...")
                                .withButton1Text("YES")
                                .withDividerColor(Color.RED)
                                .withIcon(R.drawable.ic_danger)
                                .withDialogColor(R.color.errorColor)
                                .withButton2Text("NO")
                                .setButton1Click(view1 -> {
                                    //product delete code here
                                            crud.remove(product.getKey()).addOnSuccessListener(suc -> {
                                                Intent intent2 = new Intent(getContext(), ProductDashboard.class);
                                                getContext().startActivity(intent2);
                                                Toast.makeText(getContext(), "Product item removed successfully",
                                                        Toast.LENGTH_SHORT).show();
                                            }).addOnFailureListener(e -> {
                                                Toast.makeText
                                                        (getContext(), "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                                            });
                                })
                                .setButton2Click(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view12) {
                                        dialogBuilder1.dismiss();
                                        dialogBuilder.dismiss();
                                    }
                                }).show();

                    }).setButton1Click(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //product edit code here...
                    dialogBuilder.dismiss();
                    Intent intent = new Intent(getContext(), ProductDashboard.class);
                    intent.putExtra("EDIT",product);
                    getContext().startActivity(intent);
                }
            }).show();
        };
    }
}