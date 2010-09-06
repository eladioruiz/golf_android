package org.example.mygolfcard;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

import android.app.AlertDialog;
import android.content.Context;

public class Authentication {
	
	private static String auth_token; 
	
	public static boolean saveToken(Context ctx, String token) {
		
		try {
			OutputStreamWriter out=new OutputStreamWriter(ctx.openFileOutput("token.txt",0));
			auth_token = token;
			
			out.write(token);
			out.close();
			
			return true;
		}
		catch (java.io.FileNotFoundException e) {
			// that's OK, we probably haven't created it yet
			return false;
		}
		catch (Throwable t) {
			new AlertDialog.Builder(ctx)
				.setIcon(R.drawable.alert_dialog_icon)
				.setTitle("Error guardando sesión")
				.setMessage("La aplicación no ha podido guardar los datos correspondientes a su sesión. Por favor, vuelva a introducirlos.\n")
				.setPositiveButton("Aceptar", null)
				.show();
			return false;
		}
	}
	
	public static void readToken(Context ctx)
	{
		auth_token = "";
		try {
			InputStream in = ctx.openFileInput("token.txt");
			if (in!=null) {
				BufferedReader reader=new BufferedReader(new InputStreamReader(in));
				String str;
				StringBuffer buf=new StringBuffer();
				while ((str = reader.readLine()) != null) {
					buf.append(str+"\n");
				}
				in.close();
				auth_token = buf.toString();
			}
		}
		catch (java.io.FileNotFoundException e) {
			// that's OK, we probably haven't created it yet
		}
		catch (Throwable t) {
			
		}
	}
	
	public static String getToken() {
		return auth_token;
	}
}
