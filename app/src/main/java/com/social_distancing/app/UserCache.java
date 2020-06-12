package com.social_distancing.app;

import android.util.ArrayMap;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class UserCache {
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    private ArrayMap<String, BaseUser> BaseUserArray = new ArrayMap<>();
    private ArrayMap<String, ExtendedUser> ExtendedUserArray = new ArrayMap<>();

    private UserCache() {

    }

    private void cacheNewBaseUser(final String UserID) {
        if (BaseUserArray.keySet().contains(UserID) || ExtendedUserArray.keySet().contains(UserID)) {
            return;
        } else {
            DocumentReference documentReference = db.collection("Users").document(UserID);
            documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.getResult().exists()) {
                    BaseUser newUser = task.getResult().toObject(User.class);
                    newUser.setUserID(UserID);
                    newUser.initSnapshotListener();
                }
                }
            });
        }
    }

    private void cacheNewExtendedUser(final String UserID) {
        DocumentReference documentReference = db.collection("Users").document(UserID);
        documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.getResult().exists()) {
                    ExtendedUser newUser = task.getResult().toObject(User.class);
                    newUser.setUserID(UserID);
                    newUser.initSnapshotListener();
                }
            }
        });
    }

    public User getUser(String UserID) {
        if (BaseUserArray.keySet().contains(UserID)) {
            return BaseUserArray.get(UserID);
        } else if (ExtendedUserArray.keySet().contains(UserID)) {
            return (BaseUser)ExtendedUserArray.get(UserID);
        } else {
            return null;
        }
    }

    public BaseUser getBaseUser(String UserID) {
        if (BaseUserArray.keySet().contains(UserID)) {
            return BaseUserArray.get(UserID);
        } else if (ExtendedUserArray.keySet().contains(UserID)) {
            return (BaseUser)ExtendedUserArray.get(UserID);
        } else {
            return null;
        }
    }

    public ExtendedUser getExtendedUser(String UserID) {
        if (BaseUserArray.keySet().contains(UserID)) {
            return BaseUserArray.get(UserID);
        } else if (ExtendedUserArray.keySet().contains(UserID)) {
            return ExtendedUserArray.get(UserID);
        } else {
            return null;
        }
    }
}
