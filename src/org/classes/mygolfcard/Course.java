/**
 * Package: org.classes.mygolfcard
 * File: Course.java
 * Description:
 * Create At: 20/10/2010
 * Created By: ERL
 * Last Modifications:
 * 
 */
package org.classes.mygolfcard;

import org.activities.mygolfcard.R;
import org.activities.mygolfcard.RestClient;
import org.activities.mygolfcard.RestClient.RequestMethod;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.util.Log;

/**
 * @author eladioruiz
 *
 */
public class Course {

	private int course_id;
	private String courseName;
	private String courseAddress;
	private String courseDescription;
	private String courseConfig;
	private String courseDetails;

	private static Context ctxCourse;
	/**
	 * 
	 */
	public Course(Context ctx) {
		// TODO Auto-generated constructor stub
		super();
		
		course_id = 0;
		courseName 			= "";
		courseAddress		= "";
		courseDescription	= "";
		courseConfig		= "";
		courseDetails		= "";
		
		ctxCourse = ctx;
	}

	public int getCourse_id() {
		return course_id;
	}

	public void setCourse_id(int course_id) {
		this.course_id = course_id;
	}

	public String getCourseName() {
		return courseName;
	}

	public void setCourseName(String courseName) {
		this.courseName = courseName;
	}

	public String getCourseAddress() {
		return courseAddress;
	}

	public void setCourseAddress(String courseAddress) {
		this.courseAddress = courseAddress;
	}

	public String getCourseDescription() {
		return courseDescription;
	}

	public void setCourseDescription(String courseDescription) {
		this.courseDescription = courseDescription;
	}

	public String getCourseConfig() {
		return courseConfig;
	}

	public void setCourseConfig(String courseConfig) {
		this.courseConfig = courseConfig;
	}

	public String getCourseDetails() {
		return courseDetails;
	}

	public void setCourseDetails(String courseDetails) {
		this.courseDetails = courseDetails;
	}

	public Course setDataFromRemote(int course_id, int user_id, String token) {
		Course aux = new Course(ctxCourse);
		String result;
		
		result = getCourse(course_id, user_id, token);
		aux = setInfoCourse(result);
		
		return aux;
	}
	
	public static Course[] getCoursesFromRemote(String auth_token, Context ctx) {
		String result;

		ctxCourse = ctx;
		result = getCourses(auth_token);
		return setInfoCourses(result);
	}
	
	public static Course[] getCoursesFromLocal(Context ctx) {
		String result;

		ctxCourse = ctx;
		result = Authentication.readCourses(ctxCourse);
		return setInfoCourses(result);
	}
	
	private String getCourse(int course_id, int user_id, String token) {
		String response;
		String URL_COURSE = ctxCourse.getString(R.string.URL_APIS) + ctxCourse.getString(R.string.ACTION_COURSE);
	    
		RestClient client = new RestClient(URL_COURSE);
	    client.AddParam("token", token);
	    client.AddParam("user_id", "" + user_id);
	    //ERL client.AddParam("course_id", course_id);
	    client.AddParam("course_id", "" + course_id);
	    
	    Log.i("RESPONSE", "" + URL_COURSE);
	    Log.i("RESPONSE", "" + token);
	    Log.i("RESPONSE", "" + user_id);
	    Log.i("RESPONSE", "" + course_id);
	        
	    response = "";
	    try {
	        client.Execute(RequestMethod.POST);
	        response = client.getResponse();
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	    
	    Log.i("RESPONSE", "" + response);
	    return response;
	}

	private Course setInfoCourse(String result) {
		JSONObject json;
		String aux;
		Course res = new Course(ctxCourse);
		
		try {
			json = new JSONObject(result);
			res.setCourseName(json.get("name").toString());
			res.setCourseAddress(json.get("address").toString());
			res.setCourseDescription(json.get("long_description").toString());
					
			aux					=	"Par : " + json.get("handicap").toString() + "\n" +
									"Nº Hoyos : " + json.get("n_holes").toString() + "\n" +
									"Long Amarillas : " + json.get("length_yellow").toString() + "\n" +
									"Long Rojas : " + json.get("length_red").toString() + "\n" +
									"Long Blancas : " + json.get("length_white").toString();
			
			res.setCourseConfig(aux);
			
			aux					=	"Año Fundación : " + json.get("founded").toString() + "\n" +
									"Diseñador : " + json.get("designer").toString() + "\n" +
									"Año Fundación : " + json.get("founded").toString() + "\n" +
									"Habilidad : " + json.get("ability_recommended").toString() + "/10\n" +
									"Mantenimiento : " + json.get("maintance").toString() + "/10\n" +
									"Relieve : " + json.get("relief").toString() + "/10\n" +
									"Viento : " + json.get("wind").toString() + "/10\n" +
									"Agua : " + json.get("water_in_play").toString() + "/10\n" +
									"Árboles : " + json.get("trees_in_play").toString() + "/10\n";
			
			res.setCourseDetails(aux);
			
		} catch (JSONException e) {
			e.printStackTrace();
		}	
		
		return res;
	}
	
	private static String getCourses(String auth_token) {
		String response;
		String URL_COURSES = ctxCourse.getString(R.string.URL_APIS) + ctxCourse.getString(R.string.ACTION_COURSES);
		
	    RestClient client = new RestClient(URL_COURSES);
	    client.AddParam("token", auth_token);
	    
	    response = "";
	    try {
	        client.Execute(RequestMethod.POST);
	        response = client.getResponse();
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	    
	    Authentication.saveCourses(ctxCourse, response);
	    return response;
	}
	
	private static Course[] setInfoCourses(String result) {
		JSONObject jsonObj;
		JSONArray  jsonArr;
		Course coursesList[];
		
		try {
			jsonArr = new JSONArray(result);
			
			coursesList = new org.classes.mygolfcard.Course[jsonArr.length()];
			
			for (int i=0; i<jsonArr.length(); i++) {
				jsonObj = new JSONObject(jsonArr.get(i).toString());
				coursesList[i] = new Course(ctxCourse);
				
				coursesList[i].setCourseName(jsonObj.getString("name"));
				coursesList[i].setCourseAddress(jsonObj.getString("address"));
				coursesList[i].setCourse_id(Integer.parseInt(jsonObj.getString("id")));				
			}
			
			return coursesList;
			
		} catch (JSONException e) {
			e.printStackTrace();
			
			return null;
		}
	}

}
