/**
 * Package: org.activities.mygolfcard
 * File: MyGolfCard.java
 * Description:
 * Create At: ---
 * Created By: ERL
 * Last Modifications:
 * 		20/10/2010 - ERL - POO
 */
package org.activities.mygolfcard;

import org.activities.mygolfcard.RestClient.RequestMethod;
import org.classes.mygolfcard.Authentication;
import org.classes.mygolfcard.User;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.Toast;

public class MyGolfCard extends Activity  implements OnClickListener {

	private static String URL;
	private User cUser = new User();
	private String auth_token;
	private String user_name;
	
	//ERL private String auth_user_id;
	private String auth_error_code;
	private boolean connectionOK = true;
	private static Context ctx = null;
	
	static public String resWebService;


	private SQLiteDatabase db = null;
	private String DATABASE_NAME = "mygolfcard.db";
	
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
		// Leemos el posibles fichero de conexiones anteriores, para recuperar el token...
		// ... que nos sirve de validación
		Authentication.readDataUser(MyGolfCard.this);
		String auth_token = Authentication.getToken();
		
		createDatabase();
		
		// Si dicho token existe, damos el login por correcto y continuamos la ejecución...
		// ... a la siguiente pantalla: el menú de la aplicación
		if (auth_token.length() > 0) {
			Intent i = new Intent(this, MenuApp.class);
    		startActivity(i);
    		
    		// Cerramos la Activity del Login para que no se pueda volver mediante pulsaciones...
    		// ... del botón ATRAS del móvil
    		finish();
		}
		else {
        
	        ctx = MyGolfCard.this;
	        
	        URL = getString(R.string.URL_APIS) + getString(R.string.ACTION_AUTH);
	        
	        // Chequeo de la conexión a Internet
	        connectionOK = Authentication.checkConnection(MyGolfCard.this);
	
	        // Inicializamos las preferencias que se hayan podido quedar guardadas
	        SharedPreferences.Editor editor = getPreferences(0).edit();
	        editor.remove("course");
	        editor.remove("date");
	        editor.remove("hour");
	        editor.remove("holes");
	        editor.clear();
	        editor.commit();
	        editor.clear();
	        editor.commit();
	        
	        
	        setContentView(R.layout.main);
	        
	        // Set up click listeners for all the buttons
			View continueButton = findViewById(R.id.continue_button);
			continueButton.setOnClickListener(this);
			
			// Set up click listeners for all the buttons
			View signupButton = findViewById(R.id.signup_button);
			signupButton.setOnClickListener(this);
		}
    }
      
    // Callback de pulsación sobre los botones
    public void onClick(View v) {
    	String user_login;
    	String user_password;
    	
    	switch (v.getId()) {
	    	case R.id.continue_button:
	    		// ** Botón de Continuar (ENTRAR en la aplicación)
	    		
	    		// Recuperamos los valores de usuario y password...
	    		EditText user 		= (EditText) findViewById(R.id.login_user);
	    		EditText password 	= (EditText) findViewById(R.id.login_password);
	    		
	    		user_login 		= user.getText().toString();
	    		user_password 	= password.getText().toString();
	    		
	    		// ... y si hay conexión a Internet ...
	    		if (connectionOK) {
	    			// ... los validamos contra el servidor ...
	    			// (las conexiones remotas se lanzan siempre en un hilo separado para ...
	    			// ... no entorpecer el resto de funciones del teléfono)
	    			InitTask task = new InitTask(user_login, user_password);
	    			task.execute();
	    		}
	    		else {
	    			// ... en caso contrario, pasamos intentamos recuperar el usuario y password...
	    			// ... de alguna conexión anterior
	    			
	    			// Mensaje advirtiendo que no hay conexión a Internet
	    			Toast.makeText(MyGolfCard.this, R.string.no_internet,
		                    Toast.LENGTH_SHORT).show();
	    			
	    			// Leemos el posibles fichero de conexiones anteriores, para recuperar el token...
	    			// ... que nos sirve de validación
	    			Authentication.readDataUser(MyGolfCard.this);
	    			String auth_token = Authentication.getToken();
	    			
	    			// Si dicho token existe, damos el login por correcto y continuamos la ejecución...
	    			// ... a la siguiente pantalla: el menú de la aplicación
	    			if (auth_token.length() > 0) {
		    			Intent i = new Intent(this, MenuApp.class);
		        		startActivity(i);
		        		
		        		// Cerramos la Activity del Login para que no se pueda volver mediante pulsaciones...
		        		// ... del botón ATRAS del móvil
		        		finish();
	    			}
	    			else {
	    				// No se ha podido recuperar el token y entonces no dejamos continuar hasta ...
	    				// ... que no hay aconexión a internet
	    				new AlertDialog.Builder(this)
		    				.setIcon(R.drawable.alert_dialog_icon)
		    				.setTitle(R.string.title_file_connection)
		    				.setMessage(R.string.file_connection)
		    				.setPositiveButton(R.string.alert_button_default, null)
		    				.show();
	    			}
	    		}
	    		
	    		break;

	    	case R.id.signup_button:
	    		Intent i = new Intent(this, Signup.class);
        		startActivity(i);
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
			new AlertDialog.Builder(ctx)
				.setIcon(R.drawable.alert_dialog_icon)
				.setTitle(R.string.title_file_connection)
				.setMessage(R.string.file_connection)
				.setPositiveButton(R.string.alert_button_default, null)
				.show();
	    }
	    	 
	    resWebService = response;
	    return response;
    }

	private void parseJSONResponse(String jsonResponse) {
		JSONObject json;
		
		try {
			json = new JSONObject(jsonResponse);
			
			cUser.setUser_id(Integer.parseInt(json.get("user_id").toString()));
			cUser.setUserName(json.get("user_name").toString());
			
			auth_token 		= json.get("token").toString();
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
		if (!Authentication.saveDataUser(MyGolfCard.this,auth_token,cUser.getUser_id(),cUser.getUserName()))
			finish();
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
			saveDataUser();
    		manageAuthentication();
		}
	}
	
	private void createDatabase() {
    	/* Create a Database. */
    	try {
    		Log.i("INFO", "CREATING DB");
    		
    		db = this.openOrCreateDatabase(DATABASE_NAME, MODE_PRIVATE, null);
    		
    		/* Create a Table in the Database. */
    		db.execSQL(	"CREATE TABLE IF NOT EXISTS "
    				+	"matches "                     
    				+ 	" (ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
    						"course_id INT(3), date_hour_match VARCHAR, " +
    						"course_name VARCHAR, " +
    						"holes INT(2), status INT(1), " +
    						"player1_id INT(5), tee1 INT(1), " +
    						"player2_id INT(5), tee2 INT(1), " +
    						"player3_id INT(5), tee3 INT(1), " +
    						"player4_id INT(5), tee4 INT(1)); ");
    		
    		db.execSQL(	"CREATE TABLE IF NOT EXISTS "
    				+	"strokes "                     
    				+ 	" (ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
    						"match_id INT(5),  " +
    						"player_id INT(5), " +
    						"hole INT(2), " +
    						"strokes INT(2), putts INT(2), " +
    						"time NOT NULL DEFAULT CURRENT_TIMESTAMP); ");
    	}
    	catch(Exception e) {
    		Log.e("Error", "Error CREATING DB", e);
    	} 
    	finally {
    		if (db != null)
    			db.close();
    	}
    }
}