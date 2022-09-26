package com.hanuman.smartagriculture.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.google.android.material.chip.Chip;
import com.hanuman.smartagriculture.R;
import com.hanuman.smartagriculture.Utilities;
import com.hanuman.smartagriculture.models.Product;

import java.util.ArrayList;

public class ProductDetailsHomeAdapter  extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements Filterable {

    private Context context;
    ArrayList<Product> list = new ArrayList<>();
    private RecyclerViewClickListener listener;

    public ProductDetailsHomeAdapter(Context context, RecyclerViewClickListener listener, ArrayList<Product> list) {
        this.context = context;
        this.listener= listener;
        this.list = list;
    }


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_product_details,parent,false);
        return new ProductDetailsVH(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ProductDetailsVH vh = (ProductDetailsVH) holder;
        Product product = list.get(position);
        Glide.with(context).load(product.getProductImage()).placeholder(R.drawable.ic_image).fitCenter().centerCrop().into(vh.productImage);
        vh.title.setText(product.getProductTitle());
        String day = Utilities.formatDay(product.getProductDate());
        String month = Utilities.formatMonth(product.getProductDate());
        String date = Utilities.formatDate(product.getProductDate());
        vh.price.setText(product.getProductPrice()+ " Rs.");
        vh.date.setText(date+" "+month+", "+ day);
    }
    public void setItem(ArrayList<Product> product){
        list.addAll(product);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    @Override
    public Filter getFilter() {
        return filter;
    }
    Filter filter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence keyword) {
            ArrayList<Product> filteredData = new ArrayList<>();
            if(keyword.toString().trim().isEmpty()){
                filteredData.addAll(list);
            }
            else{
                    for (Product obj: list){
                        if (obj.getProductTitle().toLowerCase().contains(keyword.toString().toLowerCase().trim())){
                            filteredData.add(obj);
                        }
                    }
            }
            FilterResults results = new FilterResults();
            results.values = filteredData;
            return results;
        }

        @Override
        protected void publishResults(CharSequence charSequence, FilterResults results) {
            list.clear();
            list.addAll((ArrayList<Product>)results.values);
            notifyDataSetChanged();
        }


    };

    public class ProductDetailsVH  extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView title,price;
        ImageView productImage;
        Chip date;
        public ProductDetailsVH(@NonNull @com.google.firebase.database.annotations.NotNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.productTitleDesc);
            price = itemView.findViewById(R.id.productPriceDesc);
            productImage=itemView.findViewById(R.id.productImageDesc);
            date = itemView.findViewById(R.id.productDate);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            listener.onClick(view,getBindingAdapterPosition());
        }
    }
    public interface RecyclerViewClickListener{
        void onClick(View view, int position);
    }
}
