package com.hanuman.smartagriculture.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.hanuman.smartagriculture.R;
import com.hanuman.smartagriculture.models.News;
import com.hanuman.smartagriculture.models.Order;
import com.hanuman.smartagriculture.services.order.CrudOrder;

import java.util.ArrayList;

//farmer profile order adapter
public class FarmerProfileAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
    ArrayList<Order> list = new ArrayList<>();
    CrudOrder order = new CrudOrder();
    private FarmerProfileAdapter.OnClickFarmerProfile listener;

    public FarmerProfileAdapter(ArrayList<Order> list,OnClickFarmerProfile listener) {
        this.list = list;
        this.listener= listener;
    }
    public void setItem(ArrayList<Order> order){
        list.addAll(order);
    }


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_farmer_profile,parent,false);
        return new FarmerProfileVH(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        FarmerProfileVH vh = (FarmerProfileVH) holder;
        Order order = list.get(position);
        vh.title.setText(order.getProductTitle());
        vh.buyerEmail.setText(order.getBuyerEmail());
        vh.buyerStock.setText(order.getStockTotal());
        vh.totalBuyerPrice.setText(order.getOrderPrice() + " Rs");
        vh.buyerName.setText(order.getBuyerName());
        vh.orderCompleted.setText(!order.isCompleted() ? "NOT COMPLETED ": "COMPLETED");
        vh.orderCompleted.setChecked(order.isCompleted());
        if(order.isCompleted()){
            vh.orderCompleted.setEnabled(false);
        }

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class FarmerProfileVH extends RecyclerView.ViewHolder implements View.OnClickListener{
        TextView  title, buyerEmail, buyerName, buyerStock,totalBuyerPrice;
        RadioButton orderCompleted;
        public FarmerProfileVH(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.item_order_title);
            buyerEmail = itemView.findViewById(R.id.item_order_bemail);
            buyerName = itemView.findViewById(R.id.item_order_bname);
            buyerStock = itemView.findViewById(R.id.item_order_bstock);
            totalBuyerPrice = itemView.findViewById(R.id.item_order_price);
            orderCompleted = itemView.findViewById(R.id.item_order_done);

            orderCompleted.setOnClickListener(this);

        }

        @Override
        public void onClick(View view) {
            listener.onClick(view,getBindingAdapterPosition());
        }
    }
    public interface OnClickFarmerProfile{
        void onClick(View view, int position);
    }
}
