/**
 * Package: org.activities.mygolfcard
 * File: Authentication.java
 * Description:
 * Create At: ---
 * Created By: ERL
 * Last Modifications:
 * 		20/10/2010 - ERL - POO
 */
package org.classes.mygolfcard;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import org.activities.mygolfcard.R;

import android.app.AlertDialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class Authentication {

	private static String auth_token; 
	private static int auth_user_id;	
	private static String user_name;
	
	public static boolean checkConnection(Context ctx){
		boolean bTieneConexion = false;
		ConnectivityManager connec =  (ConnectivityManager)ctx.getSystemService(Context.CONNECTIVITY_SERVICE);

		//Con esto recogemos todas las redes que tiene el móvil (wifi, gprs...)
		NetworkInfo[] redes = connec.getAllNetworkInfo();

		for(int i=0; i<2; i++){
			//Si alguna tiene conexión ponemos el boolean a true
			if (redes[i].getState() == NetworkInfo.State.CONNECTED){
				if (pingResponding(ctx)) {
					bTieneConexion = true;
				}
			}
		}

/*		//Si el boolean sigue a false significa que no hay red disponible
		if(!bTieneConexion){
			//Mostramos un error indicando al usuario que no tiene conexión a Internet
			Toast.makeText(ctx, R.string.no_internet,
	                Toast.LENGTH_SHORT).show();
		}
*/
		//bTieneConexion = false;
		
		return bTieneConexion;
	}

	public static boolean pingResponding(Context ctx) {
		boolean bRes = true;
/*		
		InetAddress in;
		in=null;
		
		// Definimos la ip de la cual haremos el ping
		try {
			in = InetAddress.getByName("192.168.1.5");
		} 
		catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    
		//Definimos un tiempo en el cual ha de responder
		try {
			if(in.isReachable(5000)) {
				bRes= true;
			}
			else {
				bRes = false;
			}
		} 
		catch (IOException e) {
			bRes = false;
		}
*/
		
		try {
		
			String URL_CONNECTION = ctx.getString(R.string.URL_APIS) + ctx.getString(R.string.ACTION_CONNECTION);
		    URL url = new URL(URL_CONNECTION);
		
			HttpURLConnection urlc = (HttpURLConnection) url.openConnection();
			urlc.setRequestProperty("User-Agent", "Android Application:2.2");
			urlc.setRequestProperty("Connection", "close");
			urlc.setConnectTimeout(1000 * 30); // mTimeout is in seconds
			urlc.connect();
			if (urlc.getResponseCode() == 200) {
			    bRes = false;
			}
		} 
		catch (MalformedURLException e1) {
		    // TODO Auto-generated catch block
			bRes = false;
		} catch (IOException e) {
		    // TODO Auto-generated catch block
	        bRes = false;
		}

		return bRes;
	}
	
	public static boolean saveDataUser(Context ctx, String token, int user_id, String name) {
		
		try {
			OutputStreamWriter out=new OutputStreamWriter(ctx.openFileOutput("user.txt",0));

			auth_token 	 	= token;
			auth_user_id	= user_id;
			user_name	 	= name;
			out.write(auth_token  + '\n');
			out.write("" + auth_user_id  + '\n');
			out.write("" + user_name  + '\n');
			
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
				.setTitle(R.string.alert_title_no_save)
				.setMessage(R.string.no_save)
				.setPositiveButton(R.string.alert_button_default, null)
				.show();
			return false;
		}
	}
	
	public static void readDataUser(Context ctx)
	{
		int i = 0;
		
		auth_token 		= "";
		auth_user_id 	= 0;
		user_name		= "";
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
						auth_user_id = Integer.parseInt(str);
					else if (i==2)
						user_name = str;
					
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
	
	public static int getUserId() {
		return auth_user_id;
	}
	
	public static String getUserName() {
		return user_name;
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
	
	public static void deleteFriends(Context ctx) {
		deleteFile(ctx,"friends.txt");
	}
	
	public static void deleteInfoHoles(Context ctx) {
		deleteFile(ctx,"holes.txt");
	}
	
	public static boolean saveMatches(Context ctx, String result) {
		deleteMatches(ctx);
		return saveFile(ctx,"matches.txt",result);
	}
	
	public static boolean saveCourses(Context ctx, String result) {
		deleteCourses(ctx);
		return saveFile(ctx,"courses.txt",result);
	}
	
	public static boolean saveFriends(Context ctx, String result) {
		deleteFriends(ctx);
		return saveFile(ctx,"friends.txt",result);
	}
	
	public static boolean saveInfoHoles(Context ctx, String result) {
		deleteInfoHoles(ctx);
		return saveFile(ctx,"holes.txt",result);
	}
	
	public static String readMatches(Context ctx) {
		return readFile(ctx,"matches.txt");
	}
	
	public static String readCourses(Context ctx) {
		return readFile(ctx,"courses.txt");
	}
	
	public static String readFriends(Context ctx) {
		return readFile(ctx,"friends.txt");
	}
	
	public static String readInfoHoles(Context ctx) {
		return readFile(ctx,"holes.txt");
	}
	
	public static String readFile(Context ctx, String fileName) {
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
	
	public static boolean saveFile(Context ctx, String fileName, String result) {
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
				.setTitle(R.string.alert_title_no_save)
				.setMessage(R.string.no_save)
				.setPositiveButton(R.string.alert_button_default, null)
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

