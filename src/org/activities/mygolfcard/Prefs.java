package org.activities.mygolfcard;

import android.preference.PreferenceActivity;
import android.os.Bundle;

public class Prefs extends PreferenceActivity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.settings);
	}
}
