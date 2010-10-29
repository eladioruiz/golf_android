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

import org.example.mygolfcard.Authentication;
import org.example.mygolfcard.R;
import org.example.mygolfcard.RestClient;
import org.example.mygolfcard.RestClient.RequestMethod;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;

public class Match {
	private int match_id;
	private int course_id;
	private String courseName;
	private String dateHour;
	private Player players[] = new Player[4];
	private static Context ctxMatch;
	private int nHoles;
	private int indexPlayer = 0;

	public Match(Context ctx) {
		super();
		ctxMatch = ctx;
		
		course_id	= 0;
		courseName	= "";
		dateHour	= "";
		//players		= null;
		
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

	public Player[] getPlayers() {
		return players;
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
	
	public Match setDataFromRemote(int match_id, int user_id, String token) {
		String result;
		Match aux = new Match(ctxMatch);
		
		result = getMatch(match_id, user_id, token);
		aux = setInfoMatch(result);
		
		return aux;
	}
	
	public static Match[] getMatchesFromRemote(String auth_token, int user_id, Context ctx) {
		String result;

		ctxMatch = ctx;
		result = getMatches(auth_token, user_id);
		return setInfoMatches(result);
	}
	
	public static Match[] getMatchesFromLocal(Context ctx) {
		String result;

		ctxMatch = ctx;
		result = Authentication.readMatches(ctxMatch);
		return setInfoMatches(result);
	}

	private String getMatch(int match_id, int user_id, String token) {
		String response;
		String URL_MATCH = ctxMatch.getString(R.string.URL_APIS) + ctxMatch.getString(R.string.ACTION_MATCH);
	    
		RestClient client = new RestClient(URL_MATCH);
	    client.AddParam("token", token);
	    client.AddParam("user_id", "" + user_id);
	    //ERL client.AddParam("course_id", course_id);
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
			
		} catch (JSONException e) {
			e.printStackTrace();
			
			return null;
		}
	}

	private Match setInfoMatch(String result) {
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
			
			jsonArr = new JSONArray(aux_players);
			
			for (int i=0; i<jsonArr.length(); i++) {
				jsonObj = new JSONObject(jsonArr.get(i).toString());
				
				Player aux_player = new Player();
				aux_player.setPlayer_id(Integer.parseInt(jsonObj.getString("user_id")));
				aux_player.setUserWeb_id(Integer.parseInt(jsonObj.getString("player_id")));
				aux_player.setPlayerName(jsonObj.getString("user_name"));
				aux_player.setHCP(Float.parseFloat(jsonObj.getString("handicap")));
				aux_player.setStrokesFirst(Integer.parseInt(jsonObj.getString("card_1")));
				aux_player.setStrokesSecond(Integer.parseInt(jsonObj.getString("card_2")));
				aux_player.setStrokesTotal(Integer.parseInt(jsonObj.getString("card_total")));
				res.addPlayer(aux_player);				
			}
			
			for (int i=jsonArr.length();i<4;i++) {
				res.setPlayerNull(i);
			}
			return res;
					
		} catch (JSONException e) {
			e.printStackTrace();
			return null;
		}
	}
	

}
