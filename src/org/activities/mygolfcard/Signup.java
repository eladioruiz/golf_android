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

import org.classes.mygolfcard.User;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class Signup extends Activity  implements OnClickListener {
	
	//private String auth_token;
	//private CurrentUser cUser = new CurrentUser();
	
	private TextView user_name;
	private TextView user_email;
	private TextView user_login;
	private TextView user_password;
	private TextView user_confirm;
	private TextView user_handicap;
	private Button continueButton;
	private Button exitButton;
	
	String sName		= "";
	String sEMail		= "";
	String sLogin		= "";
	String sPassword	= "";
	String sConfirm		= "";
	String sHandicap	= "";
	
	String response;
	String auth_token;
	User cUser;
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.signup);

/*		// Set up click listeners for all the buttons
		View exitButton = findViewById(R.id.exit_button);
		exitButton.setOnClickListener(this);
*/		
		findViews();
		
	} 
	
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.exit_button:
				finish();
				break;
			
			case R.id.continue_button:
				if (validateForm()) {
					InitTask task = new InitTask();
					task.execute();
				}
				break;
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);

		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		return false;
	}    

	private void findViews() {
		user_name 		= (TextView)findViewById(R.id.user_name);
		user_email		= (TextView)findViewById(R.id.user_email);
		user_login		= (TextView)findViewById(R.id.user_login);
		user_password	= (TextView)findViewById(R.id.user_password);
		user_confirm	= (TextView)findViewById(R.id.user_password_confirm);
		user_handicap	= (TextView)findViewById(R.id.user_handicap);
		
		// Set up click listeners for all the buttons
		continueButton = (Button) findViewById(R.id.continue_button);
		continueButton.setOnClickListener(this);
		
		// Set up click listeners for all the buttons
		exitButton = (Button) findViewById(R.id.exit_button);
		exitButton.setOnClickListener(this);
	}
	

	private boolean validateForm() {
		boolean bOK			= true;
		String sRes 		= "";
		
		sName 		= user_name.getText().toString();
		sEMail		= user_email.getText().toString();
		sLogin		= user_login.getText().toString();
		sPassword	= user_password.getText().toString();
		sConfirm		= user_confirm.getText().toString();
		sHandicap	= user_handicap.getText().toString();
		
		if (sName.equals("")) {
			sRes = "El nombre no puede estar vacío.\n";
		}
		
		if (sEMail.equals("")) {
			sRes = "La dirección de email no puede estar vacía.\n";
		}
		
		if (sLogin.equals("")) {
			sRes = "El login no puede estar vacío.\n";
		}
		
		if (sPassword.equals("")) {
			sRes = "La contraseña no puede estar vacía.\n";
		}
		
		if (!sConfirm.equals(sPassword)) {
			sRes = "La contraseña y su confirmación no coinciden.\n";
		}
		
		if (sHandicap.equals("")) {
			sRes = "El handicap no puede estar vacío.\n";
		}
		
		bOK = sRes.equals("");
		
		if (!bOK) {
			new AlertDialog.Builder(Signup.this)
				.setIcon(R.drawable.alert_dialog_icon)
				.setTitle(R.string.title_form_validation)
				.setMessage(getString(R.string.form_validation) + sRes)
				.setPositiveButton(R.string.alert_button_default, null)
				.show();
		}
		
		return bOK;
	}
	
	private void autoLogin() {
		
		if (Authentication.saveDataUser(Signup.this,auth_token,cUser.getUser_id())) {
			Intent i = new Intent(this, MenuApp.class);
    		startActivity(i);
    		finish();
		}
	}
	
	/**
	 * sub-class of AsyncTask
	 */
	protected class InitTask extends AsyncTask<Context, Integer, String>
	{
		private ProgressDialog dialog;		
		
		public InitTask () {
			
		};
		
		// -- run intensive processes here
		// -- notice that the datatype of the first param in the class definition matches the param passed to this method 
		// -- and that the datatype of the last param in the class definition matches the return type of this mehtod
		@Override
		protected String doInBackground( Context... params ) 
		{
			cUser = org.classes.mygolfcard.User.createNewUser(sName,sEMail,sLogin,sPassword,sHandicap,Signup.this);
			return "";
		}

		// -- gets called just before thread begins
		@Override
		protected void onPreExecute() 
		{
			Log.i( "makemachine", "onPreExecute()" );
			super.onPreExecute();
			this.dialog = ProgressDialog.show(Signup.this, "Conexión Remota", "Escribiendo datos en el servidor remoto de My Golf Card.", true);
		}

		// -- called from the publish progress 
		// -- notice that the datatype of the second param gets passed to this method
		@Override
		protected void onProgressUpdate(Integer... values) 
		{
			super.onProgressUpdate(values);
			Log.i( "makemachine", "onProgressUpdate(): " +  String.valueOf( values[0] ) );
			//_percentField.setText( ( values[0] * 2 ) + "%");
			//_percentField.setTextSize( values[0] );
		}

		// -- called as soon as doInBackground method completes
		// -- notice that the third param gets passed to this method
		@Override
		protected void onPostExecute( String result ) 
		{
			super.onPostExecute(result);
			Log.i( "makemachine", "onPostExecute(): " + result );
			this.dialog.cancel();
			
			autoLogin();
		}
	}   	
}
