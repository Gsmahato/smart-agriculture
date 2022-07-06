package com.hanuman.smartagriculture.dashboard.info;

import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.hanuman.smartagriculture.models.Info;

import java.util.HashMap;

public class CrudInfo {
    private DatabaseReference databaseReference;

    public CrudInfo() {
        FirebaseDatabase db = FirebaseDatabase.getInstance();
        databaseReference = db.getReference(Info.class.getSimpleName());
    }

        public Task<Void> add(Info info){
            String uploadId = databaseReference.push().getKey();
            return databaseReference.child(uploadId).setValue(info);
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

