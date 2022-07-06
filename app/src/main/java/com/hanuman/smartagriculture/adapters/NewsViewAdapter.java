package com.hanuman.smartagriculture.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.database.annotations.NotNull;
import com.hanuman.smartagriculture.R;
import com.hanuman.smartagriculture.models.News;

import java.util.ArrayList;

public class NewsViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final Context context;
    ArrayList<News> list = new ArrayList<>();
    private RecyclerViewClickListener listener;


    public NewsViewAdapter(Context context, RecyclerViewClickListener listener,ArrayList<News> list) {
        this.context = context;
        this.listener = listener;
        this.list = list;
    }
    public void setItem(ArrayList<News> news){
        list.addAll(news);
    }
    @NonNull
    @org.jetbrains.annotations.NotNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull @org.jetbrains.annotations.NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_news_front,parent,false);
        return new NewsViewVH(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @org.jetbrains.annotations.NotNull RecyclerView.ViewHolder holder, int position) {
        NewsViewVH vh = (NewsViewVH) holder;
        News news = list.get(position);
        vh.title.setText(news.getNews_title());
        vh.details.setText(news.getNews_summary());
        vh.date.setText(news.getNews_date());
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class NewsViewVH  extends RecyclerView.ViewHolder implements View.OnClickListener{
        public TextView title,details,date;
        public NewsViewVH(@NonNull @NotNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.news_title);
            details = itemView.findViewById(R.id.news_desc);
            date= itemView.findViewById(R.id.news_date);
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
