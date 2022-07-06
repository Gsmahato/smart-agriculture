package com.hanuman.smartagriculture.ui.more;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.hanuman.smartagriculture.R;
import com.hanuman.smartagriculture.hyperlink.DailyVegMarketActivity;
import com.hanuman.smartagriculture.hyperlink.WeatherInformationActivity;
import com.hanuman.smartagriculture.models.Farmer;
import com.hanuman.smartagriculture.services.kheti.KhetiDashboard;
import com.hanuman.smartagriculture.services.news.NewsActivity;
import com.hanuman.smartagriculture.services.products.ProductDashboard;
import com.hanuman.smartagriculture.services.register.FarmerRegisterActivity;
import com.hanuman.smartagriculture.databinding.FragmentMoreBinding;

import java.util.Objects;

public class MoreFragment extends Fragment {
    FirebaseDatabase database;
    FirebaseAuth auth;

    private FragmentMoreBinding binding;
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentMoreBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        //for display drawer layout
        ActionBar mActionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        Objects.requireNonNull(mActionBar).setDisplayHomeAsUpEnabled(true);

        //invisible buttons
        binding.khetiTarikaBtn2.setVisibility(View.INVISIBLE);
        binding.khetiTarikaBtn3.setVisibility(View.INVISIBLE);

        binding.khetiTarikaBtn.setOnClickListener(view -> {
            Intent intent = new Intent(getContext(), KhetiDashboard.class);
            startActivity(intent);
        });


        binding.krishiNewsImgBtn.setOnClickListener(view -> {
            Intent intent = new Intent(getActivity(), NewsActivity.class);
            startActivity(intent);
        });
        binding.kalimatiVegMarketImgBtn.setOnClickListener(view -> {
            Intent intent = new Intent(getActivity(), DailyVegMarketActivity.class);
            startActivity(intent);
        });
        binding.weatherInfoImgBtn.setOnClickListener(view -> {
            Intent intent = new Intent(getActivity(), WeatherInformationActivity.class);
            startActivity(intent);
        });
        binding.imgRegisterBtn.setOnClickListener(view -> {
            Intent intent = new Intent(getActivity(), FarmerRegisterActivity.class);
            startActivity(intent);
        });
        binding.productDashboardBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), ProductDashboard.class);
                startActivity(intent);
            }
        });

        //to get farmer database
        database = FirebaseDatabase.getInstance();
        auth = FirebaseAuth.getInstance();
        binding.productDashboardBtn.setVisibility(View.GONE);
        binding.registerAsFarmerDesc.setVisibility(View.GONE);
        binding.imgRegisterBtn.setVisibility(View.GONE);
        binding.swipCircle.startAnim();
        binding.swipCircle.setRoundColor(ContextCompat.getColor(getContext(), R.color.darkGreen));
        binding.swipCircle.setViewColor(ContextCompat.getColor(getContext(), R.color.splashColor));
        if(auth.getCurrentUser()!=null) {
            database.getReference("Farmer").child(auth.getCurrentUser().getUid()).get().addOnSuccessListener(dataSnapshot -> {
                Farmer farmer = dataSnapshot.getValue(Farmer.class);
                if (farmer != null) {
                    try {
                        binding.productDashboardBtn.setVisibility(View.VISIBLE);
                        binding.registerAsFarmerDesc.setVisibility(View.GONE);
                        binding.imgRegisterBtn.setVisibility(View.GONE);
                    }
                    catch(Exception e){
                        Log.d("productDashboard",e.getMessage()+"");
                    }
                } else {
                    binding.productDashboardBtn.setVisibility(View.GONE);
                    binding.imgRegisterBtn.setVisibility(View.VISIBLE);
                    binding.registerAsFarmerDesc.setVisibility(View.VISIBLE);
                    binding.registerAsFarmerDesc.setOnClickListener(view -> {
                        binding.registerAsFarmerDesc.setText(R.string.register_as_farmer_details_nepali);
                    });
                }
                try {
                    binding.swipCircle.stopAnim();
                    binding.swipCircle.setVisibility(View.GONE);
                }
                catch(Exception e){
                    Log.d("MoreFragment", e.getMessage()+"");
                }

            }).addOnFailureListener(e -> {
                Log.d("MoreFragment",e.getMessage()+"");
            });
        }
        else{
            binding.registerAsFarmerDesc.setVisibility(View.VISIBLE);
            binding.swipCircle.stopAnim();
            binding.swipCircle.setVisibility(View.GONE);
            binding.registerAsFarmerDesc.setText(R.string.desc_reg_farmer);
        }


        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}