package net.minewriter;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

public class Upload extends Thread {

	/**
	 * Uses apaches httpclient
	 * @param jso
	 * @throws Exception
	 */
	@SuppressWarnings("deprecation")
	public static void postJSON(JSONObject jso) throws Exception{
		HttpClient httpclient= new DefaultHttpClient();
	    HttpPost httppost= new HttpPost("http://minewriter.net/stat.php");
	    List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);  
	    nameValuePairs.add(new BasicNameValuePair("json", jso.toString()));
	    httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
	    httpclient.execute(httppost);  
		httppost.setEntity(new StringEntity(jso.toString(),"application/json","UTF-8"));
	}
}
