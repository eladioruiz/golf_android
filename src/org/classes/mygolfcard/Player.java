/**
 * Package: org.classes.mygolfcard
 * File: Player.java
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
import org.activities.mygolfcard.Strokes;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;

public class Player {
	private int player_id;
	private String playerName;
	private int tee_id;
	private String teeName;
	private int userWeb_id;
	private float HCP;
	private int strokesFirst;
	private int strokesSecond;
	private int strokesTotal;
	private int match_id;
	private org.classes.mygolfcard.Card card;
	private String URL_STROKES;
	private static Context ctxPlayer;
	private String auth_token;
	private int auth_user_id;
	private boolean connectionOK;
	
	public Player(Context ctx) {
		super();
		// TODO Auto-generated constructor stub
		
		ctxPlayer = ctx;
		URL_STROKES	= ctxPlayer.getString(R.string.URL_APIS) + ctxPlayer.getString(R.string.ACTION_STROKES);
		
		connectionOK = Authentication.checkConnection(ctxPlayer);
		if (connectionOK) {
			Authentication.readDataUser(ctxPlayer);
			auth_token = Authentication.getToken();
			auth_user_id = Authentication.getUserId();			
		}

	}

	public int getMatch_id() {
		return this.match_id;
	}
	
	public void setMatch_id(int match_id) {
		this.match_id = match_id;
	}
	
	public int getUserWeb_id() {
		return userWeb_id;
	}

	public void setUserWeb_id(int userWeb_id) {
		this.userWeb_id = userWeb_id;
	}

	public int getPlayer_id() {
		return player_id;
	}

	public void setPlayer_id(int player_id) {
		this.player_id = player_id;
	}

	public String getPlayerName() {
		return playerName;
	}

	public void setPlayerName(String playerName) {
		this.playerName = playerName;
	}

	public int getTee_id() {
		return tee_id;
	}

	public void setTee_id(int tee_id) {
		this.tee_id = tee_id;
	}

	public String getTeeName() {
		return teeName;
	}

	public void setTeeName(String teeName) {
		this.teeName = teeName;
	}

	public float getHCP() {
		return HCP;
	}

	public void setHCP(float hCP) {
		HCP = hCP;
	}

	public int getStrokesFirst() {
		return strokesFirst;
	}

	public void setStrokesFirst(int strokesFirst) {
		this.strokesFirst = strokesFirst;
	}

	public int getStrokesSecond() {
		return strokesSecond;
	}

	public void setStrokesSecond(int strokesSecond) {
		this.strokesSecond = strokesSecond;
	}

	public int getStrokesTotal() {
		return strokesTotal;
	}

	public void setStrokesTotal(int strokesTotal) {
		this.strokesTotal = strokesTotal;
	}

	public static Player[] getFriendsFromRemote(String auth_token, int auth_user_id, Context ctx) {
		String result;

		ctxPlayer = ctx;
		result = getFriends(auth_token, auth_user_id);
		return setInfoFriends(result);
	}

	public static Player[] getFriendsFromLocal(Context ctx) {
		String result;

		ctxPlayer = ctx;
		result = Authentication.readFriends(ctxPlayer);
		return setInfoFriends(result);
	}
		
	private static String getFriends(String auth_token, int auth_user_id) {
		String response;
		String URL_COURSES = ctxPlayer.getString(R.string.URL_APIS) + ctxPlayer.getString(R.string.ACTION_FRIENDS);
		
	    RestClient client = new RestClient(URL_COURSES);
	    client.AddParam("token", auth_token);
	    client.AddParam("user_id", "" + auth_user_id);
	    
	    response = "";
	    try {
	        client.Execute(RequestMethod.POST);
	        response = client.getResponse();
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	    
	    Authentication.saveFriends(ctxPlayer, response);
	    return response;
	}
	
	private static Player[] setInfoFriends(String result) {
		JSONObject jsonObj;
		JSONArray  jsonArr;
		Player playersList[];
		
		try {
			jsonArr = new JSONArray(result);
			
			playersList = new org.classes.mygolfcard.Player[jsonArr.length()];
			
			for (int i=0; i<jsonArr.length(); i++) {
				jsonObj = new JSONObject(jsonArr.get(i).toString());
				playersList[i] = new Player(ctxPlayer);
				
				playersList[i].setPlayerName(jsonObj.getString("name"));
				playersList[i].setPlayer_id(Integer.parseInt(jsonObj.getString("id")));
				//playersList[i].setCourse_id(Integer.parseInt(jsonObj.getString("id")));				
			}
			
			return playersList;
			
		} catch (JSONException e) {
			e.printStackTrace();
			
			return null;
		}
	}

	public Stroke[] getStrokes() {
		Stroke[] aux = new Stroke[18];
		String result;
		
		result = getRemoteStrokes();
		aux = setInfoStrokes(result);
		return aux;
	}
	
	private String getRemoteStrokes() {
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
	
	public Stroke[] setInfoStrokes(String result) {
		JSONObject jsonObj;
		JSONArray  jsonArr;
		int hole_number;
		int strokes;
		Stroke aux;
		Stroke[] infoStrokes = new Stroke[18];

		try {
			jsonArr = new JSONArray(result);
			
			for (int i=0; i<jsonArr.length(); i++) {
				jsonObj = new JSONObject(jsonArr.get(i).toString());
				
				hole_number = Integer.parseInt(jsonObj.getString("hole_number"));
				strokes 	= Integer.parseInt(jsonObj.getString("strokes"));
				
				aux = new Stroke();
				//aux.setCard_id(card.getCard_id());
				aux.setHole_id(hole_number);
				aux.setMatch_id(match_id);
				aux.setPlayer_id(player_id);
				aux.setPutts(0);
				aux.setStrokes(strokes);
								
				infoStrokes[hole_number-1] = aux;
			}
			return infoStrokes; 
					
		} catch (JSONException e) {
			e.printStackTrace();
			return null;
		}
	}
}
