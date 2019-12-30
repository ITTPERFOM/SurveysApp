package com.timetracker.business;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import org.json.JSONObject;

import java.io.IOException;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import com.timetracker.business.OkHttpMethods;

public class ConnectionMethods {


    public static final String WebServiceURL  = "https://formaxws.com/api";
	//public static final String WebServiceURL  = "http://61a92187e7d34e979136e269e9067b8f.cloudapp.net/"; // Stage
	//public static final String WebServiceURL  = "http://192.168.2.138/api"; // Erick
    //public static final String WebServiceURL  = "http://192.168.2.119/api"; //Alex
    public static String AutToken = "84,96,144,168,153,0,59,242,120,199,144,75,248,58,171,167,154,165,133,59,147,62,134,138,79,172,251,200,106,16,33,135,23,73,15,54,65,32,241,194,134,13,104,6,59,199,57,73,134,45,69,109,159,27,50,28,75,153,211,185,10,245,84,134,190,29,90,16,202,207,181,97,250,124,226,25,188,173,241,130,63,225,234,153,150,31,216,182,24,1,223,60,74,114,193,237,130,16,70,11,93,248,173,17,110,28,149,156,10,208,232,54,254,115,170,150,234,118,30,253,163,69,172,162,101,90,180,82";

	//================================================================================
    // Check Internet Connection
    //================================================================================

	public static String isInternetConnected(Context context, Boolean checkWalledGarden)
    {
    	ConnectivityManager connMgr = (ConnectivityManager)context.getSystemService(Activity.CONNECTIVITY_SERVICE);
        @SuppressLint("MissingPermission") NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()){
        	if(!checkWalledGarden){
        		return "";
        	}
        	if(isWalledGardenConnection()){
        		return "";
        	}else{
        		return "Error: Conexion a internet bloqueada";
        	}
        }
        else{
        	return "Error: Sin conexion a internet";
        }
    }

	//================================================================================
    // Check Internet Restrictions
    //================================================================================

	public static boolean isWalledGardenConnection() {
	    HttpURLConnection urlConnection = null;
	    try {
	        URL url = new URL("http://clients3.google.com/generate_204");
	        urlConnection = (HttpURLConnection) url.openConnection();
	        urlConnection.setInstanceFollowRedirects(false);
	        urlConnection.setConnectTimeout(10000);
	        urlConnection.setReadTimeout(10000);
	        urlConnection.setUseCaches(false);
	        urlConnection.getInputStream();
	        Boolean result = false;
	        if(urlConnection.getResponseCode() == 204){
	        	result = true;
	        }else{
	        	result = false;
	        }
	        if (urlConnection != null) {
	        	urlConnection.disconnect();
	        }
	        return result;
	    } catch (IOException e) {
	        return false;
	    } finally {

	    }
	}

	//================================================================================
    // Connection Post Method
    //================================================================================

	public static String Post(Context context, String item, String url, boolean longTimeout){
		int milliseconds = 30000;
		if(longTimeout){
			milliseconds = 600000;
		}
		String resultInternet = isInternetConnected(context,true);
		if(!resultInternet.equals("")){
			return resultInternet;
		}
		String result = "";
		InputStream inputStream = null;
		try{
			URL vurl = new URL(WebServiceURL + url);
			HttpURLConnection connection = (HttpURLConnection)vurl.openConnection();
			connection.setReadTimeout(milliseconds);
			connection.setConnectTimeout(milliseconds);
			connection.setRequestMethod("POST");
			connection.setDoInput(true);
			connection.setDoOutput(true);
			connection.addRequestProperty("Accept", "application/json");
			connection.addRequestProperty("Content-type", "application/json");
			connection.addRequestProperty("Authorization-Token", AutToken);

			OutputStream out = new BufferedOutputStream(connection.getOutputStream());
			BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(out, "UTF-8"));
			writer.write(item);
			writer.close();
			out.close();
			System.setProperty("http.keepAlive", "false");
			connection.connect();
			if(connection.getResponseCode() == 200){
				inputStream = connection.getInputStream();
				result = convertInputStreamToString(inputStream);
			}else{
				result = "Error: No se pudo realizar conexion a servicio, codigo:" + connection.getResponseCode();
			}
			if (connection != null) {
				connection.disconnect();
			}
		}catch (Exception e) {
			result = "Error: " + e.getLocalizedMessage();
		}
		return result;
	}

	public static final MediaType JSON = MediaType.get("application/json; charset=utf-8");

	OkHttpClient client = new OkHttpClient();

	String post(String url, String json) throws IOException {
		RequestBody body = RequestBody.create(JSON, json);
		Request request = new Request.Builder()
				.url(url)
				.post(body)
				.build();
		try (Response response = client.newCall(request).execute()) {
			return response.body().string();
		}
	}

	//================================================================================
    // Connection Get Method
    //================================================================================

	public static String GET(Context context, String url){
		String resultInternet = isInternetConnected(context,true);
		if(!resultInternet.equals("")){
			return resultInternet;
		}
	    InputStream inputStream = null;
	    String result = "";
	    try {
	    	URL vurl = new URL(WebServiceURL + url);
        	HttpURLConnection connection = (HttpURLConnection)vurl.openConnection();
        	connection.setRequestMethod("GET");
        	connection.addRequestProperty("Authorization-Token", AutToken);
        	connection.connect();
        	if(connection.getResponseCode() == 200){
        		inputStream = connection.getInputStream();
    	        if(inputStream != null){
    	        	result = convertInputStreamToString(inputStream);
    	        }
    	        else{
    	        	result = "Error: No se pudo Realizar Conexion GET";
    	        }
        	}else{
        		result = "Error: No se pudo realizar conexion a servicio, codigo:" + connection.getResponseCode();
        	}
	        if (connection != null) {
		    	connection.disconnect();
	        }
        } catch (Exception e) {
        	result = "Error: " + e.getLocalizedMessage();
	    }
	    return result;
	}

	private static String convertInputStreamToString(InputStream inputStream) throws IOException{
        BufferedReader bufferedReader = new BufferedReader( new InputStreamReader(inputStream));
        String line = "";
        String result = "";
        while((line = bufferedReader.readLine()) != null){
        	result += line;
        }
        inputStream.close();
        return result;
    }


}
