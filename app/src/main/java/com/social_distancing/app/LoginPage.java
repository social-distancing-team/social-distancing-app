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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginPage extends AppCompatActivity {
	
	final Context context = this;
	FirebaseAuth mAuth;

	// Interactive Element Declarations
	LinearLayout rootLayout;
	EditText emailEditText;
	EditText passwordEditText;
	CheckBox rememberMeCheckbox;
	Button loginButton;
	Button signupButton;

	/* Title Layout
	 *
	 * The Title Layout displays the App Name.
	 * */
	private LinearLayout makeTitleLayout() {
		LinearLayout titleLayout = new LinearLayout(this);
		titleLayout.setOrientation(LinearLayout.VERTICAL);
		
		// Title
		TextView titleTextView = new TextView(this);
		titleTextView.setText("Social Distancing App"); // TODO - Change from staticly assigning strings to using strings.xml 
		titleTextView.setTextSize(32);
		
		// Subtitle
		TextView subtitleTextView = new TextView(this);
		subtitleTextView.setText("Professional Studio A"); // TODO - Change from staticly assigning strings to using strings.xml
		subtitleTextView.setTextSize(18);

		// Add the constructed Title UI elements to the Title Layout
		titleLayout.addView(titleTextView);
		titleLayout.addView(subtitleTextView);
		titleLayout.setPadding(0,0,0, 50);

		return titleLayout;
	}

	private LinearLayout makeEmailLayout() {
		/* Email Layout
		 * --- */
		LinearLayout emailLayout = new LinearLayout(this);
		
		// Email field label
		TextView emailLabel = new TextView(this);
		emailLabel.setText("Email: ");
		
		//Email field
		emailEditText = new EditText(this);
		emailEditText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
		emailEditText.setHint("user@address.com");
		emailEditText.setText("johnsmith@example.com"); // TODO - Remove after development/testing
		emailEditText.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
		
		// Add the constructed Email UI elements to the Email Layout
		emailLayout.addView(emailLabel);
		emailLayout.addView(emailEditText);

		return emailLayout;
	}

	private LinearLayout makePasswordLayout() {
		/* Password Layout
		 * --- */
		LinearLayout passwordLayout = new LinearLayout(this);
		
		// Password field Label
		TextView paswordLabel = new TextView(this);
		paswordLabel.setText("Password: ");
		
		//Password field
		passwordEditText = new EditText(this);
		passwordEditText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
		passwordEditText.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
		passwordEditText.setText("johnsmith"); // TODO - Remove after development/testing
			
		// Add the constructed Password UI elements to the Password Layout
		passwordLayout.addView(paswordLabel);
		passwordLayout.addView(passwordEditText);
		
		return passwordLayout;
	}


	/* Login Layout
	 *
	 * The Login Layout is one of the 3 views that comprise the Root Layout.
	 * It is made up of sub views and UI elements in which the Users credentials are entered.
	 * This includes the:
	 * - Email text field
	 * - Password text field
	 * - Remember Me checkbox
	 * - Login button
	 * ----- */
	private LinearLayout makeLoginLayout() {
		LinearLayout loginLayout = new LinearLayout(this);
		loginLayout.setOrientation(LinearLayout.VERTICAL);

		// Login prompt
		TextView enterDetailsTextView = new TextView(this);
		enterDetailsTextView.setText("Enter your details below to login.");
		
		// RememberMe checkbox
		rememberMeCheckbox = new CheckBox(this);
		rememberMeCheckbox.setChecked(true);
		rememberMeCheckbox.setText("Remember me?");

		// Login button
		loginButton = new Button(this);
		loginButton.setText("Login");
		loginButton.setPadding(0,0,0, 20);

		// Add the constructed Login UI elements to the Login Layout
		loginLayout.addView(enterDetailsTextView);
		loginLayout.addView(makeEmailLayout());
		loginLayout.addView(makePasswordLayout());
		loginLayout.addView(rememberMeCheckbox);
		loginLayout.addView(loginButton);
		loginLayout.setPadding(0,100,0,0);
		
		return loginLayout;
	}

	private LinearLayout makeSignupLayout() {
		/* SignUp Layout
		 *
		 * The Login Layout is one of the 3 views that comprise the Root Layout.
		 * It is made up of sub views and UI elements in which the Users credentials are entered.
		 * This includes the:
		 * - Email text field
		 * - Password text field
		 * - Remember Me checkbox
		 * - Login button
		 * ----- */
		LinearLayout signupLayout = new LinearLayout(this);
		signupLayout.setOrientation(LinearLayout.VERTICAL);
		
		// SignUp prompt
		TextView signupAskTextView = new TextView(this);
		signupAskTextView.setText("Don't have an account? Click below to get started!");
		
		// SignUp button
		signupButton = new Button(this);
		signupButton.setText("Sign up");
		
		// Add the constructed SignUp UI elements to the SignUp Layout
		signupLayout.addView(signupAskTextView);
		signupLayout.addView(signupButton);
		signupLayout.setPadding(0,100,0,0);
		
		return signupLayout;
	}

	private void initViews() {
		/* Root Layout
		 *
		 * The Root Layout is the content that will be displayed to the App User.
		 * It is comprised of 3 sub views:
		 * - Title
		 * - Login
		 * - SignUp
		 * These 3 views are defined below and are then added to the Root Layout.
		 *
		 * Currently only a vertical screen orientation is supported.
		 * */
		rootLayout = new LinearLayout(this);
		rootLayout.setOrientation(LinearLayout.VERTICAL);
		rootLayout.setPadding(100,100,100,100);

		// Add constructed views to the Root Layout
		rootLayout.addView(makeTitleLayout());
		rootLayout.addView(makeLoginLayout());
		rootLayout.addView(makeSignupLayout());

		setContentView(rootLayout);
		
		return;
	}

	private void initLoginListener() {
		/* Login button
		 * 
		 * Action: When the loginButton UI element is clicked.
		 * Response: Attempt to authenticate the User based on the credentials entered in the Email and Password fields.
		 * ----- */
		loginButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// Retrieve the entered credentials from the text fields.
				String email = emailEditText.getText().toString();// = ((TextView) findViewById(R.id.EditText_Email)).getText().toString();
				String password = passwordEditText.getText().toString();// ((TextView) findViewById(R.id.EditText_Password)).getText().toString();
				
				// Provide visual response, to show button click has been registered.
				loginButton.setEnabled(false);
				
				// Authenticate against Firebase
				mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
					@Override
					public void onComplete(@NonNull Task<AuthResult> task) {
						if (task.isSuccessful()) {
							UserSingleton.getInstance();

							Toast.makeText(context, "Successfully logged in.", Toast.LENGTH_SHORT).show();

							// Take the authenticated user to the UserPage
							final Intent k = new Intent(context, UserPage.class);
							startActivity(k);
						} else {
							Toast.makeText(context, "Login failed." + task.getException().toString(), Toast.LENGTH_SHORT).show();
						}
						loginButton.setEnabled(true);
					}
				});
			}
		});
	}

	private void initSignupListener() {
		/* SignUp button
		 * 
		 * Action: When the signupButton UI element is clicked.
		 * Response: Redirect the App user to the SignupPage.
		 * ----- */
		signupButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				final Intent k = new Intent(context, SignupPage.class);
				startActivity(k);
			}
		});

		return;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		/* Create the UI programmatically
		 * */
		initViews();
		/* Instantiate the FirebaseAuth instance and fetch the current User.
		 *
		 * This is used for User authentication and session management.
		 * */
		mAuth = FirebaseAuth.getInstance();
		/* Action Listeners
		 *
		 * The following Listeners are used to provide specified responses/actions to User interactions with UI elements.
		 * ------- */
		initLoginListener();
		initSignupListener();
	}
}
