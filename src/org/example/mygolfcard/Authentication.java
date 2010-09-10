package org.example.mygolfcard;

import java.io.*;

import android.app.AlertDialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.NetworkInfo.State; 
import android.widget.Toast;

public class Authentication {
	
	private static String auth_token; 
	private static String auth_user_id;
	
	public static boolean checkConnection(Context ctx){
		boolean bTieneConexion = false;
		ConnectivityManager connec =  (ConnectivityManager)ctx.getSystemService(Context.CONNECTIVITY_SERVICE);

		//Con esto recogemos todas las redes que tiene el móvil (wifi, gprs...)
		NetworkInfo[] redes = connec.getAllNetworkInfo();

		for(int i=0; i<2; i++){
			//Si alguna tiene conexión ponemos el boolean a true
			if (redes[i].getState() == NetworkInfo.State.CONNECTED){
				bTieneConexion = true;
			}
		}

		//Si el boolean sigue a false significa que no hay red disponible
		if(!bTieneConexion){
			//Mostramos un error indicando al usuario que no tiene conexión a Internet
			Toast.makeText(ctx, "No tiene conexión a Internet.",
	                Toast.LENGTH_SHORT).show();
		}
//bTieneConexion = false;
		return bTieneConexion;
	}

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
		deleteFile(ctx,"user.txt");
	}
	
	public static void deleteMatches(Context ctx) {
		deleteFile(ctx,"matches.txt");
	}
	
	public static void deleteCourses(Context ctx) {
		deleteFile(ctx,"courses.txt");
	}
	
	public static boolean saveMatches(Context ctx, String result) {
		deleteMatches(ctx);
		return saveFile(ctx,"matches.txt",result);
	}
	
	public static boolean saveCourses(Context ctx, String result) {
		deleteCourses(ctx);
		return saveFile(ctx,"courses.txt",result);
	}
	
	public static String readMatches(Context ctx) {
		return readFile(ctx,"matches.txt");
	}
	
	public static String readCourses(Context ctx) {
		return readFile(ctx,"courses.txt");
	}
	
	private static String readFile(Context ctx, String fileName) {
		try {
			InputStream in = ctx.openFileInput(fileName);
			if (in!=null) {
				BufferedReader reader=new BufferedReader(new InputStreamReader(in));
				String str;
				StringBuffer buf=new StringBuffer();
				while ((str = reader.readLine()) != null) {
					buf.append(str+"\n");
				}
				in.close();
				
				return buf.toString();
			}
			else {
				return "";
			}
		}
		catch (java.io.FileNotFoundException e) {
			// that's OK, we probably haven't created it yet
			return "";
		}
		catch (Throwable t) {
			return "";
		}
	}
	
	private static boolean saveFile(Context ctx, String fileName, String result) {
		try {
			OutputStreamWriter out=new OutputStreamWriter(ctx.openFileOutput(fileName,0));
			
			out.write(result);
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
				.setTitle("Error guardando datos")
				.setMessage("La aplicación no ha podido guardar los datos correspondientes a su sesión. \n")
				.setPositiveButton("Aceptar", null)
				.show();
			return false;
		}
	}
	
	private static void deleteFile(Context ctx, String fileName) {
		try {
			ctx.deleteFile(fileName);
		}
		catch (Throwable t) {
			//
		}
	}
}

