package org.example.mygolfcard;

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
import android.widget.TextView;
import android.widget.Button;

public class Strokes extends Activity implements OnClickListener {
	private View strokesView;
	private final View playerButton[] = new View[4];
	private View previousButton;
	private View nextButton;
	private int holeNumber;
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.strokes);
		strokesView = findViewById(R.layout.strokes);
		
		holeNumber = Integer.parseInt(getIntent().getCharSequenceExtra("hole_number").toString());
		TextView tx = (TextView) findViewById(R.id.hole_info);
		tx.setText("Hoyo " + holeNumber + ":\nPar : 5     Handicap :18\nLong : (R) 5879  (A) 6378");
		
		findViews();
		initButtons();
		setListeners();
	}
	
	public void onClick(View v) {
		boolean bRes = false;
		
		switch (v.getId()) {
			case R.id.strokes_previous:
				if (holeNumber>1) {
					--holeNumber;
					bRes = true;
				}
				break;
			
			case R.id.strokes_next:
				if (holeNumber<18) {
					++holeNumber;
					bRes = true;
				}
				break;
		}
		
		if (bRes) {
			finish();
			
			Intent i = new Intent(this, Strokes.class);
			i.putExtra("hole_number", "" + holeNumber);
			startActivity(i);
		
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
					.setTitle("RESUMEN PARTIDO")
					.setMessage(Html.fromHtml("<b>Bea (HCP : 36)</b> : 45<br><b>Eladio (HCP : 36)</b> : 46<br><b>Juan (HCP : 36)</b> : 48<br><b>Merche (HCP : 36)</b> : 50"))
					.setPositiveButton("Aceptar", null)
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
	
	void setKey(String value, int targetView) {
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
		playerButton[0] = findViewById(R.id.strokesButton_1);
		playerButton[1] = findViewById(R.id.strokesButton_2);
		playerButton[2] = findViewById(R.id.strokesButton_3);
		playerButton[3] = findViewById(R.id.strokesButton_4);
		
		previousButton = findViewById(R.id.strokes_previous);
		nextButton = findViewById(R.id.strokes_next);
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

	private void initButtons() {
		for (int i = 0; i < playerButton.length; i++) {
			setKey("0", playerButton[i].getId());
		}
	}

	/** Open the keypad if there are any valid moves */
	protected void showKeypad(int playerPush) {
		Log.d("My Golf Card", "showKeypad");
		Dialog v = new Keypad(this, playerPush);
		v.show();
	}
	
} 
