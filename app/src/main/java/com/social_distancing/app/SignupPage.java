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

	Button signupButton;
	EditText firstnameEditText;
	EditText lastnameEditText;
	EditText dateOfBirthEditText;
	EditText emailEditText;
	EditText passwordEditText;
	EditText securityAnswerEditText;

	private void initViews() {
		signupButton = (Button)findViewById(R.id.signupButton);
		firstnameEditText = (EditText)findViewById(R.id.firstnameEditText);
		lastnameEditText = (EditText)findViewById(R.id.lastnameEditText);
		dateOfBirthEditText = (EditText)findViewById(R.id.dateOfBirthEditText);
		emailEditText = (EditText)findViewById(R.id.emailEditText);
		passwordEditText = (EditText)findViewById(R.id.passwordEditText);
		securityAnswerEditText = (EditText)findViewById(R.id.securityAnswerEditText);
	}

	private void initSignupListener() {
		signupButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
			final String email = emailEditText.getText().toString();
			final String password = passwordEditText.getText().toString();

			//Create a user with the email and password
			//Then get the ID of the created user
			//Then add the other details to the User collection with the ID
			mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
				@Override
				public void onComplete(@NonNull Task<AuthResult> task) {
					if (task.isSuccessful()) {
						// Sign in success, update UI with the signed-in user's information
						FirebaseUser mUser = mAuth.getCurrentUser();
						UserSingleton.getInstance();

						Map<String, Object> data = new HashMap<>();
						data.put("FirstName", firstnameEditText.getText().toString());
						data.put("LastName", lastnameEditText.getText().toString());
						data.put("Email", email);
						data.put("Friends", (ArrayList<String>)null);
						data.put("Chats", (ArrayList<String>)null);

						db.collection("Users").document(mUser.getUid()).set(data);

						Toast.makeText(context, "Account created.", Toast.LENGTH_SHORT).show();

						final Intent k = new Intent(context, UserPage.class);
						startActivity(k);
					} else {
						// If sign in fails, display a message to the user.

						Toast.makeText(context, "Authentication failed." + task.getException(), Toast.LENGTH_SHORT).show();
					}
				}
			});

			}
		});
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_signup_page);
		mAuth = FirebaseAuth.getInstance();
		initSignupListener();
	}
}
