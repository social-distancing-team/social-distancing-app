package com.social_distancing.app;

import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;

import org.w3c.dom.Document;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class HelperClass {
	
	
	public static final FirebaseAuth auth = FirebaseAuth.getInstance();
	public static final FirebaseFirestore db = FirebaseFirestore.getInstance();
	
	public static class LOG{
		public static String ERROR = "ERROR";
		public static String SUCCESS = "SUCCESS";
		public static String WARNING = "WARNING";
		public static String INFORMATION = "INFO";
		//	D/(SUCCESS|ERROR|WARNING|INFO)
	}
	
	public static class Collections{
		public static String USERS = "Users";
		public static String LISTS = "Lists";
		public static String USERDATA = "UserData";
		public static String USERLISTS = "UserLists";
		
		public static class Users{
			public static String FIRSTNAME = "FirstName";
			public static String LASTNAME = "LastName";
			public static String EMAIL = "Email";
			public static String FRIENDS = "Friends";
			public static String LISTS = "Lists";
			public static String LOCATION = "Location";
			public static String SECURITYQUESTION = "SecurityQuestion";
			public static String SECURITYANSWER = "SecurityAnswer";
			public static String JOINED = "Joined";
			public static String LASTSEEN = "LastSeen";
			public static String STATUS = "Status";
		}
		
		public static class Lists{
			public static String NAME = "Name";
			public static String DESCRIPTION = "Description";
			public static String USERS = "Users";
			public static String ITEMS = "Items";
			
			public static class ListItem{
				public static String NAME = "Name";
				public static String QUANTITY = "Quantity";
				public static String USER = "USER";
			}
			
		}
		
		public static class UserData{
			public static String LISTS = "Lists";
			public static String CHATS = "Chats";
		}
		
		public static class UserLists{
			public static String LISTS = "Lists";
		}
		
	}
	
	public static class User {
		public static final ArrayList<Runnable> runnables = new ArrayList<>();
		public static final Map<String, Runnable> userInfoRunnables = new HashMap<>();
		
		
		public static final Map<String, Object> userInfo = new HashMap<>();
		public static final Map<String, Object> userData = new HashMap<>();
		
		public static boolean initiliased = false;
		
		public static boolean isLoggedIn() {
			return auth.getCurrentUser() != null;
		}
		
		public static boolean logout() {
			if (auth.getCurrentUser() != null) {
				auth.signOut();
				Log.d(LOG.INFORMATION, "Logged out.");
				return true;
			} else {
				Log.d(LOG.ERROR, "Cannot logout when not logged in.");
				return false;
			}
		}
		
		//public static ArrayList<String>
		
		public static final ArrayList<String> friends = new ArrayList<>();
		
		public static void setup(){
			friends.clear();
			DocumentReference userInfoReference = (DocumentReference)db.collection(Collections.USERS).document(auth.getCurrentUser().getUid());
			userInfoReference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
				@Override
				public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
					Log.d(LOG.INFORMATION, "User Information changed.");
					Log.d(LOG.INFORMATION, "User Information changed2.");
					friends.clear();
					ArrayList<String> newFriends = (ArrayList<String>)documentSnapshot.get(Collections.Users.FRIENDS);
					friends.addAll(newFriends);
					userInfo.clear();
					userInfo.putAll(documentSnapshot.getData());
					Log.d(LOG.INFORMATION, "Friends: " + friends.toString());
				}
			});
			
			
		}
		
		public static Task<DocumentSnapshot> login(String email, String password) {
			if (auth.getCurrentUser() == null) {
				
				Task<DocumentSnapshot> loginTask = auth.signInWithEmailAndPassword("passwordispassword@email.com", "password").addOnCompleteListener(new OnCompleteListener<AuthResult>() {
					@Override
					public void onComplete(@NonNull Task<AuthResult> task) {
						if (task.isSuccessful()) {
							AuthResult authResult = (AuthResult) task.getResult();
							Log.d(HelperClass.LOG.SUCCESS, "Logged in: " + authResult.getUser().getUid());
							initiliased = true;
						} else {
							Log.d(HelperClass.LOG.ERROR, "Failed to login. " + task.getException().getMessage());
						}
					}
				}).continueWithTask(new Continuation<AuthResult, Task<DocumentSnapshot>>() {
					@Override
					public Task<DocumentSnapshot> then(@NonNull Task<AuthResult> task) throws Exception {
						if (task.isSuccessful()) {
							AuthResult authResult = (AuthResult) task.getResult();
							FirebaseUser currentUser = authResult.getUser();
							String currentUserID = currentUser.getUid();
							return getUserInfo(currentUserID);
						} else {
							// Failed to login
							return Tasks.forResult(null);
						}
					}
				}).continueWithTask(new Continuation<DocumentSnapshot, Task<DocumentSnapshot>>() {
					@Override
					public Task<DocumentSnapshot> then(@NonNull Task<DocumentSnapshot> task) throws Exception {
						if (task.isSuccessful() && task.getResult() != null) {
							return getUserData();
						} else {
							// Failed to login or failed to get the user document
							return Tasks.forResult(null);
						}
					}
				});
				
				return loginTask;
			} else {
				Log.d(HelperClass.LOG.ERROR, "Already logged in.");
				return Tasks.forResult(null);
			}
		}
		
		private static Task<DocumentSnapshot> getUserInfo(String currentUserID){
			Log.d(HelperClass.LOG.INFORMATION, "Getting user info.");
			return db.collection(Collections.USERS).document(currentUserID).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
				@Override
				public void onComplete(@NonNull Task<DocumentSnapshot> task) {
					if (task.isSuccessful()) {
						DocumentSnapshot documentSnapshot = (DocumentSnapshot) task.getResult();
						Log.d(HelperClass.LOG.SUCCESS, "User found.");
						Map<String, Object> mapData = documentSnapshot.getData();
						for (String key : mapData.keySet()) {
							userInfo.put(key, mapData.get(key));
						}
						
						friends.clear();
						friends.addAll((ArrayList<String>)mapData.get(Collections.Users.FRIENDS));
						DocumentReference userInfoReference = (DocumentReference)db.collection(Collections.USERS).document(auth.getCurrentUser().getUid());
						userInfoReference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
							@Override
							public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
								Log.d(LOG.INFORMATION, "User Information changed.");
								friends.clear();
								ArrayList<String> newFriends = (ArrayList<String>)documentSnapshot.get(Collections.Users.FRIENDS);
								friends.addAll(newFriends);

								userInfo.clear();
								userInfo.putAll(documentSnapshot.getData());
								Log.d(LOG.INFORMATION, "Friends: " + friends.toString());
								
								for (String key : userInfoRunnables.keySet()){
									userInfoRunnables.get(key).run();
								}
								
							}
						});
						
						Log.d(HelperClass.LOG.INFORMATION, "userInfo: " + userInfo.toString());
					} else {
						Log.d(LOG.ERROR, "Failed to get user info.");
					}
				}
			});
		}
		
		private static Task<DocumentSnapshot> getUserData() {
			Log.d(LOG.INFORMATION, "Getting user data.");
			return db.collection(Collections.USERDATA).document(auth.getCurrentUser().getUid()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
				@Override
				public void onComplete(@NonNull Task<DocumentSnapshot> task) {
					if (task.isSuccessful()) {
						DocumentSnapshot documentSnapshot = (DocumentSnapshot) task.getResult();
						Log.d(HelperClass.LOG.SUCCESS, "User Data found.");
						Map<String, Object> mapData = documentSnapshot.getData();
						for (String key : mapData.keySet()) {
							userData.put(key, mapData.get(key));
						}
						Log.d(LOG.INFORMATION, "UserData: " + userData.toString());
					} else {
						Log.d(LOG.ERROR, "Failed to get user data.");
					}
				}
			});
		}
		
		public static Task<ArrayList<String> > getFriends() {
			Task<ArrayList<String>> task = db.collection(Collections.USERS).document(auth.getCurrentUser().getUid()).get().continueWithTask(new Continuation<DocumentSnapshot, Task<ArrayList<String>>>() {
				@Override
				public Task<ArrayList<String> > then(@NonNull Task<DocumentSnapshot> task) throws Exception {
					if (task.isSuccessful() && task.getResult() != null){
						DocumentSnapshot documentSnapshot = (DocumentSnapshot)task.getResult();
						ArrayList<String> friends = (ArrayList<String>)documentSnapshot.get(Collections.Users.FRIENDS);
						return Tasks.forResult(friends);
					}
					return Tasks.forResult(null);
				}
			});
			return task;
		}
		
		public static ArrayList<String> getLists(){
			return (ArrayList<String>)userData.get(Collections.UserData.LISTS);
		}
		
		public static ArrayList<String> getChats(){
			return (ArrayList<String>)userData.get(Collections.UserData.CHATS);
		}
		
		public static Task<Map<String, Object> > getProfileInfo(final String userID){
			Log.d(LOG.INFORMATION, "Looking for user profile: " + userID);
			Task<Map<String, Object> > task = db.collection(Collections.USERS).document(userID).get().continueWithTask(new Continuation<DocumentSnapshot, Task<Map<String, Object>>>() {
				@Override
				public Task<Map<String, Object>> then(@NonNull Task<DocumentSnapshot> task) throws Exception {
					if (task.isSuccessful()){
						DocumentSnapshot userInfo = (DocumentSnapshot)task.getResult();
						if (userInfo.exists()){
							Log.d(LOG.INFORMATION, "Found user profile: " + userID);
							return Tasks.forResult(userInfo.getData());
						}
						Log.d(LOG.ERROR, "User profile not found.");
					}
					Log.d(LOG.ERROR, "Failed to get profile of user " + userID);
					return Tasks.forResult(null);
				}
			});
			return task;
		}
		
		public static Task<Void> addFriend(final String userID){
			DocumentReference userInfoReference = (DocumentReference)db.collection(Collections.USERS).document(auth.getCurrentUser().getUid());
			final Map<String, Object> addFriendMap = new HashMap<>();
			addFriendMap.put("Friends", FieldValue.arrayUnion(userID));
			
			return userInfoReference.update(addFriendMap);
		}
		
		public static Task<Void> removeFriend(final String userID){
			DocumentReference userInfoReference = (DocumentReference)db.collection(Collections.USERS).document(auth.getCurrentUser().getUid());
			final Map<String, Object> addFriendMap = new HashMap<>();
			addFriendMap.put("Friends", FieldValue.arrayRemove(userID));
			
			return userInfoReference.update(addFriendMap);
		}
		
		public static void addRunnable(Runnable runnable){
			runnables.add(runnable);
		}
	}
}
