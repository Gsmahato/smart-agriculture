package com.hanuman.smartagriculture.services.order;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.hanuman.smartagriculture.models.Order;
import com.hanuman.smartagriculture.models.Sale;

import java.util.HashMap;
import java.util.Locale;

public class CrudSale {
    private DatabaseReference databaseReference;
    FirebaseAuth auth;

    public CrudSale(){
        FirebaseDatabase db = FirebaseDatabase.getInstance();
        databaseReference = db.getReference(Sale.class.getSimpleName());
    }
    public Task<Void> add(Sale sale){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        return databaseReference.child(user.getUid()).setValue(sale);
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
