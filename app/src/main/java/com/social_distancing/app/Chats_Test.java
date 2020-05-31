package com.social_distancing.app;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.rpc.Help;
import com.social_distancing.app.HelperClass;
import com.social_distancing.app.HelperClass.User;
import com.social_distancing.app.HelperClass.Collections;

public class Chats_Test extends AppCompatActivity {
	
	public static final String email =  "passwordispassword@email.com";
	public static final String password = "password";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_chats__test);
		
		User.login(email, password).addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
			@Override
			public void onComplete(@NonNull Task<DocumentSnapshot> task) {
				if (task.isSuccessful() && task.getResult() != null){
					DocumentSnapshot documentSnapshot = (DocumentSnapshot)task.getResult();
					
				}
			}
		});
		
	}
}
