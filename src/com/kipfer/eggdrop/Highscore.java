package com.kipfer.eggdrop;


import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Random;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class Highscore{
	SQLiteDatabase db;
	DbHelper dbHelper;
	InputStream is;
	
	public Highscore(DbHelper Database) {

		dbHelper = Database;
		
	}

	public int getLocalHighScore(){
		int _highscore;

		db = dbHelper.getReadableDatabase();
		Cursor answer = db.rawQuery("Select * FROM scores  ORDER BY score DESC",null);
		answer.moveToNext();
		_highscore = answer.getInt(2);
		return _highscore;
	}
	
	public String getId(){
		String _id;
		db = dbHelper.getReadableDatabase();
		Cursor cur = db.rawQuery("Select * FROM scores  ORDER BY score DESC",null);
		cur.moveToNext();
			_id = cur.getString(1) ;
			db.close();
			return _id;
	}
	
	public double getAverage(){
		double _average;
		int played = getNumGames();
		double sum =0;
		db = dbHelper.getReadableDatabase();
		Cursor cur = db.rawQuery("Select * FROM scores  ORDER BY score DESC",null);
		cur.moveToNext();
		for (int i = 0; i < cur.getCount(); i++){
			sum = sum + cur.getInt(2) ;
			cur.moveToNext();
		}
		_average = sum / played;
		db.close();
		return _average;
		
	}
	
	public int getNumGames(){
		int _games;
		db = dbHelper.getReadableDatabase();
		Cursor answer = db.rawQuery("Select * FROM scores  ORDER BY score DESC",null);
		_games = answer.getCount();
		db.close();
	    
		return _games;
	}
	
 public String createId(){
	 String id = "";
	 for(int i=0;i<8;i++){
	 Random r = new Random();
	 int charorint = r.nextInt(2);
	 char c = (char)(r.nextInt(26)+ 'a');
	 int z = r.nextInt(9);
	 if(charorint == 1){
		 id = id.concat(""+c);
	 }else{
		 id = id.concat(""+z);
	 }
		 
	 }
	 boolean isavailable;
	 isavailable = checkId(id);
	 if(!isavailable){
		id = createId();
		return id;
	 }else{
		if(putIntoDatabase(id)){
			return id;
		}else{
		
			
		}
		
	 return id;
 	}
 }
	
public boolean checkId(String id){
		boolean isavail;
	
		
		String result = "";
		// the year data to send
		ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
		nameValuePairs.add(new BasicNameValuePair("id", id));
		try {

			HttpClient httpclient = new DefaultHttpClient();
			HttpPost httppost;
			httppost = new HttpPost("http://www.scottkipferdesign.com/eggdropCheck.php");
			httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
			HttpResponse response = httpclient.execute(httppost);
			HttpEntity entity = response.getEntity();
			is = entity.getContent();
		} catch (Exception e) {
			
		}
		
		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					is, "iso-8859-1"), 8);
			StringBuilder sb = new StringBuilder();
			String line = null;
			while ((line = reader.readLine()) != null) {
				sb.append(line);
			}
			is.close();

			result = sb.toString();
			result = result.replace(" ", "");
		} catch (Exception e) {
		}
		
	
		if(result.equals("false")){
			return true;
		}
		else{
		return false;
		}
	}

	public boolean putIntoDatabase(String id){
		// the year data to send
		String result = "";
		ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
		nameValuePairs.add(new BasicNameValuePair("id", id));
		try {

			HttpClient httpclient = new DefaultHttpClient();
			HttpPost httppost;
			httppost = new HttpPost("http://www.scottkipferdesign.com/eggdropPutId.php");
			httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
			HttpResponse response = httpclient.execute(httppost);
			HttpEntity entity = response.getEntity();
			is = entity.getContent();
		} catch (Exception e) {
			
		}
		
		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					is, "iso-8859-1"), 8);
			StringBuilder sb = new StringBuilder();
			String line = null;
			while ((line = reader.readLine()) != null) {
				sb.append(line);
			}
			is.close();

			result = sb.toString();
			result = result.replace(" ", "");
		} catch (Exception e) {
			
		}
		
		if(result.equals("false")){
			return false;
		}
		else{
		return true;
		}
	}
	public boolean upDateOnlineHighScore(String id, int highscore, double average, int games){
		boolean pass;
		String high = "" + highscore;
		String av = "" + average;
		String gam = "" + games;
		
		ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
		nameValuePairs.add(new BasicNameValuePair("id", id));
		nameValuePairs.add(new BasicNameValuePair("highscore", high));
		nameValuePairs.add(new BasicNameValuePair("games", gam));
		nameValuePairs.add(new BasicNameValuePair("average", av));
		try {

			HttpClient httpclient = new DefaultHttpClient();
			HttpPost httppost;
			httppost = new HttpPost("http://www.scottkipferdesign.com/eggdropHighScore.php");
			httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
			httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
			HttpResponse response = httpclient.execute(httppost);
			HttpEntity entity = response.getEntity();
			is = entity.getContent();
			pass = true;
		} catch (Exception e) {
			pass = false;
		}
		return pass;
	}
	
	public int getPostion(int id){
		int position=0;
		String result = "";
		
		ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
		nameValuePairs.add(new BasicNameValuePair("id", "id"));
		try {

			HttpClient httpclient = new DefaultHttpClient();
			HttpPost httppost;
			httppost = new HttpPost("http://www.scottkipferdesign.com/eggdropPosition.php");
			httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
			HttpResponse response = httpclient.execute(httppost);
			HttpEntity entity = response.getEntity();
			is = entity.getContent();
		
			
		} catch (Exception e) {
		}
		// convert response to string
		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					is, "iso-8859-1"), 8);
			StringBuilder sb = new StringBuilder();
			String line = null;
			while ((line = reader.readLine()) != null) {
				sb.append(line + "\n");
			}
			is.close();

			result = sb.toString();
		} catch (Exception e) {
		}

		// parse json data
		try {
			boolean found = false;
			JSONArray jArray = new JSONArray(result);
			 position = jArray.length();
//			for (int i = 0; i < jArray.length(); i++) {
//				JSONObject json_data = jArray.getJSONObject(i);
//				if(json_data.getString("highscore").equals(""+id) && !found){
//					
//					position = i+1;
//					found = true;
//				}
			//}
		}catch (JSONException e) {
		}
			
		return position;
	}
	
}
