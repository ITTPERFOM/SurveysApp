package com.timetracker.surveys;

import org.json.JSONObject;

import com.timetracker.surveys.R;
import com.timetracker.business.ConnectionMethods;
import com.timetracker.data.Devices;
import com.timetracker.sqlite.MySQLiteHelper;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

public class RegisterActivity extends Activity {
	
	private MySQLiteHelper db = new MySQLiteHelper(RegisterActivity.this);
	private ProgressDialog progressDialog;
	
	//================================================================================
    // Global Variables
    //================================================================================
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_register);
		Devices Device = db.GetDevice();
		if(Device != null){
			if(Device.UsesBiometric == 1 && Device.ImageWareRegister == 0 && Device.UsesKioskMode == 0){
				if((Device.BiometricID == 0)){
					TableRow trButtonSelectBiometric = (TableRow) findViewById(R.id.trButtonSelectBiometric);
					trButtonSelectBiometric.setVisibility(View.VISIBLE);
				}
			}
			TableRow trDeviceName = (TableRow) findViewById(R.id.trDeviceName);
			TableRow trDeviceCode = (TableRow) findViewById(R.id.trDeviceCode);
			TableRow trDeviceTerms = (TableRow) findViewById(R.id.trDeviceTerms);
			TableRow trButton = (TableRow) findViewById(R.id.trButton);
			TextView lblDeviceNameInfo = (TextView) findViewById(R.id.lblDeviceNameInfo);
			trDeviceName.setVisibility(View.GONE);
			trDeviceCode.setVisibility(View.GONE);
			trDeviceTerms.setVisibility(View.GONE);
			trButton.setVisibility(View.GONE);
			String Estatus = "En Espera";
			if(Device.Status == 2){
				Estatus = "Aprobado";
			}else if(Device.Status == 3){
				Estatus = "Rechazado";
			}
			String EstatusBiometric = "";
			if(Device.UsesBiometric == 1){
				EstatusBiometric = "\n\n Biometrico: ";
				if(Device.UsesKioskMode == 1){
					EstatusBiometric += "Modo Kiosco";
				}else{
					if(Device.Account.equals("Grupo Andrea")){
						EstatusBiometric += "Luxand";
					}else{
						if(Device.ImageWareRegister == 1){
							EstatusBiometric += "Registrado";
						}else{
							EstatusBiometric += "Sin Registrar";
						}
					}
				}
			}
			lblDeviceNameInfo.setText(" Dispositivo: " + Device.Name + "\n\n Codigo: " + Device.Code + "\n\n Cuenta: " + Device.Account + "\n\n Estatus: " + Estatus + EstatusBiometric +"\n\n Nivel: " + Device.Level);
			TableRow trButtonReset = (TableRow) findViewById(R.id.trButtonReset);
			trButtonReset.setVisibility(View.VISIBLE);
		}else{
			CheckBox checkTerms = (CheckBox) findViewById(R.id.checkTerms);
			checkTerms.setMovementMethod(LinkMovementMethod.getInstance());
			TableRow trButtonReset = (TableRow) findViewById(R.id.trButtonReset);
			trButtonReset.setVisibility(View.GONE);
		}
	}
	
	public void onDestroy() {
	    super.onDestroy();
	    db.close();
	}
	
	//================================================================================
    // Menu Options
    //================================================================================

	public void SendHome(View view){
		Intent intent = new Intent(getBaseContext(),com.timetracker.surveys.HomeActivity.class);
		startActivity(intent);
		finish();
	}
	
	// ================================================================================
	// Register New Person
	// ================================================================================

	public void selectPerson(View view) {
		Intent intent = new Intent(getBaseContext(),com.timetracker.surveys.SelectBiometricActivity.class);
		startActivity(intent);
		finish();
	}
	
	//================================================================================
    // Register Device
    //================================================================================
	
	public void RegisterDevice(View view){
	   try
		{
		   CheckBox checkTerms = (CheckBox) findViewById(R.id.checkTerms);
		   if(checkTerms.isChecked()){
			   if (ConnectionMethods.isInternetConnected(RegisterActivity.this, false).equals("")){
		    	   TextView txtCodigo = (TextView) findViewById(R.id.txtDeviceCode);
		    	   TextView txtNombre = (TextView) findViewById(R.id.txtDeviceName);
		    	   String sCodigo = txtCodigo.getText().toString();
		    	   String sNombre = txtNombre.getText().toString();
		    	   if(!sCodigo.equals("")&& !sNombre.equals("")){
		    		   if(sCodigo.matches("[a-zA-Z0-9.? ]*") && sNombre.matches("[a-zA-Z0-9.? ]*")){
		    			   int Type = 3;
			    		   if(isTablet(getApplicationContext())){
			    			   Type = 4;
			    		   }
			    		   PackageInfo pInfo = null;
			    		   try {
			    			   pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
			    		   } catch (NameNotFoundException e) {
			    		   }
			    		   String OSVersion = Build.VERSION.RELEASE;
			    		   String Model = getDeviceName();
			    		   Devices DeviceVar = new Devices(sNombre,sCodigo,String.valueOf(Type),0,1,1,0,0,0,0,0,0,0,0,0,"",0);
			    		   Button btnCheckIn = (Button) findViewById(R.id.btnCheckIn);
			    		   btnCheckIn.setEnabled(false);
			    		   HttpAsyncTask httpAsyncTask = new HttpAsyncTask(DeviceVar,pInfo.versionName,Model,OSVersion);
			    		   httpAsyncTask.execute("/Devices/");
		    		   }else{
		    			   Toast.makeText(getBaseContext(), "No son permitidos Caracteres Especiales", Toast.LENGTH_LONG).show();
		    		   }
		    	   }else{
		    		   Toast.makeText(getBaseContext(), "Ingrese Nombre y Codigo", Toast.LENGTH_LONG).show();
		    	   }
			   }
		   }else{
			   Toast.makeText(getBaseContext(), "Para continuar debe Aceptar los Terminos y Condiciones", Toast.LENGTH_LONG).show();
		   }
		} catch (Exception e) {
			Toast.makeText(getBaseContext(), e.toString(), Toast.LENGTH_LONG).show();
		}
	}
	
	//================================================================================
    // Reset Device
    //================================================================================
	
	public void ResetDevice(View view){
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Advertencia")
        .setMessage("Le gustaria eliminar los datos del dispositivo?")
        .setCancelable(false)
        .setPositiveButton("Si",new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
                db.deleteQuestionOptions();
            	db.deleteQuestionSentences();
            	db.deleteQuestions();
            	db.deleteAllSurveys();
            	db.deleteDevice();
            	db.deleteSelectedSurveys();
            	db.deleteBiometrics();
            	Intent intent = new Intent(getBaseContext(),com.timetracker.surveys.HomeActivity.class);
        		startActivity(intent);
        		finish();
            }
        })
        .setNegativeButton("No",new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
           	 dialog.cancel();
            }
        });
        AlertDialog alert = builder.create();
        alert.show();
	}
	
	//================================================================================
    // Check Device type
    //================================================================================
	
	public boolean isTablet(Context context) {
	    boolean xlarge = ((context.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) == Configuration.SCREENLAYOUT_SIZE_XLARGE);
	    boolean large = ((context.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) == Configuration.SCREENLAYOUT_SIZE_LARGE);
	    return (xlarge || large);
	}
	
	//================================================================================
    // Web Task
    //================================================================================
	
	private class HttpAsyncTask extends AsyncTask<String, Void, String> {
    	Devices DeviceVar;
    	String AppVersion;
    	String Model;
    	String OSVersion;
        public HttpAsyncTask(Devices DeviceVar,String AppVersion,String Model,String OSVersion) {
        	this.DeviceVar=DeviceVar;
        	this.AppVersion = AppVersion;
        	this.Model = Model;
        	this.OSVersion = OSVersion;
		}
		@Override
        protected String doInBackground(String... params) {
        	String resultado = "0";
        	 try {
				JSONObject item = new JSONObject();
				item.put("Name", DeviceVar.Name);
				item.put("Code", DeviceVar.Code);
				item.put("DeviceTypeID", DeviceVar.DeviceTypeID);
				item.put("AppVersion", AppVersion);
				item.put("Model", Model);
				item.put("OSVersion", OSVersion);
	            resultado = ConnectionMethods.Post(RegisterActivity.this,item.toString(), params[0],false);
	         } catch (Exception e) {
	        	 resultado = "0";              
	         }
             return resultado;
        }
        @Override
        protected void onPostExecute(String result) {
        	if(!result.equals("\"0\""))
        	{
        		try{
            		db.deleteDevice();
            		DeviceVar.DeviceID = Integer.parseInt(result.replace("\"", ""));
            		db.addDevice(DeviceVar);
            		Toast.makeText(getBaseContext(), "Dispositivo Registrado", Toast.LENGTH_LONG).show();
            		Intent intent = new Intent(getBaseContext(),com.timetracker.surveys.HomeActivity.class);
            		startActivity(intent);
            		finish();
        		}catch (Exception e) {
        			Toast.makeText(RegisterActivity.this, "Error:" + e.toString(), Toast.LENGTH_LONG).show();
        		}
        	}
        	else
        	{
        		Toast.makeText(getBaseContext(), "El codigo que ingreso es incorrecto", Toast.LENGTH_LONG).show();
        	}
        	Button btnCheckIn = (Button) findViewById(R.id.btnCheckIn);
        	btnCheckIn.setEnabled(true);
       }
    }
	
	private class AsyncUpdateBiometric extends AsyncTask<String, Void, String> {
   		@Override
	   	protected String doInBackground(String... urls) {
		   return ConnectionMethods.GET(RegisterActivity.this, urls[0]);
   		}
   		@Override
   		protected void onPostExecute(String result) {
   			hideSpinner();
   			try 
   			{
   				Intent intent = new Intent(getBaseContext(),com.timetracker.surveys.RegisterActivity.class);
   				startActivity(intent);
   				finish();
			} 
   			catch (Exception e) {
   				showToast(e.toString());
   			}
   		}
    }
	
	private void showToast(final String toast) {
		runOnUiThread(new Runnable() {
			public void run() {
				Toast.makeText(RegisterActivity.this, toast, Toast.LENGTH_LONG).show();
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
    // Get Device Information
    //================================================================================
	
	public static String getDeviceName() {
	    String manufacturer = Build.MANUFACTURER;
	    String model = Build.MODEL;
	    if (model.startsWith(manufacturer)) {
	        return capitalize(model);
	    }
	    return capitalize(manufacturer) + " " + model;
	}

	private static String capitalize(String str) {
	    if (TextUtils.isEmpty(str)) {
	        return str;
	    }
	    char[] arr = str.toCharArray();
	    boolean capitalizeNext = true;
	    String phrase = "";
	    for (char c : arr) {
	        if (capitalizeNext && Character.isLetter(c)) {
	            phrase += Character.toUpperCase(c);
	            capitalizeNext = false;
	            continue;
	        } else if (Character.isWhitespace(c)) {
	            capitalizeNext = true;
	        }
	        phrase += c;
	    }
	    return phrase;
	}
	
}
