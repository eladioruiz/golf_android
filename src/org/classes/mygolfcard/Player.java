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

import org.activities.mygolfcard.Authentication;
import org.activities.mygolfcard.R;
import org.activities.mygolfcard.RestClient;
import org.activities.mygolfcard.RestClient.RequestMethod;
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
	private org.classes.mygolfcard.Card card;

	private static Context ctxPlayer;
	
	public Player(Context ctx) {
		super();
		// TODO Auto-generated constructor stub
		
		ctxPlayer = ctx;
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

	public static Player[] getPlayersFromRemote(String auth_token, String auth_user_id, Context ctx) {
		String result;

		ctxPlayer = ctx;
		result = getPlayers(auth_token, auth_user_id);
		return setInfoPlayers(result);
	}

	public static Player[] getPlayersFromLocal(Context ctx) {
		String result;

		ctxPlayer = ctx;
		result = Authentication.readFriends(ctxPlayer);
		return setInfoPlayers(result);
	}
		
	private static String getPlayers(String auth_token, String auth_user_id) {
		String response;
		String URL_COURSES = ctxPlayer.getString(R.string.URL_APIS) + ctxPlayer.getString(R.string.ACTION_FRIENDS);
		
	    RestClient client = new RestClient(URL_COURSES);
	    client.AddParam("token", auth_token);
	    client.AddParam("user_id", auth_user_id);
	    
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
	
	private static Player[] setInfoPlayers(String result) {
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

	
}
