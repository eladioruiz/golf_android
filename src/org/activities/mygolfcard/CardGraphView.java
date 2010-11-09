package org.activities.mygolfcard;

import org.activities.mygolfcard.RestClient.RequestMethod;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.FontMetrics;
import android.graphics.Paint.Style;
import android.graphics.Rect;
import android.os.AsyncTask;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

public class CardGraphView extends View {
	private static final String TAG = "MyGolfCard" ;
	private Context ctxCardGraph;
	
	private String auth_token;
	private String auth_user_id;
	private boolean connectionOK;
	private String URL_MATCH;
	private String URL_STROKES;	
	private String result_getmatch;
	private String result_getstrokes;
	
	private float width; // width of one tile
	private float height; // height of one tile
	private int selX; // X index of selection
	private int selY; // Y index of selection
	private final Rect selRect = new Rect();
	
	private static int COLS= 16;
	private static int ROWS= 10;
	
	private static int FIRST_COL= 5;
	private static int FIRST_ROW= 3;
	private static int PARTIAL_COL= 5;
	private static int TOTAL_COL= 6;
	private static int TITLE_COL= 5;

	private static final int TYPEMATCH_INTERNAL 	= 1;
	private static final int TYPEMATCH_REMOTE		= 2;
	
	private int match_id;
	private int mitad;
	private int typeMatch;

	private SQLiteDatabase db = null;
	private String DATABASE_NAME = "";
	
	private int colPlayer[] = new int[4];
	private int colCourseName;
	private int colDateHour;
	private int colnHoles;

	private String course_name = "-- CAMPO --";
	private String date_hour_match = "-- FECHA/HORA --";
	private int aux_player_id[] = new int[4]; 
	private int aux_playerweb_id[] = new int[4];
	private int arrStrokes[][] = new int[4][19];

	private String[] players_field1;
	private String[] players_field2;
	

	public CardGraphView(Context context, int match_id, int mitad, int type_match) {
		super(context);

		String result = Authentication.readFriends(context);
		setInfoPlayers(result);
		
		DATABASE_NAME = context.getString(R.string.DB_NAME);

		ctxCardGraph = context;

		this.match_id = match_id;
		this.mitad = mitad;
		this.typeMatch = type_match;
		
		getInfoCard();
		
		setFocusable(true);
		setFocusableInTouchMode(true);
	}
	
	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		width = w / (float)COLS;
		height = h / (float)ROWS;
		getRect(selX, selY, selRect);
		Log.d(TAG, "onSizeChanged: width " + width + ", height " + height);
		super.onSizeChanged(w, h, oldw, oldh);
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		int i;
		int j;
		
		Log.d(TAG, "onDraw");
		
		//getInfoCard();
		
		// Draw the background...
		Paint background = new Paint();
		background.setColor(getResources().getColor(android.R.color.white));
		canvas.drawRect(0, 0, getWidth(), getHeight(), background);
		
		// Draw the board...
		// Define colors for the grid lines
		Paint dark = new Paint();
		dark.setColor(getResources().getColor(R.color.card_dark));
		
		Paint hilite = new Paint();
		hilite.setColor(getResources().getColor(R.color.card_hilite));
		
		Paint light = new Paint();
		light.setColor(getResources().getColor(R.color.card_light));
		
		// Draw the minor vertical grid lines
		for (i = FIRST_COL; i < COLS; i++) {
			canvas.drawLine(i * width, 3 * height, i * width, getHeight(), light);  // lineas vert
			canvas.drawLine(i * width + 1, 3 * height, i * width + 1, getHeight(), hilite);  // lineas vert
		}
		
		// Draw the minor horizontal grid lines
		for (i = FIRST_ROW; i < ROWS; i++) {
			canvas.drawLine(0, i * height, getWidth(), i * height, light); // lineas hoz
			canvas.drawLine(0, i * height + 1, getWidth(), i * height + 1, hilite); // lineas hoz
		} 
		
		// Draw the major vertical grid lines
		i = 5;
		canvas.drawLine(i * width, 0, i * width, getHeight(), dark);
		canvas.drawLine(i * width + 1, 0, i * width + 1, getHeight(), hilite);
		
		i = 14;
		canvas.drawLine(i * width, 3 * height, i * width, getHeight(), dark);
		canvas.drawLine(i * width + 1, 3 * height, i * width + 1, getHeight(), hilite);
		
		i = 15;
		canvas.drawLine(i * width, 3 * height, i * width, getHeight(), dark);
		
		// Draw the major horizontal grid lines
		i = 3;
		canvas.drawLine(0, i * height, getWidth(), i * height, dark);
		canvas.drawLine(0, i * height + 1, getWidth(), i * height + 1, hilite);
		
		i = 6;
		canvas.drawLine(0, i * height, getWidth(), i * height, dark);
		canvas.drawLine(0, i * height + 1, getWidth(), i * height + 1, hilite);
		
		
		
		// Draw the numbers...
		// Define color and style for numbers
		/////////////////////////////////////////////////////////////////////////
		Paint players = new Paint(Paint.ANTI_ALIAS_FLAG);
		players.setColor(getResources().getColor(R.color.card_players));
		players.setStyle(Style.FILL);
		players.setTextSize(height * 0.55f);
		players.setTextScaleX(width / height);
		players.setTextAlign(Paint.Align.LEFT);
		
		Paint strokes = new Paint(Paint.ANTI_ALIAS_FLAG);
		strokes.setColor(getResources().getColor(R.color.card_players));
		strokes.setStyle(Style.FILL);
		strokes.setTextSize(height * 0.55f);
		strokes.setTextScaleX(width / height);
		strokes.setTextAlign(Paint.Align.CENTER);
		
		Paint players_total = new Paint(Paint.ANTI_ALIAS_FLAG);
		players_total.setColor(getResources().getColor(R.color.card_players));
		players_total.setStyle(Style.FILL);
		players_total.setTextSize(height * 0.60f);
		players_total.setTextScaleX(width / height);
		players_total.setTextAlign(Paint.Align.RIGHT);
		
		// Draw the number in the center of the tile
		FontMetrics fm_p = players.getFontMetrics();

		// Centering in X: use alignment (and X at midpoint)
		float x = width / 2;
		
		// Centering in Y: measure ascent/descent first
		float y = height / 2 - (fm_p.ascent + fm_p.descent) / 2;
		
		canvas.drawCircle((float)(2.5 * width), (float)(1.5 * height), (float)(1 * width), dark);
		canvas.drawText(mitad==1 ? "2P" : "1P", (float)(1.75 * width + x), (float)(1 * height + y), players);
		
		canvas.drawText(course_name, (TITLE_COL * width + x), (float)(0.75 * height + y), players);
		canvas.drawText(date_hour_match, (TITLE_COL * width + x), (float)(1.5 * height + y), players);
		
		/////////////////////////////////////////////////////////////////////////
		Paint nHoles = new Paint(Paint.ANTI_ALIAS_FLAG);
		nHoles.setColor(getResources().getColor(R.color.card_foreground));
		nHoles.setStyle(Style.FILL);
		nHoles.setTextSize(height * 0.75f);
		nHoles.setTextScaleX(width / height);
		nHoles.setTextAlign(Paint.Align.CENTER);
		// Draw the number in the center of the tile
		FontMetrics fm_i = nHoles.getFontMetrics();
		
		// Centering in X: use alignment (and X at midpoint)
		x = width / 2;
		
		// Centering in Y: measure ascent/descent first
		y = height / 2 - (fm_i.ascent + fm_i.descent) / 2;
		
		// Drawing info holes
		j= 3;
		
		for (i=5;i<=13;i++) {
			int holeNumber = i - 4 + ((mitad-1) * 9);
			canvas.drawText("" + holeNumber,  (i * width) + x, (j * height) + y, nHoles);
		}

		i = 4;
		nHoles.setTextAlign(Paint.Align.RIGHT);
		canvas.drawText("Nº HOYO",  (i * width) + x, (j * height) + y, nHoles);
		

		/////////////////////////////////////////////////////////////////////////
		Paint infoHoles = new Paint(Paint.ANTI_ALIAS_FLAG);
		infoHoles.setColor(getResources().getColor(R.color.card_dark));
		infoHoles.setStyle(Style.FILL);
		infoHoles.setTextSize(height * 0.55f);
		infoHoles.setTextScaleX(width / height);
		infoHoles.setTextAlign(Paint.Align.CENTER);
		// Draw the number in the center of the tile
		FontMetrics fm_h = infoHoles.getFontMetrics();
		
		// Centering in X: use alignment (and X at midpoint)
		x = width / 2;
		
		// Centering in Y: measure ascent/descent first
		y = height / 2 - (fm_h.ascent + fm_h.descent) / 2;
		
		// Drawing info holes PAR
		j = 4;
		for (i=5;i<=13;i++) {
			int holeNumber = i - 4 + ((mitad-1) * 9);
			canvas.drawText("" + holeNumber,  (i * width) + x, (j * height) + y, infoHoles);
		}

		// Drawing info holes HCP
		j = 5;
		for (i=5;i<=13;i++) {
			int holeNumber = i - 4 + ((mitad-1) * 9);
			canvas.drawText("" + holeNumber,  (i * width) + x, (j * height) + y, infoHoles);
		}

		i = 4;
		j = 4;
		infoHoles.setTextAlign(Paint.Align.RIGHT);
		canvas.drawText("PAR",  (i * width) + x, (j * height) + y, infoHoles);
		j = 5;
		canvas.drawText("HCP",  (i * width) + x, (j * height) + y, infoHoles);

		i = 14;
		infoHoles.setTextAlign(Paint.Align.CENTER);
		infoHoles.setColor(getResources().getColor(android.R.color.black));
		canvas.drawText("P",  (i * width) + x, (j * height) + y, infoHoles);
		
		i = 15;
		infoHoles.setTextAlign(Paint.Align.CENTER);
		infoHoles.setColor(getResources().getColor(android.R.color.black));
		canvas.drawText("T",  (i * width) + x, (j * height) + y, infoHoles);
		
		// INFO MATCH
		// Drawing Players
		i = 0;
		j = 6;
		int sum_mitad = 0;
		int sum_total = 0;
		for (int iPlayer=0;iPlayer<aux_player_id.length;iPlayer++) {
			i = 0;
			sum_mitad = 0;
			sum_total = 0;
			
			canvas.drawText(getPlayerName(""+ aux_player_id[iPlayer]),  (i * width) + 1, (j * height) + y, players);
			++i;
			
			for (int z=1 + ((mitad-1) * 9);z<10 + ((mitad-1) * 9);z++) {
				sum_mitad += arrStrokes[iPlayer][z];
				canvas.drawText("" + arrStrokes[iPlayer][z], ((i+4) * width) + x, (j* height) + y, strokes);
				++i;
			}
			
			for (int z=1;z<19;z++) {
				sum_total += arrStrokes[iPlayer][z];
			}
			canvas.drawText("" + sum_mitad, ((i + PARTIAL_COL) * width) , (j* height) + y, players_total);
			canvas.drawText("" + sum_total, ((i + TOTAL_COL) * width) , (j* height) + y, players_total);
			
			++j;
		}
				
		for (j=0;j<4;j++) {
						
		}
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (event.getAction() != MotionEvent.ACTION_DOWN)
			return super.onTouchEvent(event);
		
		select((int) (event.getX() / width), (int) (event.getY() / height));
		Log.d(TAG, "onTouchEvent: x " + selX + ", y " + selY);
		
		changeMitad(selX,selY);
		
		return true;
	}
	
	private void getRect(int x, int y, Rect rect) {
		rect.set((int) (x * width), (int) (y * height), (int) (x * width + width), (int) (y * height + height));
	}
	
	private void select(int x, int y) {
		invalidate(selRect);
		selX = Math.min(Math.max(x, 0), 8);
		selY = Math.min(Math.max(y, 0), 8);
		getRect(selX, selY, selRect);
		invalidate(selRect);
	}

	public void setMatchId(int id) {
		match_id = id;
	}
	
	public void setMitad(int id) {
		mitad = id;
	}
	
	public void setTypeMatch(int id) {
		typeMatch = id;
	}
	
	private void getInfoCard() {
		Log.d(TAG, "getInfoCard:" + typeMatch);
	
		for (int j=0;j<4;j++) {
			for (int i=0;i<19;i++) {
				arrStrokes[j][i] = 0;
			}
		}
		
		switch (typeMatch) {
			case TYPEMATCH_INTERNAL : // TYPEMATCH_INTERNAL:
				getInfoCardInternal();
				break;
			case TYPEMATCH_REMOTE : // TYPEMATCH_REMOTE:
				getInfoCardRemote();
				break;
		}
	}
	
	private void getInfoCardInternal()
	{
		String sql = "";
		int i = 0;
		
		sql = "SELECT course_name, date_hour_match, holes, player1_id, player2_id, player3_id, player4_id FROM matches where id=" + match_id;

		try {
			db = ctxCardGraph.openOrCreateDatabase(DATABASE_NAME, 0, null);
		 	
			Cursor c = db.rawQuery(sql, null);
		 	colPlayer[0]	= c.getColumnIndex("player1_id");
		 	colPlayer[1]	= c.getColumnIndex("player2_id");
		 	colPlayer[2]	= c.getColumnIndex("player3_id");
		 	colPlayer[3]	= c.getColumnIndex("player4_id");
		 	
		 	colCourseName	= c.getColumnIndex("course_name");
		 	colDateHour		= c.getColumnIndex("date_hour_match");
		 	colnHoles		= c.getColumnIndex("holes");
		 	
		 	c.moveToLast();
		 	c.moveToFirst();
		 	if (c != null) {
		 		i = 0;
		 		
		 		// recupera los ids de lo jugadores que participan en el partido
	 			aux_player_id[0] = c.getInt(colPlayer[0]);
	 			aux_player_id[1] = c.getInt(colPlayer[1]);
	 			aux_player_id[2] = c.getInt(colPlayer[2]);
	 			aux_player_id[3] = c.getInt(colPlayer[3]);
	 			
	 			// y los datos del partido
	 			course_name 	= c.getString(colCourseName);
	 			date_hour_match = c.getString(colDateHour);
		 		
		 		for (i=0;i<4;i++) {
		 			if (aux_player_id[i]!=0) { 
			 			sql = "SELECT * FROM strokes WHERE match_id=" + match_id + " AND player_id=" + aux_player_id[i];
			 			
			 			Cursor c2 = db.rawQuery(sql, null);
			 			int colHoleNumber	= c2.getColumnIndex("hole");
			 			int colStrokes		= c2.getColumnIndex("strokes");
			 			
			 			c2.moveToLast();
					 	c2.moveToFirst();
					 	if (c2 != null) {
					 		do {
					 			int holeNumber 	= c2.getInt(colHoleNumber);
					 			int strokes		= c2.getInt(colStrokes);
					 		
					 			arrStrokes[i][holeNumber] = strokes;
					 		}
					 		while (c2.moveToNext());
					 	}		 			
			 			c2.close();
		 			}
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
	
	private void getInfoCardRemote()
	{
		URL_MATCH 	= ctxCardGraph.getString(R.string.URL_APIS) + ctxCardGraph.getString(R.string.ACTION_MATCH);
		URL_STROKES	= ctxCardGraph.getString(R.string.URL_APIS) + ctxCardGraph.getString(R.string.ACTION_STROKES);
		
		connectionOK = Authentication.checkConnection(ctxCardGraph);
		if (connectionOK) {
			Authentication.readDataUser(ctxCardGraph);
			auth_token = Authentication.getToken();
			auth_user_id = Authentication.getUserId();
		
			InitTask task = new InitTask();
			task.execute();
		}
		else {
			new AlertDialog.Builder(ctxCardGraph)
				.setIcon(R.drawable.alert_dialog_icon)
				.setTitle(R.string.remote_connection)
				.setMessage(R.string.no_internet_connect)
				.setPositiveButton(R.string.alert_button_default, null)
				.show();
		}
	}
	
	private String getPlayerName(String playerId) {
		String res = "---";
		
		for (int i=0; i<players_field1.length; i++) {
			if (playerId.equals(players_field1[i])) {
				res = players_field2[i];
			}
		}
		
		return res;
	}
		
	private void setInfoPlayers(String result) {
		JSONObject jsonObj;
		JSONArray  jsonArr;

		try {
			jsonArr = new JSONArray(result);
			
			players_field1 = new String[jsonArr.length()];
			players_field2 = new String[jsonArr.length()];
			
			for (int i=0; i<jsonArr.length(); i++) {
				jsonObj = new JSONObject(jsonArr.get(i).toString());
				
				players_field1[i] = jsonObj.getString("id");
				players_field2[i] = jsonObj.getString("name");
				Log.i("JSON", "" + i);				
			}			
		} catch (JSONException e) {
			e.printStackTrace();
		}		
	}

	private void changeMitad(int x, int y) {
		if (x>=1 && x<=3 & y>=0 && y<=2)
		{
			if (mitad==1)
				mitad = 2;
			else
				mitad = 1;
		}
		invalidate();
	}
	
	public String getMatch() {
		String response;
    	
		Log.i("CALL", "" + URL_MATCH);
		
	    RestClient client = new RestClient(URL_MATCH);
	    client.AddParam("token", auth_token);
	    client.AddParam("user_id", auth_user_id);
	    client.AddParam("match_id", "" + match_id);
	    
	    Log.i("RESPONSE", "" + auth_user_id);
	        
	    response = "";
	    try {
	        client.Execute(RequestMethod.POST);
	        response = client.getResponse();
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	    
	    Log.i("RESPONSE", "" + response);
	    
	    return response;
	}
	
	public String getStrokes(int player_id) {
		String response;
    	
		Log.i("CALL", "" + URL_STROKES);

	    RestClient client = new RestClient(URL_STROKES);
	    client.AddParam("token", auth_token);
	    client.AddParam("user_id", auth_user_id);
	    client.AddParam("match_id", "" + match_id);
	    client.AddParam("player_id", "" + player_id);
	    
	    Log.i("RESPONSE", "" + auth_user_id);
	        
	    response = "";
	    try {
	        client.Execute(RequestMethod.POST);
	        response = client.getResponse();
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	    
	    Log.i("RESPONSE", "" + response);
	    
	    return response;
	}
	
	public void setInfoMatch(String result) {
		JSONObject jsonObj;
		JSONArray  jsonArr;
		String aux_players;

		try {
			jsonObj = new JSONObject(result);
			
			aux_players 	= jsonObj.getString("players");
			course_name 	= jsonObj.getString("course_name");
			date_hour_match	= jsonObj.getString("date_hour_match");
			
			jsonArr = new JSONArray(aux_players);
			
			for (int i=0; i<jsonArr.length(); i++) {
				jsonObj = new JSONObject(jsonArr.get(i).toString());
				
				aux_player_id[i] = Integer.parseInt(jsonObj.getString("user_id"));
				aux_playerweb_id[i] = Integer.parseInt(jsonObj.getString("player_id"));
				
				Log.i("JSON", "" + aux_player_id[i]);
				
			}
			Log.i("JSON", "" + aux_players);
					
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	
	public void setInfoStrokes(int iPlayer, String result) {
		JSONObject jsonObj;
		JSONArray  jsonArr;
		int hole_number;
		int strokes;

		try {
			jsonArr = new JSONArray(result);
			
			for (int i=0; i<jsonArr.length(); i++) {
				jsonObj = new JSONObject(jsonArr.get(i).toString());
				
				hole_number = Integer.parseInt(jsonObj.getString("hole_number"));
				strokes 	= Integer.parseInt(jsonObj.getString("strokes"));
				
				arrStrokes[iPlayer][hole_number] = strokes;
				
				Log.i("JSON", "" + hole_number + ":" + strokes);
				
			}
					
		} catch (JSONException e) {
			e.printStackTrace();
		}
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
			result_getmatch 	= getMatch();
			setInfoMatch(result_getmatch);
			for (int i=0;i<aux_playerweb_id.length;i++) {
				result_getstrokes	= getStrokes(aux_playerweb_id[i]);
				setInfoStrokes(i,result_getstrokes);
			}
			
			return "";
		}

		// -- gets called just before thread begins
		@Override
		protected void onPreExecute() 
		{
			Log.i( "makemachine", "onPreExecute()" );
			super.onPreExecute();
			CharSequence title_remote = ctxCardGraph.getString(R.string.title_remote_connection);
			CharSequence remote = ctxCardGraph.getString(R.string.remote_connection);
			this.dialog = ProgressDialog.show(ctxCardGraph, title_remote, remote, true);
		}

		// -- called from the publish progress 
		// -- notice that the datatype of the second param gets passed to this method
		@Override
		protected void onProgressUpdate(Integer... values) 
		{
			super.onProgressUpdate(values);
			Log.i( "makemachine", "onProgressUpdate(): " +  String.valueOf( values[0] ) );
			//_percentField.setText( ( values[0] * 2 ) + "%");
			//_percentField.setTextSize( values[0] );
		}

		// -- called as soon as doInBackground method completes
		// -- notice that the third param gets passed to this method
		@Override
		protected void onPostExecute( String result ) 
		{
			super.onPostExecute(result);
			Log.i( "makemachine", "onPostExecute(): " + result );
			this.dialog.cancel();
			invalidate();
			
		}
	}   
		
}
