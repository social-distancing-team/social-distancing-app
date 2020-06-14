/*
 *	Project:		Remote Life
 * 	Last edited:	13/06/2020
 * 	Author:			Karan Bajwa
 */

package com.social_distancing.app;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

/*
OS and UI
 */
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

/*
Tasks, Async and Firebase
 */
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;

/*
Collections
 */
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/*
Helper class and functions written by us
 */
import com.social_distancing.app.HelperClass;
import com.social_distancing.app.HelperClass.Collections;
import com.social_distancing.app.HelperClass.LOG;
import com.social_distancing.app.HelperClass.User;

public class userprofile extends AppCompatActivity {
	
	Context context = this;
	
	//Keep an ArrayList of the group IDs
	final ArrayList<String> groupsList = new ArrayList<>();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_userprofile);
		getSupportActionBar().hide();
		
		final String userID = getIntent().getStringExtra("userID");
		final String finalUserID = userID;
		
		String userName = getIntent().getStringExtra("userName");
		final String finalUserName = userName;
		
		getSupportActionBar().hide();
		final LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		
		final LinearLayout linearLayout_Friends = (LinearLayout) findViewById(R.id.linearLayout_Friends);
		final Switch switch_Friends = (Switch) findViewById(R.id.switch_Friends);
		
		final LinearLayout linearLayout_Groups = (LinearLayout) findViewById(R.id.linearLayout_Groups);
		final Switch switch_Groups = (Switch) findViewById(R.id.switch_Groups);
		
		final LinearLayout linearLayout_Chats = (LinearLayout) findViewById(R.id.linearLayout_Lists);
		final Switch switch_Chats = (Switch) findViewById(R.id.switch_Chats);
		
		final ListView listView_Friends = (ListView) findViewById(R.id.listView_Friends);
		final ListView listView_Groups = (ListView) findViewById(R.id.listView_Groups);
		final ListView listView_Lists = (ListView) findViewById(R.id.listView_Lists);
		
		final Button button_MessageUser = (Button) findViewById(R.id.button_MessageUser);
		
		final TextView textView_UserID = (TextView) findViewById(R.id.textView_UserID);
		final TextView textView_UserFullName = (TextView) findViewById(R.id.textView_UserFullName);
		final TextView textView_LastSeen = (TextView) findViewById(R.id.textView_LastSeen);
		final TextView textView_Location = (TextView) findViewById(R.id.textView_Location);
		
		final TextView textView_FriendsCount = (TextView) findViewById(R.id.textView_FriendsCount);
		final TextView textView_GroupsCount = (TextView) findViewById(R.id.textView_GroupsCount);
		
		textView_UserFullName.setText(finalUserName);
		textView_UserID.setText(finalUserID);
		textView_UserID.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
				ClipData clip = ClipData.newPlainText("Copied Text", textView_UserID.getText().toString());
				clipboard.setPrimaryClip(clip);
				Toast.makeText(context, "Copied UID to clipboard.", Toast.LENGTH_SHORT).show();
			}
		});
		
		DocumentReference userInfoReference = HelperClass.db.collection(Collections.USERS).document(finalUserID);
		DocumentReference userChatsReference = HelperClass.db.collection("UserChats").document(finalUserID);
		DocumentReference userGroupsReference = HelperClass.db.collection("UserGroups").document(finalUserID);
		DocumentReference userListsReference = HelperClass.db.collection("UserLists").document(finalUserID);
		
		userInfoReference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
			@Override
			public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
				String name = documentSnapshot.get(Collections.Users.FIRSTNAME).toString() + " " + documentSnapshot.get(Collections.Users.LASTNAME).toString();
				textView_UserFullName.setText(name);
				textView_Location.setText(documentSnapshot.get(Collections.Users.LOCATION).toString());
				
				final List<String> friendsKeyList = (ArrayList<String>) documentSnapshot.get(Collections.Users.FRIENDS);
				final List<String> friendsNameList = new ArrayList<String>();
				
				Log.d(LOG.INFORMATION, "Friends friends: " + friendsKeyList.toString());
				
				for (String friendID : friendsKeyList) {
					
					if (HelperClass.auth.getCurrentUser().getUid().equals(friendID)) {
						friendsNameList.add(User.userInfo.get("FirstName") + " " + User.userInfo.get("LastName") + " (You)");
						continue;
					}
					
					if (!User.friendInfo.containsKey(friendID) /*|| friendID.equals(HelperClass.auth.getCurrentUser().getUid())*/) {
						friendsNameList.add(friendID);
						continue;
					}
					Map<String, Object> friendUserData = (Map<String, Object>) User.friendInfo.get(friendID);
					if (friendID.equals(HelperClass.auth.getCurrentUser().getUid())) {
						friendsNameList.add(friendUserData.get(Collections.Users.FIRSTNAME).toString() + " " + friendUserData.get(Collections.Users.LASTNAME).toString() + " (You)");
					} else {
						friendsNameList.add(friendUserData.get(Collections.Users.FIRSTNAME).toString() + " " + friendUserData.get(Collections.Users.LASTNAME).toString());
						
					}
				}
				
				final ArrayAdapter<String> adapterFriends = new ArrayAdapter<String>(context,
						android.R.layout.simple_list_item_1, android.R.id.text1, friendsNameList);
				
				listView_Friends.setAdapter(adapterFriends);
				
				listView_Friends.setOnItemClickListener(new AdapterView.OnItemClickListener() {
					@Override
					public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
						final Intent intent = new Intent(context, userprofile.class);
						
						if (finalUserID.equals(friendsKeyList.get(position)) || friendsKeyList.get(position).equals(HelperClass.auth.getCurrentUser().getUid())) //cant view self
							return;
						
						if (User.friendInfo.containsKey(friendsKeyList.get(position))) {
							intent.putExtra("userID", friendsKeyList.get(position));
							intent.putExtra("userName", friendsNameList.get(position));
							startActivity(intent);
							finish();
						} else {
							Toast.makeText(context, "This user is not a mutual friend.\nYou cannot access their profile.", Toast.LENGTH_SHORT).show();
						}
					}
				});
				
				if (friendsNameList.size() > 0) {
					switch_Friends.setVisibility(View.VISIBLE);
				} else {
					switch_Friends.setVisibility(View.GONE);
				}
				if (friendsNameList.size() > 0) {
					textView_FriendsCount.setText("(" + Integer.toString(friendsNameList.size()) + ")");
				} else {
					textView_FriendsCount.setText("");
				}
				
			}
		});
		
		userGroupsReference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
			@Override
			public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
				groupsList.clear();
				
				Map<String, Object> userGroupData = documentSnapshot.getData();
				if (userGroupData.get(Collections.UserGroup.GROUPS).toString().equals(""))
					return;
				
				ArrayList<String> groupIDs = (ArrayList<String>) userGroupData.get(Collections.UserGroup.GROUPS);
				groupsList.addAll(groupIDs);
				
				final List<String> groupsKeyList = new ArrayList<>();
				//final List<String> friendsKeyList = new ArrayList<String>(User.friends);
				final List<String> groupsNameList = new ArrayList<String>();
				
				Log.d(LOG.INFORMATION, "USERGROUPS: " + groupsList.toString() + ", " + User.groupInfo.keySet().toString());
				
				for (String groupID : groupIDs) {
					if (User.groupInfo.containsKey(groupID)) {
						groupsKeyList.add(groupID);
						groupsNameList.add(User.groupInfo.get(groupID).get("Name").toString());
					}
				}
				
				final ArrayAdapter<String> adapterGroups = new ArrayAdapter<String>(context,
						android.R.layout.simple_list_item_1, android.R.id.text1, groupsNameList);
				
				listView_Groups.setAdapter(adapterGroups);
				listView_Groups.setOnItemClickListener(new AdapterView.OnItemClickListener() {
					@Override
					public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
						final Intent intent = new Intent(context, groupprofile.class);
						intent.putExtra("groupID", groupsKeyList.get(position));
						intent.putExtra("groupName", groupsNameList.get(position));
						startActivity(intent);
						//finish();
					}
				});
				
				if (groupsKeyList.size() > 0) {
					switch_Groups.setVisibility(View.VISIBLE);
				} else {
					switch_Groups.setVisibility(View.GONE);
				}
				if (groupsKeyList.size() > 0) {
					textView_GroupsCount.setText("(" + Integer.toString(groupsKeyList.size()) + ")");
				} else {
					textView_GroupsCount.setText("");
				}
			}
		});
		
		switch_Friends.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if (isChecked) {
					linearLayout_Friends.setVisibility(View.VISIBLE);
				} else {
					linearLayout_Friends.setVisibility(View.GONE);
				}
			}
		});
		
		switch_Groups.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if (isChecked) {
					linearLayout_Groups.setVisibility(View.VISIBLE);
					//getGroupInformation.run();
					
				} else {
					linearLayout_Groups.setVisibility(View.GONE);
				}
			}
		});
		
		
		switch_Chats.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if (isChecked) {
					linearLayout_Chats.setVisibility(View.VISIBLE);
				} else {
					linearLayout_Chats.setVisibility(View.GONE);
				}
			}
		});
		
		button_MessageUser.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				final Intent intent = new Intent(context, chat.class);
				intent.putExtra("userID", User.friendInfo.get(userID).get("FirstName").toString());
				intent.putExtra("name", textView_UserFullName.getText().toString());
				
				String chatID = User.chats.get(userID);
				intent.putExtra("chatID", chatID);
				startActivity(intent);
			}
		});
	}
}
