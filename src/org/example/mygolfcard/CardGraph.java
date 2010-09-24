package org.example.mygolfcard;

import android.app.Activity;
import android.os.Bundle;

public class CardGraph extends Activity {
	private CardGraphView cardGraphView;

	private String match_id;
	private int mitad;
	private int typeMatch;	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		match_id	= getIntent().getCharSequenceExtra("match_id").toString(); 
		mitad		= getIntent().getIntExtra("mitad", 1);
		typeMatch	= getIntent().getIntExtra("type_match", 1);
		
		cardGraphView = new CardGraphView(this,Integer.parseInt(match_id),mitad,typeMatch);
		/*cardGraphView.setMatchId(match_id);
		cardGraphView.setTypeMatch(typeMatch);
		cardGraphView.setMitad(mitad);*/
		
		setContentView(cardGraphView);
		cardGraphView.requestFocus();
	}
}
