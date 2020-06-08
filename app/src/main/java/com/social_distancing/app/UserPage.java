package com.social_distancing.app;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;


import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.text.TextUtils;
import android.util.ArrayMap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Space;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GetTokenResult;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
// import com.google.gson.JsonObject;
// import com.google.gson.JsonArray;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONArray;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;


import com.google.android.gms.tasks.Continuation;

public class UserPage extends AppCompatActivity {
	
	FirebaseAuth mAuth;
	FirebaseUser mUser;
	User userData;
	FirebaseFirestore db = FirebaseFirestore.getInstance();
	Context context = this;

	RequestQueue mQueue;

	//////////////

	ConstraintLayout rootView;

	EditText userIDEditText;
	EditText userIDAddFriendEditText;
	EditText messageDataEditText;

	TextView fullnameTextView;
	TextView textView3;
	Button button3;
	Button friendsButton;
	Button chatsButton;
	Button listsButton;
	Button sendChatmessageButton;
	Button addFriendButton;
	
	LinearLayout linearLayout1;
	LinearLayout linearLayout2;
	LinearLayout linearLayout3;
	LinearLayout parentLinearLayout;
	LinearLayout friendsListLayout;
	LinearLayout insertMessageLayout;
	ScrollView friendsListScrollLayout;

	TabLayout mainTabLayout;

	RecyclerView friendsRecyclerView;
	RecyclerView chatsRecyclerView;
	RecyclerView listsRecyclerView;
	
	//String myName;
	//////////////
	private void getUserData(){
		DocumentReference docRef = db.collection("Users").document(mUser.getUid());
		docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
			@Override
			public void onComplete(@NonNull Task<DocumentSnapshot> task) {
				if (task.getResult().exists()) {
					userData = task.getResult().toObject(User.class);
					userData.initSnapshotListener();
					userData.initFriendsData();
					setContent();
				}
			}
		});
	}

	/*
	private void getChats(String uid){
		CollectionReference collectionReference = db.collection("Users").document(uid).collection("Chats");
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
	 */

	/*
	private void addChats(List<DocumentSnapshot> chats){
		for (DocumentSnapshot chat : chats){
			if (!chat.exists())
				continue;
			final String chatID = (String)chat.get("chatid");
			final String friendID = (String)chat.getId();
			
			Log.d("", chatID + " , " + friendID);
			
			DocumentReference docRef = db.collection("Users").document(friendID);
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
						}
					}
				}
			});
		}
	}

	 */

	/*
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
											//textView.setText(myName);
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
												/*
							
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
										//textView.setText(myName);
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
					//insertMessageLayout.setVisibility(View.VISIBLE);
				}
			}
		});
	}
	 */

	/*
	private void getFriends(String uid){
		DocumentReference docRef = db.collection("Users").document(uid);
		docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
			@Override
			public void onComplete(@NonNull Task<DocumentSnapshot> task) {
				if (task.isSuccessful()){
					DocumentSnapshot documentSnapshot = task.getResult();
					ArrayList<String> friends_uid = (ArrayList<String>)documentSnapshot.get("Friends");
					//Log.d("", friends_uid.get(0));
					addFriends(friends_uid);
				}
			}
		});
	}
	 */

	/*
	private void addFriends(ArrayList<String> friends_uid){
		for (String friend_uid : friends_uid) {
			DocumentReference documentReference = db.collection("Users").document(friend_uid);
			
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
	 */

	/*
	private void getProfileInformation(final String uid){
		DocumentReference documentReference = db.collection("Users").document(uid);
		documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
			@Override
			public void onComplete(@NonNull Task<DocumentSnapshot> task) {
				if (task.isSuccessful()){
					DocumentSnapshot documentSnapshot = (DocumentSnapshot)task.getResult();
					if (documentSnapshot.exists()){
						String firstName = (String)documentSnapshot.get("FirstName");
						String lastName = (String)documentSnapshot.get("LastName");
						//myName = firstName;
						//fullnameTextView.setText(firstName + " " + lastName);
						getFriends(uid);
						getChats(uid);
					}
				}
			}
		});
	}
	 */

	/*
	private void addFriend(final String frienduid){
		final DocumentReference documentReference = db.collection("Users").document(mAuth.getCurrentUser().getUid().toString());
		Task<DocumentSnapshot> documentSnapshotTask = documentReference.get();
		documentSnapshotTask.addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
			@Override
			public void onComplete(@NonNull Task<DocumentSnapshot> task) {
				if (task.isSuccessful()){
					DocumentSnapshot documentSnapshot = task.getResult();
					Map<String, Object> data = new HashMap<>();
					data.put("Friends", FieldValue.arrayUnion(frienduid));
					documentReference.update(data);
				}
			}
		});

	}
     */

	/*
	private void getFriendData() {
		final ArrayMap<String, Friend> friends = new ArrayMap<>();
		for (final String friendUid : userData.getFriends()) {
			DocumentReference docRef = db.collection("Users").document(friendUid);
			docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
				@Override
				public void onComplete(@NonNull Task<DocumentSnapshot> task) {
					if (task.getResult().exists()) {
						friends.put(friendUid, task.getResult().toObject(Friend.class));
					}
				}
			});
		}
	}

	 */

	private void friendLayout() {
		friendsListLayout.removeAllViews();
		for (String friendUid: userData.getFriendsData().keySet()) {
			LinearLayout friendUserLayout = new LinearLayout(context);
			friendUserLayout.setBackgroundColor(Color.TRANSPARENT);
			friendUserLayout.setOrientation(LinearLayout.HORIZONTAL);
			friendUserLayout.setWeightSum(1);

			TextView friendFullName = new TextView(context);
			friendFullName.setText(userData.getFriendsData().get(friendUid).getFullName());
			friendFullName.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
			friendFullName.setTextSize(18);
			friendFullName.setTextColor(Color.BLACK);

			LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
			layoutParams.weight = (float) 0.9;
			friendFullName.setLayoutParams(layoutParams);

			friendUserLayout.addView(friendFullName);

			friendUserLayout.setPadding(20,0,20,30);

			friendsListLayout.addView(friendUserLayout);
		}
	}

	private void setContent() {
		fullnameTextView.setText(userData.getFullName());
		friendLayout();
		if (rootView.getVisibility() != View.VISIBLE) {
			rootView.setVisibility(View.VISIBLE);
		}
	}

	// An example of calling the RestAPI on the server
	private void testRestAPI(){
		FirebaseUser mUser = mAuth.getCurrentUser();
		mUser.getIdToken(true).addOnCompleteListener(new OnCompleteListener<GetTokenResult>() {
			@Override
			public void onComplete(@NonNull Task<GetTokenResult> task) {
				if (task.isSuccessful()) {
					final String IdToken = task.getResult().getToken();
					Log.i("MMDEBUG_testRestAPI_IdToken", IdToken.toString());
					// TODO API request, including IdToken in Header
					String url = "http://<!--TODO enter your IP address to allow testing RestAPI on locally running server-->:8000/api/messages/list/";
					JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
							new Response.Listener<JSONObject>() {
								@Override
								public void onResponse(JSONObject response) {
									// JSONArray jsonArray = response.getJSONObject("Users");
									Log.i("MMDEBUG_testRestAPI_response", response.toString());
									// textView3.setText(jsonArray.toString());
								}
							}, new Response.ErrorListener() {
						@Override
						public void onErrorResponse(VolleyError error) {
							Log.i("MMDEBUG_testRestAPI_error", error.toString());
							// error.printStackTrace();
						}
					}) {
						@Override
						public Map<String, String> getHeaders() {
							Map<String, String> headers = new HashMap<String, String>();
							headers.put("Authorization", "JWT " + IdToken);
							return headers;
						}
					};
					mQueue.add(request);
				} else {
					task.getException();
				}
			}
		});
	}

	private void initViews() {
		rootView = (ConstraintLayout) findViewById(R.id.user_page_layout);
		rootView.setVisibility(View.GONE);

		fullnameTextView = (TextView)findViewById(R.id.fullnameTextView);
		textView3 = (TextView)findViewById(R.id.textView3);

		button3 = (Button)findViewById(R.id.button3);
		friendsButton = (Button)findViewById(R.id.friendsButton);
		chatsButton = (Button)findViewById(R.id.chatsButton);
		listsButton = (Button)findViewById(R.id.listsButton);
		
		linearLayout1 = (LinearLayout)findViewById(R.id.LinearLayout1);
		linearLayout2 = (LinearLayout)findViewById(R.id.LinearLayout2);
		linearLayout3 = (LinearLayout)findViewById(R.id.LinearLayout3);
		parentLinearLayout = (LinearLayout)findViewById(R.id.parentLinearLayout);
		
		//friendsRecyclerView = (RecyclerView)findViewById(R.id.friendsRecyclerView);
		chatsRecyclerView = (RecyclerView)findViewById(R.id.chatsRecyclerView);
		listsRecyclerView = (RecyclerView)findViewById(R.id.listsRecyclerView);
		
		userIDAddFriendEditText = (EditText)findViewById(R.id.userIDAddFriendEditText);
		userIDEditText = (EditText) findViewById(R.id.userIDEditText);
		messageDataEditText = (EditText)findViewById(R.id.messageDataEditText);

		mainTabLayout = (TabLayout)findViewById(R.id.mainTabLayout);
		
		parentLinearLayout.removeAllViews();
		parentLinearLayout.addView(linearLayout1);
		friendsListLayout = (LinearLayout)findViewById(R.id.friendsListLayout);
		insertMessageLayout = (LinearLayout)findViewById(R.id.InsertMessageLayout);

		friendsListScrollLayout = (ScrollView)findViewById(R.id.friendsListScrollLayout);
		
		sendChatmessageButton = (Button)findViewById(R.id.sendChatMessageButton);
		addFriendButton = (Button)findViewById(R.id.addFriendButton);

		return;
	}

	private void initTabbedMenuListener() {
		mainTabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
			@Override
			public void onTabSelected(TabLayout.Tab tab) {
				parentLinearLayout.removeAllViews();
				setContent();

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
	}

	private void initAddFriendListener() {
		addFriendButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				//addFriend(userIDAddFriendEditText.getText().toString());
			}
		});
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_user_page);
		initViews();

		initTabbedMenuListener();
		initAddFriendListener();

		mAuth = FirebaseAuth.getInstance();
		mUser = mAuth.getCurrentUser();
		mQueue = VolleySingleton.getInstance(this).getRequestQueue();

		getUserData();
		//testRestAPI();
	}
}
