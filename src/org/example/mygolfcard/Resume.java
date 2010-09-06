package org.example.mygolfcard;

import android.app.Activity;
import android.os.Bundle;
import android.text.Html;
import android.widget.TextView;

public class Resume extends Activity {
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.resume);
        
        TextView t3 = (TextView) findViewById(R.id.resume_content);
        t3.setText(Html.fromHtml("<b>Bea (HCP : 36)</b> : 45<br><b>Eladio (HCP : 36)</b> : 46<br><b>Juan (HCP : 36)</b> : 48<br><b>Merche (HCP : 36)</b> : 50"));
    }
}
