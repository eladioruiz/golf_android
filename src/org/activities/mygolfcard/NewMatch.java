/**
 * Package: org.activities.mygolfcard
 * File: NewMatch.java
 * Description:
 * Create At: ---
 * Created By: ERL
 * Last Modifications:
 * 		31/10/2010 - ERL - POO
 */
package org.activities.mygolfcard;

import java.util.Calendar;

import org.classes.mygolfcard.Authentication;
import org.classes.mygolfcard.User;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

public class NewMatch extends Activity implements TextWatcher, AdapterView.OnItemSelectedListener, OnClickListener {
	private AutoCompleteTextView newmatch_course;
	private AutoCompleteTextView newmatch_player[] = new AutoCompleteTextView[4];
	private TextView newmatch_date;
	private TextView newmatch_hour;
	private TextView newmatch_n_holes;
	private Spinner newmatch_tee[] = new Spinner[4];
	private TextView titleNameText;
	private User cUser = new User();
	
	private int newmatch_id;
	private String[] tees={"Amarillas", "Rojas", "Blancas"};
	
	private String[] courses; 
	private String[] players; 

	private org.classes.mygolfcard.Course coursesList[];
	private org.classes.mygolfcard.Player friendsList[];
	
	private Button pickDate;
	private Button pickTime;
	private View okButton;
	private View cancelButton;
	private View logoutButton;
	
	private boolean connectionOK;
	private String auth_token;
	private int auth_user_id;
	
    // date and time
    private int mYear;
    private int mMonth;
    private int mDay;
    private int mHour;
    private int mMinute;

    static final int TIME_DIALOG_ID = 0;
    static final int DATE_DIALOG_ID = 1;

	private SQLiteDatabase db = null;
	private String DATABASE_NAME = "mygolfcard.db";
	
	String course 	= "";
	String date		= "";
	String hour		= "";
	String n_holes	= "";
	int player_id[] = new int[4];
	int tee_id[] 	= new int[4];
	int course_id;
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
        setContentView(R.layout.newmatch);
        getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.custom_title_2);
		
		findViews();
		initViews();
		setListeners();
		
		connectionOK = Authentication.checkConnection(NewMatch.this);
		if (connectionOK) {
			InitTask task = new InitTask();
			task.execute();
		}
		else {
			Toast.makeText(NewMatch.this, R.string.no_internet,
                    Toast.LENGTH_SHORT).show();
			
			coursesList = org.classes.mygolfcard.Course.getCoursesFromLocal(NewMatch.this);
			friendsList = org.classes.mygolfcard.Player.getFriendsFromLocal(NewMatch.this);

		}
		
        final Calendar c = Calendar.getInstance();
        mYear	= c.get(Calendar.YEAR);
        mMonth 	= c.get(Calendar.MONTH);
        mDay 	= c.get(Calendar.DAY_OF_MONTH);
        mHour 	= c.get(Calendar.HOUR_OF_DAY);
        mMinute	= c.get(Calendar.MINUTE);

        updateDisplay();
        
	}

	@Override
    protected void onPause() {
        super.onPause();

        saveScreenData();
    }
	
	@Override
    protected void onResume() {
        super.onPause();

        getScreenData();
    }
	
	@Override
	protected Dialog onCreateDialog(int id) {
		switch (id) {
			case TIME_DIALOG_ID:
				return new TimePickerDialog(this, mTimeSetListener, mHour, mMinute, false);
			case DATE_DIALOG_ID:
				return new DatePickerDialog(this, mDateSetListener, mYear, mMonth, mDay);
		}
		return null;
	}

	@Override
	protected void onPrepareDialog(int id, Dialog dialog) {
		switch (id) {
			case TIME_DIALOG_ID:
				((TimePickerDialog) dialog).updateTime(mHour, mMinute);
				break;
			case DATE_DIALOG_ID:
				((DatePickerDialog) dialog).updateDate(mYear, mMonth, mDay);
				break;
		}
	}    

	private void updateDisplay() {
		newmatch_date.setText(
				new StringBuilder()
				// Month is 0 based so add 1
				.append(mDay).append("-")
				.append(mMonth + 1).append("-")
				.append(mYear).append(" "));
		
		newmatch_hour.setText(
				new StringBuilder()
				// Month is 0 based so add 1
				.append(pad(mHour)).append(":")
				.append(pad(mMinute)));
	}

	private DatePickerDialog.OnDateSetListener mDateSetListener =
		new DatePickerDialog.OnDateSetListener() {

		public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
			mYear 	= year;
			mMonth 	= monthOfYear;
			mDay 	= dayOfMonth;
			updateDisplay();
		}
	};

	private TimePickerDialog.OnTimeSetListener mTimeSetListener =
		new TimePickerDialog.OnTimeSetListener() {

		public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
			mHour	= hourOfDay;
			mMinute	= minute;
			updateDisplay();
		}
	};

	private static String pad(int c) {
		if (c >= 10)
			return String.valueOf(c);
		else
			return "0" + String.valueOf(c);
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
		
		newmatch_date		= (TextView)findViewById(R.id.newmatch_text_date);
		newmatch_date.setEnabled(false);
		
		newmatch_hour		= (TextView)findViewById(R.id.newmatch_text_hour);
		newmatch_hour.setEnabled(false);
		
		newmatch_n_holes 	= (TextView)findViewById(R.id.newmatch_holes);
		
		pickDate 			= (Button) findViewById(R.id.newmatch_button_date);
		pickTime 			= (Button) findViewById(R.id.newmatch_button_hour);
		
		okButton 			= findViewById(R.id.newmatch_ok);
		cancelButton 		= findViewById(R.id.newmatch_cancel);
		
		logoutButton = findViewById(R.id.logout);
		titleNameText	= (TextView) findViewById(R.id.titleName); 
	}
	
	private void initViews() {
		Authentication.readDataUser(NewMatch.this);
		auth_token    = Authentication.getToken();
		auth_user_id  = Authentication.getUserId();	
		
		cUser.setUser_id(Authentication.getUserId());
		cUser.setUserName(Authentication.getUserName());
		
		titleNameText.setText(cUser.getUserName());
	}
	
	private void setListeners() {

		newmatch_course.addTextChangedListener(this);        

		ArrayAdapter<CharSequence> aa = new ArrayAdapter<CharSequence>(this, R.layout.spinnerview, tees);
		//aa.setDropDownViewResource(R.layout.spinnerviewdropdown);
		aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		
		for (int i=0; i < newmatch_tee.length; i++) {
			newmatch_tee[i].setOnItemSelectedListener(this);
			newmatch_tee[i].setAdapter(aa);
		}
		
		for (int i=0; i < newmatch_player.length; i++) {
			newmatch_player[i].addTextChangedListener(this);
		}

		// Set up click listeners for all the buttons
		okButton.setOnClickListener(this);
		cancelButton.setOnClickListener(this);
		logoutButton.setOnClickListener(this);
        pickDate.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                showDialog(DATE_DIALOG_ID);
            }
        });
        pickTime.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                showDialog(TIME_DIALOG_ID);
            }
        });
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

    @SuppressWarnings("rawtypes")
    public void onItemSelected(AdapterView parent, View v, int position, long id) {
    	// needed for interface, but not used
    }
    
    @SuppressWarnings("rawtypes")
	public void onNothingSelected(AdapterView parent) {
    	// needed for interface, but not used
    }
    
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.newmatch_cancel:
				finish();
				break;
			
			case R.id.newmatch_ok:
				if (validateForm()) {
					saveMatchinDB();
					Log.d("My Golf Card", "NewMatch");
					Intent i = new Intent(this, Card.class);
					i.putExtra("match_id", newmatch_id);
					startActivity(i);
					finish();
				}
				break;
				
			case R.id.logout:
				finish();
				break;

		}
	}

	private void saveMatchinDB() {
		saveMatchData();
	}
	

	
	private void saveMatchData() {
		String sql = "";
		try {
			sql = "insert into matches " +
					"(course_id, course_name, date_hour_match, holes, status, player1_id, tee1, player2_id, tee2, player3_id, tee3, player4_id, tee4) values " +
					"(" + "" + course_id + ",\"" + 
							course + "\",\"" +
							"" + date + " " + hour + "\"," + 
							"" + n_holes + "," +
							"0," + 
							"" + player_id[0] + "," + tee_id[0] + "," + 
							"" + player_id[1] + "," + tee_id[1] + "," + 
							"" + player_id[2] + "," + tee_id[2] + "," + 
							"" + player_id[3] + "," + tee_id[3] + ");";
			
			db = this.openOrCreateDatabase(DATABASE_NAME, MODE_PRIVATE, null);
		 	db.execSQL(sql);
			
		 	// Recupera el último partido introducido, para pasarlo a la siguiente página como param
			sql = "select ID from matches order by id desc limit 1;";
			sql = "select last_insert_rowid() as ID;";
			Cursor c 		= db.rawQuery(sql, null);
			int colMatchId	= c.getColumnIndex("ID");
			
			c.moveToFirst();
		 	
		 	if (c != null) {
		 		do {
		 			newmatch_id	= c.getInt(colMatchId);
		 		} while (c.moveToNext());
		 	}
		 	
		 	c.close();
			
			SharedPreferences.Editor editor = getPreferences(0).edit();
	        editor.putString("sql", sql);
	        editor.commit();
		        
			Log.i("SQL", "INSERTING NEW MATCH: " + sql);
		}
		catch(Exception e) {
    		Log.e("Error", "Error INSERTING new match", e);
    	} 
    	finally {
    		if (db != null)
    			db.close();
    	}
	}

	private boolean validateForm() {
		String res;
		boolean bPlayers = false;
		boolean bOK = true;
		
		// Recupera campos introducidos en formulario
		course 			= newmatch_course.getText().toString();
		course_id		= getCourseID(course);
		date 			= newmatch_date.getText().toString();
		hour			= newmatch_hour.getText().toString();
		n_holes 		= newmatch_n_holes.getText().toString();
		for (int i=0; i<4; i++) {
			player_id[i]	= getPlayerID(newmatch_player[i].getText().toString());
			if (player_id[i]>0) {
				tee_id[i]	= getTeeID(newmatch_tee[i]);
			}
			else {
				tee_id[i] 	= 0;
			}
		}
		
		// Validación de campos obligatorios
		res = "";
		if (course.length()<=0)
		{
			// Campos vacíos
			res += "- Es obligatorio rellenar 'CAMPO'\n";
			bOK = false;
		}
		
		if (date.length()<=0) {
			res += "- Es obligatorio rellenar 'FECHA'\n";
			bOK = false;
		}
		
		if (hour.length()<=0) {
			res += "- Es obligatorio rellenar 'HORA'\n";
			bOK = false;
		}
		
		if (n_holes.length()<=0) {
			res += "- Es obligatorio rellenar 'Nº HOYOS'\n";
			bOK = false;
		}
		
		if (Integer.parseInt(n_holes)<=0 || Integer.parseInt(n_holes)>18) {
			res += "- El 'Nº HOYOS' debe estar entre 1 y 18\n";
			bOK = false;
		}
		
		bPlayers = false;
		for (int i=0; i<4; i++) {
			String player 	= newmatch_player[i].getText().toString();
			if (player.length()>0) {
				bPlayers = true;
			}
		}
		
		if (!bPlayers) {
			res += "- Obligatorio al menos 1 jugador\n";
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

	private void getRemoteData() {
		coursesList = org.classes.mygolfcard.Course.getCoursesFromRemote(auth_token,NewMatch.this);
		friendsList = org.classes.mygolfcard.Player.getFriendsFromRemote(auth_token,auth_user_id,NewMatch.this);
	}
	
	private void setInfoCourses() {
		courses = new String[coursesList.length];
		for (int i=0;i<coursesList.length;i++) {
			courses[i] = coursesList[i].getCourseName();
		}
		
		newmatch_course.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, courses));
	}

	private void setInfoPlayers() {
		players = new String[friendsList.length];
		for (int i=0;i<friendsList.length;i++) {
			players[i] = friendsList[i].getPlayerName();
		}
			
		for (int i=0; i<friendsList.length; i++) {
			newmatch_player[i].setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, players));
		}
	}

/*	
 	private String getCourses() {
		String response;
    	
		Log.i( "newmatch", "getting courses ");
		
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
	    
	    Log.i( "newmatch", "getting courses " + response.toString());
	    
	    return response;
	}

	private String getFriends() {
		String response;
    	
		Log.i( "newmatch", "getting friends " + auth_user_id);
		
	    RestClient client = new RestClient(URL_FRIENDS);
	    client.AddParam("token", auth_token);
	    client.AddParam("user_id", auth_user_id);
	    
	    response = "";
	    try {
	        client.Execute(RequestMethod.POST);
	        response = client.getResponse();
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	    
	    Authentication.saveFriends(NewMatch.this, response);
	    aux_friends = response;
	    
	    Log.i( "newmatch", "getting friends " + response.toString());
	    
	    return response;
	}
	
*/	
	private void getScreenData() {
		SharedPreferences prefs = getPreferences(0); 
        String restoredText;
        
        restoredText = prefs.getString("course", null);
        if (restoredText != null) {
            newmatch_course.setText(restoredText, TextView.BufferType.EDITABLE);
        }
        
        restoredText = prefs.getString("date", null);
        if (restoredText != null) {
            newmatch_date.setText(restoredText, TextView.BufferType.EDITABLE);
        }
        
        restoredText = prefs.getString("hour", null);
        if (restoredText != null) {
            newmatch_hour.setText(restoredText, TextView.BufferType.EDITABLE);
        }
        
        restoredText = prefs.getString("holes", null);
        if (restoredText != null) {
            newmatch_n_holes.setText(restoredText, TextView.BufferType.EDITABLE);
        }
	}

	private void saveScreenData() {
		SharedPreferences.Editor editor = getPreferences(0).edit();
        editor.putString("course", newmatch_course.getText().toString());
        editor.putString("date", newmatch_date.getText().toString());
        editor.putString("hour", newmatch_hour.getText().toString());
        editor.putString("holes", newmatch_n_holes.getText().toString());
        editor.commit();
	}
	
	private int getCourseID(String find) {
		int res = 0;
		
		for (int i=0; i<coursesList.length; i++)
		{
			if (coursesList[i].getCourseName().equals(find)) {
				res = coursesList[i].getCourse_id();
			}
		}
		
		return res;
	}
	
	private int getPlayerID(String find) {
		int res = 0;
		
		for (int i=0; i<friendsList.length; i++)
		{
			if (friendsList[i].getPlayerName().equals(find)) {
				res = friendsList[i].getPlayer_id();
			}
		}
		
		return res;
	}
	
	private int getTeeID(Spinner find) {
		int res = 0;
		String value = "";
		
		value = find.getItemAtPosition(find.getSelectedItemPosition()).toString().toUpperCase();
		if (value.equals("AMARILLAS")) {
			res = 1;
		}
		
		if (value.equals("ROJAS")) {
			res = 2;
		}
		
		if (value.equals("BLANCAS")) {
			res = 3;
		}
		
		return res;
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
			getRemoteData();
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
			setInfoCourses();
			setInfoPlayers();
			//setListAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, courses));
		}
	}   

    
}

