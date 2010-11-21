/**
 * Package: org.activities.mygolfcard
 * File: Synchro.java
 * Description:
 * Create At: ---
 * Created By: ERL
 * Last Modifications:
 * 		05/11/2010 - ERL - POO
 */
package org.activities.mygolfcard;

import java.util.HashMap;
import java.util.List;

import org.activities.mygolfcard.RestClient.RequestMethod;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;

public class Synchro extends ListActivity {
	private static final int DIALOG_SYNCHRO_DELETE 		= 1;
    private static final int DIALOG_SYNCHRO_UPLOAD 		= 2;
    private static final int DIALOG_SYNCHRO_PROGRESS	= 3;
    private static final int DIALOG_SYNCHRO_DELETE_ALL	= 4;
    
    private static final int MATCH_PENDING_UPLOAD	= 0;
    private static final int MATCH_PROCESSING 		= 1;
    private static final int MATCH_UPLOADED			= 2;
    
	private String[] matches_field1;
	private String[] matches_field2;
	private String[] matches_field3;
	private String[] matches_field4;
	private String[] matches_field5;
	private String[] selectedMatches;
		
	private SQLiteDatabase db = null;
	private String DATABASE_NAME;
	private String match_id;
	private String course_name;
	private String course_id;
	private String date_hour;
	private String n_holes;
	private String player_id[][]; // = new String[4];
	private String tee_id[][]; // = new String[4];
	private int holes[][][];
	
	private String auth_user_id;
	
	private CheckBoxifiedTextListAdapter cbla;
	
	List<HashMap<String, String>> fillMaps;
	
	private String URL_UPLOAD;
	
	private static final int MAX_PROGRESS = 100;
    
    int totalSelected = 0;
    
    InitTask task ;

	/** Called with the activity is first created. */
	@Override
	public void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		setContentView(R.layout.synchro);
		
		DATABASE_NAME = getString(R.string.DB_NAME);
		URL_UPLOAD = getString(R.string.URL_APIS) + getString(R.string.ACTION_UPLOAD);
		
		Authentication.readDataUser(Synchro.this);
		auth_user_id = Authentication.getUserId();

		getMatches();
		setInfo();
		
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
	  // ignore orientation/keyboard change
	  super.onConfigurationChanged(newConfig);
	}

	public void onListItemClick(ListView parent, View v, int position,long id) {
		//
		Intent i = new Intent(this, Card.class);
		i.putExtra("match_id", Integer.parseInt(matches_field1[position]));
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
				
			case R.id.synchro_delete_all:
				showDialog(DIALOG_SYNCHRO_DELETE_ALL);
				return true;
				
			case R.id.synchro_upload:
				showDialog(DIALOG_SYNCHRO_UPLOAD);				
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
				
	        case DIALOG_SYNCHRO_DELETE_ALL:
				return new AlertDialog.Builder(Synchro.this)
			        .setIcon(R.drawable.alert_dialog_icon)
			        .setTitle(R.string.alert_dialog_title_delete_matches)
			        .setMessage(R.string.alert_dialog_delete_all_matches)
			        .setPositiveButton(R.string.alert_dialog_confirm, new DialogInterface.OnClickListener() {
			            public void onClick(DialogInterface dialog, int whichButton) {
							deleteAllItems();
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
	                    	processUploadingMatches();
	                    }
	                })
	                .setNegativeButton(R.string.alert_dialog_cancel, new DialogInterface.OnClickListener() {
	                    public void onClick(DialogInterface dialog, int whichButton) {
	                    	/* User clicked Cancel so do some stuff */
	                    }
	                })
	                .create();
	        	
	        case DIALOG_SYNCHRO_PROGRESS:
        	
        }
        return null;
	}
	
	private void processUploadingMatches()
	{
		dismissDialog(DIALOG_SYNCHRO_UPLOAD);

		//getItemsChecked(); 	//ERL 
		//uploadItems();		//ERL 
		
		task = new InitTask();
		task.execute(); 
    	
		//getMatches();
		//setInfo();
    	//finish();		
	}
	
	private void getMatches() {
		String sql;
		
		course_name = "";
		course_id	= "";
		match_id	= "";
		date_hour	= "";
		n_holes		= "";
		
		//fillMaps = new ArrayList<HashMap<String, String>>();
		
		try {
			db = this.openOrCreateDatabase(DATABASE_NAME, MODE_PRIVATE, null);
			sql = "select * from matches where status in (" + MATCH_PENDING_UPLOAD + "," + MATCH_PROCESSING + ") order by ID desc";
			
		 	Cursor c = db.rawQuery(sql, null);
		 	int colMatchId		= c.getColumnIndex("ID");
		    int colCourseName	= c.getColumnIndex("course_name");
		    int colCourseId		= c.getColumnIndex("course_id");
		    int colDateHour		= c.getColumnIndex("date_hour_match");
		    int colHoles		= c.getColumnIndex("holes");
		    int colPlayer1		= c.getColumnIndex("player1_id");
		    int colPlayer2		= c.getColumnIndex("player2_id");
		    int colPlayer3		= c.getColumnIndex("player3_id");
		    int colPlayer4		= c.getColumnIndex("player4_id");
		    int colTee1			= c.getColumnIndex("tee1");
		    int colTee2			= c.getColumnIndex("tee2");
		    int colTee3			= c.getColumnIndex("tee3");
		    int colTee4			= c.getColumnIndex("tee4");
		 	
		    c.moveToLast();
		    matches_field1 	= new String[c.getCount()];
		    matches_field2 	= new String[c.getCount()];
		    matches_field3 	= new String[c.getCount()];
		    matches_field4 	= new String[c.getCount()];
		    matches_field5 	= new String[c.getCount()];
		    holes			= new int[c.getCount()][4][19];
		    player_id		= new String[c.getCount()][4];
		    tee_id			= new String[c.getCount()][4];
		 	c.moveToFirst();
		 	
		 	if (c != null) {
		 		if (c.getCount() > 0) {
			 		int i = 0;
			 		do {
			 			course_name 	= c.getString(colCourseName);
			 			course_id		= c.getString(colCourseId);
			 			match_id 		= c.getString(colMatchId);
			 			date_hour		= c.getString(colDateHour);
			 			n_holes			= c.getString(colHoles);
			 			player_id[i][0]	= c.getString(colPlayer1);
			 			player_id[i][1]	= c.getString(colPlayer2);
			 			player_id[i][2]	= c.getString(colPlayer3);
			 			player_id[i][3]	= c.getString(colPlayer4);
			 			tee_id[i][0]	= c.getString(colTee1);
			 			tee_id[i][1]	= c.getString(colTee2);
			 			tee_id[i][2]	= c.getString(colTee3);
			 			tee_id[i][3]	= c.getString(colTee4);
			 			
			 			matches_field1[i]	= match_id;
			 			matches_field2[i]	= course_name;
			 			matches_field3[i] 	= date_hour;
			 			matches_field4[i] 	= course_id;
			 			matches_field5[i] 	= n_holes;
			 			
			 			// Saca los golpes para cada jugador del partido 'i'
			 			for (int j=0;j<player_id[i].length;j++) {
			 				if (!player_id[i][j].equals("0")) {
				 				sql = "select * from strokes where match_id=" + match_id +  " and player_id=" + player_id[i][j] + " order by hole";
					 			Cursor s = db.rawQuery(sql, null);
					 			
					 			if (s != null) {
					 				if (s.getCount() > 0) {
						 				s.moveToFirst();
						 				do {
						 					int number = s.getInt(s.getColumnIndex("hole"));
						 					holes[i][j][number] = s.getInt(s.getColumnIndex("strokes"));
						 				} while (s.moveToNext());
					 				}
					 			}
					 			s.close();
			 				}
			 			}
			 			
			 			++i;
			 							
			 		} while (c.moveToNext());
		 		}
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
		
		totalSelected = 0;
		for (int i=0; i< cbla.getCount(); i++) {
			CheckBoxifiedText obj = (CheckBoxifiedText) cbla.getItem(i);
			
			if (obj.getChecked()) {
				selectedMatches[i] = matches_field1[i];
				totalSelected++;
			}
			else {
				selectedMatches[i] = "0";
			}
		}
		
	}
	
	private void deleteItems() {
		String sql = "";
		String where = "";
		
		
		for (int i=0; i<selectedMatches.length;i++) {			
			if (!selectedMatches[i].equals("0"))
			{
				where += selectedMatches[i] + ",";
			}
		}
		where += "0";
		
		sql = "delete from matches where ID in (";
		sql += where + ")";
		
		try {
			Log.i("Info", "DELETING matches : " + sql);
			db = this.openOrCreateDatabase(DATABASE_NAME, MODE_PRIVATE, null);
			
			// Limpieza para mantener la integridad referencial entre matches y strokes
			sql = "delete from strokes where not match_id in (select id from matches)";
			db.execSQL(sql);
			
			sql = "delete from strokes where match_id in (";
			sql += where + ")";
		 	db.execSQL(sql);
		 	
			sql = "delete from matches where id in (";
			sql += where + ")";
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
	
	private void deleteAllItems() {
		String sql = "";

		try {
			Log.i("Info", "DELETING ALL matches : " + sql);
			db = this.openOrCreateDatabase(DATABASE_NAME, MODE_PRIVATE, null);

			sql = "delete from matches";
			db.execSQL(sql);
			
			// Limpieza para mantener la integridad referencial entre matches y strokes
			sql = "delete from strokes";
			db.execSQL(sql);
			
		}
		catch(Exception e) {
    		Log.e("Error", "Error DELETING ALL matches", e);
    	} 
    	finally {
    		if (db != null)
    			db.close();
    	}
	}
	
	private void uploadItems() {
		String sql = "";
		String file = "";
		int processed = 1;
		int value;
		
		for (int i=0; i<selectedMatches.length;i++) {			
			if (!selectedMatches[i].equals("0"))
			{
				try {
					db = this.openOrCreateDatabase(DATABASE_NAME, MODE_PRIVATE, null);
					
					// Marcamos el partido que estamos tratando como "en proceso" 
					sql = "update matches set status=" + MATCH_PROCESSING + " where ID=" + selectedMatches[i] + "";
					db.execSQL(sql);
					
					//ContentValues cv = new ContentValues();
					//cv.put("status", 1);
					//db.update("matches", cv, "ID=?", new String[] {selectedMatches[i]});
					
					Log.i("Info", "UPLOADING matches : " + sql);
					file = createFile(selectedMatches[i],i);
					uploadFile(file);
					
					// Marcamos el partido que estamos tratando como "procesado" 
					sql = "update matches set status=" + MATCH_UPLOADED + " where ID=" + selectedMatches[i] + "";
					db.execSQL(sql);
					
					db.close();
					
					++processed;
					value = getValueProgress(processed);
					
					
					task.setProgress(value);
				}
				catch(Exception e) {
		    		Log.e("Error", "Error UPLOADING matches", e);

		    		if (db != null) {
			    		// Marcamos el partido que estamos tratando como "pendiente" para volverlo a tratar en otra ocasión 
						sql = "update matches set status=" + MATCH_PENDING_UPLOAD + " where ID=" + selectedMatches[i] + "";
						db.execSQL(sql);
		    		}
		    	} 
		    	finally {
		    		if (db != null)
		    			db.close();
		    	}
			}
		}
	}
	
	private String createFile(String match_id, int index) {
		String fileName = "match_0001_00002.xml";
		
		String header 	= "";
		String match 	= "";
		String players 	= "";
		String footer 	= "";
		String xmlFile 	= "";

		String m 	= String.format("%04d", Integer.parseInt(match_id));
		String u 	= String.format("%05d", Integer.parseInt(auth_user_id));
		fileName	= "match_" + m + "_" + u + ".xml";
		
		header 		= getXMLHeader(fileName,"");
		match		= getXMLMatch(match_id,matches_field4[index],matches_field3[index],matches_field5[index]);
		players		= getXMLPlayers(player_id[index],tee_id[index],holes[index]);
		footer		= getXMLFooter();
		
		xmlFile = header + match + players + footer;
		Authentication.saveFile(Synchro.this,fileName,xmlFile);
		
		return fileName;
	}
	
	private void uploadFile(String fileName) {
		String response = "";
		String dataFile;
    	
		dataFile = Authentication.readFile(Synchro.this, fileName);

	    RestClient client = new RestClient(URL_UPLOAD);
	    client.AddParam("filename", fileName);
	    client.AddParam("data", dataFile);
	    
	    response = "";
	    try {
	        client.Execute(RequestMethod.POST);
	        response = client.getResponse();
	    } catch (Exception e) {
	        e.printStackTrace();
	    }	    
	}

	private String getXMLHeader(String fileName, String createdAt) {
		String res;
		
		res = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<!-- \nDocument   : ##filename##\nCreated on : ##created##\n\nDescription: Document generated by android application to upload a new match to web application\n-->";
		
		res = res.replace("##filename##", fileName);
		res = res.replace("##created##", createdAt);
		
		return res;
	}
	
	private String getXMLFooter() {
		String res;
		
		res = "</match>";
		
		return res;
	}
	
	private String getXMLMatch(String match_id, String course_id, String dateHour, String nHoles) {
		String res;
		
		res = "<match id=\"##match_id##\">\n<course_id>##course_id##</course_id>\n<date_hour>##datehour##</date_hour><holes>##holes##</holes>";
		res = res.replace("##match_id##", match_id);
		res = res.replace("##course_id##", course_id);
		res = res.replace("##datehour##", dateHour);
		res = res.replace("##holes##", nHoles);
		
		return res;
	}
	
	private String getXMLPlayers(String players[], String tee_id[], int holes[][]) {
		String res;
		
		res = "<players>\n";
		for (int i=0; i<players.length; i++) {
			if (players[i]!="0") {
				res += "<player user_id=\"" + players[i] + "\" tee_id=\"" + tee_id[i] + "\">";
				for (int j=1; j<holes[i].length;j++) {
					res += "<hole number=\"" + j + "\">" + holes[i][j] + "</hole>";
				}
				res += "</player>";
			}
		}
		res += "</players>\n";
		
		return res;
	}
	
	private int getValueProgress(int processed) {
		return (int)((double)processed/(double)(totalSelected+1)*(double)MAX_PROGRESS);
	}
	
	/**
	 * sub-class of AsyncTask
	 */
	protected class InitTask extends AsyncTask<Context, Integer, String>
	{
		private ProgressDialog dialog;
		
		public InitTask () {
			
		}
		
		// -- run intensive processes here
		// -- notice that the datatype of the first param in the class definition matches the param passed to this method 
		// -- and that the datatype of the last param in the class definition matches the return type of this mehtod
		@Override
		protected String doInBackground( Context... params ) 
		{
			// Muestra el progress bar
			// Dentro de el hay un hilo que se encarga de generar y subir cada uno de los ... 
			// ... ficheros al mismo tiempo que actualiza la progress bar
			int value;
			
			getItemsChecked(); // Recupera los partidos seleccionados
			
			value = getValueProgress(1);
			publishProgress(value);

			uploadItems();
			publishProgress(MAX_PROGRESS);
			
			return "";
		}
		
		public void setProgress(int value) {
			publishProgress(value);
		}

		// -- gets called just before thread begins
		@Override
		protected void onPreExecute() 
		{
			
			Log.i( "makemachine", "onPreExecute()" );
			super.onPreExecute();
			
			this.dialog = new ProgressDialog(Synchro.this);
			this.dialog.setIcon(R.drawable.info_dialog_icon_tra);
			this.dialog.setTitle("Sincronizando partidos pendientes");
			this.dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
			this.dialog.setMax(MAX_PROGRESS);
			this.dialog.show();
		}

		// -- called from the publish progress 
		// -- notice that the datatype of the second param gets passed to this method
		@Override
		protected void onProgressUpdate(Integer... values) 
		{
			int value = 0;
			
			value = values[0];
			
			super.onProgressUpdate(values);
			Log.i( "makemachine", "onProgressUpdate(): " +  String.valueOf( value ) );
			
			this.dialog.setProgress(value);
		}

		// -- called as soon as doInBackground method completes
		// -- notice that the third param gets passed to this method
		@Override
		protected void onPostExecute( String result ) 
		{
			super.onPostExecute(result);
			Log.i( "makemachine", "onPostExecute(): " + result );
			this.dialog.cancel();

			getMatches();
			setInfo();
		}
	}   
	
}
