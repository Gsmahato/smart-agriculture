package com.hanuman.smartagriculture.services.register;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.hanuman.smartagriculture.MainActivity;
import com.hanuman.smartagriculture.R;
import com.hanuman.smartagriculture.Utilities;
import com.hanuman.smartagriculture.models.Farmer;
import com.hanuman.smartagriculture.services.products.ProductDashboard;
import com.hbb20.CountryCodePicker;
import com.hanuman.smartagriculture.databinding.ActivityFarmerRegisterBinding;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import es.dmoral.toasty.Toasty;

public class FarmerRegisterActivity extends AppCompatActivity {
    ActivityFarmerRegisterBinding binding;
    private FirebaseAuth auth;
    private CrudFarmer crudFarmer = new CrudFarmer();
    ArrayList<String> districtList = new ArrayList<String>();
    ArrayList<String> municipalityList = new ArrayList<String>();
    ArrayList<String> provienceList = new ArrayList<String>();
    ArrayAdapter<String> provinceAdapter,districtAdapter,municipalityAdapter;
    JSONObject jsonObject;
    JSONArray jsonArray;
    int selectedId;
    String getDistrict,getMunicipality,getProvince;
    String json_string;
    CountryCodePicker ccp;
    Dialog dialog;
    String otpId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityFarmerRegisterBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        //action bar color
        ActionBar actionBar;
        actionBar = getSupportActionBar();
        Utilities.appBarColor(actionBar,this);

        //get instance
        auth = FirebaseAuth.getInstance();
        ccp =(CountryCodePicker) findViewById(R.id.ccp);
        ccp.registerCarrierNumberEditText(binding.mblNumEditText);
        String finalMobile = ccp.getFullNumberWithPlus().replace(" ","");

        //JSON PARSING for nepali address
        json_string= loadJSONFromAsset();
        {
            try {
                jsonObject =new JSONObject(json_string);
                jsonArray =jsonObject.getJSONArray("location");
                for (int i = 0; i < jsonArray.length(); i++){
                    JSONObject jObj = jsonArray.getJSONObject(i);
                    for (int j=0;j< jObj.length();j++){
                        getProvince = jObj.names().getString(j);
                        provienceList.add(getProvince);
                        binding.provinceEditText.setOnItemClickListener((adapterView, view, position, l) -> {
                            selectedId = position;
                            for(selectedId=0;selectedId<jObj.length();selectedId++){
                                try {
                                    for (int k=0;k<jObj.getJSONObject(binding.provinceEditText.getText().toString()).names().length();k++){
                                        getDistrict = jObj.getJSONObject(binding.provinceEditText.getText().toString().trim()).names().getString(k);
                                        if (!districtList.contains(getDistrict)) {
                                            districtList.add(getDistrict);
                                        }
                                    }

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                        binding.districtEditText.setOnItemClickListener((adapterView, view, i1, l) -> {
                            selectedId = i1;
                            for(selectedId=0;selectedId<districtList.size();selectedId++){
                                {
                                    try {
                                        JSONArray jsonArray1 = jObj.getJSONObject(binding.provinceEditText.getText().toString()).getJSONArray(binding.districtEditText.getText().toString());
                                        for (int m=0;m<jsonArray1.length();m++){
                                            getMunicipality = jsonArray1.getString(m);
                                            if (!municipalityList.contains(getMunicipality)) {
                                                municipalityList.add(getMunicipality);
                                            }
                                        }
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }

                                }
                            }
                        });
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();

            }
        }
//end json


        //for dropdown for province
        provinceAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, provienceList);
        provinceAdapter.setDropDownViewResource(R.layout.item_dropdown);
        binding.provinceEditText.setAdapter(provinceAdapter);
        //for dropdown for district
        districtAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, districtList);
        provinceAdapter.setDropDownViewResource(R.layout.item_dropdown);
        binding.districtEditText.setAdapter(districtAdapter);
        //for dropdown for municipality
        municipalityAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, municipalityList);
        municipalityAdapter.setDropDownViewResource(R.layout.item_dropdown);
        binding.munEditText.setAdapter(municipalityAdapter);
        //get data for edit or update
        Farmer farmer_edit= (Farmer) this.getIntent().getSerializableExtra("EDITFARMER");

        if(farmer_edit!=null){
            binding.btnRegFarmer.setText("UPDATE FARMER PROFILE");
            binding.districtEditText.setText(farmer_edit.getDistrict());
            binding.munEditText.setText(farmer_edit.getMunicipality());
            binding.mblNumEditText.setText(farmer_edit.getMobile());
            binding.provinceEditText.setText(farmer_edit.getProvince());
            //for dropdown for province
            provinceAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, provienceList);
            provinceAdapter.setDropDownViewResource(R.layout.item_dropdown);
            binding.provinceEditText.setAdapter(provinceAdapter);
            //for dropdown for district
            districtAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, districtList);
            provinceAdapter.setDropDownViewResource(R.layout.item_dropdown);
            binding.districtEditText.setAdapter(districtAdapter);
            //for dropdown for municipality
            municipalityAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, municipalityList);
            municipalityAdapter.setDropDownViewResource(R.layout.item_dropdown);
            binding.munEditText.setAdapter(municipalityAdapter);

        }
        else{
            binding.btnRegFarmer.setText(R.string.reg_farmer);
        }

        binding.btnRegFarmer.setOnClickListener(view -> {
            dialog =new Dialog(this);
            dialog.setContentView(R.layout.dialog_mobile_verify);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            Button otpVerifyButton = dialog.findViewById(R.id.otp_verify_btn);
            EditText enterOtp = dialog.findViewById(R.id.getOTP);
            dialog.show();
            initiateOtp();
            otpVerifyButton.setOnClickListener(view1 -> {
                if (enterOtp.getText().toString().isEmpty()){
                    Toasty.error(FarmerRegisterActivity.this, "Empty Field cannot proceed", Toasty.LENGTH_SHORT).show();
                }
                else if(enterOtp.getText().length()!=6){
                    Toasty.error(FarmerRegisterActivity.this, "Invalid OTP", Toasty.LENGTH_SHORT).show();
                }
                else{
                    PhoneAuthCredential credential = PhoneAuthProvider.getCredential(otpId,enterOtp.getText().toString());
                    addUpdateFarmer(credential);
                    dialog.dismiss();
                }
            });



        });

    }

    public String loadJSONFromAsset() {
        String json = null;
        try {
            InputStream is = getAssets().open("location.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return json;
    }

    public boolean farmerRegisterValidation(){
        if(binding.provinceEditText.getText().toString().trim().isEmpty()){
            Toasty.error(FarmerRegisterActivity.this,"Kindly choose One Province !",Toasty.LENGTH_SHORT,true).show();
            return false;
        }
        else if(binding.districtEditText.getText().toString().trim().isEmpty()){
            Toasty.error(FarmerRegisterActivity.this,"Kindly choose District !",Toasty.LENGTH_SHORT,true).show();
            return false;
        }
        else if(binding.munEditText.getText().toString().trim().isEmpty()){
            Toasty.error(FarmerRegisterActivity.this,"Kindly choose municipality !",Toasty.LENGTH_SHORT,true).show();
            return false;
        }
        else if(binding.mblNumEditText.getText().toString().trim().isEmpty()){
            binding.mblNumEditText.setError("Kindly enter your mobile number");
            return false;
        }
        else if(binding.mblNumEditText.getText().toString().length()<10){
            Toasty.error(FarmerRegisterActivity.this,"Kindly choose 10 digit mobile number !",Toasty.LENGTH_SHORT,true).show();
            return false;
        }
        else{
            return true;
        }
    }

    private void addUpdateFarmer(PhoneAuthCredential credential){
        ccp =(CountryCodePicker) findViewById(R.id.ccp);
        ccp.registerCarrierNumberEditText(binding.mblNumEditText);
        String finalMobile = ccp.getFullNumberWithPlus().replace(" ","");
        Farmer farmer_edit= (Farmer) this.getIntent().getSerializableExtra("EDITFARMER");
        if(farmer_edit==null) {
            Farmer farmer = new Farmer();
            farmer.setDistrict(binding.districtEditText.getText().toString().trim());
            farmer.setMunicipality(binding.munEditText.getText().toString().trim());
            farmer.setMobile(finalMobile);
            farmer.setProvince(binding.provinceEditText.getText().toString().trim());
            if (farmerRegisterValidation()) {
                if (auth.getCurrentUser() != null) {
                    CrudFarmer crud = new CrudFarmer();
                    crud.add(farmer).addOnSuccessListener(unused -> {
                        binding.munEditText.setText("");
                        binding.districtEditText.setText("");
                        binding.mblNumEditText.setText("");
                        binding.provinceEditText.setText("");
                        Toasty.success(FarmerRegisterActivity.this, "Successfully registered as farmer", Toasty.LENGTH_SHORT, true).show();
                        Intent intent = new Intent(FarmerRegisterActivity.this, MainActivity.class);
                        startActivity(intent);
                    }).addOnFailureListener(e -> Toasty.error(FarmerRegisterActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT, true).show());

                } else {
                    Toasty.warning(FarmerRegisterActivity.this, "Kindly Login first to register as a farmer", Toast.LENGTH_SHORT, true).show();
                }
            }
        }
        else{
            HashMap<String, Object> hashMap = new HashMap<>();
            hashMap.put("district",binding.districtEditText.getText().toString());
            hashMap.put("mobile",finalMobile);
            hashMap.put("municipality",binding.munEditText.getText().toString());
            hashMap.put("province",binding.provinceEditText.getText().toString());
            crudFarmer.update(hashMap).addOnSuccessListener(unused -> {
                Toasty.success(FarmerRegisterActivity.this, "Successfully update your farmer profile !", Toasty.LENGTH_SHORT,true).show();
                binding.districtEditText.setText("");
                binding.mblNumEditText.setText("");
                binding.munEditText.setText("");
                binding.provinceEditText.setText("");
                Intent intent = new Intent(FarmerRegisterActivity.this, ProductDashboard.class);
                startActivity(intent);
            }).addOnFailureListener(e -> Toasty.error(FarmerRegisterActivity.this, ""+e.getMessage(), Toasty.LENGTH_SHORT,true).show());
        }
    }

    private void initiateOtp(){
        ccp =(CountryCodePicker) findViewById(R.id.ccp);
        ccp.registerCarrierNumberEditText(binding.mblNumEditText);
        String finalMobile = ccp.getFullNumberWithPlus().replace(" ","");
        PhoneAuthOptions options =
                PhoneAuthOptions.newBuilder(auth)
                        .setPhoneNumber(finalMobile)       // Phone number to verify
                        .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
                        .setActivity(this)                 // Activity (for callback binding)
                        .setCallbacks(new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                            @Override
                            public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                                super.onCodeSent(s, forceResendingToken);
                                otpId = s;
                            }

                            @Override
                            public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                                addUpdateFarmer(phoneAuthCredential);
                                dialog.dismiss();
                            }

                            @Override
                            public void onVerificationFailed(@NonNull FirebaseException e) {
                                Toast.makeText(FarmerRegisterActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        })          // OnVerificationStateChangedCallbacks
                        .build();
        PhoneAuthProvider.verifyPhoneNumber(options);
    }

}