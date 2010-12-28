/**
 * Package: org.activities.mygolfcard
 * File: Matches.java
 * Description:
 * Create At: ---
 * Created By: ERL
 * Last Modifications:
 * 		20/10/2010 - ERL - POO
 */
package org.activities.mygolfcard;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.classes.mygolfcard.User;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

public class Matches extends ListActivity {
	private static final int DIALOG_DELETE_MATCHES 		= 1;
	private static final int DIALOG_DELETE_ALL_MATCHES	= 2;
	
	private static final int PROCESS_LOAD_MATCHES 		= 1;
	private static final int PROCESS_DEL_MATCHES		= 2;
	
	private org.classes.mygolfcard.Match matchesList[];
	private String auth_token;
	private User cUser = new User();
	private boolean connectionOK;
	
	private CheckBoxifiedTextListAdapter cbla;
	private int[] selectedMatches;
	int totalSelected = 0;
	int dialogUsed = 0;
	
	/** Called with the activity is first created. */
	@Override
	public void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		setContentView(R.layout.matches);
		
		connectionOK = Authentication.checkConnection(Matches.this);
		if (connectionOK) {
			Authentication.readDataUser(Matches.this);
			auth_token = Authentication.getToken();
			cUser.setUser_id(Authentication.getUserId());
		
			InitTask task = new InitTask(PROCESS_LOAD_MATCHES);
			task.execute();
		}
		else {
			Toast.makeText(Matches.this, R.string.no_internet,
                    Toast.LENGTH_SHORT).show();
			
			matchesList = org.classes.mygolfcard.Match.getMatchesFromLocal(Matches.this);
			loadList();
		}
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.menu_matches, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.synchro_delete:
				showDialog(DIALOG_DELETE_MATCHES);
				return true;
				
			case R.id.synchro_delete_all:
				showDialog(DIALOG_DELETE_ALL_MATCHES);
				return true;
				
		}
		return false;
	}    

	protected Dialog onCreateDialog(int id) {
		dialogUsed = id;
		
        switch (id) {
	        case DIALOG_DELETE_MATCHES:
				return new AlertDialog.Builder(Matches.this)
			        .setIcon(R.drawable.alert_dialog_icon)
			        .setTitle(R.string.alert_dialog_title_delete_matches)
			        .setMessage(R.string.alert_dialog_delete_matches)
			        .setPositiveButton(R.string.alert_dialog_confirm, new DialogInterface.OnClickListener() {
			            public void onClick(DialogInterface dialog, int whichButton) {
			            	getItemsChecked();
							deleteItems();
			            }
			        })
			        .setNegativeButton(R.string.alert_dialog_cancel, new DialogInterface.OnClickListener() {
			            public void onClick(DialogInterface dialog, int whichButton) {
			                /* User clicked Cancel so do some stuff */
			            }
			        })
			        .create();
				
	        case DIALOG_DELETE_ALL_MATCHES:
				return new AlertDialog.Builder(Matches.this)
			        .setIcon(R.drawable.alert_dialog_icon)
			        .setTitle(R.string.alert_dialog_title_delete_matches)
			        .setMessage(R.string.alert_dialog_delete_all_matches)
			        .setPositiveButton(R.string.alert_dialog_confirm, new DialogInterface.OnClickListener() {
			            public void onClick(DialogInterface dialog, int whichButton) {
			            	getAllItemsChecked();
							deleteItems();
			            }
			        })
			        .setNegativeButton(R.string.alert_dialog_cancel, new DialogInterface.OnClickListener() {
			            public void onClick(DialogInterface dialog, int whichButton) {
			                /* User clicked Cancel so do some stuff */
			            }
			        })
			        .create();
				
        }
        return null;
	}
		
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
	  // ignore orientation/keyboard change
	  super.onConfigurationChanged(newConfig);
	}

	public void onListItemClick(ListView parent, View v, int position,long id) {
		//
		if (connectionOK) {
			Intent intent = new Intent(this, Match.class);
			intent.putExtra("match_id", matchesList[position].getMatch_id());
	        startActivity(intent);
		}
		else {
			new AlertDialog.Builder(Matches.this)
				.setIcon(R.drawable.alert_dialog_icon)
				.setTitle(R.string.remote_connection)
				.setMessage(R.string.no_internet_connect)
				.setPositiveButton(R.string.alert_button_default, null)
				.show();
		}
	}
	
	public void loadList() {
		List<HashMap<String, String>> fillMaps = new ArrayList<HashMap<String, String>>();
		try {
			for (int i=0; i<matchesList.length; i++) {
				HashMap<String, String> map = new HashMap<String, String>();
				map.put("course_name", matchesList[i].getCourseName());
				map.put("date_hour", matchesList[i].getDateHour());
				fillMaps.add(map);
			}			
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		// Muestra la lista
		// SE QUITA POR EL ADAPTADOR CON CHECKBOX SimpleAdapter adapter = new SimpleAdapter(this, fillMaps, R.layout.main_item_two_line_row, new String[] { "course_name", "date_hour" }, new int[] { R.id.field1,  R.id.field2});
		
		cbla = new CheckBoxifiedTextListAdapter(this);
        for(int k=0; k<matchesList.length; k++)
        {
        	cbla.addItem(new CheckBoxifiedText(matchesList[k].getCourseName(), matchesList[k].getDateHour(), false));
        }  
		setListAdapter(cbla);
		
		//setListAdapter(adapter);
		getListView().setTextFilterEnabled(true);
	}

	private void getItemsChecked() {
		selectedMatches = new int[cbla.getCount()];
		
		totalSelected = 0;
		for (int i=0; i< cbla.getCount(); i++) {
			CheckBoxifiedText obj = (CheckBoxifiedText) cbla.getItem(i);
			
			if (obj.getChecked()) {
				selectedMatches[i] = matchesList[i].getMatch_id();
				totalSelected++;
			}
			else {
				selectedMatches[i] = 0;
			}
		}
	}
	
	private void getAllItemsChecked() {
		selectedMatches = new int[cbla.getCount()];
		
		totalSelected = 0;
		for (int i=0; i< cbla.getCount(); i++) {
			selectedMatches[i] = matchesList[i].getMatch_id();
			totalSelected++;
		}		
	}

	private void deleteItems() {
		dismissDialog(dialogUsed);
		
		InitTask task = new InitTask(PROCESS_DEL_MATCHES);
		task.execute(); 
		
	}
	
	private void reloadPage() {
		InitTask task = new InitTask(PROCESS_LOAD_MATCHES);
		task.execute();
	}
	
	/**
	 * sub-class of AsyncTask
	 */
	protected class InitTask extends AsyncTask<Context, Integer, String>
	{
		
		
		private ProgressDialog dialog;
		private int iProcess;
		
		public InitTask (int process) {
			iProcess = process;
		}
		
		// -- run intensive processes here
		// -- notice that the datatype of the first param in the class definition matches the param passed to this method 
		// -- and that the datatype of the last param in the class definition matches the return type of this mehtod
		@Override
		protected String doInBackground( Context... params ) 
		{
			if (iProcess == PROCESS_LOAD_MATCHES) {
				matchesList = org.classes.mygolfcard.Match.getMatchesFromRemote(auth_token,cUser.getUser_id(),Matches.this);
				return "";
			}
			else if (iProcess == PROCESS_DEL_MATCHES) {
				int value;
				
				for (int i=0; i<selectedMatches.length;i++) {
					
					if (selectedMatches[i] != 0)
					{
						Log.i("Info", "DELETING matches : " + selectedMatches[i]);
						org.classes.mygolfcard.Match.deleteRemoteMatch(selectedMatches[i], cUser.getUser_id(), auth_token);
						
						publishProgress(i+1);
					}
				}
				publishProgress(totalSelected);
				
				return "";
			}
			else {
				return "";
			}
		}

		// -- gets called just before thread begins
		@Override
		protected void onPreExecute() 
		{
			if (iProcess == PROCESS_LOAD_MATCHES) {
				Log.i( "makemachine", "onPreExecute()" );
				super.onPreExecute();
				CharSequence title_remote = getString(R.string.title_remote_connection);
				CharSequence remote = getString(R.string.remote_connection);
				this.dialog = ProgressDialog.show(Matches.this, title_remote, remote, true);
			}
			else if (iProcess == PROCESS_DEL_MATCHES) {
				Log.i( "makemachine", "onPreExecute()" );
				super.onPreExecute();
				
				this.dialog = new ProgressDialog(Matches.this);
				this.dialog.setIcon(R.drawable.info_dialog_icon_tra);
				this.dialog.setTitle("Borrando partidos en remoto");
				this.dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
				this.dialog.setMax(totalSelected);
				this.dialog.show();
			}
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
			if (iProcess == PROCESS_LOAD_MATCHES) {
				super.onPostExecute(result);
				Log.i( "makemachine", "onPostExecute(): " + result );
				this.dialog.cancel();
				loadList();
			}
			else {
				super.onPostExecute(result);
				Log.i( "makemachine", "onPostExecute(): " + result );
				this.dialog.cancel();
				reloadPage();
			}
		}
	}   
}
