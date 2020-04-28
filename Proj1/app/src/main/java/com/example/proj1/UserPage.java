package com.example.proj1;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Layout;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.util.ArrayList;

public class UserPage extends AppCompatActivity {
	FirebaseAuth mAuth;
	FirebaseFirestore db = FirebaseFirestore.getInstance();
	Context context = this;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		/*
		
		 */
		mAuth = FirebaseAuth.getInstance();
		FirebaseUser currentUser = mAuth.getCurrentUser();
		String userIDstring = currentUser.getUid().toString();
		/*
		
		 */
		setContentView(R.layout.activity_user_page);
		
		final EditText userIDEditText = (EditText) findViewById(R.id.userIDEditText);
		userIDEditText.setText(userIDstring);
		
		final TextView fullnameTextView = (TextView)findViewById(R.id.fullnameTextView);
		final Button friendsButton = (Button)findViewById(R.id.friendsButton);
		final Button chatsButton = (Button)findViewById(R.id.chatsButton);
		final Button listsButton = (Button)findViewById(R.id.listsButton);
		
		final LinearLayout linearLayout1 = (LinearLayout)findViewById(R.id.LinearLayout1);
		final LinearLayout linearLayout2 = (LinearLayout)findViewById(R.id.LinearLayout2);
		final LinearLayout linearLayout3 = (LinearLayout)findViewById(R.id.LinearLayout3);
		final LinearLayout parentLinearLayout = (LinearLayout)findViewById(R.id.parentLinearLayout);
		
		final RecyclerView friendsRecyclerView = (RecyclerView)findViewById(R.id.friendsRecyclerView);
		final RecyclerView chatsRecyclerView = (RecyclerView)findViewById(R.id.chatsRecyclerView);
		final RecyclerView listsRecyclerView = (RecyclerView)findViewById(R.id.listsRecyclerView);
		
		final EditText userIDAddFriendEditText = (EditText)findViewById(R.id.userIDAddFriendEditText);
		userIDAddFriendEditText.setHint(currentUser.getUid().toString());
		
		final LinearLayout friendsListLinearLayout = (LinearLayout)findViewById(R.id.friendsListLinearLayout);
		

		
		chatsButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				parentLinearLayout.removeAllViews();
				parentLinearLayout.addView(linearLayout2);
			}
		});
		
		parentLinearLayout.removeAllViews();
		parentLinearLayout.addView(linearLayout1);
		
		final TabLayout mainTabLayout = (TabLayout)findViewById(R.id.mainTabLayout);
		mainTabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
			@Override
			public void onTabSelected(TabLayout.Tab tab) {
				parentLinearLayout.removeAllViews();
				switch (tab.getPosition()) {
					case 0:
						parentLinearLayout.addView(linearLayout1);
						break;
					case 1:
						parentLinearLayout.addView(linearLayout2);
						break;
					case 2:
						parentLinearLayout.addView(linearLayout3);
						break;
				}
			}
			
			@Override
			public void onTabUnselected(TabLayout.Tab tab) {
			
			}
			
			@Override
			public void onTabReselected(TabLayout.Tab tab) {
			
			}
		});
		
		DocumentReference docRef = db.collection("User").document(currentUser.getUid().toString());
		
		docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
			@Override
			public void onComplete(@NonNull Task<DocumentSnapshot> task) {
				if (task.isSuccessful()) {
					DocumentSnapshot documentSnapshot = (DocumentSnapshot)task.getResult();
					fullnameTextView.setText(documentSnapshot.get("FirstName") + " " + documentSnapshot.get("LastName"));
					
					ArrayList<String> friends = (ArrayList<String>)documentSnapshot.get("Friends");
					for (String friend : friends){

						DocumentReference documentReference =  db.collection("User").document(friend);
						documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
							@Override
							public void onComplete(@NonNull Task<DocumentSnapshot> task) {
								DocumentSnapshot snapshot = ((DocumentSnapshot)task.getResult());
								if (!snapshot.exists())
									return;
								
								Button button = new Button(context);
								button.setBackgroundColor(Color.LTGRAY);
								
								button.setText(snapshot.get("FirstName") + " " + snapshot.get("LastName"));
								//button.setLayoutParams(new ViewGroup.LayoutParams(0, 30));
								
								View view = new View(context);
								view.setLayoutParams(new ViewGroup.LayoutParams(0, 20));
								
								friendsListLinearLayout.addView(button);
								friendsListLinearLayout.addView(view);
							}
						});
					}
					
				} else {
				}
			}
		});
	}
}
