package org.example.mygolfcard;

import org.example.mygolfcard.RestClient.RequestMethod;
import org.json.*;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.Toast;

public class MyGolfCard extends Activity  implements OnClickListener {

	String LOGIN_URL = "http://dev.mygolfcard.es/api/authentication";
	String auth_token;
	String auth_error_code;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
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
	    		
	    		String res = callAuthentication(user_login, user_password);
	    		parseJSONResponse(res);
	    		manageAuthentication();
	    		
/*	    		Toast.makeText(MyGolfCard.this, user_login.concat(" // ").concat(user_password).concat(" // ").concat(auth_token),
	                    Toast.LENGTH_SHORT).show();
	    		
*/	    		
	    		break;

	    	case R.id.exit_button:
	    		finish();
	    		break;
    	}
    }

    private String callAuthentication(String pLogin, String pPass) {
    	String response;
    	
	    RestClient client = new RestClient(LOGIN_URL);
	    client.AddParam("login", pLogin);
	    client.AddParam("password", pPass);
	    
	    response = "";
	    try {
	        client.Execute(RequestMethod.POST);
	        response = client.getResponse();
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	    	    
	    return response;
    }

	private void parseJSONResponse(String jsonResponse) {
		JSONObject json;
		
		auth_token = "";
		auth_error_code = "";
		try {
			json = new JSONObject(jsonResponse);
			auth_token = json.get("token").toString();
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
}