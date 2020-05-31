package com.social_distancing.app;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.rpc.Help;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

public class UserManagement extends AppCompatActivity {
	Runnable runnable = new Runnable() {
		@Override
		public void run() {
			start();
			Log.d(HelperClass.LOG.INFORMATION, "UserDocument reference runnable called.");
		}
	};
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getSupportActionBar().hide();
		
		setContentView(R.layout.activity_user_management);
		
		//HelperClass.User.runnables.add(runnable);
		
		HelperClass.User.userInfoRunnables.put("UserManagement", runnable);
		
		
		if (!HelperClass.User.isLoggedIn() || !HelperClass.User.initiliased || HelperClass.User.userInfo == null) {
			HelperClass.User.logout();
			HelperClass.User.login(null, null).addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
				@Override
				public void onComplete(@NonNull Task<DocumentSnapshot> task) {
					if (task.getResult() != null && task.isSuccessful()) {
						start();
					}
				}
			});
		} else {
			Log.d(HelperClass.LOG.INFORMATION, "Already logged in.");
			Log.d(HelperClass.LOG.INFORMATION, "Userinfo : " + HelperClass.User.userInfo.toString());
			//register();
			start();
		}
		
	}
	
	void register(){
		DocumentReference userReference = HelperClass.db.collection(HelperClass.Collections.USERS).document(HelperClass.auth.getCurrentUser().getUid());
		userReference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
			@Override
			public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
				Map<String, Object> userInfo = documentSnapshot.getData();
				start();
				
				Button editdetails = (Button)findViewById(R.id.editdetails);
				editdetails.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
					
					}
				});
			}
		});
	}
	
	void start(){
		
		SimpleDateFormat sfd = new SimpleDateFormat("dd-MM-yyyy");
		
		final EditText email = (EditText)findViewById(R.id.email);
		final EditText firstname = (EditText)findViewById(R.id.firstname);
		final EditText lastname = (EditText)findViewById(R.id.lastname);
		final EditText location = (EditText)findViewById(R.id.location);
		final EditText securityquestion = (EditText)findViewById(R.id.securityquestion);
		final EditText answer = (EditText)findViewById(R.id.answer);
		
		TextView fullname = (TextView)findViewById(R.id.fullname);
		TextView joindate = (TextView)findViewById(R.id.joindate);
		TextView from = (TextView)findViewById(R.id.from);
		
		email.setText(HelperClass.User.userInfo.get(HelperClass.Collections.Users.EMAIL).toString());
		firstname.setText(HelperClass.User.userInfo.get(HelperClass.Collections.Users.FIRSTNAME).toString());
		lastname.setText(HelperClass.User.userInfo.get(HelperClass.Collections.Users.LASTNAME).toString());
		location.setText(HelperClass.User.userInfo.get(HelperClass.Collections.Users.LOCATION).toString());
		securityquestion.setText(HelperClass.User.userInfo.get(HelperClass.Collections.Users.SECURITYQUESTION).toString());
		answer.setText(HelperClass.User.userInfo.get(HelperClass.Collections.Users.SECURITYANSWER).toString());
		
		fullname.setText(firstname.getText() + " " + lastname.getText());
		joindate.setText("Joined on " + sfd.format(((Timestamp)HelperClass.User.userInfo.get(HelperClass.Collections.Users.JOINED)).toDate()));
		from.setText("Living in " + HelperClass.User.userInfo.get(HelperClass.Collections.Users.LOCATION));
		
		Button editdetails = (Button)findViewById(R.id.editdetails);
		
		if (editdetails.hasOnClickListeners() == true)
			return;
		
		editdetails.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				DocumentReference userReference = HelperClass.db.collection(HelperClass.Collections.USERS).document(HelperClass.auth.getCurrentUser().getUid());
				
				Map<String, String> inputUserInfo = new HashMap<>();
				inputUserInfo.put(HelperClass.Collections.Users.FIRSTNAME, firstname.getText().toString());
				inputUserInfo.put(HelperClass.Collections.Users.LASTNAME, lastname.getText().toString());
				inputUserInfo.put(HelperClass.Collections.Users.EMAIL, email.getText().toString());
				inputUserInfo.put(HelperClass.Collections.Users.LOCATION, location.getText().toString());
				inputUserInfo.put(HelperClass.Collections.Users.SECURITYQUESTION, securityquestion.getText().toString());
				inputUserInfo.put(HelperClass.Collections.Users.SECURITYANSWER, answer.getText().toString());
				
				Map<String, Object> updatedUserInfo = new HashMap<>();
				
				for (String key : inputUserInfo.keySet()){
					if (inputUserInfo.get(key).equals(HelperClass.User.userInfo.get(key)) == false){
						updatedUserInfo.put(key, inputUserInfo.get(key));
					}
				}
				
				if (updatedUserInfo.keySet().size() > 0){
					userReference.update(updatedUserInfo);
				}
			}
		});
	}
	
	
}
