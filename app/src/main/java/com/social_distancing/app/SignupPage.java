package com.social_distancing.app;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;


import androidx.annotation.NonNull;

import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class SignupPage extends AppCompatActivity {
	FirebaseAuth mAuth;
	FirebaseFirestore db = FirebaseFirestore.getInstance();
	final Context context = this;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_signup_page);
		
		final Button signupButton = (Button)findViewById(R.id.signupButton);
		final EditText firstnameEditText = (EditText)findViewById(R.id.firstnameEditText);
		final EditText lastnameEditText = (EditText)findViewById(R.id.lastnameEditText);
		final EditText dateOfBirthEditText = (EditText)findViewById(R.id.dateOfBirthEditText);
		final EditText emailEditText = (EditText)findViewById(R.id.emailEditText);
		final EditText passwordEditText = (EditText)findViewById(R.id.passwordEditText);
		//final EditText securityAnswerEditText = (EditText)findViewById(R.id.securityAnswerEditText);
		final EditText securityAnswerEditText = (EditText)findViewById(R.id.securityAnswerEditText);
		
		/*
		 
		 */
		mAuth = FirebaseAuth.getInstance();
		//FirebaseUser currentUser = mAuth.getCurrentUser();
		
		signupButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				final String firstname = firstnameEditText.getText().toString();
				final String lastname = lastnameEditText.getText().toString();
				final String dob = dateOfBirthEditText.getText().toString();
				final String email = emailEditText.getText().toString();
				final String password = passwordEditText.getText().toString();
				final String securityQuestion = firstnameEditText.getText().toString();
				final String securityAnswer = securityAnswerEditText.getText().toString();

				//Create a user with the email and password
				//Then get the ID of the created user
				//Then add the other details to the User collection with the ID
				mAuth.createUserWithEmailAndPassword(email, password)
						.addOnCompleteListener(new OnCompleteListener<AuthResult>() {
							@Override
							public void onComplete(@NonNull Task<AuthResult> task) {
								if (task.isSuccessful()) {
									// Sign in success, update UI with the signed-in user's information
									
									FirebaseUser user = mAuth.getCurrentUser();

									String userID = user.getUid().toString();
									
									Map<String, Object> data = new HashMap<>();
									data.put("FirstName", firstname);
									data.put("LastName", lastname);
									data.put("Email", email);
									
									ArrayList<String> strings = new ArrayList<>();
									strings.add("418LwC5F1ZMUIPtCc7wI3aPDsHP2");
									strings.add("Bmb5qyDnqZXc3zekZdrShin4eJy1");
									strings.add("UA684JbLmpMOlnFGcoYgNlrqo0N2");
									strings.add("zgPjvqGWQ4XSzG9wyrJqWsCeW5c2");
									strings.add("vvDrUa2xzMRuc9l93Nv9sw0svQ82");
									
									data.put("Friends", strings);
									
									data.put("Chats", null);
							
									db.collection("User").document(userID).set(data);
									
									Toast.makeText(context, "Account created.",
											Toast.LENGTH_SHORT).show();
									
									final Intent k = new Intent(context, UserPage.class);
									startActivity(k);
								} else {
									// If sign in fails, display a message to the user.
									
									Toast.makeText(context, "Authentication failed." + task.getException(),
											Toast.LENGTH_SHORT).show();
									
								}
							}
						});
				
			}
		});
	}
}
