/**
 * Package: org.example.mygolfcard
 * File: Matches.java
 * Description:
 * Create At: ---
 * Created By: ERL
 * Last Modifications:
 * 		20/10/2010 - ERL - POO
 */
package org.example.mygolfcard;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.classes.mygolfcard.CurrentUser;

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
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

public class Matches extends ListActivity {
	private org.classes.mygolfcard.Match matchesList[];
	private String auth_token;
	private CurrentUser cUser = new CurrentUser();
	private boolean connectionOK;
	
	/** Called with the activity is first created. */
	@Override
	public void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		setContentView(R.layout.matches);
		
		connectionOK = Authentication.checkConnection(Matches.this);
		if (connectionOK) {
			Authentication.readDataUser(Matches.this);
			auth_token = Authentication.getToken();
			cUser.setUser_id(Authentication.getUserId());
		
			InitTask task = new InitTask();
			task.execute();
		}
		else {
			Toast.makeText(Matches.this, R.string.no_internet,
                    Toast.LENGTH_SHORT).show();
			
			matchesList = org.classes.mygolfcard.Match.getMatchesFromLocal(Matches.this);
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
			Intent intent = new Intent(this, Match.class);
			intent.putExtra("match_id", matchesList[position].getMatch_id());
	        startActivity(intent);
		}
		else {
			new AlertDialog.Builder(Matches.this)
				.setIcon(R.drawable.alert_dialog_icon)
				.setTitle(R.string.remote_connection)
				.setMessage(R.string.no_internet_connect)
				.setPositiveButton(R.string.alert_button_default, null)
				.show();
		}
	}
	
	public void loadList() {
		List<HashMap<String, String>> fillMaps = new ArrayList<HashMap<String, String>>();
		try {
			for (int i=0; i<matchesList.length; i++) {
				HashMap<String, String> map = new HashMap<String, String>();
				map.put("course_name", matchesList[i].getCourseName());
				map.put("date_hour", matchesList[i].getDateHour());
				fillMaps.add(map);
			}			
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		// Muestra la lista
		SimpleAdapter adapter = new SimpleAdapter(this, fillMaps, R.layout.main_item_two_line_row, new String[] { "course_name", "date_hour" }, new int[] { R.id.field1,  R.id.field2});
		
		setListAdapter(adapter);
		getListView().setTextFilterEnabled(true);
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
			matchesList = org.classes.mygolfcard.Match.getMatchesFromRemote(auth_token,Integer.parseInt(cUser.getUser_id()),Matches.this);
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
			this.dialog = ProgressDialog.show(Matches.this, title_remote, remote, true);
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
