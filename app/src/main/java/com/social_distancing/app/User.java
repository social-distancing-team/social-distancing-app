package com.social_distancing.app;

import android.util.ArrayMap;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.sql.Array;
import java.util.ArrayList;
import java.util.List;

public class User {
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    private String FirstName;
    private String LastName;
    private String Email;
    private ArrayList<String> Chats;
    private ArrayList<String> Friends;
    private ArrayMap<String, Friend> friendsData = new ArrayMap<>();

    public final String getFirstName() {
        return this.FirstName;
    }

    public final String getLastName() {
        return this.LastName;
    }

    public final String getEmail() {
        return this.Email;
    }

    public final ArrayList<String> getChats() {
        return this.Chats;
    }

    public final ArrayList<String> getFriends() {
        return this.Friends;
    }

    public final String getFullName() {
        return this.FirstName + " " + this.LastName;
    }

    public final ArrayMap<String, Friend> getFriendsData() {
        return this.friendsData;
    }

    public void initFriendsData() {
        for (final String friendUid : Friends) {
            DocumentReference docRef = db.collection("Users").document(friendUid);
            docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.getResult().exists()) {
                        friendsData.put(friendUid, task.getResult().toObject(Friend.class));
                    }
                }
            });
        }
    }

    public void initSnapshotListener() {
        DocumentReference docRef =  db.collection("Users").document(mAuth.getCurrentUser().getUid());
        docRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Log.e("MMERROR", "Snapshot Listener Failed");
                    return;
                }
                if (documentSnapshot != null && documentSnapshot.exists()) {
                    if ((String)documentSnapshot.getData().get("FirstName") != FirstName) {
                        FirstName = (String)documentSnapshot.getData().get("FirstName");
                    }
                    if ((String)documentSnapshot.getData().get("LastName") != LastName) {
                        LastName = (String)documentSnapshot.getData().get("LastName");
                    }
                    if ((String)documentSnapshot.getData().get("Email") != Email) {
                        Email = (String)documentSnapshot.getData().get("Email");
                    }
                    if ((ArrayList<String>)documentSnapshot.getData().get("Chats") != Chats) {
                        Chats = (ArrayList<String>)documentSnapshot.getData().get("Chats");
                    }
                    if ((ArrayList<String>)documentSnapshot.getData().get("Friends") != Friends) {
                        Friends = (ArrayList<String>)documentSnapshot.getData().get("Friends");
                    }
                }
            }
        });
    }

    public User() {}

    public User(String FirstName, String LastName, String Email, ArrayList<String> Chats, ArrayList<String> Friends) {
        this.FirstName = FirstName;
        this.LastName = LastName;
        this.Email = Email;
        this.Chats = Chats;
        this.Friends = Friends;
    }
}