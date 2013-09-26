package com.kipfer.eggdrop;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

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
import org.json.JSONObject;


import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

public class onlineAccess extends AsyncTask<String, Integer, Integer> {
	InputStream is;

	@Override
	protected Integer doInBackground(String... id) {
		String idb = id[0];
		int position= 0;
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
			int check = 1;
			publishProgress(check);
			return check;
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
			for (int i = 0; i < jArray.length(); i++) {
				JSONObject json_data = jArray.getJSONObject(i);
				if(json_data.getString("highscore").equals(""+idb) && !found){
					
					position = (i+1);
					found = true;
				}
			}
		}catch (JSONException e) {
		}
		
			
		return position;
	}

	@Override
	protected void onProgressUpdate(Integer... values) {
		// TODO Auto-generated method stub
		super.onProgressUpdate(values);
		if(values[0] == 1){
		Context context = null;
		context.getApplicationContext();
		Toast.makeText(context, "There is no Internet connection.  Please make sure you have Data or WiFi enabled.", Toast.LENGTH_LONG).show();
	}}
		
	
	
	
}