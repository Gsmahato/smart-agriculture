package com.hanuman.smartagriculture.ui.home;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.SearchView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;

import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.annotations.NotNull;
import com.hanuman.smartagriculture.R;
import com.hanuman.smartagriculture.adapters.ProductDetailsHomeAdapter;
import com.hanuman.smartagriculture.models.Product;
import com.hanuman.smartagriculture.services.products.CrudProduct;
import com.hanuman.smartagriculture.services.products.ProductDetailsActivity;

import com.hanuman.smartagriculture.databinding.FragmentHomeBinding;

import java.util.ArrayList;
import java.util.Objects;


public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    ProductDetailsHomeAdapter adapter;
    boolean isLoading = false;
    CrudProduct crud;
    String key = null;
    FirebaseAuth auth;
    int totalItem, currentItem, scrollOutItem;
    private ArrayList<Product> list = new ArrayList<>();
    private ProductDetailsHomeAdapter.RecyclerViewClickListener listener;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, MenuInflater inflater) {
        // TODO Add your menu entries here
        inflater.inflate(R.menu.filter, menu);
        MenuItem item = menu.findItem(R.id.search);
        SearchView searchView = (SearchView) item.getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                adapter.getFilter().filter(s);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                adapter.getFilter().filter(s);
                return false;
            }
        });
        super.onCreateOptionsMenu(menu, inflater);
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        //for display drawer layout
        ActionBar mActionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        Objects.requireNonNull(mActionBar).setDisplayHomeAsUpEnabled(true);

        setOnClickListener();
        adapter = new ProductDetailsHomeAdapter(getContext(), listener,list);
        binding.recyclerViewProductDetails.setAdapter(adapter);

        //get instance
        auth = FirebaseAuth.getInstance();

        crud = new CrudProduct();
        loadData();
        //For Progress bar
        binding.swipCircle.startAnim();
        binding.swipCircle.setViewColor(ContextCompat.getColor(getContext(), R.color.dark_grey));
        binding.swipCircle.setBarColor(ContextCompat.getColor(getContext(), R.color.light_white_back));

        binding.recyclerViewProductDetails.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull @org.jetbrains.annotations.NotNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
                    isLoading = true;
                }
            }

            @Override
            public void onScrolled(@NonNull @org.jetbrains.annotations.NotNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                GridLayoutManager manager = (GridLayoutManager) binding.recyclerViewProductDetails.getLayoutManager();
                currentItem = manager.getChildCount();
                totalItem = manager.getItemCount();
                scrollOutItem = manager.findFirstVisibleItemPosition();

                if (isLoading && (currentItem + scrollOutItem == totalItem)) {
                    isLoading = false;
                    binding.swipCircle.stopAnim();
                    binding.swipCircle.setVisibility(View.GONE);
                    loadData();
                }
            }
        });

        return root;
    }

    private void loadData() {
        binding.swipCircle.startAnim();
        crud.get(key).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                ArrayList<Product> product = new ArrayList<>();
                for (DataSnapshot data : snapshot.getChildren()) {
                    Product product1 = data.getValue(Product.class);
                    product1.setKey(data.getKey());
                    String[] product_stock_array =  product1.getProductStock().split(" ");
                    int product_stock = Integer.parseInt(product_stock_array[0]);
                    if(product_stock>0) {
                        product.add(product1);
                    }
                    key = data.getKey();
                }
                adapter.setItem(product);
                adapter.notifyDataSetChanged();
                isLoading = false;
                try {
                    binding.swipCircle.stopAnim();
                    binding.swipCircle.setVisibility(View.GONE);
                }
                catch (Exception e){
                    Log.d("HomeFragment",e.getMessage()+"");
                }

            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {
             Log.d("HomeFragment",error.getMessage()+"");
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }


    private void setOnClickListener() {
        listener = (view, position) -> {
            Product product = list.get(position);
            Intent intent = new Intent(getContext(), ProductDetailsActivity.class);
            intent.putExtra("TitleProductText", product.getProductTitle());
            intent.putExtra("ProductPriceText", product.getProductPrice());
            intent.putExtra("ProductImage", product.getProductImage());
            intent.putExtra("ProductDescText", product.getProductDescription());
            intent.putExtra("SellerProfile", product.getSellerProfile());
            intent.putExtra("SellerEmail", product.getSellerEmail());
            intent.putExtra("SellerMobile", product.getSellerMobile());
            intent.putExtra("TotalProductStock", product.getProductStock());
            intent.putExtra("ProductKey", product.getKey());
            getContext().startActivity(intent);
        };
    }
}