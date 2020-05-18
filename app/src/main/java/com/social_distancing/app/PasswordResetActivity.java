package com.social_distancing.app;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import com.social_distancing.app.HelperClass.LOG;

import java.util.Map;

public class PasswordResetActivity extends AppCompatActivity {
	
	//Get the Firebase authorisation object, this is for signing in and other user related stuff
	public static final FirebaseAuth auth = FirebaseAuth.getInstance();
	//Get the Firebase database object, this is what we use to access collections and documents
	public static final FirebaseFirestore db = FirebaseFirestore.getInstance();
	
	//Login details are below
	public static final String email = "socialdistancing@socialdistancing.com";
	public static final String password = "socialdistancing";
	//The new password will be same as current password, so we can login again with the same details
	public static final String newPassword = "socialdistancing";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_password_reset);
		
		// Get the UI elements (buttons and edittexts) so we can work with them
		final Button sendPasswordResetEmailButton = (Button)findViewById(R.id.Button_sendPasswordResetEmail);
		final Button resetUserPasswordButton = (Button)findViewById(R.id.Button_resetUserPassword);
		final EditText securityQuestionAnswerEditText = (EditText)findViewById(R.id.EditText_securityQuestionAnswer);
		final EditText newUserPasswordEditText = (EditText)findViewById(R.id.EditText_newUserPassword);
		
		// Disable this button initially, only enable once we have signed in
		resetUserPasswordButton.setEnabled(false);
		
		auth.signOut();
		auth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
			@Override
			public void onComplete(@NonNull Task<AuthResult> task) {
				if (task.isSuccessful()){
					Log.d(LOG.SUCCESS, "Successfully logged in.");
					AuthResult authResult = (AuthResult)task.getResult();
					//We have signed in, so now we can enable this button
					resetUserPasswordButton.setEnabled(true);
				} else {
					Log.d(LOG.ERROR, "Error signing in. e: " + task.getException().getMessage());
				}
			}
		});
		
		//Add a listener for when the button is clicked. Similar to a "callback"
		sendPasswordResetEmailButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				//Call this function
				sendResetPasswordEmail(email);
			}
		});
		
		//Add a listener for when the button is clicked. Similar to a "callback"
		resetUserPasswordButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				String securityQuestionAnswer = securityQuestionAnswerEditText.getText().toString();
				newUserPasswordEditText.setText(newPassword);
				String newPassword = newUserPasswordEditText.getText().toString();
				//Call this function
				resetUserPassword(email, newPassword, securityQuestionAnswer);
			}
		});
	}
	
	//This function will send a reset password link to the email given.
	//It does not require a user to be signed into Firebase.
	void sendResetPasswordEmail(String email){
		// Enter your code here
	}
	
	//This function will directly change the password of a Firebase user.
	//This will only work when a user has signed into Firebase.
	//It will use check that the security question answer is correct then
	//change the password as required.
	void resetUserPassword(String email, final String newPassword, final String securityQuestionAnswer){
		// Enter your code here
	}
}
