package com.social_distancing.app;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

public class Test_Intents extends AppCompatActivity {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_test__intents);
		
		LinearLayout layout = (LinearLayout)findViewById(R.id.rootLayout);
		
		Button button = new Button(this);
		button.setText("Click me to start new activity.");
		layout.addView(button);
		button.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(getBaseContext(), LoginPage.class);
				startActivity(intent);
				finish();
			}
		});
		
	}
}
