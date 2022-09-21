package com.hanuman.smartagriculture.services.products;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.hanuman.smartagriculture.MainActivity;
import com.hanuman.smartagriculture.R;
import com.hanuman.smartagriculture.models.Farmer;
import com.hanuman.smartagriculture.models.Product;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;
import com.hanuman.smartagriculture.databinding.FragmentAddProductBinding;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import es.dmoral.toasty.Toasty;
import static android.app.Activity.RESULT_OK;
import static com.facebook.FacebookSdk.getApplicationContext;

public class AddProductFragment extends Fragment {
    FragmentAddProductBinding binding;
    private FirebaseAuth auth;
    private DatabaseReference mDatabaseReference;
    private StorageReference mStorageReference;
    public static final int PICK_IMAGE_REQUEST =1;
    public static final int CAMERA_CAPTURE =2;
    final int CROP_PIC = 3;
    private Uri mImageUri;
    private ProgressDialog progressDialog;
    private CrudProduct crud;
    private Bundle bundle;
    String currentPhotoPath;
    private FirebaseDatabase database;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentAddProductBinding.inflate(inflater,container,false);
        View root = binding.getRoot();

        //get instance
        auth = FirebaseAuth.getInstance();
        database=FirebaseDatabase.getInstance();
        mDatabaseReference = FirebaseDatabase.getInstance().getReference(Product.class.getSimpleName());
        mStorageReference = FirebaseStorage.getInstance().getReference(Product.class.getSimpleName());

        //progress dialog codes
        progressDialog = new ProgressDialog(getContext());
        progressDialog.setTitle("Please Wait");

        //take photo button
        binding.productCameraChooseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//                mImageUri = Uri.fromFile(new File(Environment
//                        .getExternalStorageDirectory(), "tmp_avatar_"
//                        + String.valueOf(System.currentTimeMillis())
//                        + ".jpg"));
                mImageUri = FileProvider.getUriForFile(getContext(), getContext().getApplicationContext().getPackageName() + ".provider", new File(Environment
                        .getExternalStorageDirectory(), "tmp_avatar_"
                        + String.valueOf(System.currentTimeMillis())
                        + ".jpg"));

                intent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT,
                        mImageUri);

                try {
                    intent.putExtra("return-data", true);

                    startActivityForResult(intent, CAMERA_CAPTURE);
                } catch (ActivityNotFoundException e) {
                    e.printStackTrace();
                }
            }
        });

        //get data for edit or update
        Product product_edit= (Product) getActivity().getIntent().getSerializableExtra("EDIT");
        if(product_edit!=null){
            binding.productSubmitBtn.setText(R.string.update_product);
            binding.productTitle.setText(product_edit.getProductTitle());
            binding.productPrice.setText(product_edit.getProductPrice());
            Glide.with(this).load(product_edit.getProductImage()).into(binding.productImgShow);
            binding.productStock.setText(product_edit.getProductStock());
            binding.productDesc.setText(product_edit.getProductDescription());
        }
        else{
            binding.productSubmitBtn.setText(R.string.add_product);
            Glide.with(getContext()).load(R.drawable.ic_image).into(binding.productImgShow);
        }

        binding.productImgChooseBtn.setOnClickListener(view -> {
            browseImage();
        });

        binding.productSubmitBtn.setOnClickListener(view -> {
            if(auth.getCurrentUser()!=null){
                uploadProduct();
            }
            else{
                Toasty.warning(getContext(), "Kindly login first !", Toast.LENGTH_SHORT, true).show();
            }

        });
        return root;
    }

    private void browseImage() {
        Dexter.withContext(getApplicationContext())
                .withPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {
                        Intent intent = new Intent();
                        intent.setType("image/*");
                        intent.setAction(intent.ACTION_GET_CONTENT);
                        startActivityForResult(intent,PICK_IMAGE_REQUEST);
                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse permissionDeniedResponse) {
                        Intent intent = new Intent();
                        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permissionRequest, PermissionToken permissionToken) {
                        permissionToken.continuePermissionRequest();
                    }
                }).check();
    }
    public String getFileExtension(Uri uri){
        ContentResolver cR = getActivity().getApplicationContext().getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable @org.jetbrains.annotations.Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data!=null && data.getData()!=null){
            mImageUri = data.getData();

            Glide.with(this).load(mImageUri).into(binding.productImgShow);
        }
        else if(requestCode==CAMERA_CAPTURE && resultCode == RESULT_OK && data!=null && data.getData()!=null){
            doCrop();
        }
        else if(requestCode==CROP_PIC && resultCode == RESULT_OK && data!=null && data.getData()!=null){
            Bundle extras = data.getExtras();
            if (extras != null) {
                Bitmap photo = extras.getParcelable("data");

                binding.productImgShow.setImageBitmap(photo);
            }
            File f = new File(mImageUri.getPath());
            if (f.exists())
                f.delete();

        }

    }

    private void doCrop() {
        final ArrayList<CropOption> cropOptions = new ArrayList<CropOption>();
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setType("image/*");

        List<ResolveInfo> list = getActivity().getPackageManager().queryIntentActivities(
                intent, 0);

        int size = list.size();
        if (size == 0) {

            Toast.makeText(getContext(), "Can not find image crop app",
                    Toast.LENGTH_SHORT).show();

            return;
        } else {
            intent.setData(mImageUri);

            intent.putExtra("outputX", 200);
            intent.putExtra("outputY", 200);
            intent.putExtra("aspectX", 1);
            intent.putExtra("aspectY", 1);
            intent.putExtra("scale", true);
            intent.putExtra("return-data", true);

            if (size == 1) {
                Intent i = new Intent(intent);
                ResolveInfo res = list.get(0);

                i.setComponent(new ComponentName(res.activityInfo.packageName,
                        res.activityInfo.name));

                startActivityForResult(i, CAMERA_CAPTURE);
            } else {
                for (ResolveInfo res : list) {
                    final CropOption co = new CropOption();

                    co.title = getActivity().getPackageManager().getApplicationLabel(
                            res.activityInfo.applicationInfo);
                    co.icon = getActivity().getPackageManager().getApplicationIcon(
                            res.activityInfo.applicationInfo);
                    co.appIntent = new Intent(intent);

                    co.appIntent
                            .setComponent(new ComponentName(
                                    res.activityInfo.packageName,
                                    res.activityInfo.name));

                    cropOptions.add(co);
                }

                CropOptionAdapter adapter = new CropOptionAdapter(
                        getApplicationContext(), cropOptions);

                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle("Choose Crop App");
                builder.setAdapter(adapter,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int item) {
                                startActivityForResult(
                                        cropOptions.get(item).appIntent,
                                        CROP_PIC);
                            }
                        });

                builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {

                        if (mImageUri != null) {
                            getActivity().getContentResolver().delete(mImageUri, null,
                                    null);
                            mImageUri = null;
                        }
                    }
                });

                AlertDialog alert = builder.create();

                alert.show();
            }
        }
    }

    public class CropOptionAdapter extends ArrayAdapter<CropOption> {
        private ArrayList<CropOption> mOptions;
        private LayoutInflater mInflater;

        public CropOptionAdapter(Context context, ArrayList<CropOption> options) {
            super(context, R.layout.crop_selector, options);

            mOptions = options;

            mInflater = LayoutInflater.from(context);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup group) {
            if (convertView == null)
                convertView = mInflater.inflate(R.layout.crop_selector, null);

            CropOption item = mOptions.get(position);

            if (item != null) {
                ((ImageView) convertView.findViewById(R.id.iv_icon))
                        .setImageDrawable(item.icon);
                ((TextView) convertView.findViewById(R.id.tv_name))
                        .setText(item.title);

                return convertView;
            }

            return null;
        }
    }


    public void uploadProduct() {
        progressDialog.show();
        bundle = this.getArguments();
        crud = new CrudProduct();
        if (mImageUri != null) {
            StorageReference fileReference = mStorageReference.child
                    (System.currentTimeMillis() + "." + getFileExtension(mImageUri));
            fileReference.putFile(mImageUri).addOnSuccessListener(taskSnapshot ->
                    fileReference.getDownloadUrl().addOnSuccessListener(uri -> {
                        database.getReference("Farmer")
                                .child(auth.getCurrentUser().getUid()).get()
                                .addOnSuccessListener(dataSnapshot -> {
                            Farmer farmer = dataSnapshot.getValue(Farmer.class);
                            String mobile = farmer.getMobile();
                            Product product_edit=  (Product) getActivity().getIntent().getSerializableExtra("EDIT");
                            if(product_edit==null) {
                                Product product = new Product(
                                        auth.getCurrentUser().getUid(),
                                        binding.productTitle.getText().toString().trim(),
                                        uri.toString(),
                                        binding.productPrice.getText().toString().trim(),
                                        binding.productStock.getText().toString().trim(),
                                        binding.productDesc.getText().toString().trim(),
                                        auth.getCurrentUser().getPhotoUrl().toString(),
                                        auth.getCurrentUser().getEmail(),
                                        mobile
                                );
                                crud.add(product).addOnSuccessListener(unused -> {
                                    Toasty.success(getContext(), "Successfully added your product"
                                            , Toast.LENGTH_SHORT, true).show();
                                    binding.productPrice.setText("");
                                    binding.productStock.setText("");
                                    binding.productDesc.setText("");
                                    binding.productTitle.setText("");
                                    Intent intent = new Intent(getContext(), MainActivity.class);
                                    getContext().startActivity(intent);
                                    progressDialog.dismiss();
                                }).addOnFailureListener(e -> {
                                    Log.d("AddProductFragment",e.getMessage()+"");
                                });
                            }
                            else{
                                HashMap<String, Object> hashMap = new HashMap<>();
                                hashMap.put("productTitle", binding.productTitle.getText().toString());
                                hashMap.put("productPrice", binding.productPrice.getText().toString());
                                hashMap.put("productStock",binding.productStock.getText().toString());
                                hashMap.put("productImage",uri.toString());
                                hashMap.put("productDesc",binding.productDesc.getText().toString());
                                crud.update(product_edit.getKey(), hashMap).addOnSuccessListener(suc -> {
                                    binding.productPrice.setText("");
                                    binding.productStock.setText("");
                                    binding.productDesc.setText("");
                                    binding.productTitle.setText("");
                                    Toasty.success(getContext(), "Product updated successfully",
                                            Toasty.LENGTH_SHORT,true).show();
                                    Intent intent = new Intent (getApplicationContext(), ProductDashboard.class);
                                    startActivity(intent);
                                    progressDialog.dismiss();
                                }).addOnFailureListener(e -> Toasty.error
                                        (getContext(), "" + e.getMessage(), Toast.LENGTH_SHORT,true).show());
                                progressDialog.dismiss();
                            }

                        });
                    })).addOnFailureListener(e -> {
                Toasty.warning(getContext(), "" + e.getMessage(), Toasty.LENGTH_SHORT, true).show();
            })
                    .addOnProgressListener(snapshot -> {
                long percent = (100*snapshot.getBytesTransferred())/snapshot.getTotalByteCount();
                progressDialog.setMessage("Completed: "+ percent + "%");
            });
        }
        else{
            progressDialog.dismiss();
            Toasty.error(getContext(), "No image Selected !", Toast.LENGTH_SHORT,true).show();
        }
    }

    public class CropOption {
        public CharSequence title;
        public Drawable icon;
        public Intent appIntent;
    }


}

