package com.hanuman.smartagriculture.descriptions.help;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.hanuman.smartagriculture.R;
import com.hanuman.smartagriculture.databinding.FragmentFarmerHelpBinding;


public class FarmerHelpFragment extends Fragment {
    FragmentFarmerHelpBinding binding;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding= FragmentFarmerHelpBinding.inflate(inflater,container,false);
        View view = binding.getRoot();
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding.moreOption.setOnClickListener(view1 -> {
            UserLoginHelpFragment fragment = new UserLoginHelpFragment();
            FragmentManager fm = getParentFragmentManager();
            fm.beginTransaction().replace(R.id.frame_container,fragment ).addToBackStack(null).commit();
        });
    }
}