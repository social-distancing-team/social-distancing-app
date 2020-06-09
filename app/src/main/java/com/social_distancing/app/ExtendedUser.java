package com.social_distancing.app;

import android.util.ArrayMap;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class ExtendedUser extends BaseUser {
    //private static FirebaseFirestore db = FirebaseFirestore.getInstance();

    protected ArrayList<String> Friends;

    public final ArrayList<String> getFriends() {
        return this.Friends;
    }

    public void updateData(DocumentSnapshot documentSnapshot) {
        super.updateData(documentSnapshot);
        if ((ArrayList<String>)documentSnapshot.getData().get("Friends") != Friends) {
            Friends = (ArrayList<String>)documentSnapshot.getData().get("Friends");
        }
    }

    public ExtendedUser() {}

    public ExtendedUser(String UserID) {
        this.UserID = UserID;
        this.FirstName = FirstName;
        this.LastName = LastName;
        this.Email = Email;
        this.Friends = Friends;
    }
}