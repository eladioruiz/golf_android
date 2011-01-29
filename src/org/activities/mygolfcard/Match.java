/**
 * Package: org.activities.mygolfcard
 * File: Match.java
 * Description:
 * Create At: ---
 * Created By: ERL
 * Last Modifications:
 * 		20/10/2010 - ERL - POO
 */
package org.activities.mygolfcard;

import org.charts.mygolfcard.IChart;
import org.classes.mygolfcard.Authentication;
import org.classes.mygolfcard.Player;
import org.classes.mygolfcard.User;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.TextView;

public class Match extends Activity implements OnClickListener {
	private String auth_token;
	private User cUser = new User();
	
	private int localMatchId;
	private org.classes.mygolfcard.Match currentMatch;
	
	private Player auxPlayer[] = new Player[4];

	private TextView txtCourseName;
	private TextView txtDateHour;
	private TextView txtHoles;
	private TextView txtPlayers[]		= new TextView[4];
	private TextView txtPlayers_1P[]	= new TextView[4];
	private TextView txtPlayers_2P[]	= new TextView[4];
	private TextView txtPlayers_TO[]	= new TextView[4];
	private View logoutButton;
	private TextView titleNameText;
	
	private IChart[] mCharts = new IChart[] {new ChartMatch()};
	
	private static int TYPE_STROKES = 1;
	private static int TYPE_TOTAL 	= 2;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
        setContentView(R.layout.match);
        getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.custom_title_2);
		
		currentMatch = new org.classes.mygolfcard.Match(this);
		
		localMatchId = getIntent().getIntExtra("match_id",0);
		currentMatch.setMatch_id(localMatchId);

		findViews();
		//initViews(); // ERL No se pone aqui, sólo depués de cargar los datos
		setListeners();
		
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
	    		Intent i1 = new Intent(this, CardGraph.class);
	    		i1.putExtra("match_id", localMatchId);
	    		i1.putExtra("mitad", 1);
	    		i1.putExtra("type_match", 2);
	    		startActivity(i1);
	
	    		return true;
			
			case R.id.menuapp:
				finish();
				startActivity(new Intent(this, MenuApp.class));
				return true;
				
			case R.id.stats1:
				Intent i2 = mCharts[0].execute(this, currentMatch, TYPE_STROKES);
				startActivity(i2);
				return true;

			case R.id.stats2:
				Intent i3 = mCharts[0].execute(this, currentMatch, TYPE_TOTAL);
				startActivity(i3);
				return true;
				
		}
		return false;
	}
	
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.logout:
				finish();
			break;
		}
	}
	
	private void findViews() {
		txtCourseName 		= (TextView) findViewById(R.id.match_course_name);
		txtDateHour			= (TextView) findViewById(R.id.match_date_hour);
		txtHoles			= (TextView) findViewById(R.id.match_holes);
		txtPlayers[0]		= (TextView) findViewById(R.id.match_player_1);
		txtPlayers[1]		= (TextView) findViewById(R.id.match_player_2);
		txtPlayers[2]		= (TextView) findViewById(R.id.match_player_3);
		txtPlayers[3]		= (TextView) findViewById(R.id.match_player_4);
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
		logoutButton 		= findViewById(R.id.logout);
		titleNameText		= (TextView) findViewById(R.id.titleName); 
	}

	private void initViews() {
		txtCourseName.setText(currentMatch.getCourseName());
		txtDateHour.setText(currentMatch.getDateHour());
		txtHoles.setText(currentMatch.getHoles() + " hoyos");
		auxPlayer = currentMatch.getPlayers();
		
		for (int i=0;i<4;i++) {
			if (auxPlayer[i]!=null) {
				txtPlayers[i].setText(auxPlayer[i].getPlayerName() + " (HCP:" + auxPlayer[i].getHCP() + ")");
				txtPlayers_1P[i].setText("" + auxPlayer[i].getStrokesFirst());
				txtPlayers_2P[i].setText("" + auxPlayer[i].getStrokesSecond());
				txtPlayers_TO[i].setText("" + auxPlayer[i].getStrokesTotal());
			}
			else {
				txtPlayers[i].setVisibility(4);
				txtPlayers_1P[i].setVisibility(4);
				txtPlayers_2P[i].setVisibility(4);
				txtPlayers_TO[i].setVisibility(4);
			}	
		}
		
		Authentication.readDataUser(Match.this);
		auth_token = Authentication.getToken();
		cUser.setUser_id(Authentication.getUserId());
		cUser.setUserName(Authentication.getUserName());
		titleNameText.setText(cUser.getUserName());
	}

	private void setListeners() {
		logoutButton.setOnClickListener(this);
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
			currentMatch = org.classes.mygolfcard.Match.setDataFromRemote(localMatchId, cUser.getUser_id(), auth_token, Match.this);
			return "";
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
		}

		// -- called as soon as doInBackground method completes
		// -- notice that the third param gets passed to this method
		@Override
		protected void onPostExecute( String result ) 
		{
			super.onPostExecute(result);
			Log.i( "makemachine", "onPostExecute(): " + result );
			this.dialog.cancel();
			initViews();
		}
	}   	
}
