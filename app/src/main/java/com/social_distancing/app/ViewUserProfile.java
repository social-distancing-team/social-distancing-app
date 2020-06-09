package com.social_distancing.app;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.rpc.Help;
import com.social_distancing.app.HelperClass;
import com.social_distancing.app.HelperClass.Collections;
import com.social_distancing.app.HelperClass.LOG;
//import com.social_distancing.app.HelperClass.User;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ViewUserProfile extends AppCompatActivity {
	
	final Context context = this;
	FirebaseAuth mAuth;
	FirebaseFirestore db = FirebaseFirestore.getInstance();
	RequestQueue mQueue;
	User mUser;

	ExtendedUser viewProfile;

	LinearLayout rootView;

	LinearLayout friendsLayout;

	TextView userFullName;
	TextView joinDateInfo;
	TextView lastSeenInfo;
	Button friendActionButton;

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		UserSingleton.getInstance();
		setContentView(R.layout.activity_view_user_profile);
		initViews();

		mAuth = FirebaseAuth.getInstance();
		mQueue = VolleySingleton.getInstance(this).getRequestQueue();
		mUser = UserSingleton.getInstance().getUser();
		if (getIntent().getStringExtra("userID") != null) {
			getViewProfile(getIntent().getStringExtra("userID"));
			Handler handler = new Handler();
			handler.postDelayed(new Runnable() {
				@Override
				public void run() {
					//((LinearLayout)findViewById(R.id.rootLayout)).setVisibility(View.GONE);
					rootView.setVisibility(View.GONE);
					start();
				}
			}, 4000);
		} else {
			viewProfile = (ExtendedUser) mUser;
			start();
		}
		/*
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
		 */
	}
	
	void setup(){
		LinearLayout friendsLayout = (LinearLayout)findViewById(R.id.friendsLayout);
		friendsLayout.removeAllViews();
		userFullName.setText(viewProfile.getFullName());

		for (final String friend : viewProfile.getFriends()){
			final TextView textView = new TextView(context);
			textView.setText(friend);
			friendsLayout.addView(textView);

			textView.setText(viewProfile.myFdata().get(friend).getFullName());
			textView.setPadding(0, 0, 0, 10);
			textView.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					final Intent intent = new Intent(context, ViewUserProfile.class);
					intent.putExtra("userID", friend);
					startActivity(intent);
				}
			});
		}
		rootView.setVisibility(View.VISIBLE);
	}

	private void getViewProfile(final String UserID) {
		DocumentReference documentReference = db.collection("Users").document(UserID);
		documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
			@Override
			public void onComplete(@NonNull Task<DocumentSnapshot> task) {
			if (task.getResult().exists()) {
				viewProfile = task.getResult().toObject(ExtendedUser.class);
				viewProfile.setUserID(UserID);
				viewProfile.initSnapshotListener();
				viewProfile.updateFdata();
			}
			}
		});
	}

	/*
	private void makeFriendsLayout() {

	}


	 */

	void start() {
		setup();
	}

	private void initViews() {
		rootView = (LinearLayout)findViewById(R.id.rootLayout);

		friendsLayout = findViewById(R.id.friendsLayout);

		userFullName = findViewById(R.id.userFullName);
		joinDateInfo = findViewById(R.id.joinDateInfo);
		lastSeenInfo = findViewById(R.id.lastSeenInfo);
		friendActionButton = findViewById(R.id.friendActionButton);
	}

	/*
	private void initFriendActionListener() {
		friendActionButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(final View v) {
				if (mUser.getFriends().contains(friendUID)){
					friendActionButton.setText("Remove friend");
					v.setEnabled(false);
					mUser.removeFriend(friendUID).addOnCompleteListener(new OnCompleteListener<Void>() {
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
					v.setEnabled(false);
					mUser.addFriend(friendUID).addOnCompleteListener(new OnCompleteListener<Void>() {
						@Override
						public void onComplete(@NonNull Task<Void> task) {
							if (task.isSuccessful()){
								friendActionButton.setText("Remove friend");
							}
							v.setEnabled(true);
						}
					});
				}
			}
		});
	}

	 */
}
