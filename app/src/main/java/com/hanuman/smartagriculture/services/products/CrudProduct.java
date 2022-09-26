package com.hanuman.smartagriculture.services.products;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.hanuman.smartagriculture.models.Product;
import java.util.HashMap;

public class CrudProduct {
    private DatabaseReference databaseReference;
    FirebaseAuth auth;

    public CrudProduct(){
        FirebaseDatabase db = FirebaseDatabase.getInstance();
        databaseReference = db.getReference(Product.class.getSimpleName());
    }
    public Task<Void> add(Product product){
        String uploadId = databaseReference.push().getKey();
        return databaseReference.child(uploadId).setValue(product);
    }
    public Task<Void> update(String key, HashMap<String,Object> hashMap){
        return databaseReference.child(key).updateChildren(hashMap);
    }
    public Task<Void> remove(String key){
        return databaseReference.child(key).removeValue();
    }

    public Query get(String key){
        if(key==null){
            return databaseReference.orderByKey().limitToFirst(30);
        }
        return databaseReference.orderByKey().startAfter(key).limitToFirst(30);
    }
}
