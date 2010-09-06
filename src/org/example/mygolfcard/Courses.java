package org.example.mygolfcard;

import org.example.mygolfcard.MyGolfCard.InitTask;
import org.example.mygolfcard.RestClient.RequestMethod;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONArray;

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
import android.widget.TextView;

public class Courses extends ListActivity {
	String[] courses;
	private String auth_token;
	static String LOGIN_URL = "http://dev.mygolfcard.es/api/getcourses";
	
	/** Called with the activity is first created. */
	@Override
	public void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		setContentView(R.layout.courses);
		
		Authentication.readToken(Courses.this);
		auth_token = Authentication.getToken();
		
		InitTask task = new InitTask();
		task.execute();
				
		//setListAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, courses));
	}

	public void onListItemClick(ListView parent, View v, int position,long id) {
		//
	}
	
	public String callCourses() {
		String response;
    	
	    RestClient client = new RestClient(LOGIN_URL);
	    client.AddParam("token", auth_token);
	    
	    response = "";
	    try {
	        client.Execute(RequestMethod.POST);
	        response = client.getResponse();
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	    	 
	    return response;
	}
	
	public void setInfo(String result) {
		JSONObject jsonObj;
		JSONArray  jsonArr;
		
		try {
			jsonArr = new JSONArray(result);
			
			courses = new String[jsonArr.length()];
			
			for (int i=0; i<jsonArr.length(); i++) {
				jsonObj = new JSONObject(jsonArr.get(i).toString());
				
				courses[i] = jsonObj.getString("name");
				Log.i("JSON", "" + i);
			}
			
			
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		//TextView tx = (TextView) findViewById(R.id.selection);
		//tx.setText(result);
		setListAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, courses));
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
			return callCourses();
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
