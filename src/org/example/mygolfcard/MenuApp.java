package org.example.mygolfcard;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.view.View.OnClickListener;
import android.util.Log;
import java.io.*;

public class MenuApp extends Activity  implements OnClickListener {
	private String auth_token;
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.menu);

		// Set up click listeners for all the buttons
		View exitButton = findViewById(R.id.exit_button);
		exitButton.setOnClickListener(this);
		
		// Set up click listeners for all the buttons
		View newButton = findViewById(R.id.new_button);
		newButton.setOnClickListener(this);
		
		// Set up click listeners for all the buttons
		View matchesButton = findViewById(R.id.matches_button);
		matchesButton.setOnClickListener(this);
		
		// Set up click listeners for all the buttons
		View coursesButton = findViewById(R.id.courses_button);
		coursesButton.setOnClickListener(this);
		
		Authentication.readToken(MenuApp.this);
		auth_token = Authentication.getToken();
		
	}
	
	@Override
	public void onDestroy() {
		try
		{
			deleteFile("token.txt");
		}
		catch (Throwable t) {
			new AlertDialog.Builder(this)
				.setIcon(R.drawable.alert_dialog_icon)
				.setTitle("Error borrando sesión")
				.setMessage("La aplicación no ha podido borrar los datos correspondientes a su sesión. \n")
				.setPositiveButton("Aceptar", null)
				.show();
			finish();
		}
		super.onDestroy();		
	}

	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.exit_button:
				finish();
				break;
			
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
		}
		return false;
	}    
}
