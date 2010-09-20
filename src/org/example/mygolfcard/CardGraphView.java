package org.example.mygolfcard;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.FontMetrics;
import android.graphics.Paint.Style;
import android.graphics.Rect;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

public class CardGraphView extends View {
	private static final String TAG = "MyGolfCard" ;
	private Context ctxCardGraph;
	
	private float width; // width of one tile
	private float height; // height of one tile
	private int selX; // X index of selection
	private int selY; // Y index of selection
	private final Rect selRect = new Rect();
	
	private static int COLS= 15;
	private static int ROWS= 10;
	
	private static int FIRST_COL= 5;
	private static int FIRST_ROW= 3;

	private static int TYPEMATCH_INTERNAL 	= 1;
	private static int TYPEMATCH_REMOTE		= 2;
	
	private int match_id;
	private int mitad;
	private int typeMatch;

	private SQLiteDatabase db = null;
	private String DATABASE_NAME = "mygolfcard.db";
	
	private int colPlayer[] = new int[4];
	
	private int aux_player_id[] = new int[4]; 
	private int arrStrokes[][] = new int[4][19];
	
	public CardGraphView(Context context, int match_id, int mitad, int type_match) {
		super(context);

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
			canvas.drawLine(i * width, 0, i * width, getHeight(), light);  // lineas vert
			canvas.drawLine(i * width + 1, 0, i * width + 1, getHeight(), hilite);  // lineas vert
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
		canvas.drawLine(i * width, 0, i * width, getHeight(), dark);
		canvas.drawLine(i * width + 1, 0, i * width + 1, getHeight(), hilite);
		
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
		// Draw the number in the center of the tile
		FontMetrics fm_p = players.getFontMetrics();

		// Centering in X: use alignment (and X at midpoint)
		float x = width / 2;
		
		// Centering in Y: measure ascent/descent first
		float y = height / 2 - (fm_p.ascent + fm_p.descent) / 2;
		
		// Drawing Players
		i = 0;
		j = 6;
		canvas.drawText("Eladio Ruiz",  (i * width) + 1, (j * height) + y, players);
		
		i = 0;
		j = 7;
		canvas.drawText("Beatriz Pardo",  (i * width) + 1, (j * height) + y, players);
		
		i = 0;
		j = 8;
		canvas.drawText("Juan Pardo",  (i * width) + 1, (j * height) + y, players);
		
		i = 0;
		j = 9;
		canvas.drawText("Merche Calvo",  (i * width) + 1 , (j * height) + y, players);
		
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
		//int mitad = 2;
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
		canvas.drawText("T",  (i * width) + x, (j * height) + y, infoHoles);
		
		// INFO MATCH
		for (j=0;j<4;j++) {
			for (i=1 + ((mitad-1) * 9);i<10 + ((mitad-1) * 9);i++) {
				canvas.drawText("" + arrStrokes[j][i], ((i+4) * width) + x, ((j + 6)* height) + y, players);
			}			
		}
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (event.getAction() != MotionEvent.ACTION_DOWN)
			return super.onTouchEvent(event);
		
		select((int) (event.getX() / width), (int) (event.getY() / height));
		Log.d(TAG, "onTouchEvent: x " + selX + ", y " + selY);
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
			case 1 : // TYPEMATCH_INTERNAL:
				getInfoCardInternal();
				break;
			case 2 : // TYPEMATCH_REMOTE:
				getInfoCardRemote();
				break;
		}
	}
	
	private void getInfoCardInternal()
	{
		String res = "";
		String sql = "";
		int i = 0;
		int j = 0;
		
		sql = "SELECT player1_id, player2_id, player3_id, player4_id FROM matches where id=" + match_id;

		try {
			db = ctxCardGraph.openOrCreateDatabase(DATABASE_NAME, 0, null);
		 	
			Cursor c = db.rawQuery(sql, null);
		 	colPlayer[0]		= c.getColumnIndex("player1_id");
		 	colPlayer[1]		= c.getColumnIndex("player2_id");
		 	colPlayer[2]		= c.getColumnIndex("player3_id");
		 	colPlayer[3]		= c.getColumnIndex("player4_id");
		 	
		 	c.moveToLast();
		 	c.moveToFirst();
		 	if (c != null) {
		 		i = 0;
		 		do {
		 			aux_player_id[0] = c.getInt(colPlayer[0]);
		 			aux_player_id[1] = c.getInt(colPlayer[1]);
		 			aux_player_id[2] = c.getInt(colPlayer[2]);
		 			aux_player_id[3] = c.getInt(colPlayer[3]);
		 		}
		 		while (c.moveToNext());
		 		
		 		for (i=0;i<4;i++) {
		 			sql = "SELECT * FROM strokes WHERE match_id=" + match_id + " AND player_id=" + aux_player_id[i];
		 			
		 			Cursor c2 = db.rawQuery(sql, null);
		 			int colHoleNumber	= c2.getColumnIndex("hole");
		 			int colStrokes		= c2.getColumnIndex("strokes");
		 			
		 			c2.moveToLast();
				 	c2.moveToFirst();
				 	if (c2 != null) {
				 		j = 0;
				 		do {
				 			int holeNumber 	= c2.getInt(colHoleNumber);
				 			int strokes		= c2.getInt(colStrokes);
				 		
				 			arrStrokes[i][holeNumber] = strokes;
				 			++j;
				 		}
				 		while (c2.moveToNext());
				 	}
		 			
		 			c2.close();
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
	
	private void drawInfo() {
		int i;
		int j;
		
	}
	
	private void getInfoCardRemote()
	{
		
	}
}
