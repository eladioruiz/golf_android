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
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;

public class Match extends Activity {
	private String auth_token;
	private String auth_user_id;
	
	private String match_id;
	private String match_course_name;
	private String match_date_hour;
	private String match_holes;
	private String match_players[] 				= new String[4];
	private String match_players_HCP[] 			= new String[4];
	private String match_players_strokes[][] 	= new String[4][3];
	
	private int aux_player_id[] 	= new int[4]; 
	private int aux_playerweb_id[] 	= new int[4];
	
	private String URL;
	
	private TextView txtCourseName;
	private TextView txtDateHour;
	private TextView txtHoles;
	private TextView txtPlayers[]		= new TextView[4];;
	private TextView txtPlayers_1P[]	= new TextView[4];;
	private TextView txtPlayers_2P[]	= new TextView[4];;
	private TextView txtPlayers_TO[]	= new TextView[4];;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.match);
		
		URL = getString(R.string.URL_APIS) + getString(R.string.ACTION_MATCH);
		
		match_id = getIntent().getCharSequenceExtra("match_id").toString();

		Authentication.readDataUser(Match.this);
		auth_token = Authentication.getToken();
		auth_user_id = Authentication.getUserId();
		
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
		inflater.inflate(R.menu.menu_match, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.card_graph:
	    		Intent i = new Intent(this, CardGraph.class);
	    		i.putExtra("match_id", match_id);
	    		i.putExtra("mitad", 1);
	    		i.putExtra("type_match", 2);
	    		startActivity(i);
	
	    		return true;
		}
		return false;
	}
	
	private void findViews() {
		txtCourseName 	= (TextView) findViewById(R.id.match_course_name);
		txtDateHour		= (TextView) findViewById(R.id.match_date_hour);
		txtHoles		= (TextView) findViewById(R.id.match_holes);
		txtPlayers[0]	= (TextView) findViewById(R.id.match_player_1);
		txtPlayers[1]	= (TextView) findViewById(R.id.match_player_2);
		txtPlayers[2]	= (TextView) findViewById(R.id.match_player_3);
		txtPlayers[3]	= (TextView) findViewById(R.id.match_player_4);
		txtPlayers_1P[0]	= (TextView) findViewById(R.id.match_player_1_1P);
		txtPlayers_1P[1]	= (TextView) findViewById(R.id.match_player_2_1P);
		txtPlayers_1P[2]	= (TextView) findViewById(R.id.match_player_3_1P);
		txtPlayers_1P[3]	= (TextView) findViewById(R.id.match_player_4_1P);
		txtPlayers_2P[0]	= (TextView) findViewById(R.id.match_player_1_2P);
		txtPlayers_2P[1]	= (TextView) findViewById(R.id.match_player_2_2P);
		txtPlayers_2P[2]	= (TextView) findViewById(R.id.match_player_3_2P);
		txtPlayers_2P[3]	= (TextView) findViewById(R.id.match_player_4_2P);
		txtPlayers_TO[0]	= (TextView) findViewById(R.id.match_player_1_TO);
		txtPlayers_TO[1]	= (TextView) findViewById(R.id.match_player_2_TO);
		txtPlayers_TO[2]	= (TextView) findViewById(R.id.match_player_3_TO);
		txtPlayers_TO[3]	= (TextView) findViewById(R.id.match_player_4_TO);
	}

	private void initViews() {
		txtCourseName.setText(match_course_name);
		txtDateHour.setText(match_date_hour);
		txtHoles.setText(match_holes + " hoyos");
		
		for (int i=0;i<4;i++) {
			txtPlayers[i].setText(match_players[i] + " (HCP:" + match_players_HCP[i] + ")");
			txtPlayers_1P[i].setText(match_players_strokes[i][0]);
			txtPlayers_2P[i].setText(match_players_strokes[i][1]);
			txtPlayers_TO[i].setText(match_players_strokes[i][2]);
			if (match_players[i]==null) {
				txtPlayers[i].setVisibility(4);
				txtPlayers_1P[i].setVisibility(4);
				txtPlayers_2P[i].setVisibility(4);
				txtPlayers_TO[i].setVisibility(4);
			}	
		}
	}
	
	public String getMatch() {
		String response;
    	
	    RestClient client = new RestClient(URL);
	    client.AddParam("token", auth_token);
	    client.AddParam("user_id", auth_user_id);
	    client.AddParam("match_id", "" + match_id);
	    
	    Log.i("RESPONSE", "" + match_id);
	        
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

	public void setInfoMatch(String result) {
		JSONObject jsonObj;
		JSONArray  jsonArr;
		String aux_players;

		match_course_name = "";
		match_date_hour = "";
		try {
			jsonObj = new JSONObject(result);
			
			aux_players 		= jsonObj.getString("players");
			match_course_name 	= jsonObj.getString("course_name");
			match_date_hour		= jsonObj.getString("date_hour_match");
			match_holes			= jsonObj.getString("holes");
			
			jsonArr = new JSONArray(aux_players);
			
			for (int i=0; i<jsonArr.length(); i++) {
				jsonObj = new JSONObject(jsonArr.get(i).toString());
				
				aux_player_id[i] 	= Integer.parseInt(jsonObj.getString("user_id"));
				aux_playerweb_id[i] = Integer.parseInt(jsonObj.getString("player_id"));
				
				match_players[i] 			= jsonObj.getString("user_name");
				match_players_HCP[i] 		= jsonObj.getString("handicap");
				match_players_strokes[i][0] = jsonObj.getString("card_1");
				match_players_strokes[i][1] = jsonObj.getString("card_2");
				match_players_strokes[i][2] = jsonObj.getString("card_total");
				
				Log.i("JSON", "" + aux_player_id[i]);
				
			}
			Log.i("JSON", "" + aux_players);
					
		} catch (JSONException e) {
			e.printStackTrace();
		}
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
			return getMatch();
		}

		// -- gets called just before thread begins
		@Override
		protected void onPreExecute() 
		{
			Log.i( "makemachine", "onPreExecute()" );
			super.onPreExecute();
			CharSequence title_remote = getString(R.string.title_remote_connection);
			CharSequence remote = getString(R.string.remote_connection);
			this.dialog = ProgressDialog.show(Match.this, title_remote, remote, true);
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
			setInfoMatch(result);
			initViews();
		}
	}   	
}
