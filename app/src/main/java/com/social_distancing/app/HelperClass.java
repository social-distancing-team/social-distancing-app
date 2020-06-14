/*
 *	Project:		Remote Life
 * 	Last edited:	13/06/2020
 * 	Author:			Karan Bajwa
 */

package com.social_distancing.app;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/*
Tasks, Async and Firebase
 */
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

/*
Collections
 */
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/*
	This is mean to be a helper class to make it easier to work with firebase.
	Encapsulated in this class is the Firebase database and authorisation objects.
	There is also
 */
public class HelperClass {
	//Firebase authorisation and database objects
	public static final FirebaseAuth auth = FirebaseAuth.getInstance();
	public static final FirebaseFirestore db = FirebaseFirestore.getInstance();
	
	/**
	 * Class for logging tags.
	 */
	public static class LOG {
		public static String ERROR = "ERROR";
		public static String SUCCESS = "SUCCESS";
		public static String WARNING = "WARNING";
		public static String INFORMATION = "INFO";
		//D/(SUCCESS|ERROR|WARNING|INFO)
	}
	
	/**
	 * Class containing the Firebase collections and document member definitions.
	 */
	public static class Collections {
		public static String USERS = "Users";
		public static String LISTS = "Lists";
		public static String CHATS = "Chats";
		public static String MESSAGES = "Messages";
		public static String GROUPS = "Groups";
		public static String USERGROUPS = "UserGroups";
		public static String USERCHATS = "UserChats";
		
		public static class Users {
			public static String DELETED = "Deleted";
			public static String FIRSTNAME = "FirstName";
			public static String LASTNAME = "LastName";
			public static String EMAIL = "Email";
			public static String FRIENDS = "Friends";
			public static String LISTS = "Lists";
			public static String LOCATION = "Location";
			public static String SECURITYQUESTION = "SecurityQuestion";
			public static String SECURITYANSWER = "SecurityAnswer";
		}
		
		public static class Lists {
			public static String NAME = "Name";
			public static String ITEMS = "Items";
		}
		
		public static class Chat {
			public static String NAME = "Name";
			public static String USERS = "Users";
			public static String MESSAGES = "Messages";
			public static String LASTMESSAGETIMESTAMP = "LastMessageTimestamp";
		}
		
		public static class Message {
			public static String CHATID = "ChatID";
			public static String CONTENT = "Content";
			public static String USERID = "sendingUserID";
			public static String TIMESTAMP = "timestamp";
		}
		
		public static class Group {
			public static String CHAT = "Chat";
			public static String LISTS = "Lists";
			public static String NAME = "Name";
			public static String OWNER = "Owner";
			public static String USERS = "Users";
		}
		
		public static class UserGroup {
			public static String GROUPS = "Groups";
		}
		
		public static class UserChat {
			public static String SINGLECHATS = "SingleChats";
		}
	}
	
	//The User class encapsulates anything to do with the user using the device.
	//Logging in, out, accessing user data, etc
	public static class User {
		/**
		 * The userInfo map will store the users document data, and is updated every time
		 * any information is changed via a ListenerRegistration.
		 */
		public static final Map<String, Object> userInfo = new HashMap<>();
		
		/**
		 * The following arrays serve to keep a list of the document IDs of each group, friend or chat.
		 * Simular to userInfo, these are updated everytime the user document is changed.
		 */
		public static final ArrayList<String> groups = new ArrayList<>();
		public static final ArrayList<String> friends = new ArrayList<>();
		public static final Map<String, String> chats = new HashMap<>();
		
		/**
		 * We keep a "cache" of the information of each friend, group and chat. This means we can
		 * directly access the document information without having to query everytime.
		 * The information in these maps is updated when the respective document of each key
		 * in the map is changed.
		 */
		public static final Map<String, Map<String, Object>> friendInfo = new HashMap<>();
		public static final Map<String, Map<String, Object>> groupInfo = new HashMap<>();
		public static final Map<String, Map<String, Object>> chatInfo = new HashMap<>();
		
		/**
		 * These runnables are to be called when data in a document changed. By using runnables
		 * we can avoid adding multiple ListenerRegistrations to a Firebase document, by having
		 * just one Listener and then having it call each runnable
		 */
		public static final Map<String, Runnable> userInfoRunnables = new HashMap<>();
		public static final Map<String, Runnable> friendInfoRunnables = new HashMap<>();
		public static final Map<String, Runnable> groupInfoRunnables = new HashMap<>();
		
		/**
		 * Map each listener to it's key (type).
		 */
		final static Map<String, ListenerRegistration> listenerRegistrationMap = new HashMap<>();
		
		
		/**
		 * Initialised basically means if we have logged in to Firebase.
		 */
		public static boolean initiliased = false;
		
		
		/**
		 * Checks if the device user is logged into any account in the Firebase database.
		 *
		 * @return Boolean depending on whether the user is logged in or not.
		 */
		public static boolean isLoggedIn() {
			return auth.getCurrentUser() != null;
		}
		
		/**
		 * Logs the user out of the Firebase database.
		 *
		 * @return A Boolean depending on whether the action completed successfully.
		 */
		public static boolean logout() {
			if (isLoggedIn()) {
				
				//Remove all the Firebase document listeners
				for (String key : listenerRegistrationMap.keySet()) {
					((ListenerRegistration) listenerRegistrationMap.get(key)).remove();
				}
				
				auth.signOut();
				Log.d(LOG.INFORMATION, "Logged out.");
				
				//Clear our caches
				userInfo.clear();
				friendInfo.clear();
				groupInfo.clear();
				chatInfo.clear();
				
				//Clear the runnables for each listener
				userInfoRunnables.clear();
				friendInfoRunnables.clear();
				groupInfoRunnables.clear();
				
				return true;
			} else {
				Log.d(LOG.ERROR, "Cannot logout when not logged in.");
				return false;
			}
		}
		
		/**
		 * Login to the Firebase database.
		 *
		 * @param email    Account email
		 * @param password Account password
		 * @return A Task with a snapshot of the users information document.
		 */
		public static Task<DocumentSnapshot> login(String email, String password) {
			if (!isLoggedIn()) {
				//Attempt to login, then continue by calling getUserInfo
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
				return loginTask;
			} else {
				Log.d(HelperClass.LOG.ERROR, "Already logged in.");
				//If we can't login, then return a task with the result of null
				return Tasks.forResult(null);
			}
		}
		
		/**
		 * Get the information of the currently logged in user.
		 *
		 * @param currentUserID The Unique ID of the user logged in.
		 * @return A Task with a snapshot of the users information document.
		 */
		private static Task<DocumentSnapshot> getUserInfo(String currentUserID) {
			Log.d(HelperClass.LOG.INFORMATION, "Getting user info.");
			return db.collection(Collections.USERS).document(currentUserID).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
				@Override
				public void onComplete(@NonNull Task<DocumentSnapshot> task) {
					//Check to make sure the task completed successfully
					if (task.isSuccessful()) {
						DocumentSnapshot documentSnapshot = (DocumentSnapshot) task.getResult();
						//Make sure that the document we want to get information from actually exists
						if (!documentSnapshot.exists()) {
							Log.d(LOG.ERROR, "user doucment not found");
							return;
						}
						Log.d(HelperClass.LOG.SUCCESS, "User found.");
						
						//Get the user data from the document
						Map<String, Object> userData = documentSnapshot.getData();
						
						Log.d(LOG.INFORMATION, "user data : " + userData.toString());
						
						//Cache the data
						for (String key : userData.keySet()) {
							userInfo.put(key, userData.get(key));
						}
						
						//Set up the document reference listeners
						setupListeners();
						
						Log.d(HelperClass.LOG.INFORMATION, "userInfo: " + userInfo.toString());
					} else {
						Log.d(LOG.ERROR, "Failed to get user info.");
					}
				}
			});
		}
		
		/**
		 * Setup Firebase database listeners for the users profile, users groups and users chats.
		 */
		public static void setupListeners() {
			String userID = auth.getCurrentUser().getUid().toString();
			
			DocumentReference userInfoReference = db.collection(Collections.USERS).document(userID);
			DocumentReference userChatsReference = db.collection("UserChats").document(userID);
			DocumentReference userGroupsReference = db.collection("UserGroups").document(userID);
			
			ListenerRegistration userListener = userInfoReference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
				@Override
				public void onEvent(@Nullable final DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
					Log.d(LOG.INFORMATION, "User Information changed.");
					userInfo.clear();
					userInfo.putAll(documentSnapshot.getData());
					
					List<Task<?>> getFriendUserInfoTasks = new ArrayList<>();
					if (!documentSnapshot.get(Collections.Users.FRIENDS).toString().equals("")) {
						friends.clear();
						friends.addAll((ArrayList<String>) documentSnapshot.get(Collections.Users.FRIENDS));
						
						final ArrayList<String> friends = (ArrayList<String>) documentSnapshot.get(Collections.Users.FRIENDS);
						Log.d(LOG.INFORMATION, "Friends: " + friends.toString());
						
						for (String friend : friends) {
							if (!friendInfo.containsKey(friend)) {
								DocumentReference friendDocumentReference = (DocumentReference) db.collection(Collections.USERS).document(friend);
								
								friendDocumentReference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
									@Override
									public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
										friendInfo.put(documentSnapshot.getId(), documentSnapshot.getData());
										for (String key : friendInfoRunnables.keySet()) {
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
					
					for (String key : userInfoRunnables.keySet()) {
						Runnable runnable = userInfoRunnables.get(key);
						if (runnable != null)
							runnable.run();
					}
					
					Tasks.whenAllComplete(getFriendUserInfoTasks).addOnCompleteListener(new OnCompleteListener<List<Task<?>>>() {
						@Override
						public void onComplete(@NonNull Task<List<Task<?>>> task) {
							List<Task<?>> taskList = (List<Task<?>>) task.getResult();
							for (Task friendInfoTask : taskList) {
								if (friendInfoTask.isSuccessful()) {
									DocumentSnapshot friendUserSnapshot = (DocumentSnapshot) friendInfoTask.getResult();
									//userNames.put(documentSnapshot1.getId(), documentSnapshot1.get("FirstName") + " " + documentSnapshot1.get("LastName"));
									friendInfo.put(friendUserSnapshot.getId(), friendUserSnapshot.getData());
								}
							}
							
							for (String key : userInfoRunnables.keySet()) {
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
					
					if (documentSnapshot.get(Collections.UserGroup.GROUPS).toString().equals(""))
						return;
					
					groups.addAll((ArrayList) documentSnapshot.get(Collections.UserGroup.GROUPS));
					
					ArrayList<String> groups = (ArrayList) documentSnapshot.get(Collections.UserGroup.GROUPS);
					
					Log.d(LOG.INFORMATION, "Group IDs: " + groups.toString());
					
					List<Task<?>> getGroupInfoTasks = new ArrayList<>();
					for (String group : groups) {
						if (!groupInfo.containsKey(group) /*|| true*/) {
							DocumentReference groupDocumentReference = (DocumentReference) db.collection(Collections.GROUPS).document(group);
							//getGroupInfoTasks.add(groupDocumentReference.get());
							
							groupDocumentReference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
								@Override
								public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
									groupInfo.put(documentSnapshot.getId(), documentSnapshot.getData());
									for (String key : groupInfoRunnables.keySet()) {
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
							List<Task<?>> taskList = (List<Task<?>>) task.getResult();
							for (Task groupInfotask : taskList) {
								if (groupInfotask.isSuccessful()) {
									DocumentSnapshot groupSnapshot = (DocumentSnapshot) groupInfotask.getResult();
									groupInfo.put(groupSnapshot.getId(), groupSnapshot.getData());
								}
							}
							
							for (String key : groupInfoRunnables.keySet()) {
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
					if (documentSnapshot.get(Collections.UserChat.SINGLECHATS).toString().equals(""))
						return;
					chats.putAll((Map<String, String>) documentSnapshot.get(Collections.UserChat.SINGLECHATS));
				}
			});
			
			listenerRegistrationMap.put(Collections.USERS, userListener);
			listenerRegistrationMap.put(Collections.GROUPS, groupListener);
			listenerRegistrationMap.put(Collections.CHATS, chatsListener);
		}
		
		/**
		 * Add friend.
		 *
		 * @param friendID User ID of the person to add as a friend
		 * @return Task
		 */
		public static Task<Void> addFriend(final String friendID) {
			DocumentReference userReference = db.collection(Collections.USERS).document(auth.getCurrentUser().getUid());
			DocumentReference friendReference = db.collection(Collections.USERS).document(friendID);
			
			final Map<String, Object> addFriendMap = new HashMap<>();
			addFriendMap.put(Collections.Users.FRIENDS, FieldValue.arrayUnion(friendID));
			Task<Void> addFriendTask = userReference.update(addFriendMap);
			
			final Map<String, Object> addFriendMap2 = new HashMap<>();
			addFriendMap2.put(Collections.Users.FRIENDS, FieldValue.arrayUnion(auth.getCurrentUser().getUid()));
			final Task<Void> addFriendTask2 = friendReference.update(addFriendMap2);
			
			return addFriendTask.continueWithTask(new Continuation<Void, Task<Void>>() {
				@Override
				public Task<Void> then(@NonNull Task<Void> task) throws Exception {
					if (task.isSuccessful()) {
						return addFriendTask2;
					} else {
						Log.d(LOG.ERROR, "Error adding friend: " + task.getException());
						return Tasks.forResult(null);
					}
				}
			});
		}
		
		/**
		 * Remove friend.
		 *
		 * @param friendID User ID of the person to remove as a friend
		 * @return Task
		 */
		public static Task<Void> removeFriend(String friendID) {
			DocumentReference userReference = db.collection(Collections.USERS).document(auth.getCurrentUser().getUid());
			DocumentReference friendReference = db.collection(Collections.USERS).document(friendID);
			
			final Map<String, Object> addFriendMap = new HashMap<>();
			addFriendMap.put(Collections.Users.FRIENDS, FieldValue.arrayRemove(friendID));
			Task<Void> addFriendTask = userReference.update(addFriendMap);
			
			final Map<String, Object> addFriendMap2 = new HashMap<>();
			addFriendMap2.put(Collections.Users.FRIENDS, FieldValue.arrayRemove(auth.getCurrentUser().getUid()));
			Task<Void> addFriendTask2 = friendReference.update(addFriendMap2);
			
			return addFriendTask2;
		}
		
		
	}
	
	/**
	 * Create a new group.
	 *
	 * @param name  Name of the group
	 * @param users ArrayList of members to be added to the group
	 * @return
	 */
	public static Task<Void> createGroup(String name, ArrayList<String> users) {
		//Create a new chat for the group
		DocumentReference chatReference = HelperClass.db.collection(Collections.CHATS).document();
		
		//Set up the group data
		Map<String, Object> groupData = new HashMap<>();
		groupData.put(Collections.Group.CHAT, chatReference.getId());
		groupData.put(Collections.Group.NAME, name);
		groupData.put(Collections.Group.OWNER, auth.getCurrentUser().getUid().toString());
		groupData.put(Collections.Group.USERS, users);
		groupData.put(Collections.Group.LISTS, "");
		
		//Generate a new document ID for the group
		DocumentReference groupReference = HelperClass.db.collection(Collections.GROUPS).document();
		//Set the group data into this new group document
		Task<Void> createGroupTask = groupReference.set(groupData);
		
		//Initialise
		Map<String, Object> chatData = new HashMap<>();
		chatData.put(Collections.Chat.LASTMESSAGETIMESTAMP, 0);
		chatData.put(Collections.Chat.MESSAGES, ""); //empty message to create the field
		chatData.put(Collections.Chat.NAME, name);
		chatData.put(Collections.Chat.USERS, users);
		Task<Void> createChatReference = chatReference.set(chatData);
		
		for (String userID : users) {
			DocumentReference userGroupReference = (DocumentReference) HelperClass.db.collection(Collections.USERGROUPS).document(userID);
			final Map<String, Object> addGroupMap = new HashMap<>();
			addGroupMap.put(Collections.USERGROUPS, FieldValue.arrayUnion(groupReference.getId()));
			Task<Void> addFriendTask = userGroupReference.update(addGroupMap);
		}
		
		return createGroupTask;
		
	}
	
	/**
	 * @param userID           User ID
	 * @param firstName        First name of user
	 * @param lastName         Last name of user
	 * @param dateOfBirth      Not used
	 * @param location
	 * @param email            Email account user is registering with
	 * @param password         Not used
	 * @param securityQuestion Security question
	 * @param answer           Security answer
	 * @return Task
	 */
	public static Task<Void> createUser(String userID, String firstName, String lastName, String dateOfBirth, String location, String email, String password, String securityQuestion, String answer) {
		DocumentReference newUserReference = db.collection(Collections.USERS).document(userID);
		DocumentReference newUserChatsReference = db.collection(Collections.USERCHATS).document(userID);
		DocumentReference newUserGroupsReference = db.collection(Collections.USERGROUPS).document(userID);
		
		List<Task<?>> tasks = new ArrayList<>();
		
		//User data map
		Map<String, Object> userData = new HashMap<>();
		userData.put(Collections.Users.FIRSTNAME, firstName);
		userData.put(Collections.Users.LASTNAME, lastName);
		userData.put(Collections.Users.LOCATION, location);
		userData.put(Collections.Users.EMAIL, email);
		userData.put(Collections.Users.SECURITYQUESTION, securityQuestion);
		userData.put(Collections.Users.SECURITYANSWER, answer);
		userData.put(Collections.Users.DELETED, false);
		userData.put(Collections.Users.FRIENDS, "");
		
		Map<String, Object> userChatData = new HashMap<>();
		userChatData.put(Collections.UserChat.SINGLECHATS, "");
		
		Map<String, Object> userGroupsData = new HashMap<>();
		userGroupsData.put(Collections.UserGroup.GROUPS, "");
		
		tasks.add(newUserReference.set(userData));
		tasks.add(newUserChatsReference.set(userChatData));
		tasks.add(newUserGroupsReference.set(userGroupsData));
		
		return Tasks.whenAll(tasks);
	}
	
}
