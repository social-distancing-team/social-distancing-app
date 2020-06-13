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
import java.util.List;
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
		public static String CHATS = "Chats";
		public static String MESSAGES = "Messages";
		
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
		
		public static class Chat{
			public static String NAME = "Name";
			public static String USERS = "Users";
			public static String MESSAGES = "Messages";
			public static String LASTMESSAGE = "LastMessage";
			public static String LASTMESSAGETIMESTAMP = "LastMessageTimestamp";
		}
		
		
		public static class Message{
			public static String CHATID = "ChatID";
			public static String CONTENT = "Content";
			public static String USERID = "sendingUserID";
			public static String TIMESTAMP = "timestamp";
		}
	}
	
	public static class User {
		public static final ArrayList<Runnable> runnables = new ArrayList<>();
		
		public static final Map<String, String> userNames = new HashMap<>();
		public static final Map<String, String> groupNames = new HashMap<>();
		public static final Map<String, String> chatNames = new HashMap<>();
		public static final Map<String, String> listNames = new HashMap<>();
		
		//public static final Map<String, Object> userData = new HashMap<>();
		
		public static final Map<String, String> userChats = new HashMap<>();
		public static final Map<String, String> groupChats = new HashMap<>();
		
		
		public static final Map<String, Object> userInfo = new HashMap<>();
		public static final Map<String, Map<String, Object>> friendInfo = new HashMap<>();
		public static final Map<String, Map<String, Object>> groupInfo = new HashMap<>();
		public static final Map<String, Map<String, Object>> chatInfo = new HashMap<>();
		public static final Map<String, Map<String, Object>> listInfo = new HashMap<>();
		
		
		public static final Map<String, Runnable> userInfoRunnables = new HashMap<>();
		public static final Map<String, Runnable> friendInfoRunnables = new HashMap<>();
		public static final Map<String, Runnable> groupInfoRunnables = new HashMap<>();
		public static final Map<String, Runnable> chatInfoRunnables = new HashMap<>();
		public static final Map<String, Runnable> listInfoRunnables = new HashMap<>();
		
		public static final ArrayList<String> groups = new ArrayList<>();
		public static final ArrayList<String> friends = new ArrayList<>();
		public static final Map<String, String> chats = new HashMap<>();
		
		public static boolean initiliased = false;
		
		public static boolean isLoggedIn() {
			return auth.getCurrentUser() != null;
		}
		
		public static boolean logout() {
			if (auth.getCurrentUser() != null) {
				
				for (String key : listenerRegistrationMap.keySet()){
					((ListenerRegistration)listenerRegistrationMap.get(key)).remove();
				}
				
				auth.signOut();
				Log.d(LOG.INFORMATION, "Logged out.");
				
				userInfo.clear();
				friendInfo.clear();
				groupInfo.clear();
				chatInfo.clear();
				userInfoRunnables.clear();
				friendInfoRunnables.clear();
				groupInfoRunnables.clear();
				
				return true;
			} else {
				Log.d(LOG.ERROR, "Cannot logout when not logged in.");
				return false;
			}
		}
		
		public static Task<DocumentSnapshot> login(String email, String password) {
			if (auth.getCurrentUser() == null) {
				
				Task<DocumentSnapshot> loginTask = auth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
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
				});
				/*.continueWithTask(new Continuation<DocumentSnapshot, Task<DocumentSnapshot>>() {
					@Override
					public Task<DocumentSnapshot> then(@NonNull Task<DocumentSnapshot> task) throws Exception {
						if (task.isSuccessful() && task.getResult() != null) {
							return getUserData();
						} else {
							// Failed to login or failed to get the user document
							return Tasks.forResult(null);
						}
					}
				});*/
				
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
						if (!documentSnapshot.exists()){
							Log.d(LOG.ERROR, "user doucment not found");
							return;
						}
						
						Log.d(HelperClass.LOG.SUCCESS, "User found.");
						Map<String, Object> mapData = documentSnapshot.getData();
						
						Log.d(LOG.INFORMATION, "user data : " + mapData.toString());
						
						for (String key : mapData.keySet()) {
							userInfo.put(key, mapData.get(key));
						}
						
						setupListeners();
						/*
						DocumentReference userInfoReference = (DocumentReference)db.collection(Collections.USERS).document(auth.getCurrentUser().getUid());
						userInfoReference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
							@Override
							public void onEvent(@Nullable final DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
								Log.d(LOG.INFORMATION, "User Information changed.");
								userInfo.clear();
								userInfo.putAll(documentSnapshot.getData());
								
								final ArrayList<String> friends = (ArrayList<String>)documentSnapshot.get(Collections.Users.FRIENDS);
								Log.d(LOG.INFORMATION, "Friends: " + friends.toString());
								
								List<Task<?>> getFriendUserInfoTasks = new ArrayList<>();
								for (String friend : friends){
									if (!friendInfo.containsKey(friend)){
										DocumentReference friendDocumentReference = (DocumentReference)db.collection(Collections.USERS).document(friend);
										getFriendUserInfoTasks.add(friendDocumentReference.get());
									}
								}
								
								Tasks.whenAllComplete(getFriendUserInfoTasks).addOnCompleteListener(new OnCompleteListener<List<Task<?>>>() {
									@Override
									public void onComplete(@NonNull Task<List<Task<?>>> task) {
										List<Task<?>> taskList = (List<Task<?>>)task.getResult();
										for (Task friendInfoTask : taskList){
											if (friendInfoTask.isSuccessful()){
												DocumentSnapshot friendUserSnapshot = (DocumentSnapshot)friendInfoTask.getResult();
												//userNames.put(documentSnapshot1.getId(), documentSnapshot1.get("FirstName") + " " + documentSnapshot1.get("LastName"));
												friendInfo.put(friendUserSnapshot.getId(),  friendUserSnapshot.getData());
											}
										}
										
										for (String key : userInfoRunnables.keySet()){
											Runnable runnable = userInfoRunnables.get(key);
											if (runnable != null)
												runnable.run();
										}
									}
								});
								
							}
						});
						*/
						Log.d(HelperClass.LOG.INFORMATION, "userInfo: " + userInfo.toString());
					} else {
						Log.d(LOG.ERROR, "Failed to get user info.");
					}
				}
			});
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
		
		public static Task<Void> addFriend_old(final String userID){
			DocumentReference userInfoReference = (DocumentReference)db.collection(Collections.USERS).document(auth.getCurrentUser().getUid());
			final Map<String, Object> addFriendMap = new HashMap<>();
			addFriendMap.put("Friends", FieldValue.arrayUnion(userID));
			return userInfoReference.update(addFriendMap);
		}
		
		public static Task<Void> removeFriend_old(final String userID){
			DocumentReference userInfoReference = (DocumentReference)db.collection(Collections.USERS).document(auth.getCurrentUser().getUid());
			final Map<String, Object> addFriendMap = new HashMap<>();
			addFriendMap.put("Friends", FieldValue.arrayRemove(userID));
			
			return userInfoReference.update(addFriendMap);
		}
		
		final static Map<String, ListenerRegistration> listenerRegistrationMap = new HashMap<>();
		
		public static void setupListeners(){
			String userID = auth.getCurrentUser().getUid().toString();
			
			DocumentReference userInfoReference = db.collection(Collections.USERS).document(userID);
			DocumentReference userChatsReference = db.collection("UserChats").document(userID);
			DocumentReference userGroupsReference = db.collection("UserGroups").document(userID);
			DocumentReference userListsReference = db.collection("UserLists").document(userID);
			
			ListenerRegistration userListener = userInfoReference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
				@Override
				public void onEvent(@Nullable final DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
					Log.d(LOG.INFORMATION, "User Information changed.");
					userInfo.clear();
					userInfo.putAll(documentSnapshot.getData());
					
					List<Task<?>> getFriendUserInfoTasks = new ArrayList<>();
					if (!documentSnapshot.get(Collections.Users.FRIENDS).toString().equals("")){
						friends.clear();
						friends.addAll((ArrayList<String>)documentSnapshot.get(Collections.Users.FRIENDS));
						
						final ArrayList<String> friends = (ArrayList<String>)documentSnapshot.get(Collections.Users.FRIENDS);
						Log.d(LOG.INFORMATION, "Friends: " + friends.toString());
						
						
						
						for (String friend : friends){
							if (!friendInfo.containsKey(friend)){
								DocumentReference friendDocumentReference = (DocumentReference)db.collection(Collections.USERS).document(friend);
								
								friendDocumentReference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
									@Override
									public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
										friendInfo.put(documentSnapshot.getId(), documentSnapshot.getData());
										for (String key : friendInfoRunnables.keySet()){
											Runnable runnable = friendInfoRunnables.get(key);
											if (runnable != null)
												runnable.run();
										}
									}
								});
								
								//getFriendUserInfoTasks.add(friendDocumentReference.get());
							}
						}
					}
					
					for (String key : userInfoRunnables.keySet()){
						Runnable runnable = userInfoRunnables.get(key);
						if (runnable != null)
							runnable.run();
					}
					
					Tasks.whenAllComplete(getFriendUserInfoTasks).addOnCompleteListener(new OnCompleteListener<List<Task<?>>>() {
						@Override
						public void onComplete(@NonNull Task<List<Task<?>>> task) {
							List<Task<?>> taskList = (List<Task<?>>)task.getResult();
							for (Task friendInfoTask : taskList){
								if (friendInfoTask.isSuccessful()){
									DocumentSnapshot friendUserSnapshot = (DocumentSnapshot)friendInfoTask.getResult();
									//userNames.put(documentSnapshot1.getId(), documentSnapshot1.get("FirstName") + " " + documentSnapshot1.get("LastName"));
									friendInfo.put(friendUserSnapshot.getId(),  friendUserSnapshot.getData());
								}
							}
							
							for (String key : userInfoRunnables.keySet()){
								Runnable runnable = userInfoRunnables.get(key);
								if (runnable != null)
									runnable.run();
							}
						}
					});
					
				}
			});
			
			ListenerRegistration groupListener = userGroupsReference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
				@Override
				public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
					groups.clear();
					
					Log.d(LOG.INFORMATION, "Group data: " + documentSnapshot.getData().toString());
					
					if (documentSnapshot.get("Groups").toString().equals(""))
						return;
					
					groups.addAll((ArrayList)documentSnapshot.get("Groups"));
					
					ArrayList<String> groups = (ArrayList)documentSnapshot.get("Groups");
					
					Log.d(LOG.INFORMATION, "Group IDs: " + groups.toString());
					
					//
					//groupInfo.clear();
					
					List<Task<?>> getGroupInfoTasks = new ArrayList<>();
					for (String group : groups){
						if (!groupInfo.containsKey(group) /*|| true*/){
							DocumentReference groupDocumentReference = (DocumentReference)db.collection("Groups").document(group);
							//getGroupInfoTasks.add(groupDocumentReference.get());
							
							groupDocumentReference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
								@Override
								public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
									groupInfo.put(documentSnapshot.getId(), documentSnapshot.getData());
									for (String key : groupInfoRunnables.keySet()){
										Runnable runnable = groupInfoRunnables.get(key);
										if (runnable != null)
											runnable.run();
									}
								}
							});
							
						}
					}
					
					
					
					Tasks.whenAllComplete(getGroupInfoTasks).addOnCompleteListener(new OnCompleteListener<List<Task<?>>>() {
						@Override
						public void onComplete(@NonNull Task<List<Task<?>>> task) {
							List<Task<?>> taskList = (List<Task<?>>)task.getResult();
							for (Task groupInfotask : taskList){
								if (groupInfotask.isSuccessful()){
									DocumentSnapshot groupSnapshot = (DocumentSnapshot)groupInfotask.getResult();
									groupInfo.put(groupSnapshot.getId(),  groupSnapshot.getData());
								}
							}
							
							for (String key : groupInfoRunnables.keySet()){
								Runnable runnable = groupInfoRunnables.get(key);
								if (runnable != null)
									runnable.run();
							}
						}
					});
				}
			});
			
			
			ListenerRegistration chatsListener = userChatsReference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
				@Override
				public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
					chats.clear();
					
					if (documentSnapshot.get("SingleChats").toString().equals(""))
						return;
					
					Log.d(LOG.INFORMATION, "ChatReference1 : "  + documentSnapshot.getData().toString());
					
					Log.d(LOG.INFORMATION, "ChatReference2 : "  + documentSnapshot.get("SingleChats").toString());
					
					chats.putAll((Map<String, String>)documentSnapshot.get("SingleChats"));
					
					Map<String, String> chats = (Map<String, String>)documentSnapshot.get("SingleChats");
					
					Log.d(LOG.INFORMATION, "Chat IDs: " + chats.toString());
					
					//
					//groupInfo.clear();
					
					if (true)
						return;
					
					List<Task<?>> getGroupInfoTasks = new ArrayList<>();
					for (String chat : chats.keySet()){
						if (!chatInfo.containsKey(chat) /*|| true*/){
							DocumentReference groupDocumentReference = (DocumentReference)db.collection("Groups").document(chat);
							//getGroupInfoTasks.add(groupDocumentReference.get());
							
							groupDocumentReference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
								@Override
								public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
									groupInfo.put(documentSnapshot.getId(), documentSnapshot.getData());
									for (String key : groupInfoRunnables.keySet()){
										Runnable runnable = groupInfoRunnables.get(key);
										if (runnable != null)
											runnable.run();
									}
								}
							});
							
						}
					}
				}
			});
			
			listenerRegistrationMap.put("user", userListener);
			listenerRegistrationMap.put("group", groupListener);
			listenerRegistrationMap.put("chat", chatsListener);
		}
		
		public static Task<Void> addFriend(final String friendID){
			DocumentReference userReference = db.collection(Collections.USERS).document(auth.getCurrentUser().getUid());
			DocumentReference friendReference = db.collection(Collections.USERS).document(friendID);
			
			final Map<String, Object> addFriendMap = new HashMap<>();
			addFriendMap.put("Friends", FieldValue.arrayUnion(friendID));
			Task<Void> addFriendTask = userReference.update(addFriendMap);
			
			final Map<String, Object> addFriendMap2 = new HashMap<>();
			addFriendMap2.put("Friends", FieldValue.arrayUnion(auth.getCurrentUser().getUid()));
			Task<Void> addFriendTask2 = friendReference.update(addFriendMap2);
			
			
			/*
			final Map<String, Object> f1 = new HashMap<>();
			Map<String, Object> m1 = new HashMap<>();
			m1.put(auth.getCurrentUser().getUid(), chatReference.getId());
			f1.put(auth.getCurrentUser().getUid(), chatReference.getId());
			db.collection("UserChats").document(friendID).update(f1);
			
			final Map<String, Object> f2 = new HashMap<>();
			Map<String, Object> m2 = new HashMap<>();
			m2.put(friendID, chatReference.getId());
			f2.put(friendID, chatReference.getId());
			db.collection("UserChats").document(auth.getCurrentUser().getUid()).update(f2);
			*/
			
			final DocumentReference chatReference = db.collection("Chats").document();
			final Map<String, Object> chatData = new HashMap<>();
			ArrayList<String> users = new ArrayList<>();
			users.add(friendID);
			users.add(auth.getCurrentUser().getUid());
			chatData.put("LastMessageTimestamp", 0);
			ArrayList<String> chatMessages = new ArrayList<>();
			chatData.put("Messages", ""); //empty message to create the field
			chatData.put("Name", "");
			chatData.put("Users", users);
			
			final DocumentReference userChatsReference = db.collection("UserChats").document(auth.getCurrentUser().getUid());
			userChatsReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
				@Override
				public void onComplete(@NonNull Task<DocumentSnapshot> task) {
					DocumentSnapshot documentSnapshot = task.getResult();
					Map<String, Object> data;
					if (documentSnapshot.get("SingleChats").toString().equals("")){
						data = new HashMap<>();
					} else {
						data = (Map<String, Object>)documentSnapshot.get("SingleChats");
					}
					
					Log.d(LOG.INFORMATION, "kjlkjofwe : " + data.toString());
					if (!data.containsKey(friendID)){
						data.put(friendID, chatReference.getId());
						Map<String, Object> objectMap = new HashMap<>();
						objectMap.put("SingleChats", data);
						userChatsReference.update(objectMap);
						Task<Void> createChatReference = chatReference.set(chatData);
					}
				}
			});
			
			final DocumentReference userChatsReference2 = db.collection("UserChats").document(friendID);
			userChatsReference2.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
				@Override
				public void onComplete(@NonNull Task<DocumentSnapshot> task) {
					DocumentSnapshot documentSnapshot = task.getResult();
					Map<String, Object> data;
					if (documentSnapshot.get("SingleChats").toString().equals("")){
						data = new HashMap<>();
					} else {
					 	data = (Map<String, Object>)documentSnapshot.get("SingleChats");
					}
					
					Log.d(LOG.INFORMATION, "kjlkjofwe : " + data.toString());
					if (!data.containsKey(auth.getCurrentUser().getUid())){
						data.put(auth.getCurrentUser().getUid(), chatReference.getId());
						Map<String, Object> objectMap = new HashMap<>();
						objectMap.put("SingleChats", data);
						userChatsReference2.update(objectMap);
						Task<Void> createChatReference = chatReference.set(chatData);
					}
				}
			});
			
			return addFriendTask2;
		}
		
		public static Task<Void> removeFriend(String friendID){
			DocumentReference userReference = db.collection(Collections.USERS).document(auth.getCurrentUser().getUid());
			DocumentReference friendReference = db.collection(Collections.USERS).document(friendID);
			
			final Map<String, Object> addFriendMap = new HashMap<>();
			addFriendMap.put("Friends", FieldValue.arrayRemove(friendID));
			Task<Void> addFriendTask = userReference.update(addFriendMap);
			
			final Map<String, Object> addFriendMap2 = new HashMap<>();
			addFriendMap2.put("Friends", FieldValue.arrayRemove(auth.getCurrentUser().getUid()));
			Task<Void> addFriendTask2 = friendReference.update(addFriendMap2);
			
			return addFriendTask2;
		}
		
		
	}
	
	public static Task<Void> createGroup(String name, ArrayList<String> users){
		DocumentReference chatReference = HelperClass.db.collection("Chats").document();
		Map<String, Object> groupData = new HashMap<>();
		
		groupData.put("Chat", chatReference.getId());
		groupData.put("Name", name);
		groupData.put("Owner", auth.getCurrentUser().getUid().toString());
		groupData.put("Users", users);
		groupData.put("Lists", "");
		
		DocumentReference groupReference = HelperClass.db.collection("Groups").document();
		Task<Void> createGroupTask = groupReference.set(groupData);
		
		Map<String, Object> chatData = new HashMap<>();
		chatData.put("LastMessageTimestamp", 0);
		ArrayList<String> chatMessages = new ArrayList<>();
		chatData.put("Messages", ""); //empty message to create the field
		chatData.put("Name", name);
		chatData.put("Users", users);
		Task<Void> createChatReference = chatReference.set(chatData);
		
		for (String userID : users){
			DocumentReference userGroupReference = (DocumentReference)HelperClass.db.collection("UserGroups").document(userID);
			final Map<String, Object> addGroupMap = new HashMap<>();
			addGroupMap.put("Groups", FieldValue.arrayUnion(groupReference.getId()));
			Task<Void> addFriendTask = userGroupReference.update(addGroupMap);
		}
		
		return createGroupTask;
		
	}
	
	public static Task<Void> createUser(String userID, String firstName, String lastName, String dateOfBirth, String location, String email, String password, String securityQuestion, String answer){
		DocumentReference newUserReference = db.collection(Collections.USERS).document(userID);
		DocumentReference newUserChatsReference = db.collection("UserChats").document(userID);
		DocumentReference newUserGroupsReference = db.collection("UserGroups").document(userID);
		
		List<Task<?>> tasks = new ArrayList<>();
		
		ArrayList<String> emptyArrayList = new ArrayList<>();
		emptyArrayList.add("");
		
		Map<String, String> emptyMap = new HashMap<>();
		emptyMap.put("", "");
		
		Map<String, Object> userData = new HashMap<>();
		userData.put("FirstName", firstName);
		userData.put("LastName", lastName);
		userData.put("Location", location);
		userData.put("Email", email);
		userData.put("SecurityQuestion", securityQuestion);
		userData.put("SecurityAnswer", answer);
		userData.put("Deleted", false);
		userData.put("Friends", "");
		
		Map<String, Object> userChatData = new HashMap<>();
		userChatData.put("SingleChats", "");
		
		Map<String, Object> userGroupsData = new HashMap<>();
		userGroupsData.put("Groups", "");
		
		tasks.add(newUserReference.set(userData));
		tasks.add(newUserChatsReference.set(userChatData));
		tasks.add(newUserGroupsReference.set(userGroupsData));
		
		return Tasks.whenAll(tasks);
	}
	
}
