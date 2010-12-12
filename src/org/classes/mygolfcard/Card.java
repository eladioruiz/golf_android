/**
 * Package: org.activities.mygolfcard
 * File: Card.java
 * Description:
 * Create At: 06/12/2010 - ERL - POO
 * Created By: ERL
 * Last Modifications:
 * 		
 */
package org.classes.mygolfcard;


public class Card {
	private int match_id;
	private int player_id;
	private int card_id;
	private int n_holes;
	private int strokes_total;
	private int strokes_first_9;
	private int strokes_second_9;
	private Stroke[] strokes;
	
	/**
	 * @return the match_id
	 */
	public int getMatch_id() {
		return match_id;
	}
	
	/**
	 * @param match_id the match_id to set
	 */
	public void setMatch_id(int match_id) {
		this.match_id = match_id;
	}
	
	/**
	 * @return the player_id
	 */
	public int getPlayer_id() {
		return player_id;
	}
	
	/**
	 * @param player_id the player_id to set
	 */
	public void setPlayer_id(int player_id) {
		this.player_id = player_id;
	}
	
	/**
	 * @return the card_id
	 */
	public int getCard_id() {
		return card_id;
	}
	
	/**
	 * @param card_id the card_id to set
	 */
	public void setCard_id(int card_id) {
		this.card_id = card_id;
	}
	
	/**
	 * @return the strokes_total
	 */
	public int getStrokes_total() {
		return strokes_total;
	}
	
	/**
	 * @param strokes_total the strokes_total to set
	 */
	public void setStrokes_total(int strokes_total) {
		this.strokes_total = strokes_total;
	}
	
	/**
	 * @return the strokes_first_9
	 */
	public int getStrokes_first_9() {
		return strokes_first_9;
	}
	
	/**
	 * @param strokes_first_9 the strokes_first_9 to set
	 */
	public void setStrokes_first_9(int strokes_first_9) {
		this.strokes_first_9 = strokes_first_9;
	}
	
	/**
	 * @return the strokes_second_9
	 */
	public int getStrokes_second_9() {
		return strokes_second_9;
	}
	
	/**
	 * @param strokes_second_9 the strokes_second_9 to set
	 */
	public void setStrokes_second_9(int strokes_second_9) {
		this.strokes_second_9 = strokes_second_9;
	}

	/**
	 * @return the n_holes
	 */
	public int getN_holes() {
		return n_holes;
	}

	/**
	 * @param n_holes the n_holes to set
	 */
	public void setN_holes(int n_holes) {
		this.n_holes = n_holes;
	}

	
	
}
