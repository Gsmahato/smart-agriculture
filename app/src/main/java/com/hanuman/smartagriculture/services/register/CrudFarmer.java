package com.hanuman.smartagriculture.services.register;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.hanuman.smartagriculture.models.Farmer;
import java.util.HashMap;

public class CrudFarmer {
    private DatabaseReference databaseReference;
    FirebaseAuth auth;

    public CrudFarmer(){
        FirebaseDatabase db = FirebaseDatabase.getInstance();
        databaseReference = db.getReference(Farmer.class.getSimpleName());
    }
    public Task<Void> add(Farmer farmer){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        return databaseReference.child(user.getUid()).setValue(farmer);
    }
    public Task<Void> update(HashMap<String,Object> hashMap){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        return databaseReference.child(user.getUid()).updateChildren(hashMap);
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
