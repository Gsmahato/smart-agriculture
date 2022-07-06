package com.hanuman.smartagriculture.adapters;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.chip.Chip;
import com.google.firebase.database.annotations.NotNull;
import com.hanuman.smartagriculture.R;
import com.hanuman.smartagriculture.dashboard.news.AddNewsActivity;
import com.hanuman.smartagriculture.dashboard.news.CrudNews;
import com.hanuman.smartagriculture.dashboard.news.ViewNewsActivity;
import com.hanuman.smartagriculture.models.News;
import com.hanuman.smartagriculture.models.Product;

import java.util.ArrayList;

public class NewsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements Filterable {
    private final Context context;
    ArrayList<News> list = new ArrayList<>();
    CrudNews crud = new CrudNews();
    Dialog dialog;

    public NewsAdapter(Context context) {
        this.context = context;
    }
    public void setItem(ArrayList<News> news){
        list.addAll(news);
    }
    @NonNull
    @NotNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_news,parent,false);
        return new NewsVH(view);
    }
    @Override
    public void onBindViewHolder(@NonNull @NotNull RecyclerView.ViewHolder holder, int position) {
        dialog = new Dialog(context);
        NewsVH vh = (NewsVH) holder;
        News news = list.get(position);
        vh.news_title.setText(news.getNews_title());
        vh.summary.setText(news.getNews_summary());
        vh.date.setText(news.getNews_date());
        vh.news_source.setText(news.getNews_source());
        vh.menuText.setOnClickListener(v -> {
            PopupMenu popupMenu = new PopupMenu(context,vh.menuText);
            popupMenu.inflate(R.menu.task);
            popupMenu.setOnMenuItemClickListener(item ->
            {
                switch(item.getItemId()){
                    case R.id.action_edit:
                        Intent intent = new Intent(context, AddNewsActivity.class);
                        intent.putExtra("EDIT", news);
                        context.startActivity(intent);
                        break;

                    case R.id.action_remove:
                        dialog.setContentView(R.layout.dialog_confirmation);
                        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                        Button yesBtn = dialog.findViewById(R.id.btn_yes_confirm);
                        Button noBtn = dialog.findViewById(R.id.btn_no_confirm);
                        dialog.show();
                        yesBtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                crud.remove(news.getKey()).addOnSuccessListener(suc -> {
                                    Intent intent2 = new Intent(context, ViewNewsActivity.class);
                                    context.startActivity(intent2);
                                    Toast.makeText(context, "news item removed successfully",
                                            Toast.LENGTH_SHORT).show();
                                }).addOnFailureListener(e -> {
                                    Toast.makeText
                                            (context, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                                });
                            }
                        });
                        noBtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                dialog.dismiss();
                            }
                        });

                        break;

                }
                return false;
            });
            popupMenu.show();
        });

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
            ArrayList<News> filteredData = new ArrayList<>();
            if(keyword.toString().trim().isEmpty()){
                filteredData.addAll(list);
            }
            else{
                for (News obj: list){
                    if (obj.getNews_title().toLowerCase().contains(keyword.toString().toLowerCase().trim())){
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
            list.addAll((ArrayList<News>)results.values);
            notifyDataSetChanged();
        }
    };

    public static class NewsVH  extends RecyclerView.ViewHolder {
        public TextView news_title,summary,date,menuText;
        Chip news_source;
        public NewsVH(@NonNull @NotNull View itemView) {
            super(itemView);
            news_title = itemView.findViewById(R.id.news_title_text_view);
            summary = itemView.findViewById(R.id.news_summary_text_view);
            date= itemView.findViewById(R.id.news_date_text_view);
            menuText=itemView.findViewById(R.id.menuText2);
            news_source = itemView.findViewById(R.id.news_source_chip);

        }

}

}
