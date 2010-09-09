package org.example.mygolfcard;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.example.mygolfcard.RestClient.RequestMethod;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.ArrayAdapter;
import android.widget.SimpleAdapter;
import android.widget.Toast;

public class Matches extends ListActivity {
	String[] matches;
	String[] matches_field1;
	String[] matches_field2;
	String[] matches_field3;
	private String auth_token;
	private String auth_user_id;
	static String LOGIN_URL = "http://dev.mygolfcard.es/api/getmatches";
	
	/** Called with the activity is first created. */
	@Override
	public void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		setContentView(R.layout.matches);
		
		Authentication.readDataUser(Matches.this);
		auth_token = Authentication.getToken();
		auth_user_id = Authentication.getUserId();
		
		InitTask task = new InitTask();
		task.execute();
		
	}

	public void onListItemClick(ListView parent, View v, int position,long id) {
		//
		Toast.makeText(Matches.this, "Opción no operativa en estos momentos. Disculpe las molestias.",
                Toast.LENGTH_SHORT).show();
	}

	public String getMatches() {
		String response;
    	
	    RestClient client = new RestClient(LOGIN_URL);
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
				
				matches[i] = jsonObj.getString("course_name") + "\n" + jsonObj.getString("date_hour");
				
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
		
		SimpleAdapter adapter = new SimpleAdapter(this, fillMaps, R.layout.main_item_two_line_row, new String[] { "course_name", "date_hour" }, new int[] { R.id.field1,  R.id.field2});
		
		setListAdapter(adapter);
		getListView().setTextFilterEnabled(true);
	}

	/**
	 * sub-class of AsyncTask
	 */
	protected class InitTask extends AsyncTask<Context, Integer, String>
	{
		private String token;
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
			this.dialog = ProgressDialog.show(Matches.this, "Conexión Remota", "Recuperando datos de servidor remoto de My Golf Card.", true);
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
