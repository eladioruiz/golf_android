package org.activities.mygolfcard;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
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
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class Strokes extends Activity implements OnClickListener {
	private final Button playerButton[] = new Button[4];
	private Button previousButton;
	private Button nextButton;
	private org.classes.mygolfcard.Match currentMatch;
	private int holeNumber;
	private String match_info;
	private int match_id; 
	private int players_id[] = new int[4];
	private org.classes.mygolfcard.Player[] pls = new org.classes.mygolfcard.Player[4];
	private TextView tx1;
	private TextView tx2;
	
	private boolean connectionOK;
	private String auth_token;
	
	private org.classes.mygolfcard.Hole[] holes;
	
	private SQLiteDatabase db = null;
	private String DATABASE_NAME; 
	
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.strokes);
		
		DATABASE_NAME = getString(R.string.DB_NAME); 
		match_id = getIntent().getIntExtra("match_id",0);
		currentMatch = new org.classes.mygolfcard.Match(Strokes.this);
		currentMatch = org.classes.mygolfcard.Match.getMatchFromDB(Strokes.this, match_id);
		holeNumber 	= Integer.parseInt(getIntent().getCharSequenceExtra("hole_number").toString());
		players_id	= getIntent().getIntArrayExtra("players_id");
		match_info 	= getIntent().getCharSequenceExtra("match_info").toString();
		
		String result = Authentication.readFriends(Strokes.this);
		setInfoPlayers(result);
		
		findViews();
		initViews();
		setListeners();
		
		connectionOK = Authentication.checkConnection(Strokes.this);
		if (connectionOK) {
			Authentication.readDataUser(Strokes.this);
			auth_token    = Authentication.getToken();
			InitTask task = new InitTask();
			task.execute();
		}
		else {
			Toast.makeText(Strokes.this, R.string.no_internet,
                    Toast.LENGTH_SHORT).show();
		}
	}
	
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
	  // ignore orientation/keyboard change
	  super.onConfigurationChanged(newConfig);
	}

	public void onClick(View v) {
		boolean bRes = false;
		
		switch (v.getId()) {
			case R.id.strokes_previous:
				if (holeNumber > 1) {
					--holeNumber;
					bRes = true;
				}
				break;
			
			case R.id.strokes_next:
				if (holeNumber < currentMatch.getHoles()) {
					++holeNumber;
					bRes = true;
				}
				break;
		}
		
		if (bRes) {
			
			Intent i = new Intent(this, Strokes.class);
			i.putExtra("hole_number", "" + holeNumber);
			i.putExtra("total_holes",currentMatch.getHoles());
			i.putExtra("course_id", currentMatch.getCourse_id());
			i.putExtra("match_info", match_info);
			i.putExtra("match_id", currentMatch.getMatch_id());
			i.putExtra("players_id", players_id);
			startActivity(i);
			finish();
		
			switch (v.getId()) {
				case R.id.strokes_previous:
					overridePendingTransition(R.anim.slide_right, R.anim.slide_right);
					break;
				
				case R.id.strokes_next:
					overridePendingTransition(R.anim.slide_left, R.anim.slide_left);
					break;
			}	
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.menu_strokes, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.resume:
				//startActivity(new Intent(this, Resume.class));
				new AlertDialog.Builder(this)
					.setIcon(R.drawable.info_dialog_icon_tra)
					.setTitle(R.string.resume_match)
					.setMessage(Html.fromHtml(getResumeInfo(currentMatch.getMatch_id())))
					.setPositiveButton(R.string.alert_button_default, null)
					.show();
				return true;
				
			case R.id.synchro:
				startActivity(new Intent(this, Synchro.class));
				finish();
				return true;
				
			case R.id.menuapp:
				startActivity(new Intent(this, MenuApp.class));
				finish();
				return true;
				
			case R.id.card_graph:
	    		Intent i = new Intent(this, CardGraph.class);
	    		i.putExtra("match_id", currentMatch.getMatch_id());
	    		i.putExtra("mitad", 1);
	    		i.putExtra("type_match", 1);
        		startActivity(i);

        		return true;
				
		}
		return false;
	}
	
	void setKey(int targetView, String value) {
		String res;
		int iPos;
		int aux_match_id;
		int aux_player_id;
		int aux_hole;
		String aux_strokes;
		String aux_putts = "0"; // Por defecto, no se utiliza todavia
		
		Button b = (Button) findViewById(targetView);
		res = b.getText().toString();
		iPos = res.indexOf(" : ");
		if (iPos>=0) {
			res = res.substring(0,iPos);
		}
		
		b.setText(res + " : " + value);
		
		aux_match_id = currentMatch.getMatch_id();
		aux_player_id = getPlayerIdFromView(targetView);
		aux_hole = holeNumber;
		aux_strokes = value;
		
		String sql = "";
		try {
			// Guardamos la información también BD
			sql = "delete from strokes where match_id=" + aux_match_id + " and player_id=" + aux_player_id + " and hole=" + aux_hole + ";";
			db = this.openOrCreateDatabase(DATABASE_NAME, MODE_PRIVATE, null);
		 	db.execSQL(sql);
		 	
		 	Log.i("SQL", "DELETING STROKES: " + sql);
		 	
		 	sql = "insert into strokes (match_id, player_id, hole, strokes, putts) values (" + aux_match_id + "," + aux_player_id + "," + aux_hole + "," + aux_strokes + "," + aux_putts + ");";
			db = this.openOrCreateDatabase(DATABASE_NAME, MODE_PRIVATE, null);
		 	db.execSQL(sql);
			
			Log.i("SQL", "INSERTING STROKES: " + sql);
		}
		catch(Exception e) {
    		Log.e("Error", "Error INSERTING new match", e);
    	} 
    	finally {
    		if (db != null)
    			db.close();
    	}
	}

	private int getPlayerIdFromView(int v) {
		int res = 0;
		
		switch (v) {
			case R.id.strokesButton_1:
				res = players_id[0];
				break;
			case R.id.strokesButton_2:
				res = players_id[1];
				break;
			case R.id.strokesButton_3:
				res = players_id[2];
				break;
			case R.id.strokesButton_4:
				res = players_id[3];
				break;
		}
		return res;
	}
	
	private void findViews() {
		playerButton[0] = (Button) findViewById(R.id.strokesButton_1);
		playerButton[1] = (Button) findViewById(R.id.strokesButton_2);
		playerButton[2] = (Button) findViewById(R.id.strokesButton_3);
		playerButton[3] = (Button) findViewById(R.id.strokesButton_4);
		
		previousButton = (Button) findViewById(R.id.strokes_previous);
		nextButton = (Button) findViewById(R.id.strokes_next);
		
		tx1 = (TextView) findViewById(R.id.card_match);
		tx2 = (TextView) findViewById(R.id.hole_info);
	}
	
	private void setListeners() {
		for (int i = 0; i < playerButton.length; i++) {
			playerButton[i].setOnClickListener(new View.OnClickListener(){
				public void onClick(View v) {
					showKeypad(v.getId());
				}});
		}
		
		previousButton.setOnClickListener(this);
		nextButton.setOnClickListener(this);
	}

	private void initViews() {
		String strokes = "0";
		
		tx1.setText(match_info);		
		
		for (int i = 0; i < playerButton.length; i++) {
			strokes = getStrokesHole(currentMatch.getMatch_id(),players_id[i],holeNumber);
			playerButton[i].setText(getPlayerName(players_id[i]) + " : " + strokes);
			//setKey(playerButton[i].getId(), "0");
			
			if (players_id[i] == 0) {
				playerButton[i].setVisibility(4);
			}
		}
		
		previousButton.setText(formatHoleNumber(holeNumber-1));
		nextButton.setText(formatHoleNumber(holeNumber+1));
		
		previousButton.setEnabled(true);
		nextButton.setEnabled(true);
		if (holeNumber == 1) {
			previousButton.setEnabled(false);
			previousButton.setText("--");
		}
		
		if (holeNumber == currentMatch.getHoles()) {
			nextButton.setEnabled(false);
			nextButton.setText("--");
		}
	}

	private void setInfoPlayers(String result) {
		pls = org.classes.mygolfcard.Player.getPlayersFromLocal(Strokes.this);
/*		JSONObject jsonObj;
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
*/
	}
	

	private void setInfoHoles() {
		tx2.setText(org.classes.mygolfcard.Hole.getTextInfo_hole(holes,holeNumber));
/*		JSONObject jsonObj;
		JSONArray  jsonArr;

		try {
			jsonArr = new JSONArray(result);
			
			holes_field1 	= new String[jsonArr.length()]; // id hole
			holes_field2 	= new String[jsonArr.length()]; // n hole
			holes_field3 	= new String[jsonArr.length()]; // par hole
			holes_field4 	= new String[jsonArr.length()]; // length yellow hole
			holes_field5 	= new String[jsonArr.length()]; // length red hole
			holes_field6 	= new String[jsonArr.length()]; // length white hole
			holes_field7 	= new String[jsonArr.length()]; // handicap
			info_holes		= new String[jsonArr.length()+1]; // info display
			
			for (int i=0; i<jsonArr.length(); i++) {
				jsonObj = new JSONObject(jsonArr.get(i).toString());
				
				holes_field1[i] = jsonObj.getString("id");
				int number 		= Integer.parseInt(jsonObj.getString("number"));
				holes_field2[i] = jsonObj.getString("number");
				holes_field3[i] = showNull(jsonObj.getString("par"),"---");
				holes_field4[i] = showNull(jsonObj.getString("length_yellow"),"---");
				holes_field5[i] = showNull(jsonObj.getString("length_red"),"---");
				holes_field6[i] = showNull(jsonObj.getString("length_white"),"---");
				holes_field7[i] = showNull(jsonObj.getString("handicap"),"---");
				
				info_holes[number] = "Hoyo " + holeNumber + ":\nPar : " + holes_field3[i] + "     Handicap : " +  holes_field7[i] + "\nLong : (R) " +  holes_field5[i] + "  (A) " +  holes_field4[i] + "";
				Log.i("JSON", "" + i);
			}
			
			tx2.setText(info_holes[holeNumber]);
			
		} catch (JSONException e) {
			e.printStackTrace();
		}
		*/
	}	
	
	/** Open the keypad if there are any valid moves */
	protected void showKeypad(int playerPush) {
		Log.d("My Golf Card", "showKeypad");
		Dialog v = new Keypad(this, playerPush);
		v.show();
	}
	
	private String getPlayerName(int playerId) {
		String res = "0";
		
		for (int i=0; i<pls.length; i++) {
			if (playerId == pls[i].getPlayer_id()) {
				res = pls[i].getPlayerName();
			}
		}
		
		return res;
	}
	
	private String getStrokesHole(int match_id, int player_id, int hole){
		String res = "0";
		String sql = "";
		
		try {
			db = this.openOrCreateDatabase(DATABASE_NAME, MODE_PRIVATE, null);
			sql = "select strokes from strokes where match_id=" + match_id + " and player_id=" + player_id + " and hole=" + hole + ";";
		 	Cursor c = db.rawQuery(sql, null);
		 	int colStrokes		= c.getColumnIndex("strokes");
		 	
		 	c.moveToFirst();
		 	if (c != null) {
		 		res = "" + c.getInt(colStrokes);
		 	}
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

	private String getResumeInfo(int match_id) {
		String res = "";
		String sql = "";
		
		sql = "SELECT match_id, player_id, sum(strokes) as sum_strokes FROM strokes where match_id=" + match_id + " group by match_id, player_id order by sum(strokes) ";

		try {
			db = this.openOrCreateDatabase(DATABASE_NAME, MODE_PRIVATE, null);
		 	Cursor c = db.rawQuery(sql, null);
		 	
		 	if (c != null) {
		 		int colPlayerId		= c.getColumnIndex("player_id");
			 	int colSumStrokes	= c.getColumnIndex("sum_strokes");
			 	
			 	c.moveToLast();
			 	c.moveToFirst();
		 	
		 		if (c.getCount()>0) {
			 		do {
			 			res += "<b>" + getPlayerName(c.getInt(colPlayerId)) + "</b> : " + c.getInt(colSumStrokes) + "<br>";
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
	
	private String showNull(String value, String def)
	{
		String res = "";
		
		res = value;
		
		if (value == null || value.equals("null")) {
			res = def;
		}
		
		return res;
	}

	private String formatHoleNumber(int hole) {
		if (hole < 10) {
			return " " + hole;
		}
		else {
			return "" + hole;
		}
	}
/*	
	private String getInfoHoles() {
		String response;
    	
		Log.i( "strokes", "getting info holes");
		
	    RestClient client = new RestClient(URL_HOLES);
	    client.AddParam("token", auth_token);
	    client.AddParam("course_id", "" + course_id);
	    
	    response = "";
	    try {
	        client.Execute(RequestMethod.POST);
	        response = client.getResponse();
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	    
	    Authentication.saveInfoHoles(Strokes.this, response);
	    aux_holes = response;
	    
	    Log.i( "strokes", "getting holes " + response.toString());
	    
	    return response;
	}
*/
	
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
			//getInfoHoles();
			holes = org.classes.mygolfcard.Hole.getInfoHoles(currentMatch.getCourse_id(),auth_token, Strokes.this);
			return "";
		}

		// -- gets called just before thread begins
		@Override
		protected void onPreExecute() 
		{
			Log.i( "makemachine", "onPreExecute()" );
			super.onPreExecute();
			this.dialog = ProgressDialog.show(Strokes.this, "Conexión Remota", "Recuperando datos de servidor remoto de My Golf Card.", true);
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
			setInfoHoles();
		}
	}   	

} 
