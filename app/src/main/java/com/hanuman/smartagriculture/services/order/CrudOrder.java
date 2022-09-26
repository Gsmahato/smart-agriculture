package com.hanuman.smartagriculture.services.order;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.hanuman.smartagriculture.models.Order;
import java.util.HashMap;

public class CrudOrder {
    private DatabaseReference databaseReference;
    FirebaseAuth auth;

    public CrudOrder(){
        FirebaseDatabase db = FirebaseDatabase.getInstance();
        databaseReference = db.getReference(Order.class.getSimpleName());
    }
    public Task<Void> add(Order order){
        return databaseReference.push().setValue(order);
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
