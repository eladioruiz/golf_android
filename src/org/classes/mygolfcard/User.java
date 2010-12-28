/**
 * Package: org.classes.mygolfcard
 * File: CurrentUser.java
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
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;

public class User {
	private int user_id;
	private String userLogin;
	private String userPassword;
	private String userName;
	private String userEmail;
	private double userHandicap;
	private static Context ctxUser;
	
	private static String auth_token;
	private static String auth_error_code;
	
	//private String authToken;
	
	public User() {
		super();
		
		user_id 		= 0;
		userLogin		= "";
		userPassword	= "";
		userName		= "";
		//authToken		= "";
	}
	
	public User(int pUserId, String pUserName, String pUserLogin, String pUserPassword) {
		super();
		
		user_id 		= pUserId;
		userLogin		= pUserLogin;
		userPassword	= pUserPassword;
		userName		= pUserName;
		//authToken		= "";
	}
	
	public int getUser_id() {
		return user_id;
	}

	public void setUser_id(int user_id) {
		this.user_id = user_id;
	}

	public String getUserLogin() {
		return userLogin;
	}

	public void setUserLogin(String userLogin) {
		this.userLogin = userLogin;
	}

	public String getUserPassword() {
		return userPassword;
	}

	public void setUserPassword(String userPassword) {
		this.userPassword = userPassword;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}
	
	public static User createNewUser(String sName,String sEMail,String sLogin,String sPassword,String sHandicap,Context ctx) {
		String response;
		String URL_SIGNUP; 
		int iUserId = 0;
		
		ctxUser	= ctx;
		
		URL_SIGNUP = ctxUser.getString(R.string.URL_APIS) + ctxUser.getString(R.string.ACTION_SIGNUP);
    	
	    RestClient client = new RestClient(URL_SIGNUP);
	    client.AddParam("user_name", "" + sName);
	    client.AddParam("user_email", "" + sEMail);
	    client.AddParam("user_login", "" + sLogin);
	    client.AddParam("user_password", "" + sPassword);
	    client.AddParam("user_handicap", "" + sHandicap);
	    
	    response = "";
	    try {
	        client.Execute(RequestMethod.POST);
	        response = client.getResponse();
	        
	        return parseJSONResponse(response);
	    } catch (Exception e) {
	        e.printStackTrace();
	        
	        return null;
	    }
	}

	private static User parseJSONResponse(String jsonResponse) {
		JSONObject json;
		User cUser;
		
		try {
			json = new JSONObject(jsonResponse);
			
			cUser = new User(Integer.parseInt(json.get("user_id").toString()),json.get("user_name").toString(),json.get("user_login").toString(),json.get("user_password").toString());
			
			auth_token = json.get("auth_token").toString();
			auth_error_code = json.get("error_code").toString();
			
			return cUser;
		} catch (JSONException e) {
			e.printStackTrace();
			
			return null;
		}
	}


/*
	public String getAuthToken() {
		return authToken;
	}

	public void setAuthToken(String authToken) {
		this.authToken = authToken;
	}
	
*/
	
}
