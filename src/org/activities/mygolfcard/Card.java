/**
 * Package: org.activities.mygolfcard
 * File: Card.java
 * Description:
 * Create At: ---
 * Created By: ERL
 * Last Modifications:
 * 		05/11/2010 - ERL - POO
 */
package org.activities.mygolfcard;

import org.activities.mygolfcard.RestClient.RequestMethod;
import org.classes.mygolfcard.CurrentUser;
import org.classes.mygolfcard.Player;

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
	private TextView cardMatch;
	
	private SQLiteDatabase db = null;
	private String DATABASE_NAME;
	
	private String match_info;
	private int match_id;
	private org.classes.mygolfcard.Match currentMatch;
	private int players_id[] = new int[4];
	
	private Player[] players;
	
	private boolean connectionOK;
	private String auth_token;
	
	private String aux_holes;
	
	private String URL_HOLES;
	
	private CurrentUser cUser = new CurrentUser();
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.card_2);
		
		DATABASE_NAME = getString(R.string.DB_NAME);
		
		match_id = getIntent().getIntExtra("match_id",0);
		
		Authentication.readDataUser(Card.this);
		auth_token 		= Authentication.getToken();
		cUser.setUser_id(Authentication.getUserId());
		
		getMatchinDB();
		findViews();
		initViews();
		setListeners();
		
		URL_HOLES = getString(R.string.URL_APIS) + getString(R.string.ACTION_INFO_HOLES);
		
		connectionOK = Authentication.checkConnection(Card.this);
		if (connectionOK) {
			
			
			players = Player.getPlayersFromRemote(auth_token, cUser.getUser_id(), Card.this);
			
			InitTask task = new InitTask();
			task.execute();
		}
		else {
			players = Player.getPlayersFromLocal(Card.this);
			
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
		i.putExtra("total_holes", currentMatch.getHoles());
		i.putExtra("course_id", currentMatch.getCourse_id());
		i.putExtra("match_info", match_info);
		i.putExtra("match_id", match_id);
		Player[] pls = currentMatch.getPlayers();
		for (int t=0;t<4;t++) {
			players_id[t] = pls[t].getUserWeb_id();
		}
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
		match_info = currentMatch.getCourseName() + "\n" + currentMatch.getDateHour();
		//match_info = course_name + "\n" + date_hour;
		cardMatch.setText(match_info);
		for (int i = currentMatch.getHoles(); i < holeButton.length; i++) {
			holeButton[i].setVisibility(4);
		}
	}

	private void getMatchinDB() {
		currentMatch = org.classes.mygolfcard.Match.getMatchFromDB(Card.this, match_id);
	}
	
	private String getInfoHoles() {
		String response;
    	
		Log.i( "card", "getting info holes : " + URL_HOLES); 
		
	    RestClient client = new RestClient(URL_HOLES);
	    client.AddParam("token", auth_token);
	    client.AddParam("course_id", "" + currentMatch.getCourse_id());
	    
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
			 			res += "<b>" + getPlayerName(Integer.parseInt(c.getString(colPlayerId))) + "</b> : " + c.getInt(colSumStrokes) + "<br>";
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

	private String getPlayerName(int playerId) {
		String res = "0";
		
		for (int i=0; i<players.length; i++) {
			if (players[i].getPlayer_id() == playerId) {
				res = players[i].getPlayerName();
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
