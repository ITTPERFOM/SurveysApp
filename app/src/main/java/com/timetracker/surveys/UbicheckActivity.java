package com.timetracker.surveys;

import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.timetracker.business.ConnectionMethods;
import com.timetracker.business.DialogMethods;
import com.timetracker.business.GPSTracker;
import com.timetracker.business.ImageMethods;
import com.timetracker.data.Devices;
import com.timetracker.data.SelectedSurvey;
import com.timetracker.data.UbicheckRequest;
import com.timetracker.data.UbicheckDetailsRequest;
import com.timetracker.sqlite.MySQLiteHelper;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import com.luxand.FSDK;
import com.luxand.FSDK.*;

import static com.timetracker.business.ImageMethods.CreateBitmap;
import static com.timetracker.business.ImageMethods.DeleteImageFile;

public class UbicheckActivity extends Activity {
	
	//================================================================================
    // Global Variables
    //================================================================================
	private ProgressDialog progressDialog;
	private GPSTracker GPSTracker;
	private LinearLayout lyButtons;
	private LinearLayout lyCheckOut;
	private LinearLayout lyActivityCheckIn;
	private LinearLayout lyActivityCheckOut;
	private List<Integer> ElementsValue;
	private int OpenUbicheckDetailID;
	private MySQLiteHelper db = new MySQLiteHelper(UbicheckActivity.this);
	private int UbicheckOption = 0;
	private int CheckInBranchID = 0;
	private boolean UsesBiometric = false;
	private int BiometricID = 0;
	private boolean UsesKioskMode = false;
	private int KioskBranchID = 0;
	//Luxand
	protected String _CameraImagePath;
	protected String _DataBaseImagePath;
	private boolean processingImage;
	protected Bitmap CameraBitmap;
	//================================================================================
    // Activity Events
    //================================================================================
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_ubicheck);
		GPSTracker = new GPSTracker(getApplicationContext());
		//Luxand
		_CameraImagePath = Environment.getExternalStorageDirectory() + "/_CameraImage.jpg";
		_DataBaseImagePath = Environment.getExternalStorageDirectory() + "/_DataBaseImage.jpg";
		//
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
		//Luxand
		try {
			int res = FSDK.ActivateLibrary("m3EqUYK5p2JcviRPHbHO5enc+4pi7NvhnQ9HtchXYITWknOiTPn4yJtMIjOB+Gws92+VEzB3HKed5B2vVHVz3wD9gNVx08H9FnTZjJt+5+k+in7n+7iKsSKwfnUy7/DDRVk37ySCOzKInDtRvufu2gdQo7sPJ/AcSV9sJBhIgA4=");
			FSDK.Initialize();
			FSDK.SetFaceDetectionParameters(false, false, 100);
			FSDK.SetFaceDetectionThreshold(5);
			if (res != FSDK.FSDKE_OK) {
				Toast.makeText(getBaseContext(), "Error activando FaceSDK: " + res , Toast.LENGTH_LONG).show();
			}
		}catch (Exception e) {
			Toast.makeText(getBaseContext(), "exception " + e.getMessage(), Toast.LENGTH_LONG).show();
		}
		//
		if(Device == null || Device.Status != 2){
			DialogMethods.showInformationDialog(UbicheckActivity.this, "Dispositivo sin registrar", "Dispositivo sin registrar. Favor de registrarlo para continuar.",onClickListener);
			return;
		}
		if(Device.UsesBiometric == 1 && Device.BiometricID == 0){
			DialogMethods.showInformationDialog(UbicheckActivity.this, "Biometrico sin registrar", "Biometrico sin registrar. Favor de registrarlo para continuar.",onClickListener);
			return;
		}
		if(Device.UsesBiometric == 1 && Device.UsesKioskMode == 1 && Device.KioskBranchID == 0){
			DialogMethods.showInformationDialog(UbicheckActivity.this, "Biometrico sin registrar", "Kiosco sin sucursal asignada. Favor de agregar sucursal para continuar.",onClickListener);
			return;
		}
		if(Device.UsesBiometric == 1){
			UsesBiometric = true;
			BiometricID = Device.BiometricID;
			if(Device.UsesKioskMode == 1){
				UsesKioskMode = true;
				KioskBranchID = Device.KioskBranchID;
			}
		}
		boolean isFormUbicheckActive = false;
		SelectedSurvey SelectedSurvey = db.GetSelectedSurvey();
		if(SelectedSurvey != null){
			if(SelectedSurvey.UbicheckID != 0){
				isFormUbicheckActive = true;
			}
		}
		if(isFormUbicheckActive){
			DialogMethods.showInformationDialog(UbicheckActivity.this, "Ubicheck-Forma Activa", "Entrada registrada con forma. Favor de terminar forma para registrar salida.",onClickListener);
			return;
		}
		if (!ConnectionMethods.isInternetConnected(UbicheckActivity.this, false).equals("")){
			DialogMethods.showInformationDialog(UbicheckActivity.this, "Sin Conexion", "No se detecto una conexion a internet. Favor de conectarse a una red antes de volver a intentar.",onClickListener);
			return;
		}
		if (ContextCompat.checkSelfPermission(UbicheckActivity.this,Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
		    int isPermited = 0;
            ActivityCompat.requestPermissions(UbicheckActivity.this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},isPermited);
            return;
		}
		
		double latitude = 0;
	   	double longitude = 0;
    	if(GPSTracker.canGetLocation())
	   	{
		   latitude = GPSTracker.getLatitude();
		   longitude = GPSTracker.getLongitude();
	   	}
		if(latitude == 0 && longitude == 0){
			DialogMethods.showInformationDialog(UbicheckActivity.this, "GPS apagado", "GPS apagado. Favor de encenderlo y esperar unos momentos antes de volver a intentar.",onClickListener);
			return;
		}
		if(!processingImage){
			Refresh(null);
		}
		if(Device.UsesCreateBranch == 0){
			LinearLayout AddNewBranch = (LinearLayout)findViewById(R.id.AddNewBranch);
			AddNewBranch.setVisibility(View.GONE);
		}
	}
	
	public void onDestroy() {
	    super.onDestroy();
	    if(UsesKioskMode){
	    	Devices Device = db.GetDevice();
	    	Device.BiometricID = 0;
	    	Device.ImageWareRegister = 0;
	    	db.updateDevice(Device);
	    }
	    db.close();
	    GPSTracker.stopUsingGPS();
	}
	
	public void AddBranch(View view){
		Intent intent = new Intent(getBaseContext(),com.timetracker.surveys.UbicheckNewActivity.class);
    	startActivity(intent);
    	finish();
   }
	
	public void CheckOut(View view){
		if(UsesBiometric){
    		UbicheckOption = 2;
    		showSpinner("Registrando Salida");
			AuthenticateUser(BiometricID);
    	}else{
    		DoUbicheckCheckOut();
    	}
	}
	
	public void ActivityCheckIn(View view){
		if(UsesBiometric){
    		UbicheckOption = 3;
    		showSpinner("Registrando Actividad");
			AuthenticateUser(BiometricID);
    	}else{
    		DoActivityCheckIn();
    	}
	}
	
	public void ActivityCheckOut(View view){
		if(UsesBiometric){
    		UbicheckOption = 4;
    		showSpinner("Cerrando Actividad");
			AuthenticateUser(BiometricID);
    	}else{
    		DoActivityCheckOut();
    	}
	}
	
	//================================================================================
    // Main Processes
    //================================================================================

	public void AuthenticateUser(int BiometricID){
		VerifyPersonWithLuxand();
	}

	public void Refresh(View view){
		LinearLayout layoutRefresh = (LinearLayout)findViewById(R.id.layoutRefresh);
		layoutRefresh.setVisibility(View.GONE);
		showSpinner("Buscando Sucursales");
		Devices Device = db.GetDevice();
		UbicheckRequest UbicheckRequest = new UbicheckRequest(0,Device.DeviceID,GPSTracker.getLatitude(),GPSTracker.getLongitude(),new Date(),0,BiometricID,GPSTracker.usesMockLocation);
		AsyncUbicheck AsyncUbicheck = new AsyncUbicheck(UbicheckRequest,false);
		AsyncUbicheck.execute("/Ubicheck");
	}
	
	public void DoUbicheckCheckIn(){
		showSpinner("Registrando Entrada");
		Devices Device = db.GetDevice();
		UbicheckRequest UbicheckRequest = new UbicheckRequest(1,Device.DeviceID,GPSTracker.getLatitude(),GPSTracker.getLongitude(),new Date(), CheckInBranchID,BiometricID,GPSTracker.usesMockLocation);
		AsyncUbicheck AsyncUbicheck = new AsyncUbicheck(UbicheckRequest,true);
		AsyncUbicheck.execute("/Ubicheck");
	}
	
	public void DoUbicheckCheckOut(){
		showSpinner("Registrando Salida");
		Devices Device = db.GetDevice();
		UbicheckRequest UbicheckRequest = new UbicheckRequest(2,Device.DeviceID,GPSTracker.getLatitude(),GPSTracker.getLongitude(),new Date(),0,BiometricID,GPSTracker.usesMockLocation);
		AsyncUbicheck AsyncUbicheck = new AsyncUbicheck(UbicheckRequest,false);
		AsyncUbicheck.execute("/Ubicheck");
	}
	
	public void DoActivityCheckIn(){
		showSpinner("Registrando Actividad");
		Spinner ActivityCheckInSpinner = (Spinner) findViewById(R.id.ActivityCheckInSpinner);
		int ElementID = ElementsValue.get(ActivityCheckInSpinner.getSelectedItemPosition());
		Devices Device = db.GetDevice();
		UbicheckDetailsRequest UbicheckDetailsRequest = new UbicheckDetailsRequest(0,ElementID,Device.DeviceID,GPSTracker.getLatitude(),GPSTracker.getLongitude(),new Date(),BiometricID);
		AsyncUbicheckDetails AsyncUbicheckDetails = new AsyncUbicheckDetails(UbicheckDetailsRequest);
		AsyncUbicheckDetails.execute("/UbicheckDetails");
	}
	
	public void DoActivityCheckOut(){
		showSpinner("Cerrando Actividad");
		int UbicheckDetailID = OpenUbicheckDetailID;
		Devices Device = db.GetDevice();
		UbicheckDetailsRequest UbicheckDetailsRequest = new UbicheckDetailsRequest(UbicheckDetailID,0,Device.DeviceID,GPSTracker.getLatitude(),GPSTracker.getLongitude(),new Date(),BiometricID);
		AsyncUbicheckDetails AsyncUbicheckDetails = new AsyncUbicheckDetails(UbicheckDetailsRequest);
		AsyncUbicheckDetails.execute("/UbicheckDetails");
	}
	
	//================================================================================
    // Web Services
    //================================================================================
	
	@SuppressLint("SimpleDateFormat")
	private class AsyncUbicheck extends AsyncTask<String, Void, String> {
		UbicheckRequest UbicheckRequest;
		Boolean SendToForm;
        public AsyncUbicheck(UbicheckRequest UbicheckRequest,Boolean SendToForm) {
        	this.UbicheckRequest = UbicheckRequest;
        	this.SendToForm = SendToForm;
		}
		@Override
        protected String doInBackground(String... params) {
        	String resultado = "";
        	 try {
        		 JSONObject item = new JSONObject();
        		 SimpleDateFormat ft = new SimpleDateFormat ("yyyy-MM-dd HH:mm:ss");
        		 item.put("Status", UbicheckRequest.Status);
        		 item.put("DeviceID", UbicheckRequest.DeviceID);
        		 item.put("Lat", UbicheckRequest.Latitude);
        		 item.put("Lon", UbicheckRequest.Longitude);
        		 item.put("Date", ft.format(UbicheckRequest.Date));
        		 item.put("BranchID", UbicheckRequest.BranchID);
        		 item.put("BiometricID", UbicheckRequest.BiometricID);
				 item.put("IsMock", UbicheckRequest.IsMock);
        		 resultado = ConnectionMethods.Post(UbicheckActivity.this,item.toString(), params[0],false);
	         } catch (Exception e) {
	        	 resultado = "";             
	         }
             return resultado;
        }
		@SuppressWarnings("deprecation")
		@Override
        protected void onPostExecute(String result) {
			hideSpinner();
        	try {
        		if(!result.equals("\"\""))
            	{
        			final JSONObject JO = new JSONObject(result);
        			final int Status = JO.getInt("Status");
    				if(Status == 0 || Status == 3)
    	        	{
    					DialogInterface.OnClickListener onClickListener = new DialogInterface.OnClickListener(){
    						@Override
    				        public void onClick(DialogInterface dialog, int which) {
    							Devices Device = db.GetDevice();
    				        	if(SendToForm && Device.UsesFormWithUbicheck == 1){
    				        		Intent intent = new Intent(getBaseContext(),com.timetracker.surveys.SelectSurvey.class);
    				        		int UbicheckID = 0;
    				        		String DistributionChannel = "";
									try {
										UbicheckID = JO.getInt("UbicheckID");
										DistributionChannel = JO.getString("DistributionChannel");
									} catch (JSONException e) {
									}
    				        		intent.putExtra("UbicheckID",UbicheckID);
    				        		if(Device.UsesClientValidation == 1){
    				        			intent.putExtra("DistributionChannel",DistributionChannel);
    				        		}
    				        		dialog.dismiss();
        				        	startActivity(intent);
        				        	finish();
    				        	}else{
    				        		if(Status == 0){
    				        			LinearLayout layoutRefresh = (LinearLayout)findViewById(R.id.layoutRefresh);
    				        			layoutRefresh.setVisibility(View.VISIBLE);
    				        			dialog.dismiss();
    				        		}else{
    				        			dialog.dismiss();
    				        			if(UsesKioskMode){
    				        				Intent intent = new Intent(getBaseContext(),com.timetracker.surveys.SelectBiometricActivity.class);
                				        	startActivity(intent);
                				        	finish();
    				        			}else{
    				        				Intent intent = new Intent(getBaseContext(),com.timetracker.surveys.HomeActivity.class);
                				        	startActivity(intent);
                				        	finish();
    				        			}
    				        		}
    				        	}
    				        }
    				    };
    					if(JO.getString("Message").equals("Entrada Registrada") || JO.getString("Message").equals("Salida Registrada") ){
							SimpleDateFormat ft = new SimpleDateFormat ("yyyy-MM-dd HH:mm:ss");
							DialogMethods.showSuccessDialog(UbicheckActivity.this, "Mensaje Ubicheck", JO.getString("Message") + "\n\nFecha:" + ft.format(UbicheckRequest.Date) ,onClickListener);
						}else{
							DialogMethods.showInformationDialog(UbicheckActivity.this, "Mensaje Ubicheck", JO.getString("Message"),onClickListener);
						}
    				}
    	        	else if(Status == 1)
    	        	{
    		      	  	JSONArray jBranches = JO.getJSONArray("Branches");
    		      	  	if(UsesKioskMode){
    		      	  		boolean containsKioskBranch = false;
    		      	  		if(jBranches.length() > 0){
	    		      	  		for (int j = 0; j < jBranches.length(); j++) {
	    		      	  			JSONObject jBranch = jBranches.getJSONObject(j);
	    		      	  			if(jBranch.getInt("BranchID") == KioskBranchID){
	    		      	  				containsKioskBranch = true;
	    		      	  				break;
	    		      	  			}
	    		      	  		}
    		      	  		}
    		      	  		if(containsKioskBranch){
    		      	  			CheckInBranchID = KioskBranchID;
    		      	  			UbicheckOption = 1;
    		      	  			showSpinner("Registrando Entrada");
								AuthenticateUser(BiometricID);
    		      	  		}else{
    		      	  			hideSpinner();
	    		      	  		DialogInterface.OnClickListener onClickListener = new DialogInterface.OnClickListener(){
	    		      		        @Override
	    		      		        public void onClick(DialogInterface dialog, int which) {
	    		      	        		SendHome(null);
	    		      		        }
	    		      		    };
    		      	  			DialogMethods.showInformationDialog(UbicheckActivity.this, "Sucursal de kiosco NO se encuentra cerca.", JO.getString("Message"),onClickListener);
    		      	  		}
    		      	  	}else{
    		      	  		lyButtons = (LinearLayout) findViewById(R.id.Buttons);
    		      	  		for (int j = 0; j < jBranches.length(); j++) {
    		      	  			JSONObject jBranch = jBranches.getJSONObject(j);
    		      	  			Button newButton = new Button(UbicheckActivity.this);
    		      	  			newButton.setText(jBranch.getString("Branch"));
    		      	  			newButton.setId(jBranch.getInt("BranchID"));
    		      	  			newButton.setBackgroundDrawable(getResources().getDrawable(R.drawable.btn_checkin));
    		      	  			newButton.setTextAppearance(getApplicationContext(), R.style.button_text);
    		      	  			LinearLayout.LayoutParams tdf = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
    		      	  			tdf.setMargins(0, 10, 0, 10);
    		      	  			newButton.setLayoutParams(tdf);
    		      	  			newButton.setOnClickListener(new View.OnClickListener() {
    		      	  				@Override
    			                    public void onClick(View v) {
    			                    	CheckInBranchID = ((Button) v).getId();
    			                    	if(UsesBiometric){
    			                    		UbicheckOption = 1;
    			                    		showSpinner("Registrando Entrada");
											AuthenticateUser(BiometricID);
    			                    	}else{
    			                    		DoUbicheckCheckIn();
    			                    	}
    			                    }
    			                });
    		      	  			LinearLayout linLayout = new LinearLayout(UbicheckActivity.this);
    		      	  			linLayout.addView(newButton);
    		      	  			lyButtons.addView(linLayout);
    		      	  		}
    		      	  		lyButtons.setVisibility(View.VISIBLE);
    		      	  	}
    		      	  	
    	        	}
    	        	else if(Status == 2)
    	        	{
    	        		lyCheckOut = (LinearLayout) findViewById(R.id.CheckOut);
    	        		lyCheckOut.setVisibility(View.VISIBLE);
    	        		TextView CheckOutMessage = (TextView) findViewById(R.id.CheckOutMessage);
    	        		CheckOutMessage.setText(JO.getString("Message"));
    	        		Devices Device = db.GetDevice();
    	        		if(Device.UsesUbicheckDetails== 1){
    	        			if(JO.getInt("OpenUbicheckDetailID") == 0){
        	        			lyActivityCheckIn = (LinearLayout) findViewById(R.id.ActivityCheckIn);
        	        			lyActivityCheckIn.setVisibility(View.VISIBLE);
        	        			JSONArray jElements = JO.getJSONArray("Elements");
        	        			List<String> ElementsText = new ArrayList<String>();
        	        			ElementsValue = new ArrayList<Integer>(); 
        	        			if(jElements.length() > 0){
        	        				for (int j = 0; j < jElements.length(); j++) {
        	        					JSONObject jElement = jElements.getJSONObject(j);
        	        					ElementsText.add(jElement.getString("Name"));
        	        					ElementsValue.add(jElement.getInt("ElementID"));
        	        				}
        	        			}
        	        			ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(UbicheckActivity.this,android.R.layout.simple_spinner_item, ElementsText);
        	        			Spinner ActivityCheckInSpinner = (Spinner) findViewById(R.id.ActivityCheckInSpinner);
        	        			ActivityCheckInSpinner.setAdapter(dataAdapter);
        	        		}else{
        	        			lyCheckOut.setVisibility(View.GONE);
        	        			lyActivityCheckOut = (LinearLayout) findViewById(R.id.ActivityCheckOut);
        	        			lyActivityCheckOut.setVisibility(View.VISIBLE);
        	        			TextView ActivityCheckOutMessage = (TextView) findViewById(R.id.ActivityCheckOutMessage);
        	        			String Actividad = "Actividad actual: " + JO.getString("OpenUbicheckDetailName");
        	        			ActivityCheckOutMessage.setText(Actividad);
        	        			OpenUbicheckDetailID = JO.getInt("OpenUbicheckDetailID");
        	        		}
    	        		}
    	        	}
            	}
        	} catch (Exception ex) {
        		DialogMethods.showErrorDialog(UbicheckActivity.this, "Ocurrio un error al momento de utilizar Ubicheck. Result:" + result + " Info: " + ex.toString()," Result:" + result + " Activity:Ubicheck | Method:AsyncUbicheck | Error:" + ex.toString());
        	}
       }
    }
	
	@SuppressLint("SimpleDateFormat")
	private class AsyncUbicheckDetails extends AsyncTask<String, Void, String> {
		UbicheckDetailsRequest UbicheckDetailsRequest;
        public AsyncUbicheckDetails(UbicheckDetailsRequest UbicheckDetailsRequest) {
        	this.UbicheckDetailsRequest = UbicheckDetailsRequest;
		}
		@Override
        protected String doInBackground(String... params) {
        	String resultado = "";
        	 try {
        		 JSONObject item = new JSONObject();
        		 SimpleDateFormat ft = new SimpleDateFormat ("yyyy-MM-dd HH:mm:ss");
        		 item.put("UbicheckDetailID", UbicheckDetailsRequest.UbicheckDetailID);
        		 item.put("ElementID", UbicheckDetailsRequest.ElementID);
        		 item.put("DeviceID", UbicheckDetailsRequest.DeviceID);
        		 item.put("Lat", UbicheckDetailsRequest.Lat);
        		 item.put("Lon", UbicheckDetailsRequest.Lon);
        		 item.put("Date", ft.format(UbicheckDetailsRequest.Date));
        		 item.put("BiometricID", UbicheckDetailsRequest.BiometricID);
        		 resultado = ConnectionMethods.Post(UbicheckActivity.this,item.toString(), params[0],false);
	         } catch (Exception e) {
	        	 resultado = "";             
	         }
             return resultado;
        }
		@Override
        protected void onPostExecute(String result) {
			hideSpinner();
        	try {
        		if(result.equals("\"1\""))
            	{
					DialogInterface.OnClickListener onClickListener = new DialogInterface.OnClickListener(){
						@Override
				        public void onClick(DialogInterface dialog, int which) {
							if(UsesBiometric && UsesKioskMode){
								Intent intent = new Intent(getBaseContext(),com.timetracker.surveys.SelectBiometricActivity.class);
					        	startActivity(intent);
					        	finish();
							}else{
								Intent intent = new Intent(getBaseContext(),com.timetracker.surveys.HomeActivity.class);
					        	startActivity(intent);
					        	finish();
							}
				        }
				    };
					DialogMethods.showInformationDialog(UbicheckActivity.this, "Mensaje Ubicheck", "Se ha guardado la actividad",onClickListener);
            	}else{
            		DialogMethods.showErrorDialog(UbicheckActivity.this, "Ocurrio un error al momento de utilizar Actividades Ubicheck. Result:" + result," Result:" + result + " Activity:Ubicheck | Method:AsyncUbicheckDetails");
            	}
        	} catch (Exception ex) {
        		DialogMethods.showErrorDialog(UbicheckActivity.this, "Ocurrio un error al momento de utilizar Actividades Ubicheck. Result:" + result + " Info: " + ex.toString()," Result:" + result + " Activity:Ubicheck | Method:AsyncUbicheckDetails | Error:" + ex.toString());
        	}
       }
    }

	//================================================================================
	// Luxand Methods
	//================================================================================

	public void VerifyPersonWithLuxand()
	{
		processingImage = true;
		StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
		StrictMode.setVmPolicy(builder.build());
		File file = new File(_CameraImagePath);
		Uri outputFileUri = Uri.fromFile(file);
		//Select Photo
		Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		intent.putExtra("android.intent.extras.CAMERA_FACING", 1);
		intent.putExtra(MediaStore.EXTRA_OUTPUT,outputFileUri);
		startActivityForResult(intent, 0);

		//Select with File Explorer
		//Intent i = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
		//startActivityForResult(i, 0);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		try {
			super.onActivityResult(requestCode, resultCode, data);
			switch(resultCode)
			{
				case 0:
					hideSpinner();
					break;
				case -1:
					//Select with File Explorer
                    /*Uri selectedImage = data.getData();
                    String[] filePathColumn = { MediaStore.Images.Media.DATA };
                    Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);
                    cursor.moveToFirst();
                    int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                    String picturePath = cursor.getString(columnIndex);
                    onPhotoTaken(picturePath);*/
					onPhotoTaken(_CameraImagePath);
					break;
			}
		} catch (Exception e) {
			Toast toas1t = Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_SHORT);
			toas1t.show();
		}
	}

	protected void onPhotoTaken(String Path)
	{
		try
		{
			CameraBitmap = CreateBitmap(_CameraImagePath);
			new AsyncGetPicture().execute("/Biometrics?BiometricID=" + BiometricID + "&Provider=Luxand");
		}
		catch(Exception ex){
			Toast.makeText(getApplicationContext(), "E010:" + ex.toString(),Toast.LENGTH_SHORT).show();
		}
	}

	private class AsyncGetPicture extends AsyncTask<String, Void, String> {
		@Override
		protected String doInBackground(String... urls) {
			return ConnectionMethods.GET(UbicheckActivity.this,urls[0]);
		}
		@Override
		protected void onPostExecute(String result) {
			try
			{
				if(result.startsWith("\"http://timetrackerstorage.blob.core.windows.net/")){
					new AsyncComparePictures().execute(result.replace("\"", ""));
				}else{
					hideSpinner();
					Toast.makeText(getBaseContext(), "No se encontro imagen de Biometrico " + BiometricID + " Mensaje:" + result, Toast.LENGTH_LONG).show();
				}
			}
			catch (Exception ex) {
				hideSpinner();
				DialogMethods.showErrorDialog(UbicheckActivity.this, "Ocurrio un error al momento de traer imagen. Info: " + ex.toString(), "Activity:Ubicheck | Method:AsyncGetPicture | Error:" + ex.toString());
			}
		}
	}

	private class AsyncComparePictures extends AsyncTask<String, Void, String> {
		protected FSDK_Features features;
		protected TFacePosition faceCoords;
		protected String picturePath;
		protected HImage picture;
		protected int result;
		protected Bitmap DataBaseBitmap;
		@Override
		protected String doInBackground(String... args) {
			String log = new String();
			picturePath =_CameraImagePath;
			faceCoords = new TFacePosition();
			faceCoords.w = 0;
			picture = new HImage();
			result = FSDK.LoadImageFromFile(picture, _CameraImagePath);
			if (result == FSDK.FSDKE_OK) {
				result = FSDK.DetectFace(picture, faceCoords);
				features = new FSDK_Features();
				if (result == FSDK.FSDKE_OK) {
					//DEBUG
					//FSDK.SetFaceDetectionThreshold(1);
					//FSDK.SetFaceDetectionParameters(false, false, 70);
					//long t0 = System.currentTimeMillis();
					//for (int i=0; i<10; ++i)
					//result = FSDK.DetectFacialFeatures(picture, features);

					result = FSDK.DetectFacialFeaturesInRegion(picture, faceCoords, features);
					//Log.d("TT", "TIME: " + ((System.currentTimeMillis()-t0)/10.0f));
				}
			}
			//return log;
			DataBaseBitmap = null;
			String resultString = "exito";
			try {
				DataBaseBitmap = BitmapFactory.decodeStream((InputStream)new URL(args[0]).getContent());
			} catch (Exception e) {
				resultString = "error";
			}
			return resultString;
		}
		@Override
		protected void onPostExecute(String resultstring) {
			processingImage = false;
			if(resultstring == "exito" && DataBaseBitmap != null){
				if (result != FSDK.FSDKE_OK){
					DeleteImageFile(_CameraImagePath);
					hideSpinner();
					Toast.makeText(UbicheckActivity.this, "No se encontro rostro en foto tomada" , Toast.LENGTH_LONG).show();
				}else{
					HImage CameraImage = new HImage();
					FSDK.LoadImageFromFile(CameraImage, _CameraImagePath);
					FSDK_FaceTemplate template1 = new FSDK_FaceTemplate();
					result = FSDK.GetFaceTemplate(CameraImage, template1);
					if(template1 != null){
						ImageMethods.CreateImageFile(DataBaseBitmap,_DataBaseImagePath);
						HImage DataBaseImage = new HImage();
						FSDK.LoadImageFromFile(DataBaseImage, _DataBaseImagePath);
						FSDK_FaceTemplate template2 = new FSDK_FaceTemplate();
						FSDK.GetFaceTemplate(DataBaseImage, template2);
						float Similarity[] = new float[1];
						float MatchingThreshold[] = new float[1];
						FSDK.GetMatchingThresholdAtFAR((float)0.25,MatchingThreshold);
						int Success = FSDK.MatchFaces(template1, template2, Similarity);
						hideSpinner();
						float simil = Similarity[0];
						if(Similarity[0] > MatchingThreshold[0]){
							switch(UbicheckOption){
								case 1:
									DoUbicheckCheckIn();
									break;
								case 2:
									DoUbicheckCheckOut();
									break;
								case 3:
									DoActivityCheckIn();
									break;
								case 4:
									DoActivityCheckOut();
									break;
							}
						}else{
							hideSpinner();
							lyCheckOut = (LinearLayout) findViewById(R.id.CheckOut);
							if(lyCheckOut.getVisibility() == View.GONE){
								LinearLayout layoutRefresh = (LinearLayout)findViewById(R.id.layoutRefresh);
								layoutRefresh.setVisibility(View.VISIBLE);
							}
							Intent intent = new Intent(UbicheckActivity.this, SimilarityPreview.class);
							Bundle b = new Bundle();
							b.putString("Similitud", (Math.round(simil * 100) + "%")); //Your id
							intent.putExtras(b); //Put your id to your next Intent
							startActivity(intent);
							//Toast.makeText(getBaseContext(), "Biometrico no coincide. Similitud: " + Math.round(simil * 100) + "%", Toast.LENGTH_LONG).show();
						}
					}else{
						hideSpinner();
						Toast.makeText(UbicheckActivity.this, "Foto tomada no valida" , Toast.LENGTH_LONG).show();
					}
				}
			}else{
				hideSpinner();
				Toast.makeText(UbicheckActivity.this, "Foto obtenida no valida" , Toast.LENGTH_LONG).show();
			}
		}
	}

	//================================================================================
    // Menu Options
    //================================================================================
	
	private void showToast(final String toast) {
		runOnUiThread(new Runnable() {
			public void run() {
				Toast.makeText(UbicheckActivity.this, toast, Toast.LENGTH_LONG).show();
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
			Intent intent = new Intent(getBaseContext(),com.timetracker.surveys.SelectBiometricActivity.class);
        	startActivity(intent);
        	finish();
		}else{
			Intent intent = new Intent(getBaseContext(),com.timetracker.surveys.HomeActivity.class);
        	startActivity(intent);
        	finish();
		}
	}
}
