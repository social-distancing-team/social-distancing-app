/*
*	Project:		Remote Life
* 	Last edited:	13/06/2020
* 	Author:			Karan Bajwa
 */

package com.social_distancing.app;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

/*
OS and UI
 */
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
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

/*
Tasks, Async and Firebase
 */
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;

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
import com.social_distancing.app.HelperClass.User;
import com.social_distancing.app.HelperClass.LOG;

public class mainpage extends AppCompatActivity {
	
	//Create a context reference to this activity
	//We use the for creating UI stuff
	final Context context = this;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_mainpage);
		getSupportActionBar().hide(); //Disable the action bar
		
		//Inflater will be used to "inflate" (create) a view
		final LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		
		/*
		Get all the UI elements from the associated layout file of this activity
		 */
		final LinearLayout linearLayout_Friends = (LinearLayout) findViewById(R.id.linearLayout_Friends);
		final Switch switch_Friends = (Switch) findViewById(R.id.switch_Friends);
		
		final LinearLayout linearLayout_Groups = (LinearLayout) findViewById(R.id.linearLayout_Groups);
		final Switch switch_Groups = (Switch) findViewById(R.id.switch_Groups);
		
		final LinearLayout linearLayout_Chats = (LinearLayout) findViewById(R.id.linearLayout_Chats);
		final Switch switch_Chats = (Switch) findViewById(R.id.switch_Chats);
		
		final ListView listView_Friends = (ListView) findViewById(R.id.listView_Friends);
		final ListView listView_Groups = (ListView) findViewById(R.id.listView_Groups);
		final ListView listView_Chats = (ListView) findViewById(R.id.listView_Chats);
		
		final Button button_addFriend = (Button) findViewById(R.id.button_AddFriend);
		final Button button_addGroup = (Button) findViewById(R.id.button_AddGroup);
		final Button button_EditUserInformation = (Button) findViewById(R.id.button_EditUserInformation);
		final Button button_DeleteSelf = (Button) findViewById(R.id.button_DeleteSelf);
		final Button button_SignOut = (Button) findViewById(R.id.button_SignOut);
		
		final TextView textView_UserID = (TextView) findViewById(R.id.textView_UserID);
		final TextView textView_UserFullName = (TextView) findViewById(R.id.textView_UserFullName);
		
		final TextView textView_FriendsCount = (TextView) findViewById(R.id.textView_FriendsCount);
		final TextView textView_GroupsCount = (TextView) findViewById(R.id.textView_GroupsCount);
		
		//Clicking on the textview that displays the User ID should copy the User ID to the devices clipboard
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
		
		//onClickListener for when the add friend button is clicked
		button_addFriend.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				//Inflate the add friend layout
				View addNewFriendView = inflater.inflate(R.layout.addfriendlayout, null);
				final EditText editText_UserID = (EditText) addNewFriendView.findViewById(R.id.editText_UserID);
				
				//Create an alertdialog which will display our inflated layout
				final AlertDialog.Builder addNewFriendDialog = new AlertDialog.Builder(context);
				addNewFriendDialog.setTitle("Add a new friend");
				addNewFriendDialog.setMessage("Enter the User ID of another user below.");
				addNewFriendDialog.setView(addNewFriendView);
				
				//If the user clicks "Add" then do the following
				addNewFriendDialog.setPositiveButton("Add", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						//Get the target users ID from the edittext in the inflated view
						final String friendID = editText_UserID.getText().toString();
						
						//Check to make sure we're not trying to add yourself
						if (friendID.equals(HelperClass.auth.getCurrentUser().getUid())) {
							Toast.makeText(context, "You cannot add yourself as a friend.", Toast.LENGTH_SHORT).show();
							return;
						}
						
						//Check to make sure we're not already friends with the user
						if (User.friendInfo.containsKey(friendID)){
							Toast.makeText(context, "You are already friends with this user.", Toast.LENGTH_SHORT).show();
							return;
						}
						
						//The user we're trying to add is not a friend.
						//So let's get his/her/ze/xim/xir's information
						DocumentReference friendReference = (DocumentReference) HelperClass.db.collection(Collections.USERS).document(friendID);
						friendReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
							@Override
							public void onComplete(@NonNull Task<DocumentSnapshot> task) {
								//We should always check to see if the task was completed successfully
								if (task.isSuccessful()) {
									DocumentSnapshot friendDocumentSnapshot = (DocumentSnapshot) task.getResult();
									//Check to make sure that the friend document actually exists
									if (friendDocumentSnapshot.exists()) {
										//Call the add friend function
										Task<Void> addFriendTask = User.addFriend(friendID);
										addFriendTask.addOnCompleteListener(new OnCompleteListener<Void>() {
											@Override
											public void onComplete(@NonNull Task<Void> task) {
												//Check if the task was sucessful and that the result
												//is non null as the function is using continuation
												if (task.isSuccessful() && task.getResult() != null) {
													Toast.makeText(context, "Successfully added a new friend.", Toast.LENGTH_SHORT).show();
												} else {
													Toast.makeText(context, "Error adding friend.\n", Toast.LENGTH_SHORT).show();
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
						//The add button should only be enabled if the edittext is non-empty
						if (count > 0) {
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
		
		//onClickListener for when the create group button is clicked
		button_addGroup.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				View createNewGroupView = inflater.inflate(R.layout.createnewgrouplayout, null);
				final AlertDialog.Builder createNewGroupDialog = new AlertDialog.Builder(context);
				createNewGroupDialog.setTitle("Create new group");
				createNewGroupDialog.setMessage("Enter the details of your new group.\n\nSelect the friends you to be a part of this group.");
				createNewGroupDialog.setView(createNewGroupView);
				createNewGroupDialog.setNegativeButton("Cancel", null);
				
				final ListView listView_Friends = (ListView) createNewGroupView.findViewById(R.id.listView_Friends);
				final EditText editText_GroupName = (EditText) createNewGroupView.findViewById(R.id.editText_GroupName);
				
				final List<String> friendsKeyList = new ArrayList<String>(User.friendInfo.keySet());
				final List<String> friendsNameList = new ArrayList<String>();
				
				for (String friendID : friendsKeyList) {
					Map<String, Object> friendUserData = (Map<String, Object>) User.friendInfo.get(friendID);
					friendsNameList.add(friendUserData.get(Collections.Users.FIRSTNAME).toString() + " " + friendUserData.get(Collections.Users.LASTNAME).toString());
				}
				
				//Create an arrayadapter with the list of our friend names
				final ArrayAdapter<String> adapterFriends = new ArrayAdapter<String>(context,
						android.R.layout.simple_list_item_multiple_choice, android.R.id.text1, friendsNameList);
				//Set the adapter to our listview
				listView_Friends.setAdapter(adapterFriends);
				
				//This is bugged
				//https://stackoverflow.com/questions/11382539/trouble-with-android-listview-selection-changes-when-switching-choicemode
				listView_Friends.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
				
				//When an item in the list view is clicked, we want to toggle its checkbox
				listView_Friends.setOnItemClickListener(new AdapterView.OnItemClickListener() {
					@Override
					public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
						CheckedTextView checkedTextView = ((CheckedTextView) view);
						checkedTextView.setChecked(!checkedTextView.isChecked());
					}
				});
				
				//If the user clicks "Create" then do the following
				createNewGroupDialog.setPositiveButton("Create", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						//Get the friends that were checked
						ArrayList<String> friendIDsToCreateGroupWith = new ArrayList<>();
						for (int i = 0; i < listView_Friends.getCount(); i++) {
							Log.d(LOG.INFORMATION, "Checking " + listView_Friends.getItemAtPosition(i).toString());
							if (listView_Friends.isItemChecked(i)) {
								Log.d(LOG.INFORMATION, "You have checked " + friendsKeyList.get(i) + " (" + friendsNameList.get(i) + " )");
								friendIDsToCreateGroupWith.add(friendsKeyList.get(i));
							}
						}
						
						//Add our self to the list of people to add to this new group
						friendIDsToCreateGroupWith.add(HelperClass.auth.getCurrentUser().getUid());
						String name = editText_GroupName.getText().toString();
						
						//Call the create group function
						HelperClass.createGroup(name, friendIDsToCreateGroupWith).addOnCompleteListener(new OnCompleteListener<Void>() {
							@Override
							public void onComplete(@NonNull Task<Void> task) {
								if (task.isSuccessful()) {
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
						//The create button should be enabled if the group name is not empty
						if (count > 0) {
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
		
		//Get friends information any time a friends information changes
		final Runnable getFriendsInformation = new Runnable() {
			@Override
			public void run() {
				final List<String> friendsKeyList = new ArrayList<String>(User.friends);
				final List<String> friendsNameList = new ArrayList<String>();
				
				//Check each friend and to an array for the listview
				for (String friendID : friendsKeyList) {
					if (!User.friendInfo.containsKey(friendID))
						continue;
					Map<String, Object> friendUserData = (Map<String, Object>) User.friendInfo.get(friendID);
					friendsNameList.add(friendUserData.get(Collections.Users.FIRSTNAME).toString() + " " + friendUserData.get(Collections.Users.LASTNAME).toString());
				}
				
				//Adapter for listview
				final ArrayAdapter<String> adapterFriends = new ArrayAdapter<String>(context,
						android.R.layout.simple_list_item_1, android.R.id.text1, friendsNameList);
				listView_Friends.setAdapter(adapterFriends);
				
				//Display the number of friends
				if (friendsNameList.size() > 0) {
					textView_FriendsCount.setText("(" + Integer.toString(friendsNameList.size()) + ")");
				} else {
					textView_FriendsCount.setText("");
				}
				
				//When the user clicks on a friends name, it should open the profile view activity
				listView_Friends.setOnItemClickListener(new AdapterView.OnItemClickListener() {
					@Override
					public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
						final Intent intent = new Intent(context, userprofile.class);
						intent.putExtra("userID", friendsKeyList.get(position));
						intent.putExtra("userName", friendsNameList.get(position));
						startActivity(intent);
					}
				});
				
				//When the user long clicks a friends name, it should prompt them asking if they want
				//to unfriend the person
				listView_Friends.setLongClickable(true);
				listView_Friends.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
					@Override
					public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
						Log.d(LOG.INFORMATION, "Long clicked.");
						
						final AlertDialog.Builder deleteFriendDialog = new AlertDialog.Builder(context);
						deleteFriendDialog.setTitle("Remove friend");// + listView_Friends.getItemAtPosition(position).toString() + "?");
						deleteFriendDialog.setMessage("Are you sure you want to unfriend " + listView_Friends.getItemAtPosition(position).toString() + "?");
						deleteFriendDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which) {
								final String friendID = friendsKeyList.get(position);
								DocumentReference friendReference = (DocumentReference) HelperClass.db.collection(Collections.USERS).document(friendID);
								friendReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
									@Override
									public void onComplete(@NonNull Task<DocumentSnapshot> task) {
										if (task.isSuccessful()) {
											DocumentSnapshot friendDocumentSnapshot = (DocumentSnapshot) task.getResult();
											if (friendDocumentSnapshot.exists()) {
												Task<Void> addFriendTask = User.removeFriend(friendID);
												addFriendTask.addOnCompleteListener(new OnCompleteListener<Void>() {
													@Override
													public void onComplete(@NonNull Task<Void> task) {
														if (task.isSuccessful()) {
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
		//Add the runnable to the list of runnables for friend documents
		User.friendInfoRunnables.put("mainpage", getFriendsInformation);
		
		//Get group information any time the group information changes
		final Runnable getGroupInformation = new Runnable() {
			@Override
			public void run() {
				final List<String> groupsKeyList = new ArrayList<String>(User.groups);
				final List<String> groupsNameList = new ArrayList<String>();
				
				for (String groupID : groupsKeyList) {
					Log.d(LOG.INFORMATION, "Getting GroupID: " + groupID);
					Log.d(LOG.INFORMATION, "GroupInfo: " + User.groupInfo.toString());
					
					if (!User.groupInfo.containsKey(groupID))
						continue;
					
					Map<String, Object> groupInfoData = (Map<String, Object>) User.groupInfo.get(groupID);
					Log.d(LOG.INFORMATION, "GroupInfoData: " + groupInfoData.toString());
					groupsNameList.add(groupInfoData.get("Name").toString());
				}
				
				if (groupsNameList.size() > 0) {
					textView_GroupsCount.setText("(" + Integer.toString(groupsNameList.size()) + ")");
				} else {
					textView_GroupsCount.setText("");
				}
				
				Log.d(LOG.INFORMATION, "GroupsNameList: " + groupsNameList.toString());
				
				Log.d(LOG.INFORMATION, "GroupInfo: " + User.groupInfo.toString());
				
				final ArrayAdapter<String> adapterGroups = new ArrayAdapter<String>(context,
						android.R.layout.simple_list_item_1, android.R.id.text1, groupsNameList);
				listView_Groups.setAdapter(adapterGroups);
				
				//Clicking a group on the listview opens the grouplist viewer
				listView_Groups.setOnItemClickListener(new AdapterView.OnItemClickListener() {
					@Override
					public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
						final Intent intent = new Intent(context, groupprofile.class);
						intent.putExtra("groupID", groupsKeyList.get(position));
						intent.putExtra("groupName", groupsNameList.get(position));
						startActivity(intent);
					}
				});
				
				//Long clicking the listview means leaving the group
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
								final DocumentReference groupReference = (DocumentReference) HelperClass.db.collection("UserGroups").document(HelperClass.auth.getCurrentUser().getUid());
								groupReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
									@Override
									public void onComplete(@NonNull Task<DocumentSnapshot> task) {
										if (task.isSuccessful()) {
											DocumentSnapshot groupDocumentSnapshot = (DocumentSnapshot) task.getResult();
											if (groupDocumentSnapshot.exists()) {
												final Map<String, Object> groupDataMap = new HashMap<>();
												groupDataMap.put("Groups", FieldValue.arrayRemove(friendID));
												Task<Void> addFriendTask = groupReference.update(groupDataMap);
												addFriendTask.addOnCompleteListener(new OnCompleteListener<Void>() {
													@Override
													public void onComplete(@NonNull Task<Void> task) {
														if (task.isSuccessful()) {
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
		//Add the runnable to the list of runnables for group documents
		User.groupInfoRunnables.put("mainpage", getGroupInformation);
		
		//Toggling the friends switch affects whether the friends layout is visible or not
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
		
		//Toggling the groups switch affects whether the friends layout is visible or not
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
		
		//Get group information any time the group information changes
		Runnable getUserInformation = new Runnable() {
			@Override
			public void run() {
				textView_UserFullName.setText(User.userInfo.get(Collections.Users.FIRSTNAME) + " " + User.userInfo.get(Collections.Users.LASTNAME));
				getFriendsInformation.run();
			}
		};
		
		getUserInformation.run();
		//Add the runnable to the list of runnables for user document
		User.userInfoRunnables.put("mainpage", getUserInformation);
		
		//Edit user information
		button_EditUserInformation.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				View userInfoView = inflater.inflate(R.layout.registrationlayout, null);
				final AlertDialog.Builder updateUserDialog = new AlertDialog.Builder(context);
				updateUserDialog.setTitle("Update account details");
				updateUserDialog.setView(userInfoView);
				
				final LinearLayout linearLayout_Password = (LinearLayout) userInfoView.findViewById(R.id.linearLayout_Password);
				linearLayout_Password.setVisibility(View.GONE);
				
				final LinearLayout linearLayout_DateOfBirth = (LinearLayout) userInfoView.findViewById(R.id.linearLayout_DateOfBirth);
				linearLayout_DateOfBirth.setVisibility(View.GONE);
				
				final LinearLayout linearLayout_Email = (LinearLayout) userInfoView.findViewById(R.id.linearLayout_Email);
				linearLayout_Email.setVisibility(View.GONE);
				
				final EditText editText_FirstName = (EditText) userInfoView.findViewById(R.id.editText_FirstName);
				final EditText editText_LastName = (EditText) userInfoView.findViewById(R.id.editText_LastName);
				final EditText editText_Location = (EditText) userInfoView.findViewById(R.id.editText_Location);
				final EditText editText_Email = (EditText) userInfoView.findViewById(R.id.editText_Email);
				final EditText editText_Password = (EditText) userInfoView.findViewById(R.id.editText_Password);
				final EditText editText_SecurityQuestion = (EditText) userInfoView.findViewById(R.id.editText_SecurityQuestion);
				final EditText editText_SecurityAnswer = (EditText) userInfoView.findViewById(R.id.editText_SecurityAnswer);
				
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
						
						//Doesn't really matter if it worked or not, for the moment
						HelperClass.db.collection(Collections.USERS).document(HelperClass.auth.getCurrentUser().getUid()).update(updatedUserData).addOnCompleteListener(new OnCompleteListener<Void>() {
							@Override
							public void onComplete(@NonNull Task<Void> task) {
								if (task.isSuccessful()) {
								
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
		
		//Delete account
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
								if (task.isSuccessful()) {
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
			}
		});
		
		//Sign out
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
