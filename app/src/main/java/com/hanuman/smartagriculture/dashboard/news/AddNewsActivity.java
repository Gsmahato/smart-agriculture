package com.hanuman.smartagriculture.dashboard.news;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Toast;
import com.google.firebase.auth.FirebaseAuth;
import com.hanuman.smartagriculture.R;
import com.hanuman.smartagriculture.Utilities;
import com.hanuman.smartagriculture.databinding.ActivityAddNewsBinding;
import com.hanuman.smartagriculture.models.News;
import java.util.HashMap;

import es.dmoral.toasty.Toasty;

public class AddNewsActivity extends AppCompatActivity {
    ActivityAddNewsBinding binding;
    FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding= ActivityAddNewsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        //get reference of CrudNews activity
        CrudNews crud = new CrudNews();

        //for dropdown buttons
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.source_list, R.layout.item_dropdown);
        adapter.setDropDownViewResource(R.layout.item_dropdown);
        binding.newsSourceEditText.setAdapter(adapter);

        //action bar color
        ActionBar actionBar;
        actionBar = getSupportActionBar();
        Utilities.appBarColor(actionBar,this);
        //get instance
        auth = FirebaseAuth.getInstance();

        //for updating the news
        News news_edit= (News) getIntent().getSerializableExtra("EDIT");
        if(news_edit!=null){
            binding.btnNewsAdd.setText(R.string.update_news);
            binding.newsLinkEditText.setText(news_edit.getNews_link());
            binding.newsSummaryEditText.setText(news_edit.getNews_summary());
            binding.newsTitleEditText.setText(news_edit.getNews_title());
            binding.newsDateEditText.setText(news_edit.getNews_date());
            binding.newsSourceEditText.setText(news_edit.getNews_source());
            ArrayAdapter<CharSequence> adapter2 = ArrayAdapter.createFromResource(this,
                    R.array.source_list, R.layout.item_dropdown);
            adapter2.setDropDownViewResource(R.layout.item_dropdown);
            binding.newsSourceEditText.setAdapter(adapter2);

        }
        else{
            binding.btnNewsAdd.setText(R.string.add_news);
        }

        //for news add button pressed
        binding.btnNewsAdd.setOnClickListener(view -> {
            News news = new News(binding.newsTitleEditText.getText().toString(),
                    binding.newsSummaryEditText.getText().toString().trim(),
                    binding.newsSourceEditText.getText().toString().trim(),
                    binding.newsLinkEditText.getText().toString(),
                    binding.newsDateEditText.getText().toString());
            if(news_edit==null) {
                crud.add(news).addOnSuccessListener(suc -> {
                    binding.newsTitleEditText.setText("");
                    binding.newsLinkEditText.setText("");
                    binding.newsSummaryEditText.setText("");
                    binding.newsDateEditText.setText("");

                    Intent intent = new Intent (getApplicationContext(), ViewNewsActivity.class);
                    startActivity(intent);

                    Toasty.success(AddNewsActivity.this, "News inserted successfully",
                            Toasty.LENGTH_SHORT,true).show();
                }).addOnFailureListener(e ->
                        Log.d("AddNewsActivity",e.getMessage()+""));
            }
            else {
                HashMap<String, Object> hashMap = new HashMap<>();
                hashMap.put("news_title", binding.newsTitleEditText.getText().toString());
                hashMap.put("news_source", binding.newsSourceEditText.getText().toString());
                hashMap.put("news_link", binding.newsLinkEditText.getText().toString());
                hashMap.put("news_summary", binding.newsSummaryEditText.getText().toString());
                hashMap.put("news_date",binding.newsDateEditText.getText().toString());

                crud.update(news_edit.getKey(), hashMap).addOnSuccessListener(suc -> {
                    Intent intent = new Intent (getApplicationContext(), ViewNewsActivity.class);
                    startActivity(intent);
                    Toasty.success(AddNewsActivity.this, "news updated successfully",
                            Toasty.LENGTH_SHORT,true).show();
                    finish();
                }).addOnFailureListener(e -> Toast.makeText
                        (AddNewsActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show());
            }
        });

    }

}