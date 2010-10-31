package org.activities.mygolfcard;


import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

public class Checkbox extends ListActivity {
    /** Called when the activity is first created. */
	
	private CheckBoxifiedTextListAdapter cbla;
	// Create CheckBox List Adapter, cbla
	private String[] items = {"Box 1", "Box 2", "Box 3", "Box 4"};
	// Array of string we want to display in our list

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        setContentView(R.layout.test);
        
        cbla = new CheckBoxifiedTextListAdapter(this);
        for(int k=0; k<items.length; k++)
        {
        	cbla.addItem(new CheckBoxifiedText(items[k], "", false));
        }  
        // Display it
        setListAdapter(cbla);
    }
    
    /* Remember the other methods of the CheckBoxifiedTextListAdapter class!!!!
     * cbla.selectAll() :: Check all items in list
     * cbla.deselectAll() :: Uncheck all items
     */

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.menu_synchro, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.synchro_delete:
				getItemsChecked();
				startActivity(new Intent(this, About.class));
				return true;
			case R.id.synchro_upload:
				getItemsChecked();
				startActivity(new Intent(this, About.class));
				return true;
			case R.id.menuapp:
				startActivity(new Intent(this, MenuApp.class));
				finish();
				return true;
		}
		return false;
	}    
	
	private void getItemsChecked() {
		String res = "";
		for (int i=0; i< cbla.getCount(); i++) {
			CheckBoxifiedText obj = (CheckBoxifiedText) cbla.getItem(i);
			res += "" + i + ":" + obj.getChecked();
		}
		
		Toast.makeText(Checkbox.this, res,
                Toast.LENGTH_SHORT).show();
	}
    
}