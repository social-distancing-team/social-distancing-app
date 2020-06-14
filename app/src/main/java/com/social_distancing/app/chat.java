/*
 *	Project:		Remote Life
 * 	Last edited:	13/06/2020
 * 	Author:			Karan Bajwa
 */

package com.social_distancing.app;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

/*
OS and UI
 */
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

/*
Tasks, Async and Firebase
 */
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestoreException;

/*
Collections
 */
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
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

public class chat extends AppCompatActivity {
	
	static final SimpleDateFormat sfd = new SimpleDateFormat("dd/MM/yy");
	static final SimpleDateFormat sfd2 = new SimpleDateFormat("kk:mm:ss dd/MM/yy");
	
	final Context context = this;
	
	//Map each message ID to a view
	Map<String, View> messageViews = new HashMap<>();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_chat);
		getSupportActionBar().hide();
		
		final LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		
		final LinearLayout rootLayout = (LinearLayout) findViewById(R.id.rootLayout);
		
		//Get the chat ID of the chat we want to read
		final String chatID = getIntent().getStringExtra("chatID");
		final String username = getIntent().getStringExtra("name");
		
		final DocumentReference chatDocument = HelperClass.db.collection(Collections.CHATS).document(chatID);
		
		//Add a snapshot listener for whenever new chat data arrives
		chatDocument.addSnapshotListener(new EventListener<DocumentSnapshot>() {
			@Override
			public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
				Map<String, Object> data = documentSnapshot.getData();
				
				Log.d(LOG.INFORMATION, "Got chat data: " + data.toString());
				
				final LinearLayout rootLayout = (LinearLayout) findViewById(R.id.rootLayout);
				final TextView lastActive = (TextView) findViewById(R.id.lastActive);
				final TextView chatName = (TextView) findViewById(R.id.chatName);
				final LinearLayout chatLayout = (LinearLayout) findViewById(R.id.chatLayout);
				
				chatName.setText(username);
				if (data.get(Collections.Chat.MESSAGES).toString().equals(""))
					return;
				
				//Get a list of all the messages
				final ArrayList<String> messages = (ArrayList<String>) data.get(Collections.Chat.MESSAGES);
				ArrayList<Task<DocumentSnapshot>> tasks = new ArrayList<>();
				
				//For each message
				for (String message : messages) {
					//If a view for the message has already been created, let's skip it
					if (messageViews.containsKey(message))
						continue;
					
					DocumentReference messageReference = HelperClass.db.collection(Collections.MESSAGES).document(message);
					
					//Add a listener for each message
					messageReference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
						@Override
						public void onEvent(@Nullable final DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
							final Map<String, Object> data = documentSnapshot.getData();
							Log.d(LOG.INFORMATION, "Got Message data: " + data.toString());
							
							boolean newMessage = false;
							final View newMessageView;
							
							//Check if a view for the message exists, else create one
							if (messageViews.containsKey(documentSnapshot.getId()))
								newMessageView = messageViews.get(documentSnapshot.getId());
							else {
								newMessageView = inflater.inflate(R.layout.chatmessage, null);
								messageViews.put(documentSnapshot.getId(), newMessageView);
								newMessage = true;
							}
							
							//Long click for deleting message
							newMessageView.setLongClickable(true);
							newMessageView.setOnLongClickListener(new View.OnLongClickListener() {
								@Override
								public boolean onLongClick(View v) {
									if (HelperClass.auth.getCurrentUser().getUid().toString().equals(data.get(Collections.Message.USERID).toString())) {
										final AlertDialog.Builder deleteMessageDialog = new AlertDialog.Builder(context);
										deleteMessageDialog.setTitle("Delete message");
										deleteMessageDialog.setMessage("Are you sure you want to delete this message?");
										
										deleteMessageDialog.setNegativeButton("No", null);
										deleteMessageDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
											@Override
											public void onClick(DialogInterface dialog, int which) {
												final DocumentReference chatDocument = HelperClass.db.collection(Collections.CHATS).document(chatID);
												
												Map<String, Object> chatData = new HashMap<>();
												chatData.put(Collections.Chat.MESSAGES, FieldValue.arrayRemove(documentSnapshot.getId()));
												
												chatDocument.update(chatData).addOnCompleteListener(new OnCompleteListener<Void>() {
													@Override
													public void onComplete(@NonNull Task<Void> task) {
														if (task.isSuccessful()) {
															newMessageView.setVisibility(View.GONE);
														}
													}
												});
											}
										});
										
										final AlertDialog dialog = deleteMessageDialog.create();
										dialog.show();
									}
									
									return false;
								}
							});
							
							//Normal click for editing messages
							newMessageView.setOnClickListener(new View.OnClickListener() {
								@Override
								public void onClick(View v) {
									if (data.containsKey("sendingUserID") && HelperClass.auth.getCurrentUser().getUid().toString().equals(data.get("sendingUserID").toString())) {
										final AlertDialog.Builder deditMessageDialog = new AlertDialog.Builder(context);
										deditMessageDialog.setTitle("Edit message");// + listView_Friends.getItemAtPosition(position).toString() + "?");
										
										final EditText messageText = new EditText(context);
										LinearLayout newMessageLayout = (LinearLayout) newMessageView.findViewById(R.id.messageLayout);
										final TextView messageContent = (TextView) newMessageLayout.findViewById(R.id.messageContent);
										TextView messageTimestamp = (TextView) newMessageLayout.findViewById(R.id.messageTimestamp);
										
										messageText.setText(messageContent.getText());
										messageText.setPadding(10, 0, 0, 0);
										
										deditMessageDialog.setView(messageText);
										
										deditMessageDialog.setNegativeButton("Cancel", null);
										deditMessageDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
											@Override
											public void onClick(DialogInterface dialog, int which) {
												if (messageText.getText().toString().equals("")) {
													newMessageView.setVisibility(View.GONE);
												} else {
													messageContent.setText(messageText.getText().toString());
												}
											}
										});
										
										final AlertDialog dialog = deditMessageDialog.create();
										dialog.show();
									}
								}
							});
							
							LinearLayout newMessageLayout = (LinearLayout) newMessageView.findViewById(R.id.messageLayout);
							TextView messageContent = (TextView) newMessageLayout.findViewById(R.id.messageContent);
							TextView messageTimestamp = (TextView) newMessageLayout.findViewById(R.id.messageTimestamp);
							
							messageContent.setText(data.get(Collections.Message.CONTENT).toString());
							if (data.get(Collections.Message.TIMESTAMP) != null)
								messageTimestamp.setText(sfd2.format(((Timestamp) data.get(Collections.Message.TIMESTAMP)).toDate()));
							
							final String userID = HelperClass.auth.getCurrentUser().getUid().toString();
							
							//If we sent the message, set the background to green and place message to the right
							if (data.get(Collections.Message.USERID).toString().equals(userID)) {
								newMessageLayout.setBackgroundColor(Color.argb(0.1f, 0.0f, 1.0f, 0.0f));
								LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
								layoutParams.gravity = Gravity.RIGHT;
								newMessageLayout.setLayoutParams(layoutParams);
							} else { //If another user sent the message, set background to red and place message to the left.
								newMessageLayout.setBackgroundColor(Color.argb(0.1f, 1, 0.0f, 0.0f));
								LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
								layoutParams.gravity = Gravity.LEFT;
								newMessageLayout.setLayoutParams(layoutParams);
							}
							
							final ScrollView chatScrollView = (ScrollView) findViewById(R.id.chatScrollView);
							
							//If a new message view has been created, scroll the messages to the bottom
							if (newMessage) {
								chatLayout.addView(newMessageView);
								chatScrollView.fullScroll(ScrollView.FOCUS_DOWN);
								//lastActive.setText("Last active on " + sfd.format(((Timestamp)data.get(Collections.Message.TIMESTAMP)).toDate()));
							}
						}
					});
				}
			}
		});
		
		final EditText editText = (EditText) findViewById(R.id.enterMessage);
		Button sendMessage = (Button) findViewById(R.id.sendMessage);
		final ScrollView chatScrollView = (ScrollView) findViewById(R.id.chatScrollView);
		
		//When the Send button is clicked
		sendMessage.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				final DocumentReference documentReference = HelperClass.db.collection(Collections.MESSAGES).document();
				
				Map<String, Object> data = new HashMap<>();
				final String userID = HelperClass.auth.getCurrentUser().getUid();
				
				data.put(Collections.Message.USERID, userID);
				data.put(Collections.Message.CONTENT, editText.getText().toString());
				data.put(Collections.Message.TIMESTAMP, new Timestamp(new Date()));
				data.put(Collections.Message.CHATID, chatID);
				
				//Clear the edit text when clicked
				editText.onEditorAction(EditorInfo.IME_ACTION_DONE);
				editText.setText("");
				
				//Update the chat data
				documentReference.set(data).addOnCompleteListener(new OnCompleteListener<Void>() {
					@Override
					public void onComplete(@NonNull Task<Void> task) {
						if (task.isSuccessful()) {
							final Map<String, Object> map = new HashMap<>();
							map.put(Collections.Chat.MESSAGES, FieldValue.arrayUnion(documentReference.getId()));
							
							chatDocument.update(map).addOnCompleteListener(new OnCompleteListener<Void>() {
								@Override
								public void onComplete(@NonNull Task<Void> task) {
									chatScrollView.fullScroll(ScrollView.FOCUS_DOWN);
								}
							});
						}
					}
				});
			}
		});
	}
}
