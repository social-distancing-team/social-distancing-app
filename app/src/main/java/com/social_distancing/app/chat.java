package com.social_distancing.app;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
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
import android.widget.Space;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.rpc.Help;
import com.social_distancing.app.HelperClass;
import com.social_distancing.app.HelperClass.Collections;
import com.social_distancing.app.HelperClass.User;
import com.social_distancing.app.HelperClass.LOG;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class chat extends AppCompatActivity {
	
	public static final String userID = HelperClass.auth.getCurrentUser().getUid().toString();
	//public static final String chatID = "D8mxodEsvADhvBdrPZTQ";
	static final SimpleDateFormat sfd = new SimpleDateFormat("dd/MM/yy");
	
	
	static final SimpleDateFormat sfd2 = new SimpleDateFormat("kk:mm:ss dd/MM/yy");
	
	final Context context = this;
	
	Map<String, View> messageViews = new HashMap<>();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_chat);
		
		final LinearLayout rootLayout = (LinearLayout)findViewById(R.id.rootLayout);
		
		
		String uID = getIntent().getStringExtra("userID");
		
		
		Log.d(LOG.INFORMATION, "Chats : " + User.userInfo.get("Chats").toString());
		
		Map<String, Object> chats = (Map<String, Object>)User.userInfo.get("Chats");
		
		
		final String chatID = chats.get(uID).toString();
		
		final DocumentReference chatDocument = HelperClass.db.collection(Collections.CHATS).document(chatID);
		/*
		chatDocument.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
			@Override
			public void onComplete(@NonNull Task<DocumentSnapshot> task) {
				if (task.isSuccessful()){
					DocumentSnapshot documentSnapshot = (DocumentSnapshot) task.getResult();
					Map<String, Object> data = documentSnapshot.getData();
					rootLayout.removeAllViews();
					TextView textView = new TextView(context);
					textView.setText(data.get(Collections.Chat.NAME).toString());
					Log.d(LOG.INFORMATION, data.toString());
					rootLayout.addView(textView);
					Log.d(LOG.INFORMATION, "DONE");
				}
			}
		});
		 */
		
		chatDocument.addSnapshotListener(new EventListener<DocumentSnapshot>() {
			@Override
			public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
				gotChatData(documentSnapshot.getData());
			}
		});
		
		final EditText editText = (EditText)findViewById(R.id.enterMessage);
		Button sendMessage = (Button)findViewById(R.id.sendMessage);
		final ScrollView chatScrollView = (ScrollView)findViewById(R.id.chatScrollView);
		
		sendMessage.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				
				final DocumentReference documentReference = HelperClass.db.collection(Collections.MESSAGES).document();
				
				Map<String, Object> data = new HashMap<>();
				data.put(Collections.Message.USERID, userID);
				data.put(Collections.Message.CONTENT, editText.getText().toString());
				data.put(Collections.Message.TIMESTAMP, new Timestamp(new Date()));
				data.put(Collections.Message.CHATID, chatID);
				
				editText.onEditorAction(EditorInfo.IME_ACTION_DONE);
				editText.setText("");
				
				documentReference.set(data).addOnCompleteListener(new OnCompleteListener<Void>() {
					@Override
					public void onComplete(@NonNull Task<Void> task) {
						if (task.isSuccessful()){
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
	
	void gotChatData(Map<String, Object> data){
		final LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		
		Log.d(LOG.INFORMATION, "Got chat data: " + data.toString());
		
		final LinearLayout rootLayout = (LinearLayout)findViewById(R.id.rootLayout);
		final TextView lastActive = (TextView)findViewById(R.id.lastActive);
		final TextView chatName = (TextView)findViewById(R.id.chatName);
		final LinearLayout chatLayout = (LinearLayout)findViewById(R.id.chatLayout);
		
		lastActive.setText("Last active on " + sfd.format(((Timestamp)data.get(Collections.Chat.LASTMESSAGETIMESTAMP)).toDate()));
		if (data.get(Collections.Chat.NAME).equals("") || true){
			String username = getIntent().getStringExtra("name");
			if (username == null){
				username = "User";
				username = HelperClass.auth.getCurrentUser().getUid();
			}
			chatName.setText(username);
		}else {
			chatName.setText(data.get(Collections.Chat.NAME).toString());
		}
		
		final ArrayList<String> messages = (ArrayList<String>)data.get(Collections.Chat.MESSAGES);
		ArrayList<Task<DocumentSnapshot>> tasks = new ArrayList<>();
		
		for (String message : messages){
			if (messageViews.containsKey(message))
				continue;
			
			DocumentReference messageReference = HelperClass.db.collection(Collections.MESSAGES).document(message);
			
			messageReference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
				@Override
				public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
					Map<String, Object> data = documentSnapshot.getData();
					Log.d(LOG.INFORMATION, "Got Message data: " + data.toString());
						
						/*
						
						TextView textView = new TextView(context);
						textView.setText(data.get(Collections.Message.CONTENT).toString());
						
						textView.setTextSize(16);
						textView.setPadding(20,20,20,20);
						textView.setBackgroundColor(Color.CYAN);
						LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
						
						if (data.get(Collections.Message.USERID) != null && data.get(Collections.Message.USERID).toString().equals(userID)){
							layoutParams.gravity = Gravity.RIGHT;
							textView.setBackgroundColor(Color.GREEN);
						}
						
						textView.setLayoutParams(layoutParams);
						
						
						Space space = new Space(context);
						space.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, 20));
						
						
						chatLayout.addView(textView);
						chatLayout.addView(space);
						 */
					
					//for (int i =0; i< 10; i++){
					boolean b = false;
					View newMessageView;
					if (messageViews.containsKey(documentSnapshot.getId()))
						newMessageView = messageViews.get(documentSnapshot.getId());
					else {
						newMessageView = inflater.inflate(R.layout.chatmessage, null);
						messageViews.put(documentSnapshot.getId(), newMessageView);
						b = true;
					}
					
					LinearLayout newMessageLayout = (LinearLayout)newMessageView.findViewById(R.id.messageLayout);
					TextView messageContent = (TextView)newMessageLayout.findViewById(R.id.messageContent);
					TextView messageTimestamp = (TextView)newMessageLayout.findViewById(R.id.messageTimestamp);
					
					messageContent.setText(data.get(Collections.Message.CONTENT).toString());
					if (data.get(Collections.Message.TIMESTAMP)!=null)
						messageTimestamp.setText(sfd2.format(((Timestamp)data.get(Collections.Message.TIMESTAMP)).toDate()));
					
					if (data.get(Collections.Message.USERID)!=null && data.get(Collections.Message.USERID).toString().equals(userID)){
						newMessageLayout.setBackgroundColor(Color.argb(0.1f, 0.0f, 1.0f, 0.0f));
						LinearLayout.LayoutParams layoutParams =  new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
						layoutParams.gravity = Gravity.RIGHT;
						newMessageLayout.setLayoutParams(layoutParams);
					} else {
						newMessageLayout.setBackgroundColor(Color.argb(0.1f, 1, 0.0f, 0.0f));
						LinearLayout.LayoutParams layoutParams =  new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
						layoutParams.gravity = Gravity.LEFT;
						newMessageLayout.setLayoutParams(layoutParams);
					}
					
					final ScrollView chatScrollView = (ScrollView)findViewById(R.id.chatScrollView);
					
					if (b) {
						chatLayout.addView(newMessageView);
						Log.d(LOG.WARNING, "AAAAAD32e21323ee");
						chatScrollView.fullScroll(ScrollView.FOCUS_DOWN);
						lastActive.setText("Last active on " + sfd.format(((Timestamp)data.get(Collections.Message.TIMESTAMP)).toDate()));
						
					}
				}
			});
			
			//tasks.add(messageReference.get());
		}
		
		
		Tasks.whenAllComplete(tasks).addOnCompleteListener(new OnCompleteListener<List<Task<?>>>() {
			@Override
			public void onComplete(@NonNull Task<List<Task<?>>> task) {
				if (true)
					return;
				chatLayout.removeAllViews();
				List<Task<?>> tasks1 = task.getResult();
				for (Task tasks2 : tasks1){
					if (task.isSuccessful()){
						DocumentSnapshot documentSnapshot = (DocumentSnapshot)tasks2.getResult();
						//Log.d(LOG.INFORMATION, documentSnapshot.getId());
						Map<String, Object> data = documentSnapshot.getData();
						Log.d(LOG.INFORMATION, "Got Message data: " + data.toString());
						
						/*
						
						TextView textView = new TextView(context);
						textView.setText(data.get(Collections.Message.CONTENT).toString());
						
						textView.setTextSize(16);
						textView.setPadding(20,20,20,20);
						textView.setBackgroundColor(Color.CYAN);
						LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
						
						if (data.get(Collections.Message.USERID) != null && data.get(Collections.Message.USERID).toString().equals(userID)){
							layoutParams.gravity = Gravity.RIGHT;
							textView.setBackgroundColor(Color.GREEN);
						}
						
						textView.setLayoutParams(layoutParams);
						
						
						Space space = new Space(context);
						space.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, 20));
						
						
						chatLayout.addView(textView);
						chatLayout.addView(space);
						 */
						
						//for (int i =0; i< 10; i++){
							View newMessageView;
							if (messageViews.containsKey(documentSnapshot.getId()))
								newMessageView = messageViews.get(documentSnapshot.getId());
							else
								newMessageView = inflater.inflate(R.layout.chatmessage, null);
							
							LinearLayout newMessageLayout = (LinearLayout)newMessageView.findViewById(R.id.messageLayout);
							TextView messageContent = (TextView)newMessageLayout.findViewById(R.id.messageContent);
							TextView messageTimestamp = (TextView)newMessageLayout.findViewById(R.id.messageTimestamp);
							
							messageContent.setText(data.get(Collections.Message.CONTENT).toString());
							if (data.get(Collections.Message.TIMESTAMP)!=null)
								messageTimestamp.setText(sfd2.format(((Timestamp)data.get(Collections.Message.TIMESTAMP)).toDate()));
							
							if (data.get(Collections.Message.USERID)!=null){
								if (data.get(Collections.Message.USERID).toString().equals(userID)){
									newMessageLayout.setBackgroundColor(Color.argb(0.1f, 0.0f, 1.0f, 0.0f));
									LinearLayout.LayoutParams layoutParams =  new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
									layoutParams.gravity = Gravity.RIGHT;
									newMessageLayout.setLayoutParams(layoutParams);
								}
							}
	
							
							chatLayout.addView(newMessageView);
						//}
						
					} else {
						Log.d(LOG.ERROR, "Get message failed. " + task.getException().toString());
					}
				}
			}
		});
		
		
	}
}
