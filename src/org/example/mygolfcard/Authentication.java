package org.example.mygolfcard;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.*;

import android.app.AlertDialog;
import android.content.Context;

public class Authentication {
	
	private static String auth_token; 
	private static String auth_user_id;
	
	public static boolean saveDataUser(Context ctx, String token, String user_id) {
		
		try {
			OutputStreamWriter out=new OutputStreamWriter(ctx.openFileOutput("user.txt",0));
			auth_token 	 = token;
			auth_user_id = user_id;
			
			out.write(auth_token  + '\n');
			out.write(auth_user_id  + '\n');
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
	
	public static void readDataUser(Context ctx)
	{
		int i = 0;
		
		auth_token 		= "";
		auth_user_id 	= "";
		try {
			InputStream in = ctx.openFileInput("user.txt");
			if (in!=null) {
				BufferedReader reader=new BufferedReader(new InputStreamReader(in));
				String str;
				StringBuffer buf=new StringBuffer();
				while ((str = reader.readLine()) != null) {
					buf.append(str+"\n");
					
					if (i==0)
						auth_token = str;
					else if (i==1)
						auth_user_id = str;
					
					++i;
				}
				in.close();
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
	
	public static String getUserId() {
		return auth_user_id;
	}
	
	public static void deleteAuth(Context ctx) {
		ctx.deleteFile("user.txt");
	}
}

