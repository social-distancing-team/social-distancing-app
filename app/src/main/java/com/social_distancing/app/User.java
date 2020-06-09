package com.social_distancing.app;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class User extends ExtendedUser {
    private static FirebaseUser mUser = FirebaseAuth.getInstance().getCurrentUser();
    //private static FirebaseFirestore db = FirebaseFirestore.getInstance();

    protected ArrayList<String> Chats;

    public final ArrayList<String> getChats() {
        return this.Chats;
    }

    public void setUserID() {
        UserID = mUser.getUid();
    }

    public void updateData(DocumentSnapshot documentSnapshot) {
        super.updateData(documentSnapshot);
        if ((ArrayList<String>)documentSnapshot.getData().get("Chats") != Chats) {
            Chats = (ArrayList<String>)documentSnapshot.getData().get("Chats");
        }
    }

    public static Task<Void> addFriend(final String friendUID){
        DocumentReference docRef = db.collection("Users").document(mUser.getUid());
        final Map<String, Object> friendMap = new HashMap<>();
        friendMap.put("Friends", FieldValue.arrayUnion(friendUID));

        return docRef.update(friendMap);
    }

    public static Task<Void> removeFriend(final String friendUID){
        DocumentReference docRef = db.collection("Users").document(mUser.getUid());
        final Map<String, Object> FriendMap = new HashMap<>();
        FriendMap.put("Friends", FieldValue.arrayRemove(friendUID));

        return docRef.update(FriendMap);
    }

    public User() {}

    public User(String FirstName, String LastName, String Email, ArrayList<String> Friends, ArrayList<String> Chats) {
        this.FirstName = FirstName;
        this.LastName = LastName;
        this.Email = Email;
        this.Friends = Friends;
        this.Chats = Chats;
    }
}