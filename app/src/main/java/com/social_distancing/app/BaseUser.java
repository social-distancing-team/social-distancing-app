package com.social_distancing.app;

import android.util.Log;

import androidx.annotation.Nullable;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

public class BaseUser {
    protected static FirebaseFirestore db = FirebaseFirestore.getInstance();

    protected String UserID;
    protected String FirstName;
    protected String LastName;
    protected String Email;

    public void setUserID(String UserID) {
        this.UserID = UserID;
    }

    public final String getUserID() {
        return this.UserID;
    }

    public final String getFirstName() {
        return this.FirstName;
    }

    public final String getLastName() {
        return this.LastName;
    }

    public final String getEmail() {
        return this.Email;
    }

    public final String getFullName() {
        return this.FirstName + " " + this.LastName;
    }

    public void initSnapshotListener() {
        DocumentReference docRef =  db.collection("Users").document(UserID);
        docRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Log.e("MMERROR", "Snapshot Listener Failed");
                    return;
                }
                if (documentSnapshot != null && documentSnapshot.exists()) {
                    updateData(documentSnapshot);
                }
            }
        });
    }

    public void updateData(DocumentSnapshot documentSnapshot) {
        if ((String)documentSnapshot.getData().get("FirstName") != FirstName) {
            FirstName = (String)documentSnapshot.getData().get("FirstName");
        }
        if ((String)documentSnapshot.getData().get("LastName") != LastName) {
            LastName = (String)documentSnapshot.getData().get("LastName");
        }
        if ((String)documentSnapshot.getData().get("Email") != Email) {
            Email = (String)documentSnapshot.getData().get("Email");
        }
    }

    public BaseUser() {}

    public BaseUser(String UserID, String FirstName, String LastName, String Email) {
        this.UserID = UserID;
        this.FirstName = FirstName;
        this.LastName = LastName;
        this.Email = Email;
    }
}