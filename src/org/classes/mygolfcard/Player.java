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
	
	public Player() {
		super();
		// TODO Auto-generated constructor stub
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

	
}
