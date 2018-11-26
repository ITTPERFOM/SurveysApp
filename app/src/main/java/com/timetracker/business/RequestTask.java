package com.timetracker.business;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import android.os.AsyncTask;

public class RequestTask extends AsyncTask<String, String, String>{
	
	public static String AutToken = "84,96,144,168,153,0,59,242,120,199,144,75,248,58,171,167,154,165,133,59,147,62,134,138,79,172,251,200,106,16,33,135,23,73,15,54,65,32,241,194,134,13,104,6,59,199,57,73,134,45,69,109,159,27,50,28,75,153,211,185,10,245,84,134,190,29,90,16,202,207,181,97,250,124,226,25,188,173,241,130,63,225,234,153,150,31,216,182,24,1,223,60,74,114,193,237,130,16,70,11,93,248,173,17,110,28,149,156,10,208,232,54,254,115,170,150,234,118,30,253,163,69,172,162,101,90,180,82";
	
    @Override
    protected String doInBackground(String... uri) {
        InputStream inputStream = null;
        String responseString = null;
        try {
        	URL vurl = new URL(uri[0]);
        	HttpURLConnection connection = (HttpURLConnection)vurl.openConnection();
        	connection.setRequestMethod("GET");
        	connection.addRequestProperty("Authorization-Token", AutToken);
        	connection.connect();
            if(connection.getResponseCode() == 200){
                responseString = convertInputStreamToString(inputStream);
            } else{
            	responseString = "Ocurrion un error";
            }
        } catch (IOException e) {
        }
        return responseString;
    }

    private static String convertInputStreamToString(InputStream inputStream) throws IOException{
        BufferedReader bufferedReader = new BufferedReader( new InputStreamReader(inputStream));
        String line = "";
        String result = "";
        while((line = bufferedReader.readLine()) != null)
            result += line;
 
        inputStream.close();
        return result;
    }
    
    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
        //Do anything with response..
    }
}