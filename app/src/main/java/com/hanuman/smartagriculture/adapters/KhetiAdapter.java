package com.hanuman.smartagriculture.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.chip.Chip;
import com.hanuman.smartagriculture.R;
import com.hanuman.smartagriculture.dashboard.info.CrudInfo;
import com.hanuman.smartagriculture.models.Info;
import java.util.ArrayList;

public class KhetiAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
    private Context context;
    ArrayList<Info> list = new ArrayList<>();
    private RecyclerViewClickListener listener;


    public KhetiAdapter(Context context, RecyclerViewClickListener listener, ArrayList<Info> list) {
        this.context = context;
        this.listener = listener;
        this.list = list;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_kheti,parent,false);
        return new KhetiVH(view);
    }
    public void setItem(ArrayList<Info> info){
        list.addAll(info);
    }



    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        KhetiVH vh = (KhetiVH) holder;
        Info info = list.get(position);
        vh.title.setText(info.getInfoTitle());
        Glide.with(context).load(info.getInfoImage()).into(vh.image);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class KhetiVH  extends RecyclerView.ViewHolder implements View.OnClickListener{
        public ImageView image;
        Chip title;
        public KhetiVH(@NonNull @com.google.firebase.database.annotations.NotNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.kheti_title);
            image = itemView.findViewById(R.id.kheti_image);
            itemView.setOnClickListener(this);

        }

        @Override
        public void onClick(View view) {
            listener.onClick(view, getBindingAdapterPosition());
        }
    }
    public interface RecyclerViewClickListener{
        void onClick(View view, int position);
    }
}
