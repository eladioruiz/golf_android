/**
 * Package: org.activities.mygolfcard
 * File: Course.java
 * Description:
 * Create At: ---
 * Created By: ERL
 * Last Modifications:
 * 		20/10/2010 - ERL - POO
 */
package org.activities.mygolfcard;

import org.classes.mygolfcard.Authentication;
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
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.TextView;

public class Course extends Activity implements OnClickListener {
	private String auth_token;
	//private String auth_user_id;
	private User cUser = new User();
	
	private int localCourseId;
	private org.classes.mygolfcard.Course currentCourse;
	
	private TextView txtCourseName;
	private TextView txtCourseAddress;
	private TextView txtCourseDescription;
	private TextView txtCourseConfig;
	private View logoutButton;
	private TextView titleNameText;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
        setContentView(R.layout.course);
        getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.custom_title_2);
		
		currentCourse = new org.classes.mygolfcard.Course(this);
		
		localCourseId = getIntent().getIntExtra("course_id", 0);
		currentCourse.setCourse_id(localCourseId);

		findViews();
		initViews();
		setListeners();
		
		InitTask task = new InitTask();
		task.execute();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.menu_course, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.course_details:
				new AlertDialog.Builder(this)
					.setIcon(R.drawable.info_dialog_icon_tra)
					.setTitle(R.string.course_details_label)
					.setMessage(currentCourse.getCourseDetails())
					.setPositiveButton(R.string.alert_button_default, null)
					.show();
				return true;
				
			case R.id.synchro:
				startActivity(new Intent(this, Synchro.class));
				return true;
				
			case R.id.menuapp:
				finish();
				startActivity(new Intent(this, MenuApp.class));
				return true;
		}
		return false;
	}

	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.logout:
				finish();
				break;
		}
	}

	private void findViews() {
		txtCourseName 			= (TextView) findViewById(R.id.course_name);
		txtCourseAddress		= (TextView) findViewById(R.id.course_address);
		txtCourseDescription	= (TextView) findViewById(R.id.course_description);
		txtCourseConfig			= (TextView) findViewById(R.id.course_config);
		logoutButton 			= findViewById(R.id.logout);
		titleNameText	= (TextView) findViewById(R.id.titleName); 
	}

	private void setListeners() {
		logoutButton.setOnClickListener(this);	
	}
	
	private void initViews() {
		txtCourseName.setText(currentCourse.getCourseName());
		txtCourseAddress.setText(currentCourse.getCourseAddress());
		txtCourseDescription.setText(currentCourse.getCourseDescription());
		txtCourseConfig.setText(currentCourse.getCourseConfig());
		
		Authentication.readDataUser(Course.this);
		auth_token = Authentication.getToken();
		cUser.setUser_id(Authentication.getUserId());
		cUser.setUserName(Authentication.getUserName());
		
		titleNameText.setText(cUser.getUserName());
	}
	
	/**
	 * sub-class of AsyncTask
	 */
	protected class InitTask extends AsyncTask<Context, Integer, String>
	{
		private ProgressDialog dialog;
		
		public InitTask () {
			
		}
		
		// -- run intensive processes here
		// -- notice that the datatype of the first param in the class definition matches the param passed to this method 
		// -- and that the datatype of the last param in the class definition matches the return type of this mehtod
		@Override
		protected String doInBackground( Context... params ) 
		{
			currentCourse = currentCourse.setDataFromRemote(localCourseId, cUser.getUser_id(), auth_token);
			return "";
		}

		// -- gets called just before thread begins
		@Override
		protected void onPreExecute() 
		{
			Log.i( "makemachine", "onPreExecute()" );
			super.onPreExecute();
			CharSequence title_remote = getString(R.string.title_remote_connection);
			CharSequence remote = getString(R.string.remote_connection);
			this.dialog = ProgressDialog.show(Course.this, title_remote, remote, true);
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
			//setInfo(result);
			initViews();
		}
	}   	
}
