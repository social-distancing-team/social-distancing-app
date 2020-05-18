package com.social_distancing.app;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseUser;


import androidx.annotation.NonNull;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


import com.google.android.gms.tasks.Continuation;


import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;

import com.social_distancing.app.HelperClass.LOG;
import com.social_distancing.app.HelperClass.COLLECTION;
import com.social_distancing.app.HelperClass.USER;
import com.social_distancing.app.HelperClass.LIST;

public class TesterActivity extends AppCompatActivity {

	Context context = this;
	
	final Map<String, ListenerRegistration> listenerRegistrations = new HashMap<>();
	
	public static final FirebaseAuth auth = FirebaseAuth.getInstance();
	public static final FirebaseFirestore db = FirebaseFirestore.getInstance();
	
	protected void disable(){
		final Window window = getWindow();
		window.setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
	}
	
	protected void enable(){
		final Window window = getWindow();
		window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_tester);
		
		if (auth.getCurrentUser() != null){
			Log.d(LOG.WARNING, "Already logged in. Logging out.");
			auth.signOut();
		}
		
		final LinearLayout rootLayout = (LinearLayout)findViewById(R.id.rootLayout);
		
		final Map<String, Object> userData = new HashMap<>();
		final TextView firstNameLabel = new TextView(this);
		final TextView lastNameLabel = new TextView(this);
		
		final Spinner listSpinner = new Spinner(this);
		listSpinner.setAdapter(new ArrayAdapter<String>(context, android.R.layout.simple_spinner_dropdown_item));
		final Spinner listItemSpinner = new Spinner(this);
		
		rootLayout.addView(firstNameLabel);
		rootLayout.addView(lastNameLabel);
		
		
		Button changeActivity = new Button(this);
		changeActivity.setText("Change activity");
		changeActivity.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				//final Intent k = new Intent(context, UserPage.class);
				//startActivity(k);
			}
		});
		
		rootLayout.addView(changeActivity);
		
		final LinearLayout listLayout = new LinearLayout(this);
		listLayout.setVisibility(View.GONE);
		
		final LinearLayout listLayoutForItems = new LinearLayout(this);
		listLayoutForItems.setOrientation(LinearLayout.VERTICAL);
		
		final Button button = new Button(this);
		final Button signupButton = new Button(this);
		
		button.setText("Login");
		button.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (auth.getCurrentUser() == null){
					disable();
					
					auth.signInWithEmailAndPassword("passwordispassword@email.com", "password").addOnCompleteListener(new OnCompleteListener<AuthResult>() {
						@Override
						public void onComplete(@NonNull Task<AuthResult> task) {
							if (task.isSuccessful()){
								AuthResult authResult = (AuthResult)task.getResult();
								Log.d(LOG.SUCCESS, "Logged in: " + authResult.getUser().getUid());
								button.setVisibility(View.GONE);
								signupButton.setVisibility(View.GONE);
							} else {
								Log.d(LOG.ERROR, "Failed to login. " + task.getException().getMessage());
							}
						}
					}).continueWithTask(new Continuation<AuthResult, Task<DocumentSnapshot>>() {
						@Override
						public Task<DocumentSnapshot> then(@NonNull Task<AuthResult> task) throws Exception {
							if (task.isSuccessful()){
								AuthResult authResult = (AuthResult)task.getResult();
								FirebaseUser currentUser = authResult.getUser();
								String currentUserID = currentUser.getUid();
								
								Log.d(LOG.INFORMATION, "Now getting user document.");
								
								return db.collection(COLLECTION.USERS).document(currentUserID).get();
							} else {
								return null;
							}
						}
					}).addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
						@Override
						public void onComplete(@NonNull Task<DocumentSnapshot> task) {
							if (task != null){
								if (task.isSuccessful()){
									DocumentSnapshot documentSnapshot = (DocumentSnapshot)task.getResult();
									if (documentSnapshot.exists()){
										Log.d(LOG.SUCCESS, "User document found.");
										Map<String, Object> mapData = documentSnapshot.getData();
										for (String key : mapData.keySet()){
											userData.put(key, mapData.get(key));
										}
										Log.d(LOG.INFORMATION, userData.toString());
										//firstNameLabel.setText(mapData.get(USER.FIRSTNAME).toString());
										//lastNameLabel.setText(mapData.get(USER.LASTNAME).toString());
										
										firstNameLabel.setText("Welcome User Name");
										
										ArrayAdapter<String> adapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_dropdown_item);
										adapter.addAll((ArrayList<String>)userData.get(USER.LISTS));
										listSpinner.setAdapter(adapter);
										
										listLayout.setVisibility(View.VISIBLE);
										
										final String key = "lists";
										
										if (listenerRegistrations.containsKey(key)){
											listenerRegistrations.get(key).remove();
										}
										
										ListenerRegistration registration = documentSnapshot.getReference().addSnapshotListener(new EventListener<DocumentSnapshot>() {
											@Override
											public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
												Log.d(LOG.INFORMATION, "User document snapshot listener has been called. ID:" + documentSnapshot.getId() + ".");
												ArrayList<String> items = (ArrayList<String>)documentSnapshot.get(USER.LISTS);
												ArrayAdapter<String> adapter = ((ArrayAdapter<String>)listSpinner.getAdapter());
												if (adapter != null){
													adapter.clear();
													adapter.addAll(items);
													
												} else {
													Log.d(LOG.ERROR, key + " ArrayAdapter is null.");
													ArrayAdapter<String> newAdapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_dropdown_item);
													newAdapter.addAll(items);
													listSpinner.setAdapter(newAdapter);
												}
											}
										});
										
										listenerRegistrations.put(key, registration);
										
									} else {
										Log.d(LOG.WARNING, "User document does not exist.");
									}
								} else {
									Log.d(LOG.ERROR, "Failed to get user document.");
								}
							} else {
							
							}
							enable();
						}
					});
				} else{
					Log.d(LOG.ERROR, "Already logged in.");
				}
			}
		});
		
		
		signupButton.setText("Sign up");
		signupButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (auth.getCurrentUser() != null){
					Log.d(LOG.ERROR, "Cannot create new user when signed in.");
					return;
				}
				
				auth.createUserWithEmailAndPassword("testing@test.com", "test12345").continueWithTask(new Continuation<AuthResult, Task<Void>>() {
					@Override
					public Task<Void> then(@NonNull Task<AuthResult> task) throws Exception {
						if (task.isSuccessful()){
							Log.d(LOG.SUCCESS, "Successfully created user.");
							
							Map<String, Object> mapData = new HashMap<>();
							mapData.put(USER.FIRSTNAME, "FirstName");
							mapData.put(USER.LASTNAME, "LastName");
							mapData.put(USER.EMAIL, "SomeEmail@email.com");
							mapData.put(USER.FRIENDS, new ArrayList<String>());
							mapData.put(USER.LISTS, new ArrayList<String>());
							
							DocumentReference newUserDocument = db.collection(COLLECTION.USERS).document();
							
							Log.d(LOG.INFORMATION, "Adding new user to Users collection.");
							
							return newUserDocument.set(mapData);
						} else {
							Log.d(LOG.ERROR, "Failed to create user. " + task.getException().getMessage());
						}
						return Tasks.forResult(null);
					}
				}).addOnCompleteListener(new OnCompleteListener<Void>() {
					@Override
					public void onComplete(@NonNull Task<Void> task) {
						if (task.getResult() != null){
							if (task.isSuccessful()){
								Log.d(LOG.SUCCESS, "Successfully added user to Users collection.");
							} else {
								Log.d(LOG.ERROR, "Failed to add user to Users collection. " + task.getException().getMessage());
							}
						}
					}
				});
			}
		});
		
		Button listButton = new Button(this);
		listButton.setText("list Stuff");
		listButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
			
			}
		});
		
		
		Button signoutButton = new Button(this);
		signoutButton.setText("Log out");
		signoutButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (auth.getCurrentUser() != null){
					auth.signOut();
					Log.d(LOG.INFORMATION, "Logged out.");
					listLayout.setVisibility(View.GONE);
					button.setVisibility(View.VISIBLE);
					signupButton.setVisibility(View.VISIBLE);
				} else{
					Log.d(LOG.ERROR, "Cannot logout when not logged in.");
				}
			}
		});
		
		rootLayout.addView(button);
		rootLayout.addView(signupButton);
		rootLayout.addView(signoutButton);
		
		
		listLayout.setOrientation(LinearLayout.VERTICAL);
		
		Button createList = new Button(this);
		createList.setText("Create List");
		createList.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (auth.getCurrentUser() == null) {
					Log.d(LOG.ERROR, "Cannot create list. Not logged in.");
					return;
				}
				
				Log.d(LOG.INFORMATION, "Creating new List.");
				final DocumentReference newListDocument = db.collection(COLLECTION.LISTS).document();
				Map<String, Object> listData = new HashMap<>();
				listData.put(LIST.USERS, new ArrayList<String>(Arrays.asList(auth.getCurrentUser().getUid())));
				listData.put(LIST.ITEMS, new ArrayList<String>());
				
				newListDocument.set(listData).continueWithTask(new Continuation<Void, Task<Void>>() {
					@Override
					public Task<Void> then(@NonNull Task<Void> task) throws Exception {
						if (task.isSuccessful()){
							Log.d(LOG.SUCCESS, "Successfully created list.");
							DocumentReference userDocument = db.collection(COLLECTION.USERS).document(auth.getCurrentUser().getUid());
							Log.d(LOG.INFORMATION, "Adding list to user document.");
							//return userDocument.update(USER.LISTS, new ArrayList<String>(Arrays.asList(newListDocument.getId())));
							return userDocument.update(USER.LISTS, FieldValue.arrayUnion(newListDocument.getId()));
						} else {
							Log.d(LOG.ERROR, "Failed to create list. " + task.getException().getMessage());
							return null;
						}
					}
				}).addOnCompleteListener(new OnCompleteListener<Void>() {
					@Override
					public void onComplete(@NonNull Task<Void> task) {
						if (task != null){
							if (task.isSuccessful()){
								Log.d(LOG.SUCCESS, "Successfully added list to user document.");
								ArrayAdapter<String> adapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_dropdown_item);
								ArrayList<String> lists = (ArrayList<String>)userData.get(USER.LISTS);
								lists.add(newListDocument.getId());
								adapter.addAll(lists);
								listSpinner.setAdapter(adapter);
							} else {
								Log.d(LOG.ERROR, "Failed to add list to user document. " + task.getException().getMessage());
							}
						} else {
							Log.d(LOG.ERROR, "Recieved null.");
						}
					}
				});
				
			}
		});
		
		final EditText listItemValue = new EditText(this);
		listItemValue.setHint("List item value");
		
		Button addItemToList = new Button(this);
		addItemToList.setText("Add item to list");
		addItemToList.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (auth.getCurrentUser() == null) {
					Log.d(LOG.ERROR, "Cannot add item to list. Not logged in.");
					return;
				}
				
				DocumentReference userDocumentReference = db.collection(COLLECTION.USERS).document(auth.getCurrentUser().getUid());
				userDocumentReference.get().continueWithTask(new Continuation<DocumentSnapshot, Task<Void>>() {
					@Override
					public Task<Void> then(@NonNull Task<DocumentSnapshot> task) throws Exception {
						if (task.isSuccessful()){
							DocumentSnapshot userDocument = (DocumentSnapshot)task.getResult();
							ArrayList<String> userLists = (ArrayList<String>)userDocument.get(USER.LISTS);
							if (userLists.size() == 0){
								Log.d(LOG.ERROR, "User has no lists.");
								return null;
							} else {
								String listID = userLists.get(0);
								listID = listSpinner.getSelectedItem().toString();
								
								DocumentReference listReference = db.collection(COLLECTION.LISTS).document(listID);
								Log.d(LOG.INFORMATION, "Attempting to add item to list.");
								return listReference.update(LIST.ITEMS, FieldValue.arrayUnion(listItemValue.getText().toString()));
							}
						} else {
							Log.d(LOG.ERROR, "Failed to get user document. Perhaps it was deleted? " + task.getException().getMessage());
							return null;
						}
					}
				}).addOnCompleteListener(new OnCompleteListener<Void>() {
					@Override
					public void onComplete(@NonNull Task<Void> task) {
						if (task != null){
							if (task.isSuccessful()){
								Log.d(LOG.SUCCESS, "Successfully added item to list document.");
								
							} else {
								Log.d(LOG.ERROR, "Failed to add item to list document. " + task.getException().getMessage());
							}
						}else {
							Log.d(LOG.ERROR, "Recieved null.");
						}
					}
				});
				
			}
		});
		
		TextView chooseList = new TextView(this);
		chooseList.setText("Choose a list:");
		
		TextView addItem = new TextView(this);
		addItem.setPadding(0,100,0,0);
		addItem.setText("Add new item to list:");
		
		TextView listItems = new TextView(this);
		listItems.setText("List items:");
		
		listLayout.addView(createList);
		listLayout.addView(chooseList);
		listLayout.addView(listSpinner);
		listLayout.addView(listItems);
		listLayout.addView(listLayoutForItems);
		//listLayout.addView(listItemSpinner);
		listLayout.addView(addItem);
		listLayout.addView(listItemValue);
		listLayout.addView(addItemToList);
		
		final Button deleteList = new Button(this);
		deleteList.setText("Delete list");
		deleteList.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (listSpinner.getSelectedItemPosition() == AdapterView.INVALID_POSITION)
					return;
				
				final String listID = listSpinner.getSelectedItem().toString();
				DocumentReference listDocument = db.collection(COLLECTION.LISTS).document(listID);
				
				listDocument.delete().continueWithTask(new Continuation<Void, Task<Void>>() {
					@Override
					public Task<Void> then(@NonNull Task<Void> task) throws Exception {
						if (task.isSuccessful()){
							DocumentReference userDocument = db.collection(COLLECTION.USERS).document(auth.getCurrentUser().getUid());
							Log.d(LOG.INFORMATION, "Attempting to delete list item from users list array.");
							return userDocument.update(USER.LISTS, FieldValue.arrayRemove(listID));
						} else {
							Log.d(LOG.ERROR, "Failed at deleting list document.");
							return null;
						}
					}
				}).addOnCompleteListener(new OnCompleteListener<Void>() {
					@Override
					public void onComplete(@NonNull Task<Void> task) {
						if (task != null){
							if (task.isSuccessful()){
								ArrayAdapter<String> adapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_dropdown_item);
								ArrayList<String> lists = (ArrayList<String>)userData.get(USER.LISTS);
								lists.remove(listID);
								adapter.addAll(lists);
								listSpinner.setAdapter(adapter);
								Log.d(LOG.SUCCESS, "Successfully removed list document and list from user document.");
							} else {
								Log.d(LOG.ERROR, "Failed to delete list from user document. " + task.getException().getMessage());
							}
						}
					}
				});
			}
		});
		
		listSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				String listID = listSpinner.getItemAtPosition(position).toString();
				DocumentReference listDocument = db.collection(COLLECTION.LISTS).document(listID);
				
				String key = "list";
				
				if (listenerRegistrations.containsKey(key)){
					listenerRegistrations.get(key).remove();
				}
				
				ListenerRegistration registration = listDocument.addSnapshotListener(new EventListener<DocumentSnapshot>() {
					@Override
					public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
						Log.d(LOG.INFORMATION, "List document snapshot listener has been called. ID:" + documentSnapshot.getId() + ".");
						ArrayList<String> items = (ArrayList<String>)documentSnapshot.get(LIST.ITEMS);
						
						listLayoutForItems.removeAllViews();
						if (items == null){
							return;
						}
						for (String item : items){
							TextView textView = new TextView(context);
							textView.setText(item);
							textView.setTextSize(20);
							listLayoutForItems.addView(textView);
						}
						
						ArrayAdapter<String> adapter = ((ArrayAdapter<String>)listItemSpinner.getAdapter());
						if (adapter != null){
							adapter.clear();
							adapter.addAll(items);
							
						} else {
							Log.d(LOG.ERROR, "ArrayAdapter is null.");
							ArrayAdapter<String> newAdapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_dropdown_item);
							newAdapter.addAll(items);
							listItemSpinner.setAdapter(newAdapter);
						}
					}
				});
				
				listenerRegistrations.put(key, registration);
				
				Log.d(LOG.INFORMATION, "Getting list document.");
				listDocument.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
					@Override
					public void onComplete(@NonNull Task<DocumentSnapshot> task) {
						if (task.isSuccessful()){
							Log.d(LOG.SUCCESS, "Successfully got list document.");
							if (true)
								return;
							DocumentSnapshot documentSnapshot = (DocumentSnapshot)task.getResult();
							ArrayList<String> listItems = (ArrayList<String>)documentSnapshot.get(LIST.ITEMS);
							ArrayAdapter<String> adapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_dropdown_item);
							adapter.addAll(listItems);
							listItemSpinner.setAdapter(adapter);
						} else {
							Log.d(LOG.ERROR, "Failed to get list document. " + task.getException().getMessage());
						}
					}
				});
			}
			
			@Override
			public void onNothingSelected(AdapterView<?> parent) {
				Log.d(LOG.WARNING, "nothing selected");
			}
		});
		
		listLayout.addView(deleteList);
		listLayout.setPadding(0, 50, 0,0 );
		rootLayout.addView(listLayout);
	}
}
