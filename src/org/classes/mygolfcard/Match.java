/**
 * Package: org.classes.mygolfcard
 * File: Match.java
 * Description:
 * Create At: 20/10/2010
 * Created By: ERL
 * Last Modifications:
 * 
 */
package org.classes.mygolfcard;

import org.activities.mygolfcard.R;
import org.activities.mygolfcard.RestClient;
import org.activities.mygolfcard.RestClient.RequestMethod;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class Match {
	private int match_id;
	private int course_id;
	private String courseName;
	private String dateHour;
	private Player players[] = new Player[4];
	private static Context ctxMatch;
	private int nHoles;
	private int indexPlayer = 0;
	private static boolean localStorage = false;

	private static SQLiteDatabase db = null;
	private static String DATABASE_NAME;
	
	public Match(Context ctx) {
		super();
		ctxMatch = ctx;
		
		course_id	= 0;
		courseName	= "";
		dateHour	= "";
		//players		= null;
		
		DATABASE_NAME = ctx.getString(R.string.DB_NAME);
		
		// TODO Auto-generated constructor stub
	}
	
	public int getMatch_id() {
		return match_id;
	}
	
	public void setMatch_id(int match_id) {
		this.match_id = match_id;
	}
	
	public int getCourse_id() {
		return course_id;
	}
	
	public void setCourse_id(int course_id) {
		this.course_id = course_id;
	}
	
	public String getCourseName() {
		return courseName;
	}
	
	public void setCourseName(String courseName) {
		this.courseName = courseName;
	}
	
	public String getDateHour() {
		return dateHour;
	}
	
	public void setDateHour(String dateHour) {
		this.dateHour = dateHour;
	}
	
	public int getHoles() {
		return nHoles;
	}

	public void setHoles(int nHoles) {
		this.nHoles = nHoles;
	}
	
	public boolean getLocalStorage() {
		return localStorage;
	}

	public Player[] getPlayers() {
		return players;
	}
	
	public int getNumPlayers() {
		int res = 0;
		for (int i=0;i<4;i++) {
			if (this.players[i] != null) {
				res += 1;
			}
		}
		
		return res;
	}
	
	public void setPlayer(Player[] players) {
		this.players = players;
	}
	
	public void addPlayer(Player pl) {
		if (indexPlayer<4) {
			this.players[indexPlayer++] = pl;
		}
	}
	
	public void setPlayerNull(int index) {
		this.players[index] = null;
	}
	
	public static Match setDataFromRemote(int match_id, int user_id, String token, Context ctx) {
		String result;
		Match aux = new Match(ctx);
		
		ctxMatch = ctx;
		result 	= getMatch(match_id, user_id, token);
		aux 	= setInfoMatch(result);
		
		return aux;
	}
	
	public static Match[] getMatchesFromRemote(String auth_token, int user_id, Context ctx) {
		String result;

		ctxMatch = ctx;
		localStorage = false;
		result = getMatches(auth_token, user_id);
		return setInfoMatches(result);
	}
	
	public static Match[] getMatchesFromLocal(Context ctx) {
		String result;

		ctxMatch = ctx;
		result = Authentication.readMatches(ctxMatch);
		return setInfoMatches(result);
	}
	
	public static Match getMatchFromDB(Context ctx, int match_id) {
		String sql;
		org.classes.mygolfcard.Match auxMatch = null;
		
		localStorage = true;
		
		try {
			auxMatch = new org.classes.mygolfcard.Match(ctx);
			
			db = ctx.openOrCreateDatabase(DATABASE_NAME, Context.MODE_PRIVATE, null);
			sql = "select * from matches where ID=" + match_id + ";";
		 	Cursor c = db.rawQuery(sql, null);
		 	
		 	if (c != null) {
			 	int colCourseId		= c.getColumnIndex("course_id");
			    int colCourseName	= c.getColumnIndex("course_name");
			    int colDateHour		= c.getColumnIndex("date_hour_match");
			    int colHoles		= c.getColumnIndex("holes");
			    int colPlayer1		= c.getColumnIndex("player1_id");
			    int colPlayer2		= c.getColumnIndex("player2_id");
			    int colPlayer3		= c.getColumnIndex("player3_id");
			    int colPlayer4		= c.getColumnIndex("player4_id");
			 	
			 	c.moveToFirst();
			 	
		 		do {
		 			auxMatch.setMatch_id(match_id);
		 			auxMatch.setCourse_id(c.getInt(colCourseId));
		 			auxMatch.setCourseName(c.getString(colCourseName));
		 			auxMatch.setDateHour(c.getString(colDateHour));
		 			auxMatch.setHoles(c.getInt(colHoles));
		 			auxMatch.setPlayerNull(0);
		 			auxMatch.setPlayerNull(1);
		 			auxMatch.setPlayerNull(2);
		 			auxMatch.setPlayerNull(3);
		 			
		 			Player pls[] = new Player[4];
		 			for (int i=0;i<pls.length;i++) {
		 				pls[i] = new Player(ctx);
		 			}
		 					 			
		 			pls[0].setUserWeb_id(c.getInt(colPlayer1));
		 			pls[1].setUserWeb_id(c.getInt(colPlayer2));
		 			pls[2].setUserWeb_id(c.getInt(colPlayer3));
		 			pls[3].setUserWeb_id(c.getInt(colPlayer4));
		 			auxMatch.setPlayer(pls);
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
    	
    	return auxMatch;
	}

	public int getStrokesPerHole(int playerId,int holeNumber) {
		int res = 0;
		
		// Si es un partido almacenado en BD (pendiente)...
		// ... sacamos los datos de BD
		if (localStorage) {
			res = Integer.parseInt(getStrokesHole(this.match_id,playerId,holeNumber));
		}
		else {
			// ... en caso contrario, hay que pedirlos por WS
			res = 0;
		}
		return res;
	}
	
	private String getStrokesHole(int match_id, int player_id, int hole){
		String res = "0";
		String sql = "";
		
		try {
			db = ctxMatch.openOrCreateDatabase(DATABASE_NAME, ctxMatch.MODE_PRIVATE, null);
			sql = "select strokes from strokes where match_id=" + match_id + " and player_id=" + player_id + " and hole=" + hole + ";";
		 	Cursor c = db.rawQuery(sql, null);
		 	int colStrokes		= c.getColumnIndex("strokes");
		 	
		 	c.moveToFirst();
		 	if (c != null) {
		 		if (c.getCount() > 0) {
		 			res = "" + c.getInt(colStrokes);
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
    	
		return res;
	}

	private static String getMatch(int match_id, int user_id, String token) {
		String response;
		String URL_MATCH = ctxMatch.getString(R.string.URL_APIS) + ctxMatch.getString(R.string.ACTION_MATCH);
	    
		RestClient client = new RestClient(URL_MATCH);
	    client.AddParam("token", token);
	    client.AddParam("user_id", "" + user_id);
	    client.AddParam("match_id", "" + match_id);
	    
	    response = "";
	    try {
	        client.Execute(RequestMethod.POST);
	        response = client.getResponse();
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	    
	    return response;
	}

	private static String getMatches(String auth_token, int user_id) {
		String response;
		String URL_MATCHES = ctxMatch.getString(R.string.URL_APIS) + ctxMatch.getString(R.string.ACTION_MATCHES);
    	
	    RestClient client = new RestClient(URL_MATCHES);
	    client.AddParam("token", auth_token);
	    client.AddParam("user_id", "" + user_id);
	    
	    response = "";
	    try {
	        client.Execute(RequestMethod.POST);
	        response = client.getResponse();
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	    
	    Authentication.saveMatches(ctxMatch, response);
	    return response;
	}
	
	private static Match[] setInfoMatches(String result) {
		JSONObject jsonObj;
		JSONArray  jsonArr;
		Match matchesList[];

		try {
			jsonArr = new JSONArray(result);
			
			//matches = new String[jsonArr.length()];
			matchesList = new org.classes.mygolfcard.Match[jsonArr.length()];
			
			for (int i=0; i<jsonArr.length(); i++) {
				jsonObj = new JSONObject(jsonArr.get(i).toString());
				matchesList[i] = new Match(ctxMatch);
				
				matchesList[i].setMatch_id(Integer.parseInt(jsonObj.getString("match_id")));
				matchesList[i].setCourseName(jsonObj.getString("course_name"));
				matchesList[i].setDateHour(jsonObj.getString("date_hour"));
			}			

			return matchesList;
			
		} catch (Exception e) {
			e.printStackTrace();
			
			return null;
		}
	}

	private static Match setInfoMatch(String result) {
		JSONObject jsonObj;
		JSONArray  jsonArr;
		String aux_players;
		Match res = new Match(ctxMatch);

		try {
			jsonObj = new JSONObject(result);
			
			aux_players = jsonObj.getString("players");
			res.setCourseName(jsonObj.getString("course_name"));
			//res.setCourse_id(Integer.parseInt(jsonObj.getString("course_id")));
			res.setDateHour(jsonObj.getString("date_hour_match"));
			res.setHoles(Integer.parseInt(jsonObj.getString("holes")));
			res.setMatch_id(Integer.parseInt(jsonObj.getString("match_id")));
			
			jsonArr = new JSONArray(aux_players);
			
			for (int i=0; i<jsonArr.length(); i++) {
				jsonObj = new JSONObject(jsonArr.get(i).toString());
				
				Player aux_player = new Player(ctxMatch);
				aux_player.setMatch_id(res.getMatch_id());
				aux_player.setPlayer_id(Integer.parseInt(jsonObj.getString("player_id")));
				aux_player.setUserWeb_id(Integer.parseInt(jsonObj.getString("user_id")));
				aux_player.setPlayerName(jsonObj.getString("user_name"));
				aux_player.setHCP(Float.parseFloat(jsonObj.getString("handicap")));
				aux_player.setStrokesFirst(Integer.parseInt(jsonObj.getString("card_1")));
				aux_player.setStrokesSecond(Integer.parseInt(jsonObj.getString("card_2")));
				aux_player.setStrokesTotal(Integer.parseInt(jsonObj.getString("card_total")));
				res.addPlayer(aux_player);				
			}
			
			if (jsonArr.length()<4) {
				for (int i=jsonArr.length();i<4;i++) {
					res.setPlayerNull(i);
				}
			}
			return res;
					
		} catch (JSONException e) {
			e.printStackTrace();
			Log.e("MATCH", e.getMessage());
			return res;
		}
	}
	
	public static String deleteRemoteMatch(int match_id, int user_id, String auth_token) {
		String response;
		String URL_MATCHES = ctxMatch.getString(R.string.URL_APIS) + ctxMatch.getString(R.string.ACTION_DELETEMATCH);
    	
	    RestClient client = new RestClient(URL_MATCHES);
	    client.AddParam("token", auth_token);
	    client.AddParam("user_id", "" + user_id);
	    client.AddParam("match_id", "" + match_id);
	    
	    response = "";
	    try {
	        client.Execute(RequestMethod.POST);
	        response = client.getResponse();
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	    
	    Authentication.saveMatches(ctxMatch, response);
	    return response;
	}

}
