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
import android.content.res.Configuration;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;
import android.widget.Toast;

public class Card extends Activity implements OnClickListener {
	private final View holeButton[] = new View[18];
	private SQLiteDatabase db = null;
	private String DATABASE_NAME;
	private String course_id;
	private String course_name;
	private String date_hour;
	private String n_holes;
	private String match_info;
	private String match_id;
	private String players_id[] = new String[4];
	
	private String[] players_field1;
	private String[] players_field2;
	
	private TextView cardMatch;
	
	private boolean connectionOK;
	private String auth_token;
	
	private String aux_holes;
	
	private String URL_HOLES;
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.card_2);
		
		DATABASE_NAME = getString(R.string.DB_NAME);
		
		String result = Authentication.readFriends(Card.this);
		setInfoPlayers(result);
		
		getMatchinDB();
		findViews();
		initViews();
		setListeners();
		
		URL_HOLES = getString(R.string.URL_APIS) + getString(R.string.ACTION_INFO_HOLES);
		
		connectionOK = Authentication.checkConnection(Card.this);
		if (connectionOK) {
			Authentication.readDataUser(Card.this);
			auth_token    = Authentication.getToken();
			InitTask task = new InitTask();
			task.execute();
		}
		else {
			Toast.makeText(Card.this, R.string.no_internet,
                    Toast.LENGTH_SHORT).show();
		}
	}
	
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
	  // ignore orientation/keyboard change
	  super.onConfigurationChanged(newConfig);
	}

	public void onClick(View v) {
		String hole_number;
		
		hole_number="0";
		switch (v.getId()) {
			case R.id.card_hole1:
				hole_number = "1";
				break;
			
			case R.id.card_hole2:
				hole_number = "2";
				break;
			
			case R.id.card_hole3:
				hole_number = "3";
				break;
				
			case R.id.card_hole4:
				hole_number = "4";
				break;
			
			case R.id.card_hole5:
				hole_number = "5";
				break;
				
			case R.id.card_hole6:
				hole_number = "6";
				break;
				
			case R.id.card_hole7:
				hole_number = "7";
				break;
				
			case R.id.card_hole8:
				hole_number = "8";
				break;
				
			case R.id.card_hole9:
				hole_number = "9";
				break;
				
			case R.id.card_hole10:
				hole_number = "10";
				break;
				
			case R.id.card_hole11:
				hole_number = "11";
				break;
				
			case R.id.card_hole12:
				hole_number = "12";
				break;
				
			case R.id.card_hole13:
				hole_number = "13";
				break;
				
			case R.id.card_hole14:
				hole_number = "14";
				break;
				
			case R.id.card_hole15:
				hole_number = "15";
				break;
				
			case R.id.card_hole16:
				hole_number = "16";
				break;
				
			case R.id.card_hole17:
				hole_number = "17";
				break;
				
			case R.id.card_hole18:
				hole_number = "18";
				break;
		}
		
		Intent i = new Intent(this, Strokes.class);
		i.putExtra("hole_number", hole_number);
		i.putExtra("total_holes", n_holes);
		i.putExtra("course_id", course_id);
		i.putExtra("match_info", match_info);
		i.putExtra("match_id", match_id);
		i.putExtra("players_id", players_id);
		startActivity(i); 
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.menu_card, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.synchro:
				startActivity(new Intent(this, Synchro.class));
				finish();
				
				return true;

			case R.id.resume:
				//startActivity(new Intent(this, Resume.class));
				new AlertDialog.Builder(this)
					.setIcon(R.drawable.info_dialog_icon_tra)
					.setTitle(R.string.resume_match)
					.setMessage(Html.fromHtml(getResumeInfo(match_id)))
					.setPositiveButton(R.string.alert_button_default, null)
					.show();
				return true;
	
			case R.id.card_graph:
	    		Intent i = new Intent(this, CardGraph.class);
	    		i.putExtra("match_id", match_id);
	    		i.putExtra("mitad", 1);
	    		i.putExtra("type_match", 1);
        		startActivity(i);

        		return true;
		}
		return false;
	}
	
	private void findViews() {
		holeButton[0]  	= findViewById(R.id.card_hole1);
		holeButton[1]  	= findViewById(R.id.card_hole2);
		holeButton[2]  	= findViewById(R.id.card_hole3);
		holeButton[3]  	= findViewById(R.id.card_hole4);
		holeButton[4]  	= findViewById(R.id.card_hole5);
		holeButton[5]  	= findViewById(R.id.card_hole6);
		holeButton[6]  	= findViewById(R.id.card_hole7);
		holeButton[7]  	= findViewById(R.id.card_hole8);
		holeButton[8]  	= findViewById(R.id.card_hole9);
		holeButton[9]  	= findViewById(R.id.card_hole10);
		holeButton[10] 	= findViewById(R.id.card_hole11);
		holeButton[11] 	= findViewById(R.id.card_hole12);
		holeButton[12] 	= findViewById(R.id.card_hole13);
		holeButton[13] 	= findViewById(R.id.card_hole14);
		holeButton[14] 	= findViewById(R.id.card_hole15);
		holeButton[15] 	= findViewById(R.id.card_hole16);
		holeButton[16] 	= findViewById(R.id.card_hole17);
		holeButton[17] 	= findViewById(R.id.card_hole18);
		cardMatch 		= (TextView)findViewById(R.id.card_match);
	}
	
	private void setListeners() {
		for (int i=0; i<holeButton.length; i++) {
			holeButton[i].setOnClickListener(this);
		}
	}

	private void initViews() {
		//match_info = "Campo : " + course_name + "\n" + "Fecha/Hora : " + date_hour;
		match_info = course_name + "\n" + date_hour;
		cardMatch.setText(match_info);
		for (int i = Integer.parseInt(n_holes); i < holeButton.length; i++) {
			//
			holeButton[i].setVisibility(4);
		}
	}

	private void getMatchinDB() {
		String sql;
		
		course_name = "";
		course_id	= "";
		date_hour	= "";
		
		try {
			db = this.openOrCreateDatabase(DATABASE_NAME, MODE_PRIVATE, null);
			match_id = getIntent().getCharSequenceExtra("match_id").toString();
			sql = "select * from matches where ID=" + match_id + ";";
		 	Cursor c = db.rawQuery(sql, null);
		 	int colCourseId		= c.getColumnIndex("course_id");
		    int colCourseName	= c.getColumnIndex("course_name");
		    int colDateHour		= c.getColumnIndex("date_hour_match");
		    int colHoles		= c.getColumnIndex("holes");
		    int colPlayer1		= c.getColumnIndex("player1_id");
		    int colPlayer2		= c.getColumnIndex("player2_id");
		    int colPlayer3		= c.getColumnIndex("player3_id");
		    int colPlayer4		= c.getColumnIndex("player4_id");
		 	
		 	c.moveToFirst();
		 	
		 	if (c != null) {
		 		do {
		 			course_name 	= c.getString(colCourseName);
		 			course_id 		= c.getString(colCourseId);
		 			date_hour		= c.getString(colDateHour);
		 			n_holes			= c.getString(colHoles);
		 			players_id[0]	= c.getString(colPlayer1);
		 			players_id[1]	= c.getString(colPlayer2);
		 			players_id[2]	= c.getString(colPlayer3);
		 			players_id[3]	= c.getString(colPlayer4);
		 		} while (c.moveToNext());
		 	}
		 	
		 	c.close();
		}
		catch(Exception e) {
    		Log.e("Error", "Error reading DB", e);
    	} 
    	finally {
    		if (db != null)
    			db.close();
    	}
	}
	
	private String getInfoHoles() {
		String response;
    	
		Log.i( "card", "getting info holes");
		
	    RestClient client = new RestClient(URL_HOLES);
	    client.AddParam("token", auth_token);
	    client.AddParam("course_id", course_id);
	    
	    response = "";
	    try {
	        client.Execute(RequestMethod.POST);
	        response = client.getResponse();
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	    
	    Authentication.saveInfoHoles(Card.this, response);
	    aux_holes = response;
	    
	    Log.i( "card", "getting holes " + response.toString());
	    
	    return response;
	}
	
	private void setInfoHoles(String result) {
		//	
	}

	private String getResumeInfo(String match_id) {
		String res = "";
		String sql = "";
		
		sql = "SELECT match_id, player_id, sum(strokes) as sum_strokes FROM strokes where match_id=" + match_id + " group by match_id, player_id order by sum(strokes) ";

		try {
			db = this.openOrCreateDatabase(DATABASE_NAME, MODE_PRIVATE, null);
		 	Cursor c = db.rawQuery(sql, null);
		 	int colPlayerId		= c.getColumnIndex("player_id");
		 	int colSumStrokes	= c.getColumnIndex("sum_strokes");
		 	
		 	c.moveToLast();
		 	c.moveToFirst();
		 	if (c != null) {
		 		if (c.getCount()>0) {
			 		do {
			 			res += "<b>" + getPlayerName(c.getString(colPlayerId)) + "</b> : " + c.getInt(colSumStrokes) + "<br>";
			 		}
			 		while (c.moveToNext());
		 		}
		 		else {
			 		res = "Todavía no hay datos de golpes asociados a este partido.";
			 	}		 		
		 	}
		 	else {
		 		res = "Todavía no hay datos de golpes asociados a este partido.";
		 	}
		 	
		 	c.close();
		}
		catch(Exception e) {
    		Log.e("Error", "Error reading DB", e);
    	} 
    	finally {
    		if (db != null)
    			db.close();
    	}
    	
		
		return res;
	}

	private void setInfoPlayers(String result) {
		JSONObject jsonObj;
		JSONArray  jsonArr;

		try {
			jsonArr = new JSONArray(result);
			
			players_field1 = new String[jsonArr.length()];
			players_field2 = new String[jsonArr.length()];
			
			for (int i=0; i<jsonArr.length(); i++) {
				jsonObj = new JSONObject(jsonArr.get(i).toString());
				
				players_field1[i] = jsonObj.getString("id");
				players_field2[i] = jsonObj.getString("name");
				Log.i("JSON", "" + i);				
			}			
		} catch (JSONException e) {
			e.printStackTrace();
		}		
	}

	private String getPlayerName(String playerId) {
		String res = "0";
		
		for (int i=0; i<players_field1.length; i++) {
			if (playerId.equals(players_field1[i])) {
				res = players_field2[i];
			}
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
			getInfoHoles();
			return "";
		}

		// -- gets called just before thread begins
		@Override
		protected void onPreExecute() 
		{
			Log.i( "makemachine", "onPreExecute()" );
			super.onPreExecute();
			this.dialog = ProgressDialog.show(Card.this, "Conexión Remota", "Recuperando datos de servidor remoto de My Golf Card.", true);
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
			setInfoHoles(aux_holes);
		}
	}   	
}
