package com.timetracker.surveys;

import java.net.URLEncoder;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import com.timetracker.business.ConnectionMethods;
import com.timetracker.business.DialogMethods;
import com.timetracker.data.Biometrics;
import com.timetracker.data.Devices;
import com.timetracker.sqlite.MySQLiteHelper;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

public class SelectBiometricActivity extends Activity {

	private MySQLiteHelper db = new MySQLiteHelper(SelectBiometricActivity.this);
	private ProgressDialog progressDialog;
	private int DeviceID = 0;
	private LinearLayout lyButtons;
	private boolean UsesKioskMode = false;

	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_select_biometric);
		lyButtons = (LinearLayout) findViewById(R.id.Buttons);
		DialogInterface.OnClickListener onClickListener = new DialogInterface.OnClickListener(){
	        @Override
	        public void onClick(DialogInterface dialog, int which) {
        		dialog.dismiss();
	        	Intent intent = new Intent(getBaseContext(),com.timetracker.surveys.HomeActivity.class);
	        	startActivity(intent);
	        	finish();
	        }
	    };
		Devices Device = db.GetDevice();
		if(Device == null || Device.Status != 2){
			DialogMethods.showInformationDialog(SelectBiometricActivity.this, "Dispositivo sin registrar", "Dispositivo sin registrar. Favor de registrarlo para poder continuar.",onClickListener);
			return;
		}
		if(Device.UsesKioskMode == 1){
			UsesKioskMode = true;
		}
		DeviceID = Device.DeviceID;
		if (!ConnectionMethods.isInternetConnected(SelectBiometricActivity.this, false).equals("")){
			DialogMethods.showInformationDialog(SelectBiometricActivity.this, "Sin Conexion", "No se detecto una conexion a internet. Favor de conectarse a una red antes de volver a intentar.",onClickListener);
			return;
		}
		if (ContextCompat.checkSelfPermission(SelectBiometricActivity.this,Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
		    int isPermited = 0;
            ActivityCompat.requestPermissions(SelectBiometricActivity.this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},isPermited);
            return;
		}
		List<Biometrics> Biometrics = db.getBiometricsByName(); 
		if(Biometrics != null && Biometrics.size() > 0){
			for(Biometrics Biometric: Biometrics){
				final int BiometricID = Biometric.BiometricID;
				final String Name = Biometric.Name;
				Button newButton = new Button(SelectBiometricActivity.this);
				newButton.setText(Name);
				newButton.setBackgroundDrawable(getResources().getDrawable(R.drawable.btn_start));
				newButton.setTextAppearance(getApplicationContext(), R.style.button_text);
				LinearLayout.LayoutParams tdf = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
				tdf.setMargins(0, 10, 0, 10);
				newButton.setLayoutParams(tdf);
				newButton.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						BiometriMethods(1, BiometricID, Name);
					}
				});
				LinearLayout linLayout = new LinearLayout(SelectBiometricActivity.this);
				linLayout.addView(newButton);
				lyButtons.addView(linLayout);
			}
			lyButtons.setVisibility(View.VISIBLE);
		}
	}

	@Override
	public void onDestroy() {
	    super.onDestroy();
	    db.close();
	}
	
	//================================================================================
    // Search Biometric
    //================================================================================
	
	public void SearchBiometric(View view){
		View Currentview = this.getCurrentFocus();
		if (Currentview != null) {  
		    InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
		    imm.hideSoftInputFromWindow(Currentview.getWindowToken(), 0);
		}
		showSpinner("Buscando Empleados");
		EditText txtBiometric = (EditText) findViewById(R.id.txtBiometric);
		String Name = "";
		try{
			Name = txtBiometric.getText().toString();
			Name = Name.trim();
			Name = URLEncoder.encode(Name, "utf-8");
		}catch (Exception ex) {
		}
		lyButtons.removeAllViews();
		new AsyncSearchClient().execute("/Biometrics?Name=" + Name + "&DeviceID=" + DeviceID);
	}
	
	private class AsyncSearchClient extends AsyncTask<String, Void, String> {
		String url;
	   @Override
	   	protected String doInBackground(String... urls) {
	   		url = urls[0];
		   return ConnectionMethods.GET(SelectBiometricActivity.this, urls[0]);
	   }
	   @SuppressWarnings("deprecation")
	   @Override
	   	protected void onPostExecute(String result) {
		   if(result.startsWith("Error:")){
			   DialogMethods.showInformationDialog(SelectBiometricActivity.this, "Ocurrio un Error", result + "\n URL:" + url,null);
		   }else{
			   try 
	            {
				   JSONArray JR = new JSONArray(result);
				   if(JR.length() > 0){
					   for(int i=0;i < JR.length();i++)
					   {
						   JSONObject JO = (JSONObject)JR.getJSONObject(i);
						   final int BiometricID = JO.getInt("BiometricID");
						   final String Name = JO.getString("Name");
						   final int FaceRegister = JO.getInt("FaceRegister");
						   Button newButton = new Button(SelectBiometricActivity.this);
						   newButton.setText(Name);
						   newButton.setBackgroundDrawable(getResources().getDrawable(R.drawable.btn_start));
						   newButton.setTextAppearance(getApplicationContext(), R.style.button_text);
						   LinearLayout.LayoutParams tdf = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
						   tdf.setMargins(0, 10, 0, 10);
						   newButton.setLayoutParams(tdf);
						   newButton.setOnClickListener(new View.OnClickListener() {
							   @Override
							   public void onClick(View v) {
								   BiometriMethods(FaceRegister, BiometricID, Name);
							   }
						   });
						   LinearLayout linLayout = new LinearLayout(SelectBiometricActivity.this);
						   linLayout.addView(newButton);
						   lyButtons.addView(linLayout);
					   }
					   lyButtons.setVisibility(View.VISIBLE);
				   }
				} 
	            catch (Exception ex) {
	            	DialogMethods.showErrorDialog(SelectBiometricActivity.this, "Ocurrio un error al momento de utilizar Ubicheck. Info: " + ex.toString(),"Activity:UbicheckNew | Method:AsyncSearchClient | Error:" + ex.toString());
				}
		   	}
		   hideSpinner();
   		}
    }
	
	//================================================================================
    // Update Biometric
    //================================================================================
	
	private class AsyncUpdateBiometric extends AsyncTask<String, Void, String> {
   		@Override
	   	protected String doInBackground(String... urls) {
		   return ConnectionMethods.GET(SelectBiometricActivity.this, urls[0]);
   		}
   		@Override
   		protected void onPostExecute(String result) {
   			hideSpinner();
   			try 
   			{
   				if(UsesKioskMode){
   					Intent intent = new Intent(getBaseContext(),com.timetracker.surveys.UbicheckActivity.class);
   	   				startActivity(intent);
   	   				finish();
   				}else{
   					Intent intent = new Intent(getBaseContext(),com.timetracker.surveys.RegisterActivity.class);
   	   				startActivity(intent);
   	   				finish();
   				}
			} 
   			catch (Exception e) {
   				showToast(e.toString());
   			}
   		}
    }
	
	//================================================================================
    // Main Methods
    //================================================================================
	
	public void BiometriMethods(int FaceRegister,int BiometricID,String Name){
		showSpinner("Buscando empleado");
		Devices Device = db.GetDevice();
		Device.BiometricID = BiometricID;
		db.updateDevice(Device);
		if(UsesKioskMode){
			List<Biometrics> Biometrics = db.getBiometricsByLastConnection();
			if(Biometrics != null && Biometrics.size() > 7){
				for(Biometrics Biometric: Biometrics){
					db.deleteBiometricByID(Biometric.BiometricID);
					break;
				}
			}
			int currentTime = (int)System.currentTimeMillis();
			db.addBiometric(BiometricID, Name, currentTime);
			Intent intent = new Intent(getBaseContext(),com.timetracker.surveys.UbicheckActivity.class);
			startActivity(intent);
			finish();
		}else{
			Intent intent = new Intent(getBaseContext(),com.timetracker.surveys.RegisterActivity.class);
			startActivity(intent);
			finish();
		}
	}
	
	private void showToast(final String toast) {
		runOnUiThread(new Runnable() {
			public void run() {
				Toast.makeText(SelectBiometricActivity.this, toast, Toast.LENGTH_LONG).show();
			}
		});
	}
	
	private void showSpinner(String Message) {
		progressDialog = new ProgressDialog(this);
		progressDialog.setMessage(Message);
		progressDialog.setCancelable(false);
		progressDialog.show();
	}
	
	private void hideSpinner() {
		runOnUiThread(new Runnable() {
			public void run() {
				if (progressDialog !=null)
					progressDialog.cancel();
			}
		});
	}
	
	//================================================================================
    // Menu Options
    //================================================================================

	public void SendHome(View view){
		if(UsesKioskMode){
			Intent intent = new Intent(getBaseContext(),com.timetracker.surveys.HomeActivity.class);
			startActivity(intent);
			finish();
		}else{
			Intent intent = new Intent(getBaseContext(),com.timetracker.surveys.RegisterActivity.class);
			startActivity(intent);
			finish();
		}
	}
}
