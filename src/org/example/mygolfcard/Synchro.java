package org.example.mygolfcard;

import java.util.HashMap;
import java.util.List;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;

public class Synchro extends ListActivity {
	private static final int DIALOG_SYNCHRO_DELETE = 1;
    private static final int DIALOG_SYNCHRO_UPLOAD = 2;
    
	private String[] matches_field1;
	private String[] matches_field2;
	private String[] matches_field3;
	private String[] selectedMatches;
		
	private SQLiteDatabase db = null;
	private String DATABASE_NAME = "mygolfcard";
	private String match_id;
	private String course_name;
	private String date_hour;
	private String n_holes;
	private String player_id[] = new String[4];
	
	private CheckBoxifiedTextListAdapter cbla;
	
	List<HashMap<String, String>> fillMaps;
	
	/** Called with the activity is first created. */
	@Override
	public void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		setContentView(R.layout.synchro);
		
		getMatches();
		setInfo();
	}

	public void onListItemClick(ListView parent, View v, int position,long id) {
		//
		Intent i = new Intent(this, Card.class);
		i.putExtra("match_id", matches_field1[position]);
		startActivity(i);
		finish();
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.menu_synchro, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.synchro_delete:
				showDialog(DIALOG_SYNCHRO_DELETE);
				return true;
				
			case R.id.synchro_upload:
				showDialog(DIALOG_SYNCHRO_UPLOAD);
				return true;
				
			case R.id.menuapp:
				startActivity(new Intent(this, MenuApp.class));
				finish();
				return true;
		}
		return false;
	}    
	
	protected Dialog onCreateDialog(int id) {
        switch (id) {
	        case DIALOG_SYNCHRO_DELETE:
				return new AlertDialog.Builder(Synchro.this)
			        .setIcon(R.drawable.alert_dialog_icon)
			        .setTitle(R.string.alert_dialog_title_delete_matches)
			        .setMessage(R.string.alert_dialog_delete_matches)
			        .setPositiveButton(R.string.alert_dialog_confirm, new DialogInterface.OnClickListener() {
			            public void onClick(DialogInterface dialog, int whichButton) {
			            	getItemsChecked();
							deleteItems();
							finish();
			            }
			        })
			        .setNegativeButton(R.string.alert_dialog_cancel, new DialogInterface.OnClickListener() {
			            public void onClick(DialogInterface dialog, int whichButton) {
			                /* User clicked Cancel so do some stuff */
			            }
			        })
			        .create();
	        case DIALOG_SYNCHRO_UPLOAD:
	        	return new AlertDialog.Builder(Synchro.this)
	                .setIcon(R.drawable.alert_dialog_icon)
	                .setTitle(R.string.alert_dialog_title_upload_matches)
	                .setMessage(R.string.alert_dialog_upload_matches)
	                .setPositiveButton(R.string.alert_dialog_confirm, new DialogInterface.OnClickListener() {
	                    public void onClick(DialogInterface dialog, int whichButton) {
	                    	//        				
	                    }
	                })
	                /*.setNegativeButton(R.string.alert_dialog_cancel, new DialogInterface.OnClickListener() {
	                    public void onClick(DialogInterface dialog, int whichButton) {
	                        
	                    }
	                })*/
	                .create();
        }
        return null;
	}
	
	private void getMatches() {
		String sql;
		
		course_name = "";
		match_id	= "";
		date_hour	= "";
		
		//fillMaps = new ArrayList<HashMap<String, String>>();
		
		try {
			db = this.openOrCreateDatabase(DATABASE_NAME, MODE_PRIVATE, null);
			sql = "select * from matches where status=0 order by ID desc;";
			
		 	Cursor c = db.rawQuery(sql, null);
		 	int colMatchId		= c.getColumnIndex("ID");
		    int colCourseName	= c.getColumnIndex("course_name");
		    int colDateHour		= c.getColumnIndex("date_hour_match");
		    int colHoles		= c.getColumnIndex("holes");
		    int colPlayer1		= c.getColumnIndex("player1_id");
		    int colPlayer2		= c.getColumnIndex("player2_id");
		    int colPlayer3		= c.getColumnIndex("player3_id");
		    int colPlayer4		= c.getColumnIndex("player4_id");
		 	
		    c.moveToLast();
		    matches_field1 = new String[c.getCount()];
		    matches_field2 = new String[c.getCount()];
		    matches_field3 = new String[c.getCount()];
		 	c.moveToFirst();
		 	
		 	if (c != null) {
		 		int i = 0;
		 		do {
		 			course_name 	= c.getString(colCourseName);
		 			match_id 		= c.getString(colMatchId);
		 			date_hour		= c.getString(colDateHour);
		 			n_holes			= c.getString(colHoles);
		 			player_id[0]	= c.getString(colPlayer1);
		 			player_id[1]	= c.getString(colPlayer2);
		 			player_id[2]	= c.getString(colPlayer3);
		 			player_id[3]	= c.getString(colPlayer4);
		 			
		 			matches_field1[i]	= match_id;
		 			matches_field2[i]	= course_name;
		 			matches_field3[i] 	= date_hour;
		 			
		 			++i;
		 			
		 			//HashMap<String, String> map = new HashMap<String, String>();
					//map.put("course_name", matches_field2[i]);
					//map.put("date_hour", matches_field3[i]);
					//fillMaps.add(map);
					
		 		} while (c.moveToNext());
		 	}
		 	
		 	c.close();
		}
		catch(Exception e) {
    		Log.e("Error", "Error reading DB", e);
    	} 
    	finally {
    		if (db != null)
    			db.close();
    	}
		
	}
	
	private void setInfo() {
		// Muestra la lista
		//SimpleAdapter adapter = new SimpleAdapter(this, fillMaps, R.layout.main_item_two_line_row_multiple_choice, new String[] { "course_name", "date_hour" }, new int[] { R.id.field1,  R.id.field2});
		
		//setListAdapter(adapter);
		//getListView().setTextFilterEnabled(true);
		
		cbla = new CheckBoxifiedTextListAdapter(this);
        for(int k=0; k<matches_field1.length; k++)
        {
        	cbla.addItem(new CheckBoxifiedText(matches_field2[k], matches_field3[k], false));
        }  
        // Display it
        setListAdapter(cbla);
	}

	private void getItemsChecked() {
		selectedMatches = new String[cbla.getCount()];
		
		for (int i=0; i< cbla.getCount(); i++) {
			CheckBoxifiedText obj = (CheckBoxifiedText) cbla.getItem(i);
			
			if (obj.getChecked()) {
				selectedMatches[i] = matches_field1[i];
			}
			else {
				selectedMatches[i] = "0";
			}
		}
		
/*		Toast.makeText(Synchro.this, res,
                Toast.LENGTH_SHORT).show();
*/	}
	
	private void deleteItems() {
		String sql = "";
		
		sql = "delete from matches where ID in (";
		for (int i=0; i<selectedMatches.length;i++) {			
			if (!selectedMatches[i].equals("0"))
			{
				sql += selectedMatches[i] + ",";
			}
		}
		sql += "0);";
		
		try {
			Log.e("Info", "DELETING matches : " + sql);
			db = this.openOrCreateDatabase(DATABASE_NAME, MODE_PRIVATE, null);
		 	db.execSQL(sql);
		}
		catch(Exception e) {
    		Log.e("Error", "Error DELETING matches", e);
    	} 
    	finally {
    		if (db != null)
    			db.close();
    	}
	}
}
