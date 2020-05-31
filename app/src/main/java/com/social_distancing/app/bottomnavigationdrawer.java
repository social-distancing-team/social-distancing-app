package com.social_distancing.app;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.material.badge.BadgeDrawable;
import com.google.android.material.bottomnavigation.BottomNavigationMenu;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class bottomnavigationdrawer extends AppCompatActivity {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_bottomnavigationdrawer);
		
		final BottomNavigationView bottomNavigationView = (BottomNavigationView)findViewById(R.id.navigator);
		
		
		bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
			@Override
			public boolean onNavigationItemSelected(@NonNull MenuItem item) {
				Fragment selectedFragment = new ListsFragment();
				getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, selectedFragment).commit();
				
				return true;
			}
		});
		
		BadgeDrawable badgeDrawable = bottomNavigationView.getOrCreateBadge(R.id.nav_friends);
		badgeDrawable.setVisible(true);
		badgeDrawable.setNumber(91);

	}
}
