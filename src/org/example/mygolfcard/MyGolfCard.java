package org.example.mygolfcard;

import org.example.mygolfcard.RestClient.RequestMethod;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.Toast;

public class MyGolfCard extends Activity  implements OnClickListener {

	private static String URL;
	private String auth_token;
	private String auth_user_id;
	private String auth_error_code;
	private boolean connectionOK = true;
	
	static public String resWebService;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        URL = getString(R.string.URL_APIS) + getString(R.string.ACTION_AUTH);
        
        connectionOK = Authentication.checkConnection(MyGolfCard.this);
        if (connectionOK) {
        	Authentication.deleteAuth(MyGolfCard.this);
        }
        
        SharedPreferences.Editor editor = getPreferences(0).edit();
        editor.clear();
        editor.commit();
        
        Authentication.deleteMatches(MyGolfCard.this);
        
        // Set up click listeners for all the buttons
		View continueButton = findViewById(R.id.continue_button);
		continueButton.setOnClickListener(this);

		// Set up click listeners for all the buttons
		View exitButton = findViewById(R.id.exit_button);
		exitButton.setOnClickListener(this);
		
		
    }
    
    // ...
    public void onClick(View v) {
    	String user_login;
    	String user_password;
    	
    	switch (v.getId()) {
	    	case R.id.continue_button:
	    		EditText user = (EditText) findViewById(R.id.login_user);
	    		EditText password = (EditText) findViewById(R.id.login_password);
	    		
	    		user_login = user.getText().toString();
	    		user_password = password.getText().toString();
	    		
	    		if (connectionOK) {
	    			// Si tenemos conexion a Internet, validamos contra el servidor...
	    			InitTask task = new InitTask(user_login, user_password);
	    			task.execute();
	    		}
	    		else {
	    			// ... en caso contrario, pasamos a la sigueinte Activity (que ya se encargará ...
	    			// ... de mirar si el fichero con el usuario existe y puede continuar
	    			
	    			Toast.makeText(MyGolfCard.this, R.string.no_internet,
		                    Toast.LENGTH_SHORT).show();
	    			
	    			Authentication.readDataUser(MyGolfCard.this);
	    			String auth_token = Authentication.getToken();
	    			
	    			if (auth_token.length() > 0) {
		    			Intent i = new Intent(this, MenuApp.class);
		        		startActivity(i);
		        		finish();
	    			}
	    			else {
	    				new AlertDialog.Builder(this)
		    				.setIcon(R.drawable.alert_dialog_icon)
		    				.setTitle(R.string.title_file_connection)
		    				.setMessage(R.string.file_connection)
		    				.setPositiveButton(R.string.alert_button_default, null)
		    				.show();
	    			}
	    		}
	    		
	    		//String res = callAuthentication(user_login, user_password);
	    		//parseJSONResponse(resWebService);
	    		//manageAuthentication();
	    		
/*	    		Toast.makeText(MyGolfCard.this, user_login.concat(" // ").concat(user_password).concat(" // ").concat(auth_token),
	                    Toast.LENGTH_SHORT).show();
	    		
*/	    		
	    		break;

	    	case R.id.exit_button:
	    		finish();
	    		break;
    	}
    }

    
    private static String callAuthentication(String pLogin, String pPass) {
    	String response;
    	
	    RestClient client = new RestClient(URL);
	    client.AddParam("login", pLogin);
	    client.AddParam("password", pPass);
	    
	    response = "";
	    try {
	        client.Execute(RequestMethod.POST);
	        response = client.getResponse();
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	    	 
	    resWebService = response;
	    return response;
    }

	private void parseJSONResponse(String jsonResponse) {
		JSONObject json;
		
		auth_token = "";
		auth_user_id = "";
		auth_error_code = "";
		try {
			json = new JSONObject(jsonResponse);
			auth_token = json.get("token").toString();
			auth_user_id = json.get("user_id").toString();
			auth_error_code = json.get("error_code").toString();
		} catch (JSONException e) {
			e.printStackTrace();
		}

	}

	private void manageAuthentication() {
		if (Integer.parseInt(auth_error_code)<0) {
			// ERROR DE AUTENTICACIÓN.
			// MOSTRAMOS MENSAJE
			new AlertDialog.Builder(this)
				.setIcon(R.drawable.alert_dialog_icon)
				.setTitle("Error en datos de acceso")
				.setMessage("Los datos introducidos no son correctos.\nSi es un usuario registrado en www.mygolfcard.es, inténtelo de nuevo o recupere en la web su contraseña.\nSi no es usuario registrado, deberá registrarse antes de utilizar esta aplicación.\n")
				.setPositiveButton("Aceptar", null)
				.show();
		}
		else {
    		Intent i = new Intent(this, MenuApp.class);
    		startActivity(i);
    		finish();
		}
	}
	
	private void saveDataUser() {
		if (!Authentication.saveDataUser(MyGolfCard.this,auth_token,auth_user_id))
			finish();
		/*try {
			OutputStreamWriter out=new OutputStreamWriter(openFileOutput("token.txt",MODE_PRIVATE));
			
			out.write(auth_token);
			out.close();
		}
		catch (java.io.FileNotFoundException e) {
			// that's OK, we probably haven't created it yet
		}
		catch (Throwable t) {
			new AlertDialog.Builder(this)
				.setIcon(R.drawable.alert_dialog_icon)
				.setTitle("Error guardando sesión")
				.setMessage("La aplicación no ha podido guardar los datos correspondientes a su sesión. Por favor, vuelva a introducirlos.\n")
				.setPositiveButton("Aceptar", null)
				.show();
			finish();
		}*/
	}
	
	/**
	 * sub-class of AsyncTask
	 */
	protected class InitTask extends AsyncTask<Context, Integer, String>
	{
		private String user;
		private String pass;
		private ProgressDialog dialog;
		
		public InitTask (String pUser, String pPass) {
			this.user = pUser;
			this.pass = pPass;
		}
		
		// -- run intensive processes here
		// -- notice that the datatype of the first param in the class definition matches the param passed to this method 
		// -- and that the datatype of the last param in the class definition matches the return type of this mehtod
		@Override
		protected String doInBackground( Context... params ) 
		{
			return callAuthentication(this.user, this.pass);
		}

		// -- gets called just before thread begins
		@Override
		protected void onPreExecute() 
		{
			Log.i( "makemachine", "onPreExecute()" );
			super.onPreExecute();
			this.dialog = ProgressDialog.show(MyGolfCard.this, "Autenticación", "Realizando llamada al servidor para comprobar sus datos de acceso.", true);
		}

		// -- called from the publish progress 
		// -- notice that the datatype of the second param gets passed to this method
		@Override
		protected void onProgressUpdate(Integer... values) 
		{
			super.onProgressUpdate(values);
			Log.i( "makemachine", "onProgressUpdate(): " +  String.valueOf( values[0] ) );
			//_percentField.setText( ( values[0] * 2 ) + "%");
			//_percentField.setTextSize( values[0] );
		}

		// -- called as soon as doInBackground method completes
		// -- notice that the third param gets passed to this method
		@Override
		protected void onPostExecute( String result ) 
		{
			super.onPostExecute(result);
			Log.i( "makemachine", "onPostExecute(): " + result );
			parseJSONResponse(result);
			this.dialog.cancel();
    		manageAuthentication();
    		saveDataUser();
		}
	}   
}