package org.example.mygolfcard;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.TextView;
import android.widget.Spinner;
import android.widget.ArrayAdapter;
import android.text.TextWatcher;
import android.text.Editable;

public class NewMatch extends Activity implements TextWatcher, AdapterView.OnItemSelectedListener, OnClickListener {
	TextView selection;
	AutoCompleteTextView newmatch_course;
	AutoCompleteTextView newmatch_player_1;
	AutoCompleteTextView newmatch_player_2;
	AutoCompleteTextView newmatch_player_3;
	AutoCompleteTextView newmatch_player_4;
	Spinner newmatch_tee_1;
	Spinner newmatch_tee_2;
	Spinner newmatch_tee_3;
	Spinner newmatch_tee_4;
	String[] courses={"RACE", "Oliva Nova", "La Duquesa"};
	String[] players={"Eladio", "Bea", "Juan", "Merche"};
	String[] tees={"Amarillas", "Rojas", "Blancas"};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.newmatch);
		
		ArrayAdapter<CharSequence> aa = new ArrayAdapter<CharSequence>(this, R.layout.spinnerview, tees);
		aa.setDropDownViewResource(R.layout.spinnerviewdropdown);

		newmatch_tee_1 = (Spinner)findViewById(R.id.newmatch_tee_1);
		newmatch_tee_1.setOnItemSelectedListener(this);
		newmatch_tee_1.setAdapter(aa);
		
		newmatch_tee_2 = (Spinner)findViewById(R.id.newmatch_tee_2);
		newmatch_tee_2.setOnItemSelectedListener(this);
		newmatch_tee_2.setAdapter(aa);
		
		newmatch_tee_3 = (Spinner)findViewById(R.id.newmatch_tee_3);
		newmatch_tee_3.setOnItemSelectedListener(this);
		newmatch_tee_3.setAdapter(aa);
		
		newmatch_tee_4 = (Spinner)findViewById(R.id.newmatch_tee_4);
		newmatch_tee_4.setOnItemSelectedListener(this);
		newmatch_tee_4.setAdapter(aa);
		
        newmatch_course=(AutoCompleteTextView)findViewById(R.id.newmatch_course);
        newmatch_course.addTextChangedListener(this);
        newmatch_course.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, courses));
        
        newmatch_player_1=(AutoCompleteTextView)findViewById(R.id.newmatch_player_1);
        newmatch_player_1.addTextChangedListener(this);
        newmatch_player_1.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, players));
        
        newmatch_player_2=(AutoCompleteTextView)findViewById(R.id.newmatch_player_2);
        newmatch_player_2.addTextChangedListener(this);
        newmatch_player_2.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, players));
        
        newmatch_player_3=(AutoCompleteTextView)findViewById(R.id.newmatch_player_3);
        newmatch_player_3.addTextChangedListener(this);
        newmatch_player_3.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, players));
        
        newmatch_player_4=(AutoCompleteTextView)findViewById(R.id.newmatch_player_4);
        newmatch_player_4.addTextChangedListener(this);
        newmatch_player_4.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, players));
        
		// Set up click listeners for all the buttons
		View okButton = findViewById(R.id.newmatch_ok);
		okButton.setOnClickListener(this);
		
		// Set up click listeners for all the buttons
		View cancelButton = findViewById(R.id.newmatch_cancel);
		cancelButton.setOnClickListener(this);

	}

    public void onTextChanged(CharSequence s, int start, int before, int count) {
    	// needed for interface, but not used
    }
    
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
    	// needed for interface, but not used
    }
    
    public void afterTextChanged(Editable e) {
    	// needed for interface, but not used
    }

    
    public void onItemSelected(AdapterView parent, View v, int position, long id) {
    
    }
    
    public void onNothingSelected(AdapterView parent) {
    		
    }
    
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.newmatch_cancel:
				finish();
				break;
			
			case R.id.newmatch_ok:
				Log.d("My Golf Card", "NewMatch");
				Intent i = new Intent(this, Card.class);
				startActivity(i);
				break;
		}
	}

    
}
