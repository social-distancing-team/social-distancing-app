package com.social_distancing.app;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;

import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.social_distancing.app.HelperClass.Collections;
import com.social_distancing.app.HelperClass.User;
import com.social_distancing.app.HelperClass.LOG;

import org.w3c.dom.Document;
import org.w3c.dom.Text;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Examples_and_testing extends AppCompatActivity {
	
	public static final String email =  "passwordispassword@email.com";
	public static final String password = "password";
	
	public static final Map<String, ListenerRegistration> listeners= new HashMap<>();
	
	public static final Map<String, ListenerRegistration> listListeners = new HashMap<>();
	public static final Map<String, LinearLayout> listLayouts = new HashMap<>();
	
	public static final Map<String, ListenerRegistration> listItemListeners = new HashMap<>();
	public static final Map<String, LinearLayout> listItemLayouts = new HashMap<>();
	
	public static final Map<String, View> listViews = new HashMap<>();
	
	
	public final Context context = this;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_examples_and_testing);
		
		LinearLayout linearLayout =  (LinearLayout)findViewById(R.id.rootLayout);
		//linearLayout.setBackgroundColor(Color.RED);
		
		if (User.isLoggedIn())
			User.logout();
		Log.d("TEST", "TEST");
		tryLogin();
	}
	
	@Override
	protected void onStart() {
		super.onStart();
	}
	
	void tryLogin(){
		HelperClass.User.login(email, password).addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
			@Override
			public void onComplete(@NonNull Task<DocumentSnapshot> task) {
				if (task.isSuccessful() && task.getResult() != null){
					DocumentSnapshot documentSnapshot = (DocumentSnapshot)task.getResult(); //userData document
					ArrayList<String> lists = User.getLists();
					
					setupListListeners();
					
					Log.d(LOG.INFORMATION, "Lists: " + lists.toString());
				}
			}
		});
	}
	
	void setupListeners(){
	
	}
	
	static boolean ready = false;
	void setupListListeners(){
		//ready = true;
		String userID = HelperClass.auth.getCurrentUser().getUid();
		
		for (String key : listListeners.keySet()){
			listListeners.get(key).remove();
		}
		
		//if (listListeners.containsKey(userID))
		//	return;
		
		ListenerRegistration registration = HelperClass.db.collection(Collections.USERLISTS).document(HelperClass.auth.getCurrentUser().getUid()).addSnapshotListener(new EventListener<DocumentSnapshot>() {
			@Override
			public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
				
				LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				final LinearLayout rootLayout = (LinearLayout)findViewById(R.id.rootLayout);
				
				DocumentSnapshot userListsDocument = (DocumentSnapshot)documentSnapshot;
				ArrayList<String> lists = (ArrayList<String>)documentSnapshot.get(Collections.UserLists.LISTS);
				
				for (String list : lists){
					//updated lists
					if (listLayouts.containsKey(list)){
						//current lists
					} else {
						//the list has been added
						View newList = inflater.inflate(R.layout.item_list_list, null);
						listViews.put(list, newList);
						rootLayout.addView(newList);
					}
				}
				
				ArrayList<String> keysToRemove = new ArrayList<>();
				for (String key : listLayouts.keySet()){
					// keys are existing lists
					if (lists.contains(key)){
						//if the the list already exists
					} else {
						//the list has been deleted
						//listListeners.get(key).remove();
						keysToRemove.add(key);
					}
				}
				
				for (String key : keysToRemove){
					LinearLayout linearLayout = (LinearLayout)listLayouts.get(key);
					linearLayout.setBackgroundColor(Color.BLUE);
					rootLayout.removeView(listViews.get(key));
					listItemListeners.get(key).remove();
				}
				
				listItemListeners.clear();
				
				for (String list : lists){
					Log.d(LOG.INFORMATION, "Got list: " + list);
					
					ListenerRegistration registration = HelperClass.db.collection(Collections.LISTS).document(list).addSnapshotListener(new EventListener<DocumentSnapshot>() {
						@Override
						public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
							
							DocumentSnapshot listItemDocument = (DocumentSnapshot)documentSnapshot;
							String list = documentSnapshot.getId();
							String title = documentSnapshot.get(Collections.Lists.NAME).toString();
							
							View itemView = listViews.get(list);
							rootLayout.removeView(itemView);
							
							((TextView)itemView.findViewById(R.id.listTitle)).setText(title);
							((TextView)itemView.findViewById(R.id.listTimestamp)).setText("02/02/2002");
							
							Map<String, Object> listData = listItemDocument.getData();
							Log.d(LOG.INFORMATION, "Got list data: " + listData.toString());
							
							ArrayList<String> items = (ArrayList<String>)listData.get(Collections.Lists.ITEMS);
							for (String item : items){
							}
							
							
							rootLayout.addView(itemView,0);
						}
					});
					
					listItemListeners.put(list, registration);
				}
			}
		});
		
		listListeners.put(userID, registration);
	}
	
	
	void setupListListeners2(){
		String userID = HelperClass.auth.getCurrentUser().getUid();
		
		if (listListeners.containsKey(userID))
			return;
		
		ListenerRegistration registration = HelperClass.db.collection(Collections.USERLISTS).document(HelperClass.auth.getCurrentUser().getUid()).addSnapshotListener(new EventListener<DocumentSnapshot>() {
			@Override
			public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
				
				LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				LinearLayout rootLayout = (LinearLayout)findViewById(R.id.rootLayout);
				
				DocumentSnapshot userListsDocument = (DocumentSnapshot)documentSnapshot;
				ArrayList<String> lists = (ArrayList<String>)documentSnapshot.get(Collections.UserLists.LISTS);
				
				for (String list : lists){
					//updated lists
					if (listLayouts.containsKey(list)){
						//current lists
					} else {
						//the list has been added
						View newList = inflater.inflate(R.layout.listlayout, null);
						LinearLayout rootListLayout = (LinearLayout)newList.findViewById(R.id.listlayout_root);
						final LinearLayout listItemsLayout = (LinearLayout)newList.findViewById(R.id.listlayout_items_layout);
						
						listLayouts.put(list, rootListLayout);
						listItemLayouts.put(list, listItemsLayout);
						listViews.put(list, newList);
						
						//rootListLayout.setBackgroundColor(Color.GREEN);
						
						rootLayout.addView(newList);
					}
				}
				
				ArrayList<String> keysToRemove = new ArrayList<>();
				for (String key : listLayouts.keySet()){
					// keys are existing lists
					if (lists.contains(key)){
						//if the the list already exists
					} else {
						//the list has been deleted
						//listListeners.get(key).remove();
						keysToRemove.add(key);
					}
				}
				
				for (String key : keysToRemove){
					LinearLayout linearLayout = (LinearLayout)listLayouts.get(key);
					linearLayout.setBackgroundColor(Color.BLUE);
					rootLayout.removeView(listViews.get(key));
					//rootLayout.removeView(linearLayout);
					listItemListeners.get(key).remove();
					listLayouts.remove(key);
					listItemLayouts.remove(key);
				}
				
				/*
				for (String key : listItemListeners.keySet()){
					listItemListeners.get(key).remove();
				}
				 */
				
				listItemListeners.clear();
				
				//rootLayout.removeAllViews();
				
				for (String list : lists){
					Log.d(LOG.INFORMATION, "Got list: " + list);
					
					ListenerRegistration registration = HelperClass.db.collection(Collections.LISTS).document(list).addSnapshotListener(new EventListener<DocumentSnapshot>() {
						@Override
						public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
							
							DocumentSnapshot listItemDocument = (DocumentSnapshot)documentSnapshot;
							String list = documentSnapshot.getId();
							String title = "";
							if (documentSnapshot.get(Collections.Lists.NAME) != null)
								title = documentSnapshot.get(Collections.Lists.NAME).toString();
							
							((TextView)listViews.get(list).findViewById(R.id.listlayout_title)).setText(title);
							
							LinearLayout layout = (LinearLayout)listItemLayouts.get(list);
							layout.removeAllViews();
							
							Map<String, Object> listData = listItemDocument.getData();
							Log.d(LOG.INFORMATION, "Got list data: " + listData.toString());
							
							ArrayList<String> items = (ArrayList<String>)listData.get(Collections.Lists.ITEMS);
							for (String item : items){
								TextView itemText = new TextView(context);
								itemText.setText(item);
								layout.addView(itemText);
							}
						}
					});
					
					listItemListeners.put(list, registration);
				}
			}
		});
		
		listListeners.put(userID, registration);
	}
	
	void setupListeners(DocumentSnapshot userDocumentSnapshot){
		Map<String, Object> userData = userDocumentSnapshot.getData();
		DocumentReference userDocumentReference = userDocumentSnapshot.getReference();
		
		ArrayList<String> lists = (ArrayList<String>)userData.get(Collections.Users.LISTS);
		
		for (final String list : lists){
			if (listeners.containsKey(list)){
				continue;
			}
			
			ListenerRegistration registration = userDocumentReference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
				@Override
				public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
					//User document has changed -> so when any field is changed
					
					
					
					TextView textView = new TextView(context);
					textView.setText(list);
					//linearLayout.addView(textView);
				}
			});
			
			
			
			listeners.put(list, registration);
		}
	}
	
	void setupUI(){
		//LinearLayout linearLayout = (LinearLayout)findViewById(R.id.rootLayout);
		
	}
	
}
