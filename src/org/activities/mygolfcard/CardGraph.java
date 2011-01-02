package org.activities.mygolfcard;

import org.classes.mygolfcard.Authentication;
import org.classes.mygolfcard.User;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

public class CardGraph extends Activity {
	private CardGraphView cardGraphView;

	private int match_id;
	private int mitad;
	private int typeMatch;
		
	private String auth_token;
	
	private View logoutButton;
	private TextView titleNameText;
	
	private User cUser = new User();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		match_id	= getIntent().getIntExtra("match_id",0); 
		mitad		= getIntent().getIntExtra("mitad", 1);
		typeMatch	= getIntent().getIntExtra("type_match", 1);
		
		cardGraphView = new CardGraphView(this,match_id,mitad,typeMatch);
		/*cardGraphView.setMatchId(match_id);
		cardGraphView.setTypeMatch(typeMatch);
		cardGraphView.setMitad(mitad);*/
		
		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		setContentView(cardGraphView);
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.custom_title_2);
		cardGraphView.requestFocus();
		
		titleNameText	= (TextView) findViewById(R.id.titleName);
		logoutButton 	= findViewById(R.id.logout);
		
		Authentication.readDataUser(CardGraph.this);
		auth_token = Authentication.getToken();
		cUser.setUser_id(Authentication.getUserId());
		cUser.setUserName(Authentication.getUserName());
		
		titleNameText.setText(cUser.getUserName()); 
	}
}
