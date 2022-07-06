package com.hanuman.smartagriculture.dashboard.info;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.Toast;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;
import com.hanuman.smartagriculture.LoginActivity;
import com.hanuman.smartagriculture.R;
import com.hanuman.smartagriculture.Utilities;
import com.hanuman.smartagriculture.databinding.ActivityAddInfoBinding;
import com.hanuman.smartagriculture.models.Info;
import org.jetbrains.annotations.NotNull;
import java.util.HashMap;

import es.dmoral.toasty.Toasty;

public class AddInfoActivity extends AppCompatActivity {
    ActivityAddInfoBinding binding;
    FirebaseAuth auth;
    private DatabaseReference mDatabaseReference;
    private StorageReference mStorageReference;
    public static final int PICK_IMAGE_REQUEST =1;
    private Uri mImageUri;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding= ActivityAddInfoBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        //action bar color
        ActionBar actionBar;
        actionBar = getSupportActionBar();
        Utilities.appBarColor(actionBar, this); //action bar ends

        //progress dialog codes
        progressDialog = new ProgressDialog(AddInfoActivity.this);
        progressDialog.setTitle("Please Wait");

        //get instance
        auth = FirebaseAuth.getInstance();
        mDatabaseReference = FirebaseDatabase.getInstance().getReference(Info.class.getSimpleName());
        mStorageReference = FirebaseStorage.getInstance().getReference(Info.class.getSimpleName());

        //for dropdown buttons
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.info_type, R.layout.item_dropdown);
        adapter.setDropDownViewResource(R.layout.item_dropdown);
        binding.infoTypeEditText.setAdapter(adapter);

        binding.btnChooseImgInfo.setOnClickListener(view -> {
            browseImage();
        });
        Info info_edit= (Info) getIntent().getSerializableExtra("EDIT");

        if(info_edit!=null){
            binding.btnAddInfo.setText(R.string.update_info);
            binding.infoTitleEditText.setText(info_edit.getInfoTitle());
            binding.infoDetailsEditText.setText(info_edit.getInfoDetails());
            Glide.with(this).load(info_edit.getInfoImage()).into(binding.imgInfoView);
            binding.infoTypeEditText.setText(info_edit.getInfoType());

            //for dropdown buttons
            ArrayAdapter<CharSequence> adapter2 = ArrayAdapter.createFromResource(this,
                    R.array.info_type, R.layout.item_dropdown);
            adapter2.setDropDownViewResource(R.layout.item_dropdown);
            binding.infoTypeEditText.setAdapter(adapter2);
        }
        else{
            binding.btnAddInfo.setText(R.string.add_info);
        }

        //for news add button pressed
        binding.btnAddInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AddInfoActivity.this.uploadFile();
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable @org.jetbrains.annotations.Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data!=null && data.getData()!=null){
            mImageUri = data.getData();
            Glide.with(this).load(mImageUri).into(binding.imgInfoView);

        }
    }

    public String getFileExtension(Uri uri){
        ContentResolver cR = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }

    public void uploadFile(){
        Info info_edit= (Info) getIntent().getSerializableExtra("EDIT");
        CrudInfo crud = new CrudInfo();

        if(mImageUri !=null){
            progressDialog.show();
            StorageReference fileReference = mStorageReference.child(System.currentTimeMillis()+"."+getFileExtension(mImageUri));
            fileReference.putFile(mImageUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            fileReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    Info info = new Info(uri.toString(),binding.infoTitleEditText.getText().toString().trim(),
                                            binding.infoDetailsEditText.getText().toString().trim(),binding.infoTypeEditText.getText().toString());
                                    if(info_edit==null) {
                                        crud.add(info).addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void suc) {
                                                binding.infoTitleEditText.setText("");
                                                binding.infoDetailsEditText.setText("");
                                                binding.infoTypeEditText.setText("");
                                                Toasty.success(AddInfoActivity.this, "Agriculture Info inserted successfully",
                                                        Toasty.LENGTH_SHORT,true).show();
                                                Intent intent = new Intent(AddInfoActivity.this.getApplicationContext(), ViewInfoActivity.class);
                                                AddInfoActivity.this.startActivity(intent);
                                                progressDialog.dismiss();
                                            }
                                        }).addOnFailureListener(e -> Toast.makeText
                                                (AddInfoActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show());
                                    }

                                    else {
                                        HashMap<String, Object> hashMap = new HashMap<>();
                                        hashMap.put("infoTitle", binding.infoTitleEditText.getText().toString());
                                        hashMap.put("infoDetails", binding.infoDetailsEditText.getText().toString());
                                        hashMap.put("infoImage",uri.toString());
                                        hashMap.put("infoType",binding.infoTypeEditText.getText().toString());
                                        crud.update(info_edit.getKey(), hashMap).addOnSuccessListener(suc -> {
                                            Toasty.success(AddInfoActivity.this, "Info updated successfully",
                                                    Toasty.LENGTH_SHORT).show();
                                            Intent intent = new Intent (getApplicationContext(), ViewInfoActivity.class);
                                            startActivity(intent);
                                            progressDialog.dismiss();
                                        }).addOnFailureListener(e -> Toast.makeText
                                                (AddInfoActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show());
                                        progressDialog.dismiss();
                                    }
                                }
                            });
                        }
                    })
                    .addOnProgressListener(snapshot -> {
                        long percent = (100*snapshot.getBytesTransferred())/snapshot.getTotalByteCount();
                        progressDialog.setMessage("Saving information to your account :"+ percent + "% Completed");
                    })
                    .addOnFailureListener(e -> Log.d("AddInfoActivity",e.getMessage()+""));
        }
        else{
            Toast.makeText(this, "No File Selected !", Toast.LENGTH_SHORT).show();
        }

    }

    private void browseImage(){
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

}