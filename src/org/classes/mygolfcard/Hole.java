package org.classes.mygolfcard;

import org.activities.mygolfcard.Authentication;
import org.activities.mygolfcard.R;
import org.activities.mygolfcard.RestClient;
import org.activities.mygolfcard.RestClient.RequestMethod;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.util.Log;

public class Hole {
	private int hole_id;
	private int holeNumber;
	private int par;
	private int length_yellow;
	private int length_red;
	private int length_white;
	private int length_blue;
	private int handicap;
	private String info_hole;
	
	private static String URL_HOLES;
	
	private static Context ctx;
	
	public Hole(Context ctx2) {
		ctx = ctx2;
		
		URL_HOLES = ctx.getString(R.string.URL_APIS) + ctx.getString(R.string.ACTION_INFO_HOLES);
	}
	
	public int getHoleId() {
		return hole_id;
	}
	
	public void setHoleId(int hole_id) {
		this.hole_id = hole_id;
	}
	
	public int getHoleNumber() {
		return holeNumber;
	}
	
	public void setHoleNumber(int holeNumber) {
		this.holeNumber = holeNumber;
	}
	
	public int getPar() {
		return par;
	}
	
	public void setPar(int par) {
		this.par = par;
	}
	
	public int getLength_yellow() {
		return length_yellow;
	}
	
	public void setLength_yellow(int length_yellow) {
		this.length_yellow = length_yellow;
	}
	
	public int getLength_red() {
		return length_red;
	}
	
	public void setLength_red(int length_red) {
		this.length_red = length_red;
	}
	
	public int getLength_white() {
		return length_white;
	}
	
	public void setLength_white(int length_white) {
		this.length_white = length_white;
	}
	
	public int getLength_blue() {
		return length_blue;
	}
	
	public void setLength_blue(int length_blue) {
		this.length_blue = length_blue;
	}
	
	public int getHandicap() {
		return handicap;
	}
	
	public void setHandicap(int handicap) {
		this.handicap = handicap;
	}
	
	public static String getTextInfo_hole(Hole[] holes, int hole_number) {
		String res = "";
		
		for (int i=0;i<holes.length;i++) {
			if (holes[i].getHoleNumber() == hole_number) {
				res = holes[i].getInfo_hole(); 
			}
		}
		return res;
	}
	
	public String getInfo_hole() {
		return this.info_hole;
	}
	
	public void setInfo_hole(String info_hole) {
		this.info_hole = info_hole;
	}

	public static Hole[] getInfoHoles(int course_id, String auth_token, Context ctx2) {
		String result;
		
		ctx = ctx2;
		
		result = Authentication.readInfoHoles(ctx2);
		
		if (result != "") {
			return setInfoHoles(result, ctx2);
		}
		else {
			return getHoles(course_id, auth_token, ctx2);
		}
	}
	
	public static Hole[] getHoles(int course_id, String auth_token, Context ctx) {
		String response;
		boolean connectionOK;
		
		connectionOK = Authentication.checkConnection(ctx);
		if (connectionOK) {
			Log.i( "strokes", "getting info holes");		
		    RestClient client = new RestClient(URL_HOLES);
		    client.AddParam("token", auth_token);
		    client.AddParam("course_id", "" + course_id);
		    
		    response = "";
		    try {
		        client.Execute(RequestMethod.POST);
		        response = client.getResponse();
		    } catch (Exception e) {
		        e.printStackTrace();
		    }
		    
		    Authentication.saveInfoHoles(ctx, response);
		    
		    Log.i( "strokes", "getting holes " + response.toString());
		    
		    return setInfoHoles(response, ctx);
		}
		else {
			return null;
		}
	}

	private static Hole[] setInfoHoles(String result, Context ctx) {
		JSONObject jsonObj;
		JSONArray  jsonArr;
		Hole[] aux_holes;
		
		try {
			jsonArr = new JSONArray(result);
			
			aux_holes = new Hole[jsonArr.length()];
			
/*			holes_field1 	= new String[jsonArr.length()]; // id hole
			holes_field2 	= new String[jsonArr.length()]; // n hole
			holes_field3 	= new String[jsonArr.length()]; // par hole
			holes_field4 	= new String[jsonArr.length()]; // length yellow hole
			holes_field5 	= new String[jsonArr.length()]; // length red hole
			holes_field6 	= new String[jsonArr.length()]; // length white hole
			holes_field7 	= new String[jsonArr.length()]; // handicap 
			info_holes		= new String[jsonArr.length()+1]; // info display
*/			
			for (int i=0; i<jsonArr.length(); i++) {
				aux_holes[i] = new Hole(ctx);
				
				jsonObj = new JSONObject(jsonArr.get(i).toString());
				
				int number 		= jsonObj.getInt("number");
				
				aux_holes[i].setHoleId(jsonObj.getInt("id"));
				aux_holes[i].setHoleNumber(jsonObj.getInt("number"));
				aux_holes[i].setPar(formatNull(jsonObj.getString("par"),0));
				aux_holes[i].setLength_yellow(formatNull(jsonObj.getString("length_yellow"),0));
				aux_holes[i].setLength_red(formatNull(jsonObj.getString("length_red"),0));
				aux_holes[i].setLength_white(formatNull(jsonObj.getString("length_white"),0));
				aux_holes[i].setLength_blue(formatNull(jsonObj.getString("length_blue"),0));
				aux_holes[i].setHandicap(formatNull(jsonObj.getString("handicap"),0));
/*				holes_field3[i] = showNull(jsonObj.getString("par"),"---");
				holes_field4[i] = showNull(jsonObj.getString("length_yellow"),"---");
				holes_field5[i] = showNull(jsonObj.getString("length_red"),"---");
				holes_field6[i] = showNull(jsonObj.getString("length_white"),"---");
				holes_field7[i] = showNull(jsonObj.getString("handicap"),"---");
*/				
				aux_holes[i].setInfo_hole("Hoyo " + number + "\nPar : " + aux_holes[i].getPar() + "     Handicap : " +  aux_holes[i].getHandicap() + "\nLong : (R) " +  aux_holes[i].getLength_red() + "  (A) " +  aux_holes[i].getLength_yellow() + "");
				Log.i("JSON", "" + i);
			}
			
		} catch (JSONException e) {
			e.printStackTrace();
			
			aux_holes = null;
		}
		
		return aux_holes;
	}

	private static int formatNull(String value, int def)
	{
		int res = 0;
		
		if (value == null) {
			res = def;
		}
		else {
			if (value.equals("null")) {
				res = def;
			}
			else {
				res = Integer.parseInt(value);
			}
		}
		
		return res;
	}


}
