/**
 * Package: org.activities.mygolfcard
 * File: Courses.java
 * Description:
 * Create At: ---
 * Created By: ERL
 * Last Modifications:
 * 		20/10/2010 - ERL - POO
 */
package org.activities.mygolfcard;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.classes.mygolfcard.Authentication;
import org.classes.mygolfcard.User;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class Courses extends ListActivity implements OnClickListener  {
	private org.classes.mygolfcard.Course coursesList[];
	private String auth_token;
	private boolean connectionOK;
	private View logoutButton;
	private TextView titleNameText;
	private User cUser = new User();
	
	/** Called with the activity is first created. */
	@Override
	public void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
        setContentView(R.layout.courses);
        getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.custom_title_2);
		
        logoutButton = findViewById(R.id.logout);
        logoutButton.setOnClickListener(this);
		
		connectionOK = Authentication.checkConnection(Courses.this);
		findViews();
		initViews();
		if (connectionOK) {			
			InitTask task = new InitTask();
			task.execute();
		}
		else {
			Toast.makeText(Courses.this, R.string.no_internet,
                    Toast.LENGTH_SHORT).show();
			
			coursesList = org.classes.mygolfcard.Course.getCoursesFromLocal(Courses.this);
			loadList();
		}
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
	  // ignore orientation/keyboard change
	  super.onConfigurationChanged(newConfig);
	}

	public void onListItemClick(ListView parent, View v, int position,long id) {
		//
		if (connectionOK) {
			Intent intent = new Intent(this, Course.class);
	        intent.putExtra("course_id",coursesList[position].getCourse_id());
	        startActivity(intent);
		}
		else {
			new AlertDialog.Builder(Courses.this)
				.setIcon(R.drawable.alert_dialog_icon)
				.setTitle(R.string.title_remote_connection)
				.setMessage(R.string.no_internet_connect)
				.setPositiveButton(R.string.alert_button_default, null)
				.show();
		}
	}

	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.logout:
				finish();
				break;
		}
	}

	public void loadList() {
		List<HashMap<String, String>> fillMaps = new ArrayList<HashMap<String, String>>();
		try {
			for (int i=0; i<coursesList.length; i++) {
				HashMap<String, String> map = new HashMap<String, String>();
				map.put("name", coursesList[i].getCourseName());
				map.put("address", coursesList[i].getCourseAddress());
				fillMaps.add(map);
			}			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		// fill in the grid_item layout
		SimpleAdapter adapter = new SimpleAdapter(this, fillMaps, R.layout.main_item_two_line_row, new String[] { "name", "address" }, new int[] { R.id.field1,  R.id.field2});
		
		setListAdapter(adapter);
		getListView().setTextFilterEnabled(true);
	}

	private void findViews() {
		titleNameText	= (TextView) findViewById(R.id.titleName); 
	}

	private void initViews() {
		Authentication.readDataUser(Courses.this);
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
			coursesList = org.classes.mygolfcard.Course.getCoursesFromRemote(auth_token,Courses.this);
			return "";
		}

		// -- gets called just before thread begins
		@Override
		protected void onPreExecute() 
		{
			Log.i( "makemachine", "onPreExecute()" );
			super.onPreExecute();
			this.dialog = ProgressDialog.show(Courses.this, "Conexión Remota", "Recuperando datos de servidor remoto de My Golf Card.", true);
		}

		// -- called from the publish progress 
		// -- notice that the datatype of the second param gets passed to this method
		@Override
		protected void onProgressUpdate(Integer... values) 
		{
			super.onProgressUpdate(values);
			Log.i( "makemachine", "onProgressUpdate(): " +  String.valueOf( values[0] ) );
		}

		// -- called as soon as doInBackground method completes
		// -- notice that the third param gets passed to this method
		@Override
		protected void onPostExecute( String result ) 
		{
			super.onPostExecute(result);
			Log.i( "makemachine", "onPostExecute(): " + result );
			this.dialog.cancel();
			loadList();
		}
	}   

}
