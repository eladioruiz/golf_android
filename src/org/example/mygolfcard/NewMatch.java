package org.example.mygolfcard;

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
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class NewMatch extends Activity implements TextWatcher, AdapterView.OnItemSelectedListener, OnClickListener {
	private AutoCompleteTextView newmatch_course;
	private AutoCompleteTextView newmatch_player[] = new AutoCompleteTextView[4];
/*	private AutoCompleteTextView newmatch_player_1;
	private AutoCompleteTextView newmatch_player_2;
	private AutoCompleteTextView newmatch_player_3;
	private AutoCompleteTextView newmatch_player_4;
*/	private Spinner newmatch_tee[] = new Spinner[4];
/*	private Spinner newmatch_tee_1;
	private Spinner newmatch_tee_2;
	private Spinner newmatch_tee_3;
	private Spinner newmatch_tee_4;
*/	private String[] courses; //={"RACE", "Oliva Nova", "La Duquesa"};
	private String[] players={"Eladio", "Bea", "Juan", "Merche"};
	private String[] tees={"Amarillas", "Rojas", "Blancas"};
	
	private boolean connectionOK;
	private String auth_token;
	
	private String aux_courses;
	private String URL_COURSES;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.newmatch);
		
/*		ArrayAdapter<CharSequence> aa = new ArrayAdapter<CharSequence>(this, R.layout.spinnerview, tees);
		aa.setDropDownViewResource(R.layout.spinnerviewdropdown);
*/
		findViews();
		setListeners();
		
		URL_COURSES = getString(R.string.URL_APIS) + getString(R.string.ACTION_COURSES);
		
		connectionOK = Authentication.checkConnection(NewMatch.this);
		if (connectionOK) {
			Authentication.readDataUser(NewMatch.this);
			auth_token = Authentication.getToken();
			InitTask task = new InitTask();
			task.execute();
		}
		else {
			Toast.makeText(NewMatch.this, R.string.no_internet,
                    Toast.LENGTH_SHORT).show();
			
			String result = Authentication.readCourses(NewMatch.this);
			setInfoCourses(result);
		}
		
/*		newmatch_tee_1 = (Spinner)findViewById(R.id.newmatch_tee_1);
		newmatch_tee_1.setOnItemSelectedListener(this);
		newmatch_tee_1.setAdapter(aa);
		
		newmatch_tee_2 = (Spinner)findViewById(R.id.newmatch_tee_2);
		newmatch_tee_2.setOnItemSelectedListener(this);
		newmatch_tee_2.setAdapter(aa);
		
		newmatch_tee_3 = (Spinner)findViewById(R.id.newmatch_tee_3);
		newmatch_tee_3.setOnItemSelectedListener(this);
		newmatch_tee_3.setAdapter(aa);
		
		newmatch_tee_4 = (Spinner)findViewById(R.id.newmatch_tee_4);
		newmatch_tee_4.setOnItemSelectedListener(this);
		newmatch_tee_4.setAdapter(aa);
*/		
/*        newmatch_course=(AutoCompleteTextView)findViewById(R.id.newmatch_course);
        newmatch_course.addTextChangedListener(this);
        newmatch_course.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, courses));
        
        newmatch_player_1=(AutoCompleteTextView)findViewById(R.id.newmatch_player_1);
        newmatch_player_1.addTextChangedListener(this);
        newmatch_player_1.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, players));
        
        newmatch_player_2=(AutoCompleteTextView)findViewById(R.id.newmatch_player_2);
        newmatch_player_2.addTextChangedListener(this);
        newmatch_player_2.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, players));
        
        newmatch_player_3=(AutoCompleteTextView)findViewById(R.id.newmatch_player_3);
        newmatch_player_3.addTextChangedListener(this);
        newmatch_player_3.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, players));
        
        newmatch_player_4=(AutoCompleteTextView)findViewById(R.id.newmatch_player_4);
        newmatch_player_4.addTextChangedListener(this);
        newmatch_player_4.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, players));
*/        
/*		// Set up click listeners for all the buttons
		View okButton = findViewById(R.id.newmatch_ok);
		okButton.setOnClickListener(this);
		
		// Set up click listeners for all the buttons
		View cancelButton = findViewById(R.id.newmatch_cancel);
		cancelButton.setOnClickListener(this);
*/
	}

	private void findViews() {
		newmatch_player[0] 	= (AutoCompleteTextView)findViewById(R.id.newmatch_player_1);
		newmatch_player[1] 	= (AutoCompleteTextView)findViewById(R.id.newmatch_player_2);
		newmatch_player[2] 	= (AutoCompleteTextView)findViewById(R.id.newmatch_player_3);
		newmatch_player[3] 	= (AutoCompleteTextView)findViewById(R.id.newmatch_player_4);
		
		newmatch_tee[0]		= (Spinner)findViewById(R.id.newmatch_tee_1);
		newmatch_tee[1]		= (Spinner)findViewById(R.id.newmatch_tee_2);
		newmatch_tee[2] 	= (Spinner)findViewById(R.id.newmatch_tee_3);
		newmatch_tee[3] 	= (Spinner)findViewById(R.id.newmatch_tee_4);
		
		newmatch_course		= (AutoCompleteTextView)findViewById(R.id.newmatch_course);
	}
	
	private void setListeners() {

		newmatch_course.addTextChangedListener(this);        

		ArrayAdapter<CharSequence> aa = new ArrayAdapter<CharSequence>(this, R.layout.spinnerview, tees);
		aa.setDropDownViewResource(R.layout.spinnerviewdropdown);
		
		for (int i=0; i < newmatch_tee.length; i++) {
			newmatch_tee[i].setOnItemSelectedListener(this);
			newmatch_tee[i].setAdapter(aa);
		}
		
		for (int i=0; i < newmatch_player.length; i++) {
			newmatch_player[i].addTextChangedListener(this);
	        //newmatch_player[i].setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, players));
		}

		// Set up click listeners for all the buttons
		View okButton = findViewById(R.id.newmatch_ok);
		okButton.setOnClickListener(this);
		
		// Set up click listeners for all the buttons
		View cancelButton = findViewById(R.id.newmatch_cancel);
		cancelButton.setOnClickListener(this);

	}
	
    public void onTextChanged(CharSequence s, int start, int before, int count) {
    	// needed for interface, but not used
    }
    
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
    	// needed for interface, but not used
    }
    
    public void afterTextChanged(Editable e) {
    	// needed for interface, but not used
    }

    
    public void onItemSelected(AdapterView parent, View v, int position, long id) {
    
    }
    
    public void onNothingSelected(AdapterView parent) {
    		
    }
    
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.newmatch_cancel:
				finish();
				break;
			
			case R.id.newmatch_ok:
				if (validateForm()) {
					Log.d("My Golf Card", "NewMatch");
					Intent i = new Intent(this, Card.class);
					startActivity(i);
				}
				break;
		}
	}
	
	private boolean validateForm() {
		// Recupera campos introducidos en formulario
		String course 		= newmatch_course.getText().toString();
		String date_hour 	= ((TextView)findViewById(R.id.newmatch_datehour)).getText().toString();
		String n_holes 		= ((TextView)findViewById(R.id.newmatch_holes)).getText().toString();
		String res;
		boolean bOK = true;
		
		// Validación de campos obligatorios
		res = "";
		if (course.length()<=0)
		{
			// Campos vacíos
			res += "- Es obligatorio rellenar 'CAMPO'\n";
			bOK = false;
		}
		
		if (date_hour.length()<=0) {
			res += "- Es obligatorio rellenar 'FECHA/HORA'\n";
			bOK = false;
		}
		
		if (n_holes.length()<=0) {
			res += "- Es obligatorio rellenar 'Nº HOYOS'\n";
			bOK = false;
		}
		
		
		if (!bOK) {
			new AlertDialog.Builder(NewMatch.this)
				.setIcon(R.drawable.alert_dialog_icon)
				.setTitle(R.string.title_form_validation)
				.setMessage(getString(R.string.form_validation) + res)
				.setPositiveButton(R.string.alert_button_default, null)
				.show();
		}
		
		return bOK;
	}

	private void getData() {
		aux_courses = getCourses();
	}
	
	private void setInfoCourses(String result) {
		JSONObject jsonObj;
		JSONArray  jsonArr;

		try {
			jsonArr = new JSONArray(result);
			
/*			courses_field1 = new String[jsonArr.length()];
			courses_field2 = new String[jsonArr.length()];
			courses_field3 = new String[jsonArr.length()];
*/			
			courses = new String[jsonArr.length()];
			
			for (int i=0; i<jsonArr.length(); i++) {
				jsonObj = new JSONObject(jsonArr.get(i).toString());
				
/*				courses_field1[i] = jsonObj.getString("name");
				courses_field2[i] = jsonObj.getString("address");
				courses_field3[i] = jsonObj.getString("id");
*/				Log.i("JSON", "" + i);
				
				courses[i] = jsonObj.getString("name");
				
			}			
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		// fill in the grid_item layout
		newmatch_course.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, courses));
	}

	private void setInfoPlayers(String result) {
		for (int i=0; i<newmatch_player.length; i++) {
			newmatch_player[i].setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, players));
		}
	}

	private String getCourses() {
		String response;
    	
	    RestClient client = new RestClient(URL_COURSES);
	    client.AddParam("token", auth_token);
	    
	    response = "";
	    try {
	        client.Execute(RequestMethod.POST);
	        response = client.getResponse();
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	    
	    Authentication.saveCourses(NewMatch.this, response);
	    aux_courses = response;
	    return response;
	}

	/**
	 * sub-class of AsyncTask
	 */
	protected class InitTask extends AsyncTask<Context, Integer, String>
	{
		private ProgressDialog dialog;		
		private String aux_players;
		
		public InitTask () {
			
		}
		
		// -- run intensive processes here
		// -- notice that the datatype of the first param in the class definition matches the param passed to this method 
		// -- and that the datatype of the last param in the class definition matches the return type of this mehtod
		@Override
		protected String doInBackground( Context... params ) 
		{
			getData();
			return "";
		}

		// -- gets called just before thread begins
		@Override
		protected void onPreExecute() 
		{
			Log.i( "makemachine", "onPreExecute()" );
			super.onPreExecute();
			this.dialog = ProgressDialog.show(NewMatch.this, "Conexión Remota", "Recuperando datos de servidor remoto de My Golf Card.", true);
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
			setInfoCourses(aux_courses);
			setInfoPlayers(aux_players);
			//setListAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, courses));
		}
	}   

    
}
