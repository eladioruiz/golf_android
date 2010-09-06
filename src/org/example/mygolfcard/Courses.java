package org.example.mygolfcard;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.ArrayAdapter;

public class Courses extends ListActivity {
	String[] courses = {"RACE", "Oliva Nova", "La Duquesa"};
	
	/** Called with the activity is first created. */
	@Override
	public void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		setContentView(R.layout.courses);
		setListAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, courses));
	}

	public void onListItemClick(ListView parent, View v, int position,long id) {
		//
	}
	
}
