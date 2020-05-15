package com.social_distancing.app;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginPage extends AppCompatActivity {
	
	final Context context = this;
	RequestQueue queue;
	FirebaseAuth mAuth;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		/*
		Get the FirebaseAuth instance
		This is used for logging in, signing up or logging out
		*/
		mAuth = FirebaseAuth.getInstance();
		FirebaseUser currentUser = mAuth.getCurrentUser();
		
		/*
		Create the Buttons/UI stuff programmatically
		*/
		
		LinearLayout rootLayout =  new LinearLayout(this);
		rootLayout.setOrientation(LinearLayout.VERTICAL);
		rootLayout.setPadding(100,100,100,100);
		/*
		View divider = new View(this);
		divider.setLayoutParams(new LinearLayout.LayoutParams(0,2));
		divider.setPadding(0,5,0,5);
		//divider.setBackgroundResource(R.color.colorPrimaryDark);
		//divider.setAlpha(0);
		*/
		
		TextView titleTextView = new TextView(this);
		TextView subtitleTextView = new TextView(this);
		final EditText emailEditText = new EditText(this);
		final EditText passwordEditText = new EditText(this);
		final Button loginButton = new Button(this);
		final Button signupButton = new Button(this);
		
		LinearLayout titleLayout = new LinearLayout(this);
		titleLayout.setOrientation(LinearLayout.VERTICAL);
		titleTextView.setText("Social Distancing App");
		titleTextView.setTextSize(32);
		subtitleTextView.setText("Professional Studio A");
		subtitleTextView.setTextSize(18);
		titleLayout.addView(titleTextView);
		titleLayout.addView(subtitleTextView);
		titleLayout.setPadding(0,0,0, 50);
		
		emailEditText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
		emailEditText.setHint("user@address.com");
		emailEditText.setText("username@email.com");
		//emailEditText.setWidth(0);
		emailEditText.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
		
		passwordEditText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
		passwordEditText.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
		passwordEditText.setText("username");
		
		loginButton.setText("Login");
		loginButton.setPadding(0,0,0, 20);
		signupButton.setText("Sign up");
		
		LinearLayout emailLayout = new LinearLayout(this);
		TextView emailLabel = new TextView(this);
		emailLabel.setText("Email: ");
		emailLayout.addView(emailLabel);
		emailLayout.addView(emailEditText);
		
		LinearLayout passwordLayout = new LinearLayout(this);
		TextView paswordLabel = new TextView(this);
		paswordLabel.setText("Password: ");
		passwordLayout.addView(paswordLabel);
		passwordLayout.addView(passwordEditText);
		
		LinearLayout loginLayout = new LinearLayout(this);
		loginLayout.setOrientation(LinearLayout.VERTICAL);
		TextView enterDetailsTextView = new TextView(this);
		enterDetailsTextView.setText("Enter your details below to login.");
		loginLayout.addView(enterDetailsTextView);
		loginLayout.addView(emailLayout);
		loginLayout.addView(passwordLayout);
		CheckBox rememberMeCheckbox = new CheckBox(this);
		rememberMeCheckbox.setChecked(true);
		rememberMeCheckbox.setText("Remember me?");
		loginLayout.addView(rememberMeCheckbox);
		loginLayout.addView(loginButton);
		//loginLayout.setBackgroundColor(Color.YELLOW);
		loginLayout.setPadding(0,100,0,0);
		
		LinearLayout signupLayout = new LinearLayout(this);
		signupLayout.setOrientation(LinearLayout.VERTICAL);
		TextView signupAskTextView = new TextView(this);
		signupAskTextView.setText("Don't have an account? Click below to get started!");
		
		signupLayout.addView(signupAskTextView);
		signupLayout.addView(signupButton);
		signupLayout.setPadding(0,100,0,0);
		
		rootLayout.addView(titleLayout);
		rootLayout.addView(loginLayout);
		rootLayout.addView(signupLayout);
		
		/*
		LinearLayout.LayoutParams rootLayoutParams = new LinearLayout.LayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
		rootLayoutParams.gravity = Gravity.CENTER;
		rootLayoutParams.weight = 1.0f;
		rootLayout.setOrientation(LinearLayout.VERTICAL);
		*/
		setContentView(rootLayout);
		/*
		
		 */
		
		final LinearLayout signupScreenLayout = new LinearLayout(this);
		signupScreenLayout.setOrientation(LinearLayout.VERTICAL);
		
		loginButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				String email = emailEditText.getText().toString();// = ((TextView) findViewById(R.id.EditText_Email)).getText().toString();
				String password = passwordEditText.getText().toString();// ((TextView) findViewById(R.id.EditText_Password)).getText().toString();
				
				loginButton.setEnabled(false);
				
				mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
					@Override
					public void onComplete(@NonNull Task<AuthResult> task) {
						if (task.isSuccessful()) {
							FirebaseUser user = mAuth.getCurrentUser();
							Toast.makeText(context, "Successfully logged in.",
									Toast.LENGTH_SHORT).show();
							
							final Intent k = new Intent(context, UserPage.class);
							startActivity(k);
							
						} else {
							Toast.makeText(context, "Login failed." + task.getException().toString(),
									Toast.LENGTH_SHORT).show();
						}
						loginButton.setEnabled(true);
					}
				});
			}
		});
		
		signupButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				final Intent k = new Intent(context, SignupPage.class);
				startActivity(k);
			}
		});
	}
}
