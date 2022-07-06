package com.hanuman.smartagriculture.services.kheti;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.bumptech.glide.Glide;
import com.hanuman.smartagriculture.R;
import com.hanuman.smartagriculture.Utilities;
import com.hanuman.smartagriculture.databinding.ActivityKhetiDetailsBinding;

public class KhetiDetails extends AppCompatActivity {
    private String title, image, description, type;
    ActivityKhetiDetailsBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityKhetiDetailsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        getSetIntentData();
    }

    void getSetIntentData(){
        if (getIntent().hasExtra("infoImage") && getIntent().hasExtra("infoTitle")
                && getIntent().hasExtra("infoDesc")
                && getIntent().hasExtra("intoType")){
            title = getIntent().getStringExtra("infoTitle");
            type = getIntent().getStringExtra("intoType");
            description = getIntent().getStringExtra("infoDesc");
            image = getIntent().getStringExtra("infoImage");

            binding.khetiTitleText.setText(title);
            binding.khetiDescText.setText(description);
            Glide.with(this).load(image).into(binding.khetiImageBg);

            ActionBar actionBar;
            actionBar = getSupportActionBar();
            actionBar.setTitle(type);
            Utilities.appBarColor(actionBar,this);

        }
    }


}