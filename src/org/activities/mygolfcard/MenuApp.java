/**
 * Package: org.activities.mygolfcard
 * File: MenuApp.java
 * Description:
 * Create At: ---
 * Created By: ERL
 * Last Modifications:
 * 		20/10/2010 - ERL - POO
 */
package org.activities.mygolfcard;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;

public class MenuApp extends Activity  implements OnClickListener {
	
	//private String auth_token;
	//private CurrentUser cUser = new CurrentUser();
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//setContentView(R.layout.menu);
		
		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
        setContentView(R.layout.menu);
        getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.custom_title_1);

/*		// Set up click listeners for all the buttons
		View exitButton = findViewById(R.id.exit_button);
		exitButton.setOnClickListener(this);
*/		
		// Set up click listeners for all the buttons
		View newButton = findViewById(R.id.new_button);
		newButton.setOnClickListener(this);
		
		// Set up click listeners for all the buttons
		View matchesButton = findViewById(R.id.matches_button);
		matchesButton.setOnClickListener(this);
		
		// Set up click listeners for all the buttons
		View coursesButton = findViewById(R.id.courses_button);
		coursesButton.setOnClickListener(this);
		
		// Set up click listeners for all the buttons
		View synchroButton = findViewById(R.id.synchro_button);
		synchroButton.setOnClickListener(this);

		//ERL Authentication.readDataUser(MenuApp.this);
		
		//ERL auth_token = Authentication.getToken();
		
		//ERL cUser.setAuthToken(Authentication.getToken());
		
	} 
	
	public void onClick(View v) {
		switch (v.getId()) {
/*			case R.id.exit_button:
				finish();
				break;
*/			
			case R.id.new_button:
				Intent i_new = new Intent(this, NewMatch.class);
				startActivity(i_new);
				break;
				
			case R.id.matches_button:
				Intent i_matches = new Intent(this, Matches.class);
				startActivity(i_matches);
				break;
				
			case R.id.courses_button:
				Intent i_courses = new Intent(this, Courses.class);
				startActivity(i_courses);
				break;
				
			case R.id.synchro_button:
				Intent i_synchro = new Intent(this, Synchro.class);
				startActivity(i_synchro);
				break;
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.menu_app, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.settings:
				startActivity(new Intent(this, Prefs.class));
				return true;
			case R.id.about:
				startActivity(new Intent(this, About.class));
				return true;
			case R.id.synchro:
				startActivity(new Intent(this, Synchro.class));
				return true;
		}
		return false;
	}    
}
