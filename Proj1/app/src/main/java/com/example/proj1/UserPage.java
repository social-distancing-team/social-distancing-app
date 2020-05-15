package com.example.proj1;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Layout;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Space;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UserPage extends AppCompatActivity {
	
	FirebaseAuth mAuth;
	FirebaseFirestore db = FirebaseFirestore.getInstance();
	Context context = this;
	
	//////////////
	
	EditText userIDEditText;
	
	TextView fullnameTextView;
	Button friendsButton;
	Button chatsButton;
	Button listsButton;
	
	LinearLayout linearLayout1;
	LinearLayout linearLayout2;
	LinearLayout linearLayout3;
	LinearLayout parentLinearLayout;
	
	RecyclerView friendsRecyclerView;
	RecyclerView chatsRecyclerView;
	RecyclerView listsRecyclerView;
	
	EditText userIDAddFriendEditText;
	
	LinearLayout friendsListLinearLayout;
	
	TabLayout mainTabLayout;
	
	LinearLayout insertMessageLayout;
	
	Button sendChatmessageButton;
	
	EditText messageDataEditText;
	
	String myName;
	//////////////
	
	private Object getDocument(String collection, String document){
		DocumentReference docRef = db.collection(collection).document(document);
		
		Task<DocumentSnapshot> task = docRef.get();
		while(!task.isComplete());
		if (task.isSuccessful()) {
			return task.getResult();
		}
		return null;
	}
	
	private void getChats(String uid){
		CollectionReference collectionReference = db.collection("User").document(uid).collection("Chats");
		collectionReference.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
			@Override
			public void onComplete(@NonNull Task<QuerySnapshot> task) {
				if (task.isSuccessful()){
					QuerySnapshot querySnapshot = task.getResult();
					List<DocumentSnapshot> documentSnapshots = querySnapshot.getDocuments();
					for (DocumentSnapshot documentSnapshot : documentSnapshots){
						String chatID = (String)documentSnapshot.get("chatid");
						String friendID = (String)documentSnapshot.getId();
						Log.d("", friendID + " -> " + chatID);
					}
					addChats(documentSnapshots);
				}
			}
		});
		//addChats(null);
	}
	
	private void addChats(List<DocumentSnapshot> chats){
		for (DocumentSnapshot chat : chats){
			if (!chat.exists())
				continue;
			final String chatID = (String)chat.get("chatid");
			final String friendID = (String)chat.getId();
			
			Log.d("", chatID + " , " + friendID);
			
			DocumentReference docRef = db.collection("User").document(friendID);
			docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
				@Override
				public void onComplete(@NonNull Task<DocumentSnapshot> task) {
					if (task.isSuccessful()){
						for (int i =0; i<1;i++) {
							DocumentSnapshot documentSnapshot = task.getResult();
							final String firstName = (String)documentSnapshot.get("FirstName");
							final String lastName = (String)documentSnapshot.get("LastName");
							boolean status = true;
							if (documentSnapshot.contains("status")){
								status = (boolean)documentSnapshot.get("status");
							}
							
							LinearLayout friendMessageUserLayout = new LinearLayout(context);
							friendMessageUserLayout.setOrientation(LinearLayout.VERTICAL);
							
							LinearLayout friendUserLayout = new LinearLayout(context);
							friendUserLayout.setOrientation(LinearLayout.HORIZONTAL);
							friendUserLayout.setWeightSum(1);
							
							TextView friendFillName = new TextView(context);
							friendFillName.setText(firstName + " " + lastName);
							friendFillName.setTextSize(18);
							friendFillName.setTextColor(Color.BLACK);
							
							LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
							layoutParams.weight = (float) 0.7;
							friendFillName.setLayoutParams(layoutParams);
							//friendFillName.setBackgroundColor(Color.RED);
							
							LinearLayout.LayoutParams layoutParams2 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
							layoutParams2.weight = (float) 0.15;
							TextView timeTextView = new TextView(context);
							timeTextView.setText("13:37");
							timeTextView.setLayoutParams(layoutParams2);
							//timeTextView.setBackgroundColor(Color.BLUE);
							
							TextView statusTextView = new TextView(context);
							statusTextView.setText("●");
							statusTextView.setTextSize(14);
							if (status){
								statusTextView.setTextColor(Color.GREEN);
							} else {
								statusTextView.setTextColor(Color.RED);
							}
							
							LinearLayout.LayoutParams layoutParams3 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
							layoutParams3.weight = (float) 0.15;
							
							statusTextView.setLayoutParams(layoutParams3);
							//statusTextView.setBackgroundColor(Color.GREEN);
							
							friendUserLayout.addView(friendFillName);
							friendUserLayout.addView(timeTextView);
							friendUserLayout.addView(statusTextView);
							
							friendUserLayout.setPadding(0,0,0,10);
							
							LinearLayout messageTextLinearLayout = new LinearLayout(context);
							messageTextLinearLayout.setOrientation(LinearLayout.HORIZONTAL);
							messageTextLinearLayout.setWeightSum(1);
							
							TextView messageTextView = new TextView(context);
							messageTextView.setText("testman: " + " social distancing sucks reeeeeeeeeeeeeeeeee");
							messageTextView.setEllipsize(TextUtils.TruncateAt.END);
							messageTextView.setMaxLines(1);
							//messageTextView.setTextColor(Color.LTGRAY);
							LinearLayout.LayoutParams layoutParams4 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
							layoutParams4.weight = (float)0.25;
							messageTextView.setLayoutParams(layoutParams4);
							//messageTextView.setBackgroundColor(Color.YELLOW);
							
							
							Space s = new Space(context);
							LinearLayout.LayoutParams layoutParams5 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
							layoutParams5.weight = (float)0.75;
							s.setLayoutParams(layoutParams5);
							
							messageTextLinearLayout.addView(messageTextView);
							messageTextLinearLayout.addView(s);
							
							friendMessageUserLayout.addView(friendUserLayout);
							friendMessageUserLayout.addView(messageTextLinearLayout);
							
							friendMessageUserLayout.setPadding(20,0,20,40);
							
							friendMessageUserLayout.setOnClickListener(new View.OnClickListener() {
								@Override
								public void onClick(View v) {
									getChatMessages(firstName, chatID);
								}
							});
							
							linearLayout2.addView(friendMessageUserLayout);
							
							
							
							//linearLayout2.setBackgroundColor(Color.RED);
						}
					}
				}
			});
		}
	}
	
	private void getChatMessages(final String uid, final String cid){
		DocumentReference docRef = db.collection("Chat").document(cid);
		docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
			@Override
			public void onComplete(@NonNull Task<DocumentSnapshot> task) {
				if (task.isSuccessful()){
					DocumentSnapshot documentSnapshot = task.getResult();
					ArrayList<String> messages = (ArrayList<String>)documentSnapshot.get("Messages");
					Log.d("", messages.get(0));
					
					final LinearLayout chatMessagesLayout = new LinearLayout(context);
					chatMessagesLayout.setOrientation(LinearLayout.VERTICAL);
					for (int i = 0; i< 1; i++) {
						for (final String message : messages) {
							DocumentReference docRef = db.collection("Message").document(message);
							docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
								@Override
								public void onComplete(@NonNull Task<DocumentSnapshot> task) {
									if (task.isSuccessful()) {
										
										LinearLayout linlayout = new LinearLayout(context);
										linlayout.setOrientation(LinearLayout.HORIZONTAL);
										
										LinearLayout linearLayout = new LinearLayout(context);
										linearLayout.setOrientation(LinearLayout.VERTICAL);
										DocumentSnapshot documentSnapshot1 = (DocumentSnapshot) task.getResult();
										if (!documentSnapshot1.exists())
											return;
										String userID = (String) documentSnapshot1.get("UserID");
										
										final String content = (String) documentSnapshot1.get("Content");
										TextView textView = new TextView(context);
										textView.setText(uid);
										
										Log.d("", userID + " : " + content + ", " + mAuth.getCurrentUser().getUid());
										
										//textView.setTextSize(20);
										textView.setTypeface(null, Typeface.BOLD);
										
										TextView contentTextView = new TextView(context);
										contentTextView.setText(content);
										
										linearLayout.addView(textView);
										linearLayout.addView(contentTextView);
										
										//linearLayout.setBackgroundColor(Color.WHITE);
										int padding = 10 * 2;
										linearLayout.setPadding(padding, padding, padding, padding);
										LinearLayout.LayoutParams layoutParams2 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
										
										//layoutParams2.gravity = Gravity.RIGHT;
										//linearLayout.setLayoutParams(layoutParams2);
										
										
										if (userID.toString().equals(mAuth.getCurrentUser().getUid().toString())) {
											Log.d("", "My message : " + content);
											Space s = new Space(context);
											LinearLayout.LayoutParams layoutParams5 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
											layoutParams5.weight = (float) 1;
											s.setLayoutParams(layoutParams5);
											linlayout.addView(s);
											
											linlayout.setLayoutParams(layoutParams5);
											textView.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_END);
											contentTextView.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_END);
											//linlayout.setBackgroundColor(Color.YELLOW);
											textView.setText(myName);
										}
										
										linlayout.addView(linearLayout);
										chatMessagesLayout.addView(linlayout);
										
										linlayout.setPadding(padding, padding, padding, padding);
										
										LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
										chatMessagesLayout.setLayoutParams(layoutParams);
										//chatMessagesLayout.setBackgroundColor(Color.BLUE);
										
									}
								}
							});
						}
					}
					
					sendChatmessageButton.setOnClickListener(null);
					sendChatmessageButton.setOnClickListener(new View.OnClickListener() {
						@Override
						public void onClick(View v) {
												/*
												Map<String, Object> data = new HashMap<>();
												data.put("Messages", FieldValue.arrayUnion("Test"));
												String id = db.collection("Chat").document(cid).getId();
												db.collection("Chat").document(cid).update(data);
												*/
							
							String id = db.collection("Message").document().getId();
							
							Map<String, Object> messageData = new HashMap<>();
							final String content = messageDataEditText.getText().toString();
							messageData.put("Content", content);
							messageData.put("UserID", mAuth.getCurrentUser().getUid());
							db.collection("Message").document(id).set(messageData);
							
							Map<String, Object> data = new HashMap<>();
							data.put("Messages", FieldValue.arrayUnion(id));
							db.collection("Chat").document(cid).update(data).addOnCompleteListener(new OnCompleteListener<Void>() {
								@Override
								public void onComplete(@NonNull Task<Void> task) {
									LinearLayout linlayout = new LinearLayout(context);
									linlayout.setOrientation(LinearLayout.HORIZONTAL);
									
									LinearLayout linearLayout = new LinearLayout(context);
									linearLayout.setOrientation(LinearLayout.VERTICAL);
									//DocumentSnapshot documentSnapshot1 = (DocumentSnapshot) task.getResult();
									//if (!documentSnapshot1.exists())
									//	return;
									//String userID = (String) documentSnapshot1.get("UserID");
									
									
									TextView textView = new TextView(context);
									textView.setText(uid);
									
									//Log.d("", userID + " : " + content + ", " + mAuth.getCurrentUser().getUid());
									
									//textView.setTextSize(20);
									textView.setTypeface(null, Typeface.BOLD);
									
									TextView contentTextView = new TextView(context);
									contentTextView.setText(content);
									
									linearLayout.addView(textView);
									linearLayout.addView(contentTextView);
									
									//linearLayout.setBackgroundColor(Color.WHITE);
									int padding = 10 * 2;
									linearLayout.setPadding(padding, padding, padding, padding);
									LinearLayout.LayoutParams layoutParams2 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
									
									//layoutParams2.gravity = Gravity.RIGHT;
									//linearLayout.setLayoutParams(layoutParams2);
									
									
									if (true) {
										//Log.d("", "My message : " + content);
										Space s = new Space(context);
										LinearLayout.LayoutParams layoutParams5 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
										layoutParams5.weight = (float) 1;
										s.setLayoutParams(layoutParams5);
										linlayout.addView(s);
										
										linlayout.setLayoutParams(layoutParams5);
										textView.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_END);
										contentTextView.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_END);
										//linlayout.setBackgroundColor(Color.YELLOW);
										textView.setText(myName);
									}
									
									linlayout.addView(linearLayout);
									linlayout.setPadding(padding, padding, padding, padding);
									
									chatMessagesLayout.addView(linlayout);
								}
							});
						}
					});
					
					parentLinearLayout.removeAllViews();
					parentLinearLayout.addView(chatMessagesLayout);
					insertMessageLayout.setVisibility(View.VISIBLE);
				}
			}
		});
	}
	
	private void getFriends(String uid){
		DocumentReference docRef = db.collection("User").document(uid);
		docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
			@Override
			public void onComplete(@NonNull Task<DocumentSnapshot> task) {
				if (task.isSuccessful()){
					DocumentSnapshot documentSnapshot = task.getResult();
					ArrayList<String> friends_uid = (ArrayList<String>)documentSnapshot.get("Friends");
					Log.d("", friends_uid.get(0));
					addFriends(friends_uid);
				}
			}
		});
	}
	
	private void addFriends(ArrayList<String> friends_uid){
		for (String friend_uid : friends_uid) {
			DocumentReference documentReference = db.collection("User").document(friend_uid);
			
			documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
				@Override
				public void onComplete(@NonNull Task<DocumentSnapshot> task) {
					if (task.isSuccessful()){
						DocumentSnapshot documentSnapshot = (DocumentSnapshot)task.getResult();
						if (documentSnapshot.exists()){
							String firstName = (String)documentSnapshot.get("FirstName");
							String lastName = (String)documentSnapshot.get("LastName");
							Log.d("", firstName + " " + lastName);
							
							for (int i=0; i<1; i++){
								LinearLayout friendUserLayout = new LinearLayout(context);
								friendUserLayout.setBackgroundColor(Color.TRANSPARENT);
								friendUserLayout.setOrientation(LinearLayout.HORIZONTAL);
								friendUserLayout.setWeightSum(1);
								
								TextView friendFillName = new TextView(context);
								friendFillName.setText(firstName + " " + lastName);
								friendFillName.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
								friendFillName.setTextSize(18);
								friendFillName.setTextColor(Color.BLACK);
								
								LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
								layoutParams.weight = (float) 0.9;
								friendFillName.setLayoutParams(layoutParams);
								
								TextView statusTextView = new TextView(context);
								statusTextView.setText("●");
								statusTextView.setTextSize(14);
								statusTextView.setTextColor(Color.RED);
								layoutParams.weight = (float) (1 - layoutParams.weight);
								statusTextView.setLayoutParams(layoutParams);
								
								friendUserLayout.addView(friendFillName);
								friendUserLayout.addView(statusTextView);
								
								friendUserLayout.setPadding(20,0,20,30);
								
								linearLayout1.addView(friendUserLayout);
								linearLayout1.setBackgroundColor(Color.TRANSPARENT);
							}
						}
					}
				}
			});
		}
	}
	
	
	private void getProfileInformation(final String uid){
		DocumentReference documentReference = db.collection("User").document(uid);
		documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
			@Override
			public void onComplete(@NonNull Task<DocumentSnapshot> task) {
				if (task.isSuccessful()){
					DocumentSnapshot documentSnapshot = (DocumentSnapshot)task.getResult();
					if (documentSnapshot.exists()){
						String firstName = (String)documentSnapshot.get("FirstName");
						String lastName = (String)documentSnapshot.get("LastName");
						myName = firstName;
						fullnameTextView.setText(firstName + " " + lastName);
						getFriends(uid);
						getChats(uid);
					}
				}
			}
		});
	}
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		mAuth = FirebaseAuth.getInstance();
		final FirebaseUser currentUser = mAuth.getCurrentUser();
		
		setContentView(R.layout.activity_user_page);
		
		userIDEditText = (EditText) findViewById(R.id.userIDEditText);
		
		fullnameTextView = (TextView)findViewById(R.id.fullnameTextView);
		friendsButton = (Button)findViewById(R.id.friendsButton);
		chatsButton = (Button)findViewById(R.id.chatsButton);
		listsButton = (Button)findViewById(R.id.listsButton);
		
		linearLayout1 = (LinearLayout)findViewById(R.id.LinearLayout1);
		linearLayout2 = (LinearLayout)findViewById(R.id.LinearLayout2);
		linearLayout3 = (LinearLayout)findViewById(R.id.LinearLayout3);
		parentLinearLayout = (LinearLayout)findViewById(R.id.parentLinearLayout);
		
		friendsRecyclerView = (RecyclerView)findViewById(R.id.friendsRecyclerView);
		chatsRecyclerView = (RecyclerView)findViewById(R.id.chatsRecyclerView);
		listsRecyclerView = (RecyclerView)findViewById(R.id.listsRecyclerView);
		
		userIDAddFriendEditText = (EditText)findViewById(R.id.userIDAddFriendEditText);
		
		friendsListLinearLayout = (LinearLayout)findViewById(R.id.friendsListLinearLayout);
		
		mainTabLayout = (TabLayout)findViewById(R.id.mainTabLayout);
		
		userIDAddFriendEditText.setHint(currentUser.getUid().toString());
		userIDAddFriendEditText.setHint(currentUser.getUid().toString());
		
		parentLinearLayout.removeAllViews();
		parentLinearLayout.addView(linearLayout1);
		
		insertMessageLayout = (LinearLayout)findViewById(R.id.InsertMessageLayout);
		
		
		sendChatmessageButton = (Button)findViewById(R.id.sendChatMessageButton);
		
		messageDataEditText = (EditText)findViewById(R.id.messageDataEditText);
		
		mainTabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
			@Override
			public void onTabSelected(TabLayout.Tab tab) {
				insertMessageLayout.setVisibility(View.GONE);
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
				onTabSelected(tab);
			}
		});
		
		linearLayout2.removeAllViews();
		
		if (mAuth.getCurrentUser() == null){
			Log.d("", "LOGGING IN");
			mAuth.signInWithEmailAndPassword("username@email.com", "username").addOnCompleteListener(new OnCompleteListener<AuthResult>() {
				@Override
				public void onComplete(@NonNull Task<AuthResult> task) {
					if (task.isSuccessful()) {
						FirebaseUser user = mAuth.getCurrentUser();
						Toast.makeText(context, "Successfully logged in.",
								Toast.LENGTH_SHORT).show();
						
						String uid = currentUser.getUid().toString();
						getProfileInformation(uid);
					} else {
						Toast.makeText(context, "Login failed." + task.getException().toString(),
								Toast.LENGTH_SHORT).show();
					}
				}
			});
		} else {
			Log.d("", "Already logged in");
			String uid = mAuth.getUid().toString();
			getProfileInformation(uid);
		}
		
		userIDEditText.setText(mAuth.getUid().toString());
		
	}
}
