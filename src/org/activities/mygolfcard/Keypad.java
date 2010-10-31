/***
 * Excerpted from "Hello, Android! 2e",
 * published by The Pragmatic Bookshelf.
 * Copyrights apply to this code. It may not be used to create training material, 
 * courses, books, articles, and the like. Contact us if you are in doubt.
 * We make no guarantees that this code is fit for any purpose. 
 * Visit http://www.pragmaticprogrammer.com/titles/eband2 for more book information.
***/

package org.activities.mygolfcard;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;

public class Keypad extends Dialog {

	private final View keys[] = new View[12];
	private View keypad;
	private final Strokes strokes;
	private final int playerPush;

	public Keypad(Context context, int playerPush) {
		super(context);
		this.playerPush = playerPush;
		this.strokes = (Strokes) context;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setTitle(R.string.keypad_title);
		setContentView(R.layout.keypad);
		findViews();
		setListeners();
	}

	private void findViews() {
		keypad = findViewById(R.id.keypad);
		keys[0] = findViewById(R.id.keypad_1);
		keys[1] = findViewById(R.id.keypad_2);
		keys[2] = findViewById(R.id.keypad_3);
		keys[3] = findViewById(R.id.keypad_4);
		keys[4] = findViewById(R.id.keypad_5);
		keys[5] = findViewById(R.id.keypad_6);
		keys[6] = findViewById(R.id.keypad_7);
		keys[7] = findViewById(R.id.keypad_8);
		keys[8] = findViewById(R.id.keypad_9);
		keys[9] = findViewById(R.id.keypad_10);
		keys[10] = findViewById(R.id.keypad_11);
		keys[11] = findViewById(R.id.keypad_12);
	}
   
	private void setListeners() {
		for (int i = 0; i < keys.length; i++) {
			final int t = i + 1;
			keys[i].setOnClickListener(new View.OnClickListener(){
				public void onClick(View v) {
					returnResult("" + t, playerPush);
				}});
		}
		
		keypad.setOnClickListener(new View.OnClickListener(){
			public void onClick(View v) {
				returnResult("0", playerPush);
			}});
	}

	/** Return the chosen tile to the caller */
	private void returnResult(String tile, int targetView) {
		this.strokes.setKey(targetView, tile);
		dismiss();
	}
   

}

