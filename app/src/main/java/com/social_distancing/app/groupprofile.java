package com.social_distancing.app;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckedTextView;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.rpc.Help;
import com.social_distancing.app.HelperClass;
import com.social_distancing.app.HelperClass.Collections;
import com.social_distancing.app.HelperClass.LOG;
import com.social_distancing.app.HelperClass.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class groupprofile extends AppCompatActivity {
	
	Context context = this;
	
	final Map<String, Map<String, Object>> userInfo = new HashMap<>();
	final ArrayList<String> usersList = new ArrayList<>();
	final Map<String, Object> groupData = new HashMap<>();
	
	final Map<String, Map<String, Object>> listInfo = new HashMap<>();
	final ArrayList<String> listsList = new ArrayList<>();
	final Map<String, Object> listData = new HashMap<>();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_groupprofile);
		getSupportActionBar().hide();
		
		final String groupID = getIntent().getStringExtra("groupID");
		final String finalGroupID = groupID;
		
		String groupName = getIntent().getStringExtra("groupName");
		final String finalGroupName = groupName;
		
		
		final LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		
		final LinearLayout linearLayout_Members = (LinearLayout)findViewById(R.id.linearLayout_Members);
		final Switch switch_Members = (Switch)findViewById(R.id.switch_Members);
		
		final LinearLayout linearLayout_Lists = (LinearLayout)findViewById(R.id.linearLayout_Lists);
		final Switch switch_Lists = (Switch)findViewById(R.id.switch_Lists);
		
		final ListView listView_Members = (ListView)findViewById(R.id.listView_Members);
		final ListView listView_Lists = (ListView)findViewById(R.id.listView_Lists);
		
		final Button button_MessageGroup = (Button)findViewById(R.id.button_MessageGroup);
		final TextView textView_GroupName = (TextView)findViewById(R.id.textView_GroupName);
		
		final TextView textView_MembersCount = (TextView)findViewById(R.id.textView_MembersCount);
		final TextView textView_ListsCount = (TextView)findViewById(R.id.textView_ListsCount);
		
		final Button button_AddFriendToGroup = (Button)findViewById(R.id.button_AddFriendToGroup);
		button_AddFriendToGroup.setVisibility(View.GONE);
		
		final Button button_CreateList = (Button)findViewById(R.id.button_CreateList);
		button_CreateList.setVisibility(View.GONE);
		
		
		textView_GroupName.setText(groupName);
		
		Map<String, Object> groupData = User.groupInfo.get(groupID);
		ArrayList<String> groupUsers = (ArrayList<String>)groupData.get("Users");
		
		textView_MembersCount.setText("(" + Integer.toString(groupUsers.size()) + ")");
		
		button_MessageGroup.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				final Intent intent = new Intent(context, chat.class);
				intent.putExtra("userID", groupID);
				intent.putExtra("name", textView_GroupName.getText().toString());
				
				String chatID = User.groupInfo.get(groupID).get("Chat").toString();
				intent.putExtra("chatID", chatID);
				startActivity(intent);
			}
		});
		
		final Runnable getUsersInformation = new Runnable() {
			@Override
			public void run() {
				final List<String> usersKeyList = new ArrayList<String>(usersList);
				final List<String> usersNameList = new ArrayList<String>();
				
				for (String userID : usersKeyList){
					if (!userInfo.containsKey(userID))
						continue;
					Map<String, Object> userData = (Map<String, Object>)userInfo.get(userID);
					
					if (User.groupInfo.get(groupID).get("Owner").toString().equals(userID)){
						usersNameList.add(userData.get(Collections.Users.FIRSTNAME).toString() + " " + userData.get(Collections.Users.LASTNAME).toString() + " (Owner)");
					} else
						usersNameList.add(userData.get(Collections.Users.FIRSTNAME).toString() + " " + userData.get(Collections.Users.LASTNAME).toString());
				}
				
				//Log.d(LOG.INFORMATION, "friendsList" + friendsList.toString());
				final ArrayAdapter<String> adapterMembers = new ArrayAdapter<String>(context,
						android.R.layout.simple_list_item_1, android.R.id.text1, usersNameList){
					
					@Override
					public View getView(int position, View convertView, ViewGroup parent){
						// Get the Item from ListView
						View view = super.getView(position, convertView, parent);
						
						// Initialize a TextView for ListView each Item
						TextView tv = (TextView) view.findViewById(android.R.id.text1);
						int color = tv.getCurrentTextColor();
						
						// Set the text color of TextView (ListView Item)
						if (!HelperClass.auth.getCurrentUser().getUid().equals(usersKeyList.get(position))){
							//tv.setTextColor(Color.RED);
							Log.d(LOG.WARNING, usersKeyList.get(position) + ", MAKING IT REEEEEEEEEEEEEEEEEEEEEDDDDDD");
						} else {
							//tv.setTextColor(Color.BLUE);
							Log.d(LOG.WARNING, usersKeyList.get(position) + ", 233452535rr5");
						}
						
						
						//Log.d(LOG.INFORMATION, "UserKey: " + usersKeyList.get(position).toString());
						
						// Generate ListView Item using TextView
						return view;
					}
				};
				
				listView_Members.setAdapter(adapterMembers);
				
				listView_Members.setOnItemClickListener(new AdapterView.OnItemClickListener() {
					@Override
					public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
						final Intent intent = new Intent(context, userprofile.class);
						//Log.d(LOG.WARNING, "ABCDE: " + friendsKeyList.get(position).toString());
						//Log.d(LOG.WARNING, "FGHJI: " + friendsNameList.get(position));
						
						if (HelperClass.auth.getCurrentUser().getUid().equals(usersKeyList.get(position))) {
							Log.d(LOG.WARNING, "iiiiiiii");
							return;
						}
						
						if (User.friendInfo.containsKey(usersKeyList.get(position))){
							intent.putExtra("userID", usersKeyList.get(position));
							intent.putExtra("userName", usersNameList.get(position));
							startActivity(intent);
							//finish();
						} else {
							Toast.makeText(context, "You have not added this user as a friend.\nYou cannot access their profile.", Toast.LENGTH_SHORT).show();
						}
					}
				});
				
				/*
				listView_Members.setLongClickable(true);
				listView_Members.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
					@Override
					public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
						if (!User.friendInfo.containsKey(usersKeyList.get(position))) {
							// dialog
							final AlertDialog.Builder removeMemberFromGroupDialog = new AlertDialog.Builder(context);
							removeMemberFromGroupDialog.setTitle("Add friend");// + listView_Friends.getItemAtPosition(position).toString() + "?");
							removeMemberFromGroupDialog.setMessage("Do you to add " + listView_Members.getItemAtPosition(position).toString() + " as a friend?");
							removeMemberFromGroupDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog, int which) {
									String memberID = usersKeyList.get(position);
									User.addFriend(memberID).addOnCompleteListener(new OnCompleteListener<Void>() {
										@Override
										public void onComplete(@NonNull Task<Void> task) {
											if (task.isSuccessful()){
												Toast.makeText(context, "Successfully added a new friend.", Toast.LENGTH_SHORT).show();
											} else {
												Toast.makeText(context, "Error adding friend.\n" + task.getException().toString(), Toast.LENGTH_SHORT).show();
											}
										}
									});
								}
							});
							removeMemberFromGroupDialog.setNegativeButton("No", null);
							
							final AlertDialog dialog = removeMemberFromGroupDialog.create();
							dialog.show();
						}
						
						return false;
					}
				});
				*/
				
				listView_Members.setLongClickable(true);
				
				listView_Members.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
					@Override
					public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
						if (User.groupInfo.get(groupID).get("Owner").toString().equals(HelperClass.auth.getCurrentUser().getUid())){
							if (usersList.get(position).toString().equals(HelperClass.auth.getCurrentUser().getUid()))
								return true;
							
							final AlertDialog.Builder removeMemberFromGroupDialog = new AlertDialog.Builder(context);
							removeMemberFromGroupDialog.setTitle("Remove member from group");// + listView_Friends.getItemAtPosition(position).toString() + "?");
							removeMemberFromGroupDialog.setMessage("Are you sure you want to remove " + listView_Members.getItemAtPosition(position).toString() + " from this group?");
							removeMemberFromGroupDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog, int which) {
									String memberID = usersKeyList.get(position);
									
									DocumentReference userGroupReference = HelperClass.db.collection("UserGroups").document(memberID);
									final Map<String, Object> removeGroupMap = new HashMap<>();
									removeGroupMap.put("Groups", FieldValue.arrayRemove(groupID));
									Task<Void> removeUserGroupTask = userGroupReference.update(removeGroupMap);
									
									DocumentReference groupReference = HelperClass.db.collection("Groups").document(groupID);
									final Map<String, Object> removeGroupMap2 = new HashMap<>();
									removeGroupMap2.put("Users", FieldValue.arrayRemove(memberID));
									Task<Void> removeGroupTask = groupReference.update(removeGroupMap2);
									
								}
							});
							removeMemberFromGroupDialog.setNegativeButton("No", null);
							
							final AlertDialog dialog = removeMemberFromGroupDialog.create();
							dialog.show();
						}
						return true;
					}
				});
				
				if (usersNameList.size() > 0 ){
					textView_MembersCount.setText("(" + Integer.toString(usersNameList.size()) + ")");
				} else {
					textView_MembersCount.setText("");
				}
			}
		};
		
		final Runnable getListsInformation = new Runnable() {
			@Override
			public void run() {
				final List<String> listsKeyList = new ArrayList<String>(listsList);
				final List<String> listsNameList = new ArrayList<String>();
				
				for (String listID : listsKeyList){
					if (!listInfo.containsKey(listID))
						continue;
					
					Map<String, Object> listData = (Map<String, Object>)listInfo.get(listID);
					
					listsNameList.add(listData.get("Name").toString());
					
					/*
					if (User.groupInfo.get(groupID).get("Owner").toString().equals(userID)){
						usersNameList.add(userData.get(Collections.Users.FIRSTNAME).toString() + " " + userData.get(Collections.Users.LASTNAME).toString() + " (Owner)");
					} else
						usersNameList.add(userData.get(Collections.Users.FIRSTNAME).toString() + " " + userData.get(Collections.Users.LASTNAME).toString());
					
					 */
				}
				
				//Log.d(LOG.INFORMATION, "friendsList" + friendsList.toString());
				final ArrayAdapter<String> adapterLists = new ArrayAdapter<String>(context,
						android.R.layout.simple_list_item_1, android.R.id.text1, listsNameList){
					
					@Override
					public View getView(int position, View convertView, ViewGroup parent){
						// Get the Item from ListView
						View view = super.getView(position, convertView, parent);
						/*
						// Initialize a TextView for ListView each Item
						TextView tv = (TextView) view.findViewById(android.R.id.text1);
						int color = tv.getCurrentTextColor();
						
						// Set the text color of TextView (ListView Item)
						if (!HelperClass.auth.getCurrentUser().getUid().equals(usersKeyList.get(position))){
							//tv.setTextColor(Color.RED);
							Log.d(LOG.WARNING, usersKeyList.get(position) + ", MAKING IT REEEEEEEEEEEEEEEEEEEEEDDDDDD");
						} else {
							//tv.setTextColor(Color.BLUE);
							Log.d(LOG.WARNING, usersKeyList.get(position) + ", 233452535rr5");
						}
						*/
						
						//Log.d(LOG.INFORMATION, "UserKey: " + usersKeyList.get(position).toString());
						
						// Generate ListView Item using TextView
						return view;
					}
				};
				
				listView_Lists.setAdapter(adapterLists);
				
				listView_Lists.setOnItemClickListener(new AdapterView.OnItemClickListener() {
					@Override
					public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
						//Log.d(LOG.WARNING, "ABCDE: " + friendsKeyList.get(position).toString());
						//Log.d(LOG.WARNING, "FGHJI: " + friendsNameList.get(position));
						
						if (HelperClass.auth.getCurrentUser().getUid().equals(listsKeyList.get(position))) {
							Log.d(LOG.WARNING, "iiiiiiii");
							return;
						}
						
						final Intent intent = new Intent(context, grouplist.class);
						
						intent.putExtra("listID", listsKeyList.get(position));
						intent.putExtra("listName", listsNameList.get(position));
						startActivity(intent);
					}
				});
				
				listView_Lists.setLongClickable(true);
				
				listView_Lists.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
					@Override
					public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
						if (User.groupInfo.get(groupID).get("Owner").toString().equals(HelperClass.auth.getCurrentUser().getUid())){
							if (usersList.get(position).toString().equals(HelperClass.auth.getCurrentUser().getUid()))
								return true;
							
							final AlertDialog.Builder removeMemberFromGroupDialog = new AlertDialog.Builder(context);
							removeMemberFromGroupDialog.setTitle("Delete grou list");// + listView_Friends.getItemAtPosition(position).toString() + "?");
							removeMemberFromGroupDialog.setMessage("Are you sure you want to delete " + listView_Lists.getItemAtPosition(position).toString() + " from this group?");
							removeMemberFromGroupDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog, int which) {
									String listID = listsKeyList.get(position);

									DocumentReference groupReference = HelperClass.db.collection("Groups").document(groupID);
									final Map<String, Object> removeGroupMap2 = new HashMap<>();
									removeGroupMap2.put("Lists", FieldValue.arrayRemove(listID));
									Task<Void> removeGroupTask = groupReference.update(removeGroupMap2);
									
								}
							});
							removeMemberFromGroupDialog.setNegativeButton("No", null);
							
							final AlertDialog dialog = removeMemberFromGroupDialog.create();
							dialog.show();
						}
						return true;
					}
				});
				
				if (listsNameList.size() > 0 ){
					textView_ListsCount.setText("(" + Integer.toString(listsNameList.size()) + ")");
					switch_Lists.setVisibility(View.VISIBLE);
				} else {
					textView_ListsCount.setText("");
					switch_Lists.setVisibility(View.GONE);
				}
			}
		};
		
		
		DocumentReference groupReference = HelperClass.db.collection("Groups").document(groupID);
		groupReference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
			@Override
			public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
				Map<String, Object> newGroupData = documentSnapshot.getData();
				ArrayList<String> userIDs = (ArrayList<String>)newGroupData.get("Users");
				usersList.clear();
				usersList.addAll(userIDs);
				
				if (newGroupData.get("Owner").toString().equals(HelperClass.auth.getCurrentUser().getUid())){
					button_CreateList.setVisibility(View.VISIBLE);
					
					button_AddFriendToGroup.setVisibility(View.VISIBLE);
					button_AddFriendToGroup.setOnClickListener(new View.OnClickListener() {
						@Override
						public void onClick(View v) {
							View friendsView = inflater.inflate(R.layout.createnewgrouplayout, null);
							final AlertDialog.Builder addMembersToGroupDialog = new AlertDialog.Builder(context);
							addMembersToGroupDialog.setTitle("Add members to group");
							addMembersToGroupDialog.setMessage("Select the members you want to add to this group.");
							addMembersToGroupDialog.setView(friendsView);
							addMembersToGroupDialog.setNegativeButton("Cancel", null);
							
							final ListView listView_Friends = (ListView)friendsView.findViewById(R.id.listView_Friends);
							//final EditText editText_GroupName = (EditText)friendsView.findViewById(R.id.editText_GroupName);
							LinearLayout linearLayout_Groups = (LinearLayout)friendsView.findViewById(R.id.linearLayout_Group);
							linearLayout_Groups.setVisibility(View.GONE);
							
							final List<String> friendsKeyList = new ArrayList<String>(User.friendInfo.keySet());
							final List<String> addableFriendsKeyList = new ArrayList<>();
							final List<String> addblefriendsNameList = new ArrayList<String>();
							
							for (String friendID : friendsKeyList){
								if (usersList.contains(friendID))
									continue;
								
								Map<String, Object> friendUserData = (Map<String, Object>)User.friendInfo.get(friendID);
								addableFriendsKeyList.add(friendID);
								addblefriendsNameList.add(friendUserData.get(Collections.Users.FIRSTNAME).toString() + " " + friendUserData.get(Collections.Users.LASTNAME).toString());
							}
							
							final ArrayAdapter<String> adapterFriends = new ArrayAdapter<String>(context,
									android.R.layout.simple_list_item_multiple_choice, android.R.id.text1, addblefriendsNameList);
							
							listView_Friends.setAdapter(adapterFriends);
							listView_Friends.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
							
							addMembersToGroupDialog.setPositiveButton("Add", new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog, int which) {
									ArrayList<String> friendIDsToCreateGroupWith = new ArrayList<>();
									for (int i = 0; i < listView_Friends.getCount(); i++) {
										Log.d(LOG.INFORMATION, "Checking " + listView_Friends.getItemAtPosition(i).toString());
										if (listView_Friends.isItemChecked(i)) {
											Log.d(LOG.INFORMATION, "You have checked " + addableFriendsKeyList.get(i) + " (" + addblefriendsNameList.get(i) + " )");
											friendIDsToCreateGroupWith.add(addableFriendsKeyList.get(i));
										}
									}
									for (String friendID : friendIDsToCreateGroupWith){
										Log.d(LOG.INFORMATION, "ASD#E#E@@EER%%R%R");
										DocumentReference userGroupReference = HelperClass.db.collection("UserGroups").document(friendID);
										final Map<String, Object> removeGroupMap = new HashMap<>();
										removeGroupMap.put("Groups", FieldValue.arrayUnion(groupID));
										Task<Void> removeUserGroupTask = userGroupReference.update(removeGroupMap);
										
										DocumentReference groupReference = HelperClass.db.collection("Groups").document(groupID);
										final Map<String, Object> removeGroupMap2 = new HashMap<>();
										removeGroupMap2.put("Users", FieldValue.arrayUnion(friendID));
										Task<Void> removeGroupTask = groupReference.update(removeGroupMap2);
									}
								}
							});
							
							
							final AlertDialog dialog = addMembersToGroupDialog.create();
							

							
							listView_Friends.setOnItemClickListener(new AdapterView.OnItemClickListener() {
								@Override
								public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
									CheckedTextView checkedTextView = ((CheckedTextView)view);
									checkedTextView.setChecked(!checkedTextView.isChecked());
									if (listView_Friends.getCheckedItemCount()>0){
										dialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(true);
									} else {
										dialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);
									}
								}
							});
							
							dialog.show();
							dialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);
						}
					});
				}
				
				Log.d(LOG.INFORMATION, "Group IDs: " + usersList.toString());
				
				for (String userID : userIDs){
					if (!userInfo.containsKey(userID)){
						DocumentReference userDocumentReference = (DocumentReference)HelperClass.db.collection(Collections.USERS).document(userID);
						
						userDocumentReference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
							@Override
							public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
								Log.d(LOG.INFORMATION, "134533453453");
								userInfo.put(documentSnapshot.getId(), documentSnapshot.getData());
								getUsersInformation.run();
							}
						});
					}
				}
				
				getUsersInformation.run();
				
				listsList.clear();
				
				Log.d(LOG.INFORMATION, "NewGroupData: " + newGroupData.toString());
				
				if (newGroupData.get("Lists").toString().equals(""))
					return;
				
				ArrayList<String> listIDs = (ArrayList<String>)newGroupData.get("Lists");
				
				if (listIDs.size() == 0)
					return;
				
				if (listIDs.size() == 1 && listIDs.get(0).toString().equals(""))
					return;
				
				listsList.addAll(listIDs);
				
				for (String listID : listIDs){
					if (!listInfo.containsKey(listID)){
						DocumentReference listDocumentReference = (DocumentReference)HelperClass.db.collection(Collections.LISTS).document(listID);
						
						listDocumentReference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
							@Override
							public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
								Log.d(LOG.INFORMATION, "134533453453");
								listInfo.put(documentSnapshot.getId(), documentSnapshot.getData());
								getListsInformation.run();
							}
						});
					}
				}
				
				
				getListsInformation.run();
				
			}
		});
		
		switch_Members.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if (isChecked){
					linearLayout_Members.setVisibility(View.VISIBLE);
				} else{
					linearLayout_Members.setVisibility(View.GONE);
				}
			}
		});
		
		switch_Lists.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if (isChecked){
					linearLayout_Lists.setVisibility(View.VISIBLE);
				} else{
					linearLayout_Lists.setVisibility(View.GONE);
				}
			}
		});
	
		button_CreateList.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				final AlertDialog.Builder addNewFriendDialog = new AlertDialog.Builder(context);
				addNewFriendDialog.setTitle("Create a new group list");
				addNewFriendDialog.setMessage("Enter name of the new list.");
				final EditText editText = new EditText(context);
				editText.setPadding(10,0,0,0);
				
				
				
				addNewFriendDialog.setView(editText);
				addNewFriendDialog.setPositiveButton("Create", new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which) {
								final String listName = editText.getText().toString();
								final DocumentReference newListReference = HelperClass.db.collection("Lists").document();
								Map<String, Object> listData = new HashMap<>();
								listData.put("Name", listName);
								listData.put("Items", "");
								newListReference.set(listData).addOnCompleteListener(new OnCompleteListener<Void>() {
									@Override
									public void onComplete(@NonNull Task<Void> task) {
										if (task.isSuccessful()){
											DocumentReference groupDocumentReference = HelperClass.db.collection("Groups").document(groupID);
											Map<String, Object> groupData = new HashMap<>();
											groupData.put("Lists", FieldValue.arrayUnion(newListReference.getId()));
											groupDocumentReference.update(groupData);
										}
									}
								});
								
							}
						});
				
				addNewFriendDialog.setNegativeButton("Cancel", null);
				final AlertDialog dialog = addNewFriendDialog.create();
				
				editText.addTextChangedListener(new TextWatcher() {
					@Override
					public void beforeTextChanged(CharSequence s, int start, int count, int after) {
					
					}
					
					@Override
					public void onTextChanged(CharSequence s, int start, int before, int count) {
						if (count > 0){
							dialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(true);
						} else {
							dialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);
						}
					}
					
					@Override
					public void afterTextChanged(Editable s) {
					
					}
				});
				
				dialog.show();
				dialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);
			}
		});
		
	}
}
