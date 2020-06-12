package com.social_distancing.app;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Checkable;
import android.widget.CheckedTextView;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;

import com.google.firebase.auth.AuthResult;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.social_distancing.app.HelperClass;
import com.social_distancing.app.HelperClass.Collections;
import com.social_distancing.app.HelperClass.LOG;
import com.social_distancing.app.HelperClass.User;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.social_distancing.app.HelperClass;
import com.social_distancing.app.HelperClass.Collections;
import com.social_distancing.app.HelperClass.User;
import com.social_distancing.app.HelperClass.LOG;

public class mainpage extends AppCompatActivity {
	
	Context context = this;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_mainpage);
		getSupportActionBar().hide();
		final LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		
		final LinearLayout linearLayout_Friends = (LinearLayout)findViewById(R.id.linearLayout_Friends);
		final Switch switch_Friends = (Switch)findViewById(R.id.switch_Friends);
		
		final LinearLayout linearLayout_Groups = (LinearLayout)findViewById(R.id.linearLayout_Groups);
		final Switch switch_Groups = (Switch)findViewById(R.id.switch_Groups);
		
		final LinearLayout linearLayout_Chats = (LinearLayout)findViewById(R.id.linearLayout_Chats);
		final Switch switch_Chats = (Switch)findViewById(R.id.switch_Chats);
		
		final ListView listView_Friends = (ListView)findViewById(R.id.listView_Friends);
		final ListView listView_Groups = (ListView)findViewById(R.id.listView_Groups);
		final ListView listView_Chats = (ListView)findViewById(R.id.listView_Chats);
		
		final Button button_addFriend = (Button)findViewById(R.id.button_AddFriend);
		final Button button_addGroup = (Button)findViewById(R.id.button_AddGroup);
		final Button button_EditUserInformation = (Button)findViewById(R.id.button_EditUserInformation);
		final Button button_DeleteSelf = (Button)findViewById(R.id.button_DeleteSelf);
		final Button button_SignOut = (Button)findViewById(R.id.button_SignOut);
		
		final TextView textView_UserID = (TextView)findViewById(R.id.textView_UserID);
		final TextView textView_UserFullName = (TextView)findViewById(R.id.textView_UserFullName);
		
		final TextView textView_FriendsCount = (TextView)findViewById(R.id.textView_FriendsCount);
		final TextView textView_GroupsCount = (TextView)findViewById(R.id.textView_GroupsCount);
		
		textView_UserID.setText(HelperClass.auth.getCurrentUser().getUid());
		textView_UserID.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
				ClipData clip = ClipData.newPlainText("Copied Text", textView_UserID.getText().toString());
				clipboard.setPrimaryClip(clip);
				Toast.makeText(context, "Copied UID to clipboard.", Toast.LENGTH_SHORT).show();
			}
		});
		
		button_addFriend.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				View addNewFriendView = inflater.inflate(R.layout.addfriendlayout, null);
				final EditText editText_UserID = (EditText)addNewFriendView.findViewById(R.id.editText_UserID);
				
				final AlertDialog.Builder addNewFriendDialog = new AlertDialog.Builder(context);
				addNewFriendDialog.setTitle("Add a new friend");
				addNewFriendDialog.setMessage("Enter the User ID of another user below.");
				addNewFriendDialog.setView(addNewFriendView);
				addNewFriendDialog.setPositiveButton("Add", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						final String friendID = editText_UserID.getText().toString();
						
						if (friendID.equals(HelperClass.auth.getCurrentUser().getUid())){
							Toast.makeText(context, "You cannot add yourself as a friend.", Toast.LENGTH_SHORT).show();
							return;
						}
						
						DocumentReference friendReference = (DocumentReference)HelperClass.db.collection(Collections.USERS).document(friendID);
						friendReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
							@Override
							public void onComplete(@NonNull Task<DocumentSnapshot> task) {
								if (task.isSuccessful()){
									DocumentSnapshot friendDocumentSnapshot = (DocumentSnapshot)task.getResult();
									if (friendDocumentSnapshot.exists()){
										Task<Void> addFriendTask = User.addFriend(friendID);
										addFriendTask.addOnCompleteListener(new OnCompleteListener<Void>() {
											@Override
											public void onComplete(@NonNull Task<Void> task) {
												if (task.isSuccessful()){
													Toast.makeText(context, "Successfully added a new friend.", Toast.LENGTH_SHORT).show();
												} else {
													Toast.makeText(context, "Error adding friend.\n" + task.getException().toString(), Toast.LENGTH_SHORT).show();
												}
											}
										});
									} else {
										Toast.makeText(context, "Error adding friend.\nThe User ID does not exist.", Toast.LENGTH_SHORT).show();
									}
								} else {
									Toast.makeText(context, "Error adding friend.\n" + task.getException().toString(), Toast.LENGTH_SHORT).show();
								}
							}
						});
					}
				});
				addNewFriendDialog.setNegativeButton("Cancel", null);
				
				final AlertDialog dialog = addNewFriendDialog.create();
				
				editText_UserID.addTextChangedListener(new TextWatcher() {
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
		
		button_addGroup.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				View createNewGroupView = inflater.inflate(R.layout.createnewgrouplayout, null);
				final AlertDialog.Builder createNewGroupDialog = new AlertDialog.Builder(context);
				createNewGroupDialog.setTitle("Create new group");
				createNewGroupDialog.setMessage("Enter the details of your new group.\n\nSelect the friends you to be a part of this group.");
				createNewGroupDialog.setView(createNewGroupView);
				createNewGroupDialog.setNegativeButton("Cancel", null);
				
				final ListView listView_Friends = (ListView)createNewGroupView.findViewById(R.id.listView_Friends);
				//listView_Friends.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
				//listView_Friends.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
				
				
				final EditText editText_GroupName = (EditText)createNewGroupView.findViewById(R.id.editText_GroupName);
				
				final List<String> friendsKeyList = new ArrayList<String>(User.friendInfo.keySet());
				final List<String> friendsNameList = new ArrayList<String>();
				
				for (String friendID : friendsKeyList){
					Map<String, Object> friendUserData = (Map<String, Object>)User.friendInfo.get(friendID);
					friendsNameList.add(friendUserData.get(Collections.Users.FIRSTNAME).toString() + " " + friendUserData.get(Collections.Users.LASTNAME).toString());
				}
				
				//Log.d(LOG.INFORMATION, "friendsList" + friendsList.toString());
				final ArrayAdapter<String> adapterFriends = new ArrayAdapter<String>(context,
						android.R.layout.simple_list_item_multiple_choice, android.R.id.text1, friendsNameList);
				
				listView_Friends.setAdapter(adapterFriends);
				
				//This is bugged
				//https://stackoverflow.com/questions/11382539/trouble-with-android-listview-selection-changes-when-switching-choicemode
				listView_Friends.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
				
				listView_Friends.setOnItemClickListener(new AdapterView.OnItemClickListener() {
					@Override
					public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
						CheckedTextView checkedTextView = ((CheckedTextView)view);
						checkedTextView.setChecked(!checkedTextView.isChecked());
					}
				});
				
				
				createNewGroupDialog.setPositiveButton("Create", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						ArrayList<String> friendIDsToCreateGroupWith = new ArrayList<>();
						for (int i = 0; i < listView_Friends.getCount(); i++) {
							Log.d(LOG.INFORMATION, "Checking " + listView_Friends.getItemAtPosition(i).toString());
							if (listView_Friends.isItemChecked(i)) {
								Log.d(LOG.INFORMATION, "You have checked " + friendsKeyList.get(i) + " (" + friendsNameList.get(i) + " )");
								friendIDsToCreateGroupWith.add(friendsKeyList.get(i));
							}
						}
						friendIDsToCreateGroupWith.add(HelperClass.auth.getCurrentUser().getUid());
						String name = editText_GroupName.getText().toString();
						
						HelperClass.createGroup(name, friendIDsToCreateGroupWith).addOnCompleteListener(new OnCompleteListener<Void>() {
							@Override
							public void onComplete(@NonNull Task<Void> task) {
								if (task.isSuccessful()){
									Toast.makeText(context, "Successfully created group.", Toast.LENGTH_SHORT).show();
								} else {
									Toast.makeText(context, "Error creating group. " + task.getException(), Toast.LENGTH_SHORT).show();
								}
							}
						});
					}
				});
				
				final AlertDialog dialog = createNewGroupDialog.create();
				
				editText_GroupName.addTextChangedListener(new TextWatcher() {
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
		
		final Runnable getFriendsInformation = new Runnable() {
			@Override
			public void run() {
				final List<String> friendsKeyList = new ArrayList<String>(User.friends);
				final List<String> friendsNameList = new ArrayList<String>();
				
				for (String friendID : friendsKeyList){
					if (!User.friendInfo.containsKey(friendID))
						continue;
					Map<String, Object> friendUserData = (Map<String, Object>)User.friendInfo.get(friendID);
					friendsNameList.add(friendUserData.get(Collections.Users.FIRSTNAME).toString() + " " + friendUserData.get(Collections.Users.LASTNAME).toString());
				}
				
				//Log.d(LOG.INFORMATION, "friendsList" + friendsList.toString());
				final ArrayAdapter<String> adapterFriends = new ArrayAdapter<String>(context,
						android.R.layout.simple_list_item_1, android.R.id.text1, friendsNameList);
				listView_Friends.setAdapter(adapterFriends);
				
				if (friendsNameList.size() > 0 ){
					textView_FriendsCount.setText("(" + Integer.toString(friendsNameList.size()) + ")");
				} else {
					textView_FriendsCount.setText("");
				}
				
				listView_Friends.setOnItemClickListener(new AdapterView.OnItemClickListener() {
					@Override
					public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
						final Intent intent = new Intent(context, userprofile.class);
						//Log.d(LOG.WARNING, "ABCDE: " + friendsKeyList.get(position).toString());
						//Log.d(LOG.WARNING, "FGHJI: " + friendsNameList.get(position));
						intent.putExtra("userID", friendsKeyList.get(position));
						intent.putExtra("userName", friendsNameList.get(position));
						startActivity(intent);
						//finish();
					}
				});
				
				listView_Friends.setLongClickable(true);
				listView_Friends.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
					@Override
					public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
						Log.d(LOG.INFORMATION, "Long clicked.");
						//Toast.makeText(context, listView_Friends.getItemAtPosition(position).toString(), Toast.LENGTH_SHORT).show();
						
						final AlertDialog.Builder deleteFriendDialog = new AlertDialog.Builder(context);
						deleteFriendDialog.setTitle("Remove friend");// + listView_Friends.getItemAtPosition(position).toString() + "?");
						deleteFriendDialog.setMessage("Are you sure you want to unfriend " + listView_Friends.getItemAtPosition(position).toString() + "?");
						deleteFriendDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which) {
								final String friendID = friendsKeyList.get(position);
								DocumentReference friendReference = (DocumentReference)HelperClass.db.collection(Collections.USERS).document(friendID);
								friendReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
									@Override
									public void onComplete(@NonNull Task<DocumentSnapshot> task) {
										if (task.isSuccessful()){
											DocumentSnapshot friendDocumentSnapshot = (DocumentSnapshot)task.getResult();
											if (friendDocumentSnapshot.exists()){
												Task<Void> addFriendTask = User.removeFriend(friendID);
												addFriendTask.addOnCompleteListener(new OnCompleteListener<Void>() {
													@Override
													public void onComplete(@NonNull Task<Void> task) {
														if (task.isSuccessful()){
															Toast.makeText(context, "Successfully unfriended " + friendsNameList.get(position).toString() + ".", Toast.LENGTH_SHORT).show();
														} else {
															Toast.makeText(context, "Error removing friend.\n" + task.getException().toString(), Toast.LENGTH_SHORT).show();
														}
													}
												});
											} else {
												Toast.makeText(context, "Error removing friend.\nThe User ID does not exist.", Toast.LENGTH_SHORT).show();
											}
										} else {
											Toast.makeText(context, "Error removing friend.\n" + task.getException().toString(), Toast.LENGTH_SHORT).show();
										}
									}
								});
							}
						});
						deleteFriendDialog.setNegativeButton("No", null);
						
						final AlertDialog dialog = deleteFriendDialog.create();
						dialog.show();
						return true;
					}
				});
			}
		};
		
		getFriendsInformation.run();
		User.friendInfoRunnables.put("mainpage", getFriendsInformation);
		
		
		final Runnable getGroupInformation = new Runnable() {
			@Override
			public void run() {
				final List<String> groupsKeyList = new ArrayList<String>(User.groups);
				final List<String> groupsNameList = new ArrayList<String>();
				
				for (String groupID : groupsKeyList){
					Log.d(LOG.INFORMATION, "Getting GroupID: " + groupID);
					Log.d(LOG.INFORMATION, "GroupInfo: " + User.groupInfo.toString());
					
					if (!User.groupInfo.containsKey(groupID))
						continue;
					
					Map<String, Object> groupInfoData = (Map<String, Object>)User.groupInfo.get(groupID);
					Log.d(LOG.INFORMATION, "GroupInfoData: " + groupInfoData.toString());
					groupsNameList.add(groupInfoData.get("Name").toString());
				}
				
				if (groupsNameList.size() > 0 ){
					textView_GroupsCount.setText("(" + Integer.toString(groupsNameList.size()) + ")");
				} else {
					textView_GroupsCount.setText("");
				}
				
				Log.d(LOG.INFORMATION, "GroupsNameList: " + groupsNameList.toString());
				
				Log.d(LOG.INFORMATION, "GroupInfo: " + User.groupInfo.toString());
				
				final ArrayAdapter<String> adapterGroups = new ArrayAdapter<String>(context,
						android.R.layout.simple_list_item_1, android.R.id.text1, groupsNameList);
				listView_Groups.setAdapter(adapterGroups);
				
				listView_Groups.setOnItemClickListener(new AdapterView.OnItemClickListener() {
					@Override
					public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
						final Intent intent = new Intent(context, groupprofile.class);
						//Log.d(LOG.WARNING, "ABCDE: " + friendsKeyList.get(position).toString());
						//Log.d(LOG.WARNING, "FGHJI: " + friendsNameList.get(position));
						intent.putExtra("groupID", groupsKeyList.get(position));
						intent.putExtra("groupName", groupsNameList.get(position));
						startActivity(intent);
						//finish();
					}
				});
				
				listView_Groups.setLongClickable(true);
				listView_Groups.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
					@Override
					public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
						Log.d(LOG.INFORMATION, "Long clicked.");
						//Toast.makeText(context, listView_Friends.getItemAtPosition(position).toString(), Toast.LENGTH_SHORT).show();
						
						final AlertDialog.Builder leaveGroupDialog = new AlertDialog.Builder(context);
						leaveGroupDialog.setTitle("Leave group");// + listView_Friends.getItemAtPosition(position).toString() + "?");
						leaveGroupDialog.setMessage("Are you sure you want to leave " + groupsNameList.get(position).toString() + "?");
						leaveGroupDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which) {
								final String friendID = groupsKeyList.get(position);
								final DocumentReference groupReference = (DocumentReference)HelperClass.db.collection("UserGroups").document(HelperClass.auth.getCurrentUser().getUid());
								groupReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
									@Override
									public void onComplete(@NonNull Task<DocumentSnapshot> task) {
										if (task.isSuccessful()){
											DocumentSnapshot groupDocumentSnapshot = (DocumentSnapshot)task.getResult();
											if (groupDocumentSnapshot.exists()){
												final Map<String, Object> groupDataMap = new HashMap<>();
												groupDataMap.put("Groups", FieldValue.arrayRemove(friendID));
												Task<Void> addFriendTask = groupReference.update(groupDataMap);
												addFriendTask.addOnCompleteListener(new OnCompleteListener<Void>() {
													@Override
													public void onComplete(@NonNull Task<Void> task) {
														if (task.isSuccessful()){
															Toast.makeText(context, "Successfully left " + groupsNameList.get(position).toString() + ".", Toast.LENGTH_SHORT).show();
														} else {
															Toast.makeText(context, "Error leaving group.\n" + task.getException().toString(), Toast.LENGTH_SHORT).show();
														}
													}
												});
											} else {
												Toast.makeText(context, "Error leaving group.\nThe Group ID does not exist.", Toast.LENGTH_SHORT).show();
											}
										} else {
											Toast.makeText(context, "Error leaving group.\n" + task.getException().toString(), Toast.LENGTH_SHORT).show();
										}
									}
								});
							}
						});
						leaveGroupDialog.setNegativeButton("No", null);
						
						final AlertDialog dialog = leaveGroupDialog.create();
						dialog.show();
						return true;
					}
				});
			}
		};
		
		getGroupInformation.run();
		User.groupInfoRunnables.put("mainpage", getGroupInformation);
		
		switch_Friends.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if (isChecked){
					linearLayout_Friends.setVisibility(View.VISIBLE);
				} else{
					linearLayout_Friends.setVisibility(View.GONE);
				}
			}
		});
		
		switch_Groups.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if (isChecked){
					linearLayout_Groups.setVisibility(View.VISIBLE);
					//getGroupInformation.run();
					
				} else{
					linearLayout_Groups.setVisibility(View.GONE);
				}
			}
		});
		
		
		switch_Chats.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if (isChecked){
					linearLayout_Chats.setVisibility(View.VISIBLE);
				} else{
					linearLayout_Chats.setVisibility(View.GONE);
				}
			}
		});
		
		Runnable getUserInformation = new Runnable() {
			@Override
			public void run() {
				textView_UserFullName.setText(User.userInfo.get(Collections.Users.FIRSTNAME) + " " + User.userInfo.get(Collections.Users.LASTNAME));
				getFriendsInformation.run();
			}
		};
		
		getUserInformation.run();
		User.userInfoRunnables.put("mainpage", getUserInformation);
		
		button_EditUserInformation.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				View userInfoView = inflater.inflate(R.layout.registrationlayout, null);
				final AlertDialog.Builder updateUserDialog = new AlertDialog.Builder(context);
				updateUserDialog.setTitle("Update account details");
				//registrationDialog.setMessage("Please enter your details");
				updateUserDialog.setView(userInfoView);
				
				final LinearLayout linearLayout_Password = (LinearLayout)userInfoView.findViewById(R.id.linearLayout_Password);
				linearLayout_Password.setVisibility(View.GONE);
				
				final LinearLayout linearLayout_DateOfBirth = (LinearLayout)userInfoView.findViewById(R.id.linearLayout_DateOfBirth);
				linearLayout_DateOfBirth.setVisibility(View.GONE);
				
				final LinearLayout linearLayout_Email = (LinearLayout)userInfoView.findViewById(R.id.linearLayout_Email);
				linearLayout_Email.setVisibility(View.GONE);
				
				final EditText editText_FirstName = (EditText)userInfoView.findViewById(R.id.editText_FirstName);
				final EditText editText_LastName = (EditText)userInfoView.findViewById(R.id.editText_LastName);
				final EditText editText_Location = (EditText)userInfoView.findViewById(R.id.editText_Location);
				final EditText editText_Email = (EditText)userInfoView.findViewById(R.id.editText_Email);
				final EditText editText_Password = (EditText)userInfoView.findViewById(R.id.editText_Password);
				final EditText editText_SecurityQuestion = (EditText)userInfoView.findViewById(R.id.editText_SecurityQuestion);
				final EditText editText_SecurityAnswer = (EditText)userInfoView.findViewById(R.id.editText_SecurityAnswer);
				
				editText_FirstName.setText(User.userInfo.get("FirstName").toString());
				editText_LastName.setText(User.userInfo.get("LastName").toString());
				editText_Location.setText(User.userInfo.get("Location").toString());
				editText_SecurityQuestion.setText(User.userInfo.get("SecurityQuestion").toString());
				editText_SecurityAnswer.setText(User.userInfo.get("SecurityAnswer").toString());
				
				updateUserDialog.setPositiveButton("Update", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						final String firstName = editText_FirstName.getText().toString();
						final String lastName = editText_LastName.getText().toString();
						final String location = editText_Location.getText().toString();
						final String securityQuestion = editText_SecurityQuestion.getText().toString();
						final String securityAnswer = editText_SecurityAnswer.getText().toString();
						
						Map<String, Object> updatedUserData = new HashMap<>();
						updatedUserData.put("FirstName", firstName);
						updatedUserData.put("LastName", lastName);
						updatedUserData.put("Location", location);
						updatedUserData.put("SecurityQuestion", securityQuestion);
						updatedUserData.put("SecurityAnswer", securityAnswer);
						
						HelperClass.db.collection(Collections.USERS).document(HelperClass.auth.getCurrentUser().getUid()).update(updatedUserData).addOnCompleteListener(new OnCompleteListener<Void>() {
							@Override
							public void onComplete(@NonNull Task<Void> task) {
								if (task.isSuccessful()){
								
								} else {
								
								}
							}
						});
						
					}
				});
				updateUserDialog.setNegativeButton("Cancel", null);
				updateUserDialog.show();
			}
		});
		
		button_DeleteSelf.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				final AlertDialog.Builder deleteSelfDialog = new AlertDialog.Builder(context);
				deleteSelfDialog.setTitle("Delete account");
				deleteSelfDialog.setMessage("Are you sure you want to delete your account?");
				deleteSelfDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						DocumentReference documentReference = HelperClass.db.collection("Users").document(HelperClass.auth.getCurrentUser().getUid());
						Map<String, Object> map = new HashMap<>();
						map.put("Deleted", true);
						documentReference.update(map).addOnCompleteListener(new OnCompleteListener<Void>() {
							@Override
							public void onComplete(@NonNull Task<Void> task) {
								if (task.isSuccessful()){
									User.logout();
									final Intent intent = new Intent(context, homePage.class);
									startActivity(intent);
									finish();
								}
							}
						});
					}
				});
				deleteSelfDialog.setNegativeButton("No", null);
				
				final AlertDialog dialog = deleteSelfDialog.create();

				dialog.show();
				//dialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);
			}
		});
		
		button_SignOut.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				User.logout();
				final Intent intent = new Intent(context, homePage.class);
				startActivity(intent);
				finish();
			}
		});
	}
}
