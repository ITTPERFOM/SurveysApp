package com.timetracker.surveys;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import org.json.JSONObject;
import com.timetracker.surveys.R;
import com.timetracker.business.ConnectionMethods;
import com.timetracker.business.GPSTracker;
import com.timetracker.data.Devices;
import com.timetracker.data.Tracker;
import com.timetracker.sqlite.MySQLiteHelper;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class LoginActivity extends Activity {

	private EditText editTextEmail;
	private ProgressDialog progressDialog;
	private MySQLiteHelper db = new MySQLiteHelper(LoginActivity.this);
	private Button btnStartSurvey;
	private Button btnSelectSurvey;
	GPSTracker gps;
	private int sendOption = 1;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		btnStartSurvey = (Button) findViewById(R.id.btnStartSurvey);
		btnSelectSurvey = (Button) findViewById(R.id.btnSelectSurvey);
		btnStartSurvey.setEnabled(false);
		btnSelectSurvey.setEnabled(false);
		Devices Device = db.GetDevice();
		if(Device == null){
			Intent i = new Intent(LoginActivity.this, RegisterActivity.class);
			startActivity(i);
			finish();
		}else if(Device.Status != 2){
			if(isConnected()){
				new AsyncCheckDevice(false).execute("/Devices/" + Device.DeviceID);
			}
		}else {
			if(Device.ImageWareRegister == 0){
				btnStartSurvey.setEnabled(true);
			}else{
				btnSelectSurvey.setEnabled(true);
			}
		}
		
		
		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
		editTextEmail = (EditText) findViewById(R.id.editTextEmail);

	}

	// ================================================================================
	// Validate User
	// ================================================================================
	
	public void authenticateUser(View view) {
		Devices Device = db.GetDevice();
		showSpinner();
		sendOption = 1;
	}

	// ================================================================================
	// Register New user
	// ================================================================================
	
	public void registerUser(View view) {
		Devices Device = db.GetDevice();
		showSpinner();
		if(Device.BiometricID == 0){
			AsyncBiometricCreate AsyncBiometricCreate = new AsyncBiometricCreate(Device.DeviceID,Device.Name);
			AsyncBiometricCreate.execute("/Biometrics/");
		}else{
		}
	}
	
	// ================================================================================
	// Async Methods
	// ================================================================================
	
	private class AsyncCheckDevice extends AsyncTask<String, Void, String> {
   		Boolean IsCheck;
        public AsyncCheckDevice(Boolean sIsChecked) {
        	this.IsCheck=sIsChecked;
		}
	   @Override
	   	protected String doInBackground(String... urls) {
		   return ConnectionMethods.GET(LoginActivity.this,urls[0]);
	   }
	   @Override
	   	protected void onPostExecute(String result) {
            try 
            {
            	if(IsCheck){
            		if(result.equals("\"0\"") || result.equals("\"3\""))
                	{
            			db.deleteQuestionOptions();
                    	db.deleteQuestionSentences();
                    	db.deleteQuestions();
                    	db.deleteAllSurveys();
                    	db.deleteDevice();
                    	db.deleteSelectedSurveys();
                    	Intent intent = new Intent(getBaseContext(),com.timetracker.surveys.RegisterActivity.class);
                		startActivity(intent);
                		finish();
                	}
            	}else{
            		if(result.equals("\"2\""))
                	{
    	            	Devices Device = db.GetDevice();
                		Device.Status = 2;
                		db.updateDevice(Device);
                		btnStartSurvey.setEnabled(true);
                	}
                	else if(result.equals("\"3\""))
                	{
    	            	Devices Device = db.GetDevice();
                		Device.Status = 3;
                		db.updateDevice(Device);
                		btnStartSurvey.setEnabled(true);
                	}
            	}
			} 
            catch (Exception ex) {
            	showToast("currio un error al momento de checar dispositivo.");
            } 
       }
   }
	
	@SuppressLint("SimpleDateFormat")
	private class AsynTrackerCreate extends AsyncTask<String, Void, String> {
   		Tracker Tracker;
        public AsynTrackerCreate(Tracker Tracker) {
        	this.Tracker=Tracker;
		}
		@Override
        protected String doInBackground(String... params) {
        	String resultado = "0";
        	 try {
        		 JSONObject item = new JSONObject();
        		 item.put("TrackerID", Tracker.TrackerID);
        		 item.put("DeviceID", Tracker.DeviceID);
        		 item.put("Latitude", Tracker.Latitude);
        		 item.put("Longitude", Tracker.Longitude);
        		 item.put("Date", Tracker.Date);
        		 resultado = ConnectionMethods.Post(LoginActivity.this,item.toString(), params[0],false);
	         } catch (Exception e) {
	        	 return "Error: " + e.toString();               
	         }
             return resultado;
        }
        @Override
        protected void onPostExecute(String result) {
        	hideSpinner();
        	if(result.equals("\"1\""))
        	{
        		db.deleteTrackersByID(Tracker.TrackerID);
        		showToast("Ubicacion enviada");
        		
        	}
        	else
        	{
        		showToast("Error a enviar ubicacion:" + result);
        	}
       }
    }
	
	private class AsyncBiometricCreate extends AsyncTask<String, Void, String> {
    	int DeviceID;
    	String Name;
        public AsyncBiometricCreate(int DeviceID, String Name) {
        	this.DeviceID = DeviceID;
        	this.Name = Name;
		}
		@Override
        protected String doInBackground(String... params) {
        	String resultado = "0";
        	 try {
				JSONObject item = new JSONObject();
				item.put("DeviceID", DeviceID);
				item.put("Name", Integer.toString(DeviceID));
	            resultado = ConnectionMethods.Post(LoginActivity.this,item.toString(), params[0],false);
	         } catch (Exception e) {
	        	 resultado = "0";              
	         }
             return resultado;
        }
        @Override
        protected void onPostExecute(String result) {
        	if(!result.startsWith("\"Error:\""))
        	{
        		try{
            		Devices Device = db.GetDevice();
            		Device.BiometricID = Integer.parseInt(result.replace("\"", ""));
            		db.updateDevice(Device);
            		Toast.makeText(getBaseContext(), "Biometrico Registrado en Formax", Toast.LENGTH_LONG).show();
        		}catch (Exception e) {
        			Toast.makeText(LoginActivity.this, "Ocurrio un error:" + e.toString(), Toast.LENGTH_LONG).show();  
        			hideSpinner();
        		}
        	}
        	else
        	{
        		hideSpinner();
        		Toast.makeText(getBaseContext(), "Ocurrio un error al enviar:" + result, Toast.LENGTH_LONG).show();
        	}
       }
    }
	
	public boolean isConnected(){
        if (ConnectionMethods.isInternetConnected(LoginActivity.this,false).equals("")){
            return true;
        }
        else {
            return false;  
        } 
    }
	
	//================================================================================
    // Menu Options
    //================================================================================

	public void RegisterDevices(View view){
	   Intent intent = new Intent(getBaseContext(),com.timetracker.surveys.RegisterActivity.class);
	   startActivity(intent);
	   finish();
	}

	public void SendHome(View view){
		
	}
	
	public void SendSettings(View view){
		Devices Device = db.GetDevice();
		if(Device != null && Device.BiometricID != 0 && Device.ImageWareRegister != 0){
			showSpinner();
			sendOption = 2;
		}else{
			showToast("Registre dispositivo para continuar");
		}
   }
	
	// Show a Toast message on UI thread
	private void showToast(final String toast) {
		runOnUiThread(new Runnable() {
			public void run() {
				Toast.makeText(LoginActivity.this, toast, Toast.LENGTH_LONG).show();
			}
		});
	}
	
	// Show progress spinner
	private void showSpinner() {
		progressDialog = new ProgressDialog(this);
		progressDialog.setMessage("Espere...");
		progressDialog.setCancelable(false);
		progressDialog.show();
	}
	
	// Hide progress spinner
	private void hideSpinner() {
		runOnUiThread(new Runnable() {
			public void run() {
				if (progressDialog !=null)
					progressDialog.cancel();
			}
		});
		
	}
	
	private void ShowControls() {
		runOnUiThread(new Runnable() {
			public void run() {
				btnStartSurvey.setEnabled(false);
				btnSelectSurvey.setEnabled(true);
			}
		});
		
	}

}
