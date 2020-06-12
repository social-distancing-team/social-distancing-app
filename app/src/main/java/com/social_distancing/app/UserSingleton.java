package com.social_distancing.app;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class UserSingleton {
    private static UserSingleton mInstance;
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private User mUser = null;

    private UserSingleton(){
        Log.d("MMDEBUG", "UserSingleton: UserSingleton:");
        DocumentReference documentReference = db.collection("Users").document(mAuth.getCurrentUser().getUid());
        documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.getResult().exists()) {
                    mUser = task.getResult().toObject(User.class);
                    mUser.setUserID();
                    mUser.initSnapshotListener();
                }
            }
        });
    }

    public static synchronized UserSingleton getInstance(){
        if (mInstance == null){
            Log.d("MMDEBUG", "UserSingleton: getInstance");
            mInstance = new UserSingleton();
        }
        return mInstance;
    }

    public User getUser(){
        Log.d("MMDEBUG", "UserSingleton: getUser");
        return mUser;
    }
}
