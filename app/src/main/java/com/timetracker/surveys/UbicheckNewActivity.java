package com.timetracker.surveys;

import org.json.JSONArray;
import org.json.JSONObject;

import com.timetracker.business.ConnectionMethods;
import com.timetracker.business.DialogMethods;
import com.timetracker.business.GPSTracker;
import com.timetracker.data.Devices;
import com.timetracker.sqlite.MySQLiteHelper;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

@SuppressLint("ClickableViewAccessibility")
public class UbicheckNewActivity extends Activity {

	//================================================================================
    // Global Variables
    //================================================================================
	
	private ProgressDialog progress;
	private GPSTracker GPSTracker;
	private MySQLiteHelper db = new MySQLiteHelper(UbicheckNewActivity.this);
	private double latitude = 0;
	private double longitude = 0;
	private int DeviceID = 0;
	private int GlobalClientID = 0;
	private TableLayout tblClientOptions;
	private EditText txtClient;
	
	//================================================================================
    // Activity Events
    //================================================================================
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_ubicheck_new);
		GPSTracker = new GPSTracker(getApplicationContext());
		progress = new ProgressDialog(UbicheckNewActivity.this);
		progress.setCancelable(false);
		tblClientOptions = (TableLayout) findViewById(R.id.tblClientOptions);
		txtClient = (EditText) findViewById(R.id.txtClient);
		EditText txtLatitude = (EditText) findViewById(R.id.txtLatitude);
		EditText txtLongitude = (EditText) findViewById(R.id.txtLongitude);
		txtLatitude.setEnabled(false);
		txtLongitude.setEnabled(false);
		DialogInterface.OnClickListener onClickListener = new DialogInterface.OnClickListener(){
	        @Override
	        public void onClick(DialogInterface dialog, int which) {
        		dialog.dismiss();
	        	Intent intent = new Intent(getBaseContext(),com.timetracker.surveys.HomeActivity.class);
	        	startActivity(intent);
	        	finish();
	        }
	    };
	    if (ConnectionMethods.isInternetConnected(UbicheckNewActivity.this, false).equals("")){
			if (ContextCompat.checkSelfPermission(UbicheckNewActivity.this,Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
			    int isPermited = 0;
	            ActivityCompat.requestPermissions(UbicheckNewActivity.this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},isPermited);
		    }else{
		    	Devices Device = db.GetDevice();
				if(Device == null){
					Intent intent = new Intent(getBaseContext(),com.timetracker.surveys.HomeActivity.class);
	        		startActivity(intent);
					finish();
				}
				DeviceID = Device.DeviceID;
				
		    	if(GPSTracker.canGetLocation())
 			   	{
 				   latitude = GPSTracker.getLatitude();
 				   longitude = GPSTracker.getLongitude();
 			   	}
		    	if(latitude == 0 || longitude == 0){
	          		DialogMethods.showInformationDialog(UbicheckNewActivity.this, "GPS apagado", "GPS apagado. Favor de encenderlo y esperar unos momentos antes de volver a intentar.",onClickListener);
	          	}else{
	          		txtLatitude.setText(Double.toString(latitude));
	          		txtLongitude.setText(Double.toString(longitude));
	          	}
		    }
		}else{
			DialogMethods.showInformationDialog(UbicheckNewActivity.this, "Sin Conexion", "No se detecto una conexion a internet. Favor de conectarse a una red antes de volver a intentar.",onClickListener);
		}
	}
	
	public void onDestroy() {
	    super.onDestroy();
	    db.close();
	    GPSTracker.stopUsingGPS();
	}
	
	//================================================================================
    // Search Client
    //================================================================================
	
	public void SearchClient(View view){
		progress.setMessage("Buscando clientes, por favor espere...");
		progress.show();
		tblClientOptions.removeAllViews();
		EditText txtClient = (EditText) findViewById(R.id.txtClient);
		new AsyncSearchClient().execute("/Branches?Name=" + txtClient.getText().toString() + "&DeviceID=" + DeviceID);
	}
	
	private class AsyncSearchClient extends AsyncTask<String, Void, String> {
	   @Override
	   	protected String doInBackground(String... urls) {
		   return ConnectionMethods.GET(UbicheckNewActivity.this, urls[0]);
	   }
	   @SuppressWarnings("deprecation")
	   @Override
	   	protected void onPostExecute(String result) {
		   if(result.startsWith("Error:")){
			   DialogMethods.showInformationDialog(UbicheckNewActivity.this, "Ocurrio un Error", result,null);
		   }else{
			   try 
	            {
				   JSONArray JR = new JSONArray(result);
				   TableRow.LayoutParams rowParams = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT);
				   if(JR.length() > 0){
					   TableRow trRow = new TableRow(UbicheckNewActivity.this);
					   trRow.setLayoutParams(rowParams);
					   trRow.setBackgroundColor(Color.parseColor("#126BAD"));
					   
					   TextView TextViewHeader1 = new TextView(UbicheckNewActivity.this);
					   TextViewHeader1.setText("Cliente");
					   TextViewHeader1.setTextColor(Color.WHITE);
					   TextViewHeader1.setLayoutParams(rowParams);
					   TextViewHeader1.setPadding(3, 3, 5, 3);
					   TextViewHeader1.setTextAppearance(getBaseContext(), R.style.SQLGridHeader);
   					   trRow.addView(TextViewHeader1);
   					   
   					   TextView TextViewHeader2 = new TextView(UbicheckNewActivity.this);
   					   TextViewHeader2.setText("");
   					   TextViewHeader2.setTextColor(Color.WHITE);
   					   TextViewHeader2.setLayoutParams(rowParams);
   					   TextViewHeader2.setPadding(3, 3, 5, 3);
   					   TextViewHeader2.setTextAppearance(getBaseContext(), R.style.SQLGridHeader);
					   trRow.addView(TextViewHeader2);
				       tblClientOptions.addView(trRow,0);
				   }
				   for(int i=0;i < JR.length();i++)
				   {
					   JSONObject JO = (JSONObject)JR.getJSONObject(i);
					   final int ClientID = JO.getInt("ClientID");
					   final String Name = JO.getString("Name");
					   
					   TableRow trRow = new TableRow(UbicheckNewActivity.this);
					   trRow.setLayoutParams(rowParams);
					   trRow.setBackgroundColor(Color.WHITE);
					   
					   TextView TextViewHeader1 = new TextView(UbicheckNewActivity.this);
					   TextViewHeader1.setText(Name);
					   TextViewHeader1.setTextColor(Color.BLACK);
					   TextViewHeader1.setLayoutParams(rowParams);
					   TextViewHeader1.setPadding(3, 3, 5, 3);
					   TextViewHeader1.setTextAppearance(getBaseContext(), R.style.SQLGridText);
   					   trRow.addView(TextViewHeader1);
   					   
   					   TextView TextViewHeader2 = new TextView(UbicheckNewActivity.this);
   					   TextViewHeader2.setText("Seleccionar");
   					   
   					   TextViewHeader2.setTypeface(null, Typeface.BOLD);
   					   TextViewHeader2.setLayoutParams(rowParams);
   					   TextViewHeader2.setPadding(3, 3, 5, 3);
   					   TextViewHeader2.setTextAppearance(getBaseContext(), R.style.SQLGridText);
   					   TextViewHeader2.setTextColor(Color.BLUE);
   					   TextViewHeader2.setOnTouchListener(new OnTouchListener() {
   						   public boolean onTouch(View v, MotionEvent event) {
   							   txtClient.setText(Name);
   							   GlobalClientID = ClientID;
   							   tblClientOptions.removeAllViews();
   							   return true;
   						   }
   					   });
					   trRow.addView(TextViewHeader2);
				       tblClientOptions.addView(trRow,i + 1);
				   }
				} 
	            catch (Exception ex) {
	            	DialogMethods.showErrorDialog(UbicheckNewActivity.this, "Ocurrio un error al momento de utilizar Ubicheck. Info: " + ex.toString(),"Activity:UbicheckNew | Method:AsyncSearchClient | Error:" + ex.toString());
				}
		   	}
		   progress.dismiss();
   		}
    }
	
	//================================================================================
    // Send Branch
    //================================================================================
	
	public void SendBranch(View view){
		progress.setMessage("Enviando sucursal, por favor espere...");
		progress.show();
		String ClientName = ((EditText) findViewById(R.id.txtClient)).getText().toString();
		String Name = ((EditText) findViewById(R.id.txtBranch)).getText().toString();
		String ChainNumber = ((EditText) findViewById(R.id.txtChainNumber)).getText().toString();
		String DistributionChannel = ((EditText) findViewById(R.id.txtDistributionChannel)).getText().toString();
		String Address1 = ((EditText) findViewById(R.id.txtAddress1)).getText().toString();
		String Address2 = ((EditText) findViewById(R.id.txtAddress2)).getText().toString();
		String ExteriorNumber = ((EditText) findViewById(R.id.txtExteriorNumber)).getText().toString();
		String City = ((EditText) findViewById(R.id.txtCity)).getText().toString();
		String State = ((EditText) findViewById(R.id.txtState)).getText().toString();
		String ZIP = ((EditText) findViewById(R.id.txtZIP)).getText().toString();
		String Country = ((EditText) findViewById(R.id.txtCountry)).getText().toString();
		String Schedule = ((EditText) findViewById(R.id.txtSchedule)).getText().toString();
		String Phone = ((EditText) findViewById(R.id.txtPhone)).getText().toString();
		String Website = ((EditText) findViewById(R.id.txtWebsite)).getText().toString();
		double Latitude = Double.parseDouble(((EditText) findViewById(R.id.txtLatitude)).getText().toString());
		double Longitude = Double.parseDouble(((EditText) findViewById(R.id.txtLongitude)).getText().toString());
		WSBranch WSBranch = new WSBranch(GlobalClientID,ClientName,DeviceID,Name,ChainNumber,DistributionChannel,Address1,Address2,ExteriorNumber,City,State,ZIP,Country,Schedule,Phone,Website,Latitude,Longitude);
		AsynBranchCreate AsynBranchCreate = new AsynBranchCreate(WSBranch);
		AsynBranchCreate.execute("/Branches");
	}
	
	private class AsynBranchCreate extends AsyncTask<String, Void, String> {
		WSBranch WSBranch;
        public AsynBranchCreate(WSBranch WSBranch) {
        	this.WSBranch=WSBranch;
		}
		@Override
        protected String doInBackground(String... params) {
        	String resultado = "0";
        	 try {
        		 JSONObject item = new JSONObject();
        		 item.put("ClientID", WSBranch.ClientID);
        		 item.put("ClientName", WSBranch.ClientName);
        		 item.put("DeviceID", WSBranch.DeviceID);
        		 item.put("Name", WSBranch.Name);
        		 item.put("ChainNumber", WSBranch.ChainNumber);
        		 item.put("DistributionChannel", WSBranch.DistributionChannel);
        		 item.put("Address1", WSBranch.Address1);
        		 item.put("Address2", WSBranch.Address2);
        		 item.put("ExteriorNumber", WSBranch.ExteriorNumber);
        		 item.put("City", WSBranch.City);
        		 item.put("State", WSBranch.State);
        		 item.put("ZIP", WSBranch.ZIP);
        		 item.put("Country", WSBranch.Country);
        		 item.put("Schedule", WSBranch.Schedule);
        		 item.put("Phone", WSBranch.Phone);
        		 item.put("Website", WSBranch.Website);
        		 item.put("Latitude", WSBranch.Latitude);
        		 item.put("Longitude", WSBranch.Longitude);
        		 resultado = ConnectionMethods.Post(UbicheckNewActivity.this,item.toString(), params[0],false);
	         } catch (Exception e) {
	        	 return "Error: " + e.toString();               
	         }
             return resultado;
        }
        @Override
        protected void onPostExecute(String result) {
        	progress.dismiss();
        	if(result.equals("\"1\""))
        	{
        		DialogInterface.OnClickListener onClickListener = new DialogInterface.OnClickListener(){
        	        @Override
        	        public void onClick(DialogInterface dialog, int which) {
                		dialog.dismiss();
        	        	Intent intent = new Intent(getBaseContext(),com.timetracker.surveys.UbicheckNewActivity.class);
        	        	startActivity(intent);
        	        	finish();
        	        }
        	    };
        		DialogMethods.showInformationDialog(UbicheckNewActivity.this, "Sucursal enviada", "Sucursal enviada exitosamente.", onClickListener);
        	}
        	else
        	{
        		DialogMethods.showErrorDialog(UbicheckNewActivity.this, "Ocurrio un error al momento de enviar ubicacion. Info: " + result, "Activity:Home | Method:AsynTrackerCreate | Result:" + result);
        	}
       }
    }
	
	public class WSBranch {
		public int ClientID;
		public String ClientName;
		public int DeviceID;
		public String Name;
		public String ChainNumber;
		public String DistributionChannel;
		public String Address1;
		public String Address2;
		public String ExteriorNumber;
		public String City;
		public String State;
		public String ZIP;
		public String Country;
		public String Schedule;
		public String Phone;
		public String Website;
		public double Latitude;
		public double Longitude;
		public WSBranch(int ClientID, String ClientName, int DeviceID, String Name,String ChainNumber, String DistributionChannel, String Address1, String Address2, String ExteriorNumber, String City,String State,String ZIP,String Country,String Schedule,String Phone,String Website,double Latitude,double Longitude){
		    this.ClientID = ClientID;
		    this.ClientName = ClientName;
		    this.DeviceID = DeviceID;
			this.Name = Name;
			this.ChainNumber = ChainNumber;
			this.DistributionChannel = DistributionChannel;
			this.Address1 = Address1;
			this.Address2 = Address2;
			this.ExteriorNumber = ExteriorNumber;
			this.City = City;
			this.State = State;
			this.ZIP = ZIP;
			this.Country = Country;
			this.Schedule = Schedule;
			this.Phone = Phone;
			this.Website = Website;
			this.Latitude = Latitude;
			this.Longitude = Longitude;
		}
	}
	
	//================================================================================
    // Return Button
    //================================================================================
	
	public void SendHome(View view){
		Intent intent = new Intent(getBaseContext(),com.timetracker.surveys.UbicheckActivity.class);
		startActivity(intent);
		finish();
	}
}
