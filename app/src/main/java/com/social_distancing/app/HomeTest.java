package com.social_distancing.app;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;

public class HomeTest extends AppCompatActivity {
	final Context context = this;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_home_test);
		
		final Button usermanagement = (Button)findViewById(R.id.usermanagement);
		final Button userprofile = (Button)findViewById(R.id.userprofile);
		
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
					}
				}
			});
		}
	}
	
}
