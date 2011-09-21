package org.activities.mygolfcard;

import org.activities.mygolfcard.RestClient.RequestMethod;
import org.classes.mygolfcard.Authentication;
import org.classes.mygolfcard.Hole;
import org.classes.mygolfcard.Player;
import org.classes.mygolfcard.User;
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
	private int auth_user_id;
	private boolean connectionOK;
	//private String URL_MATCH;
	private String URL_STROKES;	
	//private String result_getmatch;
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
	
	private static final int MEDALPLAY	= 1;
	private static final int STABLEFORD	= 2;
	
	private int match_id;
	private int mitad;
	private int puntuacion;
	private int typeMatch;

	private SQLiteDatabase db = null;
	private String DATABASE_NAME = "";
	
	private int colPlayer[] = new int[4];
	private int colCourseName;
	private int colDateHour;
	private int colnHoles;

	private String course_name = "-- CAMPO --";
	private String date_hour_match = "-- FECHA/HORA --";
	//ERL private int aux_player_id[] = new int[4]; 
	//ERL private int aux_playerweb_id[] = new int[4];
	private int arrStrokes[][] = new int[4][19];
	private int arrPoints[][] = new int[4][19];

	//ERL private String[] players_field1;
	//ERL private String[] players_field2;
	
	private org.classes.mygolfcard.Match currentMatch;
	private Player auxPlayer[] = new Player[4];
	private User cUser = new User();

	private org.classes.mygolfcard.Course currentCourse;
	private org.classes.mygolfcard.Hole[] holes;

	public CardGraphView(Context context, int match_id, int mitad, int type_match) {
		super(context);

		
		DATABASE_NAME = context.getString(R.string.DB_NAME);

		ctxCardGraph = context;

		this.match_id 	= match_id;
		this.mitad 		= mitad;
		this.puntuacion	= MEDALPLAY;
		this.typeMatch 	= type_match;
		
		getInfoCard();
		
		setFocusable(true);
		setFocusableInTouchMode(true);
	}
	
	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		width = w / (float)COLS;
		height = h / (float)ROWS;
		getRect(selX, selY, selRect);
		super.onSizeChanged(w, h, oldw, oldh);
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		int i;
		int j;
		
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
		players.setTextSize(height * 0.45f);
		players.setTextScaleX(width / height);
		players.setTextAlign(Paint.Align.LEFT);
		
		Paint strokes = new Paint(Paint.ANTI_ALIAS_FLAG);
		strokes.setColor(getResources().getColor(R.color.card_players));
		strokes.setStyle(Style.FILL);
		strokes.setTextSize(height * 0.45f);
		strokes.setTextScaleX(width / height);
		strokes.setTextAlign(Paint.Align.CENTER);
		
		Paint players_total = new Paint(Paint.ANTI_ALIAS_FLAG);
		players_total.setColor(getResources().getColor(R.color.card_players));
		players_total.setStyle(Style.FILL);
		players_total.setTextSize(height * 0.50f);
		players_total.setTextScaleX(width / height);
		players_total.setTextAlign(Paint.Align.RIGHT);
		
		// Draw the number in the center of the tile
		FontMetrics fm_p = players.getFontMetrics();

		// Centering in X: use alignment (and X at midpoint)
		float x = width / 5;
		
		// Centering in Y: measure ascent/descent first
		float y = height / 2 - (fm_p.ascent + fm_p.descent) / 2;
		
		canvas.drawCircle((float)(1.5 * width), (float)(1.5 * height), (float)(1 * width), dark);
		canvas.drawText(mitad==1 ? "2P" : "1P", (float)(1.75 * width + x), (float)(1 * height + y), players);
		
		
		// Centering in X: use alignment (and X at midpoint)
		x = 4 * width / 5;
		
		canvas.drawCircle((float)(3.5 * width), (float)(1.5 * height), (float)(1 * width), dark);
		canvas.drawText(puntuacion == STABLEFORD ? "MP" : "SF", (float)(2.75 * width + x), (float)(1 * height + y), players);
		
		
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
		
		if (holes != null) {
			// Drawing info holes PAR
			j = 4;
			for (i=5;i<=13;i++) {
				int holeNumber = i - 4 + ((mitad-1) * 9);
				canvas.drawText("" + holes[holeNumber-1].getPar(),  (i * width) + x, (j * height) + y, infoHoles);
			}

			// Drawing info holes HCP
			j = 5;
			for (i=5;i<=13;i++) {
				int holeNumber = i - 4 + ((mitad-1) * 9);
				canvas.drawText("" + holes[holeNumber-1].getHandicap(),  (i * width) + x, (j * height) + y, infoHoles);
			}
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
		
		int sum_mitad 		= 0;
		int points_mitad 	= 0;
		int sum_total 		= 0;
		int points_total	= 0;
		
		for (int iPlayer=0;iPlayer<auxPlayer.length;iPlayer++) {
			i = 0;
			sum_mitad = 0;
			sum_total = 0;
			
			points_mitad = 0;
			points_total = 0;
			
			if (auxPlayer[iPlayer] != null ) {
				canvas.drawText(getPlayerName(""+ auxPlayer[iPlayer].getPlayer_id()),  (i * width) + 1, (j * height) + y, players);
			}
			else {
				canvas.drawText("---",  (i * width) + 1, (j * height) + y, players);
			}
			++i;
			
			for (int z=1 + ((mitad-1) * 9);z<10 + ((mitad-1) * 9);z++) {
				sum_mitad += arrStrokes[iPlayer][z];
				points_mitad += arrPoints[iPlayer][z];
				
				// Preguntar primero si queremos ver medal play o stableford
				int display = 0;
				if (puntuacion == MEDALPLAY) {
					display = arrStrokes[iPlayer][z];
				}
				else {
					display = arrPoints[iPlayer][z];
				}
				
				canvas.drawText("" + display, ((i+4) * width) + x, (j* height) + y, strokes);
				
				++i;
			}
			
			for (int z=1;z<19;z++) {
				sum_total += arrStrokes[iPlayer][z];
				points_total += arrPoints[iPlayer][z];
			}
			
			// Preguntar primero si queremos ver medal play o stableford
			if (puntuacion == MEDALPLAY) {
				canvas.drawText("" + sum_mitad, ((i + PARTIAL_COL) * width) , (j* height) + y, players_total);
				canvas.drawText("" + sum_total, ((i + TOTAL_COL) * width) , (j* height) + y, players_total);
			}
			else {
				canvas.drawText("" + points_mitad, ((i + PARTIAL_COL) * width) , (j* height) + y, players_total);
				canvas.drawText("" + points_total, ((i + TOTAL_COL) * width) , (j* height) + y, players_total);
			}
			
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
		
		changeMitad(selX,selY);
		changeCount(selX,selY);
		
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
	
		for (int j=0;j<4;j++) {
			for (int i=0;i<19;i++) {
				arrStrokes[j][i]	= 0;
				arrPoints[j][i]		= 0;
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
		String sql;

		currentMatch = org.classes.mygolfcard.Match.getMatchFromDB(ctxCardGraph, match_id);
		auxPlayer = currentMatch.getPlayers();
		String result = Authentication.readFriends(ctxCardGraph);
		setInfoPlayers(result);
		
		course_name = currentMatch.getCourseName();
		date_hour_match = currentMatch.getDateHour();
		
		try {
			db = ctxCardGraph.openOrCreateDatabase(DATABASE_NAME, 0, null);
		
			for (int i=0;i<4;i++) {
	 			if (auxPlayer[i] != null) { 
		 			sql = "SELECT * FROM strokes WHERE match_id=" + match_id + " AND player_id=" + auxPlayer[i].getUserWeb_id();
		 			
		 			Cursor c2 = db.rawQuery(sql, null);
		 			int colHoleNumber	= c2.getColumnIndex("hole");
		 			int colStrokes		= c2.getColumnIndex("strokes");
		 			
		 			c2.moveToLast();
				 	c2.moveToFirst();
				 	if (c2 != null) {
				 		do {
				 			int holeNumber 	= c2.getInt(colHoleNumber);
				 			int strokes		= c2.getInt(colStrokes);
				 			int points		= calculatePointsStableford(holeNumber,strokes,holes,auxPlayer[i].getHCP());
				 		
				 			arrStrokes[i][holeNumber] 	= strokes;
				 			arrPoints[i][holeNumber] 	= points;
				 		}
				 		while (c2.moveToNext());
				 	}		 			
		 			c2.close();
	 			}
	 		}
		}
		catch(Exception e) {
    		Log.e("Error", "Error reading DB", e);
    	} 
    	finally {
    		if (db != null)
    			db.close();
    	}
    	
    	invalidate();
	}
	
	private void getInfoCardRemote()
	{
		//ERL URL_MATCH 	= ctxCardGraph.getString(R.string.URL_APIS) + ctxCardGraph.getString(R.string.ACTION_MATCH);
		URL_STROKES	= ctxCardGraph.getString(R.string.URL_APIS) + ctxCardGraph.getString(R.string.ACTION_STROKES);
		
		connectionOK = Authentication.checkConnection(ctxCardGraph);
		if (connectionOK) {
			Authentication.readDataUser(ctxCardGraph);
			auth_token = Authentication.getToken();
			auth_user_id = Authentication.getUserId();
			
			cUser.setUser_id(Authentication.getUserId());
			cUser.setUserName(Authentication.getUserName());
		
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
		
		for (int i=0;i<auxPlayer.length;i++) {
			if (auxPlayer[i] != null) {
				if (playerId.equals("" + auxPlayer[i].getPlayer_id())) {
					res = auxPlayer[i].getPlayerName() + " HCP:" + auxPlayer[i].getHCP();
				}
			}
		}
		
		return res;
	}
		
	private void setInfoPlayers(String result) {
		JSONObject jsonObj;
		JSONArray  jsonArr;

		try {
			jsonArr = new JSONArray(result);
			
			//auxPlayer = new org.classes.mygolfcard.Player[jsonArr.length()];
			
			for (int i=0; i<jsonArr.length(); i++) {
				jsonObj = new JSONObject(jsonArr.get(i).toString());
				
				auxPlayer[i].setPlayer_id(Integer.parseInt(jsonObj.getString("id")));
				auxPlayer[i].setPlayerName(jsonObj.getString("name"));
			}			
		} catch (JSONException e) {
			e.printStackTrace();
		}		
	}

	private void changeMitad(int x, int y) {
		if (x>=1 && x<=2 & y>=0 && y<=2)
		{
			if (mitad == 1)
				mitad = 2;
			else
				mitad = 1;
		}
		invalidate();
	}
		
	private void changeCount(int x, int y) {
		if (x>=3 && x<=4 & y>=0 && y<=2)
		{
			if (puntuacion == MEDALPLAY)
				puntuacion = STABLEFORD;
			else
				puntuacion = MEDALPLAY;
		}
		invalidate();
	}
		
	public String getStrokes(int player_id) {
		String response;
    	
	    RestClient client = new RestClient(URL_STROKES);
	    client.AddParam("token", auth_token);
	    client.AddParam("user_id", "" + auth_user_id);
	    client.AddParam("match_id", "" + match_id);
	    client.AddParam("player_id", "" + player_id);
	    
	    response = "";
	    try {
	        client.Execute(RequestMethod.POST);
	        response = client.getResponse();
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	    
	    return response;
	}
	
	public void setInfoStrokes(int iPlayer, String result, double playerHandicap) {
		JSONObject jsonObj;
		JSONArray  jsonArr;
		int hole_number;
		int strokes;
		int points;

		try {
			jsonArr = new JSONArray(result);
			
			for (int i=0; i<jsonArr.length(); i++) {
				jsonObj = new JSONObject(jsonArr.get(i).toString());
				
				hole_number = Integer.parseInt(jsonObj.getString("hole_number"));
				strokes 	= Integer.parseInt(jsonObj.getString("strokes"));
				points		= calculatePointsStableford(hole_number,strokes,holes,playerHandicap);
				
				arrStrokes[iPlayer][hole_number] = strokes;
				arrPoints[iPlayer][hole_number] = points;
			}
					
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	private int calculatePointsStableford(int holeNumber, int strokes, Hole[] holes, double playerHandicap) {
		int iPoints;
		
		iPoints = 0;
		
		for (int i=0;i<holes.length;i++) {
			if (holes[i].getHoleNumber()==holeNumber) {
				iPoints = pointsStableford(strokes,playerHandicap,holes[i].getHandicap(),holes[i].getPar(),holes.length);
			}
		}
		
		return iPoints;
	}
	
	private int pointsStableford(int strokes,double playerHandicap, int holeHandicap, int holePar, int nHoles) {
		int aps           = 0;
	    int strk          = 0;
	    int hole_par      = 0;
	    int strk_allowed  = 0;
	    int points        = 0;

	    aps           = pointsStablefordPerHole(holeHandicap, playerHandicap, nHoles);
	    strk          = strokes;
	    hole_par      = holePar;
	    strk_allowed  = aps + hole_par;
	    
	    if (strk > strk_allowed + 1)
	    	points = 0;
	    else if (strk == strk_allowed + 1)
	      points = 1;
	    else if (strk == strk_allowed)
	      points = 2;
	    else if (strk == strk_allowed - 1)
	      points = 3;
	    else if (strk == strk_allowed - 2)
	      points = 4;
	    else if (strk == strk_allowed - 3)
	      points = 5;
	    else if (strk == strk_allowed - 4)
	      points = 6;
	    else
	      points = 0;

	    return points;
	}
	
	private int pointsStablefordPerHole(int holeHandicap, double playerHandicap, int nHoles) {
		int iPoints = 0;
		
		iPoints = addPointsStableford(playerHandicap, nHoles);
	    int hole_handicap = holeHandicap; 
	    if (hole_handicap <= lastHoleStableford(playerHandicap,nHoles))
	    	iPoints += 1;
	    	
		return iPoints;
	}
	
	private int addPointsStableford(double playerHandicap, int nHoles) {
		int iRes = 0;
		
	    double hand = 0;
	    int n_holes = 0;

	    hand = playerHandicap;
	    n_holes = nHoles;

	    if (n_holes != 0)
	      iRes = (int)hand / n_holes;
	    else
	      iRes = 0;

		return iRes;
	}
	
	private int lastHoleStableford(double playerHandicap, int nHoles) {
		int iRes = 0;
		
		if (nHoles != 0)
		 iRes = (int)playerHandicap % nHoles;
		
		return iRes;
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
			//ERL result_getmatch 	= getMatch();
			//ERL setInfoMatch(result_getmatch);
			currentMatch = org.classes.mygolfcard.Match.setDataFromRemote(match_id, cUser.getUser_id(), auth_token, ctxCardGraph);
			auxPlayer = currentMatch.getPlayers();
			
			course_name = currentMatch.getCourseName();
			date_hour_match	= currentMatch.getDateHour();
			
			currentCourse = new org.classes.mygolfcard.Course(ctxCardGraph);
			holes = org.classes.mygolfcard.Hole.getInfoHoles(currentMatch.getCourse_id(),auth_token, ctxCardGraph);
			
			for (int i=0;i<auxPlayer.length;i++) {
				if (auxPlayer[i] != null) {
					result_getstrokes	= getStrokes(auxPlayer[i].getPlayer_id());
					setInfoStrokes(i,result_getstrokes,auxPlayer[i].getHCP());
				}
			}
			
			return "";
		}

		// -- gets called just before thread begins
		@Override
		protected void onPreExecute() 
		{
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
		}

		// -- called as soon as doInBackground method completes
		// -- notice that the third param gets passed to this method
		@Override
		protected void onPostExecute( String result ) 
		{
			super.onPostExecute(result);
			this.dialog.cancel();
			invalidate();
		}
	}   
		
}

