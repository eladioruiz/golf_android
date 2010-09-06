package org.example.mygolfcard;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.ArrayAdapter;

public class Matches extends ListActivity {
	String[] matches={"RACE  12/08/2010", "RACE  05/07/2010", "Oliva Nova  14/06/2010"};
	
	/** Called with the activity is first created. */
	@Override
	public void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		setContentView(R.layout.matches);
		setListAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, matches));
	}

	public void onListItemClick(ListView parent, View v, int position,long id) {
		//
	}
	
}
