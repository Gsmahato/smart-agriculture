package com.hanuman.smartagriculture.ui.account;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;
import com.bumptech.glide.Glide;
import com.facebook.login.LoginManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.hanuman.smartagriculture.LoginActivity;
import com.hanuman.smartagriculture.R;
import com.hanuman.smartagriculture.descriptions.AboutActivity;
import com.hanuman.smartagriculture.descriptions.PrivacyPolicyActivity;
import com.hanuman.smartagriculture.services.order.OrderList;
import com.hanuman.smartagriculture.BuildConfig;
import com.hanuman.smartagriculture.databinding.FragmentAccountBinding;
import java.util.Objects;
import es.dmoral.toasty.Toasty;

public class AccountFragment extends Fragment {
    FragmentAccountBinding binding;
    FirebaseAuth auth;
    Dialog dialog;
    public Context context;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentAccountBinding.inflate(inflater,container,false);
        View root = binding.getRoot();

        //for dialog confirmation
        dialog = new Dialog(getActivity());

        //get instance
        auth = FirebaseAuth.getInstance();
        FirebaseUser user = auth.getCurrentUser();
        //Setting Version name
        String versionName = BuildConfig.VERSION_NAME;
        binding.appVersionTextView.setText("Version : " + versionName);

        if(user!=null){
            String personImage = user.getPhotoUrl().toString()+"?height=500";
            Glide.with(this).load(personImage).fitCenter().centerCrop().into(binding.profileImageAccount);
            binding.usernameTextView.setText(user.getDisplayName());
            binding.emailTextView.setText(user.getEmail());
        }
        else{
            binding.emailTextView.setText("");
            binding.profileImageAccount.setVisibility(View.INVISIBLE);
            binding.btnLogout2.setVisibility(View.INVISIBLE);
            binding.usernameTextView.setText(R.string.login_click);
            binding.usernameTextView.setOnClickListener(view -> {
                Intent intent = new Intent(getActivity(), LoginActivity.class);
                startActivity(intent);
            });

        }

        //for display drawer layout
        ActionBar mActionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        Objects.requireNonNull(mActionBar).setDisplayHomeAsUpEnabled(true);

        binding.btnAboutUs.setOnClickListener(view -> {
            Intent intent = new Intent(getActivity(), AboutActivity.class);
            startActivity(intent);
        });

        binding.btnPrivacyPolicy.setOnClickListener(view -> {
            Intent intent = new Intent(getActivity(), PrivacyPolicyActivity.class);
            startActivity(intent);
        });
        binding.btnShareApp.setOnClickListener(view -> shareApp());
        binding.btnLogout2.setOnClickListener(view -> {
            dialog.setContentView(R.layout.dialog_confirmation);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            Button yesBtn = dialog.findViewById(R.id.btn_yes_confirm);
            Button noBtn = dialog.findViewById(R.id.btn_no_confirm);
            dialog.show();
            ImageView closeImage = dialog.findViewById(R.id.close_image);
            closeImage.setOnClickListener(view13 -> dialog.dismiss());

            yesBtn.setOnClickListener(view1 -> {
                auth.signOut();
                LoginManager.getInstance().logOut();
                Toasty.success(getActivity(), "Successfully Logout the account.", Toast.LENGTH_SHORT,true).show();
                Intent intent2 = new Intent(getActivity(), LoginActivity.class);
                startActivity(intent2);
            });
            noBtn.setOnClickListener(view12 -> dialog.dismiss());

        });

        binding.btnViewOrders.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), OrderList.class);
                startActivity(intent);
            }
        });

    return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    public void shareApp(){
        Intent i = new Intent(Intent.ACTION_SEND);
        i.setType("text/plain");
        i.putExtra(Intent.EXTRA_SUBJECT, "Sharing URL");
        i.putExtra(Intent.EXTRA_TEXT, "https://www.facebook.com/smartagricultureandroid");
        startActivity(Intent.createChooser(i, "Share URL"));
    }

}