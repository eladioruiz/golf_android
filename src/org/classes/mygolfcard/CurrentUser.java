/**
 * Package: org.classes.mygolfcard
 * File: CurrentUser.java
 * Description:
 * Create At: 20/10/2010
 * Created By: ERL
 * Last Modifications:
 * 
 */package org.classes.mygolfcard;

public class CurrentUser {
	private String user_id;
	private String userLogin;
	private String userPassword;
	private String userName;
	//private String authToken;
	
	public CurrentUser() {
		super();
		
		user_id 		= "";
		userLogin		= "";
		userPassword	= "";
		userName		= "";
		//authToken		= "";
	}
	
	public String getUser_id() {
		return user_id;
	}

	public void setUser_id(String user_id) {
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
/*
	public String getAuthToken() {
		return authToken;
	}

	public void setAuthToken(String authToken) {
		this.authToken = authToken;
	}
	
*/	
}
