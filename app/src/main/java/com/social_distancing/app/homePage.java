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
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

/*
Tasks, Async and Firebase
 */
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.firestore.DocumentSnapshot;

/*
Helper class and functions written by us
 */
import com.social_distancing.app.HelperClass.LOG;
import com.social_distancing.app.HelperClass.User;

public class homePage extends AppCompatActivity {
	
	Context context = this;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_home_page);
		getSupportActionBar().hide();
		
		final LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		
		final EditText editText_Email = (EditText) findViewById(R.id.editText_Email);
		final EditText editText_Password = (EditText) findViewById(R.id.editText_Password);
		
		final Button button_SignIn = (Button) findViewById(R.id.button_SignIn);
		final Button button_Register = (Button) findViewById(R.id.button_Register);
		final Button button_ResetPassword = (Button) findViewById(R.id.button_ResetPassword);
		
		final TextView textView_LoginStatus = (TextView) findViewById(R.id.textView_LoginStatus);
		
		User.logout();
		
		button_SignIn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(final View v) {
				textView_LoginStatus.setVisibility(View.INVISIBLE);
				v.setEnabled(false);
				
				button_SignIn.setEnabled(false);
				button_Register.setEnabled(false);
				
				User.login(editText_Email.getText().toString(), editText_Password.getText().toString()).addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
					@Override
					public void onComplete(@NonNull Task<DocumentSnapshot> task) {
						if (task.isSuccessful() && task.isSuccessful() && task.getResult() != null) {
							
							if ((boolean) User.userInfo.get("Deleted")) {
								textView_LoginStatus.setText("This user has been deleted.");
								textView_LoginStatus.setTextColor(Color.RED);
								textView_LoginStatus.setVisibility(View.VISIBLE);
								User.logout();
								v.setEnabled(true);
								return;
							}
							
							Toast.makeText(context, "Successfully logged in.", Toast.LENGTH_SHORT).show();
							
							final Intent intent = new Intent(context, mainpage.class);
							startActivity(intent);
							Log.d(LOG.WARNING, "Calling finish.");
							Log.d(LOG.WARNING, "Current ID: " + HelperClass.auth.getCurrentUser().getUid());
							finish();
						} else {
							Toast.makeText(context, "Incorrect Email and password combination.", Toast.LENGTH_LONG).show();
							textView_LoginStatus.setText("Incorrect Email and password combination.\nPlease try again.");
							textView_LoginStatus.setTextColor(Color.RED);
							textView_LoginStatus.setVisibility(View.VISIBLE);
							v.setEnabled(true);
							button_SignIn.setEnabled(true);
							button_Register.setEnabled(true);
						}
						
					}
				});
			}
		});
		
		button_Register.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				
				button_SignIn.setEnabled(false);
				button_Register.setEnabled(false);
				View registrationView = inflater.inflate(R.layout.registrationlayout, null);
				final AlertDialog.Builder registrationDialog = new AlertDialog.Builder(context);
				registrationDialog.setTitle("Register a new account");
				registrationDialog.setMessage("Please enter your details");
				registrationDialog.setView(registrationView);
				
				final EditText editText_FirstName = (EditText) registrationView.findViewById(R.id.editText_FirstName);
				final EditText editText_LastName = (EditText) registrationView.findViewById(R.id.editText_LastName);
				final EditText editText_Location = (EditText) registrationView.findViewById(R.id.editText_Location);
				final EditText editText_Email = (EditText) registrationView.findViewById(R.id.editText_Email);
				final EditText editText_Password = (EditText) registrationView.findViewById(R.id.editText_Password);
				final EditText editText_SecurityQuestion = (EditText) registrationView.findViewById(R.id.editText_SecurityQuestion);
				final EditText editText_SecurityAnswer = (EditText) registrationView.findViewById(R.id.editText_SecurityAnswer);
				
				
				registrationDialog.setPositiveButton("Done", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						final String firstName = editText_FirstName.getText().toString();
						final String lastName = editText_LastName.getText().toString();
						final String location = editText_Location.getText().toString();
						final String email = editText_Email.getText().toString();
						final String password = editText_Password.getText().toString();
						final String securityQuestion = editText_SecurityQuestion.getText().toString();
						final String securityAnswer = editText_SecurityAnswer.getText().toString();
						
						HelperClass.auth.createUserWithEmailAndPassword(email, password)
								.addOnCompleteListener(new OnCompleteListener<AuthResult>() {
									@Override
									public void onComplete(@NonNull Task<AuthResult> task) {
										if (task.isSuccessful()) {
											String userID = HelperClass.auth.getCurrentUser().getUid();
											HelperClass.createUser(userID, firstName, lastName, "", location, email, "", securityQuestion, securityAnswer).addOnCompleteListener(new OnCompleteListener<Void>() {
												@Override
												public void onComplete(@NonNull Task<Void> task) {
													Toast.makeText(context, "Successfully registered an account.", Toast.LENGTH_SHORT).show();
													User.logout();
													User.login(email, password).addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
														@Override
														public void onComplete(@NonNull Task<DocumentSnapshot> task) {
															if (task.isSuccessful() && task.isSuccessful() && task.getResult() != null) {
																final Intent k = new Intent(context, mainpage.class);
																startActivity(k);
																finish();
															}
															
														}
													});
												}
											});
											
										} else {
											button_SignIn.setEnabled(true);
											button_Register.setEnabled(true);
											Toast.makeText(context, "Failed to register.", Toast.LENGTH_SHORT).show();
										}
									}
								});
					}
				});
				registrationDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						button_SignIn.setEnabled(true);
						button_Register.setEnabled(true);
					}
				});
				registrationDialog.show();
			}
		});
		
		//Reset password
		button_ResetPassword.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
			
			}
		});
	}
}
