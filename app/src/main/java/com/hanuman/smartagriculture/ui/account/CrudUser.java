package com.hanuman.smartagriculture.ui.account;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.hanuman.smartagriculture.models.Product;
import com.hanuman.smartagriculture.models.Users;

import java.util.HashMap;

public class CrudUser {
    private DatabaseReference databaseReference;
    FirebaseAuth auth;

    public CrudUser(){
        FirebaseDatabase db = FirebaseDatabase.getInstance();
        databaseReference = db.getReference(Users.class.getSimpleName());
    }
    public Task<Void> add(Users users){
        String uploadId = databaseReference.push().getKey();
        return databaseReference.child(uploadId).setValue(users);
    }
    public Task<Void> update(String key, HashMap<String,Object> hashMap){
        return databaseReference.child(key).updateChildren(hashMap);
    }
    public Task<Void> remove(String key){
        return databaseReference.child(key).removeValue();
    }

    public Query get(String key){
        if(key==null){
            return databaseReference.orderByKey().limitToFirst(8);
        }
        return databaseReference.orderByKey().startAfter(key).limitToFirst(8);
    }
}
