package org.example.mygolfcard;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.example.mygolfcard.RestClient.RequestMethod;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

public class Matches extends ListActivity {
	private String[] matches;
	private String[] matches_field1;
	private String[] matches_field2;
	private String[] matches_field3;
	private String auth_token;
	private String auth_user_id;
	private boolean connectionOK;
	private String URL_MATCHES;
	
	/** Called with the activity is first created. */
	@Override
	public void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		setContentView(R.layout.matches);
		
		URL_MATCHES = getString(R.string.URL_APIS) + getString(R.string.ACTION_MATCHES);
		
		connectionOK = Authentication.checkConnection(Matches.this);
		if (connectionOK) {
			Authentication.readDataUser(Matches.this);
			auth_token = Authentication.getToken();
			auth_user_id = Authentication.getUserId();
		
			InitTask task = new InitTask();
			task.execute();
		}
		else {
			Toast.makeText(Matches.this, R.string.no_internet,
                    Toast.LENGTH_SHORT).show();
			
			String result = Authentication.readMatches(Matches.this);
			setInfo(result);
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
			Toast.makeText(Matches.this, R.string.no_implemented,
					Toast.LENGTH_SHORT).show();
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

	public String getMatches() {
		String response;
    	
	    RestClient client = new RestClient(URL_MATCHES);
	    client.AddParam("token", auth_token);
	    client.AddParam("user_id", auth_user_id);
	    
	    Log.i("RESPONSE", "" + auth_user_id);
	        
	    response = "";
	    try {
	        client.Execute(RequestMethod.POST);
	        response = client.getResponse();
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	    
	    Log.i("RESPONSE", "" + response);
	    
	    Authentication.saveMatches(Matches.this, response);
	    return response;
	}
	
	public void setInfo(String result) {
		JSONObject jsonObj;
		JSONArray  jsonArr;

		List<HashMap<String, String>> fillMaps = new ArrayList<HashMap<String, String>>();
		try {
			jsonArr = new JSONArray(result);
			
			matches = new String[jsonArr.length()];
			
			matches_field1 = new String[jsonArr.length()];
			matches_field2 = new String[jsonArr.length()];
			matches_field3 = new String[jsonArr.length()];
			
			for (int i=0; i<jsonArr.length(); i++) {
				jsonObj = new JSONObject(jsonArr.get(i).toString());
				
				matches[i] = jsonObj.getString("players") ;
				
				matches_field1[i] = jsonObj.getString("course_name");
				matches_field2[i] = jsonObj.getString("date_hour");
				matches_field3[i] = jsonObj.getString("match_id");
				
				Log.i("JSON", "" + matches[i]);
				
				HashMap<String, String> map = new HashMap<String, String>();
				map.put("course_name", matches_field1[i]);
				map.put("date_hour", matches_field2[i]);
				fillMaps.add(map);
			}
			
			
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		//setListAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, matches));
		
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
			return getMatches();
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
			setInfo(result);
			//setListAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, courses));
		}
	}   
	
}
