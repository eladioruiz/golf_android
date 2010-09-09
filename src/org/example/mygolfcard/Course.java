package org.example.mygolfcard;

import org.example.mygolfcard.Matches.InitTask;
import org.example.mygolfcard.RestClient.RequestMethod;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class Course extends Activity {
	String auth_token;
	
	String course_id;
	String course_name;
	String course_address;
	String course_description;
	String course_config;
	String course_details;
	
	static String LOGIN_URL = "http://dev.mygolfcard.es/api/getcourse";
	
	private TextView txtCourseName;
	private TextView txtCourseAddress;
	private TextView txtCourseDescription;
	private TextView txtCourseConfig;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.course);
		
		course_id = getIntent().getCharSequenceExtra("course_id").toString();

		Authentication.readDataUser(Course.this);
		auth_token = Authentication.getToken();
		
		findViews();
		//initViews();
		//setListeners();
		
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
				//startActivity(new Intent(this, Resume.class));
				new AlertDialog.Builder(this)
					.setIcon(R.drawable.info_dialog_icon_tra)
					.setTitle(R.string.course_details_label)
					.setMessage(course_details)
					.setPositiveButton("Aceptar", null)
					.show();
				return true;
				
			case R.id.synchro:
				startActivity(new Intent(this, Synchro.class));
				return true;
				
			case R.id.menuapp:
				startActivity(new Intent(this, MenuApp.class));
				finish();
				return true;
		}
		return false;
	}
	
	private void findViews() {
		txtCourseName 			= (TextView) findViewById(R.id.course_name);
		txtCourseAddress		= (TextView) findViewById(R.id.course_address);
		txtCourseDescription	= (TextView) findViewById(R.id.course_description);
		txtCourseConfig			= (TextView) findViewById(R.id.course_config);
	}

	private void initViews() {
		txtCourseName.setText(course_name);
		txtCourseAddress.setText(course_address);
		txtCourseDescription.setText(course_description);
		txtCourseConfig.setText(course_config);
	}
	
	public String getCourse() {
		String response;
    	
	    RestClient client = new RestClient(LOGIN_URL);
	    client.AddParam("token", auth_token);
	    client.AddParam("course_id", course_id);
	    
	    Log.i("RESPONSE", "" + course_id);
	        
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
		JSONObject json;
		String aux;
		
		course_name 		= "";
		course_address 		= "";
		course_description 	= "";
		course_config		= "";
		try {
			json = new JSONObject(result);
			course_name			= json.get("name").toString();
			course_address		= json.get("address").toString();
			course_description	= json.get("long_description").toString();
					
			aux					=	"Par : " + json.get("handicap").toString() + "\n" +
									"Nº Hoyos : " + json.get("n_holes").toString() + "\n" +
									"Long Amarillas : " + json.get("length_yellow").toString() + "\n" +
									"Long Rojas : " + json.get("length_red").toString() + "\n" +
									"Long Blancas : " + json.get("length_white").toString();
			
			course_config		= aux;
			
			course_details 		= "";
			
			aux					=	"Año Fundación : " + json.get("founded").toString() + "\n" +
									"Diseñador : " + json.get("designer").toString() + "\n" +
									"Año Fundación : " + json.get("founded").toString() + "\n" +
									"Habilidad : " + json.get("ability_recommended").toString() + "/10\n" +
									"Mantenimiento : " + json.get("maintance").toString() + "/10\n" +
									"Relieve : " + json.get("relief").toString() + "/10\n" +
									"Viento : " + json.get("wind").toString() + "/10\n" +
									"Agua : " + json.get("water_in_play").toString() + "/10\n" +
									"Árboles : " + json.get("trees_in_play").toString() + "/10\n";
			
			course_details 		= aux;
			
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		initViews();
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
			return getCourse();
		}

		// -- gets called just before thread begins
		@Override
		protected void onPreExecute() 
		{
			Log.i( "makemachine", "onPreExecute()" );
			super.onPreExecute();
			this.dialog = ProgressDialog.show(Course.this, "Conexión Remota", "Recuperando datos de servidor remoto de My Golf Card.", true);
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
		}
	}   	
}
