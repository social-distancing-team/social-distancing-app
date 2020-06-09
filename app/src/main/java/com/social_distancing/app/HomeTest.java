package com.social_distancing.app;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.ArrayList;
import java.util.Map;

import com.social_distancing.app.HelperClass;
import com.social_distancing.app.HelperClass.Collections;
import com.social_distancing.app.HelperClass.User;
import com.social_distancing.app.HelperClass.LOG;

public class HomeTest extends AppCompatActivity {
	final Context context = this;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_home_test);
		
		final Button usermanagement = (Button)findViewById(R.id.usermanagement);
		final Button userprofile = (Button)findViewById(R.id.userprofile);
		final Button chatTest = (Button)findViewById(R.id.chatTest);
		final LinearLayout rootLayout = (LinearLayout)findViewById(R.id.rootLayout);

		
		if (true) {
			HelperClass.User.logout();
			HelperClass.User.login(null, null).addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
				@Override
				public void onComplete(@NonNull Task<DocumentSnapshot> task) {
					if (task.getResult() != null && task.isSuccessful()) {
						usermanagement.setEnabled(true);
						usermanagement.setOnClickListener(new View.OnClickListener() {
							@Override
							public void onClick(View v) {
								final Intent intent = new Intent(context, UserManagement.class);
								startActivity(intent);
							}
						});
						
						userprofile.setEnabled(true);
						userprofile.setOnClickListener(new View.OnClickListener() {
							@Override
							public void onClick(View v) {
								final Intent intent = new Intent(context, ViewUserProfile.class);
								intent.putExtra("userID", HelperClass.auth.getCurrentUser().getUid());
								startActivity(intent);
							}
						});
						
						chatTest.setEnabled(true);
						chatTest.setOnClickListener(new View.OnClickListener() {
							@Override
							public void onClick(View v) {
								final Intent intent = new Intent(context, chat.class);
								intent.putExtra("userID", HelperClass.auth.getCurrentUser().getUid());
								startActivity(intent);
							}
						});
					}
					
					
					DocumentReference documentReference = HelperClass.db.collection("UserChats").document(HelperClass.auth.getCurrentUser().getUid().toString());
					documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
						@Override
						public void onComplete(@NonNull Task<DocumentSnapshot> task) {
							if (task.isSuccessful()){
								DocumentSnapshot documentSnapshot = (DocumentSnapshot)task.getResult();
								Map<String, Object> data = documentSnapshot.getData();
								ArrayList<String> chats = (ArrayList<String>)data.get("Chats");
								for (final String chat: chats){
									DocumentReference chatDocumentReference = HelperClass.db.collection(Collections.CHATS).document(chat);
									chatDocumentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
										@Override
										public void onComplete(@NonNull Task<DocumentSnapshot> task) {
											if (task.isSuccessful()){
												DocumentSnapshot chatDocumentSnapshot = (DocumentSnapshot)task.getResult();
												Map<String, Object> chatData = chatDocumentSnapshot.getData();
												TextView textView = new TextView(context);
												textView.setText(chatData.get(Collections.Chat.NAME).toString() + " : " + chatData.get(Collections.Chat.USERS).toString());
												rootLayout.addView(textView);
											}
										}
									});
								}
							}
						}
					});
				}
			});
		}
		

		
	}
	
}
