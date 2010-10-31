/* $Id: BulletedTextView.java 57 2007-11-21 18:31:52Z steven $
 *
 * Copyright 2007 Steven Osborn
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

/* Code modifications by Daniel Ricciotti
 * This code was built using the IconifiedText tutorial by Steven Osborn
 * http://www.anddev.org/iconified_textlist_-_the_making_of-t97.html
 * 
 * Copyright 2008 Daniel Ricciotti
 */
package org.activities.mygolfcard;

import android.content.Context;
import android.graphics.Typeface;
import android.view.View;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

public class CheckBoxifiedTextView extends LinearLayout {

	private TextView mTextTitle;
	private TextView mTextSubtitle;
	private CheckBox mCheckBox;
	private CheckBoxifiedText mCheckBoxText;

	public CheckBoxifiedTextView(Context context, CheckBoxifiedText aCheckBoxifiedText) {
		super(context);
		
		/* First CheckBox and the Text to the right (horizontal),
		 * not above and below (vertical) */
		this.setOrientation(HORIZONTAL);
		mCheckBoxText = aCheckBoxifiedText;
		mCheckBox = new CheckBox(context);
		mCheckBox.setPadding(20, 0, 20, 0); // 5px to the right
		mCheckBox.setFocusable(false);

		/* Set the initial state of the checkbox. */
		mCheckBox.setChecked(aCheckBoxifiedText.getChecked());
		addView(mCheckBox,  new LinearLayout.LayoutParams(
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
		
		/* At first, add the CheckBox to ourself
		 * (! we are extending LinearLayout) */
		LinearLayout otro = new LinearLayout(context);
		addView(otro,new LinearLayout.LayoutParams(
				LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
		otro.setOrientation(VERTICAL);

		mTextTitle = new TextView(context);
		mTextTitle.setText(aCheckBoxifiedText.getTextTitle());
		mTextTitle.setTextSize(0,16);
		//mText.setPadding(0, 0, 15, 0);
		otro.addView(mTextTitle, new LinearLayout.LayoutParams(
				LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
				
		mTextSubtitle = new TextView(context);
		mTextSubtitle.setText(aCheckBoxifiedText.getTextSubtitle());
		mTextSubtitle.setTextSize(0,13);
		mTextSubtitle.setPadding(20, 0, 20, 0); // 5px to the right
		mTextSubtitle.setTypeface(Typeface.DEFAULT, Typeface.ITALIC);
		otro.addView(mTextSubtitle, new LinearLayout.LayoutParams(
				LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));

		mCheckBox.setOnClickListener( new OnClickListener() {
			/**
			 * Check or uncked the current checkbox!
			 */
			@Override
			public void onClick(View v) {
				toggleCheckBoxState();
			}
		});
		
	}

    public void toggleCheckBoxState()
    {
    	setCheckBoxState(!getCheckBoxState());
    }
    
    public boolean getCheckBoxState()
    {
    	return mCheckBox.isChecked();
    }
    
	public void setTextTitle(String words) {
		mTextTitle.setText(words);
	}
	
	public void setTextSubtitle(String words) {
		mTextSubtitle.setText(words);
	}
	
	public void setCheckBoxState(boolean bool)
	{
		//mCheckBox.setChecked(mCheckBoxText.getChecked());
		mCheckBoxText.setChecked(getCheckBoxState());
	}
}