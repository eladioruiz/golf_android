package org.example.mygolfcard;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

public class Card extends Activity implements OnClickListener {
	private final View holeButton[] = new View[18];
	private SQLiteDatabase db = null;
	private String DATABASE_NAME = "mygolfcard";
	private String course_id;
	private String course_name;
	private String date_hour;
	private String n_holes;
	private String match_info;
	private TextView cardMatch;
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.card_2);
		
		getMatchinDB();
		findViews();
		initViews();
		setListeners();
		
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
		startActivity(i); 
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
			case R.id.synchro:
				startActivity(new Intent(this, Prefs.class));
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
		match_info = "Campo : " + course_name + "\n" + "Fecha/Hora : " + date_hour;
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
			sql = "select course_id, course_name, date_hour_match, holes from matches order by id desc limit 1;";
		 	Cursor c = db.rawQuery(sql, null);
		 	int colCourseId		= c.getColumnIndex("course_id");
		    int colCourseName	= c.getColumnIndex("course_name");
		    int colDateHour		= c.getColumnIndex("date_hour_match");
		    int colHoles		= c.getColumnIndex("holes");
		 	
		 	c.moveToFirst();
		 	
		 	if (c != null) {
			    do {
			 		course_name = c.getString(colCourseName);
			 	    course_id 	= c.getString(colCourseId);
			 	    date_hour	= c.getString(colDateHour);
			 	    n_holes		= c.getString(colHoles);
			 	} while (c.moveToNext());
		 	}
		}
		catch(Exception e) {
    		Log.e("Error", "Error reading DB", e);
    	} 
    	finally {
    		if (db != null)
    			db.close();
    	}
	}
}
