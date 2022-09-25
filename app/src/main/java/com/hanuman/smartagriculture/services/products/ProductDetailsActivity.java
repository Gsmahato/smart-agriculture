package com.hanuman.smartagriculture.services.products;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.hanuman.smartagriculture.R;
import com.hanuman.smartagriculture.Utilities;
import com.hanuman.smartagriculture.databinding.ActivityProductDetailsBinding;
import com.hanuman.smartagriculture.models.Order;
import com.hanuman.smartagriculture.services.order.CrudOrder;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import es.dmoral.toasty.Toasty;

public class ProductDetailsActivity extends AppCompatActivity {
    ActivityProductDetailsBinding binding;
    String title, sellerProfile,sellerMobile,sellerEmail, price, description, image,totalProductStock, productKey, latitude, longitude;
    CrudOrder crud;
    private FirebaseAuth auth;
    Order order;
    Dialog dialog;
    CrudProduct crudProduct;
    double doubleLatitude;
    double doubleLongitude;
    String address;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityProductDetailsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        getAndSetIntentData();

        // Set App bar color
        ActionBar actionBar;
        actionBar = getSupportActionBar();
        Utilities.appBarColor(actionBar,this);
        actionBar.setTitle(getIntent().getStringExtra("TitleProductText"));
        auth = FirebaseAuth.getInstance();

        dialog = new Dialog(this);

        binding.mapButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("geo:<" + doubleLatitude  + ">,<" + doubleLongitude + ">?q=<" + doubleLatitude  + ">,<" + doubleLongitude + ">(" + address + ")"));
                startActivity(intent);
                Toasty.success(ProductDetailsActivity.this, "Going to Google Map", Toasty.LENGTH_SHORT, true).show();
            }
        });


//request an order
        if(auth.getCurrentUser()!=null) {
            binding.orderByButton.setOnClickListener(view -> {
                dialog.setContentView(R.layout.dialog_stock);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                ImageButton req_button = dialog.findViewById(R.id.send_req_button);
                EditText yourStock = dialog.findViewById(R.id.your_stock_edit_text);
                dialog.show();
                    req_button.setOnClickListener(view1 -> {
                        if(validate()) {
                            String buyerEmail = auth.getCurrentUser().getEmail();
                            String buyerName = auth.getCurrentUser().getDisplayName();
                            String buyerProfile = auth.getCurrentUser().getPhotoUrl().toString();
                            String myStock = yourStock.getText().toString();
                            int orderStock = Integer.parseInt(myStock);
                            int perUnitPrice = Integer.parseInt(price);
                            int orderPrice = orderStock * perUnitPrice;
                            order = new Order(sellerEmail, buyerEmail, buyerName, buyerProfile,
                                    title, myStock, "" + orderPrice, false);
                            crud = new CrudOrder();
                            try {
                                String[] product_stock_array =  totalProductStock.split(" ");
                                int product_stock = Integer.parseInt(product_stock_array[0]);
                                int final_stock = product_stock-orderStock;
                                if(product_stock>=orderStock && product_stock > 0) {
                                    crud.add(order).addOnSuccessListener(unused -> {
                                        yourStock.setText("");
                                        dialog.dismiss();
                                        Toasty.success(ProductDetailsActivity.this,
                                                        "Successfully sent order to the farmer !",
                                                        Toasty.LENGTH_SHORT, true)
                                                .show();
                                        crudProduct = new CrudProduct();
                                        HashMap<String, Object> hashMap = new HashMap<>();
                                        hashMap.put("productStock", final_stock + " "+ product_stock_array[1]);
                                        crudProduct.update(productKey, hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void unused) {
                                            binding.productDetailsTotalStock.setText(final_stock+ " "+ product_stock_array[1]);
                                            }
                                        });
                                    }).addOnFailureListener(e -> {
                                        dialog.dismiss();
                                        Toasty.error(ProductDetailsActivity.this, "" + e.getMessage()
                                                , Toasty.LENGTH_SHORT, true).show();

                                    });
                                }else{
                                    Toasty.error(ProductDetailsActivity.this,"Sorry! Stock is Limited", Toasty.LENGTH_SHORT, true);
                                }
                            } catch (Exception e) {
                                Toast.makeText(ProductDetailsActivity.this, "" +
                                        e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
            });
        }
        else{
            Toasty.error(this, "Kindly login first to order product !",
                    Toasty.LENGTH_SHORT,true).show();
        }


    }

    void getAndSetIntentData() {
        if (getIntent().hasExtra("SellerEmail") && getIntent().hasExtra("SellerMobile")
                && getIntent().hasExtra("TotalProductStock")
                && getIntent().hasExtra("SellerProfile") && getIntent().hasExtra("TitleProductText")
                && getIntent().hasExtra("ProductPriceText")
                && getIntent().hasExtra("ProductImage") && getIntent().hasExtra("ProductDescText")
                && getIntent().hasExtra("ProductKey")
                && getIntent().hasExtra("Latitude")
                && getIntent().hasExtra("Longitude")
        ) {
            //Getting Data from Intent
            title = getIntent().getStringExtra("TitleProductText");
            price = getIntent().getStringExtra("ProductPriceText");
            description = getIntent().getStringExtra("ProductDescText");
            image = getIntent().getStringExtra("ProductImage");
            sellerProfile=getIntent().getStringExtra("SellerProfile");
            sellerEmail=getIntent().getStringExtra("SellerEmail");
            sellerMobile=getIntent().getStringExtra("SellerMobile");
            totalProductStock = getIntent().getStringExtra("TotalProductStock");
            productKey =  getIntent().getStringExtra("ProductKey");
            latitude = getIntent().getStringExtra("Latitude");
            longitude = getIntent().getStringExtra("Longitude");

             doubleLatitude = Double.parseDouble(latitude);
             doubleLongitude = Double.parseDouble(longitude);

            Geocoder geocoder = new Geocoder(ProductDetailsActivity.this, Locale.getDefault());
            List<Address> addresses = null;

            try {
                addresses = geocoder.getFromLocation(doubleLatitude,doubleLongitude,1);
            } catch (IOException e) {
                e.printStackTrace();
            }
            address = addresses.get(0).getAddressLine(0);
            binding.sellerLocationTextView.setText(address);

            //Setting Intent Data
            binding.productTitleDetails.setText(title);
            binding.productDetailsPrice.setText(price + " Rs.");
            binding.productDescriptionDetails.setText(description);
            binding.productDetailsTotalStock.setText("Total Stock : "+ totalProductStock);

            Glide.with(this).load(image).placeholder(R.drawable.ic_image).into(binding.imageView);
            Glide.with(this).load(sellerProfile).placeholder(R.drawable.ic_image).into(binding.sellerProfileImage);
            binding.sendEmailTextView.setText(sellerEmail);
            binding.sellerMobileTextView.setText(sellerMobile);

        } else {
            Toasty.error(this, "No data Found !", Toasty.LENGTH_SHORT, true).show();
        }
    }

    private boolean validate(){
        EditText yourStock = dialog.findViewById(R.id.your_stock_edit_text);
        String myStock = yourStock.getText().toString();
        if(myStock.isEmpty()){
            Toasty.error(ProductDetailsActivity.this,"Kindly enter product stock as your requirement", Toasty.LENGTH_SHORT, true).show();
            return false;
        }
        else {
            return true;
        }
    }

}