package org.example.mygolfcard;

import org.example.mygolfcard.NewMatch.InitTask;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Button;
import android.widget.Toast;

public class Strokes extends Activity implements OnClickListener {
	private final Button playerButton[] = new Button[4];
	private View previousButton;
	private View nextButton;
	private int holeNumber;
	private int totalHoles;
	private int course_id;
	private String match_info;
	private String player_id[] = new String[4];
	private TextView tx1;
	private TextView tx2;
	
	private String[] players_field1;
	private String[] players_field2;
	
	private String[] holes_field1;
	private String[] holes_field2;
	private String[] holes_field3;
	private String[] holes_field4;
	private String[] holes_field5;
	private String[] holes_field6;
	private String[] holes_field7;
	private String[] info_holes;
	
	private boolean connectionOK;
	private String auth_token;
	private String auth_user_id;
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.strokes);
		
		holeNumber 	= Integer.parseInt(getIntent().getCharSequenceExtra("hole_number").toString());
		totalHoles	= Integer.parseInt(getIntent().getCharSequenceExtra("total_holes").toString());
		course_id 	= Integer.parseInt(getIntent().getCharSequenceExtra("course_id").toString());
		player_id	= getIntent().getStringArrayExtra("player_id");
		match_info 	= getIntent().getCharSequenceExtra("match_info").toString();
		
		String result = Authentication.readFriends(Strokes.this);
		setInfoPlayers(result);
		
		findViews();
		initViews();
		setListeners();
		
		result = Authentication.readInfoHoles(Strokes.this);
		
		if (result != "") {
			setInfoHoles(result);
		}
		else {
			Toast.makeText(Strokes.this, R.string.no_holes,
                    Toast.LENGTH_SHORT).show();
		}
		
/*		connectionOK = Authentication.checkConnection(Strokes.this);
		if (connectionOK) {
			Authentication.readDataUser(Strokes.this);
			auth_token    = Authentication.getToken();
			auth_user_id  = Authentication.getUserId();
			InitTask task = new InitTask();
			task.execute();
		}
		else {
			Toast.makeText(Strokes.this, R.string.no_internet,
                    Toast.LENGTH_SHORT).show();
		}
*/	}
	
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
				if (holeNumber < totalHoles) {
					++holeNumber;
					bRes = true;
				}
				break;
		}
		
		if (bRes) {
			
			Intent i = new Intent(this, Strokes.class);
			i.putExtra("hole_number", "" + holeNumber);
			i.putExtra("total_holes", "" + totalHoles);
			i.putExtra("course_id", "" + course_id);
			i.putExtra("match_info", match_info);
			i.putExtra("player_id", player_id);
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
					.setMessage(Html.fromHtml("<b>Bea (HCP : 36)</b> : 45<br><b>Eladio (HCP : 36)</b> : 46<br><b>Juan (HCP : 36)</b> : 48<br><b>Merche (HCP : 36)</b> : 50"))
					.setPositiveButton(R.string.alert_button_default, null)
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
	
	void setKey(int targetView, String value) {
		String res;
		int iPos;
		
		Button b = (Button) findViewById(targetView);
		res = b.getText().toString();
		iPos = res.indexOf(" : ");
		if (iPos>=0) {
			res = res.substring(0,iPos);
		}
		
		b.setText(res + " : " + value);
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
		tx1.setText(match_info);		
		
		for (int i = 0; i < playerButton.length; i++) {
			playerButton[i].setText(getPlayerName(player_id[i]));
			setKey(playerButton[i].getId(), "0");
			
			if (player_id[i].equals("0")) {
				playerButton[i].setVisibility(4);
			}
		}
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

	private void setInfoHoles(String result) {
		JSONObject jsonObj;
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
				holes_field3[i] = jsonObj.getString("par");
				holes_field4[i] = jsonObj.getString("length_yellow");
				holes_field5[i] = jsonObj.getString("length_red");
				holes_field6[i] = jsonObj.getString("length_white");
				holes_field7[i] = jsonObj.getString("handicap");
				
				info_holes[number] = "Hoyo " + holeNumber + ":\nPar : " + holes_field3[i] + "     Handicap : " +  holes_field7[i] + "\nLong : (R) " +  holes_field5[i] + "  (A) " +  holes_field4[i] + "";
				Log.i("JSON", "" + i);
			}
			
			tx2.setText(info_holes[holeNumber]);
			
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
	}
	
	/** Open the keypad if there are any valid moves */
	protected void showKeypad(int playerPush) {
		Log.d("My Golf Card", "showKeypad");
		Dialog v = new Keypad(this, playerPush);
		v.show();
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
	
} 
