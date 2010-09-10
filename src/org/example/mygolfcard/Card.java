package org.example.mygolfcard;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;

public class Card extends Activity implements OnClickListener {
	private final View holeButton[] = new View[18];
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.card_2);

		findViews();
		initButtons();
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
		holeButton[0]  = findViewById(R.id.card_hole1);
		holeButton[1]  = findViewById(R.id.card_hole2);
		holeButton[2]  = findViewById(R.id.card_hole3);
		holeButton[3]  = findViewById(R.id.card_hole4);
		holeButton[4]  = findViewById(R.id.card_hole5);
		holeButton[5]  = findViewById(R.id.card_hole6);
		holeButton[6]  = findViewById(R.id.card_hole7);
		holeButton[7]  = findViewById(R.id.card_hole8);
		holeButton[8]  = findViewById(R.id.card_hole9);
		holeButton[9]  = findViewById(R.id.card_hole10);
		holeButton[10] = findViewById(R.id.card_hole11);
		holeButton[11] = findViewById(R.id.card_hole12);
		holeButton[12] = findViewById(R.id.card_hole13);
		holeButton[13] = findViewById(R.id.card_hole14);
		holeButton[14] = findViewById(R.id.card_hole15);
		holeButton[15] = findViewById(R.id.card_hole16);
		holeButton[16] = findViewById(R.id.card_hole17);
		holeButton[17] = findViewById(R.id.card_hole18);
	}
	
	private void setListeners() {
		for (int i=0; i<holeButton.length; i++) {
			holeButton[i].setOnClickListener(this);
		}
	}

	private void initButtons() {
		for (int i = 0; i < holeButton.length; i++) {
			//
		}
	}

	
}
