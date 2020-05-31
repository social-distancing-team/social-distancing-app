package com.social_distancing.app;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.rpc.Help;
import com.social_distancing.app.HelperClass;
import com.social_distancing.app.HelperClass.Collections;
import com.social_distancing.app.HelperClass.LOG;
import com.social_distancing.app.HelperClass.User;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ViewUserProfile extends AppCompatActivity {
	
	final Context context = this;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_view_user_profile);
		
		//((LinearLayout)findViewById(R.id.rootLayout)).setVisibility(View.GONE);
		
		if (!User.isLoggedIn() || !User.initiliased) {
			User.logout();
			User.login(null, null).addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
				@Override
				public void onComplete(@NonNull Task<DocumentSnapshot> task) {
					if (task.getResult() != null && task.isSuccessful()) {
						start();
					}
				}
			});
		} else {
			
			start();
		}
	}
	
	void setup(String uID){
		DocumentReference userInfoReference = (DocumentReference)HelperClass.db.collection(Collections.USERS).document(uID);
		userInfoReference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
			@Override
			public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
				//Log.d(LOG.INFORMATION, "AAAAAAAAAAAAAAAAAAAAA");
				ArrayList<String> newFriends = (ArrayList<String>)documentSnapshot.get(Collections.Users.FRIENDS);
				LinearLayout friendsLayout = (LinearLayout)findViewById(R.id.friendsLayout);
				friendsLayout.removeAllViews();
				
				ArrayList<Task<?>> tasks = new ArrayList<>();
				
				for (final String friend : newFriends){
					final TextView textView = new TextView(context);
					textView.setText(friend);
					friendsLayout.addView(textView);
					
					Task<?> task1 = User.getProfileInfo(friend).addOnCompleteListener(new OnCompleteListener<Map<String, Object>>() {
						@Override
						public void onComplete(@NonNull Task<Map<String, Object>> task) {
							if (task.getResult() != null){
								Map<String, Object> userInfo = (Map<String, Object>)task.getResult();
								
								if (User.friends.contains(friend)) {
									//textView.setTextColor(Color.GREEN);
								}
								
								textView.setText(userInfo.get(Collections.Users.FIRSTNAME).toString() + " " + userInfo.get(Collections.Users.LASTNAME).toString());
								textView.setPadding(0, 0, 0, 10);
								
								if (friend == HelperClass.auth.getCurrentUser().getUid())
									return;
								
								textView.setOnClickListener(new View.OnClickListener() {
									@Override
									public void onClick(View v) {
										final Intent intent = new Intent(context, ViewUserProfile.class);
										intent.putExtra("userID", friend);
										startActivity(intent);
										String userID = getIntent().getStringExtra("userID");
										if (userID != null && userID.equals(HelperClass.auth.getCurrentUser().getUid()) == false) {
											Log.d(LOG.WARNING, "Calling finish.");
											Log.d(LOG.WARNING, "Current ID: " + HelperClass.auth.getCurrentUser().getUid() + ", User ID: " + userID);
											finish();
										}
										//finish();
									}
								});
								
								
								
							}
						}
					});
					tasks.add(task1);
					Tasks.whenAllComplete(tasks).addOnCompleteListener(new OnCompleteListener<List<Task<?>>>() {
						@Override
						public void onComplete(@NonNull Task<List<Task<?>>> task) {
							
							((LinearLayout)findViewById(R.id.rootLayout)).setVisibility(View.VISIBLE);
						}
					});
				}
				
			}
		});
	}
	
	void start(){
		
		final TextView userFullName = (TextView)findViewById(R.id.userFullName);
		final TextView joinDateInfo = (TextView)findViewById(R.id.joinDateInfo);
		final TextView lastSeenInfo = (TextView)findViewById(R.id.lastSeenInfo);
		final Button friendActionButton = (Button)findViewById(R.id.friendActionButton);
		
		final LinearLayout friendsLayout = (LinearLayout)findViewById(R.id.friendsLayout);
		
		String userID = getIntent().getStringExtra("userID");
		if (userID == null){
			userID = "objai3tCzJLXzuJYXFNU";
			userID = HelperClass.auth.getCurrentUser().getUid();
		}
		
		final String finalUserID = userID;
		
		
		/*
		final ArrayList<String> friends = (ArrayList<String>)User.userInfo.get(Collections.Users.FRIENDS);
		if (friends.contains(userID)){
			friendActionButton.setText("Remove friend");
		} else {
			friendActionButton.setText("Add friend");
			friendActionButton.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					User.addFriend(finalUserID);
				}
			});
		}*/
		
		/*
		User.getFriends().addOnCompleteListener(new OnCompleteListener<ArrayList<String>>() {
			@Override
			public void onComplete(@NonNull Task<ArrayList<String>> task) {
				if (task.isSuccessful()){
					ArrayList<String> friends = (ArrayList<String>)task.getResult();
					if (friends.contains(finalUserID)){
						friendActionButton.setText("Remove friend");
					} else {
						friendActionButton.setText("Add friend");
						friendActionButton.setOnClickListener(new View.OnClickListener() {
							@Override
							public void onClick(View v) {
								User.addFriend(finalUserID);
							}
						});
					}
				}
			}
		});
		 */
		ArrayList<String> friends = User.friends;
		
		if (User.friends.contains(finalUserID)) {
			friendActionButton.setText("Remove friend");
		} else {
			friendActionButton.setText("Add friend");
		}
		
		friendActionButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(final View v) {
				Log.d(LOG.INFORMATION, "Friends2: " + User.friends.toString());
				if (User.friends.contains(finalUserID)){
					friendActionButton.setText("Remove friend");
					Log.d(LOG.INFORMATION, "Removing friend.");
					v.setEnabled(false);
					User.removeFriend(finalUserID).addOnCompleteListener(new OnCompleteListener<Void>() {
						@Override
						public void onComplete(@NonNull Task<Void> task) {
							if (task.isSuccessful()){
								friendActionButton.setText("Add friend");
							}
							v.setEnabled(true);
						}
					});
				} else {
					friendActionButton.setText("Add friend");
					Log.d(LOG.INFORMATION, "Adding friend.");
					v.setEnabled(false);
					User.addFriend(finalUserID).addOnCompleteListener(new OnCompleteListener<Void>() {
						@Override
						public void onComplete(@NonNull Task<Void> task) {
							if (task.isSuccessful()){
								friendActionButton.setText("Remove friend");
								//friendActionButton.setOnClickListener();
							}
							v.setEnabled(true);
						}
					});
				}
			}
		});
		
		/*
		if (User.friends.contains(finalUserID)){
			friendActionButton.setText("Remove friend");
			friendActionButton.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(final View v) {
					Log.d(LOG.INFORMATION, "Removing friend.");
					v.setEnabled(false);
					User.removeFriend(finalUserID).addOnCompleteListener(new OnCompleteListener<Void>() {
						@Override
						public void onComplete(@NonNull Task<Void> task) {
							if (task.isSuccessful()){
								friendActionButton.setText("Add friend");
							}
							v.setEnabled(true);
						}
					});
				}
			});
		} else {
			friendActionButton.setText("Add friend");
			friendActionButton.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(final View v) {
					Log.d(LOG.INFORMATION, "Adding friend.");
					v.setEnabled(false);
					User.addFriend(finalUserID).addOnCompleteListener(new OnCompleteListener<Void>() {
						@Override
						public void onComplete(@NonNull Task<Void> task) {
							if (task.isSuccessful()){
								friendActionButton.setText("Remove friend");
								//friendActionButton.setOnClickListener();
							}
							v.setEnabled(true);
						}
					});
				}
			});
		}
		*/
		
		User.getProfileInfo(userID).addOnCompleteListener(new OnCompleteListener<Map<String, Object>>() {
			@Override
			public void onComplete(@NonNull Task<Map<String, Object>> task) {
				if (task.getResult() != null && task.isSuccessful()){
					Map<String, Object> userInfo = (Map<String, Object>)task.getResult();
					
					SimpleDateFormat sfd = new SimpleDateFormat("dd-MM-yyyy");
					
					userFullName.setText(userInfo.get(Collections.Users.FIRSTNAME).toString() + " " + userInfo.get(Collections.Users.LASTNAME).toString());
					//joinDateInfo.setText("Joined on " + sfd.format(((Timestamp)userInfo.get("Joined")).toDate()));
					//lastSeenInfo.setText("Last seen online on " + sfd.format(((Timestamp)userInfo.get("LastSeen")).toDate()));
					
					
					Log.d(LOG.INFORMATION, User.userInfo.toString());
					
					/*
					if (finalUserID == HelperClass.auth.getCurrentUser().getUid()){
						DocumentReference userInfoReference = (DocumentReference)HelperClass.db.collection(Collections.USERS).document(HelperClass.auth.getCurrentUser().getUid());
						userInfoReference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
							@Override
							public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
								Log.d(LOG.INFORMATION, "User Information changed.");
								friends.clear();
								ArrayList<String> newFriends = (ArrayList<String>)documentSnapshot.get(Collections.Users.FRIENDS);
								friends.addAll(newFriends);
							}
						});
						return;
					}
					 */
					
					/*
					
					final ArrayList<String> friends = (ArrayList<String>)userInfo.get(Collections.Users.FRIENDS);
					
					final ArrayList<Task<?>> tasks = new ArrayList<>();
					
					for (final String friend : (ArrayList<String>)userInfo.get(Collections.Users.FRIENDS)){
						final TextView textView = new TextView(context);
						textView.setText(friend);
						friendsLayout.addView(textView);
						
						Task<?> task1 = User.getProfileInfo(friend).addOnCompleteListener(new OnCompleteListener<Map<String, Object>>() {
							@Override
							public void onComplete(@NonNull Task<Map<String, Object>> task) {
								if (task.getResult() != null){
									Map<String, Object> userInfo = (Map<String, Object>)task.getResult();
									
									if (User.friends.contains(friend)) {
										//textView.setTextColor(Color.GREEN);
									}
									
									textView.setText(userInfo.get(Collections.Users.FIRSTNAME).toString() + " " + userInfo.get(Collections.Users.LASTNAME).toString());
									textView.setPadding(0, 0, 0, 10);
									
									textView.setOnClickListener(new View.OnClickListener() {
										@Override
										public void onClick(View v) {
											final Intent intent = new Intent(context, ViewUserProfile.class);
											intent.putExtra("userID", friend);
											startActivity(intent);
											//finish();
										}
									});
									
								}
							}
						});
						tasks.add(task1);
					}
					
					Tasks.whenAllComplete(tasks).addOnCompleteListener(new OnCompleteListener<List<Task<?>>>() {
						@Override
						public void onComplete(@NonNull Task<List<Task<?>>> task) {
							((LinearLayout)findViewById(R.id.rootLayout)).setVisibility(View.VISIBLE);
						}
					});
					
					
					 */
					
					setup(finalUserID);
				}
			}
		});
		
		
		//userFullName.setText(User.userInfo.get(Collections.Users.FIRSTNAME).toString() + ' ' + User.userInfo.get(Collections.Users.LASTNAME).toString());
		
	}
}
